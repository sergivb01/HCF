package idaniel84.timer.type;

import idaniel84.timer.PlayerTimer;
import idaniel84.HCF;
import idaniel84.timer.PlayerTimer;
import idaniel84.utils.ConfigurationService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class NotchAppleTimer
		extends PlayerTimer
		implements Listener{
	public NotchAppleTimer(JavaPlugin plugin){
		super(ConfigurationService.NOTCH_APPLE_TIMER, ConfigurationService.KIT_MAP ? TimeUnit.MINUTES.toMillis(30L) : TimeUnit.HOURS.toMillis(4));
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.NOTCH_APPLE_COLOUR;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerConsume(PlayerItemConsumeEvent event){
		ItemStack stack = event.getItem();
		if(stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1){
			Player player = event.getPlayer();
			if(this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false)){
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588\u2588&c\u2588\u2588\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588&e\u2588&c\u2588\u2588\u2588\u2588 &6&l " + this.name + ": "));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588 &7  Consumed"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&f\u2588&6\u2588&6\u2588\u2588&c\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588&f\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &6 Cooldown Remaining:"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588 &7  " + HCF.getRemaining(this.defaultCooldown, true, true)));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588&6\u2588\u2588&6\u2588&6\u2588&6\u2588\u2588&c\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588&6\u2588\u2588\u2588\u2588&c\u2588\u2588"));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c\u2588\u2588\u2588\u2588\u2588&c\u2588\u2588\u2588"));
			}else{
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You still have a " + this.getDisplayName() + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + '.');
			}
		}
	}
}

