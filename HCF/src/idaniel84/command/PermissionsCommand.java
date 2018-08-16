package idaniel84.command;

import idaniel84.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permissible;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionsCommand
		implements CommandExecutor, Listener{
	private final HCF plugin;
	public Inventory opList;
	public Inventory permList;

	public PermissionsCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		Player player = (Player) sender;
		if(args.length == 0){
			player.sendMessage(ChatColor.RED + "Usage: /" + command.getName() + " <groups/ops/permission> [permission node]");
			return true;
		}
		if(args[0].equalsIgnoreCase("groups")){
			player.sendMessage("groups");
			return true;

		}else if(args[0].equalsIgnoreCase("ops")){
			player.sendMessage("ops");
			opList = Bukkit.createInventory(player, 54, "Management | OP List");
			getOpGUI(player);
			return true;
		}else if(args[0].equalsIgnoreCase("permission")){
			if(args.length == 1){
				player.sendMessage("You have not specified a permission to query.");
				return true;
			}else{
				player.sendMessage("permission check");
				permList = Bukkit.createInventory(player, 54, "Management | Permissions");
				getPermissionGui(player, args[1]);
				return true;
			}
		}else{
			player.sendMessage(ChatColor.RED + "Usage: /" + command.getName() + " <groups/ops/permission> [permission node]");
			return true;
		}
	}


	private void getOpGUI(Player player){
		Set<OfflinePlayer> list = (Bukkit.getOperators());
		int i = 0;
		for(OfflinePlayer p : list){
			ItemStack is = new ItemStack(35, 1, (short) (14));
			ItemMeta meta = is.getItemMeta();
			meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7&oClick to &a&ode-op&7&o this player.")));
			meta.setDisplayName(ChatColor.GOLD.toString() + p.getName());
			is.setItemMeta(meta);
			opList.setItem(i, is);
			i++;
		}
		player.openInventory(opList);
	}

	private void getPermissionGui(Player player, String permssion){
		int i = 0;
		for(Permissible permissible : Bukkit.getPluginManager().getPermissionSubscriptions(permssion).stream().filter(permissible -> permissible instanceof Player).collect(Collectors.toList())){
			Player targ = (Player) permissible;
			ItemStack is = new ItemStack(35, 1, (short) (14));
			ItemMeta meta = is.getItemMeta();
			meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&7abc")));
			meta.setDisplayName(ChatColor.GOLD.toString() + targ.getName());
			is.setItemMeta(meta);
			permList.setItem(i, is);
			i++;
			//TODO: Check if var i <= MAX_INV_SLOTS
			//TODO: Create + open inventory
		}


        /*Set<OfflinePlayer> list = (Bukkit.getOperators());
        int i = 0;
        for (OfflinePlayer p : list) {
            ItemStack is = new ItemStack(35, 1, (short) (14));
            ItemMeta meta = is.getItemMeta();
            meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7abc")));
            meta.setDisplayName(ChatColor.GOLD.toString() + p.getName());
            is.setItemMeta(meta);
            permList.setItem(i, is);
            i++;
        }*/
		player.openInventory(permList);
	}


}
