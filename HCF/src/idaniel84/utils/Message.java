package idaniel84.utils;

import idaniel84.HCF;
import net.veilmc.util.chat.Text;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

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

