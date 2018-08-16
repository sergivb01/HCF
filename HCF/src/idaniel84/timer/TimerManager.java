package idaniel84.timer;

import idaniel84.HCF;
import idaniel84.kothgame.EventTimer;
import idaniel84.timer.type.*;
import net.veilmc.util.Config;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TimerManager
		implements Listener{
	public final LogoutTimer logoutTimer;
	public final EnderPearlTimer enderPearlTimer;
	public final NotchAppleTimer notchAppleTimer;
	public final GoldenAppleTimer goldenAppleTimer;
	public final PvpProtectionTimer pvpProtectionTimer;
	public final PvpClassWarmupTimer pvpClassWarmupTimer;
	public final StuckTimer stuckTimer;
	public final SpawnTagTimer spawnTagTimer;
	public final TeleportTimer teleportTimer;
	public final EventTimer eventTimer;
	public final ArcherTimer archerTimer;
	public final SOTWTimer sotwTimer;
	private final Set<Timer> timers = new HashSet<Timer>();
	private final JavaPlugin plugin;

	public TimerManager(HCF plugin){
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.archerTimer = new ArcherTimer(plugin);
		this.registerTimer(this.archerTimer);
		this.sotwTimer = new SOTWTimer();
		this.registerTimer(this.sotwTimer);
		this.enderPearlTimer = new EnderPearlTimer(plugin);
		this.registerTimer(this.enderPearlTimer);
		this.logoutTimer = new LogoutTimer();
		this.registerTimer(this.logoutTimer);


		this.notchAppleTimer = new NotchAppleTimer(plugin);
		this.registerTimer(this.notchAppleTimer);


		this.goldenAppleTimer = new GoldenAppleTimer(plugin);
		this.registerTimer(this.goldenAppleTimer);


		this.stuckTimer = new StuckTimer();
		this.registerTimer(this.stuckTimer);
		this.pvpProtectionTimer = new PvpProtectionTimer(plugin);
		this.registerTimer(this.pvpProtectionTimer);
		this.spawnTagTimer = new SpawnTagTimer(plugin);
		this.registerTimer(this.spawnTagTimer);
		this.teleportTimer = new TeleportTimer(plugin);
		this.registerTimer(this.teleportTimer);
		this.eventTimer = new EventTimer(plugin);
		this.registerTimer(this.eventTimer);
		this.pvpClassWarmupTimer = new PvpClassWarmupTimer(plugin);
		this.registerTimer(this.pvpClassWarmupTimer);
	}

	public void enable(){
		for(Timer timer : timers){
			timer.load(new Config(HCF.getPlugin(), "timer.yml"));
		}
	}

	public void disable(){
		for(Timer timer : timers){
			timer.onDisable(new Config(HCF.getPlugin(), "timer.yml"));
		}
	}

	public Collection<Timer> getTimers(){
		return this.timers;
	}

	public void registerTimer(Timer timer){

		this.timers.add(timer);
		if(timer instanceof Listener){
			this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
		}
	}

	public void unregisterTimer(Timer timer){
		this.timers.remove(timer);
	}
}