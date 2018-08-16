package idaniel84.deathban;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import idaniel84.utils.ConfigurationService;
import net.veilmc.util.Config;
import net.veilmc.util.PersistableLocation;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FlatFileDeathbanManager implements DeathbanManager{
	private static final int MAX_DEATHBAN_MULTIPLIER = 300;
	private final HCF plugin;
	private TObjectIntMap<UUID> livesMap;
	private Config livesConfig;

	public FlatFileDeathbanManager(final HCF plugin){
		super();
		this.plugin = plugin;
		this.reloadDeathbanData();
	}

	@Override
	public TObjectIntMap<UUID> getLivesMap(){
		return this.livesMap;
	}

	@Override
	public int getLives(final UUID uuid){
		return this.livesMap.get(uuid);
	}

	@Override
	public int setLives(final UUID uuid, final int lives){
		this.livesMap.put(uuid, lives);
		return lives;
	}

	@Override
	public int addLives(final UUID uuid, final int amount){
		return this.livesMap.adjustOrPutValue(uuid, amount, amount);
	}

	@Override
	public int takeLives(final UUID uuid, final int amount){
		return this.setLives(uuid, this.getLives(uuid) - amount);
	}

   /*@Override
    public long getDeathBanMultiplier(final Player player) {
        //if (player.hasPermission("idaniel84.deathban.extra")) {
        for (int i = 5; i < 21600; --i) {
        if (player.hasPermission("idaniel84.deathban.minutes." + i)) {
                    return TimeUnit.MINUTES.convert(i, TimeUnit.MILLISECONDS);
                }
            }
        return ConfigurationService.DEFAULT_DEATHBAN_DURATION;
    }

    @Override
    public Deathban applyDeathBan(final Player player, final String reason) {
        final Location location = player.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        long duration = (factionAt.isDeathban() || !factionAt.isSafezone()) ? (long)(factionAt.getDeathbanMultiplier() * getDeathBanMultiplier(player)) : 2L;
        return this.applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(FlatFileDeathbanManager.MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
    }

    @Override
    public Deathban applyDeathBan(final UUID uuid, final Deathban deathban) {
        this.plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }

    @Override
    public void reloadDeathbanData() {
        this.livesConfig = new Config(this.plugin, "lives");
        final Object object = this.livesConfig.get("lives");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            this.livesMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(keys.size(), 0.5f, 0);
            for (final String id : keys) {
                this.livesMap.put(UUID.fromString(id), this.livesConfig.getInt(section.getCurrentPath() + "." + id));
            }
        }
        else {
            this.livesMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(10, 0.5f, 0);
        }
    }

    @Override
    public void saveDeathbanData() {
        final Map<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.livesMap.size());
        this.livesMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        });
        this.livesConfig.set("lives", (Object)saveMap);
        this.livesConfig.save();
    }
}*/

	@Override
	public long getDeathBanMultiplier(final Player player){
		if(player.hasPermission("idaniel84.deathban.extra")){
			for(int i = 5; i < 21600; --i){
				if(player.hasPermission("idaniel84.deathban.seconds." + i)){
					return i / 1000;
				}
			}
		}
		return ConfigurationService.DEFAULT_DEATHBAN_DURATION;
	}

	@Override
	public Deathban applyDeathBan(final Player player, final String reason){
		final Location location = player.getLocation();
		final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
		long duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
		if(!factionAt.isDeathban()){
			duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
		}
		if(player.hasPermission("idaniel84.deathban.insider")){
			duration = 28800000L;
		}
		if(player.hasPermission("idaniel84.deathban.150m")){
			duration = 9000000L;
		}
		if(player.hasPermission("idaniel84.deathban.120m")){
			duration = 7200000L;
		}
		if(player.hasPermission("idaniel84.deathban.90m")){
			duration = 5400000L;
		}
		if(player.hasPermission("idaniel84.deathban.60m")){
			duration = 3600000L;
		}
		if(player.hasPermission("idaniel84.deathban.45m")){
			duration = 2700000L;
		}
		if(player.hasPermission("idaniel84.deathban.40m")){
			duration = 2700000L;
		}
		if(player.hasPermission("idaniel84.deathban.30m")){
			duration = 1800000L;
		}
		if(player.hasPermission("idaniel84.deathban.35m")){
			duration = 2100000L;
		}
		if(player.hasPermission("idaniel84.deathban.15m")){
			duration = 900000L;
		}
		if(player.hasPermission("idaniel84.deathban.10m")){
			duration = 600000L;
		}
		if(player.hasPermission("idaniel84.deathban.5m")){
			duration = 300000L;
		}

		return this.applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
	}

	@Override
	public Deathban applyDeathBan(final UUID uuid, final Deathban deathban){
		this.plugin.getUserManager().getUser(uuid).setDeathban(deathban);
		return deathban;
	}

	@Override
	public void reloadDeathbanData(){
		this.livesConfig = new Config(this.plugin, "lives");
		final Object object = this.livesConfig.get("lives");
		if(object instanceof MemorySection){
			final MemorySection section = (MemorySection) object;
			final Set<String> keys = section.getKeys(false);
			this.livesMap = (TObjectIntMap<UUID>) new TObjectIntHashMap(keys.size(), 0.5f, 0);
			for(final String id : keys){
				this.livesMap.put(UUID.fromString(id), this.livesConfig.getInt(section.getCurrentPath() + "." + id));
			}
		}else{
			this.livesMap = (TObjectIntMap<UUID>) new TObjectIntHashMap(10, 0.5f, 0);
		}
	}

	@Override
	public void saveDeathbanData(){
		final Map<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.livesMap.size());
		this.livesMap.forEachEntry((uuid, i) -> {
			saveMap.put(uuid.toString(), i);
			return true;
		});
		this.livesConfig.set("lives", saveMap);
		this.livesConfig.save();
	}
}
