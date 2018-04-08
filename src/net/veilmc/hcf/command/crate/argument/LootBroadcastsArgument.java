package net.veilmc.hcf.command.crate.argument;

import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LootBroadcastsArgument
		extends CommandArgument{
	public LootBroadcastsArgument(){
		super("broadcasts", "Toggle broadcasts for key announcements", new String[]{"togglealerts", "togglebroadcasts"});
		this.permission = "hcf.command.loot.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		ConfigurationService.CRATE_BROADCASTS = !ConfigurationService.CRATE_BROADCASTS;
		boolean newBroadcasts = ConfigurationService.CRATE_BROADCASTS;
		sender.sendMessage(ChatColor.GOLD + "Crate keys " + (newBroadcasts ? "now" : "no longer") + " broadcasts reward messages.");
		return true;
	}
}

