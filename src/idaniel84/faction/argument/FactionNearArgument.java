package idaniel84.faction.argument;

import idaniel84.HCF;
import idaniel84.faction.type.PlayerFaction;
import net.veilmc.util.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FactionNearArgument extends CommandArgument {
    private final HCF plugin;
    private static int i;

    public FactionNearArgument(HCF plugin){
        super("near", "View 5 nearest factions with online players.");
        this.plugin = plugin;
    }

    public String getUsage(String label){
        return "" + '/' + label + ' ' + this.getName();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        Player player = (Player) sender;
        ArrayList<String> factions = new ArrayList<>();
        i = 0;
        player.sendMessage("Nearest factions.");
        player.getNearbyEntities(2000, 2000, 2000).forEach(Entity -> {
            if (factions.size() < 5 && Entity instanceof Player) {
                PlayerFaction namedFaction = this.plugin.getFactionManager().getPlayerFaction((Entity).getUniqueId());
                PlayerFaction senderFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
                if(namedFaction != null && !factions.toString().contains(namedFaction.getName()) && namedFaction.getHome() != null && ((senderFaction != null && !namedFaction.getName().equals(senderFaction.getName()))) || senderFaction == null){
                    i++;
                    player.sendMessage(" " + i + ". " + namedFaction.getName() + " (" + namedFaction.getOnlinePlayers().size() + "/" + namedFaction.getMembers().size() + "). Faction home at " + (int)namedFaction.getHome().distance(player.getLocation()) + " blocks to you. DTR: " + namedFaction.getDtrColour() + namedFaction.getDeathsUntilRaidable() + ChatColor.WHITE + "/" + namedFaction.getMaximumDeathsUntilRaidable() + namedFaction.getRegenStatus().getSymbol());
                }
            }
        });
        if (factions.size() == 0 && i == 0) {
            player.sendMessage(ChatColor.RED + "There are not near factions.");
            return true;
        }
        factions.clear();
        return true;
    }
}
