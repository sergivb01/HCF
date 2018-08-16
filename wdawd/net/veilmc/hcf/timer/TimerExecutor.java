package net.veilmc.hcf.timer;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.timer.argument.TimerCheckArgument;
import net.veilmc.hcf.timer.argument.TimerSetArgument;
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

public class TimerExecutor
		extends ArgumentExecutor{
	public TimerExecutor(HCF plugin){
		super("timer");
		this.addArgument(new TimerCheckArgument(plugin));
		this.addArgument(new TimerSetArgument(plugin));
	}
}

