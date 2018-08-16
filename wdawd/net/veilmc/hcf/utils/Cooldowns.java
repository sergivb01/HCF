package net.veilmc.hcf.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Cooldowns{
	private static HashMap<String, HashMap<UUID, Long>> cooldown = new HashMap();

	public static void createCooldown(String k){
		if(cooldown.containsKey(k)){
			throw new IllegalArgumentException("Cooldown already exists.");
		}
		cooldown.put(k, new HashMap());
	}

	public static HashMap<UUID, Long> getCooldownMap(String k){
		if(cooldown.containsKey(k)){
			return cooldown.get(k);
		}
		return null;
	}

	public static void addCooldown(String k, Player p, int seconds){
		if(!cooldown.containsKey(k)){
			throw new IllegalArgumentException(k + " does not exist");
		}
		long next = System.currentTimeMillis() + (long) seconds * 1000;
		cooldown.get(k).put(p.getUniqueId(), next);
	}

	public static boolean isOnCooldown(String k, Player p){
		return cooldown.containsKey(k) && cooldown.get(k).containsKey(p.getUniqueId()) && System.currentTimeMillis() <= cooldown.get(k).get(p.getUniqueId());
	}

	public static int getCooldownForPlayerInt(String k, Player p){
		return (int) (cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis()) / 1000;
	}

	public static long getCooldownForPlayerLong(String k, Player p){
		return cooldown.get(k).get(p.getUniqueId()) - System.currentTimeMillis();
	}

	public static void removeCooldown(String k, Player p){
		if(!cooldown.containsKey(k)){
			throw new IllegalArgumentException(k + " does not exist");
		}
		cooldown.get(k).remove(p.getUniqueId());
	}
}

