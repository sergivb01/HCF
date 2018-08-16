package idaniel84.faction.type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import idaniel84.HCF;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.event.FactionClaimChangeEvent;
import idaniel84.faction.event.FactionClaimChangedEvent;
import idaniel84.faction.event.cause.ClaimChangeCause;
import idaniel84.HCF;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.event.FactionClaimChangeEvent;
import idaniel84.faction.event.FactionClaimChangedEvent;
import idaniel84.faction.event.cause.ClaimChangeCause;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.event.FactionClaimChangeEvent;
import idaniel84.faction.event.FactionClaimChangedEvent;
import idaniel84.faction.event.cause.ClaimChangeCause;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.event.FactionClaimChangeEvent;
import idaniel84.faction.event.FactionClaimChangedEvent;
import idaniel84.faction.event.cause.ClaimChangeCause;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.GenericUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.*;

public class ClaimableFaction
		extends Faction{
	protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS =
			(ImmutableMap.of(World.Environment.NETHER, "Nether", World.Environment.NORMAL, "Overworld", World.Environment.THE_END, "The End"));
	protected final Set<Claim> claims = new HashSet<Claim>();

	public ClaimableFaction(String name){
		super(name);
	}

	public ClaimableFaction(Map<String, Object> map){
		super(map);
		this.claims.addAll(GenericUtils.createList(map.get("claims"), (Class) Claim.class));
	}

	@Override
	public Map<String, Object> serialize(){
		Map<String, Object> map = super.serialize();
		map.put("claims", new ArrayList<Claim>(this.claims));
		return map;
	}

	@Override
	public void printDetails(CommandSender sender){
        /*sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
        sender.sendMessage(this.getDisplayName(sender));
        for (Claim claim : this.claims) {
            Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.WHITE.toString() + ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7&m" + BukkitUtils.STRAIGHT_LINE_DEFAULT));*/
		List<String> toSend = new ArrayList<>();


		for(String string : HCF.getInstance().getConfig().getStringList("faction-settings.show.system-faction")){

			string = string.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT + "");
			string = string.replace("%FACTION%", this.getDisplayName(sender));


			if(string.contains("%WORLD%")){
				for(Claim claim : this.claims){
					Location location = claim.getCenter();
					string = string.replace("%WORLD%", ENVIRONMENT_MAPPINGS.get(location.getWorld().getEnvironment()) + "");
				}
			}
			if(string.contains("%X%")){
				for(Claim claim : this.claims){
					Location location = claim.getCenter();
					string = string.replace("%X%", location.getBlockX() + "");
				}
			}
			if(string.contains("%Z%")){
				for(Claim claim : this.claims){
					Location location = claim.getCenter();
					string = string.replace("%Z%", location.getBlockZ() + "");
				}
			}

			toSend.add(string);
		}

		for(String message : toSend){
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		}
	}

	public Set<Claim> getClaims(){
		return this.claims;
	}

	public boolean addClaim(Claim claim, CommandSender sender){
		return this.addClaims(Collections.singleton(claim), sender);
	}

	public boolean addClaims(Collection<Claim> adding, CommandSender sender){
		if(sender == null){
			sender = Bukkit.getConsoleSender();
		}
		FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled() || !this.claims.addAll(adding)){
			return false;
		}
		Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
		return true;
	}

	public boolean removeClaim(Claim claim, CommandSender sender){
		return this.removeClaims(Collections.singleton(claim), sender);
	}

	public boolean removeClaims(Collection<Claim> removing, CommandSender sender){
		if(sender == null){
			sender = Bukkit.getConsoleSender();
		}
		int previousClaims = this.claims.size();
		FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
		Bukkit.getPluginManager().callEvent(event);

		if(event.isCancelled()){
			return false;
		}
		for(Claim claim : Lists.newArrayList(removing)){
			while(claims.contains(claim)){
				claims.remove(claim);
			}
		}
		if(this instanceof PlayerFaction){
			PlayerFaction playerFaction = (PlayerFaction) this;
			Location home = playerFaction.getHome();
			HCF plugin = HCF.getPlugin();
			int refund = 0;
			for(Claim claim : removing){
				refund += plugin.getClaimHandler().calculatePrice(claim, previousClaims, true);
				if(previousClaims > 0){
					--previousClaims;
				}
				if(home == null || !claim.contains(home)) continue;
				playerFaction.setHome(null);
				playerFaction.broadcast(ChatColor.RED.toString() + ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
				break;
			}
			plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueId(), refund);
			playerFaction.broadcast(ChatColor.YELLOW + "Faction leader was refunded " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " due to a land unclaim.");
		}
		Bukkit.getPluginManager().callEvent(new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
		return true;
	}
}

