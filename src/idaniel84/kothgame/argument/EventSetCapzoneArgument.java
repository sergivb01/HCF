package idaniel84.kothgame.argument;

import idaniel84.HCF;
import idaniel84.palace.PalaceFaction;
import idaniel84.palace.PalaceTracker;
import idaniel84.HCF;
import idaniel84.palace.PalaceFaction;
import idaniel84.palace.PalaceTracker;
import idaniel84.palace.PalaceFaction;
import idaniel84.palace.PalaceTracker;
import idaniel84.faction.FactionManager;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.type.Faction;
import idaniel84.kothgame.CaptureZone;
import idaniel84.kothgame.faction.*;
import idaniel84.kothgame.tracker.ConquestTracker;
import idaniel84.kothgame.tracker.KnockKothTracker;
import idaniel84.kothgame.tracker.KothTracker;
import idaniel84.palace.PalaceFaction;
import idaniel84.palace.PalaceTracker;
import net.veilmc.util.command.CommandArgument;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventSetCapzoneArgument
		extends CommandArgument{
	private final HCF plugin;

	public EventSetCapzoneArgument(HCF plugin){
		super("setcapzone", "Sets the capture zone of an event");
		this.plugin = plugin;
		this.aliases = new String[]{"setcapturezone", "setcap", "setcappoint", "setcapturepoint", "setcappoint"};
		this.permission = "hcf.command.event.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <eventName>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		CaptureZone captureZone;
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can set KOTH arena capture points");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
		if(worldEdit == null){
			sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set KOTH capture points.");
			return true;
		}
		Selection selection = worldEdit.getSelection((Player) sender);
		if(selection == null){
			sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
			return true;
		}
		if(selection.getWidth() < 2 || selection.getLength() < 2){
			sender.sendMessage(ChatColor.RED + "Capture zones must be at least " + 2 + 'x' + 2 + '.');
			return true;
		}
		Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
		if(!(faction instanceof CapturableFaction)){
			sender.sendMessage(ChatColor.RED + "There is not a capturable faction named '" + args[1] + "'.");
			return true;
		}
		CapturableFaction capturableFaction = (CapturableFaction) faction;
		Set<Claim> claims = capturableFaction.getClaims();
		if(claims.isEmpty()){
			sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
			return true;
		}
		Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
		World world = claim.getWorld();
		int minimumX = claim.getMinimumX();
		int maximumX = claim.getMaximumX();
		int minimumZ = claim.getMinimumZ();
		int maximumZ = claim.getMaximumZ();
		FactionManager factionManager = this.plugin.getFactionManager();
		for(int x = minimumX; x <= maximumX; ++x){
			for(int z = minimumZ; z <= maximumZ; ++z){
				Faction factionAt = factionManager.getFactionAt(world, x, z);
				if(factionAt.equals(capturableFaction)) continue;
				sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
				return true;
			}
		}
		if(capturableFaction instanceof ConquestFaction){
			if(args.length < 3){
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + this.getName() + ' ' + faction.getName() + " <red|blue|green|yellow>");
				return true;
			}
			ConquestFaction conquestFaction = (ConquestFaction) capturableFaction;
			ConquestFaction.ConquestZone conquestZone = ConquestFaction.ConquestZone.getByName(args[2]);
			if(conquestZone == null){
				sender.sendMessage(ChatColor.RED + "There is no conquest zone named '" + args[2] + "'.");
				sender.sendMessage(ChatColor.RED + "Did you mean?: " + StringUtils.join(ConquestFaction.ConquestZone.getNames(), ", "));
				return true;
			}
			captureZone = new CaptureZone(conquestZone.getName(), conquestZone.getColor().toString(), claim, ConquestTracker.DEFAULT_CAP_MILLIS);
			conquestFaction.setZone(conquestZone, captureZone);
		}else if(capturableFaction instanceof KothFaction){
			captureZone = new CaptureZone(capturableFaction.getName(), claim, KothTracker.DEFAULT_CAP_MILLIS);
			((KothFaction) capturableFaction).setCaptureZone(captureZone);
		}else if(capturableFaction instanceof PalaceFaction) {
			captureZone = new CaptureZone(capturableFaction.getName(), claim, PalaceTracker.DEFAULT_CAP_MILLIS1);
			((PalaceFaction) capturableFaction).setCaptureZone(captureZone);
		} else if(capturableFaction instanceof KnockKothFaction) {
			captureZone = new CaptureZone(capturableFaction.getName(), claim, KnockKothTracker.DEFAULT_CAP_MILLIS);
			((KnockKothFaction) capturableFaction).setCaptureZone(captureZone);
		}else{
			sender.sendMessage(ChatColor.RED + "Unexpected error");
			return false;
		}
		sender.sendMessage(ChatColor.YELLOW + "Set capture zone " + captureZone.getDisplayName() + ChatColor.YELLOW + " for faction " + faction.getName() + ChatColor.YELLOW + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		switch(args.length){
			case 2:{
				return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
			}
			case 3:{
				Faction faction2 = this.plugin.getFactionManager().getFaction(args[1]);
				if(faction2 instanceof ConquestFaction){
					ConquestFaction.ConquestZone[] zones = ConquestFaction.ConquestZone.values();
					ArrayList<String> results = new ArrayList<String>(zones.length);
					for(ConquestFaction.ConquestZone zone : zones){
						results.add(zone.name());
					}
					return results;
				}
				return Collections.emptyList();
			}
		}
		return Collections.emptyList();
	}
}

