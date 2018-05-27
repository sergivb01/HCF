package net.veilmc.hcf.command;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.google.common.primitives.Ints;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.JavaUtils;
import net.veilmc.util.chat.ClickAction;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SumoEventCommand implements CommandExecutor {

    private HCF plugin;
    public SumoEventCommand(HCF plugin) {
        this.plugin = plugin;
    }

    private static final int[] ALERT_SECONDS = new int[]{14400, 7200, 1800, 600, 300, 270, 240, 210, 180, 150, 120, 90, 60, 40, 30, 20, 15, 10, 5, 3, 1};
    private static final long TICKS_DAY = TimeUnit.DAYS.toMillis(1);
    private long current = Long.MIN_VALUE;
    private BukkitTask task;
    private boolean isPendingSumo(){
        return this.task != null && this.current != Long.MIN_VALUE;
    }
    public long getRemainingTicks(){
        return this.current - (long) MinecraftServer.currentTick;
    }
    private static ArrayList<Player> queue = new ArrayList<Player>();
    private static Location location;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command!");
            return true;
        }
        Player player = (Player) sender;
        if (!ConfigurationService.KIT_MAP) {
            player.sendMessage(ChatColor.RED + "You can only use this command on KitMap!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.BLUE + "Queued players: " + queue.size());
            if (player.hasPermission("hcf.sumoevent.admin")) {
                player.sendMessage(ChatColor.BLUE + "/sumoevent <minutes> to start the event. Players will be teleported to the location where you executed the commmand.");
                player.sendMessage(ChatColor.BLUE + "/sumoevent cancel to cancel events.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (queue.contains(player)) {
                player.sendMessage(ChatColor.RED + "You are already in the queue!");
                return true;
            }
            if (plugin.getTimerManager().spawnTagTimer.getRemaining(player) > 0) {
                player.sendMessage(ChatColor.RED + "You cannot join events while spawntagged.");
                return true;
            }
            if (!this.isPendingSumo()) {
                player.sendMessage(ChatColor.RED + "There isn't currently a sumo event.");
                return true;
            }
            queue.add(player);
            player.sendMessage(ChatColor.BLUE + "You have joined the queue for sumo event.");
            return true;
        }
        if (player.hasPermission("hcf.sumoevent.admin")) {
            if (args[0].equalsIgnoreCase("cancel")) {
                if(this.isPendingSumo()){
                    queue.clear();
                    this.task.cancel();
                    this.task = null;
                    this.current = Long.MIN_VALUE;
                    player.sendMessage(ChatColor.BLUE + "Sumo event cancelled.");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "There are no events.");
                return true;
            }
            if(args.length != 1){
                sender.sendMessage(ChatColor.RED + "Usage: /sumoevent <time>");
                return true;
            }
            long millis = JavaUtils.parse(args[0]);
            if(millis == -1 || (!args[0].contains("m") && !args[0].contains("s"))){
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }
            if (this.isPendingSumo()) {
                player.sendMessage(ChatColor.RED + "There is currently an active Sumo queue. Type </sumoevent cancel> to cancel it.");
                return true;
            }
            location = player.getLocation();
            player.sendMessage(ChatColor.BLUE + "Sumo will start in " + DurationFormatUtils.formatDurationWords(millis, true, true) + ". Use </sumoevent join> to play the event.");
            player.sendMessage(ChatColor.RED + "NOTE: All queued players will be teleported to the location where you executed the command. If the location is wrong, type </sumoevent cancel>");
            this.current = (long) (MinecraftServer.currentTick + 20) + millis / 50;
            this.task = new BukkitRunnable(){
                public void run(){
                    long remainingTicks;
                    if((remainingTicks = getRemainingTicks()) <= 0){
                        this.cancel();
                        queue.forEach(Player -> Player.teleport(location));
                        Bukkit.broadcastMessage(ChatColor.BLUE + "All queued players have been teleported to the event location.");
                        queue.clear();
                        Bukkit.dispatchCommand(player, "sumoevent cancel");
                        return;
                    }
                    long remainingMillis = remainingTicks * 50;
                    if(Ints.contains(ALERT_SECONDS, (int) (remainingMillis / 1000))){
                        Bukkit.broadcastMessage(" ");
                        Bukkit.broadcastMessage(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Sumo Event");
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            new net.veilmc.util.chat.Text(ChatColor.BLUE + "Click here to play the event before the timer ends or type ").setHoverText("Join Event").setClick(ClickAction.RUN_COMMAND, "/sumoevent join").send(all); new net.veilmc.util.chat.Text(ChatColor.BLUE + "</sumoevent join>. Time: " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(remainingMillis, true, true)).setHoverText("Join Event").setClick(ClickAction.RUN_COMMAND, "/sumoevent join").send(all);
                        }
                        Bukkit.broadcastMessage(" ");
                    }
                }
            }.runTaskTimer(this.plugin, 20L, 20L);
        }
        return true;
    }
}
