package net.veilmc.hcf.deathban;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import org.bukkit.entity.Player;

public interface DeathbanManager{
	long MAX_DEATHBAN_TIME = TimeUnit.HOURS.toMillis(8);

	TObjectIntMap<UUID> getLivesMap();

	int getLives(UUID var1);

	int setLives(UUID var1, int var2);

	int addLives(UUID var1, int var2);

	int takeLives(UUID var1, int var2);

	long getDeathBanMultiplier(Player var1);

	Deathban applyDeathBan(Player var1, String var2);

	Deathban applyDeathBan(UUID var1, Deathban var2);

	void reloadDeathbanData();

	void saveDeathbanData();
}

