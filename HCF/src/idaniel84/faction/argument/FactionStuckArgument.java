package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import idaniel84.kothgame.faction.EventFaction;
import idaniel84.timer.type.StuckTimer;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionStuckArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionStuckArgument(HCF plugin){
		super("stuck", "Teleport to a safe position.", new String[]{"trap", "trapped"});
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
		if(player.getWorld().getEnvironment() != World.Environment.NORMAL){
			sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
			return true;
		}
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
		if((factionAt instanceof EventFaction)){
			sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
			return true;
		}
		StuckTimer stuckTimer = this.plugin.getTimerManager().stuckTimer;
		if(!stuckTimer.setCooldown(player, player.getUniqueId())){
			sender.sendMessage(ChatColor.RED + "Your " + stuckTimer.getDisplayName() + ChatColor.RED + " timer is already active.");
			return true;
		}
		sender.sendMessage(ChatColor.YELLOW + stuckTimer.getDisplayName() + ChatColor.YELLOW + " timer has started. " + "Teleportation will commence in " + ChatColor.LIGHT_PURPLE + HCF.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". " + "This will cancel if you move more than " + 5 + " blocks.");
		return true;
	}
}

