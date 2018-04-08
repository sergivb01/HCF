package net.veilmc.hcf.deathban;

import net.minecraft.util.com.google.common.cache.CacheBuilder;
import net.veilmc.base.BasePlugin;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.user.FactionUser;
import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.util.BukkitUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DeathbanListener
		implements Listener{
	private static final long LIFE_USE_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30L);
	private static final String LIFE_USE_DELAY_WORDS = DurationFormatUtils.formatDurationWords(LIFE_USE_DELAY_MILLIS, true, true);
	private static final String DEATH_BAN_BYPASS_PERMISSION = "hcf.deathban.bypass";
	private final ConcurrentMap<Object, Object> lastAttemptedJoinMap;
	private final HCF plugin;

	public DeathbanListener(HCF plugin){
		this.plugin = plugin;
		this.lastAttemptedJoinMap = CacheBuilder.newBuilder().expireAfterWrite(LIFE_USE_DELAY_MILLIS, TimeUnit.MILLISECONDS).build().asMap();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event){
		Player player = event.getPlayer();
		FactionUser user = this.plugin.getUserManager().getUser(player.getUniqueId());
		Deathban deathban = user.getDeathban();

		if((deathban == null) || (!deathban.isActive())) return;

		if(player.hasPermission("hcf.deathban.bypass")){
			//new LoginMessageRunnable(player, ChatColor.RED + "You would be death-banned for " + deathban.getReason() + ChatColor.RED + ", but you have access to bypass.").runTask(this.plugin);
			//new LoginMessageRunnable(player, ConfigurationService.DEATHBAN_BYPASS.replace("%reason%", deathban.getReason()));
			return;
		}
		if(this.plugin.getEotwHandler().isEndOfTheWorld()){
			//event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Deathbanned for the entirety of the map due to EOTW.\nCome back for SOTW.");
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ConfigurationService.DEATHBANNED_EOTW);
		}else{
			UUID uuid = player.getUniqueId();
			int lives = this.plugin.getDeathbanManager().getLives(uuid);
			String formattedDuration = HCF.getRemaining(deathban.getRemaining(), true, false);
			String reason = deathban.getReason();
			//String prefix = ChatColor.RED + "You are currently death-banned" + (reason != null ? " for " + reason + ".\n" : ".") + ChatColor.WHITE + formattedDuration + " remaining.\n" + ChatColor.RED + "You currently have " + (lives <= 0 ? "no" : Integer.valueOf(lives)) + " lives.";
			String prefix = ConfigurationService.DEATHBANNED_ACTIVE.replace("%reason%", reason).replace("%time%", formattedDuration) + ChatColor.RED + " You currently have " + (lives <= 0 ? "no" : Integer.valueOf(lives)) + " lives.";
			if(lives > 0){
				long millis = System.currentTimeMillis();
				Long lastAttemptedJoinMillis = (Long) this.lastAttemptedJoinMap.get(uuid);
				if((lastAttemptedJoinMillis != null) && (lastAttemptedJoinMillis - System.currentTimeMillis() < LIFE_USE_DELAY_MILLIS)){
					this.lastAttemptedJoinMap.remove(uuid);
					user.removeDeathban();
					lives = this.plugin.getDeathbanManager().takeLives(uuid, 1);
					event.setResult(PlayerLoginEvent.Result.ALLOWED);
					new LoginMessageRunnable(player, ChatColor.GRAY + "You have used a life bypass your death. You now have " + ChatColor.YELLOW + lives + ChatColor.GRAY + " lives.").runTask(this.plugin);
				}else{
					this.lastAttemptedJoinMap.put(uuid, millis + LIFE_USE_DELAY_MILLIS);
					event.disallow(PlayerLoginEvent.Result.KICK_OTHER, prefix + ChatColor.GOLD + "\n\n" + "You may use a life by reconnecting within " + ChatColor.WHITE + LIFE_USE_DELAY_WORDS + ChatColor.GOLD + '.');
				}
				return;
			}
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Deathbanned for " + formattedDuration + ChatColor.RED + "\nReason: " + ChatColor.WHITE + deathban.getReason() + "\n" + ChatColor.RED + "\nYou can purchase lives at our store to bypass this.");
			//event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ConfigurationService.STILL_DEATHBANNED.replace("%time%", formattedDuration).replace("%reason%", deathban.getReason()));

		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event){
		if(ConfigurationService.KIT_MAP || ConfigurationService.VEILZ){
			return;
		}

		final Player player = event.getEntity();

		if(player.hasPermission("hcf.deathban.bypass")) return;

		final Deathban deathban = this.plugin.getDeathbanManager().applyDeathBan(player, event.getDeathMessage());

		final String durationString = HCF.getRemaining(deathban.getRemaining(), true, false);

		String formattedDuration = HCF.getRemaining(deathban.getRemaining(), true, false);


		if(player.hasPermission("deathban.nokick")){
			return;
		}


		new BukkitRunnable(){
			public void run(){
				if(DeathbanListener.this.plugin.getEotwHandler().isEndOfTheWorld()){
					player.kickPlayer(ConfigurationService.DEATHBANNED_EOTW_ENTIRE);
				}else{
					player.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
					player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW + "You have been " + ChatColor.RED + "Death-banned");
					player.sendMessage(ChatColor.YELLOW + " ");
					player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW + "This deathban expires in " + ChatColor.GOLD + durationString);
					player.sendMessage(ChatColor.WHITE + " * " + ChatColor.YELLOW + "Reason: " + ChatColor.RED + ChatColor.stripColor(deathban.getReason()));
					player.sendMessage(ChatColor.DARK_GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);

					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);
					try{
						out.writeUTF("Connect");
						out.writeUTF("lobby1");
					}catch(IOException e){
						player.sendMessage(ChatColor.RED + "Error while trying to connect to the lobby.");
					}
					player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());


					new BukkitRunnable(){
						@Override
						public void run(){
							player.kickPlayer(ChatColor.RED + "Deathbanned for " + ChatColor.RED + formattedDuration + ChatColor.RED + ".\n" + ChatColor.RED + "Reason: " + ChatColor.YELLOW + deathban.getReason());
						}
					}.runTaskLater(BasePlugin.getPlugin(), 20L);

				}
			}
		}.runTaskLater(this.plugin, 20L);
	}

	private static class LoginMessageRunnable
			extends BukkitRunnable{
		private final Player player;
		private final String message;

		public LoginMessageRunnable(Player player, String message){
			this.player = player;
			this.message = message;
		}

		public void run(){
			this.player.sendMessage(this.message);
		}
	}
}
