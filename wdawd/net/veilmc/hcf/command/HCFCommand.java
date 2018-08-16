package net.veilmc.hcf.command;

import net.veilmc.base.BaseConstants;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.command.ArgumentExecutor;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HCFCommand
		extends ArgumentExecutor{
	private static final Pattern USAGE_REPLACER_PATTERN = Pattern.compile("(command)", 16);
	private final String name;
	private final String description;
	private String[] aliases;
	private String usage;

	public HCFCommand(String name, String description){
		super(name);
		this.name = name;
		this.description = description;
	}

	public static boolean checkNull(CommandSender sender, String player){
		Player target = BukkitUtils.playerWithNameOrUUID(player);
		if(target == null || !HCFCommand.canSee(sender, target)){
			sender.sendMessage(String.format(BaseConstants.PLAYER_WITH_NAME_OR_UUID_NOT_FOUND, player));
			return true;
		}
		return false;
	}

	public static boolean canSee(CommandSender sender, Player target){
		return target != null && (!(sender instanceof Player) || ((Player) sender).canSee(target));
	}

	public final String getPermission(){
		return "command." + this.name;
	}

	public boolean isPlayerOnlyCommand(){
		return false;
	}

	public String getName(){
		return this.name;
	}

	public String getDescription(){
		return this.description;
	}

	public String getUsage(){
		if(this.usage == null){
			this.usage = "";
		}
		return ChatColor.RED + "Usage: " + USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(this.name) + ChatColor.GRAY + " - " + this.description;
	}

	public void setUsage(String usage){
		this.usage = usage;
	}

	public String getUsage(String label){
		return ChatColor.RED + "Usage: " + USAGE_REPLACER_PATTERN.matcher(this.usage).replaceAll(label) + ChatColor.GRAY + " - " + this.description;
	}

	public String[] getAliases(){
		if(this.aliases == null){
			this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
		}
		return Arrays.copyOf(this.aliases, this.aliases.length);
	}

	protected void setAliases(String[] aliases){
		this.aliases = aliases;
	}
}

