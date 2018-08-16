package idaniel84.faction.argument;

import com.google.common.base.Enums;
import idaniel84.HCF;
import idaniel84.faction.LandMap;
import idaniel84.user.FactionUser;
import idaniel84.visualise.VisualType;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FactionMapArgument
		extends CommandArgument{
	private final HCF plugin;

	public FactionMapArgument(HCF plugin){
		super("map", "View all claims around your chunk.");
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " [factionName]";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		boolean newShowingMap;
		VisualType visualType;
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		FactionUser factionUser = this.plugin.getUserManager().getUser(uuid);
		if(args.length <= 1){
			visualType = VisualType.CLAIM_MAP;
		}else{
			visualType = (VisualType) Enums.getIfPresent((Class) VisualType.class, args[1]).orNull();
			if(visualType == null){
				player.sendMessage(ChatColor.RED + "Visual type " + args[1] + " not found.");
				return true;
			}
		}
		boolean bl = newShowingMap = !factionUser.isShowClaimMap();
		if(newShowingMap){
			if(!LandMap.updateMap(player, this.plugin, visualType, true)){
				return true;
			}
		}else{
			this.plugin.getVisualiseHandler().clearVisualBlocks(player, visualType, null);
			sender.sendMessage(ChatColor.RED + "Claim pillars are no longer shown.");
		}
		factionUser.setShowClaimMap(newShowingMap);
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2 || !(sender instanceof Player)){
			return Collections.emptyList();
		}
		VisualType[] values = VisualType.values();
		ArrayList<String> results = new ArrayList<String>(values.length);
		for(VisualType visualType : values){
			results.add(visualType.name());
		}
		return results;
	}
}

