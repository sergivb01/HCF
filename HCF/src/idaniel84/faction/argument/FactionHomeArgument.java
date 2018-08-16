package idaniel84.faction.argument;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.timer.PlayerTimer;
import idaniel84.HCF;
import idaniel84.faction.FactionExecutor;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.kothgame.faction.EventFaction;
import idaniel84.timer.PlayerTimer;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class FactionHomeArgument
		extends CommandArgument{
	private final FactionExecutor factionExecutor;
	private final HCF plugin;

	public FactionHomeArgument(FactionExecutor factionExecutor, HCF plugin){
		super("home", "Teleport to the faction home.");
		this.factionExecutor = factionExecutor;
		this.plugin = plugin;
	}

	public String getUsage(String label){
		return '/' + label + ' ' + getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		if((args.length >= 2) && (args[1].equalsIgnoreCase("set"))){
			this.factionExecutor.getArgument("sethome").onCommand(sender, command, label, args);
			return true;
		}
		UUID uuid = player.getUniqueId();
		PlayerTimer timer = this.plugin.getTimerManager().enderPearlTimer;
		long remaining = timer.getRemaining(player);
		if(remaining > 0L){
			sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
			return true;
		}
		if((remaining = (timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player)) > 0L){
			sender.sendMessage(ChatColor.RED + "You cannot warp whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
			return true;
		}
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
		if(playerFaction == null){
			sender.sendMessage(ChatColor.RED + "You are not in a faction.");
			return true;
		}
		Location home = playerFaction.getHome();
		if(home == null){
			sender.sendMessage(ChatColor.RED + "Your faction does not have a home set.");
			return true;
		}
		Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
		if((factionAt instanceof EventFaction)){
			sender.sendMessage(ChatColor.RED + "You cannot warp whilst in event zones.");
			return true;
		}
		long millis = 0L;
		if(factionAt.isSafezone()){
			millis = 0L;
		}else{
			switch(player.getWorld().getEnvironment()){
				case THE_END:
					sender.sendMessage(ChatColor.RED + "You cannot teleport to your faction home whilst in The End.");
					return true;
				case NETHER:
					millis = 30000L;
					break;
				default:
					millis = 10000L;
			}
		}
		if((!factionAt.equals(playerFaction)) && ((factionAt instanceof PlayerFaction))){
			millis *= 2L;
		}
		if(this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player.getUniqueId()) > 0L){
			player.sendMessage(ChatColor.RED + "You still have PvP Protection, you must enable it to teleport.");
			return true;
		}
		this.plugin.getTimerManager().teleportTimer.teleport(player, home, millis, ChatColor.YELLOW + "Sending you to your faction home in " + ChatColor.LIGHT_PURPLE + HCF.getRemaining(millis, true, false) + ChatColor.YELLOW + ". Do not move or take damage.", PlayerTeleportEvent.TeleportCause.COMMAND);
		return true;
	}
}
