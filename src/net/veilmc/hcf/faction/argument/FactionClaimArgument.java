package net.veilmc.hcf.faction.argument;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.claim.ClaimHandler;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FactionClaimArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionClaimArgument(HCF plugin){
		super("claim", "Claim land in the Wilderness.", new String[]{"claimland"});
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
		UUID uuid = player.getUniqueId();
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		if(playerFaction.isRaidable()){
			sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
			return true;
		}
		PlayerInventory inventory = player.getInventory();
		if(inventory.contains(ClaimHandler.CLAIM_WAND)){
			sender.sendMessage(ChatColor.RED + "You already have a claiming wand in your inventory.");
			return true;
		}
		if(!inventory.addItem(new ItemStack[]{ClaimHandler.CLAIM_WAND}).isEmpty()){
			sender.sendMessage(ChatColor.RED + "Your inventory is full.");
			return true;
		}
		sender.sendMessage(ChatColor.YELLOW + "You have recieved a" + ChatColor.LIGHT_PURPLE + " claiming wand" + ChatColor.YELLOW + ". Read the item to understand how to claim. You can also" + ChatColor.YELLOW + " use " + ChatColor.RED + '/' + label + " claimchunk" + ChatColor.YELLOW + '.');
		return true;
	}
}

