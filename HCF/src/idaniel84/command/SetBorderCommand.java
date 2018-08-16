package idaniel84.command;

import idaniel84.HCF;
import net.veilmc.util.BukkitUtils;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class SetBorderCommand
		implements CommandExecutor, TabCompleter{
	private static final int MIN_SET_SIZE = 50;
	private static final int MAX_SET_SIZE = 25000;

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <worldType> <amount>");
			return true;
		}
		Optional<World.Environment> optional = Enums.getIfPresent(World.Environment.class, args[0]);
		if(!optional.isPresent()){
			sender.sendMessage(ChatColor.RED + "Environment '" + args[0] + "' not found.");
			return true;
		}
		Integer amount = Ints.tryParse(args[1]);
		if(amount == null){
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
			return true;
		}
		if(amount.intValue() < 50){
			sender.sendMessage(ChatColor.RED + "Minimum border size is " + 50 + 100 + '.');
			return true;
		}
		if(amount.intValue() > 25000){
			sender.sendMessage(ChatColor.RED + "Maximum border size is " + 25000 + '.');
			return true;
		}
		World.Environment environment = optional.get();
		HCF.getPlugin().getServerHandler().setServerBorder(environment, amount);
		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set border size of environment " + environment.name() + " to " + amount + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 1){
			return Collections.emptyList();
		}
		World.Environment[] values = World.Environment.values();
		List<String> results = new ArrayList(values.length);
		for(World.Environment environment : values){
			results.add(environment.name());
		}
		return BukkitUtils.getCompletions(args, results);
	}
}
