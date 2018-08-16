package idaniel84.faction.argument;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.HCF;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionDisbandArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionDisbandArgument(HCF plugin){
		super("disband", "Disband your faction.");
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
		if(playerFaction.isRaidable() && !this.plugin.getEotwHandler().isEndOfTheWorld()){
			sender.sendMessage(ChatColor.RED + "You cannot disband your faction while it is raidable.");
			return true;
		}
		if(playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER){
			sender.sendMessage(ChatColor.RED + "You must be a leader to disband the faction.");
			return true;
		}
		this.plugin.getFactionManager().removeFaction(playerFaction, sender);
		return true;
	}
}

