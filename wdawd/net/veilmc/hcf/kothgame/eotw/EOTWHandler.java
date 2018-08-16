package net.veilmc.hcf.kothgame.eotw;

import net.veilmc.base.ServerHandler;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.claim.Claim;
import net.veilmc.hcf.faction.type.ClaimableFaction;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.listener.BorderListener;

import java.util.concurrent.TimeUnit;

import net.veilmc.util.BukkitUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EOTWHandler{
	public static final int BORDER_DECREASE_MINIMUM = 1000;
	public static final int BORDER_DECREASE_AMOUNT = 200;
	public static final long BORDER_DECREASE_TIME_MILLIS = TimeUnit.MINUTES.toMillis(5);
	public static final int BORDER_DECREASE_TIME_SECONDS = (int) (BORDER_DECREASE_TIME_MILLIS / 1000);
	public static final String BORDER_DECREASE_TIME_WORDS = DurationFormatUtils.formatDurationWords((long) BORDER_DECREASE_TIME_MILLIS, (boolean) true, (boolean) true);
	public static final String BORDER_DECREASE_TIME_ALERT_WORDS = DurationFormatUtils.formatDurationWords((long) (BORDER_DECREASE_TIME_MILLIS / 2), (boolean) true, (boolean) true);
	public static final long EOTW_WARMUP_WAIT_MILLIS = TimeUnit.MINUTES.toMillis(5);

	public static final int EOTW_WARMUP_WAIT_SECONDS = (int) (EOTW_WARMUP_WAIT_MILLIS / 6000);
	private static final long EOTW_CAPPABLE_WAIT = TimeUnit.MINUTES.toMillis(5);

	private final HCF plugin;
	private EotwRunnable runnable;

	public EOTWHandler(HCF plugin){
		this.plugin = plugin;
	}

	public EotwRunnable getRunnable(){
		return this.runnable;
	}

	public boolean isEndOfTheWorld(){
		return this.isEndOfTheWorld(true);
	}

	public void setEndOfTheWorld(boolean yes){
		if(yes == this.isEndOfTheWorld(false)){
			return;
		}
		if(yes){
			this.runnable = new EotwRunnable(this.plugin.getServerHandler().getWorldBorder());
			this.runnable.runTaskTimer(this.plugin, 1, 100);
		}else if(this.runnable != null){
			this.runnable.cancel();
			this.runnable = null;
		}
	}

	public boolean isEndOfTheWorld(boolean ignoreWarmup){
		return this.runnable != null && (!ignoreWarmup || this.runnable.getElapsedMilliseconds() > 0);
	}

	public static final class EotwRunnable
			extends BukkitRunnable{
		private static final PotionEffect WITHER = new PotionEffect(PotionEffectType.WITHER, 200, 0);
		private boolean hasInformedStarted = false;
		private long startStamp;
		private int borderSize;

		public EotwRunnable(int borderSize){
			this.borderSize = borderSize;
			this.startStamp = System.currentTimeMillis() + EOTWHandler.EOTW_WARMUP_WAIT_MILLIS;
		}

		public long getTimeUntilStarting(){
			long difference = System.currentTimeMillis() - this.startStamp;
			return difference > 0 ? 0 : Math.abs(difference);
		}

		public long getTimeUntilCappable(){
			return EOTW_CAPPABLE_WAIT - this.getElapsedMilliseconds();
		}

		public long getElapsedMilliseconds(){
			return System.currentTimeMillis() - this.startStamp;
		}

		public void run(){
			long elapsedMillis = this.getElapsedMilliseconds();
			int elapsedSeconds = (int) Math.round((double) elapsedMillis / 1000.0);
			if(!this.hasInformedStarted && elapsedSeconds >= 0){
				for(Faction faction : HCF.getPlugin().getFactionManager().getFactions()){
					if(!(faction instanceof ClaimableFaction)) continue;
					ClaimableFaction claimableFaction = (ClaimableFaction) faction;
					for(Claim claims : claimableFaction.getClaims()){
						claimableFaction.removeClaim(claims, Bukkit.getConsoleSender());
					}
					claimableFaction.getClaims().clear();
				}
				this.hasInformedStarted = true;
				//    Bukkit.broadcastMessage((String)(ChatColor.DARK_RED.toString() + (Object)ChatColor.BOLD + "End Of The World" + (Object)ChatColor.RED + " has began. Border will decrease by " + 200 + " blocks every " + EOTWHandler.BORDER_DECREASE_TIME_WORDS + " until at " + 1000 + " blocks."));
				Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "              End Of The World");
				Bukkit.broadcastMessage(ChatColor.RED + "                      has begun!");
				Bukkit.broadcastMessage(ChatColor.GRAY + "");
				Bukkit.broadcastMessage(ChatColor.YELLOW + "              All Faction claims have");
				Bukkit.broadcastMessage(ChatColor.YELLOW + "                  been unclaimed!");
				Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				return;
			}
			if(elapsedMillis < 0 && elapsedMillis >= -EOTWHandler.EOTW_WARMUP_WAIT_MILLIS){
				Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "End Of The World" + ChatColor.RED + " will start in " + HCF.getRemaining(Math.abs(elapsedMillis), true, false) + '.');
				return;
			}
			for(Player on : Bukkit.getOnlinePlayers()){
				if(BorderListener.isWithinBorder(on.getLocation())) continue;
				on.sendMessage(ChatColor.RED + "EOTW is active and your outside of the border. You will get wither.");
				on.addPotionEffect(WITHER, true);
			}
			if(HCF.getPlugin().getServerHandler().getWorldBorder() <= 1000){
				return;
			}
			int newBorderSize = this.borderSize - 200;
			if(newBorderSize <= 1000){
				return;
			}
			if(elapsedSeconds % EOTWHandler.BORDER_DECREASE_TIME_SECONDS == 0){
				int borderSize;
				World.Environment normal = World.Environment.NORMAL;
				this.borderSize = borderSize = newBorderSize;
				HCF.getPlugin().getServerHandler().setServerBorder(normal, Integer.valueOf(borderSize));
				Bukkit.broadcastMessage(ChatColor.RED + "Border has been decreased to " + ChatColor.RED + newBorderSize + ChatColor.RED + " blocks.");
			}else if((long) elapsedSeconds % TimeUnit.MINUTES.toSeconds(5) == 0){
				Bukkit.broadcastMessage(ChatColor.RED + "Border decreasing to " + ChatColor.RED + newBorderSize + ChatColor.RED + " blocks in " + ChatColor.RED + EOTWHandler.BORDER_DECREASE_TIME_ALERT_WORDS + ChatColor.DARK_AQUA + '.');
			}
		}
	}

}

