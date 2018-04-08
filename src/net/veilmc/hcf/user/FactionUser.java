package net.veilmc.hcf.user;

import com.google.common.collect.Maps;
import net.veilmc.hcf.deathban.Deathban;
import net.veilmc.util.GenericUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FactionUser
		implements ConfigurationSerializable{
	private final Set<UUID> factionChatSpying = new HashSet<>();
	private final Set<String> shownScoreboardScores = new HashSet<>();
	private final UUID userUUID;
	private boolean reclaimed;
	private boolean capzoneEntryAlerts;
	private boolean showClaimMap;
	private boolean showLightning = true;
	private Deathban deathban;
	private long lastFactionLeaveMillis;
	private int kills;
	private int diamondsMined;
	private int deaths;
	private int spawnTokens;
	private int tabStyle;

	public FactionUser(UUID userUUID){
		this.userUUID = userUUID;
	}

	public FactionUser(Map<String, Object> map){
		this.shownScoreboardScores.addAll(GenericUtils.createList(map.get("shownScoreboardScores"), (Class) String.class));
		this.factionChatSpying.addAll(GenericUtils.createList(map.get("faction-chat-spying"), (Class) String.class));
		this.userUUID = UUID.fromString((String) map.get("userUUID"));
		this.capzoneEntryAlerts = (Boolean) map.get("capzoneEntryAlerts");
		this.showLightning = (Boolean) map.get("showLightning");
		this.deathban = (Deathban) map.get("deathban");
		this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
		this.diamondsMined = (Integer) map.get("diamonds");
		this.kills = (Integer) map.get("kills");
		this.deaths = (Integer) map.get("deaths");
		this.reclaimed = (Boolean) map.getOrDefault("reclaimed", false);
		this.spawnTokens = (Integer) map.get("spawnTokens");
		this.tabStyle = (Integer) map.getOrDefault("tabStyle", 0);
	}

	public Map<String, Object> serialize(){
		LinkedHashMap map = Maps.newLinkedHashMap();
		map.put("shownScoreboardScores", new ArrayList<>(this.shownScoreboardScores));
		map.put("faction-chat-spying", this.factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
		map.put("userUUID", this.userUUID.toString());
		map.put("diamonds", this.diamondsMined);
		map.put("capzoneEntryAlerts", this.capzoneEntryAlerts);
		map.put("showClaimMap", this.showClaimMap);
		map.put("showLightning", this.showLightning);
		map.put("deathban", this.deathban);
		map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
		map.put("kills", this.kills);
		map.put("deaths", this.deaths);
		map.put("reclaimed", this.reclaimed);
		map.put("spawnTokens", this.spawnTokens);
		map.put("tabStyle", this.tabStyle);
		return map;
	}

	public boolean isCapzoneEntryAlerts(){
		return this.capzoneEntryAlerts;
	}

	public boolean isShowClaimMap(){
		return this.showClaimMap;
	}

	public void setShowClaimMap(boolean showClaimMap){
		this.showClaimMap = showClaimMap;
	}

	public int getKills(){
		return this.kills;
	}

	public void setKills(int kills){
		this.kills = kills;
	}

	public int getDiamondsMined(){
		return this.diamondsMined;
	}

	public void setDiamondsMined(int diamondsMined){
		this.diamondsMined = diamondsMined;
	}

	public Deathban getDeathban(){
		return this.deathban;
	}

	public void setDeathban(Deathban deathban){
		this.deathban = deathban;
	}

	public void removeDeathban(){
		this.deathban = null;
	}

	public long getLastFactionLeaveMillis(){
		return this.lastFactionLeaveMillis;
	}

	public void setLastFactionLeaveMillis(long lastFactionLeaveMillis){
		this.lastFactionLeaveMillis = lastFactionLeaveMillis;
	}

	public int getDeaths(){
		return this.deaths;
	}

	public void setDeaths(Integer deaths){
		this.deaths = deaths;
	}

	public int getSpawnTokens(){
		return this.spawnTokens;
	}

	public void setSpawnTokens(Integer spawnTokens){
		this.spawnTokens = spawnTokens;
	}

	public boolean isShowLightning(){
		return this.showLightning;
	}

	public void setShowLightning(boolean showLightning){
		this.showLightning = showLightning;
	}

	public Set<UUID> getFactionChatSpying(){
		return this.factionChatSpying;
	}

	public Set<String> getShownScoreboardScores(){
		return this.shownScoreboardScores;
	}

	public UUID getUserUUID(){
		return this.userUUID;
	}

	public Player getPlayer(){
		return Bukkit.getPlayer(this.userUUID);
	}

	public boolean isReclaimed(){
		return reclaimed;
	}

	public void setReclaimed(boolean reclaimed){
		this.reclaimed = reclaimed;
	}

	public void setTabStyle(TabStyles style){
		switch(style){
			default:
			case CLASSIC:
				this.tabStyle = 0;
				break;
			case FACTION_LIST:
				this.tabStyle = 1;
				break;
			case STAFF:
				this.tabStyle = 2;
				break;
		}
	}

	public int getTabStyleInt(){
		return this.tabStyle;
	}

	public TabStyles getTabStyle(){
		switch(this.tabStyle){
			default:
			case 0:
				return TabStyles.CLASSIC;
			case 1:
				return TabStyles.FACTION_LIST;
			case 2:
				return TabStyles.STAFF;
		}
	}

	public void setTabStyle(int i){
		if(i >= 0 && i < 3){
			this.tabStyle = i;
		}
	}

}

