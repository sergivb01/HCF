package idaniel84.timer.type;

import idaniel84.HCF;
import idaniel84.classes.archer.ArcherClass;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.event.TimerExpireEvent;
import idaniel84.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArcherTimer
		extends PlayerTimer
		implements Listener{
	private final HCF plugin;

	public ArcherTimer(HCF plugin){
		//super("Archer Tag", TimeUnit.SECONDS.toMillis(7L));
		super(ConfigurationService.ARCHER_TIMER, TimeUnit.SECONDS.toMillis(10L));
		this.plugin = plugin;
	}

	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.ARCHER_COLOUR;
	}

	public void run(){
	}

	@EventHandler
	public void onExpire(TimerExpireEvent e){
		if((e.getUserUUID().isPresent()) &&
				(e.getTimer().equals(this))){
			UUID userUUID = e.getUserUUID().get();
			Player player = Bukkit.getPlayer(userUUID);
			if(player == null) return;

			if(!player.isOnline()) return;

			Bukkit.getPlayer(ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.GOLD + "Your archer mark on " + ChatColor.RED + player.getName() + ChatColor.GOLD + " has expired.");
			player.sendMessage(ChatColor.GOLD + "You are no longer archer marked.");
			ArcherClass.tagged.remove(player.getUniqueId());

			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
				for(Player players : Bukkit.getServer().getOnlinePlayers()){
					this.plugin.getScoreboardHandler().getPlayerBoard(players.getUniqueId()).init(player);
				}
			}, 10L);

		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
		if(((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Player))){
			Player entity = (Player) e.getEntity();
			Entity damager = e.getDamager();
			if(getRemaining(entity) > 0L){
				e.setDamage(e.getDamage() * 1.3D);
			}
		}
		if(((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow)) && ((((Arrow) e.getDamager()).getShooter() instanceof Player))){
			Player entity = (Player) e.getEntity();
			Entity damager = (Player) ((Arrow) e.getDamager()).getShooter();
			if(((damager instanceof Player)) &&
					(getRemaining(entity) > 0L)){
				if(ArcherClass.tagged.get(entity.getUniqueId()).equals(damager.getUniqueId())){
					setCooldown(entity, entity.getUniqueId());
				}
				e.setDamage(e.getDamage() * 1.3D);
			}
		}
	}
}