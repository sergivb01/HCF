package idaniel84.command;

import idaniel84.faction.type.PlayerFaction;
import net.veilmc.base.BasePlugin;
import net.veilmc.base.user.BaseUser;
import idaniel84.HCF;
import idaniel84.faction.FactionManager;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpawnEntitiesCommand implements CommandExecutor, Listener {
    private static boolean entitiesOn = true;


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Invalid usage. Try using /spawnentities");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "Player entities are currently " + (entitiesOn ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.BLUE + " on spawn.");
            sender.sendMessage(ChatColor.BLUE + "Type /spawnentities " + (!entitiesOn ? "<true> to enable them." : "<false> to disable them."));
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("true")) {
                if (entitiesOn) {
                    sender.sendMessage(ChatColor.RED + "Player entitites are already enabled.");
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Player entities has been enabled on spawn.");
                entitiesOn = true;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    setEntitiesOn(player);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("false")) {
                if (!entitiesOn) {
                    sender.sendMessage(ChatColor.RED + "Player entitites are already disabled.");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Player entities has been disabled on spawn.");
                entitiesOn = false;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    setEntitiesOff(player);
                }
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Invalid usage. Try using /spawnentities");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerMoveOnSpawn(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (entitiesOn) {
            setEntitiesOn(player);
        }
        if (!entitiesOn) {
            setEntitiesOff(player);
        }
    }

    private void setEntitiesOn(Player player) {
        BaseUser baseUser = BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId());
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!baseUser.isVanished()) all.showPlayer(player);
        }
    }

    private void setEntitiesOff(Player player) {
        BaseUser baseUser = BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId());
        Faction factionAt;
        PlayerFaction playerFaction;
        FactionManager factionManager = HCF.getPlugin().getFactionManager();
        if ((factionAt = factionManager.getFactionAt(Bukkit.getPlayer(player.getName()).getLocation())).isSafezone() && ((playerFaction = factionManager.getPlayerFaction((Player) player)) == null || !factionAt.equals(playerFaction))) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                all.hidePlayer(player);
            }
        }
        else {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!baseUser.isVanished()) all.showPlayer(player);
            }
        }
    }
}
