package net.veilmc.hcf.user;

import net.veilmc.hcf.HCF;
import net.veilmc.util.Config;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class UserManager
		implements Listener{
	private final HCF plugin;
	private final Map<UUID, FactionUser> users = new HashMap<>();
	private Config userConfig;

	public UserManager(HCF plugin){
		this.plugin = plugin;
		this.reloadUserData();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		this.users.putIfAbsent(uuid, new FactionUser(uuid));
	}

	public Map<UUID, FactionUser> getUsers(){
		return this.users;
	}


	public FactionUser getUserAsync(UUID uuid){
		synchronized(this.users){
			FactionUser revert = new FactionUser(uuid);
			FactionUser user = this.users.putIfAbsent(uuid, revert);
			return ObjectUtils.firstNonNull(user, revert);
		}
	}

	public FactionUser getUser(UUID uuid){
		FactionUser revert = new FactionUser(uuid);
		FactionUser user = this.users.putIfAbsent(uuid, revert);
		return ObjectUtils.firstNonNull(user, revert);
	}

	public void reloadUserData(){
		this.userConfig = new Config(this.plugin, "faction-users");
		final Object object = this.userConfig.get("users");
		if(object instanceof MemorySection){
			final MemorySection section = (MemorySection) object;
			final Collection<String> keys = section.getKeys(false);
			for(final String id : keys){
				this.users.put(UUID.fromString(id), (FactionUser) this.userConfig.get(section.getCurrentPath() + '.' + id));
			}
		}
	}

	public void saveUserData(){
		Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
		LinkedHashMap<String, FactionUser> saveMap = new LinkedHashMap<String, FactionUser>(entrySet.size());
		for(Map.Entry<UUID, FactionUser> entry : entrySet){
			saveMap.put(entry.getKey().toString(), entry.getValue());
		}
		this.userConfig.set("users", saveMap);
		this.userConfig.save();
	}
}

