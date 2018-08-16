package net.veilmc.hcf.tab;

import com.google.common.collect.Lists;
import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.FactionManager;
import net.veilmc.hcf.faction.type.PlayerFaction;
import net.veilmc.util.MapSorting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TUtils{
	private HCF plugin;
	private FactionManager factionManager;

	TUtils(HCF plugin){
		this.plugin = plugin;
		this.factionManager = plugin.getFactionManager();
	}

	public String getCardinalDirection(final Player player){
		double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;
		if(rotation < 0.0){
			rotation += 360.0;
		}
		if(0.0 <= rotation && rotation < 22.5){
			return "W";
		}
		if(22.5 <= rotation && rotation < 67.5){
			return "NW";
		}
		if(67.5 <= rotation && rotation < 112.5){
			return "N";
		}
		if(112.5 <= rotation && rotation < 157.5){
			return "NE";
		}
		if(157.5 <= rotation && rotation < 202.5){
			return "E";
		}
		if(202.5 <= rotation && rotation < 247.5){
			return "SE";
		}
		if(247.5 <= rotation && rotation < 292.5){
			return "S";
		}
		if(292.5 <= rotation && rotation < 337.5){
			return "SW";
		}
		if(337.5 <= rotation && rotation < 360.0){
			return "W";
		}
		return null;
	}

	public String trs(final Player player, String path){
		final Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<>();
		PlayerFaction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());

		Bukkit.getOnlinePlayers().forEach(p -> {
			PlayerFaction pFac = factionManager.getPlayerFaction(p);
			if(pFac != null){
				factionOnlineMap.put(pFac, factionOnlineMap.getOrDefault(pFac, 0) + 1);
			}
		});

		if(path.contains("%f_list_")){
			final List<Map.Entry<PlayerFaction, Integer>> sortedMap = (List<Map.Entry<PlayerFaction, Integer>>) MapSorting.sortedValues(factionOnlineMap, (Comparator) Comparator.reverseOrder());
			for(int i = 0; i < 20; ++i){
				if(i >= sortedMap.size()){
					path = path.replace("%f_list_" + (i + 1) + "%", "");
				}else{
					String name = ChatColor.RED + sortedMap.get(i).getKey().getName();
					if(playerFaction != null){
						name = sortedMap.get(i).getKey().getDisplayName(playerFaction);
						int max = (name.length() < 11) ? name.length() : 11;
						name = name.substring(0, max);
					}
					path = path.replace("%f_list_" + (i + 1) + "%", String.valueOf(name) + ChatColor.GRAY + " (" + sortedMap.get(i).getValue() + ")");
				}
			}
		}


		path = path.replace("%player_kills%", player.getStatistic(Statistic.PLAYER_KILLS) + "")
				.replace("%player_deaths%", player.getStatistic(Statistic.DEATHS) + "")
				.replace("%player_location%", "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ") [" + getCardinalDirection(player) + "]")
				.replace("%player_bal%", plugin.getEconomyManager().getBalance(player.getUniqueId()) + "")
				.replace("%player_lives%", plugin.getDeathbanManager().getLives(player.getUniqueId()) + "")
				.replace("%player_ping%", ((CraftPlayer) player).getHandle().ping + "")
				.replace("%online_players%", Bukkit.getOnlinePlayers().size() + "")
				.replace("%max_players%", Bukkit.getMaxPlayers() + "");


		if(playerFaction == null){
			return path;
		}

		path.replace("%faction_location%", factionManager.getFactionAt(player.getLocation()).getDisplayName(player))
				.replace("%f_title%", "Faction Info");


		if(playerFaction != null){
			if(path.contains("%f_title%")){
				path = path.replace("%f_title%", "Faction Info");
			}
			if(path.contains("%ftag%")){
				path = path.replace("%ftag%", playerFaction.getDisplayName(player));
			}
			if(path.contains("%fdtr%")){
				path = path.replace("%fdtr%", "&7DTR: &a" + String.format("%.2f", playerFaction.getDeathsUntilRaidable()));
			}
			if(path.contains("%fhome%")){
				if(playerFaction.getHome() != null){
					path = path.replace("%fhome%", "&7HQ: &a" + playerFaction.getHome().getBlockX() + ", " + HCF.getInstance().getFactionManager().getPlayerFaction(player.getUniqueId()).getHome().getBlockY() + ", " + HCF.getInstance().getFactionManager().getPlayerFaction(player.getUniqueId()).getHome().getBlockZ());
				}else{
					path = path.replace("%fhome%", "&7HQ: &a" + "None");
				}
			}
			if(path.contains("%fleader%")){
				path = path.replace("%fleader%", playerFaction.getLeader().getName());
			}
			if(path.contains("%fbal%")){
				path = path.replace("%fbal%", "&7Balance: &a" + "$" + playerFaction.getBalance());
			}
			final PlayerFaction playerFaction3 = playerFaction;
			if(path.contains("%fonline%")){
				path = path.replace("%fonline%", "&7Online: &a" + String.valueOf(playerFaction3.getOnlinePlayers().size()) + "/" + playerFaction3.getMembers().size());
			}
			final List<Player> online = Lists.newArrayList(playerFaction3.getOnlinePlayers());
			online.sort(Comparator.comparing(HumanEntity::getName));
			online.sort(Comparator.comparingInt(o -> playerFaction3.getMember(o).getRole().ordinal()));
			for(int j = 0; j < 16; ++j){
				if(j >= online.size()){
					path = path.replace("%f_member_" + j + "%", "");
				}else{
					path = path.replace("%f_member_" + (j + 1) + "%", "&a" + String.valueOf(playerFaction3.getMember(online.get(j)).getRole().getAstrix()) + online.get(j).getName());
				}
			}
		}else{
			if(path.contains("%f_title%")){
				return "";
			}
			if(path.contains("%ftag%")){
				return "";
			}
			if(path.contains("%fdtr%")){
				return "";
			}
			if(path.contains("%fhome%")){
				return "";
			}
			if(path.contains("%fleader%")){
				return "";
			}
			if(path.contains("%fbal%")){
				return "";
			}
			if(path.contains("%fonline%")){
				return "";
			}
			for(int i = 1; i < 31; ++i){
				path = path.replace("%f_member_" + i + "%", "");
			}
			for(int i = 1; i < 31; ++i){
				path = path.replace("%f_list_" + i + "%", "");
			}
		}
		return ChatColor.translateAlternateColorCodes('&', path);
	}

}
