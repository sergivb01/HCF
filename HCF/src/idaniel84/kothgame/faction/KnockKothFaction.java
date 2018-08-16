package idaniel84.kothgame.faction;

import com.google.common.collect.ImmutableList;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.kothgame.EventType;
import idaniel84.HCF;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.kothgame.CaptureZone;
import idaniel84.kothgame.EventType;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class KnockKothFaction extends CapturableFaction implements ConfigurationSerializable {

    private CaptureZone captureZone;

    public KnockKothFaction(String name){
        super(name);
        this.setDeathban(true);
    }

    public KnockKothFaction(Map<String, Object> map){
        super(map);
        this.setDeathban(true);
        this.captureZone = (CaptureZone) map.get("captureZone");
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> map = super.serialize();
        map.put("captureZone", this.captureZone);
        return map;
    }

    @Override
    public List<CaptureZone> getCaptureZones(){
        return this.captureZone == null ? ImmutableList.of() : ImmutableList.of(this.captureZone);
    }

    @Override
    public EventType getEventType(){
        return EventType.KNOCK;
    }

    @Override
    public void printDetails(CommandSender sender){
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        sender.sendMessage(this.getDisplayName(sender));
        for(Claim claim : this.claims){
            Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '(' + ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
        }
        if(this.captureZone != null){
            long remainingCaptureMillis = this.captureZone.getRemainingCaptureMillis();
            long defaultCaptureMillis = this.captureZone.getDefaultCaptureMillis();
            if(remainingCaptureMillis > 0 && remainingCaptureMillis != defaultCaptureMillis){
                sender.sendMessage(ChatColor.YELLOW + "  Remaining Time: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
            }
            sender.sendMessage(ChatColor.YELLOW + "  Capture Delay: " + ChatColor.RED + this.captureZone.getDefaultCaptureWords());
            if(this.captureZone.getCappingPlayer() != null && sender.hasPermission("hcf.koth.checkcapper")){
                PlayerFaction playerFaction;
                Player capping = this.captureZone.getCappingPlayer();
                String factionTag = "[" + ((playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(capping)) == null ? "*" : playerFaction.getName()) + "]";
                sender.sendMessage(ChatColor.YELLOW + "  Current Capper: " + ChatColor.RED + capping.getName() + ChatColor.GOLD + factionTag);
            }
        }
        sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }

    public CaptureZone getCaptureZone(){
        return this.captureZone;
    }

    public void setCaptureZone(CaptureZone captureZone){
        this.captureZone = captureZone;
    }
}
