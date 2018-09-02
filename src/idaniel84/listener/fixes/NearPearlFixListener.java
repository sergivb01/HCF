package idaniel84.listener.fixes;

import idaniel84.HCF;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class NearPearlFixListener implements Listener {

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack.getType() == Material.ENDER_PEARL && event.getClickedBlock() != null && event.isCancelled()) {
            if (HCF.getPlugin().getTimerManager().enderPearlTimer.hasCooldown(player)) {
                player.sendMessage(ChatColor.RED + "You cannot use" + ChatColor.YELLOW + " Enderpearl " + ChatColor.RED + "for another " + ChatColor.BOLD + HCF.getRemaining(HCF.getPlugin().getTimerManager().enderPearlTimer.getRemaining(player), true, false) + ChatColor.RED + '.');
                return;
            }
            player.launchProjectile(EnderPearl.class);
            if (itemStack.getAmount() == 1) {
                player.setItemInHand(new ItemStack(Material.AIR));
                return;
            }
            itemStack.setAmount(itemStack.getAmount() - 1);
        }
    }
}
