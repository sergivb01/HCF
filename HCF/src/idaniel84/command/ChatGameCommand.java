package idaniel84.command;

import idaniel84.HCF;
import idaniel84.faction.FactionMember;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.utils.ConfigurationService;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatGameCommand implements CommandExecutor, Listener {

    private static String answer_question;
    private static String answer_shuffle;

    private static String type;

    private static String question_guess;
    private static String question_shuffle;

    private static double start;
    private static boolean isActive;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (isActive == true && args.length >= 2 && (args[0].equalsIgnoreCase("shuffle") || args[0].equalsIgnoreCase("type") || args[0].equalsIgnoreCase("guess"))) {
            sender.sendMessage(ChatColor.RED + "There is currently an active ChatGame");
            if (sender.isOp()) sender.sendMessage(ChatColor.RED + "Answers: " + answer_shuffle + ", " + type + ", " + answer_question);
            return false;
        }
        if (args.length <= 1 || ((args[0].equalsIgnoreCase("shuffle") || args[0].equalsIgnoreCase("type")) && !(args.length == 2)) || (args[0].equalsIgnoreCase("guess") && args.length <= 2)) {
            sender.sendMessage(ChatColor.BOLD + " * " + ChatColor.BLUE + "Chatgame commands:");
            sender.sendMessage(ChatColor.WHITE + " » " + ChatColor.AQUA + "/chatgame type <word>");
            sender.sendMessage(ChatColor.WHITE + " » " + ChatColor.AQUA + "/chatgame shuffle <word>");
            sender.sendMessage(ChatColor.WHITE + " » " + ChatColor.AQUA + "/chatgame guess <answer> <question>");
            return false;
        }
        if (args[0].equalsIgnoreCase("shuffle") && args.length == 2) {
            answer_shuffle = args[1];
            List<String> a = Arrays.asList(answer_shuffle.split(""));
            Collections.shuffle(a);
            question_shuffle = "";
            for (String b : a) {
                question_shuffle += b;
            }
            start = System.currentTimeMillis();
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + "First to unscramble the word " + ChatColor.AQUA + question_shuffle + ChatColor.BLUE + " wins!");
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            isActive = true;
            return true;
        }
        if (args[0].equalsIgnoreCase("type") && args.length == 2) {
            type = args[1];
            start = System.currentTimeMillis();
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE.toString() + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + "First to type the word " + ChatColor.AQUA + type + ChatColor.BLUE + " wins!");
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            isActive = true;
            return true;
        }
        if ((args[0].equalsIgnoreCase("guess")) && (args.length >= 3)) {
            answer_question = args[1];
            start = System.currentTimeMillis();
            question_guess = "";
            for (int i = 2; i < args.length; i++) {
                String arg = args[i] + " ";
                question_guess += arg;
            }
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + question_guess);
            Bukkit.broadcastMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            isActive = true;
            return true;
        }
        return false;
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String player = e.getPlayer().getName();
        String message = e.getMessage();
        NumberFormat formatter = new DecimalFormat("#.##");
        if (isActive == true) {
            if (message.equalsIgnoreCase(answer_shuffle)) {
                double end = System.currentTimeMillis();
                String time = formatter.format((end - start) / 1000.0D);
                if (ConfigurationService.VEILZ) {
                    Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + player + " has won " + ChatColor.GREEN + "$70" + ChatColor.BLUE + " for unscrambling " + ChatColor.RED + question_shuffle + ChatColor.BLUE + " in " + ChatColor.RED + time + ChatColor.BLUE + " seconds. " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + answer_shuffle + ChatColor.DARK_GRAY + ")");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + player + " give 70");
                    setNull();
                    return;
                }
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + player + " has won " + ChatColor.GREEN + "$200" + ChatColor.BLUE + " for unscrambling " + ChatColor.RED + question_shuffle + ChatColor.BLUE + " in " + ChatColor.RED + time + ChatColor.BLUE + " seconds. " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + answer_shuffle + ChatColor.DARK_GRAY + ")");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + player + " give 200");
                setNull();
            }
            if (message.equalsIgnoreCase(type)) {
                double end = System.currentTimeMillis();
                String time = formatter.format((end - start) / 1000.0D);
                if (ConfigurationService.VEILZ) {
                    Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + player + " has won " + ChatColor.GREEN + "$30" + ChatColor.BLUE + " for typing " + ChatColor.RED + type + ChatColor.BLUE + " in " + ChatColor.RED + time + ChatColor.BLUE + " seconds.");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + player + " give 30");
                    setNull();
                    return;
                }
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + player + " has won " + ChatColor.GREEN + "$100" + ChatColor.BLUE + " for typing " + ChatColor.RED + type + ChatColor.BLUE + " in " + ChatColor.RED + time + ChatColor.BLUE + " seconds.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + player + " give 100");
                setNull();
            }
            if (message.equalsIgnoreCase(answer_question)) {
                double end = System.currentTimeMillis();
                String time = formatter.format((end - start) / 1000.0D);
                if (ConfigurationService.VEILZ) {
                    Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + player + " has won " + ChatColor.GREEN + "$50" + ChatColor.BLUE + " for guessing " + ChatColor.AQUA + question_guess + ChatColor.BLUE + " in " + ChatColor.RED + time + ChatColor.BLUE + " seconds. " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + answer_question + ChatColor.DARK_GRAY + ")");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + player + " give 50");
                    setNull();
                    return;
                }
                Bukkit.broadcastMessage(ChatColor.BOLD.toString() + ChatColor.WHITE + ChatColor.BOLD + " CHATGAME " + ChatColor.BLUE + player + " has won " + ChatColor.GREEN + "$150" + ChatColor.BLUE + " for guessing " + ChatColor.AQUA + question_guess + ChatColor.BLUE + " in " + ChatColor.RED + time + ChatColor.BLUE + " seconds. " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + answer_question + ChatColor.DARK_GRAY + ")");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + player + " give 150");
                setNull();
            }
        }
    }
    private void setNull() {
        answer_shuffle = null;
        type = null;
        answer_question = null;
        isActive = false;
    }
}
