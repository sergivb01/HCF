package net.veilmc.hcf.kothgame.eotw;

import net.veilmc.hcf.HCF;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.conversations.*;

import java.util.Collections;
import java.util.List;

public class EotwCommand
		implements CommandExecutor,
		TabCompleter{
	private final ConversationFactory factory;

	public EotwCommand(HCF plugin){
		this.factory = new ConversationFactory(plugin).withFirstPrompt(new EotwPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof ConsoleCommandSender)){
			sender.sendMessage(ChatColor.RED + "Command Console only.");
			return true;
		}
		Conversable conversable = (Conversable) sender;
		conversable.beginConversation(this.factory.buildConversation(conversable));
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}

	private static final class EotwPrompt
			extends StringPrompt{
		private EotwPrompt(){
		}

		public String getPromptText(ConversationContext context){
			return ChatColor.YELLOW + "Are you sure you want to do this? The server will be in EOTW mode, If EOTW mode is active, all claims whilst making Spawn a KOTH. " + "You will still have " + EOTWHandler.EOTW_WARMUP_WAIT_SECONDS + " seconds to cancel this using the same command though. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
		}

		public Prompt acceptInput(ConversationContext context, String string){
			if(string.equalsIgnoreCase("yes")){
				boolean newStatus = !HCF.getPlugin().getEotwHandler().isEndOfTheWorld(false);
				Conversable conversable = context.getForWhom();
				Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
				Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "              End Of The World");
				Bukkit.broadcastMessage(ChatColor.RED + "                 has been activated!");
				Bukkit.broadcastMessage(ChatColor.GRAY + "");
				Bukkit.broadcastMessage(ChatColor.YELLOW + "              All Faction claims will be");
				Bukkit.broadcastMessage(ChatColor.YELLOW + "               unclaimed in 5 minutes!");
				Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);

				if(conversable instanceof CommandSender){
					Command.broadcastCommandMessage((CommandSender) conversable, ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
				}else{
					conversable.sendRawMessage(ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
				}
				HCF.getPlugin().getEotwHandler().setEndOfTheWorld(newStatus);
			}else if(string.equalsIgnoreCase("no")){
				context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of setting EOTW mode.");
			}else{
				context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of toggling EOTW mode has been cancelled.");
			}
			return Prompt.END_OF_CONVERSATION;
		}
	}

}

