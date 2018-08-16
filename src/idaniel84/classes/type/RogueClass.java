package idaniel84.classes.type;

import idaniel84.HCF;
import idaniel84.classes.PvpClass;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RogueClass extends PvpClass implements Listener{
	private static final PotionEffect ARCHER_SPEED_EFFECT;
	private static final long ARCHER_SPEED_COOLDOWN_DELAY;

	static{
		ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 275, 5);
		ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.SECONDS.toMillis(45L);
	}

	private final HCF plugin;
	private final TObjectLongMap<UUID> archerSpeedCooldowns;
	private RougeRestorer rougeRestorer;

	public RogueClass(HCF plugin){
		super("Rouge", TimeUnit.SECONDS.toMillis(5));
		this.archerSpeedCooldowns = new TObjectLongHashMap<>();
		this.plugin = plugin;
		this.rougeRestorer = new RougeRestorer(plugin);
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			final Player damaged = (Player) event.getEntity();
			final Player damager = (Player) event.getDamager();
			if(damaged != damager && this.plugin.getPvpClassManager().getEquippedClass(damager) == this){
				final ItemStack itemInHand = damager.getItemInHand();
				if(itemInHand != null && itemInHand.getType() == Material.GOLD_SWORD && itemInHand.getEnchantments().isEmpty()){
					boolean cancelled = false;
					for(final PotionEffect activePotionEffects : damager.getActivePotionEffects()){
						if(activePotionEffects.getType().equals(PotionEffectType.SLOW)){
							cancelled = true;
						}
					}
					if(!cancelled){
						final Vector damagerDirection = damager.getLocation().getDirection();
						final Vector damagedDirection = damaged.getLocation().getDirection();
						if(damagerDirection.dot(damagedDirection) > 0.0){
							damaged.setHealth(damaged.getHealth() - 9.0);
							damager.setItemInHand(new ItemStack(Material.AIR));
							damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
							damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 2));
							damager.sendMessage("");
							damaged.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lYou have been &f&lBACKSTABBED &c&lby " + damager.getName()));
							damager.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have backstabbed &a" + damaged.getName() + "&e!"));
							damager.sendMessage("");
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onPlayerInteract(final PlayerInteractEvent event){
		final Action action = event.getAction();
		if((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem() && event.getItem().getType() == Material.SUGAR){
			if(this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this){
				return;
			}
			final Player player = event.getPlayer();
			final UUID uuid = player.getUniqueId();
			final long timestamp = this.archerSpeedCooldowns.get(uuid);
			final long millis = System.currentTimeMillis();
			final long remaining = (timestamp == this.archerSpeedCooldowns.getNoEntryValue()) ? -1L : (timestamp - millis);
			if(remaining > 0L){
				player.sendMessage(ChatColor.RED + "Cannot use " + this.getName() + " speed for another " + DurationFormatUtils.formatDurationWords(remaining, true, true) + ".");
			}else{
				final ItemStack stack = player.getItemInHand();
				if(stack.getAmount() == 1){
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}else{
					stack.setAmount(stack.getAmount() - 1);
				}
				rougeRestorer.setRestoreEffect(player, RogueClass.ARCHER_SPEED_EFFECT);
				player.sendMessage(ChatColor.YELLOW + "You now have your " + ChatColor.GREEN + "Rouge Speed" + ChatColor.YELLOW + "!");
				this.archerSpeedCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + RogueClass.ARCHER_SPEED_COOLDOWN_DELAY);
			}
		}
	}

	@Override
	public boolean isApplicableFor(Player player){
		PlayerInventory playerInventory = player.getInventory();
		ItemStack helmet = playerInventory.getHelmet();
		if(helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET){
			return false;
		}
		ItemStack chestplate = playerInventory.getChestplate();
		if(chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE){
			return false;
		}
		ItemStack leggings = playerInventory.getLeggings();
		if(leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS){
			return false;
		}
		ItemStack boots = playerInventory.getBoots();
		return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
	}

}