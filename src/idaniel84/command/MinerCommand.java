package idaniel84.command;

import net.veilmc.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) sender.sendMessage(ChatColor.RED + "Try using /miner | /miner <player>");
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + " * Miner levels info:");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "» Level 1 (250 Diamonds)");
            sender.sendMessage(ChatColor.WHITE + " Speed I, Haste II");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "» Level 2 (500 Diamonds)");
            sender.sendMessage(ChatColor.WHITE + " Speed I, Haste IV");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "» Level 3 (750 Diamonds)");
            sender.sendMessage(ChatColor.WHITE + " Speed I, Haste IV, Resistance I");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "» Level 4 (1000 Diamonds)");
            sender.sendMessage(ChatColor.WHITE + " Speed I, Haste IV, Resistance I, Saturation I");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.AQUA + "» Level 5 (2000 Diamonds)");
            sender.sendMessage(ChatColor.WHITE + " Speed I, Haste IV, Resistance I, Saturation I, Regeneration I");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.DARK_AQUA + "Type /miner <" + sender.getName() + "> to see your miner level.");
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);

        }
        if (args.length == 1) {
            OfflinePlayer p = Bukkit.getServer().getPlayer(args[0]);
            if (p == null || !p.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Invalid player.");
                return true;
            }
            Integer diamonds = p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            if (diamonds < 250) sender.sendMessage(ChatColor.BLUE + p.getName() + "'s Miner level: 0 (Default effects)");
            if (diamonds >= 250 && diamonds < 500) sender.sendMessage(ChatColor.BLUE + p.getName() + "'s Miner level: 1");
            if (diamonds >= 500 && diamonds < 750) sender.sendMessage(ChatColor.BLUE + p.getName() + "'s Miner level: 2");
            if (diamonds >= 750 && diamonds < 1000) sender.sendMessage(ChatColor.BLUE + p.getName() + "'s Miner level: 3");
            if (diamonds >= 1000 && diamonds < 2000) sender.sendMessage(ChatColor.BLUE + p.getName() + "'s Miner level: 4");
            if (diamonds >= 2000) sender.sendMessage(ChatColor.BLUE + p.getName() + "'s Miner level: 5");
            sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            return true;
        }
        return false;
    }
}
