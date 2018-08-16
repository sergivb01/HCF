package idaniel84.listener;

import idaniel84.faction.type.PlayerFaction;
import idaniel84.timer.PlayerTimer;
import idaniel84.HCF;
import idaniel84.faction.type.Faction;
import idaniel84.faction.type.PlayerFaction;
import idaniel84.timer.PlayerTimer;
import idaniel84.utils.ConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener{
	private final HCF plugin;
	private String prefix;
	private String signTitle;

	public ElevatorListener(HCF plugin){
		this.plugin = plugin;
		this.signTitle = ChatColor.BLUE + ChatColor.BOLD.toString() + "[Elevator]";
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onSignUpdate(final SignChangeEvent e){
		if(StringUtils.containsIgnoreCase(e.getLine(0), "Elevator")){
			boolean up;
			if(StringUtils.containsIgnoreCase(e.getLine(1), "Up")){
				up = true;
			}else{
				if(!StringUtils.containsIgnoreCase(e.getLine(1), "Down")){
					e.getPlayer().sendMessage(ChatColor.RED + "Incorrect usage: Up/Down");
					return;
				}
				up = false;
			}
			e.setLine(0, this.signTitle);
			e.setLine(1, up ? "Up" : "Down");
			e.setLine(2, "");
			e.setLine(3, "");
		}
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerInteract(final PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null){
			final Block block = e.getClickedBlock();
			if(block.getState() instanceof Sign){
				final Sign sign = (Sign) block.getState();
				final String[] lines = sign.getLines();
				if(lines[0].equals(this.signTitle)){
					boolean up;
					if(lines[1].equalsIgnoreCase("Up")){
						up = true;
					}else{
						if(!lines[1].equalsIgnoreCase("Down")){
							return;
						}
						up = false;
					}
					this.signClick(e.getPlayer(), sign.getLocation(), up);
				}
			}
		}
	}

	public boolean signClick(final Player player, final Location signLocation, final boolean up){
		Block block = signLocation.getBlock();
		do{
			block = block.getRelative(up ? BlockFace.UP : BlockFace.DOWN);
			if(block.getY() > block.getWorld().getMaxHeight() || block.getY() <= 1){
				player.sendMessage(ChatColor.RED + "Could not find a sign " + (up ? "above" : "below") + " to teleport you to.");
				return false;
			}
		}while(!this.isSign(block));
		final boolean underSafe = this.isSafe(block.getRelative(BlockFace.DOWN));
		final boolean overSafe = this.isSafe(block.getRelative(BlockFace.UP));
		if(player.getGameMode().equals(GameMode.CREATIVE)){
			final Location location = player.getLocation().clone();
			location.setX(block.getX() + 0.5);
			location.setY((double) (block.getY() + (underSafe ? -1 : 0)));
			location.setZ(block.getZ() + 0.5);
			location.setPitch(0.0f);
			player.teleport(location);
			return true;
		}

		Faction at = this.plugin.getFactionManager().getFactionAt(block);
		if(at instanceof PlayerFaction){
			if(((PlayerFaction) at).getMember(player) == null && !ConfigurationService.KIT_MAP){
				player.sendMessage(ChatColor.RED + "You cannot use this in the territory of " + ChatColor.stripColor(at.getDisplayName(player)));
				return false;
			}
		}
		PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
		long remaining = timer.getRemaining(player);
		if(((timer = this.plugin.getTimerManager().spawnTagTimer).getRemaining(player) > 0L) && (!ConfigurationService.KIT_MAP && !ConfigurationService.VEILZ && !ConfigurationService.FFA)){
			player.sendMessage(ChatColor.RED + "You can not use this while your " + ChatColor.BOLD + "Spawn Tag" + ChatColor.RED + " is active.");
			return false;

		}
		if(!underSafe && !overSafe){
			player.sendMessage(ChatColor.RED + "There is a block blocking the sign " + (up ? "above" : "below") + "!");
			return false;
		}


		final Location location = player.getLocation().clone();
		location.setX(block.getX() + 0.5);
		location.setY((double) (block.getY() + (underSafe ? -1 : 0)));
		location.setZ(block.getZ() + 0.5);
		location.setPitch(0.0f);
		player.teleport(location);
		return true;
	}

	private boolean isSign(final Block block){
		if(block.getState() instanceof Sign){
			final Sign sign = (Sign) block.getState();
			final String[] lines = sign.getLines();
			return lines[0].equals(this.signTitle)
					&& (lines[1].equalsIgnoreCase("Up") || lines[1].equalsIgnoreCase("Down"));
		}
		return false;
	}

	private boolean isSafe(final Block block){
		return block != null && !block.getType().isSolid() && block.getType() != Material.GLASS && block.getType() != Material.STAINED_GLASS;
	}
}