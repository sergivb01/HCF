package net.veilmc.hcf.utils;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class ConfigurationService{

	public static final TimeZone SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
	public static final List<String> DISALLOWED_FACTION_NAMES = ImmutableList.of("velt", "faithful", "hcteams", "hcteamseotw", "hcteamssotw", "exploitesquad", "staff", "mod", "owner", "dev", "admin", "ipvp");
	public static final Map<Enchantment, Integer> ENCHANTMENT_LIMITS = new HashMap<Enchantment, Integer>();
	public static final Map<PotionType, Integer> POTION_LIMITS = new EnumMap<PotionType, Integer>(PotionType.class);
	public static final Map<World.Environment, Double> SPAWN_RADIUS_MAP = new EnumMap<World.Environment, Double>(World.Environment.class);
	public static int SPAWNER_PRICE = 40000;

	public static String TEAMSPEAK_IP;

	public static ChatColor TEAMMATE_COLOUR;
	public static ChatColor ALLY_COLOUR;
	public static ChatColor ENEMY_COLOUR;
	public static ChatColor SAFEZONE_COLOUR;
	public static ChatColor ROAD_COLOUR;
	public static ChatColor TARGET;
	public static ChatColor WARZONE_COLOUR;
	public static ChatColor WILDERNESS_COLOUR;
	public static ChatColor GLOWSTONE_COLOUR;

	public static int WARZONE_RADIUS;

	public static int FACTION_PLAYER_LIMIT;
	public static int MAX_ALLIES_PER_FACTION;

	public static long DTR_MILLIS_BETWEEN_UPDATES;

	public static String DTR_WORDS_BETWEEN_UPDATES;
	public static ChatColor BASECOLOUR;

	public static int CONQUEST_REQUIRED_WIN_POINTS = 100;
	public static long DEFAULT_DEATHBAN_DURATION;

	public static Map<World.Environment, Integer> BORDER_SIZES;
	public static boolean CRATE_BROADCASTS;

	//public static List<Short> DISABLED_POTIONS;

	public static String COBBLE_ENABLED;
	public static String COBBLE_DISABLED;

	public static String LOGOUT_ALREADY_STARTED;
	public static String LOGOUT_STARTED;
	public static String LOGOUT_DISCONNECT;

	public static String ENDERPEARL_TIMER;
	public static ChatColor ENDERPEARL_COLOUR;
	public static String ARCHER_TIMER;
	public static ChatColor ARCHER_COLOUR;
	public static String LOGOUT_TIMER;
	public static ChatColor LOGOUT_COLOUR;
	public static String NOTCH_APPLE_TIMER;
	public static ChatColor NOTCH_APPLE_COLOUR;
	public static String PVP_CLASS_WARMUP_TIMER;
	public static ChatColor PVP_CLASS_WARMUP_COLOUR;
	public static String PVPTIMER_TIMER;
	public static ChatColor PVPTIMER_COLOUR;
	public static String SPAWNTAG_TIMER;
	public static ChatColor SPAWNTAG_COLOUR;
	public static String STUCK_TIMER;
	public static ChatColor STUCK_COLOUR;
	public static String TELEPORT_TIMER;
	public static ChatColor TELEPORT_COLOUR;
	public static String SOTW_TIMER;
	public static ChatColor SOTW_COLOUR;
	public static String EVENT_TIMER;
	public static ChatColor EVENT_COLOUR;

	public static String SOTW_STARTED;
	public static String SOTW_NOT_ACTIVE;
	public static String SOTW_CANCELLED;
	public static String SOTW_ENDED_ONE;
	public static String SOTW_ENDED_TWO;

	public static String REVIVE_MESSAGE;

	public static String DEATHBAN_BYPASS;
	public static String DEATHBANNED_EOTW;
	public static String DEATHBANNED_ACTIVE;
	public static String STILL_DEATHBANNED;
	public static String DEATHBANNED_USE_A_LIFE;
	public static String DEATHBANNED_EOTW_ENTIRE;

	public static String END_CANNOT_BUILD;
	public static String WORLD_CANNOT_BUILD;
	public static String FAILED_PEARL;
	public static String TELEPORTED_SPAWN;
	public static String CANNOT_ATTACK;
	public static String IN_FACTION;
	public static String ALLY_FACTION;
	public static String CANNOT_BUILD;
	public static String ENDERPEARL_COOLDOWN_EXPIRED;
	public static String ENDERPEARL_ITEM;
	public static String SPAWN_TAGGED;
	//public static String LEAVING_ENTERING_MESSAGE;

	//public static String CLASS_EQUIPPED;
	//public static String CLASS_UNEQUIPPED;

    /*public static String PVPTIMER_EXPIRED;
    public static String PVPTIMER_PLAYERSPAWN;
    public static String PVPTIMER_PAUSED;
    public static String PVPTIMER_UNPAUSED;
    public static String PVPTIMER_STARTED;*/

	public static boolean KIT_MAP;
	public static boolean VEILZ;
	public static boolean FFA;
	public static boolean DEV;

	public static long VEILZ_REGEN;
	public static int VEILZ_FACTIONCOST;

	public static boolean TAB;
	public static boolean DIAMOND_ORE_ALERTS = false;
	public static int UNBUILDABLE_RANGE;

//    public static List<String> SHOW;

	public static void init(FileConfiguration config){

		KIT_MAP = config.getBoolean("kit-map");
		VEILZ = config.getBoolean("veilz");
		FFA = config.getBoolean("ffa");
		DEV = config.getBoolean("dev", false);
		TAB = config.getBoolean("tab");

		TEAMSPEAK_IP = config.getString("server-info.teamspeak");

		WARZONE_RADIUS = config.getInt("warzone");

//    	SHOW = config.getStringList("show");

		FACTION_PLAYER_LIMIT = config.getInt("faction-settings.max-players", 5);
		MAX_ALLIES_PER_FACTION = config.getInt("faction-settings.max-allies", 5);

		BORDER_SIZES = new EnumMap<>(World.Environment.class);

		POTION_LIMITS.put(PotionType.INSTANT_DAMAGE, config.getInt("potion-limits.instant_damage", 1) - 1);
		POTION_LIMITS.put(PotionType.REGEN, config.getInt("potion-limits.regen", 1) - 1);
		POTION_LIMITS.put(PotionType.STRENGTH, config.getInt("potion-limits.strength", 1) - 1);
		POTION_LIMITS.put(PotionType.WEAKNESS, config.getInt("potion-limits.weakness", 1) - 1);
		POTION_LIMITS.put(PotionType.SLOWNESS, config.getInt("potion-limits.slowness", 1) - 1);
		POTION_LIMITS.put(PotionType.INVISIBILITY, config.getInt("potion-limits.invis", 1) - 1);
		POTION_LIMITS.put(PotionType.POISON, config.getInt("potion-limits.poison", 1) - 1);

		Enchantment[] limitedEnchants = new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DAMAGE_ALL, Enchantment.ARROW_DAMAGE,
				Enchantment.KNOCKBACK, Enchantment.ARROW_KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.THORNS, Enchantment.ARROW_FIRE};
		for(Enchantment enchant : limitedEnchants){
			try{
				ENCHANTMENT_LIMITS.put(enchant, config.getInt("enchant-limits." + enchant.getName()));
			}catch(Exception e){
				System.out.println("Error getting enchantment limit. Tried getting the limit for " + enchant.getName());
			}
		}

		BORDER_SIZES.put(World.Environment.NORMAL, Integer.valueOf(3000));
		BORDER_SIZES.put(World.Environment.NETHER, Integer.valueOf(1000));
		BORDER_SIZES.put(World.Environment.THE_END, Integer.valueOf(500));

		SPAWN_RADIUS_MAP.put(World.Environment.NORMAL, Double.valueOf(63D));
		SPAWN_RADIUS_MAP.put(World.Environment.NETHER, Double.valueOf(22.5D));
		SPAWN_RADIUS_MAP.put(World.Environment.THE_END, Double.valueOf(48.5D));

		DEFAULT_DEATHBAN_DURATION = TimeUnit.MINUTES.toMillis(config.getInt("deathban-time", 120));


		VEILZ_REGEN = TimeUnit.MINUTES.toMillis(config.getInt("veilz-options.regen", 5));
		VEILZ_FACTIONCOST = config.getInt("veilz-options.faction-cost", 10);


		TEAMMATE_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.teammate", "&2").replace("&", "").trim());
		ALLY_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.ally", "&3").replace("&", "").trim());
		ENEMY_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.enemy", "&c").replace("&", "").trim());
		SAFEZONE_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.safezone", "&b").replace("&", "").trim());
		ROAD_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.road", "&c").replace("&", "").trim());
		TARGET = ChatColor.getByChar(config.getString("faction-settings.colors.target", "&d").replace("&", "").trim());
		WARZONE_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.warzone", "&c").replace("&", "").trim());
		BASECOLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.default", "&7").replace("&", "").trim());
		WILDERNESS_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.wilderness", "&d").replace("&", "").trim());
		GLOWSTONE_COLOUR = ChatColor.getByChar(config.getString("faction-settings.colors.glowstone", "&b").replace("&", "").trim());

		DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45);
		DTR_WORDS_BETWEEN_UPDATES = DurationFormatUtils.formatDurationWords(DTR_MILLIS_BETWEEN_UPDATES, true, true);

		CRATE_BROADCASTS = false;

		COBBLE_ENABLED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cobble-enabled"));
		COBBLE_DISABLED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cobble-disabled"));

		LOGOUT_ALREADY_STARTED = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.logout-already-started"));
		LOGOUT_STARTED = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.logout-started"));
		LOGOUT_DISCONNECT = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.logout-safely"));

		SOTW_STARTED = ChatColor.translateAlternateColorCodes('&', config.getString("sotw.started"));
		SOTW_CANCELLED = ChatColor.translateAlternateColorCodes('&', config.getString("sotw.cancelled"));
		SOTW_NOT_ACTIVE = ChatColor.translateAlternateColorCodes('&', config.getString("sotw.not-active"));
		SOTW_ENDED_ONE = ChatColor.translateAlternateColorCodes('&', config.getString("sotw.ended-one"));
		SOTW_ENDED_TWO = ChatColor.translateAlternateColorCodes('&', config.getString("sotw.ended-two"));

		DEATHBAN_BYPASS = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deathban-bypass"));
		DEATHBANNED_EOTW = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deathbanned-eotw"));
		DEATHBANNED_ACTIVE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deathbanned-active"));
		STILL_DEATHBANNED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.still-deathbanned"));
		DEATHBANNED_EOTW_ENTIRE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.deathbanned-eotw-entire"));

		END_CANNOT_BUILD = ChatColor.translateAlternateColorCodes('&', config.getString("messages.end-cannot-build"));
		WORLD_CANNOT_BUILD = ChatColor.translateAlternateColorCodes('&', config.getString("messages.world-cannot-build"));

		FAILED_PEARL = ChatColor.translateAlternateColorCodes('&', config.getString("messages.failed-pearl"));

		TELEPORTED_SPAWN = ChatColor.translateAlternateColorCodes('&', config.getString("messages.teleport-spawn"));

		CANNOT_ATTACK = ChatColor.translateAlternateColorCodes('&', config.getString("messages.cannot-attack"));
		IN_FACTION = ChatColor.translateAlternateColorCodes('&', config.getString("messages.in-faction"));
		ALLY_FACTION = ChatColor.translateAlternateColorCodes('&', config.getString("messages.ally-faction"));

		REVIVE_MESSAGE = ChatColor.translateAlternateColorCodes('&', config.getString("messages.revive-message"));
		LOGOUT_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.logout-timer"));
		LOGOUT_COLOUR = ChatColor.getByChar(config.getString("timers.logout-color", "&e").replace("&", "").trim());
		ENDERPEARL_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.enderpearl-timer"));
		ENDERPEARL_COLOUR = ChatColor.getByChar(config.getString("timers.enderpearl-color", "&e").replace("&", "").trim());
		ARCHER_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.archer-timer"));
		ARCHER_COLOUR = ChatColor.getByChar(config.getString("timers.archer-color", "&c").replace("&", "").trim());
		NOTCH_APPLE_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.notch-apple-timer"));
		NOTCH_APPLE_COLOUR = ChatColor.getByChar(config.getString("timers.notch-apple-color", "&6").replace("&", "").trim());
		PVP_CLASS_WARMUP_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.pvp-class-warmup-timer"));
		PVP_CLASS_WARMUP_COLOUR = ChatColor.getByChar(config.getString("timers.pvp-class-warmup-color", "&b").replace("&", "").trim());
		PVPTIMER_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.pvptimer-timer"));
		PVPTIMER_COLOUR = ChatColor.getByChar(config.getString("timers.pvptimer-color", "&a").replace("&", "").replace("&l", "").trim());
		SOTW_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.sotw-timer"));
		SOTW_COLOUR = ChatColor.getByChar(config.getString("timers.sotw-color", "&c").replace("&", "").trim());
		SPAWNTAG_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.spawntag-timer"));
		SPAWNTAG_COLOUR = ChatColor.getByChar(config.getString("timers.spawntag-color", "&c").replace("&", "").trim());
		STUCK_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.stuck-timer"));
		STUCK_COLOUR = ChatColor.getByChar(config.getString("timers.stuck-color", "&c").replace("&", "").trim());
		TELEPORT_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.teleport-timer"));
		TELEPORT_COLOUR = ChatColor.getByChar(config.getString("timers.teleport-color", "&9").replace("&", "").trim());
		EVENT_TIMER = ChatColor.translateAlternateColorCodes('&', config.getString("timers.event-timer"));
		EVENT_COLOUR = ChatColor.getByChar(config.getString("timers.event-color", "&9&l").replace("&", "").trim());

		//CLASS_EQUIPPED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.class-equipped"));
		//CLASS_UNEQUIPPED = ChatColor.translateAlternateColorCodes('&', config.getString("messages.class-unequipped"));

		ENDERPEARL_COOLDOWN_EXPIRED = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.enderpearl-expired"));
		ENDERPEARL_ITEM = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.enderpearl-item"));
		SPAWN_TAGGED = ChatColor.translateAlternateColorCodes('&', config.getString("timers.messages.spawn-tagged"));
		UNBUILDABLE_RANGE = config.getInt("unbuildable-range");


	}

}

