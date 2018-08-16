package idaniel84.classes.type;

import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.HCF;
import idaniel84.classes.PvpClass;
import idaniel84.classes.event.PvpClassEquipEvent;
import net.veilmc.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MinerClass
		extends PvpClass
		implements Listener{
	private static final int INVISIBILITY_HEIGHT_LEVEL = 20;
	private static final PotionEffect HEIGHT_INVISIBILITY;
	public static ArrayList<String> miners = new ArrayList();

	static{
		HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
	}

	private final HCF plugin;

	public MinerClass(final HCF plugin){
		super("Miner", TimeUnit.SECONDS.toMillis(5));
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
	}

	private void removeInvisibilitySafely(final Player player){
		for(final PotionEffect active : player.getActivePotionEffects()){
			if(active.getType().equals(PotionEffectType.INVISIBILITY) && active.getDuration() > MinerClass.DEFAULT_MAX_DURATION){
				player.sendMessage(ChatColor.RED + this.getName() + ChatColor.GOLD + " invisibility removed.");
				player.removePotionEffect(active.getType());
				break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(final EntityDamageByEntityEvent event){
		final Entity entity = event.getEntity();
		if(entity instanceof Player && BukkitUtils.getFinalAttacker(event, false) != null){
			final Player player = (Player) entity;
			if(this.plugin.getPvpClassManager().hasClassEquipped(player, this)){
				this.removeInvisibilitySafely(player);
			}
		}
	}

	@Override
	public void onUnequip(final Player player){
		super.onUnequip(player);
		this.removeInvisibilitySafely(player);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(final PlayerMoveEvent event){
		Location from = event.getFrom();
		int toX = event.getTo().getBlockX();
		int toY = event.getTo().getBlockY();
		int toZ = event.getTo().getBlockZ();
		if(from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ){
			this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(final PlayerTeleportEvent event){
		this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClassEquip(final PvpClassEquipEvent event){
		final Player player = event.getPlayer();
		if(event.getPvpClass() == this && player.getLocation().getBlockY() <= 30){
			player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
			player.sendMessage(ChatColor.AQUA + this.getName() + ChatColor.GREEN + " invisibility added.");
		}
	}

	private void conformMinerInvisibility(final Player player, final Location from, final Location to){
		final int fromY = from.getBlockY();
		final int toY = to.getBlockY();
		if(fromY != toY && this.plugin.getPvpClassManager().hasClassEquipped(player, this)){
			final boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
			if(toY > 30){
				if(fromY <= 30 && isInvisible){
					this.removeInvisibilitySafely(player);
				}
			}else if(!isInvisible){
				player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
				player.sendMessage(ChatColor.AQUA + this.getName() + ChatColor.GREEN + " invisibility added.");
			}
		}
	}

	@Override
	public boolean isApplicableFor(final Player player){
		final PlayerInventory playerInventory = player.getInventory();
		final ItemStack helmet = playerInventory.getHelmet();
		if(helmet == null || helmet.getType() != Material.IRON_HELMET){
			return false;
		}
		final ItemStack chestplate = playerInventory.getChestplate();
		if(chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE){
			return false;
		}
		final ItemStack leggings = playerInventory.getLeggings();
		if(leggings == null || leggings.getType() != Material.IRON_LEGGINGS){
			return false;
		}
		final ItemStack boots = playerInventory.getBoots();
		return boots != null && boots.getType() == Material.IRON_BOOTS;
	}
}