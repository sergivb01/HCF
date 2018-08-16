package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.faction.FactionMember;
import idaniel84.faction.struct.Role;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.JavaUtils;
import net.veilmc.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionWithdrawArgument
		extends CommandArgument{
	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
	private final HCF plugin;

	public FactionWithdrawArgument(HCF plugin){
		super("withdraw", "Withdraws money from the faction balance.", new String[]{"w"});
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <all|amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Integer amount;
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can update the faction balance.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player player = (Player) sender;
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		UUID uuid = player.getUniqueId();
		FactionMember factionMember = playerFaction.getMember(uuid);
		if(factionMember.getRole() == Role.MEMBER){
			sender.sendMessage(ChatColor.RED + "You must be a faction officer to withdraw money.");
			return true;
		}
		int factionBalance = playerFaction.getBalance();
		if(args[1].equalsIgnoreCase("all")){
			amount = factionBalance;
		}else{
			amount = Ints.tryParse(args[1]);
			if(amount == null){
				sender.sendMessage(ChatColor.RED + "Error: '" + args[1] + "' is not a valid number.");
				return true;
			}
		}
		if(amount <= 0){
			sender.sendMessage(ChatColor.RED + "Amount must be positive.");
			return true;
		}
		if(amount > factionBalance){
			sender.sendMessage(ChatColor.RED + "Your faction need at least " + '$' + JavaUtils.format(amount) + " to do this, whilst it only has " + '$' + JavaUtils.format(factionBalance) + '.');
			return true;
		}
		this.plugin.getEconomyManager().addBalance(uuid, amount);
		playerFaction.setBalance(factionBalance - amount);
		playerFaction.broadcast(ConfigurationService.TEAMMATE_COLOUR + factionMember.getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has withdrew " + ChatColor.BOLD + '$' + JavaUtils.format(amount) + ChatColor.YELLOW + " from the faction balance.");
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return args.length == 2 ? COMPLETIONS : Collections.emptyList();
	}
}

