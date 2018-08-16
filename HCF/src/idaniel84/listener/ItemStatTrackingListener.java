package idaniel84.listener;

import com.google.common.base.Preconditions;
import net.minecraft.server.v1_7_R4.EntityLiving;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStatTrackingListener
		implements Listener{
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event){
		ItemStack stack;
		Player player = event.getEntity();
		Player killer = player.getKiller();
		if(killer != null && (stack = killer.getItemInHand()) != null && EnchantmentTarget.WEAPON.includes(stack)){
			this.addDeathLore(stack, player, killer);
		}
	}

	private CraftEntity getKiller(PlayerDeathEvent event){
		EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle().aX();
		return lastAttacker == null ? null : lastAttacker.getBukkitEntity();
	}

	private String getEntityName(org.bukkit.entity.Entity entity){
		Preconditions.checkNotNull((Object) entity, (Object) "Entity cannot be null");
		return entity instanceof Player ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
	}

	private String getDisplayName(org.bukkit.entity.Entity entity){
		Preconditions.checkNotNull((Object) entity, (Object) "Entity cannot be null");
		if(entity instanceof Player){
			Player player = (Player) entity;
			return player.getName();
		}
		return WordUtils.capitalizeFully((String) entity.getType().name().replace('_', ' '));
	}

	private void addDeathLore(ItemStack stack, Player player, Player killer){
		List lore;
		ItemMeta meta = stack.getItemMeta();
		List list = lore = meta.hasLore() ? meta.getLore() : new ArrayList(2);
		if(lore.isEmpty() || lore.size() <= 1 || !((String) lore.get(1)).startsWith((Object) ChatColor.GOLD + "Kills ")){
			lore.add(0, ChatColor.WHITE + "");
			lore.add(1, (Object) ChatColor.GOLD + "Kills " + (Object) ChatColor.WHITE + 1);
			lore.add(2, ChatColor.WHITE + "");
			lore.add(3, (Object) ChatColor.YELLOW + this.getEntityName(killer) + ChatColor.WHITE + " killed " + ChatColor.YELLOW + this.getDisplayName(player) + (Object) ChatColor.WHITE + ".");

		}else{
			String killsString = ((String) lore.get(1)).replace((Object) ChatColor.GOLD + "Kills ", "").replace((Object) ChatColor.WHITE + "]", "");
			Integer kills = 1;
			try{
				kills = Integer.parseInt(ChatColor.stripColor(killsString));
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
			Integer killafteradd = kills + 1;
			lore.set(1, (Object) ChatColor.GOLD + "Kills " + (Object) ChatColor.WHITE + killafteradd);
			lore.add((Object) ChatColor.YELLOW + this.getDisplayName(killer) + ChatColor.WHITE + " killed " + ChatColor.YELLOW + this.getDisplayName(player) + (Object) ChatColor.WHITE + ".");

		}
		meta.setLore(lore.subList(0, Math.min(6, lore.size())));
		stack.setItemMeta(meta);
	}
}