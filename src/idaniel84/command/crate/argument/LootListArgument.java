package idaniel84.command.crate.argument;

import idaniel84.HCF;
import idaniel84.command.crate.Key;
import idaniel84.command.crate.KeyListener;
import net.veilmc.util.command.CommandArgument;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class LootListArgument
		extends CommandArgument{
	private final HCF plugin;

	public LootListArgument(HCF plugin){
		super("list", "List all crate key types");
		this.plugin = plugin;
		this.permission = "hcf.command.loot.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		List keyNames = KeyListener.getKeys().stream().map(Key::getDisplayName).collect(Collectors.toList());
		sender.sendMessage(ChatColor.GRAY + "List of key types: " + StringUtils.join(keyNames, new StringBuilder().append(ChatColor.GRAY).append(", ").toString()));
		return true;
	}
}

