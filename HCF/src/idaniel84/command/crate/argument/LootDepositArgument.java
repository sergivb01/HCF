package idaniel84.command.crate.argument;

import com.google.common.primitives.Ints;
import idaniel84.HCF;
import idaniel84.command.crate.Key;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class LootDepositArgument
		extends CommandArgument{
	private final HCF plugin;

	public LootDepositArgument(HCF plugin){
		super("deposit", "Deposits a crate key into your bank account");
		this.plugin = plugin;
		this.aliases = new String[]{"store"};
		this.permission = "hcf.command.loot.argument." + this.getName();
	}

	public String getUsage(String label){
		return "" + '/' + label + ' ' + this.getName() + " <amount>";
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage(ChatColor.RED + "Incorrect usage!" + ChatColor.YELLOW + " Use like this: " + ChatColor.AQUA + this.getUsage(label));
			return true;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		ItemStack stack = player.getItemInHand();
		Key key = this.plugin.getKeyManager().getKey(stack);
		if(key == null){
			sender.sendMessage(ChatColor.RED + "You are not holding a crate key.");
			return true;
		}
		Integer quantity = Ints.tryParse(args[1]);
		if(quantity == null){
			sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
			return true;
		}
		if(quantity <= 0){
			sender.sendMessage(ChatColor.RED + "You can only deposit crate keys in positive amounts.");
			return true;
		}
		String keyName = key.getName();
		if(quantity > stack.getAmount()){
			sender.sendMessage(ChatColor.RED + "You tried to deposit " + quantity + ' ' + keyName + " keys, but you are only holding " + stack.getAmount() + '.');
			return true;
		}
		Map<String, Integer> crateKeyMap = this.plugin.getKeyManager().getDepositedCrateMap(uuid);
		int newAmount = crateKeyMap.getOrDefault(keyName, 0) + quantity;
		crateKeyMap.put(keyName, newAmount);
		if(quantity.intValue() == stack.getAmount()){
			player.setItemInHand(new ItemStack(Material.AIR, 1));
		}else{
			stack.setAmount(stack.getAmount() - quantity);
		}
		sender.sendMessage(ChatColor.YELLOW + "Successfully deposited " + quantity + ' ' + keyName + " key ".toString() + (quantity > 1 ? "s" : "") + ". You now have " + ChatColor.LIGHT_PURPLE + newAmount + ChatColor.YELLOW + " of these keys.");
		return true;
	}
}

