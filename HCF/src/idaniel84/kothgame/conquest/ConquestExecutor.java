package idaniel84.kothgame.conquest;

import idaniel84.HCF;
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

public class ConquestExecutor
		extends ArgumentExecutor{
	public ConquestExecutor(HCF plugin){
		super("conquest");
		this.addArgument(new ConquestSetpointsArgument(plugin));
	}
}

