package idaniel84.faction.argument;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.HCF;
import idaniel84.faction.FactionMember;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.utils.ConfigurationService;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionSetHomeArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionSetHomeArgument(HCF plugin){
		super("sethome", "Sets the faction home location.");
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		FactionMember factionMember = playerFaction.getMember(player);
		if(factionMember.getRole() == Role.MEMBER){
			sender.sendMessage(ChatColor.RED + "You must be a faction officer to set the home.");
			return true;
		}
		Location location = player.getLocation();
		boolean insideTerritory = false;
		for(Claim claim : playerFaction.getClaims()){
			if(!claim.contains(location)) continue;
			insideTerritory = true;
			break;
		}
		if(!insideTerritory){
			player.sendMessage(ChatColor.RED + "You may only set your home in your territory.");
			return true;
		}
		playerFaction.setHome(location);
		playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has set the faction home.");
		return true;
	}
}

