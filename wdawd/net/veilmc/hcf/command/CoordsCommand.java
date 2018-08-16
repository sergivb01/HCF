package net.veilmc.hcf.command;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.faction.type.Faction;
import net.veilmc.hcf.kothgame.faction.ConquestFaction;
import net.veilmc.hcf.kothgame.faction.EventFaction;
import net.veilmc.hcf.kothgame.faction.KothFaction;
import net.veilmc.util.BukkitUtils;
import net.veilmc.util.chat.ClickAction;
import net.veilmc.util.chat.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class CoordsCommand
		implements CommandExecutor{
	private final HCF plugin;

	public CoordsCommand(HCF plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		List<Faction> events = this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).collect(Collectors.toList());
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " &6&lEvent Coordinates &7(Click)"));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		int i = 1;
		for(Faction factionEvent : events){


			if(factionEvent instanceof KothFaction){
				new Text(ChatColor.translateAlternateColorCodes('&', "&f&l * " + i + ". &e&l" + factionEvent.getName() + " &8(&9&lKOTH&8)")).setHoverText(ChatColor.GREEN + "Click to view faction information for " + factionEvent.getName()).setClick(ClickAction.RUN_COMMAND, "/f show " + factionEvent.getName()).send(sender);
			}else if(factionEvent instanceof ConquestFaction){
				sender.sendMessage(" ");
				new Text(ChatColor.translateAlternateColorCodes('&', "&f&l * " + i + ". &e&l" + factionEvent.getName() + " &8(&5&lCONQUEST&8)")).setHoverText(ChatColor.GREEN + "Click to view faction information for " + factionEvent.getName()).setClick(ClickAction.RUN_COMMAND, "/f show " + factionEvent.getName()).send(sender);
			}else{
				new Text(ChatColor.translateAlternateColorCodes('&', "&f&l * " + i + ". &e&l" + factionEvent.getName() + " &8(&b&lEvent&8)")).setHoverText(ChatColor.GREEN + "Click to view faction information for " + factionEvent.getName()).setClick(ClickAction.RUN_COMMAND, "/f show " + factionEvent.getName()).send(sender);

			}
			i++;

		}
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + BukkitUtils.STRAIGHT_LINE_DEFAULT));


		return true;
	}
}