package idaniel84.timer.argument;

import idaniel84.HCF;
import idaniel84.timer.PlayerTimer;
import idaniel84.timer.Timer;
import net.veilmc.util.JavaUtils;
import net.veilmc.util.command.CommandArgument;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TimerSetArgument
		extends CommandArgument{
	private static final Pattern WHITESPACE_TRIMMER = Pattern.compile("\\s");
	private final HCF plugin;

	public TimerSetArgument(HCF plugin){
		super("set", "Set remaining timer time");
		this.plugin = plugin;
		this.permission = "idaniel84.command.timer.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <timerName> <all|playerName> <remaining>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 4){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		long duration = JavaUtils.parse(args[3]);
		if(duration == -1){
			sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
			return true;
		}
		PlayerTimer playerTimer = null;
		for(Timer timer : this.plugin.getTimerManager().getTimers()){
			if(!(timer instanceof PlayerTimer) || !WHITESPACE_TRIMMER.matcher(ChatColor.stripColor(timer.getName())).replaceAll("").equalsIgnoreCase(args[1]))
				continue;
			playerTimer = (PlayerTimer) timer;
			break;
		}
		if(playerTimer == null){
			sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
			return true;
		}
		if(args[2].equalsIgnoreCase("all")){
			for(Player player : Bukkit.getOnlinePlayers()){
				playerTimer.setCooldown(player, player.getUniqueId(), duration, true);
			}
			sender.sendMessage(ChatColor.BLUE + "Set timer " + ChatColor.BLUE + playerTimer.getName() + " for all to " + DurationFormatUtils.formatDurationWords(duration, true, true) + '.');
		}else{
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
			Player targetPlayer = null;
			if(target == null || sender instanceof Player && (targetPlayer = target.getPlayer()) != null && !((Player) sender).canSee(targetPlayer)){
				sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
				return true;
			}
			playerTimer.setCooldown(targetPlayer, target.getUniqueId(), duration, true);
			sender.sendMessage(ChatColor.BLUE + "Set timer " + playerTimer.getName() + ChatColor.BLUE + " duration to " + DurationFormatUtils.formatDurationWords(duration, true, true) + " for " + target.getName() + '.');
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 2){
			return FluentIterable.from(this.plugin.getTimerManager().getTimers()).filter((Predicate) timer -> timer instanceof PlayerTimer).transform(new Function<Timer, String>(){
				@Nullable
				public String apply(Timer timer){
					return ChatColor.stripColor(WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll(""));
				}
			}).toList();
		}
		if(args.length == 3){
			ArrayList<String> list = new ArrayList<String>();
			list.add("ALL");
			Player player = sender instanceof Player ? (Player) sender : null;
			for(Player target : Bukkit.getOnlinePlayers()){
				if(player == null || player.canSee(target)){
					list.add(target.getName());
				}
			}
			return list;
		}
		return Collections.emptyList();
	}

}

