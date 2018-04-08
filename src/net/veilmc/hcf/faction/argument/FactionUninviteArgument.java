package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.struct.Role;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionUninviteArgument
		extends CommandArgument{
	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final HCF plugin;

	public FactionUninviteArgument(HCF plugin){
		super("uninvite", "Revoke an invitation to a player.", new String[]{"deinvite", "deinv", "uninv", "revoke"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <all|playerName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can un-invite from a faction.");
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
		FactionMember factionMember = playerFaction.getMember(player);
		if(factionMember.getRole() == Role.MEMBER){
			sender.sendMessage(ChatColor.RED + "You must be a faction officer to un-invite players.");
			return true;
		}
		Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
		if(args[1].equalsIgnoreCase("all")){
			invitedPlayerNames.clear();
			sender.sendMessage(ChatColor.YELLOW + "You have cleared all pending invitations.");
			return true;
		}
		if(!invitedPlayerNames.remove(args[1])){
			sender.sendMessage(ChatColor.RED + "There is not a pending invitation for " + args[1] + '.');
			return true;
		}
		playerFaction.broadcast(ChatColor.YELLOW + factionMember.getRole().getAstrix() + sender.getName() + " has uninvited " + ConfigurationService.ENEMY_COLOUR + args[1] + ChatColor.YELLOW + " from the faction.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER){
			return Collections.emptyList();
		}
		ArrayList<String> results = new ArrayList<String>(COMPLETIONS);
		results.addAll(playerFaction.getInvitedPlayerNames());
		return results;
	}
}

