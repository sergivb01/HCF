package idaniel84.command.crate;

import idaniel84.HCF;
import idaniel84.command.crate.argument.LootBankArgument;
import idaniel84.command.crate.argument.LootBroadcastsArgument;
import idaniel84.command.crate.argument.LootDepositArgument;
import idaniel84.command.crate.argument.LootGiveArgument;
import idaniel84.command.crate.argument.LootListArgument;
import idaniel84.command.crate.argument.LootWithdrawArgument;
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

public class LootExecutor
		extends ArgumentExecutor{
	public LootExecutor(HCF plugin){
		super("loot");
		this.addArgument(new LootBankArgument(plugin));
		this.addArgument(new LootBroadcastsArgument());
		this.addArgument(new LootDepositArgument(plugin));
		this.addArgument(new LootGiveArgument(plugin));
		this.addArgument(new LootListArgument(plugin));
		this.addArgument(new LootWithdrawArgument(plugin));
	}
}

