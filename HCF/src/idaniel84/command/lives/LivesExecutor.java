package idaniel84.command.lives;

import idaniel84.command.lives.argument.LivesClearDeathbansArgument;

import idaniel84.HCF;
import idaniel84.command.lives.argument.LivesCheckArgument;
import idaniel84.command.lives.argument.LivesGiveArgument;
import idaniel84.command.lives.argument.LivesReviveArgument;
import idaniel84.command.lives.argument.LivesSetArgument;
import idaniel84.command.lives.argument.LivesSetDeathbanTimeArgument;
import net.veilmc.util.command.ArgumentExecutor;

public class LivesExecutor extends ArgumentExecutor{
	public LivesExecutor(HCF plugin){
		super("lives");
		this.addArgument(new LivesCheckArgument(plugin));
		this.addArgument(new LivesClearDeathbansArgument(plugin));
		this.addArgument(new LivesGiveArgument(plugin));
		this.addArgument(new LivesReviveArgument(plugin));
		this.addArgument(new LivesSetArgument(plugin));
		this.addArgument(new LivesSetDeathbanTimeArgument());
	}
}

