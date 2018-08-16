package idaniel84.classes.archer;

import idaniel84.HCF;
import idaniel84.classes.PvpClass;
import idaniel84.utils.Cooldowns;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArcherClass
		extends PvpClass
		implements Listener{
	public static final HashMap<UUID, UUID> tagged = new HashMap();
	private static final HashMap<UUID, Long> ARCHER_COOLDOWN = new HashMap();
	private static final PotionEffect ARCHER_CRITICAL_EFFECT = new PotionEffect(PotionEffectType.POISON, 60, 0);
	private static final PotionEffect ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
	private static final long ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
	private static final int MARK_TIMEOUT_SECONDS = 10;
	private static final int MARK_EXECUTION_LEVEL = 3;
	private static final float MINIMUM_FORCE = 0.5F;
	private static final String ARROW_FORCE_METADATA = "ARROW_FORCE";
	private static PotionEffect ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.JUMP, 160, 3);
	private static long ARCHER_JUMP_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
	private final HCF plugin;

	public ArcherClass(HCF plugin){
		super("Archer", TimeUnit.SECONDS.toMillis(3L));
		this.plugin = plugin;
		this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
		this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityShootBow(EntityShootBowEvent event){
		Entity projectile = event.getProjectile();
		if((projectile instanceof Arrow)){
			projectile.setMetadata("ARROW_FORCE", new FixedMetadataValue(this.plugin, Float.valueOf(event.getForce())));
		}
	}

	@EventHandler
	public void onPlayerClickSugar(PlayerInteractEvent e){
		final Player p = e.getPlayer();
		if((this.plugin.getPvpClassManager().getEquippedClass(p) != null) && (this.plugin.getPvpClassManager().getEquippedClass(p).equals(this)) &&
				(p.getItemInHand().getType() == Material.SUGAR)){
			if(Cooldowns.isOnCooldown("Archer_item_cooldown", p)){
				p.sendMessage(ChatColor.RED + "You cannot use this for another §l" + Cooldowns.getCooldownForPlayerInt("Archer_item_cooldown", p) + ChatColor.RED.toString() + " seconds!");
				e.setCancelled(true);
				return;
			}
			Cooldowns.addCooldown("Archer_item_cooldown", p, 25);
			p.sendMessage(ChatColor.RED.toString() + "§cSpeed 4 now activated.");
			if(p.getItemInHand().getAmount() == 1){
				p.getInventory().remove(p.getItemInHand());
			}else{
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			p.removePotionEffect(PotionEffectType.SPEED);
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 3));

			BukkitTask localBukkitTask = new BukkitRunnable(){
				public void run(){
					if(ArcherClass.this.isApplicableFor(p)){
						p.removePotionEffect(PotionEffectType.SPEED);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
					}
				}
			}.runTaskLater(this.plugin, 120L);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(tagged.containsKey(e.getPlayer().getUniqueId())){
			tagged.remove(e.getPlayer().getUniqueId());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event){
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if(((entity instanceof Player)) && ((damager instanceof Arrow))){
			Arrow arrow = (Arrow) damager;
			ProjectileSource source = arrow.getShooter();
			if((source instanceof Player)){
				Player damaged = (Player) event.getEntity();
				Player shooter = (Player) source;
				PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
				if((equipped == null) || (!equipped.equals(this))){
					return;
				}
				if(this.plugin.getTimerManager().archerTimer.getRemaining((Player) entity) == 0L){
					if((this.plugin.getPvpClassManager().getEquippedClass(damaged) != null) && (this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this))){
						return;
					}
					this.plugin.getTimerManager().archerTimer.setCooldown((Player) entity, entity.getUniqueId());
					tagged.put(damaged.getUniqueId(), shooter.getUniqueId());
					for(Player player : Bukkit.getOnlinePlayers()){
						HCF.getPlugin().getScoreboardHandler().getPlayerBoard(player.getUniqueId()).init(damaged);
					}
                    /*Player[] arrayOfPlayer;
                    int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
                    for (int i = 0; i < j; i++) {
                        Player localPlayer1 = arrayOfPlayer[i];
                    }*/
					shooter.sendMessage(ChatColor.GOLD + "You have hit a player (" + ChatColor.GRAY + damaged.getName() + ChatColor.GOLD + ")");
					damaged.sendMessage(ChatColor.GOLD + "§c§lMarked! §eAn archer has hit you and §dArcher Tagged§e you. §7(Taken damage will be increased by +25% for 10 seconds.)");
					damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 1));
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onArcherJumpClick(PlayerInteractEvent event){
		final Player p = event.getPlayer();
		Action action = event.getAction();
		if(((action == Action.RIGHT_CLICK_AIR) || (action == Action.RIGHT_CLICK_BLOCK)) && (event.hasItem()) && (event.getItem().getType() == Material.FEATHER)){
			if(this.plugin.getPvpClassManager().getEquippedClass(event.getPlayer()) != this){
				return;
			}
			if(Cooldowns.isOnCooldown("Archer_jump_cooldown", p)){
				p.sendMessage(ChatColor.RED + "You cannot use this for another §l" + Cooldowns.getCooldownForPlayerInt("Archer_jump_cooldown", p) + ChatColor.RED.toString() + " seconds!");
				event.setCancelled(true);
				return;
			}
			Cooldowns.addCooldown("Archer_jump_cooldown", p, 25);
			p.sendMessage(ChatColor.RED.toString() + "§cArcher Jump boost enabled.");
			if(p.getItemInHand().getAmount() == 1){
				p.getInventory().remove(p.getItemInHand());
			}else{
				p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
			}
			p.removePotionEffect(PotionEffectType.JUMP);
			p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 6));

			BukkitTask localBukkitTask = new BukkitRunnable(){
				public void run(){
					if(ArcherClass.this.isApplicableFor(p)){
						p.removePotionEffect(PotionEffectType.JUMP);
					}
				}
			}.runTaskLater(this.plugin, 120L);
		}
	}

	public boolean isApplicableFor(Player player){
		PlayerInventory playerInventory = player.getInventory();
		ItemStack helmet = playerInventory.getHelmet();
		if((helmet == null) || (helmet.getType() != Material.LEATHER_HELMET)){
			return false;
		}
		ItemStack chestplate = playerInventory.getChestplate();
		if((chestplate == null) || (chestplate.getType() != Material.LEATHER_CHESTPLATE)){
			return false;
		}
		ItemStack leggings = playerInventory.getLeggings();
		if((leggings == null) || (leggings.getType() != Material.LEATHER_LEGGINGS)){
			return false;
		}
		ItemStack boots = playerInventory.getBoots();
		return (boots != null) && (boots.getType() == Material.LEATHER_BOOTS);
	}
}