package idaniel84.command;

import idaniel84.HCF;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;

public class LagCommand
		implements CommandExecutor{
	private final HCF plugin;

	public LagCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		// double tps = Double.parseDouble(reflection.getTPS(0).replace(",", ".")/* for spanish dedis */);
		// double lag = Math.round((1.0 - tps / 20.0) * 100.0);
		RuntimeMXBean serverStart = ManagementFactory.getRuntimeMXBean();
		String serverUptime = DurationFormatUtils.formatDurationWords(serverStart.getUptime(), true, true);
		DecimalFormat df = new DecimalFormat("#.#");
		//   ChatColor colour = tps >= 18.0 ? ChatColor.GREEN : (tps >= 15.0 ? ChatColor.YELLOW : ChatColor.RED);
		// Double tpsF = Math.round(tps * 10000.0) / 10000.0;
		sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "Server TPS: ");
		//   sender.sendMessage(ChatColor.YELLOW + "  " + colour + df.format(tpsF));
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Server Lag: ");
		//   sender.sendMessage(ChatColor.YELLOW + "  " + colour + (double)Math.round(lag * 10000.0) / 10000.0 + '%');
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Players: ");
		sender.sendMessage(ChatColor.YELLOW + "  " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Uptime: ");
		sender.sendMessage(ChatColor.YELLOW + "  " + serverUptime);
		return true;
	}
}