package net.veilmc.hcf.kothgame.tracker;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.kothgame.CaptureZone;
import net.veilmc.hcf.kothgame.EventTimer;
import net.veilmc.hcf.kothgame.EventType;
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.hcf.kothgame.faction.KnockKothFaction;
import net.veilmc.hcf.utils.DateTimeFormats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class KnockKothTracker
        implements EventTracker{
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static long lastMillis;

    static{
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(10);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(30);
    }

    private final HCF plugin;

    public KnockKothTracker(HCF plugin){
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType(){
        return EventType.KNOCK;
    }

    @Override
    public void tick(EventTimer eventTimer, EventFaction eventFaction){
        CaptureZone captureZone = ((KnockKothFaction) eventFaction).getCaptureZone();
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis <= 0){
            this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if(remainingMillis == captureZone.getDefaultCaptureMillis()){
            return;
        }
        int remainingSeconds = (int) (remainingMillis / 1000);
        if(remainingSeconds > 0 && remainingSeconds % 30 == 0){
            Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getEventType().getDisplayName() + "§8] " + ChatColor.GOLD + "Someone §eis controlling " + ChatColor.GOLD + captureZone.getDisplayName() + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }

    @Override
    public void onContest(EventFaction eventFaction, EventTimer eventTimer){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth setcapdelay " + eventFaction.getName() + " " + TimeUnit.MILLISECONDS.toSeconds(KnockKothTracker.DEFAULT_CAP_MILLIS) + "s");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "§8[§6§l" + eventFaction.getEventType().getDisplayName() + "§8] " + ChatColor.GOLD + eventFaction.getName() + ChatColor.YELLOW + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }

    @Override
    public boolean onControlTake(Player player, CaptureZone captureZone){
        player.sendMessage(ChatColor.YELLOW + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.YELLOW + '.');
        lastMillis = captureZone.getRemainingCaptureMillis();
        return true;
    }

    public boolean onControlLoss(Player player, CaptureZone captureZone, EventFaction eventFaction){
        player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        long remainingMillis = captureZone.getRemainingCaptureMillis();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "koth setcapdelay " + eventFaction.getName() + " " + TimeUnit.MILLISECONDS.toSeconds(captureZone.getRemainingCaptureMillis()) + "s");
        if((remainingMillis > 0L) && (lastMillis - remainingMillis > MINIMUM_CONTROL_TIME_ANNOUNCE)){
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }

    @Override
    public void stopTiming(){
    }
}
