package idaniel84.faction.argument.staff;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import idaniel84.HCF;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.type.ClaimableFaction;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.utils.ConfigurationService;
import net.minecraft.util.com.google.common.collect.Lists;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionClaimForArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionClaimForArgument(HCF plugin){
		super("claimfor", "Claims land for another faction.");
		this.plugin = plugin;
		this.permission = "hcf.command.faction.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <factionName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Faction targetFaction = this.plugin.getFactionManager().getFaction(args[1]);
		if(!(targetFaction instanceof ClaimableFaction)){
			sender.sendMessage(ChatColor.RED + "Claimable faction named " + args[1] + " not found.");
			return true;
		}
		Player player = (Player) sender;
		WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
		if(worldEditPlugin == null){
			sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set claim areas.");
			return true;
		}
		Selection selection = worldEditPlugin.getSelection(player);
		if(selection == null){
			sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
			return true;
		}
		if (!ConfigurationService.KIT_MAP && args[1].equalsIgnoreCase("ffa")) {
			player.sendMessage(ChatColor.RED + "You can only claim this faction on KitMap.");
			return true;
		}
		ClaimableFaction claimableFaction = (ClaimableFaction) targetFaction;
		Claim claim = new Claim(claimableFaction, selection.getMinimumPoint(), selection
				.getMaximumPoint());
		if(!(claimableFaction instanceof PlayerFaction)){
			for(Claim prev : Lists.newArrayList(claimableFaction.getClaims())){
				if(prev.getWorld() == claim.getWorld()){
					claimableFaction.removeClaim(prev, null);
				}
			}
		}
		if(claimableFaction.addClaim(claim, sender)){
			sender.sendMessage(ChatColor.YELLOW + "Successfully claimed this land for " + ChatColor.RED + targetFaction
					.getName() + ChatColor.YELLOW + '.');
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
}

