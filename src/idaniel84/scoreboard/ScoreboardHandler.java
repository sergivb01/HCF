package idaniel84.scoreboard;

import idaniel84.HCF;
import idaniel84.HCF;
import idaniel84.faction.event.FactionRelationCreateEvent;
import idaniel84.faction.event.FactionRelationRemoveEvent;
import idaniel84.faction.event.PlayerJoinedFactionEvent;
import idaniel84.faction.event.PlayerLeftFactionEvent;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.scoreboard.provider.TimerSidebarProvider;
import com.google.common.base.Optional;
import me.sergivb01.event.PotionEffectAddEvent;
import me.sergivb01.event.PotionEffectRemoveEvent;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScoreboardHandler implements Listener, Runnable{
	private final Map<UUID, PlayerBoard> playerBoards;
	private final TimerSidebarProvider timerSidebarProvider;
	private final HCF plugin;
	private final boolean threadBased = true;
	private final int TICK_IN_MS = 100;
	private final long TICK_IN_NANOS;
	private final long NANO;
	private boolean running;
	private Thread scoreboardThread;

	public ScoreboardHandler(final HCF plugin){
		this.running = true;
		this.TICK_IN_NANOS = TimeUnit.MILLISECONDS.toNanos(100L);
		this.NANO = TimeUnit.MILLISECONDS.toNanos(1L);
		this.playerBoards = new HashMap<>();
		this.plugin = plugin;
		this.timerSidebarProvider = new TimerSidebarProvider(plugin);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		for(final Player player : Bukkit.getOnlinePlayers()){
			final PlayerBoard playerBoard = new PlayerBoard(plugin, player);
			playerBoard.init(Bukkit.getOnlinePlayers());
			this.setPlayerBoard(player.getUniqueId(), playerBoard);
		}
		(this.scoreboardThread = new Thread(this)).start();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerJoin(final PlayerJoinEvent event){
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		synchronized(this.playerBoards){
			for(final PlayerBoard playerBoard : this.playerBoards.values()){
				playerBoard.init(player);
			}
		}
		final PlayerBoard playerBoard2 = new PlayerBoard(this.plugin, player);
		playerBoard2.init(Bukkit.getOnlinePlayers());
		this.setPlayerBoard(uuid, playerBoard2);
		if(PlayerBoard.INVISIBILITYFIX){
			new BukkitRunnable(){
				public void run(){
					if(player.isOnline()){
						for(final Player other : Bukkit.getOnlinePlayers()){
							if(other.hasPotionEffect(PotionEffectType.INVISIBILITY)){
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) other).getHandle()));
							}
						}
					}
				}
			}.runTask(this.plugin);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerQuit(final PlayerQuitEvent event){
		synchronized(this.playerBoards){
			this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event){
		final Optional<Player> optional = event.getPlayer();
		if(optional.isPresent()){
			final Player player = optional.get();
			final Collection<Player> players = event.getFaction().getOnlinePlayers();
			final PlayerBoard playerBoard = this.getPlayerBoard(event.getPlayer().get().getUniqueId());
			if(playerBoard != null){
				playerBoard.setMembers(players);
				final List<PlayerFaction> alliedFactions = event.getFaction().getAlliedFactions();
				for(final PlayerFaction playerFaction : alliedFactions){
					playerBoard.setAllies(playerFaction.getOnlinePlayers());
				}
			}
			for(final Player other : players){
				final PlayerBoard otherBoard = this.getPlayerBoard(other.getUniqueId());
				otherBoard.setMembers(Collections.singleton(player));
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeftFaction(final PlayerLeftFactionEvent event){
		final Optional<Player> optional = event.getPlayer();
		if(optional.isPresent()){
			final Player player = optional.get();
			final Collection<Player> players = event.getFaction().getOnlinePlayers();
			final PlayerBoard playerBoard = this.getPlayerBoard(event.getUniqueID());
			if(playerBoard != null){
				playerBoard.setNeutrals(players);
				final List<PlayerFaction> alliedFactions = event.getFaction().getAlliedFactions();
				for(final PlayerFaction playerFaction : alliedFactions){
					playerBoard.setNeutrals(playerFaction.getOnlinePlayers());
				}
			}
			for(final Player other : players){
				final PlayerBoard otherBoard = this.getPlayerBoard(other.getUniqueId());
				otherBoard.setNeutrals(Collections.singleton(player));
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionAllyCreate(final FactionRelationCreateEvent event){
		for(final Player player : event.getSenderFaction().getOnlinePlayers()){
			final PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
			playerBoard.setAllies(event.getTargetFaction().getOnlinePlayers());
		}
		for(final Player player : event.getTargetFaction().getOnlinePlayers()){
			final PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
			playerBoard.setAllies(event.getSenderFaction().getOnlinePlayers());
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFactionAllyRemove(final FactionRelationRemoveEvent event){
		for(final Player player : event.getSenderFaction().getOnlinePlayers()){
			final PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
			playerBoard.setNeutrals(event.getTargetFaction().getOnlinePlayers());
		}
		for(final Player player : event.getTargetFaction().getOnlinePlayers()){
			final PlayerBoard playerBoard = this.getPlayerBoard(player.getUniqueId());
			playerBoard.setNeutrals(event.getSenderFaction().getOnlinePlayers());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInvisibilityExpire(final PotionEffectRemoveEvent event){
		if(PlayerBoard.INVISIBILITYFIX && event.getEntity() instanceof Player && event.getEffect().getType().getId() == PotionEffectType.INVISIBILITY.getId()){
			final Player player = (Player) event.getEntity();
			new BukkitRunnable(){
				public void run(){
					if(player.isOnline() && !player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
						synchronized(ScoreboardHandler.this.playerBoards){
							for(final PlayerBoard playerBoard : ScoreboardHandler.this.playerBoards.values()){
								playerBoard.init(player);
							}
						}
						for(final Player other : Bukkit.getOnlinePlayers()){
							((CraftPlayer) other).getHandle().playerConnection.sendPacket(PacketPlayOutPlayerInfo.addPlayer(((CraftPlayer) player).getHandle()));
						}
					}
				}
			}.runTask(this.plugin);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInvisibleDrink(final PotionEffectAddEvent event){
		if(PlayerBoard.INVISIBILITYFIX && event.getEntity() instanceof Player && event.getEffect().getType().getId() == PotionEffectType.INVISIBILITY.getId()){
			final Player player = (Player) event.getEntity();
			new BukkitRunnable(){
				public void run(){
					if(player.isOnline() && player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
						synchronized(ScoreboardHandler.this.playerBoards){
							for(final PlayerBoard playerBoard : ScoreboardHandler.this.playerBoards.values()){
								playerBoard.removeAll(player);
							}
						}
					}
				}
			}.runTask(this.plugin);
		}
	}

	public PlayerBoard getPlayerBoard(final UUID uuid){
		synchronized(this.playerBoards){
			return this.playerBoards.get(uuid);
		}
	}

	public void setPlayerBoard(final UUID uuid, final PlayerBoard board){
		synchronized(this.playerBoards){
			this.playerBoards.put(uuid, board);
		}
		board.setSidebarVisible(true);
		board.setDefaultSidebar(this.timerSidebarProvider);
	}

	public void tick(){
		final long now = System.currentTimeMillis();
		synchronized(this.playerBoards){
			for(final PlayerBoard board : this.playerBoards.values()){
				if(board.getPlayer().isOnline() && !board.isRemoved()){
					try{
						board.updateObjective(now);
					}catch(Exception exception){
						exception.printStackTrace();
					}
				}
			}
		}
	}

	public void disable(){
		this.running = false;
		try{
			this.scoreboardThread.join();
		}catch(InterruptedException exception){
			exception.printStackTrace();
		}
		this.clearBoards();
	}

	public void run(){
		long start = System.nanoTime();
		while(this.plugin.isEnabled() && this.running){
			this.tick();
			final long finish = System.nanoTime();
			final long diff = finish - start;
			if(diff < this.TICK_IN_NANOS){
				final long sleep = (this.TICK_IN_NANOS - diff) / this.NANO;
				if(sleep > 0L){
					try{
						Thread.sleep(sleep);
					}catch(InterruptedException exception){
						exception.printStackTrace();
						break;
					}
				}
			}
			start = System.nanoTime();
		}
	}

	public boolean isRunning(){
		return this.running;
	}

	public void setRunning(final boolean running){
		this.running = running;
	}

	public void clearBoards(){
		synchronized(this.playerBoards){
			final Iterator<PlayerBoard> iterator = this.playerBoards.values().iterator();
			while(iterator.hasNext()){
				iterator.next().remove();
				iterator.remove();
			}
		}
	}

	public Map<UUID, PlayerBoard> getPlayerBoards(){
		return this.playerBoards;
	}
}