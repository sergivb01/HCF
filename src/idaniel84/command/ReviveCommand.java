package idaniel84.command;

import idaniel84.HCF;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.Cooldowns;
import idaniel84.deathban.Deathban;
import idaniel84.user.FactionUser;
import net.veilmc.util.BukkitUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ReviveCommand implements CommandExecutor{
	private final HCF plugin;

	public ReviveCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player p = (Player) sender;
		if(ConfigurationService.KIT_MAP){
			sender.sendMessage(ChatColor.RED + "There is no need for this command on VeilMC kitmap.");
			return false;
		}
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
			return true;
		}
		if(Cooldowns.isOnCooldown("revive_cooldown", p)){
			p.sendMessage("§cYou cannot do this for another §l" + Cooldowns.getCooldownForPlayerInt("revive_cooldown", p) / 60 + " §cminutes.");
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if((!target.hasPlayedBefore()) && (!target.isOnline())){
			sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
			return true;
		}
		UUID targetUUID = target.getUniqueId();
		FactionUser factionTarget = HCF.getPlugin().getUserManager().getUser(targetUUID);
		Deathban deathban = factionTarget.getDeathban();
		if((deathban == null) || (!deathban.isActive())){
			sender.sendMessage(ChatColor.RED + target.getName() + " is not deathbanned.");
			return true;
		}

		factionTarget.removeDeathban();
		Bukkit.broadcastMessage(" ");
		if (sender.hasPermission("veil.revive")) Bukkit.broadcastMessage("§f§l* Revive * §9§l" + sender.getName() + " §bhas revived §9§l" + target.getName() + " §busing their §3§lVeil §brank");
		else if (sender.hasPermission("medic.revive")) Bukkit.broadcastMessage("§f§l* Revive * §9§l" + sender.getName() + " §bhas revived §9§l" + target.getName() + " §busing their §a§lMedic §brank");
		else if (sender.hasPermission("keeper.revive")) Bukkit.broadcastMessage("§f§l* Revive * §9§l" + sender.getName() + " §bhas revived §9§l" + target.getName() + " §busing their §6§lKeeper §brank");
		else Bukkit.broadcastMessage("§f§l* Revive * §9§l" + sender.getName() + " §bhas revived §9§l" + target.getName() + " §busing their §3§lPlatinum §brank");
		Bukkit.broadcastMessage(ConfigurationService.REVIVE_MESSAGE);
		Bukkit.broadcastMessage(" ");
		Cooldowns.addCooldown("revive_cooldown", p, 1800);

		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 1){
			return Collections.emptyList();
		}
		List<String> results = new ArrayList();
		for(FactionUser factionUser : this.plugin.getUserManager().getUsers().values()){
			Deathban deathban = factionUser.getDeathban();
			if((deathban != null) && (deathban.isActive())){
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
				String name = offlinePlayer.getName();
				if(name != null){
					results.add(name);
				}
			}
		}
		return BukkitUtils.getCompletions(args, results);
	}
}


