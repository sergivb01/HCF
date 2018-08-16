package net.veilmc.hcf.command;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.google.common.primitives.Ints;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.FFAFaction;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.BukkitUtils;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FfaEventCommand implements CommandExecutor {

    private HCF plugin;
    public FfaEventCommand(HCF plugin) {
        this.plugin = plugin;
    }

    private static final int[] ALERT_SECONDS = new int[]{14400, 7200, 1800, 600, 300, 270, 240, 210, 180, 150, 135, 120, 100, 90, 70, 60, 50, 40, 30, 20, 15, 10, 5, 3, 2, 1};
    private static final long TICKS_DAY = TimeUnit.DAYS.toMillis(1);
    private long current = Long.MIN_VALUE;
    private BukkitTask task;
    private boolean isPendingFfa(){
        return this.task != null && this.current != Long.MIN_VALUE;
    }
    private long getRemainingTicks(){
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
            player.sendMessage(BukkitUtils.STRAIGHT_LINE_DEFAULT);
            player.sendMessage(ChatColor.BLUE + "Queued players: " + queue.size());
            if (player.hasPermission("hcf.ffaevent.admin")) {
                player.sendMessage(ChatColor.BLUE + "/ffaevent <minutes> to start the event. Players will be teleported to the location where you executed the commmand.");
                player.sendMessage(ChatColor.BLUE + "/ffaevent cancel to cancel events.");
            }
            player.sendMessage(BukkitUtils.STRAIGHT_LINE_DEFAULT);
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
            if (!this.isPendingFfa()) {
                player.sendMessage(ChatColor.RED + "There isn't currently a sumo event.");
                return true;
            }
            queue.add(player);
            player.sendMessage(ChatColor.BLUE + "You have joined the queue for ffa event.");
            return true;
        }
        if (player.hasPermission("hcf.ffaevent.admin")) {
            if (args[0].equalsIgnoreCase("cancel")) {
                if(this.isPendingFfa()){
                    queue.clear();
                    this.task.cancel();
                    this.task = null;
                    this.current = Long.MIN_VALUE;
                    player.sendMessage(ChatColor.BLUE + "Ffa event cancelled.");
                    return true;
                }
                player.sendMessage(ChatColor.RED + "There are no events.");
                return true;
            }
            Faction playerFactionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            if (!(playerFactionAt instanceof FFAFaction)) {
                player.sendMessage(ChatColor.RED + "You can only execute this command on FFA faction.");
                return true;
            }
            if(args.length != 1){
                sender.sendMessage(ChatColor.RED + "Usage: /ffaevent <time>");
                return true;
            }
            long millis = JavaUtils.parse(args[0]);
            if(millis == -1 || (!args[0].contains("m") && !args[0].contains("s"))){
                sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m1s");
                return true;
            }
            if (this.isPendingFfa()) {
                player.sendMessage(ChatColor.RED + "There is currently an active Ffa queue. Type </ffaevent cancel> to cancel it.");
                return true;
            }
            location = player.getLocation();
            player.sendMessage(ChatColor.BLUE + "Ffa will start in " + DurationFormatUtils.formatDurationWords(millis, true, true) + ". Use </ffaevent join> to play the event.");
            player.sendMessage(ChatColor.RED + "NOTE: All queued players will be teleported to the location where you executed the command. If the location is wrong, type </ffaevent cancel>");
            this.current = (long) (MinecraftServer.currentTick + 20) + millis / 50;
            this.task = new BukkitRunnable(){
                public void run(){
                    long remainingTicks;
                    if((remainingTicks = getRemainingTicks()) <= 0){
                        this.cancel();
                        queue.forEach(Player -> {
                            Player.teleport(location);
                            Player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (10 * 60) * 20, 1));
                            Player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (10 * 60) * 20, 0));
                            Player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (10 * 60) * 20, 0));
                        });
                        Bukkit.broadcastMessage(ChatColor.BLUE + "All queued players have been teleported to the event location.");
                        queue.clear();
                        Bukkit.dispatchCommand(player, "ffaevent cancel");
                        return;
                    }
                    long remainingMillis = remainingTicks * 50;
                    if(Ints.contains(ALERT_SECONDS, (int) (remainingMillis / 1000))){
                        Bukkit.broadcastMessage(" ");
                        Bukkit.broadcastMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "FFA Event");
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            new net.veilmc.util.chat.Text(ChatColor.AQUA + "Click here to play the event before the timer ends or type ").setHoverText("Join Event").setClick(ClickAction.RUN_COMMAND, "/ffaevent join").send(all); new net.veilmc.util.chat.Text(ChatColor.AQUA + "</ffaevent join>. Time: " + ChatColor.WHITE + DurationFormatUtils.formatDurationWords(remainingMillis, true, true)).setHoverText("Join Event").setClick(ClickAction.RUN_COMMAND, "/ffaevent join").send(all);
                        }
                        Bukkit.broadcastMessage(" ");
                    }
                }
            }.runTaskTimer(this.plugin, 20L, 20L);
        }
        return true;
    }
}
