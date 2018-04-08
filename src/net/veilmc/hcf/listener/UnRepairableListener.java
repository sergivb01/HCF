package net.veilmc.hcf.listener;

import me.sergivb01.event.PrepareAnvilRepairEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class UnRepairableListener
		implements Listener{
	@EventHandler
	public void onRepair(PrepareAnvilRepairEvent e){
		for(ItemStack itemStack : e.getInventory().getContents()){
			if(itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()){
				continue;
			}

			for(String lore : itemStack.getItemMeta().getLore()){
				String fixedLore = ChatColor.stripColor((String) lore.toLowerCase());
				if(!fixedLore.contains("no repair") && !fixedLore.contains("unrepairable") && !fixedLore.contains("norepair") && !fixedLore.contains("nofix") && !fixedLore.contains("no fix"))
					continue;
				e.setCancelled(true);
				e.setResult(new ItemStack(Material.AIR));
				e.getRepairer().closeInventory();
				((Player) e.getRepairer()).sendMessage((Object) ChatColor.RED + "This item cannot be repaired.");
			}
		}
	}
}

