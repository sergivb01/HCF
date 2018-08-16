package idaniel84.timer;

import idaniel84.timer.argument.TimerCheckArgument;
import idaniel84.timer.argument.TimerSetArgument;
import idaniel84.timer.argument.TimerCheckArgument;
import idaniel84.timer.argument.TimerSetArgument;
import idaniel84.timer.argument.TimerCheckArgument;
import idaniel84.timer.argument.TimerSetArgument;
import idaniel84.HCF;
import idaniel84.timer.argument.TimerCheckArgument;
import idaniel84.timer.argument.TimerSetArgument;
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

