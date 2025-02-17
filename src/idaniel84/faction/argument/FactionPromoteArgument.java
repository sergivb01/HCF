package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.HCF;
import idaniel84.faction.FactionMember;
import idaniel84.faction.struct.Relation;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionPromoteArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionPromoteArgument(HCF plugin){
		super("promote", "Promotes a player to a captain.");
		this.plugin = plugin;
		this.aliases = new String[]{"captain", "officer", "mod", "moderator"};
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can set faction captains.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		if(playerFaction.getMember(uuid).getRole() != Role.LEADER){
			sender.sendMessage(ChatColor.RED + "You must be a faction leader to assign members as a captain.");
			return true;
		}
		FactionMember targetMember = playerFaction.getMember(args[1]);
		if(targetMember == null){
			sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
			return true;
		}
		if(targetMember.getRole() != Role.MEMBER){
			sender.sendMessage(ChatColor.RED + "You can only assign captains to members, " + targetMember.getName() + " is a " + targetMember.getRole().getName() + '.');
			return true;
		}
		Role role = Role.CAPTAIN;
		targetMember.setRole(role);
		playerFaction.broadcast(Relation.MEMBER.toChatColour() + role.getAstrix() + targetMember.getName() + ChatColor.YELLOW + " has been assigned as a faction captain.");
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
		for(Map.Entry<UUID, FactionMember> entry : playerFaction.getMembers().entrySet()){
			OfflinePlayer target;
			String targetName;
			if(entry.getValue().getRole() != Role.MEMBER || (targetName = (target = Bukkit.getOfflinePlayer(entry.getKey())).getName()) == null)
				continue;
			results.add(targetName);
		}
		return results;
	}
}

