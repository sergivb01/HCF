package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.faction.event.FactionRelationCreateEvent;
import idaniel84.faction.struct.Relation;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionAllyArgument
		extends CommandArgument{
	private static final Relation RELATION = Relation.ALLY;
	private final HCF plugin;

	public FactionAllyArgument(HCF plugin){
		super("ally", "Make an ally pact with other factions.", new String[]{"alliance"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <factionName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER){
			sender.sendMessage(ChatColor.RED + "You must be an officer to make relation wishes.");
			return true;
		}
		Faction containingFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
		if(!(containingFaction instanceof PlayerFaction)){
			sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}
		PlayerFaction targetFaction = (PlayerFaction) containingFaction;
		if(playerFaction.equals(targetFaction)){
			sender.sendMessage(ChatColor.RED + "You cannot send " + RELATION.getDisplayName() + ChatColor.RED + " requests to your own faction.");
			return true;
		}
		Collection<UUID> allied = playerFaction.getAllied();
		if(allied.size() >= ConfigurationService.MAX_ALLIES_PER_FACTION){
			sender.sendMessage(ChatColor.RED + "Your faction cant have more allies than " + ConfigurationService.MAX_ALLIES_PER_FACTION + '.');
			return true;
		}
		if(targetFaction.getAllied().size() >= ConfigurationService.MAX_ALLIES_PER_FACTION){
			sender.sendMessage(targetFaction.getDisplayName(sender) + ChatColor.RED + " has reached their maximum alliance limit, which is " + ConfigurationService.MAX_ALLIES_PER_FACTION + '.');
			return true;
		}
		if(allied.contains(targetFaction.getUniqueID())){
			sender.sendMessage(ChatColor.RED + "Your faction already is " + RELATION.getDisplayName() + 'd' + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
			return true;
		}
		if(targetFaction.getRequestedRelations().remove(playerFaction.getUniqueID()) != null){
			FactionRelationCreateEvent event = new FactionRelationCreateEvent(playerFaction, targetFaction, RELATION);
			Bukkit.getPluginManager().callEvent(event);
			targetFaction.getRelations().put(playerFaction.getUniqueID(), RELATION);
			targetFaction.broadcast(ChatColor.YELLOW + "Your faction is now " + RELATION.getDisplayName() + 'd' + ChatColor.YELLOW + " with " + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + '.');
			playerFaction.getRelations().put(targetFaction.getUniqueID(), RELATION);
			playerFaction.broadcast(ChatColor.YELLOW + "Your faction is now " + RELATION.getDisplayName() + 'd' + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
			return true;
		}
		if(playerFaction.getRequestedRelations().putIfAbsent(targetFaction.getUniqueID(), RELATION) != null){
			sender.sendMessage(ChatColor.YELLOW + "Your faction has already requested to " + RELATION.getDisplayName() + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
			return true;
		}
		playerFaction.broadcast(targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + " were informed that you wish to be " + RELATION.getDisplayName() + ChatColor.YELLOW + '.');
		targetFaction.broadcast(playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " has sent a request to be " + RELATION.getDisplayName() + ChatColor.YELLOW + ". Use " + ConfigurationService.ALLY_COLOUR + "/faction " + this.getName() + ' ' + playerFaction.getName() + ChatColor.YELLOW + " to accept.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			return Collections.emptyList();
		}
		ArrayList<String> results = new ArrayList<String>();
		return results;
	}
}

