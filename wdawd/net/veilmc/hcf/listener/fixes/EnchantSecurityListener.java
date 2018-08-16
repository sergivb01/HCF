package net.veilmc.hcf.listener.fixes;

import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantSecurityListener
		implements Listener{
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onAttack(EntityDamageByEntityEvent event){
		final Entity attackerEntity = event.getDamager();
		if(attackerEntity instanceof Player && BukkitUtils.getFinalAttacker(event, false) != null){
			final Player attacker = (Player) attackerEntity;
			final ItemStack item = attacker.getItemInHand();
			if(event.getEntityType().equals(EntityType.PLAYER)){
				final Player defender = (Player) event.getEntity();
				if((item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) > 5) || (item.getEnchantmentLevel(Enchantment.KNOCKBACK) > 5) || (item.getEnchantmentLevel(Enchantment.THORNS) > 5)){
					attacker.getInventory().removeItem(attacker.getInventory().getItemInHand());
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ban " + attacker.getName() + " Illegal Enchants S:" + item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + " T:" + item.getEnchantmentLevel(Enchantment.THORNS) + " K:" + item.getEnchantmentLevel(Enchantment.KNOCKBACK) + " D:" + defender.getName());
					event.setCancelled(true);

				}
			}
		}
	}
}