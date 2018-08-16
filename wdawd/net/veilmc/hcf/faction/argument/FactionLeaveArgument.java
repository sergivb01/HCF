package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.struct.Relation;
import net.veilmc.hcf.faction.struct.Role;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionLeaveArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionLeaveArgument(HCF plugin){
		super("leave", "Leave your current faction.");
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can leave faction.");
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		UUID uuid = player.getUniqueId();
		if(playerFaction.getMember(uuid).getRole() == Role.LEADER){
			sender.sendMessage(ChatColor.RED + "You cannot leave factions as a leader. Either use " + ChatColor.GOLD + '/' + label + " disband" + ChatColor.RED + " or " + ChatColor.GOLD + '/' + label + " leader" + ChatColor.RED + '.');
			return true;
		}
		if(playerFaction.setMember(player, null)){
			sender.sendMessage(ChatColor.YELLOW + "Successfully left the faction.");
			playerFaction.broadcast(Relation.ENEMY.toChatColour() + sender.getName() + ChatColor.YELLOW + " has left the faction.");
		}
		return true;
	}
}

