package net.veilmc.hcf.faction.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.hcf.faction.FactionMember;
import net.veilmc.hcf.faction.event.FactionDtrChangeEvent;
import net.veilmc.hcf.faction.event.PlayerJoinedFactionEvent;
import net.veilmc.hcf.faction.event.PlayerLeaveFactionEvent;
import net.veilmc.hcf.faction.event.PlayerLeftFactionEvent;
import net.veilmc.hcf.faction.event.cause.FactionLeaveCause;
import net.veilmc.hcf.faction.struct.Raidable;
import net.veilmc.hcf.faction.struct.RegenStatus;
import net.veilmc.hcf.faction.struct.Relation;
import net.veilmc.hcf.faction.struct.Role;
import net.veilmc.hcf.timer.type.TeleportTimer;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.GenericUtils;
import net.veilmc.util.JavaUtils;
import net.veilmc.util.PersistableLocation;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.ChatColor.RED;

public class PlayerFaction
		extends ClaimableFaction
		implements Raidable{
	private static final UUID[] EMPTY_UUID_ARRAY = new UUID[0];
	protected final Map requestedRelations = new HashMap();
	protected final Map relations = new HashMap();
	protected final Map members = new HashMap();
	protected final Set<String> invitedPlayerNames;
	protected PersistableLocation home;
	protected String announcement;
	protected UUID focus;
	protected boolean open;
	protected int balance;
	protected double deathsUntilRaidable;
	protected long regenCooldownTimestamp;
	private long lastDtrUpdateTimestamp;
	private transient String focused;

	public PlayerFaction(String name){
		super(name);
		this.invitedPlayerNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
		this.deathsUntilRaidable = 1.0D;
	}

	public PlayerFaction(Map map){
		super(map);
		this.invitedPlayerNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
		this.deathsUntilRaidable = 1.0D;
		Iterator object = GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet().iterator();
		while(object.hasNext()){
			Map.Entry entry = (Map.Entry) object.next();
			this.members.put(UUID.fromString((String) entry.getKey()), entry.getValue());
		}
		this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));
		Object object1 = map.get("home");
		if(object1 != null){
			this.home = ((PersistableLocation) object1);
		}
		object1 = map.get("announcement");
		if(object1 != null){
			this.announcement = ((String) object1);
		}
		Iterator entry2 = GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet().iterator();
		while(entry2.hasNext()){
			Map.Entry entry1 = (Map.Entry) entry2.next();
			this.relations.put(UUID.fromString((String) entry1.getKey()), Relation.valueOf((String) entry1.getValue()));
		}
		entry2 = GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet().iterator();
		while(entry2.hasNext()){
			Map.Entry entry1 = (Map.Entry) entry2.next();
			this.requestedRelations.put(UUID.fromString((String) entry1.getKey()), Relation.valueOf((String) entry1.getValue()));
		}
		this.open = ((Boolean) map.get("open")).booleanValue();
		this.balance = ((Integer) map.get("balance")).intValue();
		this.deathsUntilRaidable = ((Double) map.get("deathsUntilRaidable")).doubleValue();
		this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
		this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
	}

	public static String format(String format){
		return ChatColor.translateAlternateColorCodes('&', format);
	}

	public String getFocused(){
		return focused;
	}

	public void setFocused(String focused){
		this.focused = focused;
	}

	public Map serialize(){
		Map map = super.serialize();
		HashMap relationSaveMap = new HashMap(this.relations.size());
		Iterator requestedRelationsSaveMap = this.relations.entrySet().iterator();
		while(requestedRelationsSaveMap.hasNext()){
			Map.Entry entrySet = (Map.Entry) requestedRelationsSaveMap.next();
			relationSaveMap.put(((UUID) entrySet.getKey()).toString(), ((Relation) entrySet.getValue()).name());
		}
		map.put("relations", relationSaveMap);
		HashMap requestedRelationsSaveMap1 = new HashMap(this.requestedRelations.size());
		Iterator entrySet1 = this.requestedRelations.entrySet().iterator();
		while(entrySet1.hasNext()){
			Map.Entry saveMap = (Map.Entry) entrySet1.next();
			requestedRelationsSaveMap1.put(saveMap.getKey().toString(), ((Relation) saveMap.getValue()).name());
		}
		map.put("requestedRelations", requestedRelationsSaveMap1);
		Set entrySet2 = this.members.entrySet();
		LinkedHashMap saveMap1 = new LinkedHashMap(this.members.size());
		Iterator var6 = entrySet2.iterator();
		while(var6.hasNext()){
			Map.Entry entry = (Map.Entry) var6.next();
			saveMap1.put(entry.getKey().toString(), entry.getValue());
		}
		map.put("members", saveMap1);
		map.put("invitedPlayerNames", new ArrayList(this.invitedPlayerNames));
		if(this.home != null){
			map.put("home", this.home);
		}
		if(this.announcement != null){
			map.put("announcement", this.announcement);
		}
		map.put("open", Boolean.valueOf(this.open));
		map.put("balance", Integer.valueOf(this.balance));
		map.put("deathsUntilRaidable", Double.valueOf(this.deathsUntilRaidable));
		map.put("regenCooldownTimestamp", Long.toString(this.regenCooldownTimestamp));
		map.put("lastDtrUpdateTimestamp", Long.toString(this.lastDtrUpdateTimestamp));
		return map;
	}

	public boolean setMember(UUID playerUUID, FactionMember factionMember){
		return setMember(null, playerUUID, factionMember, false);
	}

	public boolean setMember(UUID playerUUID, FactionMember factionMember, boolean force){
		return setMember(null, playerUUID, factionMember, force);
	}

	public boolean setMember(Player player, FactionMember factionMember){
		return setMember(player, player.getUniqueId(), factionMember, false);
	}

	public boolean setMember(Player player, FactionMember factionMember, boolean force){
		return setMember(player, player.getUniqueId(), factionMember, force);
	}

	private boolean setMember(Player player, UUID playerUUID, FactionMember factionMember, boolean force){
		if(factionMember == null){
			if(!force){
				PlayerLeaveFactionEvent event = player == null ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
				Bukkit.getPluginManager().callEvent(event);
				if(event.isCancelled()){
					return false;
				}
			}
			this.members.remove(playerUUID);
			setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, getMaximumDeathsUntilRaidable()));
			PlayerLeftFactionEvent event2 = player == null ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
			Bukkit.getPluginManager().callEvent(event2);
			return true;
		}
		PlayerJoinedFactionEvent eventPre = player == null ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
		Bukkit.getPluginManager().callEvent(eventPre);
		this.lastDtrUpdateTimestamp = System.currentTimeMillis();
		this.invitedPlayerNames.remove(factionMember.getName());
		this.members.put(playerUUID, factionMember);
		return true;
	}

	public Collection<UUID> getAllied(){
		return (Collection<UUID>) Maps.filterValues(this.relations, new Predicate<Relation>(){
			public boolean apply(@Nullable final Relation relation){
				return relation == Relation.ALLY;
			}
		}).keySet();
	}

	public List<PlayerFaction> getAlliedFactions(){
		Collection<UUID> allied = getAllied();
		Iterator<UUID> iterator = allied.iterator();
		List<PlayerFaction> results = new ArrayList(allied.size());
		while(iterator.hasNext()){
			Faction faction = HCF.getPlugin().getFactionManager().getFaction(iterator.next());
			if((faction instanceof PlayerFaction)){
				results.add((PlayerFaction) faction);
			}else{
				iterator.remove();
			}
		}
		return results;
	}

	public Map<UUID, Relation> getRequestedRelations(){
		return this.requestedRelations;
	}

	public Map<UUID, Relation> getRelations(){
		return this.relations;
	}

	public Map<UUID, FactionMember> getMembers(){
		return ImmutableMap.copyOf(this.members);
	}

	public Set<Player> getOnlinePlayers(){
		return getOnlinePlayers(null);
	}

	public Set getOnlinePlayers(CommandSender sender){
		Set entrySet = getOnlineMembers(sender).entrySet();
		HashSet<Player> results = new HashSet(entrySet.size());
		Iterator var4 = entrySet.iterator();
		while(var4.hasNext()){
			Map.Entry entry = (Map.Entry) var4.next();
			results.add(Bukkit.getPlayer((UUID) entry.getKey()));
		}
		return results;
	}

	public Map getOnlineMembers(){
		return getOnlineMembers(null);
	}

	public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender){
		Player senderPlayer = (sender instanceof Player) ? (Player) sender : null;
		HashMap<UUID, FactionMember> results = new HashMap();
		Iterator<Map.Entry<UUID, FactionMember>> iterator = this.members.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<UUID, FactionMember> entry = iterator.next();
			Player target = Bukkit.getPlayer(entry.getKey());
			if((target != null) && (
					(senderPlayer == null) || (senderPlayer.canSee(target)))){
				results.put(entry.getKey(), entry.getValue());
			}
		}
		return results;
	}

	public FactionMember getLeader(){
		Map<UUID, FactionMember> members = this.members;
		Iterator<Map.Entry<UUID, FactionMember>> iterator = members.entrySet().iterator();
		Map.Entry<UUID, FactionMember> entry;
		do{
			if(!iterator.hasNext()){
				return null;
			}
		}while((entry = iterator.next()).getValue().getRole() != Role.LEADER);
		return entry.getValue();
	}

	@Deprecated
	public FactionMember getMember(String memberName){
		UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
		if(uuid == null){
			return null;
		}
		FactionMember factionMember = (FactionMember) this.members.get(uuid);
		return factionMember;
	}

	public FactionMember getMember(Player player){
		return getMember(player.getUniqueId());
	}

	public FactionMember getMember(UUID memberUUID){
		return (FactionMember) this.members.get(memberUUID);
	}

	public Set<String> getInvitedPlayerNames(){
		return this.invitedPlayerNames;
	}

	public Location getHome(){
		return this.home == null ? null : this.home.getLocation();
	}

	public void setHome(Location home){
		if((home == null) && (this.home != null)){
			TeleportTimer timer = HCF.getPlugin().getTimerManager().teleportTimer;
			Iterator var3 = getOnlinePlayers().iterator();
			while(var3.hasNext()){
				Player player = (Player) var3.next();
				Location destination = (Location) timer.getDestination(player);
				if(Objects.equal(destination, this.home.getLocation())){
					timer.clearCooldown(player);
					player.sendMessage(RED + "Your home was unset, so your " + timer.getDisplayName() + RED + " timer has been cancelled");
				}
			}
		}
		this.home = (home == null ? null : new PersistableLocation(home));
	}

	public String getAnnouncement(){
		return this.announcement;
	}

	public void setAnnouncement(@Nullable String announcement){
		this.announcement = announcement;
	}

	public boolean isOpen(){
		return this.open;
	}

	public void setOpen(boolean open){
		this.open = open;
	}

	public int getBalance(){
		return this.balance;
	}

	public void setBalance(int balance){
		this.balance = balance;
	}

	public boolean isRaidable(){
		return this.deathsUntilRaidable <= 0.0D;
	}

	public double getDeathsUntilRaidable(){
		return getDeathsUntilRaidable(true);
	}

	public double getMaximumDeathsUntilRaidable(){
		if(this.members.size() == 1){
			return 1.1D;
		}
		return Math.min(5.5D, this.members.size() * 0.9D);
	}

	public double getDeathsUntilRaidable(boolean updateLastCheck){
		if(updateLastCheck){
			updateDeathsUntilRaidable();
		}
		return this.deathsUntilRaidable;
	}

	public ChatColor getDtrColour(){
		updateDeathsUntilRaidable();
		if(this.deathsUntilRaidable < 0.0D){
			return RED;
		}
		if(this.deathsUntilRaidable < 1.0D){
			return ChatColor.YELLOW;
		}
		return ChatColor.GREEN;
	}

	private void updateDeathsUntilRaidable(){
		if(getRegenStatus() == RegenStatus.REGENERATING){
			long now = System.currentTimeMillis();
			long millisPassed = now - this.lastDtrUpdateTimestamp;
			if(millisPassed >= ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES){
				long remainder = millisPassed % ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES;
				int multiplier = (int) ((millisPassed + remainder) / ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES);
				double increase = multiplier * 0.1D;
				this.lastDtrUpdateTimestamp = (now - remainder);
				setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
			}
		}
	}

	public double setDeathsUntilRaidable(double deathsUntilRaidable){
		return setDeathsUntilRaidable(deathsUntilRaidable, true);
	}

	private double setDeathsUntilRaidable(double deathsUntilRaidable, boolean limit){
		deathsUntilRaidable = deathsUntilRaidable * 100.0D / 100.0D;
		if(limit){
			deathsUntilRaidable = Math.min(deathsUntilRaidable, getMaximumDeathsUntilRaidable());
		}
		if(deathsUntilRaidable - this.deathsUntilRaidable != 0.0D){
			FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				deathsUntilRaidable = event.getNewDtr();
				if((deathsUntilRaidable > 0.0D) && (deathsUntilRaidable <= 0.0D)){
					HCF.getPlugin().getLogger().info("Faction " + getName() + " is now raidable.");
				}
				this.lastDtrUpdateTimestamp = System.currentTimeMillis();
				return this.deathsUntilRaidable = deathsUntilRaidable;
			}
		}
		return this.deathsUntilRaidable;
	}

	protected long getRegenCooldownTimestamp(){
		return this.regenCooldownTimestamp;
	}

	public long getRemainingRegenerationTime(){
		return this.regenCooldownTimestamp == 0L ? 0L : this.regenCooldownTimestamp - System.currentTimeMillis();
	}

	public void setRemainingRegenerationTime(long millis){
		long systemMillis = System.currentTimeMillis();
		this.regenCooldownTimestamp = (systemMillis + millis);
		this.lastDtrUpdateTimestamp = (systemMillis + ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES * 2L);
	}

	public RegenStatus getRegenStatus(){
		if(getRemainingRegenerationTime() > 0L){
			return RegenStatus.PAUSED;
		}
		if(getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable){
			return RegenStatus.REGENERATING;
		}
		return RegenStatus.FULL;
	}

	public void regenProccess(){

	}

	public void printDetails(CommandSender sender){
		String leaderName = null;
		final HashSet<String> coleaderName = new HashSet<String>();
		HashSet allyNames = new HashSet(1);
		Iterator combinedKills = this.relations.entrySet().iterator();

		PlayerFaction playerFaction;
		while(combinedKills.hasNext()){
			Map.Entry memberNames = (Map.Entry) combinedKills.next();
			Faction captainNames = HCF.getPlugin().getFactionManager().getFaction((UUID) memberNames.getKey());
			if(captainNames instanceof PlayerFaction){
				playerFaction = (PlayerFaction) captainNames;
				allyNames.add(playerFaction.getDisplayName(sender) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
			}
		}

		HashSet memberNames = new HashSet();
		int combinedKills1 = 0;
		HashSet<String> captainNames = new HashSet();
		Iterator playerFaction1 = this.members.entrySet().iterator();

		while(playerFaction1.hasNext()){
			final Map.Entry entry = (Map.Entry) playerFaction1.next();
			final FactionMember factionMember = (FactionMember) entry.getValue();
			final Player target = factionMember.toOnlinePlayer();
			final FactionUser user = HCF.getPlugin().getUserManager().getUser((UUID) entry.getKey());
			final Deathban deathban = user.getDeathban();
			int kills = user.getKills();

			ChatColor colour;
			if(target == null || (sender instanceof Player && !((Player) sender).canSee(target))){
				colour = ChatColor.GRAY;
			}else{
				colour = ChatColor.GREEN;
			}
			if(deathban != null && deathban.isActive()){
				colour = RED;
			}
			final String memberName = colour + factionMember.getName() + ChatColor.GRAY + '[' + ChatColor.AQUA + kills + ChatColor.GRAY + ']';
			switch(factionMember.getRole()){
				case LEADER:{
					leaderName = memberName;
					continue;
				}
				case COLEADER:{
					coleaderName.add(memberName);
					continue;
				}
				case CAPTAIN:{
					captainNames.add(memberName);
					continue;
				}
				case MEMBER:{
					memberNames.add(memberName);
					continue;
				}
			}
		}

		DecimalFormat df = new DecimalFormat("#");

		String finalLeaderName = leaderName;
		long dtrRegenRemaining = this.getRemainingRegenerationTime();
      /*
      HCF.getInstance().getConfig().getStringList("faction-settings.show.player-faction").forEach(str -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
              str = str.replace("%FACTION%", this.getDisplayName(sender))
                      .replace("%ONLINE%", this.getOnlineMembers().size() + "")
                      .replace("%MAX%", this.getMembers().size() + "")
                      .replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT + "")
                      .replace("%ANNOUNCEMENT%", this.announcement != null ? this.announcement : "")
                      .replace("%KILLS%", combinedKills1 + "")
                      .replace("%BALANCE%", this.balance + "")
                      .replace("%DTR%", this.getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)))
                      .replace("%MAXDTR%", this.getMaximumDeathsUntilRaidable() + "")
                      .replace("%DTR-SYMBOL%", (this.getRegenStatus().getSymbol() != null ? this.getRegenStatus().getSymbol() : ""))
                      .replace("%HOME%", this.home == null ? "None" : (df.format(this.getHome().getX()) + ", " + df.format(this.getHome().getZ())))
                      .replace("%LEADER%", finalLeaderName != null ? finalLeaderName : "")
                      .replace("%CAPTAINS%", !captainNames.isEmpty() ? StringUtils.join(captainNames, ChatColor.GRAY + ", ") : ChatColor.GRAY + "None")
                      .replace("%MEMBERS%", !memberNames.isEmpty() ? StringUtils.join(memberNames, ChatColor.GRAY + ", ") : ChatColor.GRAY + "None")
                      .replace("%ALLIES%", !allyNames.isEmpty() ? StringUtils.join(allyNames, ChatColor.GRAY + ", ") : ChatColor.GRAY + "None")
                      .replace("%REGEN%", (dtrRegenRemaining > 0L) ? DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true) : ChatColor.GRAY + "Fully Regenerated.")
              )));*/
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
		sender.sendMessage(ChatColor.AQUA + " " + ChatColor.BOLD + ChatColor.stripColor(this.getDisplayName(sender)) + ChatColor.GRAY + " [" + this.getOnlineMembers().size() + "/" + this.getMembers().size() + " online]" + ChatColor.BLUE + " Status: " + (this.isOpen() ? ChatColor.GREEN + "Open" : ChatColor.AQUA + "Closed"));
		sender.sendMessage(ChatColor.BLUE + " Home: " + ((this.home == null ? RED + "Not Set" : ChatColor.AQUA.toString() + this.home.getLocation().getBlockX() + ", " + this.home.getLocation().getBlockZ())));
		if(!allyNames.isEmpty()){
			sender.sendMessage(ChatColor.BLUE + " Allies: " + StringUtils.join(allyNames, ChatColor.GRAY + ", "));
		}
		if(leaderName != null){
			sender.sendMessage(ChatColor.BLUE + " Leader: " + leaderName);
		}
		if(!captainNames.isEmpty()){
			sender.sendMessage(ChatColor.BLUE + " Captains: " + StringUtils.join(captainNames, ChatColor.GRAY + ", "));
		}
		if(!memberNames.isEmpty()){
			sender.sendMessage(ChatColor.BLUE + " Members: " + StringUtils.join(memberNames, ChatColor.GRAY + ", "));
		}
		if(sender instanceof Player){
			final Faction playerFaction2 = HCF.getPlugin().getFactionManager().getPlayerFaction((Player) sender);
			if(playerFaction2 != null && this.announcement != null && playerFaction2.equals(this)){
				sender.sendMessage(ChatColor.BLUE + " Announcement: " + ChatColor.LIGHT_PURPLE + this.announcement);
			}
		}
		if(ConfigurationService.KIT_MAP){
			sender.sendMessage(ChatColor.BLUE + " Balance: " + ChatColor.AQUA + "$" + this.balance);
			sender.sendMessage(ChatColor.BLUE + " Total Kills: " + ChatColor.AQUA + combinedKills1);
		}else{
			sender.sendMessage(ChatColor.BLUE + " Balance: " + ChatColor.AQUA + '$' + this.balance + ChatColor.BLUE + ", " + "Total Kills: " + ChatColor.AQUA + combinedKills1 + ChatColor.AQUA + " kills");
			if(!(sender.hasPermission("rank.staff"))){
				sender.sendMessage(ChatColor.BLUE + " Deaths until Raidable: " + this.getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)) + ChatColor.GRAY + "/" + this.getMaximumDeathsUntilRaidable() + this.getRegenStatus().getSymbol());
			}else{
				Text dtr = new Text(ChatColor.BLUE + " Deaths until Raidable: " + this.getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)) + ChatColor.GRAY + "/" + this.getMaximumDeathsUntilRaidable() + this.getRegenStatus().getSymbol() + ChatColor.GRAY + " (");
				Text dtrUp = new Text(ChatColor.GREEN + "+1").setHoverText(ChatColor.GRAY + "Increase DTR").setClick(ClickAction.RUN_COMMAND, "/f setdtr " + ChatColor.stripColor(this.getDisplayName(sender) + " -i"));
				Text line = new Text(ChatColor.GRAY + " | ");
				Text dtrDown = new Text(ChatColor.RED + "-1").setHoverText(ChatColor.GRAY + "Decrease DTR").setClick(ClickAction.RUN_COMMAND, "/f setdtr " + ChatColor.stripColor(this.getDisplayName(sender) + " -d"));
				Text bracket = new Text(ChatColor.GRAY + ")");
				dtr.append(dtrUp.append(line.reset().append(dtrDown.append(bracket)))).send(sender);

			}
		}

		if(dtrRegenRemaining > 0L){
			if(sender.hasPermission("rank.staff")){
				Text beforeRegen = new Text(ChatColor.YELLOW + " Time until Regen: " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true) + " ");
				Text regen = new Text(ChatColor.GRAY + "(Remove)");
				regen.setHoverText(ChatColor.GRAY + "Click to remove regen delay.");
				regen.setClick(ClickAction.RUN_COMMAND, "/f setdtrregen " + ChatColor.stripColor(this.getDisplayName(sender) + " 0s"));
				beforeRegen.append(regen).send(sender);
				//sender.sendMessage(ChatColor.YELLOW + "   Time until Regen: " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));
			}else{
				sender.sendMessage(ChatColor.YELLOW + "   Time until Regen: " + ChatColor.LIGHT_PURPLE + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));

			}
		}


		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
	}


	public void broadcast(String message){
		broadcast(message, EMPTY_UUID_ARRAY);
	}

	public void broadcast(String[] messages){
		broadcast(messages, EMPTY_UUID_ARRAY);
	}

	public void broadcast(String message, @Nullable UUID... ignore){
		broadcast(new String[]{message}, ignore);
	}

	public void broadcast(String[] messages, UUID... ignore){
		Preconditions.checkNotNull(messages, "Messages cannot be null");
		Preconditions.checkArgument(messages.length > 0, "Message array cannot be empty");
		Collection<Player> players = getOnlinePlayers();
		Collection<UUID> ignores = ignore.length == 0 ? Collections.emptySet() : Sets.newHashSet(ignore);
		for(Player player : players){
			if(!ignores.contains(player.getUniqueId())){
				player.sendMessage(messages);
			}
		}
	}
}

