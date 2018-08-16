package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.faction.FactionMember;
import idaniel84.faction.struct.Relation;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionDemoteArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionDemoteArgument(HCF plugin){
		super("demote", "Demotes a player to a member.", new String[]{"uncaptain", "delcaptain", "delofficer"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName>";
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
		if(playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER){
			sender.sendMessage(ChatColor.RED + "You must be a officer to edit the roster.");
			return true;
		}
		FactionMember targetMember = playerFaction.getMember(args[1]);
		if(targetMember == null){
			sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
			return true;
		}

		if(targetMember.getRole() != Role.CAPTAIN){
			sender.sendMessage(ChatColor.RED + "You can only demote faction captains.");
			return true;
		}
		playerFaction.broadcast(Relation.MEMBER.toChatColour() + targetMember.getName() + ChatColor.YELLOW + " has been demoted from a faction " + targetMember.getRole().toString().toLowerCase() + ".");
		targetMember.setRole(Role.MEMBER);

		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER){
			return Collections.emptyList();
		}
		ArrayList<String> results = new ArrayList<String>();
		Set<UUID> keySet = playerFaction.getMembers().keySet();
		for(UUID entry : keySet){
			OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
			String targetName = target.getName();
			if(targetName == null || playerFaction.getMember(target.getUniqueId()).getRole() != Role.CAPTAIN) continue;
			results.add(targetName);
		}
		return results;
	}
}

