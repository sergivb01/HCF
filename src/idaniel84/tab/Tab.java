package idaniel84.tab;

import idaniel84.tab.utils.tinyprotocol.comphenix.TinyProtocol;
import idaniel84.tab.utils.tinyprotocol.comphenix.TinyProtocol;
import idaniel84.tab.utils.tinyprotocol.comphenix.TinyProtocol;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.io.netty.channel.Channel;
import idaniel84.tab.utils.tinyprotocol.comphenix.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.spigotmc.ProtocolInjector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Tab{

	private static final String SKIN_VALUE = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";
	private static final String SKING_SIGNATURE = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=";
	// Static Manager
	private static Plugin plugin;
	private static HashMap<UUID, Tab> players = new HashMap<>();
	private static List<String> tabEntrys = getTabEntrys();
	private static List<String> teamNames = getTeamNames();
	// Class
	private Player player;
	private boolean client18;
	private int tabSize;
	private Scoreboard scoreboard;

	private Tab(Player player){
		this.player = player;
		this.client18 = Protocol.isClient18(player);
		this.tabSize = client18 ? 80 : 60;
		this.scoreboard = player.getScoreboard();
		if(scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())){
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(scoreboard);
		}
		this.setupTab();
		Tab.players.put(player.getUniqueId(), this);
	}

	public static void setPlugin(Plugin plugin){
		Tab.plugin = plugin;
		Protocol.enablePacketListener();
	}

	public static boolean hasTabList(Player player){
		return players.containsKey(player.getUniqueId());
	}

	public static Tab createTabList(Player player){
		return new Tab(player);
	}

	public static Tab deleteTabList(Player player){
		return players.remove(player.getUniqueId());
	}

	public static Tab getByPlayer(Player player){
		return players.get(player.getUniqueId());
	}

	private static ArrayList<String> getTabEntrys(){
		ArrayList<String> list = new ArrayList<>();
		for(int i = 1; i <= 15; i++){
			String entry = ChatColor.values()[i].toString();
			list.add(ChatColor.RED + entry);
			list.add(ChatColor.DARK_RED + entry);
			list.add(ChatColor.GREEN + entry);
			list.add(ChatColor.DARK_GREEN + entry);
			list.add(ChatColor.BLUE + entry);
			list.add(ChatColor.DARK_BLUE + entry);
		}
		return list;
	}

	private static ArrayList<String> getTeamNames(){
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < 80; i++){
			String s = (i < 10 ? "\\u00010" : "\\u0001") + i;
			list.add(s);
		}
		return list;
	}

	private void setupTab(){
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer world = server.getWorldServer(0);
		PlayerInteractManager manager = new PlayerInteractManager(world);

		if(client18){
			scoreboard.registerNewTeam("\\u000181");
		}

		for(int i = 1; i <= tabSize; i++){
			Team team = scoreboard.registerNewTeam(teamNames.get(i - 1));
			GameProfile profile = new GameProfile(UUID.randomUUID(), tabEntrys.get(i - 1));
			EntityPlayer entity = new EntityPlayer(server, world, profile, manager);
			team.addEntry(profile.getName());
			if(client18){
				profile.getProperties().put("textures", new Property("textures", SKIN_VALUE, SKING_SIGNATURE));
				Packet packet = PacketPlayOutPlayerInfo.addPlayer(entity);
				Protocol.sendPacket(player, packet);
			}else{
				Packet packet = PacketPlayOutPlayerInfo.updateDisplayName(entity);
				Protocol.sendPacket(player, packet);
			}
		}

	}

	public int getTabSize(){
		return tabSize;
	}

	public boolean isClient18(){
		return client18;
	}

	public void setSlot(int slot, String value){
		if(client18){
			// 1
			if(slot == 4) slot = 2;
			else if(slot == 7) slot = 3;
			else if(slot == 10) slot = 4;
			else if(slot == 13) slot = 5;
			else if(slot == 16) slot = 6;
			else if(slot == 19) slot = 7;
			else if(slot == 22) slot = 8;
			else if(slot == 25) slot = 9;
			else if(slot == 28) slot = 10;
			else if(slot == 31) slot = 11;
			else if(slot == 34) slot = 12;
			else if(slot == 37) slot = 13;
			else if(slot == 40) slot = 14;
			else if(slot == 43) slot = 15;
			else if(slot == 46) slot = 16;
			else if(slot == 49) slot = 17;
			else if(slot == 52) slot = 18;
			else if(slot == 55) slot = 19;
			else if(slot == 58) slot = 20;
				// 2
			else if(slot == 2) slot = 21;
			else if(slot == 5) slot = 22;
			else if(slot == 8) slot = 23;
			else if(slot == 11) slot = 24;
			else if(slot == 14) slot = 25;
			else if(slot == 17) slot = 26;
			else if(slot == 20) slot = 27;
			else if(slot == 23) slot = 28;
			else if(slot == 26) slot = 29;
			else if(slot == 29) slot = 30;
			else if(slot == 32) slot = 31;
			else if(slot == 35) slot = 32;
			else if(slot == 38) slot = 33;
			else if(slot == 41) slot = 34;
			else if(slot == 44) slot = 35;
			else if(slot == 47) slot = 36;
			else if(slot == 50) slot = 37;
			else if(slot == 53) slot = 38;
			else if(slot == 56) slot = 39;
			else if(slot == 59) slot = 40;
				// 3
			else if(slot == 3) slot = 41;
			else if(slot == 6) slot = 42;
			else if(slot == 9) slot = 43;
			else if(slot == 12) slot = 44;
			else if(slot == 15) slot = 45;
			else if(slot == 18) slot = 46;
			else if(slot == 21) slot = 47;
			else if(slot == 24) slot = 48;
			else if(slot == 27) slot = 49;
			else if(slot == 30) slot = 50;
			else if(slot == 33) slot = 51;
			else if(slot == 36) slot = 52;
			else if(slot == 39) slot = 53;
			else if(slot == 42) slot = 54;
			else if(slot == 45) slot = 55;
			else if(slot == 48) slot = 56;
			else if(slot == 51) slot = 57;
			else if(slot == 54) slot = 58;
			else if(slot == 57) slot = 59;
		}
		Team team = scoreboard.getTeam(teamNames.get(slot - 1));
		updateTeam(team, ChatColor.translateAlternateColorCodes('&', value));
	}

	public void clearTab(){
		for(int i = 1; i <= tabSize; i++){
			setSlot(i, "");
		}
	}

	public void setPlayerListHeaderFooter(String header, String footer){
		if(client18){
			ChatComponentText a = new ChatComponentText(color(header));
			ChatComponentText b = new ChatComponentText(color(footer));
			Packet packet = new ProtocolInjector.PacketTabHeader(a, b);
			Protocol.sendPacket(player, packet);
		}
	}

	private void updateTeam(Team team, String text){
		text = color(text);
		String pre = getFirstSplit(text);
		String suf = getFirstSplit(ChatColor.getLastColors(pre) + getSecondSplit(text));
		team.setPrefix(pre);
		team.setSuffix(suf);
	}

	private String color(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	private String getFirstSplit(String s){
		return s.length() > 16 ? s.substring(0, 16) : s;
	}

	private String getSecondSplit(String s){
		if(s.length() > 32){
			s = s.substring(0, 32);
		}
		return s.length() > 16 ? s.substring(16) : "";
	}

	private static class Protocol{

		private static void enablePacketListener(){
			new TinyProtocol(Tab.plugin){
				@Override
				public Object onPacketOutAsync(Player receiver, Channel channel, Object packet){
					if(packet instanceof PacketPlayOutPlayerInfo){
						Object profile = Reflex.getValue(packet, "player");
						String name = Reflex.getValue(profile, "name", String.class);

						if(!name.startsWith("§")){
							//receiver.sendMessage("Packet canceled from: " + name);

							// Fix steves skins on 1.8
							if(isClient18(receiver) && Reflex.getValue(packet, "action", int.class) == 0){

								if(!receiver.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())){
									String username = Reflex.getValue(packet, "username", String.class);
									Team team = receiver.getScoreboard().getTeam("\\u000181");
									team.addEntry(username);
								}

								return packet;
							}

							// Cancel in 1.7
							return null;
						}
					}

					return packet;
				}

			};
		}

		private static EntityPlayer getEntity(Player player){
			return ((CraftPlayer) player).getHandle();
		}

		private static boolean isClient18(Player player){
			return getEntity(player).playerConnection.networkManager.getVersion() > 5;
		}

		private static void sendPacket(Player player, Packet packet){
			getEntity(player).playerConnection.sendPacket(packet);
		}

	}

	private static class Reflex{

		@SuppressWarnings("unchecked")
		public static <T> T getValue(Object object, String field, Class<T> clazz){
			return (T) getValue(object, field);
		}

		private static Object getValue(Object object, String field){
			try{
				Field f = object.getClass().getDeclaredField(field);
				if(!f.isAccessible()) f.setAccessible(true);
				return f.get(object);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}

	}

}