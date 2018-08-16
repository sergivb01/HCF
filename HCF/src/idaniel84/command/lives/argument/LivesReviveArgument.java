package idaniel84.command.lives.argument;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.HCF;
import idaniel84.deathban.Deathban;
import idaniel84.faction.struct.Relation;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.user.FactionUser;
import idaniel84.utils.ConfigurationService;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class LivesReviveArgument
		extends CommandArgument{
	private static final String REVIVE_BYPASS_PERMISSION = "hcf.revive.bypass";
	private static final String REVIVE_DTR = "hcf.revive.dtr";
	private static final String PROXY_CHANNEL_NAME = "BungeeCord";
	private final HCF plugin;

	public LivesReviveArgument(HCF plugin){
		super("revive", "Revive a death-banned player");
		this.plugin = plugin;
		this.permission = "hcf.command.lives.argument." + this.getName();
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
		if(!target.hasPlayedBefore() && !target.isOnline()){
			sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
			return true;
		}
		UUID targetUUID = target.getUniqueId();
		FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
		Deathban deathban = factionTarget.getDeathban();
		if(deathban == null || !deathban.isActive()){
			sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
			return true;
		}
		Relation relation = Relation.ENEMY;
		if(sender instanceof Player){
			if(!sender.hasPermission("hcf.revive.bypass") && this.plugin.getEotwHandler().isEndOfTheWorld()){
				sender.sendMessage(ChatColor.RED + "You cannot revive players during EOTW.");
				return true;
			}
			if(!sender.hasPermission("hcf.revive.bypass")){
				Player player = (Player) sender;
				UUID playerUUID = player.getUniqueId();
				int selfLives = this.plugin.getDeathbanManager().getLives(playerUUID);
				if(selfLives <= 0){
					sender.sendMessage(ChatColor.RED + "You do not have any lives.");
					return true;
				}
				this.plugin.getDeathbanManager().setLives(playerUUID, selfLives - 1);
				PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
				relation = playerFaction == null ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID));
				sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
			}else if(sender.hasPermission("hcf.revive.dtr")){
				if(this.plugin.getFactionManager().getPlayerFaction(targetUUID) != null){
					this.plugin.getFactionManager().getPlayerFaction(targetUUID).setDeathsUntilRaidable(this.plugin.getFactionManager().getPlayerFaction(targetUUID).getDeathsUntilRaidable() + 1.0);
				}
				sender.sendMessage(ChatColor.YELLOW + "You have revived and added DTR to " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
			}else{
				sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
			}
		}else{
			sender.sendMessage(ChatColor.YELLOW + "You have revived " + ConfigurationService.ENEMY_COLOUR + target.getName() + ChatColor.YELLOW + '.');
		}
		factionTarget.removeDeathban();
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length != 2){
			return Collections.emptyList();
		}
		ArrayList<String> results = new ArrayList<String>();
		Collection<FactionUser> factionUsers = this.plugin.getUserManager().getUsers().values();
		for(FactionUser factionUser : factionUsers){
			String offlineName;
			OfflinePlayer offlinePlayer;
			Deathban deathban = factionUser.getDeathban();
			if(deathban == null || !deathban.isActive() || (offlineName = (offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID())).getName()) == null)
				continue;
			results.add(offlinePlayer.getName());
		}
		return results;
	}
}

