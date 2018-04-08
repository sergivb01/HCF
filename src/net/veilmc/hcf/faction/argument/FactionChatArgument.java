package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.struct.ChatChannel;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionChatArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionChatArgument(HCF plugin){
		super("chat", "Toggle faction chat only mode on or off.", new String[]{"c"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " [fac|public|ally] [message]";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		final Player player = (Player) sender;
		final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		final FactionMember member = playerFaction.getMember(player.getUniqueId());
		final ChatChannel currentChannel = member.getChatChannel();
		final ChatChannel parsed = (args.length >= 2) ? ChatChannel.parse(args[1], null) : currentChannel.getRotation();
		if(parsed == null && currentChannel != ChatChannel.PUBLIC){
			final Collection<Player> recipients = playerFaction.getOnlinePlayers();
			if(currentChannel == ChatChannel.ALLIANCE){
				for(final PlayerFaction ally : playerFaction.getAlliedFactions()){
					recipients.addAll(ally.getOnlinePlayers());
				}
			}
			final String format = String.format(currentChannel.getRawFormat(player), "", StringUtils.join(args, ' ', 1, args.length));
			for(final Player recipient : recipients){
				recipient.sendMessage(format);
			}
			return true;
		}
		final ChatChannel newChannel = (parsed == null) ? currentChannel.getRotation() : parsed;
		member.setChatChannel(newChannel);
		sender.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.AQUA + newChannel.getDisplayName().toLowerCase() + ChatColor.YELLOW + " chat mode.");
		return true;
	}


	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		ChatChannel[] values = ChatChannel.values();
		ArrayList<String> results = new ArrayList<String>(values.length);
		for(ChatChannel type : values){
			results.add(type.getName());
		}
		return results;
	}
}

