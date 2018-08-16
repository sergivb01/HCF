package idaniel84.user;

import com.google.common.base.Preconditions;
import idaniel84.HCF;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public abstract class AbstractUserManager
		implements Listener{
	private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
	protected final HCF plugin;
	protected final ConcurrentMap<UUID, FactionUser> inMemoryStorage;
	protected final ConcurrentMap<UUID, FactionUser> onlineStorage;
	protected final Map<String, UUID> uuidCache = Collections.synchronizedMap(new TreeMap(String.CASE_INSENSITIVE_ORDER));

	public AbstractUserManager(HCF plugin){
		this.inMemoryStorage = new ConcurrentHashMap();
		this.onlineStorage = new ConcurrentHashMap();
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		reloadUserData();
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		FactionUser factionUser = this.inMemoryStorage.get(uuid);
		if(factionUser == null){
			factionUser = new FactionUser(uuid);
			this.inMemoryStorage.put(uuid, factionUser);
			saveUser(factionUser);
		}
		this.onlineStorage.put(uuid, factionUser);
		this.uuidCache.put(player.getName(), uuid);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event){
		UUID uuid = event.getPlayer().getUniqueId();
		this.onlineStorage.remove(uuid);
	}

	public ConcurrentMap<UUID, FactionUser> getUsers(){
		return this.inMemoryStorage;
	}

	public FactionUser getUser(UUID uuid){
		Preconditions.checkNotNull(uuid);
		FactionUser factionUser;
		return (factionUser = this.inMemoryStorage.get(uuid)) != null ? factionUser : (factionUser = this.onlineStorage.get(uuid)) != null ? factionUser : insertAndReturn(uuid, new FactionUser(uuid));
	}

	public FactionUser getIfContainedOffline(UUID uuid){
		Preconditions.checkNotNull(uuid);
		FactionUser factionUser;
		return (factionUser = this.onlineStorage.get(uuid)) != null ? factionUser : this.inMemoryStorage.get(uuid);
	}

	public FactionUser insertAndReturn(UUID uuid, FactionUser factionUser){
		this.inMemoryStorage.put(uuid, factionUser);
		return factionUser;
	}

	public FactionUser getIfContains(UUID uuid){
		return this.onlineStorage.get(uuid);
	}

	public UUID fetchUUID(String username){
		Player player = Bukkit.getPlayer(username);
		if(player != null){
			return player.getUniqueId();
		}
		if(USERNAME_REGEX.matcher(username).matches()){
			return this.uuidCache.get(username);
		}
		return null;
	}

	public ConcurrentMap<UUID, FactionUser> getOnlineStorage(){
		return this.onlineStorage;
	}

	public void saveUser(FactionUser user){
	}

	public abstract void saveUserData();

	public abstract void reloadUserData();
}