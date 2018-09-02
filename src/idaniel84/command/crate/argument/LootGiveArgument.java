package idaniel84.command.crate.argument;

import com.google.common.primitives.Ints;
import idaniel84.HCF;
import idaniel84.command.crate.Key;
import idaniel84.command.crate.KeyListener;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LootGiveArgument
		extends CommandArgument{
	private final HCF plugin;

	public LootGiveArgument(HCF plugin){
		super("give", "Gives a crate key to a player");
		this.plugin = plugin;
		this.aliases = new String[]{"send"};
		this.permission = "hcf.command.loot.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <playerName> <type> <amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		Integer quantity;
		if(args.length < 3){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player target = Bukkit.getPlayer(args[1]);
		if(target == null || sender instanceof Player && !((Player) sender).canSee(target)){
			sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
			return true;
		}
		Key key = KeyListener.getKey(args[2]);
		if(key == null){
			sender.sendMessage(ChatColor.RED + "There is no key type named '" + args[2] + "'.");
			return true;
		}
		if(args.length >= 4){
			quantity = Ints.tryParse(args[3]);
			if(quantity == null){
				sender.sendMessage(ChatColor.RED + "'" + args[3] + "' is not a number.");
				return true;
			}
		}else{
			quantity = 1;
		}
		if(quantity <= 0){
			sender.sendMessage(ChatColor.RED + "You can only give keys in positive quantities.");
			return true;
		}
		ItemStack stack = key.getItemStack().clone();
		int maxAmount = 16;
		if(quantity > 16){
			sender.sendMessage(ChatColor.RED + "You cannot give keys in quantities more than " + 16 + '.');
			return true;
		}
		stack.setAmount(quantity.intValue());
		PlayerInventory inventory = target.getInventory();
		Location location = target.getLocation();
		World world = target.getWorld();
		final Map<Integer, ItemStack> excess = inventory.addItem(stack);
		for(final ItemStack entry : excess.values()){
			world.dropItemNaturally(location, entry);
		}
		sender.sendMessage(ChatColor.GREEN + "Given " + quantity + "x " + key.getDisplayName() + ChatColor.GREEN + " key to " + target.getName() + '.');
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 2){
			return null;
		}
		if(args.length == 3){
			return KeyListener.getKeys().stream().map(Key::getName).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}

