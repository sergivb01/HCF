package idaniel84.listener;

import idaniel84.HCF;
import net.veilmc.base.BasePlugin;
import net.veilmc.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class FishingRodHitchListener implements Listener {

    private static HashMap<Player, Map<Player, String>> playerlist = new HashMap<>();


    @EventHandler
    private void onFishthingRodCatch(PlayerFishEvent event) {
        if(event.getCaught() instanceof Player) {
            Player thrower = event.getPlayer();
            Player caught = (Player)event.getCaught();
            if (!HCF.getPlugin().getFactionManager().getPlayerFaction(thrower).getMembers().containsKey(caught.getUniqueId()) && !HCF.getPlugin().getFactionManager().getPlayerFaction(thrower).getLeader().getUniqueId().equals(caught.getUniqueId())) {
                return;
            }
            if (playerlist.containsKey(thrower)) {
                stopTask(thrower, caught);
                thrower.sendMessage(ChatColor.YELLOW + "You have caught " + caught.getName() + ".");
                caught.sendMessage(ChatColor.YELLOW + "You have been caught by " + thrower.getName() + ".");
            }
        }
    }

    @EventHandler
    private void onFishingRodHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player;
            if ((player = BukkitUtils.getFinalAttacker(event, true)) != null) {
                if (player.getItemInHand().getType().equals(Material.FISHING_ROD) && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    Player damaged = (Player) event.getEntity();
                    if (damaged.getLocation().distance(player.getLocation()) > 3) {
                        player.sendMessage(ChatColor.RED + "You can only catch faction members at 3 or less blocks.");
                        if (playerlist.containsKey(player)) playerlist.remove(player);
                        return;
                    }
                    if (BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished()) {
                        player.sendMessage(ChatColor.RED + "Don't do this vanished!");
                        return;
                    }
                    int task = HCF.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(HCF.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            player.sendMessage(ChatColor.RED + damaged.getName() + " has been automatically teleported to you.");
                            damaged.sendMessage(ChatColor.RED + "You have been automatically teleported to " + player.getName() + ".");
                            stopTask(player, damaged);
                        }
                    }, 200L, 200L);
                    Map<Player, String> hit = new HashMap<>();
                    hit.clear();
                    hit.put(damaged, player.getInventory().getHeldItemSlot() + ", " + task + ", " + System.currentTimeMillis());
                    if (!playerlist.containsKey(player)) playerlist.put(player, hit);
                    player.sendMessage(ChatColor.YELLOW + "Catched " + damaged.getName() + ". You have 10 seconds to get him back or he will be automatically teleported.");
                    damaged.sendMessage(ChatColor.YELLOW + "You have been catched by " + player.getName() + ".");
                }
            }
        }
    }

    @EventHandler
    private void onItemChange(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        if (playerlist.containsKey(p)) {
            Boolean bool = false;
            for (String string : playerlist.get(p).values()) {
                String[] split = string.split(", ");
                if (Integer.parseInt(split[0]) == event.getPreviousSlot()) bool = true;
            }
            if (bool) {
                Player friend = p;
                for (Player pl : playerlist.get(p).keySet()) {
                    friend = pl;
                }
                stopTask(p, friend);
                p.sendMessage(ChatColor.RED + "Don't change your item in hand!");
            }
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (playerlist.containsKey(event.getPlayer())) {
            Player player = event.getPlayer();
            Player friend = event.getPlayer();
            for (Player player1 : playerlist.get(player).keySet()) {
                friend = player1;
            }
            if (player.getLocation().distance(friend.getLocation()) > 30) {
                stopTask(player, friend);
                friend.sendMessage(ChatColor.RED + "The rod have been broken and you have been teleported to " + player.getName() + ".");
                player.sendMessage(ChatColor.RED + "The rod have been broken and " + friend.getName() + " has been teleported to you.");
            }
        }
        if (playerlist.values().toString().contains(event.getPlayer().toString())) {
            Player friend = event.getPlayer();
            Player player = event.getPlayer();
            for (Player player1 : playerlist.keySet()) {
                if (playerlist.get(player1).containsKey(friend)) {
                    player = player1;
                }
            }
            if (friend.getLocation().distance(player.getLocation()) > 30) {
                stopTask(player, friend);
                friend.sendMessage(ChatColor.RED + "The rod have been broken and you have been teleported to " + player.getName() + ".");
                player.sendMessage(ChatColor.RED + "The rod have been broken and " + friend.getName() + " has been teleported to you.");
            }
        }
    }

    @EventHandler
    private void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (playerlist.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(Integer.parseInt(playerlist.get(player).get(getFriend(player)).split(", ")[1]));
            playerlist.remove(player);
        }
        else if (playerlist.values().toString().contains(player.toString())) {
            Player player1 = player;
            for (Player player2 : playerlist.keySet()) {
                if (playerlist.get(player2).containsKey(player)) {
                    player1 = player2;
                }
            }
            if (player1 != player) {
                Bukkit.getScheduler().cancelTask(Integer.parseInt(playerlist.get(player1).get(getFriend(player1)).split(", ")[1]));
                playerlist.remove(player1);
            }
        }
    }

    private void stopTask(Player player, Player friend) {
        friend.teleport(player);
        Bukkit.getScheduler().cancelTask(Integer.parseInt(playerlist.get(player).get(friend).split(", ")[1]));
        playerlist.remove(player);
    }

    public static int getTaskId(Player player) { return Integer.parseInt(playerlist.get(player).get(getFriend(player)).split(", ")[1]); }

    public static long getTaskRemainingMilliseconds(Player player) {
        long i = 0;
        if (playerlist.containsKey(player)) i = Long.parseLong(playerlist.get(player).get(getFriend(player)).split(", ")[2]) - System.currentTimeMillis() + 10000;
        else if (playerlist.values().toString().contains(player.toString())) {
            Player player1 = player;
            for (Player player2 : playerlist.keySet()) {
                if (playerlist.get(player2).containsKey(player)) {
                    player1 = player2;
                }
            }
            if (player1 != player) i = Long.parseLong(playerlist.get(player1).get(getFriend(player1)).split(", ")[2]) - System.currentTimeMillis() + 10000;
        }
        return i;
    }

    private static Player getFriend(Player player) {
        Player friend = player;
        for (Player player1 : playerlist.get(player).keySet()) {
            friend = player1;
        }
        return friend;
    }
}
