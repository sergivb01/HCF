package idaniel84.timer.type;

import idaniel84.timer.PlayerTimer;
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

public class GoldenAppleTimer
		extends PlayerTimer
		implements Listener{
	public GoldenAppleTimer(JavaPlugin plugin){
		super(ChatColor.YELLOW + "Apple", TimeUnit.SECONDS.toMillis(5));
	}

	@Override
	public ChatColor getScoreboardPrefix(){
		return ChatColor.YELLOW;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerConsume(PlayerItemConsumeEvent event){
		ItemStack stack = event.getItem();
		if(stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 0){
			Player player = event.getPlayer();
			if(!(this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false))){
				event.setCancelled(true);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are still on &eGolden Apple &ccooldown"));
			}
		}
	}
}

