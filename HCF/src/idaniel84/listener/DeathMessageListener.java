package idaniel84.listener;

import idaniel84.HCF;
import com.google.common.base.Preconditions;

import net.minecraft.server.v1_7_R4.EntityLiving;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class DeathMessageListener
		implements Listener{
	private final HCF plugin;

	public DeathMessageListener(HCF plugin){
		this.plugin = plugin;
	}

	public static String replaceLast(String text, String regex, String replacement){
		return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event){
		String message = event.getDeathMessage();
		if(message == null || message.isEmpty()){
			return;
		}
		EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.CUSTOM;
		if(event.getEntity().getLastDamageCause() != null){
			cause = event.getEntity().getLastDamageCause().getCause();
		}
		boolean isLogger = false;
		if(event.getDeathMessage().contains("Combat-Logger")){
			isLogger = true;
		}

		event.setDeathMessage(this.getDeathMessage(event.getEntity(), this.getKiller(event), cause, isLogger));

	}


	public String toReadable(ItemStack item){
		if(item == null || item.getType() == Material.AIR){
			return "";
		}
		if(item.hasItemMeta()){
			ItemMeta meta = item.getItemMeta();
			if(meta.hasDisplayName()){
				return ChatColor.YELLOW + " using " + ChatColor.RED + meta.getDisplayName() + ChatColor.YELLOW + ".";
			}
		}
		return ChatColor.YELLOW + " using " + ChatColor.RED + toReadable(item.getType()) + ChatColor.YELLOW + ".";
	}

	public String toReadable(Enum enu){
		return WordUtils.capitalize(enu.name().replace("_", " ").toLowerCase());
	}

	private CraftEntity getKiller(PlayerDeathEvent event){
		EntityLiving lastAttacker = ((CraftPlayer) event.getEntity()).getHandle().aX();
		return lastAttacker == null ? null : lastAttacker.getBukkitEntity();
	}

	private String getDeathMessage(org.bukkit.entity.Player player, org.bukkit.entity.Entity killer, EntityDamageEvent.DamageCause cause, boolean isLogger){
		String input = "";

		if(killer instanceof Player){
			ItemStack item = ((Player) killer).getItemInHand();
			if(item != null && item.getType() == Material.BOW){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " was shot by " + ChatColor.RED + getName(killer);
				input += ChatColor.YELLOW + " from " + ChatColor.LIGHT_PURPLE + (int) player.getLocation().distance(killer.getLocation()) + ChatColor.LIGHT_PURPLE + " blocks" + ChatColor.YELLOW + ".";
			}else{
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " was slain by " + ChatColor.RED + getName(killer);
				input += toReadable(item);
			}
		}else{
			if(cause == DamageCause.FALL){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " fell from a high place.";
			}else if(cause == DamageCause.FIRE){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " died to fire.";
			}else if(cause == DamageCause.LIGHTNING){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " died to lightning.";
			}else if(cause == DamageCause.WITHER){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " withered away.";
			}else if(cause == DamageCause.DROWNING){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " drowned.";
			}else if(cause == DamageCause.FALLING_BLOCK){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " died to a falling block.";
			}else if(cause == DamageCause.MAGIC){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " died to magic.";
			}else if(cause == DamageCause.VOID){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " fell into the void.";
			}else if(cause == DamageCause.ENTITY_EXPLOSION){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " died to an explosion.";
			}else if(cause == DamageCause.LAVA){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " burnt to a crisp.";
			}else if(cause == DamageCause.STARVATION){
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " starved to death.";
			}else{
				input = ChatColor.RED + getName(player) + ChatColor.YELLOW + " died.";

			}
		}
//		if(isLogger){
//			input = ChatColor.RED + "(Combat-Logger) " + input;
//		}
		return input;
	}

	private String getEntityName(org.bukkit.entity.Entity entity){
		Preconditions.checkNotNull((Object) entity, "Entity cannot be null");
		return entity instanceof Player ? ((Player) entity).getName() : ((CraftEntity) entity).getHandle().getName();
	}

	private String getDisplayName(org.bukkit.entity.Entity entity){
		Preconditions.checkNotNull((Object) entity, "Entity cannot be null");
		if(entity instanceof Player){
			Player player = (Player) entity;
			String rank = ChatColor.translateAlternateColorCodes('&', "&e" + PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
			return rank + player.getName() + ChatColor.GOLD + '[' + ChatColor.WHITE + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.GOLD + ']';
		}
		return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
	}

	private String getName(org.bukkit.entity.Entity entity){
		Preconditions.checkNotNull((Object) entity, "Entity cannot be null");
		if(entity instanceof Player){
			Player player = (Player) entity;
			return ChatColor.RED + player.getName() + ChatColor.GOLD + '[' + ChatColor.WHITE + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.GOLD + ']';
		}
		return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
	}


}