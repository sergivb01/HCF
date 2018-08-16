package idaniel84.command;

import idaniel84.utils.ConfigurationService;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

public class ServerTimeCommand
		implements CommandExecutor,
		TabCompleter{
	private static final FastDateFormat FORMAT = FastDateFormat.getInstance((String) "E MMM dd h:mm:ssa z yyyy", (TimeZone) ConfigurationService.SERVER_TIME_ZONE);

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		sender.sendMessage(ChatColor.YELLOW + "The server time is " + ChatColor.AQUA + FORMAT.format(System.currentTimeMillis()) + ChatColor.AQUA + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}
}

