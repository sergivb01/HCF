package idaniel84.command;

import idaniel84.HCF;
import net.veilmc.util.ItemBuilder;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SpawnerCommand
		implements CommandExecutor, TabCompleter{
	private final HCF plugin;

	public SpawnerCommand(HCF plugin){
		this.plugin = plugin;
	}

	public String C(String msg){
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(args.length == 0){
			sender.sendMessage(ChatColor.RED + "/spawner <entity>");

			return false;
		}
		String spawner = args[0];

		Player p = (Player) sender;

		Inventory inv = p.getInventory();

		inv.addItem(new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").loreLine(ChatColor.WHITE + WordUtils.capitalizeFully(spawner)).build());

		p.sendMessage(C("&cYou just got a &e" + spawner + "&c."));

		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		return Collections.emptyList();
	}
}
