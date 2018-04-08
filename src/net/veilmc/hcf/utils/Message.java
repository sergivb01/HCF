package net.veilmc.hcf.utils;

import net.veilmc.hcf.HCF;
import net.veilmc.util.chat.Text;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public final class Message{
	private final HashMap<UUID, Long> messageDelay = new HashMap();
	HCF plugin;

	public Message(HCF plugin){
		this.plugin = plugin;
	}

	public void sendMessage(Player player, String message){
		if(this.messageDelay.containsKey(player.getUniqueId())){
			if(this.messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0){
				return;
			}
			this.messageDelay.remove(player.getUniqueId());
		}
		this.messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000);
		player.sendMessage(message);
	}

	public void sendMessage(Player player, Text text){
		if(this.messageDelay.containsKey(player.getUniqueId())){
			if(this.messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0){
				return;
			}
			this.messageDelay.remove(player.getUniqueId());
		}
		this.messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000);
		text.send(player);
	}
}

