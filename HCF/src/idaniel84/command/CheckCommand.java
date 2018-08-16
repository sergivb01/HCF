package idaniel84.command;

import litebans.api.Database;
import idaniel84.HCF;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckCommand implements Listener, CommandExecutor{
	private static HCF plugin;

	public CheckCommand(HCF plugin){
		this.plugin = plugin;
	}

	private static Inventory getBanMenu(final OfflinePlayer target){
		final Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + target.getName() + (target.getName().endsWith("s") ? "'" : "'s") + " Bans");

		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try(PreparedStatement st = Database.get().prepareStatement("SELECT * FROM {bans} WHERE `uuid` = '" + target.getUniqueId().toString() + "'")){
				try(ResultSet rs = st.executeQuery()){
					List<String> punisherList = new ArrayList<>();
					List<String> originList = new ArrayList<>();
					List<String> reasonList = new ArrayList<>();
					List<String> idList = new ArrayList<>();
					List<String> removedNameList = new ArrayList<>();
					List<String> dateList = new ArrayList<>();
					List<String> activeList = new ArrayList<>();
					while(rs.next()){
						punisherList.add(rs.getString("banned_by_name"));
						originList.add(rs.getString("server_origin"));
						reasonList.add(rs.getString("reason"));
						idList.add(rs.getString("id"));
						removedNameList.add(rs.getString("removed_by_name"));
						dateList.add(rs.getString("removed_by_date"));
						activeList.add(rs.getString("active"));
					}

					if(reasonList.size() == 0){
						return;
					}
					for(int i = 0; i < reasonList.size(); i++){
						ItemStack is = new ItemStack(35, 1, (short) 14);
						ItemMeta meta = is.getItemMeta();
						if(activeList.get(i).equals("1")){
							// TODO: CLEANUP
							meta.setLore(Arrays.asList(
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------",
									ChatColor.translateAlternateColorCodes('&', "&eBanned by: &c" + punisherList.get(i)),
									ChatColor.translateAlternateColorCodes('&', "&eReason: &c" + reasonList.get(i)),
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------",
									ChatColor.translateAlternateColorCodes('&', "&eServer: &c" + originList.get(i)),
									ChatColor.translateAlternateColorCodes('&', "&eDate: &c" + dateList.get(i)),
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------"
							));
						}else{
							is.setType(Material.PACKED_ICE);
							meta.setLore(Arrays.asList(
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------",
									ChatColor.translateAlternateColorCodes('&', "&eBanned by: &c" + punisherList.get(i)),
									ChatColor.translateAlternateColorCodes('&', "&eReason: &c" + reasonList.get(i)),
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------",
									ChatColor.translateAlternateColorCodes('&', "&eServer: &c" + originList.get(i)),
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------",
									ChatColor.translateAlternateColorCodes('&', "&eUnbanned by: &c" + removedNameList.get(i)),
									ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------"
							));
						}
						meta.setDisplayName(ChatColor.GRAY + "ID #" + idList.get(i));
						is.setItemMeta(meta);
						inventory.setItem(i, is);
					}
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		});
		return inventory;
	}

	private static Inventory getMenu(final Player player){
		final Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.YELLOW + player.getName() + (player.getName().endsWith("s") ? "'" : "'s") + " Punishments");

		ItemStack ban = new ItemStack(35, 1, (short) 14);
		ItemMeta banMeta = ban.getItemMeta();
		banMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to view", ChatColor.GRAY + "the ban punishments", ChatColor.GRAY + "of this player."));
		banMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lBans"));
		ban.setItemMeta(banMeta);
		inventory.setItem(2, ban);

		ItemStack mute = new ItemStack(35, 1, (short) 1);
		ItemMeta muteMeta = mute.getItemMeta();
		muteMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to view", ChatColor.GRAY + "the mute punishments", ChatColor.GRAY + "of this player."));
		muteMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lMutes"));
		mute.setItemMeta(muteMeta);
		inventory.setItem(4, mute);

		ItemStack kick = new ItemStack(35, 1, (short) 3);
		ItemMeta kickMeta = kick.getItemMeta();
		kickMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to view", ChatColor.GRAY + "the kick punishments", ChatColor.GRAY + "of this player."));
		kickMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lKicks"));
		kick.setItemMeta(kickMeta);
		inventory.setItem(6, kick);

		return inventory;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return false;
		}
		if(args.length < 1){
			sender.sendMessage(ChatColor.RED + "Usage: /" + command.getName() + " <player>");
			return false;
		}
		OfflinePlayer target = Bukkit.getPlayer(args[0]);

		((Player) sender).openInventory(getBanMenu(target));
		return true;
	}


}
