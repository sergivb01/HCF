package idaniel84.classes.bard;

import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import idaniel84.HCF;
import idaniel84.classes.PvpClass;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.chat.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BardClass
		extends PvpClass
		implements Listener{
	public static final int HELD_EFFECT_DURATION_TICKS = 100;
	private static final long BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(8);
	private static final int TEAMMATE_NEARBY_RADIUS = 25;
	private static final long HELD_REAPPLY_TICKS = 20;
	private final Map<UUID, BardData> bardDataMap = new HashMap<UUID, BardData>();
	private final Map<Material, BardEffect> bardEffects = new EnumMap<Material, BardEffect>(Material.class);
	private final BardRestorer bardRestorer;
	private final HCF plugin;
	private final TObjectLongMap<UUID> msgCooldowns = new TObjectLongHashMap();

	public BardClass(HCF plugin){
		super("Bard", TimeUnit.SECONDS.toMillis(3L));
		this.plugin = plugin;
		this.bardRestorer = new BardRestorer(plugin);
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
		this.bardEffects.put(Material.SUGAR, new BardEffect(35, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 100, 1)));
		this.bardEffects.put(Material.BLAZE_POWDER, new BardEffect(50, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0)));
		this.bardEffects.put(Material.IRON_INGOT, new BardEffect(35, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
		this.bardEffects.put(Material.GHAST_TEAR, new BardEffect(30, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
		this.bardEffects.put(Material.FEATHER, new BardEffect(40, new PotionEffect(PotionEffectType.JUMP, 120, 7), new PotionEffect(PotionEffectType.JUMP, 100, 0)));
		this.bardEffects.put(Material.SPIDER_EYE, new BardEffect(45, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
		this.bardEffects.put(Material.MAGMA_CREAM, new BardEffect(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
	}

	@Override
	public boolean onEquip(final Player player){
		if(!super.onEquip(player)){
			return false;
		}
		BardData bardData = new BardData();
		this.bardDataMap.put(player.getUniqueId(), bardData);
		bardData.startEnergyTracking();

		bardData.heldTask = new BukkitRunnable(){
			int lastEnergy;

			public void run(){
				final ItemStack held = player.getItemInHand();
				if(held != null){
					final BardEffect bardEffect = BardClass.this.bardEffects.get(held.getType());
					if(bardEffect != null && !BardClass.this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()){
						final PlayerFaction playerFaction = BardClass.this.plugin.getFactionManager().getPlayerFaction(player);
						if(playerFaction != null){
							final Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0, 25.0, 25.0);
							for(final Entity nearby : nearbyEntities){
								if(nearby instanceof Player && !player.equals(nearby)){
									final Player target = (Player) nearby;
									if(!playerFaction.getMembers().containsKey(target.getUniqueId())){
										continue;
									}
									BardClass.this.bardRestorer.setRestoreEffect(target, bardEffect.heldable);
								}
							}
						}
					}
				}
				final int energy = (int) BardClass.this.getEnergy(player);
				if(energy != 0 && energy != this.lastEnergy && (energy % 10 == 0 || this.lastEnergy - energy - 1 > 0 || energy == 120.0)){
					this.lastEnergy = energy;
					player.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + BardClass.this.name + " Energy: " + ChatColor.AQUA.toString() + ChatColor.BOLD + energy);
				}
			}
		}.runTaskTimer(this.plugin, 0L, 20L);
		return true;
	}

	@Override
	public void onUnequip(Player player){
		super.onUnequip(player);
		this.clearBardData(player.getUniqueId());
	}

	private void clearBardData(UUID uuid){
		BardData bardData = this.bardDataMap.remove(uuid);
		if(bardData != null && bardData.heldTask != null){
			bardData.heldTask.cancel();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event){
		this.clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		this.clearBardData(event.getPlayer().getUniqueId());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onItemHeld(PlayerItemHeldEvent event){
		BardEffect bardEffect;
		Player player = event.getPlayer();
		PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
		if(equipped == null || !equipped.equals(this)){
			return;
		}
		UUID uuid = player.getUniqueId();
		long lastMessage = this.msgCooldowns.get(uuid);
		long millis = System.currentTimeMillis();
		if(lastMessage != this.msgCooldowns.getNoEntryValue() && lastMessage - millis > 0){
			return;
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(!event.hasItem()){
			return;
		}
		Action action = event.getAction();
		if(action == Action.RIGHT_CLICK_AIR || !event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK){
			ItemStack stack = event.getItem();
			BardEffect bardEffect = this.bardEffects.get(stack.getType());
			if(bardEffect == null || bardEffect.clickable == null){
				return;
			}
			event.setUseItemInHand(Event.Result.DENY);
			Player player = event.getPlayer();
			BardData bardData = this.bardDataMap.get(player.getUniqueId());
			if(bardData != null){
				if(!this.canUseBardEffect(player, bardData, bardEffect, true)){
					return;
				}
				if(stack.getAmount() > 1){
					stack.setAmount(stack.getAmount() - 1);
				}else{
					player.setItemInHand(new ItemStack(Material.AIR, 1));
				}
				if(!this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()){
					final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
					if(playerFaction != null && !bardEffect.clickable.getType().equals(PotionEffectType.WITHER)){
						final Collection<Entity> nearbyEntities = player.getNearbyEntities(25.0, 25.0, 25.0);
						for(final Entity nearby : nearbyEntities){
							if(nearby instanceof Player && !player.equals(nearby)){
								final Player target = (Player) nearby;
								if(!playerFaction.getMembers().containsKey(target.getUniqueId())){
									continue;
								}
								this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
								target.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + player.getName() + "&e has just given you &b" + Lang.fromPotionEffectType(bardEffect.clickable.getType()) + ' ' + (bardEffect.clickable.getAmplifier() + 1) + " Bard Buff."));
								target.playSound(target.getLocation(), Sound.LEVEL_UP, 10, 1);
							}
						}
					}
				}
				this.bardRestorer.setRestoreEffect(player, bardEffect.clickable);
				final double newEnergy = this.setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
				bardData.buffCooldown = System.currentTimeMillis() + BardClass.BUFF_COOLDOWN_MILLIS;
				player.sendMessage(ChatColor.YELLOW + "You have just used " + this.name + " buff " + ChatColor.AQUA + Lang.fromPotionEffectType(bardEffect.clickable.getType()) + ' ' + (bardEffect.clickable.getAmplifier() + 1) + ChatColor.YELLOW + " costing you " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.YELLOW + " energy. " + "Your energy is now " + ChatColor.GREEN + newEnergy * 10.0 / 10.0 + ChatColor.YELLOW + '.');
			}
		}
	}

	private boolean canUseBardEffect(Player player, BardData bardData, BardEffect bardEffect, boolean sendFeedback){
		long remaining;
		String errorFeedback = null;
		double currentEnergy = bardData.getEnergy();
		if((double) bardEffect.energyCost > currentEnergy){
			errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.RED + " energy to use this Bard buff, whilst you only have " + ChatColor.BOLD + currentEnergy + ChatColor.RED + '.';
		}
		if((remaining = bardData.getRemainingBuffDelay()) > 0){
			errorFeedback = ChatColor.RED + "You still have a cooldown on this " + ChatColor.GREEN + ChatColor.BOLD + "Bard" + ChatColor.RED + " buff for another " + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.';
		}
		if(this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()){
			errorFeedback = ChatColor.RED + "You may not use Bard buffs in safe-zones.";
		}
		if(sendFeedback && errorFeedback != null){
			player.sendMessage(errorFeedback);
		}
		return errorFeedback == null;
	}

	@Override
	public boolean isApplicableFor(Player player){
		ItemStack helmet = player.getInventory().getHelmet();
		if(helmet == null || helmet.getType() != Material.GOLD_HELMET){
			return false;
		}
		ItemStack chestplate = player.getInventory().getChestplate();
		if(chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE){
			return false;
		}
		ItemStack leggings = player.getInventory().getLeggings();
		if(leggings == null || leggings.getType() != Material.GOLD_LEGGINGS){
			return false;
		}
		ItemStack boots = player.getInventory().getBoots();
		return boots != null && boots.getType() == Material.GOLD_BOOTS;
	}


	public long getRemainingBuffDelay(Player player){
		Map<UUID, BardData> map = this.bardDataMap;
		synchronized(map){
			BardData bardData = this.bardDataMap.get(player.getUniqueId());
			return bardData == null ? 0 : bardData.getRemainingBuffDelay();
		}
	}


	public double getEnergy(Player player){
		Map<UUID, BardData> map = this.bardDataMap;
		synchronized(map){
			BardData bardData = this.bardDataMap.get(player.getUniqueId());
			return bardData == null ? 0.0 : bardData.getEnergy();
		}
	}


	public long getEnergyMillis(Player player){
		Map<UUID, BardData> map = this.bardDataMap;
		synchronized(map){
			BardData bardData = this.bardDataMap.get(player.getUniqueId());
			return bardData == null ? 0 : bardData.getEnergyMillis();
		}
	}

	public double setEnergy(Player player, double energy){
		BardData bardData = this.bardDataMap.get(player.getUniqueId());
		if(bardData == null){
			return 0.0;
		}
		bardData.setEnergy(energy);
		return bardData.getEnergy();
	}

}

