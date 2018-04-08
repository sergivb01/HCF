package net.veilmc.hcf.command.death.argument;

import me.sergivb01.sutils.utils.fanciful.FancyMessage;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class DeathInfoArgument extends CommandArgument{
	private final HCF plugin;

	public DeathInfoArgument(final HCF plugin){
		super("info", "Check Deathban Reason");
		this.plugin = plugin;
		this.permission = "hcf.command.death.argument." + this.getName();
	}

	public String getUsage(final String label){
		return '/' + label + ' ' + this.getName() + " <playerName>";
	}

	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args){
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + getUsage(command.getLabel()));
			return false;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
		if(target == null || (!(target.hasPlayedBefore()))){
			sender.sendMessage(ChatColor.RED + "Error: Player not found.");
			return true;
		}
		FactionUser player = HCF.getPlugin().getUserManager().getUser(target.getUniqueId());
		Deathban deathban = player.getDeathban();
		if(deathban == null || (!(deathban.isActive()))){
			sender.sendMessage(ChatColor.RED + "Error: This player is not deathbanned.");
			return true;
		}else{
			Double x = deathban.getDeathPoint().getX();
			Double y = deathban.getDeathPoint().getY();
			Double z = deathban.getDeathPoint().getZ();
			String remaining = HCF.getRemaining(deathban.getRemaining(), true, false);
			DecimalFormat df = new DecimalFormat("##");
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l" + target.getName() + " is currently deathbanned"));
			sender.sendMessage(" ");
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bReason: &9" + deathban.getReason()));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bRemaining: &9" + remaining));
			new FancyMessage("Location:")
					.color(ChatColor.AQUA)
					.then(df.format(x) + ", " + df.format(y) + ", " + df.format(z))
					.color(ChatColor.BLUE)
					.command("/tp " + df.format(x) + " " + df.format(y) + " " + df.format(z))
					.tooltip(ChatColor.YELLOW + "Click to teleport to " + df.format(x) + ", " + df.format(y) + ", " + df.format(z))
					.send(sender);
			//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bLocation: &9" + df.format(x) + ", " + df.format(y) + ", " + df.format(z)));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
			return true;
		}

	}
}