package idaniel84.listener;

import com.google.common.primitives.Ints;
import net.veilmc.util.ItemBuilder;
import net.veilmc.util.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BookDeenchantListener
		implements Listener{
	private static final ItemStack EMPTY_BOOK = new ItemStack(Material.BOOK, 1);
	private static final String PERMISSION_1 = "hcf.deenchant.1";
	private static final String PERMISSION_2 = "hcf.deenchant.2";
	private static final Set<Inventory> tracked = new HashSet<Inventory>();

	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if(tracked.contains((Object) e.getInventory())){
			ParticleEffect.CRITICAL_HIT.display((Player) e.getPlayer(), e.getPlayer().getLocation().add(0.0, 1.0, 0.0), 15.0f, 10);
			e.getInventory().clear();
			tracked.remove((Object) e.getInventory());
		}
	}

	@EventHandler
	public void onClickBook(InventoryClickEvent e){
		if(tracked.contains((Object) e.getInventory())){
			e.setCancelled(true);
			String levels = ChatColor.stripColor((String) ((String) e.getCurrentItem().getItemMeta().getLore().get(0)).replace("To remove this enchant it will cost ", "").replace(" levels", ""));
			Integer level = Ints.tryParse((String) levels) == null ? 0 : Ints.tryParse((String) levels);
			if(((Player) e.getWhoClicked()).getLevel() < level){
				((Player) e.getWhoClicked()).sendMessage((Object) ChatColor.RED + "You do not have enough levels");
				e.setCancelled(true);
				return;
			}
			((Player) e.getWhoClicked()).setLevel(((Player) e.getWhoClicked()).getLevel() - level);
			e.setCancelled(true);
			for(Enchantment enchantment : e.getCurrentItem().getEnchantments().keySet()){
				e.getWhoClicked().getItemInHand().removeEnchantment(enchantment);
			}
			ParticleEffect.CRITICAL_HIT.display((Player) e.getWhoClicked(), e.getWhoClicked().getLocation().add(0.0, 1.0, 0.0), 15.0f, 10);
			e.getWhoClicked().closeInventory();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem()){
			Player player = event.getPlayer();
			if(event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && player.getGameMode() != GameMode.CREATIVE){
				ItemStack stack = event.getItem();
				if(stack != null && stack.getType() == Material.ENCHANTED_BOOK){
					ItemMeta meta = stack.getItemMeta();
					if(meta instanceof EnchantmentStorageMeta){
						EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
						for(Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()){
							enchantmentStorageMeta.removeStoredEnchant(enchantment);
						}
						event.setCancelled(true);
						player.setItemInHand(EMPTY_BOOK);
					}
				}else if(stack != null && stack.getItemMeta().hasEnchants()){
					if(event.getPlayer().hasPermission(PERMISSION_1) && !event.getPlayer().hasPermission(PERMISSION_2)){
						Random random = new Random();
						Integer randomNumber = random.nextInt(stack.getEnchantments().keySet().size()) + 1;
						Integer enchant = 0;
						for(Enchantment enchantment : stack.getEnchantments().keySet()){
							Integer n = enchant;
							Integer n2 = enchant = Integer.valueOf(enchant + 1);
							if(!enchant.equals(randomNumber)) continue;
							stack.removeEnchantment(enchantment);
						}
						short durability = stack.getDurability();
						durability = (short) (durability / 5);
						short afterMath = stack.getDurability();
						afterMath = (short) (afterMath - durability);
						stack.setDurability(afterMath);
						event.setCancelled(true);
						ParticleEffect.CRITICAL_HIT.display(event.getPlayer(), event.getClickedBlock().getLocation().add(0.0, 1.0, 0.0), 15.0f, 10);
					}else if(event.getPlayer().hasPermission(PERMISSION_2) && event.getPlayer().hasPermission(PERMISSION_1)){
						Inventory trackedInv = Bukkit.createInventory((InventoryHolder) event.getPlayer(), (int) 9, (String) ((Object) ChatColor.GREEN + "Item-DeEnchanter"));
						tracked.add(trackedInv);
						for(Enchantment enchantment : stack.getEnchantments().keySet()){
							trackedInv.addItem(new ItemStack[]{new ItemBuilder(stack.getType()).enchant(enchantment, stack.getEnchantmentLevel(enchantment)).lore(new String[]{(Object) ChatColor.GREEN + "To remove this enchant it will cost " + (Object) ChatColor.YELLOW + stack.getEnchantmentLevel(enchantment) * 5 + (Object) ChatColor.GREEN + " levels"}).build()});
						}
						event.getPlayer().openInventory(trackedInv);
					}
				}
			}
		}
	}
}

