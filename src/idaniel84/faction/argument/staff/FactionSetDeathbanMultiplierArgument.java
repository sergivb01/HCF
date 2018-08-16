package idaniel84.faction.argument.staff;

import idaniel84.HCF;
import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import net.veilmc.util.command.CommandArgument;
import com.google.common.primitives.Doubles;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionSetDeathbanMultiplierArgument
		extends CommandArgument{
	private static final double MIN_MULTIPLIER = 0.0;
	private static final double MAX_MULTIPLIER = 5.0;
	private final HCF plugin;

	public FactionSetDeathbanMultiplierArgument(HCF plugin){
		super("setdeathbanmultiplier", "Sets the deathban multiplier of a faction.");
		this.plugin = plugin;
		this.permission = "hcf.command.faction.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName|factionName> <newMultiplier>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
		if(faction == null){
			sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}
		Double multiplier = Doubles.tryParse(args[2]);
		if(multiplier == null){
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
			return true;
		}
		if(multiplier < 0.0){
			sender.sendMessage(ChatColor.RED + "Deathban multipliers may not be less than " + 0.0 + '.');
			return true;
		}
		if(multiplier > 5.0){
			sender.sendMessage(ChatColor.RED + "Deathban multipliers may not be more than " + 5.0 + '.');
			return true;
		}
		double previousMultiplier = faction.getDeathbanMultiplier();
		faction.setDeathbanMultiplier(multiplier);
		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set deathban multiplier of " + faction.getName() + " from " + previousMultiplier + " to " + multiplier + '.');
		return true;
	}
}

