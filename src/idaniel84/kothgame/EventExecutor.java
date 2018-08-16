package idaniel84.kothgame;

import idaniel84.HCF;
import idaniel84.kothgame.argument.*;
import idaniel84.palace.EventSetCapzone;
import idaniel84.HCF;
import idaniel84.kothgame.argument.*;
import idaniel84.palace.EventSetCapzone;
import idaniel84.palace.EventSetCapzone;
import idaniel84.kothgame.argument.*;
import idaniel84.kothgame.argument.*;
import idaniel84.palace.EventSetCapzone;
import net.veilmc.util.command.ArgumentExecutor;

public class EventExecutor
		extends ArgumentExecutor{
	public EventExecutor(HCF plugin){
		super("event");
		this.addArgument(new EventListArgument(plugin));
		this.addArgument(new EventCancelArgument(plugin));
		this.addArgument(new EventCreateArgument(plugin));
		this.addArgument(new EventDeleteArgument(plugin));
		this.addArgument(new EventRenameArgument(plugin));
		this.addArgument(new EventSetAreaArgument(plugin));
		this.addArgument(new EventSetCapzoneArgument(plugin));
		this.addArgument(new EventStartArgument(plugin));
		this.addArgument(new EventUptimeArgument(plugin));
		this.addArgument(new EventSetCapzone(plugin));
	}
}

