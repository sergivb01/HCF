package net.veilmc.hcf.timer.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.timer.PlayerTimer;
import net.veilmc.hcf.timer.Timer;
import net.veilmc.hcf.utils.UUIDFetcher;
import net.veilmc.util.command.CommandArgument;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TimerCheckArgument
		extends CommandArgument{
	private final HCF plugin;

	public TimerCheckArgument(HCF plugin){
		super("check", "Check remaining timer time");
		this.plugin = plugin;
		this.permission = "hcf.command.timer.argument" + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <timerName> <playerName>";
	}

	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args){
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		PlayerTimer temporaryTimer = null;
		for(Timer timer : this.plugin.getTimerManager().getTimers()){
			if(!(timer instanceof PlayerTimer) || !timer.getName().equalsIgnoreCase(args[1])) continue;
			temporaryTimer = (PlayerTimer) timer;
			break;
		}
		if(temporaryTimer == null){
			sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
			return true;
		}
		final PlayerTimer playerTimer = temporaryTimer;
		new BukkitRunnable(){

			public void run(){
				UUID uuid;
				try{
					uuid = UUIDFetcher.getUUIDOf(args[2]);
				}catch(Exception ex){
					sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
					return;
				}
				if(uuid == null){
					sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
					return;
				}
				long remaining = playerTimer.getRemaining(uuid);
				sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + ChatColor.YELLOW + " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
			}
		}.runTaskAsynchronously(this.plugin);
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return args.length == 2 ? null : Collections.emptyList();
	}

}

