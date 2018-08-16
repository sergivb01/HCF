package idaniel84.balance;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;

import java.util.UUID;

public interface EconomyManager{
	char ECONOMY_SYMBOL = '$';

	TObjectIntMap<UUID> getBalanceMap();

	int getBalance(UUID var1);

	int setBalance(UUID var1, int var2);

	int addBalance(UUID var1, int var2);

	int subtractBalance(UUID var1, int var2);

	void reloadEconomyData();

	void saveEconomyData();
}

