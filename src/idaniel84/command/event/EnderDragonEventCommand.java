package idaniel84.command.event;

import idaniel84.HCF;
import net.veilmc.util.imagemessage.ImageChar;
import net.veilmc.util.imagemessage.ImageMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class EnderDragonEventCommand
		implements CommandExecutor{
	private final HCF plugin;

	public EnderDragonEventCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
			return true;
		}
		if(((Player) sender).getLocation().getWorld().getEnvironment() != World.Environment.THE_END){
			sender.sendMessage(ChatColor.RED + "You must be in the end.");
			return true;
		}
		for (Entity en : ((Player) sender).getNearbyEntities(2000, 2000, 2000)) {
			if (en instanceof EnderDragon) {
				sender.sendMessage(ChatColor.RED + "There can only be one ender dragon at the same time.");
				return true;
			}
		}
		((Player) sender).getWorld().spawnCreature(((Player) sender).getLocation().add(0,10,0), EntityType.ENDER_DRAGON);
		for(Player on : Bukkit.getServer().getOnlinePlayers()){
			for(int i = 0; i < 5; ++i){
				on.sendMessage("");
			}
			try{
				BufferedImage imageToSend = ImageIO.read(this.plugin.getResource("enderdragon-art.png"));
				new ImageMessage(imageToSend, 15, ImageChar.BLOCK.getChar()).appendText("", "", "", "", "", "", ChatColor.RED + "[EnderDragon Event]", ChatColor.YELLOW + "Dragon Spawned", ChatColor.YELLOW + "Kill it to get a big prize!").sendToPlayer(on);
			}catch(Exception e){
				e.printStackTrace();
			}
			on.playSound(on.getLocation(), Sound.ENDERDRAGON_GROWL, 3.0f, 5.0f);
		}
		return true;
	}
}

