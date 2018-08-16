package idaniel84.listener;

import idaniel84.HCF;
import idaniel84.HCF;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.imagemessage.ImageChar;
import net.veilmc.util.imagemessage.ImageMessage;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class EndListener
		implements Listener{


	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		if(event.getEntity() instanceof EnderDragon){
			PlayerFaction faction = HCF.getPlugin().getFactionManager().getPlayerFaction(event.getEntity().getKiller().getUniqueId());
			String factionName = faction == null ? "Player: " + event.getEntity().getKiller().getName() : "Faction: " + faction.getName();
			for(int i = 0; i < 5; ++i){
				Bukkit.broadcastMessage("");
			}
			for(Player on : Bukkit.getOnlinePlayers()){
				try{
					BufferedImage imageToSend = ImageIO.read(HCF.getPlugin().getResource("enderdragon-art.png"));
					String[] arrstring = new String[10];
					arrstring[0] = "";
					arrstring[1] = "";
					arrstring[2] = "";
					arrstring[3] = "";
					arrstring[4] = "";
					arrstring[5] = "";
					arrstring[6] = ChatColor.RED + "[EnderDragon]";
					arrstring[7] = ChatColor.YELLOW + "Slain by";
					arrstring[8] = ChatColor.YELLOW.toString() + ChatColor.BOLD + factionName;
					arrstring[9] = ChatColor.GRAY + (!factionName.contains("Faction: ") ? "" : "Player: " + event.getEntity().getKiller().getName());
					new ImageMessage(imageToSend, 15, ImageChar.BLOCK.getChar()).appendText(arrstring).sendToPlayer(on);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
			ItemMeta itemMeta = dragonEgg.getItemMeta();
			SimpleDateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
			itemMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Enderdragon " + ChatColor.YELLOW + "slain by " + ChatColor.AQUA + event.getEntity().getKiller().getName(), ChatColor.YELLOW + sdf.format(new Date()).replace(" AM", "").replace(" PM", "")));
			dragonEgg.setItemMeta(itemMeta);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + event.getEntity().getKiller().getName() + " Dragon 5");
			event.getEntity().getKiller().getInventory().addItem(dragonEgg);
			if(!event.getEntity().getKiller().getInventory().contains(Material.DRAGON_EGG)){
				event.getDrops().add(dragonEgg);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.DRAGON_EGG){
			Player player = e.getPlayer();
			ExperienceOrb exp = (ExperienceOrb) e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation(), (Class) ExperienceOrb.class);
			exp.setExperience(1);
			if(e.getClickedBlock().hasMetadata("broken")){
				return;
			}
			Random random = new Random();
			Integer breaks = random.nextInt(1200) + 1;
			if(breaks == 185){
				e.getClickedBlock().setMetadata("broken", new FixedMetadataValue(HCF.getPlugin(), "broken"));
				if(HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()) != null){
					HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()).broadcast(ChatColor.YELLOW + "Your ender-dragon egg has been broken. It will " + ChatColor.RED + "no longer " + ChatColor.YELLOW + "drop items.");
				}else{
					player.sendMessage(ChatColor.YELLOW + "Your ender-dragon egg has been broken. It will " + ChatColor.RED + "no longer " + ChatColor.YELLOW + "drop items.");
				}
				for(Entity nearby : player.getNearbyEntities(20.0, 350.0, 20.0)){
					if(!(nearby instanceof Player)) continue;
					((Player) nearby).sendMessage(ChatColor.YELLOW + "Your ender-dragon egg has been broken. It will " + ChatColor.RED + "no longer " + ChatColor.YELLOW + "drop items.");
					((Player) nearby).playSound(e.getClickedBlock().getLocation(), Sound.ANVIL_BREAK, 10.0f, 10.0f);
				}
			}
			Integer rand = random.nextInt(100) + 1;
			Integer gunpowder = random.nextInt(8) + 1;
			Integer enderpearl = random.nextInt(4) + 1;
			if(rand == 15){
				e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.SULPHUR, gunpowder.intValue()));
			}
			if(rand == 17){
				e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.ENDER_PEARL, enderpearl.intValue()));
			}
		}
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerPortal(PlayerPortalEvent event){
		Location endSpawnLocation = Bukkit.getWorld("world_the_end").getSpawnLocation();
		Location endExitLocation = new Location(Bukkit.getWorld("world"), 0.0D, 70.0D, 200.0D);

		endSpawnLocation.setYaw(89);
		endExitLocation.setYaw(0);
		if(event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL){
			if(event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END){
				event.getPlayer().teleport(endExitLocation);
			}else if(event.getFrom().getWorld().getEnvironment() == World.Environment.NORMAL){
				event.getPlayer().teleport(endSpawnLocation);
			}

			event.setCancelled(true);
		}
	}


	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent event){
		if(event.getEntity() instanceof Item && event.getPortalType() == PortalType.ENDER){
			event.getBlocks().clear();
		}
	}

	@EventHandler
	public void onEnderDragonSpawn(CreatureSpawnEvent event){
		if(event.getEntity().getType() == EntityType.ENDER_DRAGON){
			event.getEntity().setCustomName(ChatColor.YELLOW + "Ender Dragon");
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof EnderDragon && event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END){
			((EnderDragon) event.getEntity()).setCustomName(ChatColor.YELLOW + "Ender Dragon");
		}
	}

	@EventHandler
	public void onCreatePortal(EntityCreatePortalEvent event){
		if(event.getEntity().getType() == EntityType.ENDER_DRAGON){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		if(event.getEntity() instanceof EnderDragon){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityPortal(EntityPortalEvent event){
		if(event.getEntity() instanceof EnderDragon){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event){
		if(event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL){
			return;
		}
		Player player = event.getPlayer();
		if(event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL){
			if(event.getFrom().getWorld().getEntitiesByClass((Class) EnderDragon.class).size() != 0){
				event.setCancelled(true);
				event.setTo(event.getFrom());
				HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "You cannot leave the end before the dragon is killed.");
			}
			event.useTravelAgent(false);
			event.setTo(HCF.getPlugin().getServerHandler().getEndExit());
		}else if(event.getTo().getWorld().getEnvironment() == World.Environment.THE_END){
			if(HCF.getPlugin().getTimerManager().spawnTagTimer.hasCooldown(player)){
				event.setCancelled(true);
				HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "You cannot enter the end while spawn tagged.");
			}
			if(HCF.getPlugin().getTimerManager().pvpProtectionTimer.hasCooldown(player)){
				event.setCancelled(true);
				HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "You cannot enter the end while you have pvp protection.");
			}
			if((!HCF.getPlugin().getServerHandler().isEnd() || HCF.getPlugin().getEotwHandler().isEndOfTheWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE){
				event.setCancelled(true);
				HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "The End is currently disabled.");
			}
			event.useTravelAgent(false);
			event.setTo(event.getTo().getWorld().getSpawnLocation());
		}
		if(event.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
			boolean found = false;
			for(PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()){
				if(!potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) continue;
				found = true;
			}
			if(found){
				event.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
			}
		}
	}
}

