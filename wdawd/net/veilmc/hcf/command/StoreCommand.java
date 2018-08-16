package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class StoreCommand implements CommandExecutor, Listener{
	private final HCF plugin;
	public Inventory page1;

	public StoreCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Player p = (Player) sender;
		this.page1 = Bukkit.createInventory(null, 9, "Store");
		p.openInventory(page1);
		ItemStack live1 = new ItemStack(Material.PAPER, 1, (short) 3);
		ItemMeta live1meta = live1.getItemMeta();
		live1meta.setLore((Arrays.asList((ChatColor.WHITE + " * " + ChatColor.GREEN + "$5000"), (ChatColor.GRAY + "When you purchase this"), (ChatColor.GRAY + "you will recieve " + ChatColor.GOLD + "1x live"))));
		live1meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "1x Life");
		live1.setItemMeta(live1meta);
		page1.setItem(3, live1);

		ItemStack live5 = new ItemStack(Material.BOOK, 5, (short) 3);
		ItemMeta live5meta = live1.getItemMeta();
		live5meta.setLore((Arrays.asList((ChatColor.WHITE + " * " + ChatColor.GREEN + "$25000"), (ChatColor.GRAY + "When you purchase this"), (ChatColor.GRAY + "you will recieve " + ChatColor.GOLD + "5x lives"))));
		live5meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "5x Life");
		live5.setItemMeta(live5meta);
		page1.setItem(5, live5);

		return true;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		UUID uuid = player.getUniqueId();
		ItemStack clicked = event.getCurrentItem();
		Inventory inventory = event.getInventory();

		if(inventory.getName().equals("Store")){

			int balance = this.plugin.getEconomyManager().getBalance(uuid);

			if(clicked.getType() == Material.PAPER){
				if(balance < 5000){
					player.sendMessage(ChatColor.RED + "You can not afford this item.");
				}else{
					player.sendMessage(ChatColor.GREEN + "You have purchased this item.");
					this.plugin.getEconomyManager().setBalance(uuid, balance - 5000);
					this.plugin.getDeathbanManager().addLives(uuid, 1);
					player.closeInventory();
				}
				event.setCancelled(true);
			}
			if(clicked.getType() == Material.BOOK){
				if(balance < 25000){
					player.sendMessage(ChatColor.RED + "You can not afford this item.");
				}else{
					player.sendMessage(ChatColor.GREEN + "You have purchased this item.");
					this.plugin.getEconomyManager().setBalance(uuid, balance - 25000);
					this.plugin.getDeathbanManager().addLives(uuid, 5);
					player.closeInventory();
				}
				event.setCancelled(true);
			}


		}
	}
}