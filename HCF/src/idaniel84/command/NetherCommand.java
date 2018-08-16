package idaniel84.command;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.timer.PlayerTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.TimeUnit;

public class NetherCommand
		implements CommandExecutor{

	private final HCF plugin;

	public NetherCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player s = (Player) sender;
		Location netherSpawn = Bukkit.getWorld("world_nether").getSpawnLocation();
		if(((Player) sender).getGameMode().equals(GameMode.CREATIVE)){
			s.teleport(netherSpawn);
			return true;
		}
		PlayerFaction playerFaction;
		Location location = s.getLocation();
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
		if(!(factionAt.isSafezone() || (playerFaction = this.plugin.getFactionManager().getPlayerFaction(s)) != null && playerFaction.equals(factionAt))){
			s.sendMessage(ChatColor.RED + "You can only teleport to the nether when in safe-zones or your own claim.");
			return false;
		}
		PlayerTimer sptimer = this.plugin.getTimerManager().spawnTagTimer;
		long sremaining = sptimer.getRemaining(s);
		if((sremaining = (sptimer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(s)) > 0L){
			s.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
			return false;
		}
		PlayerTimer etimer = this.plugin.getTimerManager().enderPearlTimer;
		long eremaining = etimer.getRemaining(s);
		if((eremaining = (sptimer = this.plugin.getTimerManager().enderPearlTimer).getRemaining(s)) > 0L){
			s.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Enderpearl Timer" + ChatColor.RED + " is active.");
			return false;
		}
		this.plugin.getTimerManager().teleportTimer.teleport(s, netherSpawn, TimeUnit.SECONDS.toMillis(30L), ChatColor.YELLOW + "Teleporting to nether spawn in " + ChatColor.LIGHT_PURPLE + "30 seconds.", PlayerTeleportEvent.TeleportCause.COMMAND);
		return true;
	}
}