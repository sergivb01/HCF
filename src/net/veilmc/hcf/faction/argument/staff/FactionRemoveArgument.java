package net.veilmc.hcf.faction.argument.staff;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FactionRemoveArgument extends CommandArgument{
	private final ConversationFactory factory;
	private final HCF plugin;

	public FactionRemoveArgument(HCF plugin){
		super("remove", "Remove a faction.");
		this.plugin = plugin;
		this.aliases = new String[]{"delete", "forcedisband", "forceremove"};
		this.permission = "hcf.command.faction.argument." + this.getName();
		this.factory = new ConversationFactory(plugin).withFirstPrompt(new RemoveAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <all|factionName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		if(args[1].equalsIgnoreCase("all")){
			if(!(sender instanceof ConsoleCommandSender)){
				sender.sendMessage(ChatColor.RED + "This command can be only executed from console.");
				return true;
			}
			Conversable conversable = (Conversable) sender;
			conversable.beginConversation(this.factory.buildConversation(conversable));
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
		if(faction == null){
			sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
			return true;
		}
		if(this.plugin.getFactionManager().removeFaction(faction, sender)){
			Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Disbanded faction " + faction.getName() + ChatColor.YELLOW + '.');
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		if(args[1].isEmpty()){
			return null;
		}
		Player player = (Player) sender;
		ArrayList<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
		for(Player target : Bukkit.getOnlinePlayers()){
			if(!player.canSee(target) || results.contains(target.getName())) continue;
			results.add(target.getName());
		}
		return results;
	}

	private static class RemoveAllPrompt
			extends StringPrompt{
		private final HCF plugin;

		public RemoveAllPrompt(HCF plugin){
			this.plugin = plugin;
		}

		public String getPromptText(ConversationContext context){
			return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All factions" + ChatColor.YELLOW + " will be cleared. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
		}

		public Prompt acceptInput(ConversationContext context, String string){
			switch(string.toLowerCase()){
				case "yes":{
					final Conversable[] conversable = new Conversable[1];
					new Thread(() -> {
						Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
							for(Faction faction : this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof PlayerFaction).collect(Collectors.toList())){
								this.plugin.getFactionManager().removeFaction(faction, Bukkit.getConsoleSender());
							}
							Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "All factions have been disbanded" + ((conversable[0] = context.getForWhom()) instanceof CommandSender ? new StringBuilder().append(" by ").append(((CommandSender) conversable[0]).getName()).toString() : "") + '.');
						});
					}).start();
					return Prompt.END_OF_CONVERSATION;
				}
				case "no":{
					context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of disbanding all factions.");
					return Prompt.END_OF_CONVERSATION;
				}
			}
			context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of disbanding all factions cancelled.");
			return Prompt.END_OF_CONVERSATION;
		}
	}

}

