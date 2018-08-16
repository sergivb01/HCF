package idaniel84.command.death;

import idaniel84.HCF;
import idaniel84.command.death.argument.DeathInfoArgument;
import idaniel84.command.death.argument.DeathRefundArgument;
import idaniel84.command.death.argument.DeathReviveArgument;
import net.veilmc.util.command.ArgumentExecutor;

public class DeathExecutor extends ArgumentExecutor{
	public DeathExecutor(HCF plugin){
		super("death");
		this.addArgument(new DeathInfoArgument(plugin));
		this.addArgument(new DeathRefundArgument(plugin));
		this.addArgument(new DeathReviveArgument(plugin));
	}
}
