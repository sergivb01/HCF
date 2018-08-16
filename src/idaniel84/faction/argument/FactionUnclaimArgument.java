package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.HCF;
import idaniel84.faction.FactionMember;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import net.veilmc.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionUnclaimArgument
		extends CommandArgument{
	private static final ImmutableList<String> COMPLETIONS;
	private static final HashSet<String> stuff;

	static{
		stuff = new HashSet();
		COMPLETIONS = ImmutableList.of("all");
	}

	private final HCF plugin;

	public FactionUnclaimArgument(HCF plugin){
		super("unclaim", "Unclaims land from your faction.");
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " ";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can un-claim land from a faction.");
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		FactionMember factionMember = playerFaction.getMember(player);
		if(factionMember.getRole() != Role.LEADER){
			sender.sendMessage(ChatColor.RED + "You must be a faction leader to unclaim land.");
			return true;
		}
		Set<Claim> factionClaims = playerFaction.getClaims();
		if(factionClaims.isEmpty()){
			sender.sendMessage(ChatColor.RED + "Your faction does not own any claims.");
			return true;
		}
		if(args.length == 2){
			if(args[1].equalsIgnoreCase("yes") && stuff.contains(player.getName())){
				for(Claim claims : factionClaims){
					playerFaction.removeClaim(claims, player);
				}
				factionClaims.clear();
				return true;
			}
			if(args[1].equalsIgnoreCase("no") && stuff.contains(player.getName())){
				stuff.remove(player.getName());
				player.sendMessage(ChatColor.YELLOW + "You have been removed the unclaim-set.");
				return true;
			}
		}
		stuff.add(player.getName());
		new Text(ChatColor.YELLOW + "Do you want to unclaim " + ChatColor.BOLD + "all" + ChatColor.YELLOW + " of your land?").send(player);
		new Text(ChatColor.YELLOW + "If so, " + ChatColor.DARK_GREEN + "/f unclaim yes" + ChatColor.YELLOW + " otherwise do" + ChatColor.DARK_RED + " /f unclaim no" + ChatColor.GRAY + " (Click here to unclaim)").setHoverText(ChatColor.GOLD + "Click here to unclaim all").setClick(ClickAction.RUN_COMMAND, "/f unclaim yes").send(player);
		return true;
	}
}

