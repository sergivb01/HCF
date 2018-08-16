package idaniel84.listener;

import idaniel84.faction.type.PlayerFaction;
import net.minecraft.util.com.google.common.cache.CacheBuilder;
import idaniel84.HCF;
import idaniel84.faction.event.FactionChatEvent;
import idaniel84.faction.struct.ChatChannel;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.utils.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChatListener
		implements Listener{
	private static final Pattern PATTERN;

	static{
		PATTERN = Pattern.compile("\\W");
	}

	private final ConcurrentMap<Object, Object> messageHistory;
	private final HCF plugin;

	public ChatListener(HCF plugin){
		this.plugin = plugin;
		this.messageHistory = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.MINUTES).build().asMap();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		String message = event.getMessage();
		Player player = event.getPlayer();
		String lastMessage = (String) this.messageHistory.get(player.getUniqueId());
		String cleanedMessage = PATTERN.matcher(message).replaceAll("");
		if(lastMessage != null && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1) && !player.hasPermission("hcf.doublepost.bypass")){
			player.sendMessage(ChatColor.RED + "Double posting is prohibited.");
			event.setCancelled(true);
			return;
		}
		this.messageHistory.put(player.getUniqueId(), cleanedMessage);
		PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
		ChatChannel chatChannel = playerFaction == null ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
		Set recipients = event.getRecipients();
		if(chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE){
			if(!this.isGlobalChannel(message)){
				Set online = playerFaction.getOnlinePlayers();
				if(chatChannel == ChatChannel.ALLIANCE){
					List<PlayerFaction> allies = playerFaction.getAlliedFactions();
					for(PlayerFaction ally : allies){
						online.addAll(ally.getOnlinePlayers());
					}
				}
				recipients.retainAll(online);
				event.setFormat(chatChannel.getRawFormat(player));
				Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
				return;
			}
			message = message.substring(1, message.length()).trim();
			event.setMessage(message);
		}
		event.setCancelled(true);

//        Integer k = player.getStatistic(Statistic.PLAYER_KILLS);
//        double d = player.getStatistic(Statistic.DEATHS);
//        double kd = k / d;
//        String kdr;
//        DecimalFormat df = new DecimalFormat("#.##");
//
//        if (df.format(kd).matches(".*\\d+.*")){
//            kdr = df.format(kd);
//        } else {
//            kdr = "0";
//        }

		//String kdrPrefix = (kdr > 2 ? ChatColor.RED + kdr : ChatColor.GREEN + kdr);

		String rank = ChatColor.translateAlternateColorCodes('&', "&e" + PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
		String displayName = player.getDisplayName();
		displayName = rank + displayName;

		String tag = playerFaction == null ? "" : ChatColor.GOLD + "[" + playerFaction.getDisplayName(Bukkit.getConsoleSender()) + ChatColor.RED + "] " + ((ConfigurationService.KIT_MAP) ? ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + player.getStatistic(Statistic.PLAYER_KILLS) + ChatColor.DARK_GRAY + ") " : "");
		Bukkit.getConsoleSender().sendMessage(tag + displayName + ChatColor.GOLD + ": " + ChatColor.GRAY + message);

		//String tag = playerFaction == null ? "" : ChatColor.GOLD + "[" + playerFaction.getDisplayName(Bukkit.getConsoleSender()) + ChatColor.RED + "] ";

		for(Player recipient : event.getRecipients()){
			tag = playerFaction == null ? ChatColor.GOLD + "[" + ChatColor.RED + "*" + ChatColor.GOLD + "] " + ((ConfigurationService.KIT_MAP) ? ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + player.getStatistic(Statistic.PLAYER_KILLS) + ChatColor.DARK_GRAY + ") " : "") : ChatColor.GOLD + "[" + playerFaction.getDisplayName(recipient) + ChatColor.GOLD + "] " + ((ConfigurationService.KIT_MAP) ? ChatColor.DARK_GRAY + "(" + ChatColor.GREEN + player.getStatistic(Statistic.PLAYER_KILLS) + ChatColor.DARK_GRAY + ") " : "");
			recipient.sendMessage(tag + displayName + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
		}
	}

	private boolean isGlobalChannel(String input){
		int length = input.length();
		if(length <= 1 || !input.startsWith("!")){
			return false;
		}
		for(int i = 1; i < length; ++i){
			char character = input.charAt(i);
			if(character == ' '){
				continue;
			}
			if(character != '/') break;
			return false;
		}
		return true;
	}
}