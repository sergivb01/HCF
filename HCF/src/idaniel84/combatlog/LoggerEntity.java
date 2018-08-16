package idaniel84.combatlog;

import com.google.common.base.Function;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoggerEntity
		extends EntityVillager{
	private static final Function<Double, Double> DAMAGE_FUNCTION = f1 -> 0.0;
	private final UUID playerUUID;

	public LoggerEntity(World world, Location location, Player player){
		super((World) ((CraftWorld) world).getHandle());
		this.lastDamager = ((CraftPlayer) player).getHandle().lastDamager;
		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		setPosition(x, y, z);
		int i = MathHelper.floor(locX / 16.0D);
		int j = MathHelper.floor(locZ / 16.0D);
		((CraftWorld) world).getHandle().getChunkAt(i, j);
		String playerName = player.getName();

		boolean hasSpawned = ((CraftWorld) world).getHandle().addEntity((Entity) this, CreatureSpawnEvent.SpawnReason.CUSTOM);
		Bukkit.getConsoleSender().sendMessage((Object) ChatColor.GOLD + "Combat Logger for [" + playerName + "] " + (hasSpawned ? new StringBuilder().append((Object) ChatColor.GREEN).append("successfully spawned").toString() : new StringBuilder().append((Object) ChatColor.RED).append("failed to spawn").toString()) + (Object) ChatColor.GOLD + " at (" + String.format("%.1f", x) + ", " + String.format("%.1f", y) + ", " + String.format("%.1f", z) + ')');
		this.playerUUID = player.getUniqueId();
		if(hasSpawned){
			this.setCustomName(ChatColor.GRAY + "(Logger) " + ChatColor.RED + playerName);
			this.setCustomNameVisible(true);
			this.setPositionRotation(x, y, z, location.getYaw(), location.getPitch());
		}
	}

	private static PlayerNmsResult getResult(World world, UUID playerUUID){
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID) playerUUID);
		if(offlinePlayer.hasPlayedBefore()){
			WorldServer worldServer = ((CraftWorld) world).getHandle();
			EntityPlayer entityPlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), worldServer, new GameProfile(playerUUID, offlinePlayer.getName()), new PlayerInteractManager((World) worldServer));
			CraftPlayer player = entityPlayer.getBukkitEntity();
			if(player != null){
				player.loadData();
				return new PlayerNmsResult((Player) player, entityPlayer);
			}
		}
		return null;
	}

	public UUID getPlayerUUID(){
		return this.playerUUID;
	}

	public void move(double d0, double d1, double d2){
	}

	public void b(int i){
	}

	public void dropDeathLoot(boolean flag, int i){
	}

	public Entity findTarget(){
		return null;
	}

	public boolean damageEntity(DamageSource damageSource, float amount){
		PlayerNmsResult nmsResult = LoggerEntity.getResult((World) this.world.getWorld(), this.playerUUID);
		if(nmsResult == null){
			return true;
		}
		EntityPlayer entityPlayer = nmsResult.entityPlayer;
		if(entityPlayer != null){
			entityPlayer.setPosition(this.locX, this.locY, this.locZ);
			EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent((Entity) entityPlayer, (DamageSource) damageSource, (double) amount, (double) 0.0, (double) 0.0, (double) 0.0, (double) 0.0, (double) 0.0, (double) 0.0, DAMAGE_FUNCTION, DAMAGE_FUNCTION, DAMAGE_FUNCTION, DAMAGE_FUNCTION, DAMAGE_FUNCTION, DAMAGE_FUNCTION);
			if(event.isCancelled()){
				return false;
			}
		}
		return super.damageEntity(damageSource, amount);
	}

	public boolean a(EntityHuman entityHuman){
		return false;
	}

	public void h(){
		super.h();
	}

	public void collide(Entity entity){
	}

	public void die(final DamageSource damageSource){
		final PlayerNmsResult playerNmsResult = getResult(this.world.getWorld(), this.playerUUID);
		if(playerNmsResult == null){
			return;
		}
		final Player player = playerNmsResult.player;
		final PlayerInventory inventory = player.getInventory();
		final boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");
		final List<ItemStack> drops = new ArrayList<ItemStack>();
		if(!keepInventory){
			for(final ItemStack loggerDeathEvent : inventory.getContents()){
				if(loggerDeathEvent != null && loggerDeathEvent.getType() != Material.AIR){
					drops.add(loggerDeathEvent);
				}
			}
			for(final ItemStack loggerDeathEvent : inventory.getArmorContents()){
				if(loggerDeathEvent != null && loggerDeathEvent.getType() != Material.AIR){
					drops.add(loggerDeathEvent);
				}
			}
		}
		String deathMessage2 = ChatColor.GRAY + "(Combat-Logger) " + this.combatTracker.b().c();
		final EntityPlayer entityPlayer2 = playerNmsResult.entityPlayer;
		entityPlayer2.combatTracker = this.combatTracker;
		if(Bukkit.getPlayer(entityPlayer2.getName()) != null){
			Bukkit.getPlayer(entityPlayer2.getUniqueID()).getInventory().clear();
			Bukkit.getPlayer(entityPlayer2.getUniqueID()).kickPlayer("error");
		}
		final PlayerDeathEvent event2 = CraftEventFactory.callPlayerDeathEvent(entityPlayer2, (List) drops, deathMessage2, keepInventory);
		deathMessage2 = event2.getDeathMessage();
		if(deathMessage2 != null && !deathMessage2.isEmpty()){
			Bukkit.broadcastMessage(deathMessage2);
		}
		super.die(damageSource);
		final LoggerDeathEvent loggerDeathEvent2 = new LoggerDeathEvent(this);
		Bukkit.getPluginManager().callEvent((Event) loggerDeathEvent2);
		if(!event2.getKeepInventory()){
			inventory.clear();
			inventory.setArmorContents(new ItemStack[inventory.getArmorContents().length]);
		}
		entityPlayer2.setLocation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
		entityPlayer2.setHealth(0.0f);
		player.saveData();
	}

	public CraftLivingEntity getBukkitEntity(){
		return (CraftLivingEntity) super.getBukkitEntity();
	}

	public static final class PlayerNmsResult{
		public final Player player;
		public final EntityPlayer entityPlayer;

		public PlayerNmsResult(Player player, EntityPlayer entityPlayer){
			this.player = player;
			this.entityPlayer = entityPlayer;
		}
	}

}

