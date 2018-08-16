package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.faction.struct.Relation;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import net.veilmc.util.command.CommandArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionInviteArgument
		extends CommandArgument{
	private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
	private final HCF plugin;

	public FactionInviteArgument(HCF plugin){
		super("invite", "Invite a player to the faction.");
		this.plugin = plugin;
		this.aliases = new String[]{"inv", "invitemember", "inviteplayer"};
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can invite to a faction.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		if(!USERNAME_REGEX.matcher(args[1]).matches()){
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is an invalid username.");
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		if(playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER){
			sender.sendMessage(ChatColor.RED + "You must a faction officer to invite members.");
			return true;
		}
		Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
		String name = args[1];
		if(playerFaction.getMember(name) != null){
			sender.sendMessage(ChatColor.RED + "'" + name + "' is already in your faction.");
			return true;
		}
		if(!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.isRaidable()){
			sender.sendMessage(ChatColor.RED + "You may not invite players whilst your faction is raidable.");
			return true;
		}
		if(!invitedPlayerNames.add(name)){
			sender.sendMessage(ChatColor.RED + name + " has already been invited.");
			return true;
		}
		Player target = Bukkit.getPlayer(name);
		if(target != null){
			name = target.getName();
			Text text = new Text(sender.getName()).setColor(Relation.ENEMY.toChatColour()).append(new Text(" has invited you to join ").setColor(ChatColor.YELLOW));
			text.append(new Text(playerFaction.getName()).setColor(Relation.ENEMY.toChatColour())).append(new Text(". ").setColor(ChatColor.YELLOW));
			text.append(new Text("Click here").setColor(ChatColor.GREEN).setClick(ClickAction.RUN_COMMAND, "" + '/' + label + " accept " + playerFaction.getName()).setHoverText(ChatColor.AQUA + "Click to join " + playerFaction.getDisplayName(target) + ChatColor.AQUA + '.')).append(new Text(" to accept this invitation.").setColor(ChatColor.YELLOW));
			text.send(target);
		}
		playerFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has invited " + Relation.ENEMY.toChatColour() + name + ChatColor.YELLOW + " to the faction.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER){
			return Collections.emptyList();
		}
		ArrayList<String> results = new ArrayList<String>();
		for(Player target : Bukkit.getOnlinePlayers()){
			PlayerFaction targetFaction;
			if(!player.canSee(target) || results.contains(target.getName()) || (targetFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId())) != null && targetFaction.equals(playerFaction))
				continue;
			results.add(target.getName());
		}
		return results;
	}
}

