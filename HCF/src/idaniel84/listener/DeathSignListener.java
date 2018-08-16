package idaniel84.listener;

import com.google.common.collect.Lists;
import idaniel84.utils.DateTimeFormats;
import idaniel84.HCF;
import idaniel84.utils.DateTimeFormats;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class DeathSignListener
		implements Listener{
	private static final String DEATH_SIGN_ITEM_NAME = ChatColor.GOLD + "Death Sign";

	public DeathSignListener(HCF plugin){
		if(!plugin.getConfig().getBoolean("death-signs", true)){
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
						HandlerList.unregisterAll(this);
					}
					, 5);
		}
	}

	public static ItemStack getDeathSign(String playerName, String killerName){
		ItemStack stack = new ItemStack(Material.SIGN, 1);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(DEATH_SIGN_ITEM_NAME);
		meta.setLore((List) Lists.newArrayList((Object[]) new String[]{ChatColor.GREEN + playerName, ChatColor.GRAY + "slain by", ChatColor.GREEN + killerName, ChatColor.GRAY + DateTimeFormats.DAY_MTH_HR_MIN.format(System.currentTimeMillis())}));
		stack.setItemMeta(meta);
		return stack;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event){
		if(this.isDeathSign(event.getBlock())){
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		if(this.isDeathSign(block)){
			BlockState state = block.getState();
			Sign sign = (Sign) state;
			ItemStack stack = new ItemStack(Material.SIGN, 1);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(DEATH_SIGN_ITEM_NAME);
			meta.setLore(Arrays.asList(sign.getLines()));
			stack.setItemMeta(meta);
			Player player = event.getPlayer();
			World world = player.getWorld();
			if(player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")){
				world.dropItemNaturally(block.getLocation(), stack);
			}
			event.setCancelled(true);
			block.setType(Material.AIR);
			state.update();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event){
		final ItemStack stack = event.getItemInHand();
		final BlockState state = event.getBlock().getState();
		if(state instanceof Sign && stack.hasItemMeta()){
			final ItemMeta meta = stack.getItemMeta();
			if(meta.hasDisplayName() && meta.getDisplayName().equals(DeathSignListener.DEATH_SIGN_ITEM_NAME)){
				final Sign sign = (Sign) state;
				final List<String> lore = meta.getLore();
				int count = 0;
				for(final String loreLine : lore){
					sign.setLine(count++, loreLine);
					if(count == 4){
						break;
					}
				}
				sign.update();
				//sign.setEditible(false);
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();
		Player killer = player.getKiller();
		if(killer == null || !killer.equals(player) & true){
			// empty if block
		}
	}

	private boolean isDeathSign(Block block){
		BlockState state = block.getState();
		if(state instanceof Sign){
			String[] lines = ((Sign) state).getLines();
			return lines.length > 0 && lines[1] != null && lines[1].equals(ChatColor.WHITE + "slain by");
		}
		return false;
	}
}

