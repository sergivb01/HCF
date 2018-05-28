package net.veilmc.hcf.command;

import net.veilmc.base.BasePlugin;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerStats
		implements CommandExecutor{
	public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args){
		Player player = (Player) cs;
		if(args.length == 0){
			player.sendMessage(ChatColor.RED + "Usage: /" + s + " [player]");
			return true;
		}
		if(args.length == 1){
			if(Bukkit.getPlayer(args[0]) == null){
				if(Bukkit.getOfflinePlayer(args[0]) == null){
					player.sendMessage(ChatColor.GOLD + "Player named or with UUID '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found");
					return true;
				}
				this.sendInformation(player, Bukkit.getOfflinePlayer(args[0]));
				return true;
			}
			this.sendInformation(player, Bukkit.getPlayer(args[0]));
			return true;
		}
		return false;
	}


	public void sendInformation(Player player, OfflinePlayer target){
		FactionUser hcf = HCF.getPlugin().getUserManager().getUser(target.getUniqueId());
		final int targetLives = HCF.getPlugin().getDeathbanManager().getLives(target.getUniqueId());
		player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
		if(HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()) != null){
			player.sendMessage(HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getRelation(player).toChatColour() + target.getName());
			new Text(ChatColor.YELLOW + "  Faction: " + HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getDisplayName(player)).setHoverText(ChatColor.GRAY + "Click to view Faction").setClick(ClickAction.RUN_COMMAND, "/f who " + HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getName()).send(player);
		}else{
			player.sendMessage(ChatColor.RED + target.getName());
		}
		if(targetLives > 0){
			player.sendMessage(ChatColor.YELLOW + "  Available Lives: " + targetLives);
		}
		player.sendMessage(ChatColor.YELLOW + "  Playtime: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true));
		if(hcf.getDiamondsMined() > 0){
			player.sendMessage(ChatColor.YELLOW + "  Diamonds Mined: " + ChatColor.AQUA + hcf.getDiamondsMined());
		}
		if(hcf.getDeathban() != null){
			new Text(ChatColor.YELLOW + "  Deathbanned: " + (hcf.getDeathban().isActive() ? new StringBuilder().append(ChatColor.GREEN).append("true").toString() : new StringBuilder().append(ChatColor.RED).append("false").toString())).setHoverText(ChatColor.AQUA + "Un-Deathbanned at: " + HCF.getRemaining(hcf.getDeathban().getExpiryMillis(), true, true)).send(player);
		}else{
			if(!ConfigurationService.KIT_MAP || ConfigurationService.VEILZ || !ConfigurationService.FFA){
				player.sendMessage(ChatColor.YELLOW + "  Deathbanned: " + ChatColor.RED + "false");
			}
		}
		if(hcf.getKills() > 0){
			player.sendMessage(ChatColor.YELLOW + "  Kills: " + ChatColor.GREEN + player.getStatistic(Statistic.PLAYER_KILLS));
		}
		if(hcf.getDeaths() > 0){
			player.sendMessage(ChatColor.YELLOW + "  Deaths: " + ChatColor.RED + player.getStatistic(Statistic.DEATHS));
		}
		player.sendMessage(ChatColor.YELLOW + "  Balance: " + ChatColor.RED + HCF.getPlugin().getEconomyManager().getBalance(target.getUniqueId()));
		player.sendMessage(ChatColor.YELLOW + "  Profile: " + ChatColor.GRAY + "veilhcf.us/u/" + target.getName());
		player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
	}

}

