package idaniel84.command.spawn;

import idaniel84.HCF;
import idaniel84.command.spawn.argument.TokenCheckArgument;
import idaniel84.command.spawn.argument.TokenGiveArgument;
import idaniel84.command.spawn.argument.TokenSetArgument;
import idaniel84.HCF;
import idaniel84.command.spawn.argument.TokenCheckArgument;
import idaniel84.command.spawn.argument.TokenGiveArgument;
import idaniel84.command.spawn.argument.TokenSetArgument;
import idaniel84.command.spawn.argument.TokenCheckArgument;
import idaniel84.command.spawn.argument.TokenGiveArgument;
import idaniel84.command.spawn.argument.TokenSetArgument;
import idaniel84.command.spawn.argument.TokenCheckArgument;
import idaniel84.command.spawn.argument.TokenGiveArgument;
import idaniel84.command.spawn.argument.TokenSetArgument;
import net.veilmc.util.command.ArgumentExecutor;

public class TokenExecutor extends ArgumentExecutor{
	public TokenExecutor(HCF plugin){
		super("token");
		this.addArgument(new TokenCheckArgument(plugin));
		this.addArgument(new TokenSetArgument(plugin));
		this.addArgument(new TokenGiveArgument(plugin));
	}
}
