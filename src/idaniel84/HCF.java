package idaniel84;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import idaniel84.balance.*;
import idaniel84.classes.PvpClassManager;
import idaniel84.classes.archer.ArcherClass;
import idaniel84.combatlog.CombatLogListener;
import idaniel84.combatlog.CustomEntityRegistration;
import idaniel84.command.*;
import idaniel84.command.crate.KeyListener;
import idaniel84.command.crate.LootExecutor;
import idaniel84.command.death.DeathExecutor;
import idaniel84.command.event.EnderDragonEventCommand;
import idaniel84.command.event.FfaEventCommand;
import idaniel84.command.event.SumoEventCommand;
import idaniel84.command.event.ThimbleEventCommand;
import idaniel84.command.lives.LivesExecutor;
import idaniel84.command.spawn.SpawnCommand;
import idaniel84.command.spawn.TokenExecutor;
import idaniel84.config.EventManager;
import idaniel84.config.FlatFileEventManager;
import idaniel84.config.PotionLimiterData;
import idaniel84.deathban.Deathban;
import idaniel84.deathban.DeathbanListener;
import idaniel84.deathban.DeathbanManager;
import idaniel84.deathban.FlatFileDeathbanManager;
import idaniel84.faction.FactionExecutor;
import idaniel84.faction.FactionManager;
import idaniel84.faction.FactionMember;
import idaniel84.faction.FlatFileFactionManager;
import idaniel84.faction.argument.FactionFocusArgument;
import idaniel84.faction.claim.Claim;
import idaniel84.faction.claim.ClaimHandler;
import idaniel84.faction.claim.ClaimWandListener;
import idaniel84.faction.claim.Subclaim;
import idaniel84.faction.type.*;
import idaniel84.kothgame.CaptureZone;
import idaniel84.kothgame.EventExecutor;
import idaniel84.kothgame.EventScheduler;
import idaniel84.kothgame.conquest.ConquestExecutor;
import idaniel84.kothgame.eotw.EOTWHandler;
import idaniel84.kothgame.eotw.EotwCommand;
import idaniel84.kothgame.eotw.EotwListener;
import idaniel84.kothgame.faction.CapturableFaction;
import idaniel84.kothgame.faction.ConquestFaction;
import idaniel84.kothgame.faction.KnockKothFaction;
import idaniel84.kothgame.faction.KothFaction;
import idaniel84.kothgame.koth.KothExecutor;
import idaniel84.listener.*;
import idaniel84.listener.fixes.*;
import idaniel84.scoreboard.ScoreboardHandler;
import idaniel84.timer.TimerExecutor;
import idaniel84.timer.TimerManager;
import idaniel84.timer.type.SotwTimer;
import idaniel84.user.FactionUser;
import idaniel84.user.UserManager;
import idaniel84.utils.*;
import idaniel84.visualise.ProtocolLibHook;
import idaniel84.visualise.VisualiseHandler;
import idaniel84.visualise.WallBorderListener;
import net.veilmc.base.BasePlugin;
import net.veilmc.base.ServerHandler;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class HCF extends JavaPlugin{
	public static final Joiner SPACE_JOINER = Joiner.on(' ');
	public static final Joiner COMMA_JOINER = Joiner.on(", ");
	public static final long HOUR = TimeUnit.HOURS.toMillis(1);
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
	private static HCF plugin;
	public EventScheduler eventScheduler;
	public String scoreboardTitle;
	public long NEXT_KOTH = -1;
	public ArrayList<String> players;
	private Message message;
	private List<String> eventGames = new ArrayList<>();
	private Random random = new Random();
	private WorldEditPlugin worldEdit;
	private FoundDiamondsListener foundDiamondsListener;
	private ClaimHandler claimHandler;
	private SotwTimer sotwTimer;
	private DeathbanManager deathbanManager;
	private EconomyManager economyManager;
	private EventManager eventManager;
	private EOTWHandler eotwHandler;
	private FactionManager factionManager;
	private PvpClassManager pvpClassManager;
	private ScoreboardHandler scoreboardHandler;
	private TimerManager timerManager;
	private UserManager userManager;
	private VisualiseHandler visualiseHandler;
	private String armor;

	public static HCF getPlugin(){
		return plugin;
	}

	public static String getRemaining(long millis, boolean milliseconds){
		return HCF.getRemaining(millis, milliseconds, true);
	}

	public static String getRemaining(long duration, boolean milliseconds, boolean trail){
		if(milliseconds && duration < MINUTE){
			return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format((double) duration * 0.001) + 's';
		}
		return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
	}

	public static HCF getInstance(){
		return plugin;
	}

	private void registerGames(){
		eventGames.addAll(getFactionManager().getFactions().stream().filter(faction -> faction instanceof KothFaction).map(Faction::getName).collect(Collectors.toList()));
		eventGames.forEach(System.out::print);
	}

	public void onEnable(){
		plugin = this;

		CustomEntityRegistration.registerCustomEntities();
		ProtocolLibHook.hook(this);

		this.saveDefaultConfig();

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		ConfigurationService.init(this.getConfig());
		PotionLimiterData.getInstance().setup(this);
		PotionLimitListener.reload();

		Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
		this.worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin) wep : null;

		this.registerConfiguration();
		this.registerCommands();
		this.registerManagers();
		this.registerListeners();

		Cooldowns.createCooldown("revive_cooldown");
		Cooldowns.createCooldown("Assassin_item_cooldown");
		Cooldowns.createCooldown("Archer_item_cooldown");
		Cooldowns.createCooldown("Archer_jump_cooldown");
		Cooldowns.createCooldown("report_cooldown");
		Cooldowns.createCooldown("helpop_cooldown");

		this.scoreboardTitle = Chat.translateColors(getConfig().getString("scoreboard.title"));
		this.armor = Chat.translateColors(getConfig().getString("scoreboard.active-class"));

		this.timerManager.enable();

		registerGames();

		getLogger().info(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		getLogger().info("");
		getLogger().info(ChatColor.translateAlternateColorCodes('&', "&e[" + HCF.getPlugin().getDescription().getName() + "] Plugin loaded!"));
		getLogger().info(ChatColor.translateAlternateColorCodes('&', "&e[" + HCF.getPlugin().getDescription().getName() + "] &eVersion: " + HCF.getPlugin().getDescription().getVersion()));
		getLogger().info("");
		getLogger().info(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			List<String> donors = new ArrayList<>();
			Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("vip.broadcast") && !player.isOp() && !player.hasPermission("*")).forEach(player -> donors.add(player.getDisplayName()));

			HCF.getInstance().getConfig().getStringList("online-medics").forEach(s ->
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', s.replace("%LINE%", BukkitUtils.STRAIGHT_LINE_DEFAULT + "")
							.replace("%MEDICS%", donors.isEmpty() ?
									"&cNone" :
									donors.toString().replace("[", "").replace("]", "")))));

		}, 15 * 20L, (10 * 60) * 20L);

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
			Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Starting backup of data");
			Bukkit.getWorlds().forEach(world -> {
				world.setThundering(false);
				world.setStorm(false);
			});
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			saveData();
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lAutoSave &eTask was completed."));
		}, 10 * 20L, (60 * 15) * 20L);


		int seconds = (ConfigurationService.KIT_MAP ? 300 : 7200);
		startNewKoth(seconds);
		NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lKOTH &7� &eA new KOTH will be starting in&5 " + (ConfigurationService.KIT_MAP ? "5 minutes" : "2 hours") + "!"));
	}

	public void startNewKoth(int seconds){
		this.getLogger().info("Starting koth in " + seconds + " seconds. (" + getNextGame() + ")");
		NEXT_KOTH = System.currentTimeMillis() + (seconds * 1000);
		new BukkitRunnable(){
			public void run(){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "event start " + getNextGame());
				NEXT_KOTH = -1;
			}
		}.runTaskLater(this, 20L * seconds);
	}

	public void rotateGames(){
		this.getLogger().info("Game list was rotated.");
		Collections.rotate(eventGames, -1);
	}

	public String getNextGame(){
		return eventGames.get(0);
	}

	public void saveData(){
		BasePlugin.getPlugin().getServerHandler().saveServerData(); //Base data

		Bukkit.getOnlinePlayers().forEach(Player::saveData);//Save data

		this.deathbanManager.saveDeathbanData(); //Deathbans
		this.economyManager.saveEconomyData(); //Balance
		this.factionManager.saveFactionData(); //Factions
		this.userManager.saveUserData(); //User settings
		this.eventManager.saveEventsData();
	}

	public void onDisable(){
		Bukkit.getServer().savePlayers();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		CustomEntityRegistration.unregisterCustomEntities();
		CombatLogListener.removeCombatLoggers();
		this.pvpClassManager.onDisable();
		this.scoreboardHandler.clearBoards();
		this.saveData();
		this.timerManager.disable();
		getLogger().info("Plugin successfully disabled.");
	}

	private void registerConfiguration(){
		ConfigurationSerialization.registerClass(CaptureZone.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Subclaim.class);
		ConfigurationSerialization.registerClass(Deathban.class);
		ConfigurationSerialization.registerClass(FactionUser.class);
		ConfigurationSerialization.registerClass(ClaimableFaction.class);
		ConfigurationSerialization.registerClass(ConquestFaction.class);
		ConfigurationSerialization.registerClass(CapturableFaction.class);
		ConfigurationSerialization.registerClass(KothFaction.class);
		ConfigurationSerialization.registerClass(EndPortalFaction.class);
		ConfigurationSerialization.registerClass(Faction.class);
		ConfigurationSerialization.registerClass(KnockKothFaction.class);
		ConfigurationSerialization.registerClass(FactionMember.class);
		ConfigurationSerialization.registerClass(PlayerFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.class);
		ConfigurationSerialization.registerClass(SpawnFaction.class);
		ConfigurationSerialization.registerClass(GlowstoneFaction.class);
		ConfigurationSerialization.registerClass(FFAFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
		ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
	}

	private void registerListeners(){
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new ArcherClass(this), this);
		manager.registerEvents(new AutoRespawnListener(this), this);
		manager.registerEvents(new AutoSmeltOreListener(), this);
		manager.registerEvents(new BeaconStrengthFixListener(), this);
		manager.registerEvents(new BlockHitFixListener(), this);
		manager.registerEvents(new BlockJumpGlitchFixListener(), this);
		manager.registerEvents(new BoatGlitchFixListener(), this);
		manager.registerEvents(new BookDeenchantListener(), this);
		manager.registerEvents(new BookQuillFixListener(), this);
		manager.registerEvents(new BorderListener(), this);
		manager.registerEvents(new BottledExpListener(), this);
		manager.registerEvents(new ChatGameCommand(), this);
		manager.registerEvents(new ChatListener(this), this);
		manager.registerEvents(new ClaimWandListener(this), this);
		manager.registerEvents(new CobbleCommand(), this);
		manager.registerEvents(new ColonFix(), this);
		manager.registerEvents(new CombatLogListener(this), this);
		manager.registerEvents(new CoreListener(this), this);
		manager.registerEvents(new CreeperFriendlyListener(), this);
		manager.registerEvents(new CrowbarListener(this), this);
		manager.registerEvents(new DeathbanListener(this), this);
		manager.registerEvents(new DeathListener(this), this);
		manager.registerEvents(new DeathMessageListener(this), this);
		manager.registerEvents(new DeathSignListener(this), this);
		manager.registerEvents(new DonorOnlyListener(), this);
		manager.registerEvents(new DupeGlitchFix(), this);
		manager.registerEvents(new ElevatorListener(this), this);
		manager.registerEvents(new EnchantLimitListener(), this);
		manager.registerEvents(new EnchantSecurityListener(), this);
		manager.registerEvents(new EnderChestCommand(), this);
		manager.registerEvents(new EnderChestRemovalListener(), this);
		manager.registerEvents(new EndermanFixListener(), this);
		manager.registerEvents(new EndListener(), this);
		manager.registerEvents(new EndPortalCommand(this), this);
		manager.registerEvents(new EntityLimitListener(), this);
		manager.registerEvents(new EotwListener(this), this);
		manager.registerEvents(new EventSignListener(), this);
		manager.registerEvents(new ExpMultiplierListener(), this);
		manager.registerEvents(new FactionListener(this), this);
		manager.registerEvents(new FactionsCoreListener(this), this);
		manager.registerEvents(new FlatFileFactionManager(this), this);
		manager.registerEvents(new FoundDiamondsListener(), this);
		manager.registerEvents(new FurnaceSmeltSpeederListener(this), this);
		manager.registerEvents(new HitDetectionListener(), this);
		manager.registerEvents(new HungerFixListener(), this);
		manager.registerEvents(new InfinityArrowFixListener(), this);
		manager.registerEvents(new ItemStatTrackingListener(), this);
		manager.registerEvents(new KeyListener(this), this);
		manager.registerEvents(new KitListener(this), this);
		manager.registerEvents(new MinecartElevatorListener(), this);
		manager.registerEvents(new MobSpawnDisabledOnKitsListener(), this);
		manager.registerEvents(new NetherPortalPearlFixListener(), this);
		manager.registerEvents(new PearlGlitchListener(this), this);
		manager.registerEvents(new PexCrashFix(), this);
		manager.registerEvents(new PortalFixListener(), this);
		manager.registerEvents(new PotionLimitListener(), this);
		manager.registerEvents(new PotionListener(), this);
		manager.registerEvents(new ShopSignListener(this), this);
		manager.registerEvents(new SignSubclaimListener(this), this);
		manager.registerEvents(new SkullListener(), this);
		manager.registerEvents(new SotwListener(this), this);
		manager.registerEvents(new SpawnEntitiesCommand(), this);
		//manager.registerEvents(new StatTrackListener(), this);
		manager.registerEvents(new StoreCommand(this), this);
		manager.registerEvents(new UnRepairableListener(), this);
		manager.registerEvents(new VoidGlitchFixListener(), this);
		manager.registerEvents(new WallBorderListener(this), this);
		manager.registerEvents(new WeatherFixListener(), this);
		manager.registerEvents(new WorldListener(this), this);
		manager.registerEvents(new NearPearlFixListener(), this);
		manager.registerEvents(new FishingRodHitchListener(),this);
	}

	private void registerCommands(){
		this.getCommand("chatgame").setExecutor(new ChatGameCommand());
		this.getCommand("check").setExecutor(new CheckCommand(this));
		this.getCommand("cobble").setExecutor(new CobbleCommand());
		this.getCommand("conquest").setExecutor(new ConquestExecutor(this));
		this.getCommand("coords").setExecutor(new CoordsCommand(this));
		this.getCommand("crowbar").setExecutor(new CrowbarCommand());
		this.getCommand("crowgive").setExecutor(new CrowbarGiveCommand());
		this.getCommand("death").setExecutor(new DeathExecutor(this));
		this.getCommand("economy").setExecutor(new EconomyCommand(this));
		this.getCommand("enddragon").setExecutor(new EnderDragonEventCommand(this));
		this.getCommand("enderchest").setExecutor(new EnderChestCommand());
		this.getCommand("endportal").setExecutor(new EndPortalCommand(this));
		this.getCommand("eotw").setExecutor(new EotwCommand(this));
		this.getCommand("faction").setExecutor(new FactionExecutor(this));
		this.getCommand("ffa").setExecutor(new FFACommand());
		this.getCommand("ffaevent").setExecutor(new FfaEventCommand(this));
		this.getCommand("focus").setExecutor(new FactionFocusArgument(this));
		this.getCommand("game").setExecutor(new EventExecutor(this));
		this.getCommand("gopple").setExecutor(new GoppleCommand(this));
		this.getCommand("help").setExecutor(new HelpCommand());
		this.getCommand("koth").setExecutor(new KothExecutor(this));
		this.getCommand("lives").setExecutor(new LivesExecutor(this));
		this.getCommand("location").setExecutor(new LocationCommand(this));
		this.getCommand("logout").setExecutor(new LogoutCommand(this));
		this.getCommand("loot").setExecutor(new LootExecutor(this));
		this.getCommand("mapkit").setExecutor(new MapKitCommand(this));
		this.getCommand("revive").setExecutor(new ReviveCommand(this));
		this.getCommand("miner").setExecutor(new MinerCommand());
		this.getCommand("ores").setExecutor(new OresCommand());
		this.getCommand("pay").setExecutor(new PayCommand(this));
		this.getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
		this.getCommand("safestop").setExecutor(new SafestopCommand());
		this.getCommand("savedata").setExecutor(new SaveDataCommand());
		this.getCommand("sendcoords").setExecutor(new SendCoordsCommand(this));
		this.getCommand("servertime").setExecutor(new ServerTimeCommand());
		this.getCommand("setborder").setExecutor(new SetBorderCommand());
		this.getCommand("sotw").setExecutor(new SotwCommand(this));
		this.getCommand("spawn").setExecutor(new SpawnCommand(this));
		this.getCommand("spawnentities").setExecutor(new SpawnEntitiesCommand());
		this.getCommand("spawner").setExecutor(new SpawnerCommand(this));
		this.getCommand("staffrevive").setExecutor(new StaffReviveCommand(this));
		this.getCommand("statreset").setExecutor(new StatResetCommand(this));
		this.getCommand("stats").setExecutor(new StatsCommand());
		this.getCommand("store").setExecutor(new StoreCommand(this));
		this.getCommand("sumoevent").setExecutor(new SumoEventCommand(this));
		this.getCommand("supplydrop").setExecutor(new SupplydropCommand(this));
		this.getCommand("thimbleevent").setExecutor(new ThimbleEventCommand(this));
		this.getCommand("timer").setExecutor(new TimerExecutor(this));
		this.getCommand("toggleend").setExecutor(new ToggleEnd(this));
		this.getCommand("togglefd").setExecutor(new TogglefdCommand());
		this.getCommand("token").setExecutor(new TokenExecutor(this));
		final Map<String, Map<String, Object>> map = this.getDescription().getCommands();

		for(final Map.Entry<String, Map<String, Object>> entry : map.entrySet()){
			final PluginCommand command = this.getCommand(entry.getKey());
			command.setPermission("hcf.command." + entry.getKey());
		}

	}

	private void registerManagers(){
		this.claimHandler = new ClaimHandler(this);
		this.deathbanManager = new FlatFileDeathbanManager(this);
		this.economyManager = new FlatFileEconomyManager(this);
		this.eotwHandler = new EOTWHandler(this);
		this.eventScheduler = new EventScheduler(this);
		this.factionManager = new FlatFileFactionManager(this);
		this.pvpClassManager = new PvpClassManager(this);
		this.timerManager = new TimerManager(this);
		this.scoreboardHandler = new ScoreboardHandler(this);
		this.userManager = new UserManager(this);
		this.visualiseHandler = new VisualiseHandler();
		this.sotwTimer = new SotwTimer();
		this.message = new Message(this);
		this.eventManager = new FlatFileEventManager(this);
	}

	public Message getMessage(){
		return this.message;
	}

	public ServerHandler getServerHandler(){
		return BasePlugin.getPlugin().getServerHandler();
	}

	public Random getRandom(){
		return this.random;
	}

	public WorldEditPlugin getWorldEdit(){
		return this.worldEdit;
	}

	public ClaimHandler getClaimHandler(){
		return this.claimHandler;
	}

	public DeathbanManager getDeathbanManager(){
		return this.deathbanManager;
	}

	public EconomyManager getEconomyManager(){
		return this.economyManager;
	}

	public EventManager getEventManager(){
		return this.eventManager;
	}

	public EOTWHandler getEotwHandler(){
		return this.eotwHandler;
	}

	public FactionManager getFactionManager(){
		return this.factionManager;
	}

	public PvpClassManager getPvpClassManager(){
		return this.pvpClassManager;
	}

	public ScoreboardHandler getScoreboardHandler(){
		return this.scoreboardHandler;
	}

	public TimerManager getTimerManager(){
		return this.timerManager;
	}

	public UserManager getUserManager(){
		return this.userManager;
	}

	public VisualiseHandler getVisualiseHandler(){
		return this.visualiseHandler;
	}

	public SotwTimer getSotwTimer(){
		return this.sotwTimer;
	}

	public String scoreboardTitle(){
		return this.scoreboardTitle;
	}

	public String getKothRemaining(){
		long duration = NEXT_KOTH - System.currentTimeMillis();
		return org.apache.commons.lang.time.DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
	}
}