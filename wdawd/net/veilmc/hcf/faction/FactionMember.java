package net.veilmc.hcf.faction;

import net.veilmc.hcf.faction.struct.ChatChannel;
import net.veilmc.hcf.faction.struct.Role;
import com.google.common.base.Enums;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class FactionMember
		implements ConfigurationSerializable{
	private final UUID uniqueID;
	private ChatChannel chatChannel;
	private Role role;

	public FactionMember(Player player, ChatChannel chatChannel, Role role){
		this.uniqueID = player.getUniqueId();
		this.chatChannel = chatChannel;
		this.role = role;
	}

	public FactionMember(Map<String, Object> map){
		this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
		this.chatChannel = Enums.getIfPresent(ChatChannel.class, (String) map.get("chatChannel")).or(ChatChannel.PUBLIC);
		this.role = Enums.getIfPresent(Role.class, (String) map.get("role")).or(Role.MEMBER);
	}

	public Map<String, Object> serialize(){
		LinkedHashMap map = Maps.newLinkedHashMap();
		map.put("uniqueID", this.uniqueID.toString());
		map.put("chatChannel", this.chatChannel.name());
		map.put("role", this.role.name());
		return map;
	}

	public String getName(){
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.uniqueID);
		return offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline() ? offlinePlayer.getName() : null;
	}

	public UUID getUniqueId(){
		return this.uniqueID;
	}

	public ChatChannel getChatChannel(){
		return this.chatChannel;
	}

	public void setChatChannel(ChatChannel chatChannel){
		Preconditions.checkNotNull(chatChannel, "ChatChannel cannot be null");
		this.chatChannel = chatChannel;
	}

	public Role getRole(){
		return this.role;
	}

	public void setRole(Role role){
		this.role = role;
	}

	public Player toOnlinePlayer(){
		return Bukkit.getPlayer(this.uniqueID);
	}
}

