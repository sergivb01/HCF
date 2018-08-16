package idaniel84.faction.argument;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.HCF;
import idaniel84.faction.FactionMember;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionOpenArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionOpenArgument(HCF plugin){
		super("open", "Opens the faction to the public.");
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
		FactionMember factionMember = playerFaction.getMember(player.getUniqueId());
		if(factionMember.getRole() != Role.LEADER){
			sender.sendMessage(ChatColor.RED + "You must be a faction leader to do this.");
			return true;
		}
		boolean newOpen = !playerFaction.isOpen();
		playerFaction.setOpen(newOpen);
		playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " has " + (newOpen ? new StringBuilder().append(ChatColor.GREEN).append("opened").toString() : new StringBuilder().append(ChatColor.RED).append("closed").toString()) + ChatColor.YELLOW + " the faction to public.");
		return true;
	}
}

