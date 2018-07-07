package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.faction.type.WarzoneFaction;
import net.veilmc.hcf.faction.type.WildernessFaction;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class EnderChestCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }
        Player player = (Player) sender;
        Faction playerFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(player.getLocation());
        if (!(playerFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(player.getLocation())).isSafezone() && !(playerFactionAt instanceof WildernessFaction) && !(playerFactionAt instanceof WarzoneFaction)) {
            player.sendMessage(ChatColor.RED + "You cannot run this command in other factions.");
            return true;
        }
        if(HCF.getPlugin().getTimerManager().spawnTagTimer.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You can not do this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
            return true;
        }
        player.sendMessage(ChatColor.GREEN + "Chest opened.");
        Inventory i = Bukkit.createInventory(player, 36, ChatColor.RED.toString() + ChatColor.BOLD + "EnderChest");
        int a = 0;
        for(int ix = 0; ix < player.getEnderChest().getSize(); ix++){
            i.setItem(ix, player.getEnderChest().getItem(ix));
            a = ix;
        }
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
        if(player.getInventory().getHelmet() != null){
            i.setItem(27, player.getPlayer().getInventory().getHelmet());
        }
        else i.setItem(27, glass);
        if(player.getPlayer().getInventory().getChestplate() != null){
            i.setItem(28, player.getPlayer().getInventory().getChestplate());
        }
        else i.setItem(28, glass);
        if(player.getPlayer().getInventory().getLeggings() != null){
            i.setItem(29, player.getPlayer().getInventory().getLeggings());
        }
        else i.setItem(29, glass);
        if(player.getPlayer().getInventory().getBoots() != null){
            i.setItem(30, player.getPlayer().getInventory().getBoots());
        }
        else i.setItem(30, glass);

        ItemStack note = new ItemStack(Material.PAPER);
        ItemMeta noteMeta = note.getItemMeta();
        noteMeta.setDisplayName(ChatColor.RED + "EnderChest Info.");
        noteMeta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "This is your armor preview, in the future", ChatColor.LIGHT_PURPLE + "you will be able to get this armor and set", ChatColor.LIGHT_PURPLE + "it into the enderchest."));
        note.setItemMeta(noteMeta);
        i.setItem(31, note);
        i.setItem(32, glass);
        i.setItem(33, glass);
        i.setItem(34, glass);
        i.setItem(35, glass);
        player.openInventory(i);
        return true;
    }

    @EventHandler
    private void inventoryclose(InventoryCloseEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getName());
        if (ChatColor.stripColor(event.getInventory().getName()).equals("EnderChest")) {
            player.sendMessage(ChatColor.GREEN + "Chest saved.");
            for (int x = 0; x < player.getEnderChest().getSize(); x++){
                player.getEnderChest().setItem(x, event.getInventory().getItem(x));
            }
        }
    }

    @EventHandler
    private void inventoryclick(InventoryClickEvent event) {
        Player player = Bukkit.getPlayer(event.getWhoClicked().getName());
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("EnderChest")) {
            return;
        }
        switch (event.getRawSlot()) {
            case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 34: case 35:
                event.setCancelled(true);
                break;
            /*case 30:
                if (!event.getCurrentItem().getType().name().contains("BOOTS")) {
                    event.setCancelled(true);
                }
                break;
            case 29:
                if (!event.getCurrentItem().getType().name().contains("CHESTPLATE")) {
                    event.setCancelled(true);
                }
                break;
            case 28:
                if (!event.getCurrentItem().getType().name().contains("LEGGINGS")) {
                    event.setCancelled(true);
                }
                break;
            case 27:
                String string = "HELMET";
                if(!player.getOpenInventory().getBottomInventory().getItem(player.getInventory().getHeldItemSlot()).getType().name().contains(string)) {
                    event.setCancelled(true);
                    Bukkit.broadcastMessage(string);
                }
                if (!event.getCurrentItem().getType().name().contains(string) && !player.getItemOnCursor().getType().toString().contains("HELMET")) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getAction().equals(InventoryAction.SWAP_WITH_CURSOR)) {
                    if (!player.getItemOnCursor().getType().toString().contains("HELMET")) {
                        event.setCancelled(true);
                        return;
                    }
                    player.getInventory().setHelmet(player.getItemOnCursor());
                }
                if (event.getAction().toString().contains("PICKUP")) {
                    player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    return;
                }
                if (event.getAction().toString().contains("PLACE")) {
                    if (!player.getItemOnCursor().getType().toString().contains("HELMET")) {
                        event.setCancelled(true);
                        return;
                    }
                    player.getInventory().setHelmet(player.getItemOnCursor());
                    return;
                }
                break;*/
        }
    }
}
