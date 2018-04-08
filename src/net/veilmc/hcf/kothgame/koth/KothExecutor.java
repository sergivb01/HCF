package net.veilmc.hcf.kothgame.koth;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.kothgame.koth.argument.KothNextArgument;
import net.veilmc.hcf.kothgame.koth.argument.KothScheduleArgument;
import net.veilmc.hcf.kothgame.koth.argument.KothSetCapDelayArgument;
import net.veilmc.hcf.kothgame.koth.argument.KothShowArgument;
import net.veilmc.util.command.ArgumentExecutor;
import net.veilmc.util.command.CommandArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class KothExecutor
		extends ArgumentExecutor{
	private final KothScheduleArgument kothScheduleArgument;

	public KothExecutor(HCF plugin){
		super("koth");
		this.addArgument(new KothNextArgument(plugin));
		this.addArgument(new KothShowArgument());
		this.kothScheduleArgument = new KothScheduleArgument(plugin);
		this.addArgument(this.kothScheduleArgument);
		this.addArgument(new KothSetCapDelayArgument(plugin));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 1){
			this.kothScheduleArgument.onCommand(sender, command, label, args);
			return true;
		}
		return super.onCommand(sender, command, label, args);
	}
}

