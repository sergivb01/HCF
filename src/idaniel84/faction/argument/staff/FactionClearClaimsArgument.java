package idaniel84.faction.argument.staff;

import idaniel84.HCF;
import idaniel84.HCF;
import idaniel84.faction.type.ClaimableFaction;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class FactionClearClaimsArgument
		extends CommandArgument{
	private final ConversationFactory factory;
	private final HCF plugin;

	public FactionClearClaimsArgument(HCF plugin){
		super("clearclaims", "Clears the claims of a faction.");
		this.plugin = plugin;
		this.permission = "hcf.command.faction.argument." + this.getName();
		this.factory = new ConversationFactory(plugin).withFirstPrompt(new ClaimClearAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName|factionName|all>";
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
		if(faction instanceof ClaimableFaction){
			ClaimableFaction claimableFaction = (ClaimableFaction) faction;
			if(!claimableFaction.removeClaims(claimableFaction.getClaims(), sender)){
				sender.sendMessage(ChatColor.RED + "Potentially failed to remove claims.");

			}
			if(claimableFaction instanceof PlayerFaction){
				((PlayerFaction) claimableFaction).broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + "Your claims have been forcefully wiped by " + sender.getName() + '.');
			}
		}
		sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Claims belonging to " + faction.getName() + " have been forcefully wiped.");
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

	private static class ClaimClearAllPrompt
			extends StringPrompt{
		private final HCF plugin;

		public ClaimClearAllPrompt(HCF plugin){
			this.plugin = plugin;
		}

		public String getPromptText(ConversationContext context){
			return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All claims" + ChatColor.YELLOW + " will be cleared. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
		}

		public Prompt acceptInput(ConversationContext context, String string){
			String lowerCase;
			switch(lowerCase = string.toLowerCase()){
				case "yes":{
					Conversable conversable;
					for(Faction faction : this.plugin.getFactionManager().getFactions()){
						if(!(faction instanceof ClaimableFaction)) continue;
						ClaimableFaction claimableFaction = (ClaimableFaction) faction;
						claimableFaction.removeClaims(claimableFaction.getClaims(), Bukkit.getConsoleSender());
					}
					Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "All claims have been cleared" + ((conversable = context.getForWhom()) instanceof CommandSender ? new StringBuilder().append(" by ").append(((CommandSender) conversable).getName()).toString() : "") + '.');
					return Prompt.END_OF_CONVERSATION;
				}
				case "no":{
					context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of clearing all faction claims.");
					return Prompt.END_OF_CONVERSATION;
				}
			}
			context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of clearing all faction claims cancelled.");
			return Prompt.END_OF_CONVERSATION;
		}
	}

}

