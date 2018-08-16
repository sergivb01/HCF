package idaniel84.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NetherPortalPearlFixListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) && event.getPlayer() instanceof Player) {
            int fromX = event.getFrom().getBlockX();
            int toX = event.getTo().getBlockX();
            if (((toX - fromX) * (toX - fromX)) > 40000) {
                event.getPlayer().sendMessage(ChatColor.RED + "You were caught trying to pearl glitch with nether portals. If this is an issue, please report it to an admin.");
                if (event.getPlayer().getItemInHand().getType() == Material.ENDER_PEARL) {
                    event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() + 1);
                }
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all.hasPermission("rank.staff")) {
                        all.sendMessage(" ");
                        all.sendMessage(ChatColor.RED + event.getPlayer().getName() + " is trying to pearl glitch with nether portals.");
                        all.sendMessage(" ");
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
