package idaniel84.kothgame.conquest;

import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.kothgame.EventType;
import idaniel84.kothgame.tracker.ConquestTracker;
import net.veilmc.util.command.CommandArgument;
import com.google.common.primitives.Ints;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ConquestSetpointsArgument
		extends CommandArgument{
	private final HCF plugin;

	public ConquestSetpointsArgument(HCF plugin){
		super("setpoints", "Sets the points of a faction in the Conquest event", "idaniel84.command.conquest.argument.setpoints");
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <factionName> <amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
		if(!(faction instanceof PlayerFaction)){
			sender.sendMessage(ChatColor.RED + "Faction " + args[1] + " is either not found or is not a player faction.");
			return true;
		}
		Integer amount = Ints.tryParse(args[2]);
		if(amount == null){
			sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
			return true;
		}
		if(amount > 300){
			sender.sendMessage(ChatColor.RED + "Maximum points for Conquest is " + 100 + '.');
			return true;
		}
		PlayerFaction playerFaction = (PlayerFaction) faction;
		((ConquestTracker) EventType.CONQUEST.getEventTracker()).setPoints(playerFaction, amount);
		Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set the points of faction " + playerFaction.getName() + " to " + amount + '.');
		return true;
	}
}

