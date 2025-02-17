package idaniel84.visualise;

import net.veilmc.util.cuboid.Cuboid;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class VisualiseHandler{
	private final Table<UUID, Location, VisualBlock> storedVisualises = HashBasedTable.create();

	public Table<UUID, Location, VisualBlock> getStoredVisualises(){
		return this.storedVisualises;
	}

	@Deprecated
	public VisualBlock getVisualBlockAt(Player player, int x, int y, int z) throws NullPointerException{
		return this.getVisualBlockAt(player, new Location(player.getWorld(), (double) x, (double) y, (double) z));
	}


	public VisualBlock getVisualBlockAt(Player player, Location location) throws NullPointerException{
		Preconditions.checkNotNull((Object) player, (Object) "Player cannot be null");
		Preconditions.checkNotNull((Object) location, (Object) "Location cannot be null");
		Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized(table){
			return (VisualBlock) this.storedVisualises.get((Object) player.getUniqueId(), (Object) location);
		}
	}


	public Map<Location, VisualBlock> getVisualBlocks(Player player){
		Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized(table){
			return new HashMap<Location, VisualBlock>(this.storedVisualises.row(player.getUniqueId()));
		}
	}

	public Map<Location, VisualBlock> getVisualBlocks(Player player, final VisualType visualType){
		return Maps.filterValues(this.getVisualBlocks(player), (Predicate) new Predicate<VisualBlock>(){

			public boolean apply(VisualBlock visualBlock){
				return visualType == visualBlock.getVisualType();
			}
		});
	}

	public LinkedHashMap<Location, VisualBlockData> generate(Player player, Cuboid cuboid, VisualType visualType, boolean canOverwrite){
		HashSet<Location> locations = new HashSet<Location>(cuboid.getSizeX() * cuboid.getSizeY() * cuboid.getSizeZ());
		for(Block block : cuboid){
			locations.add(block.getLocation());
		}
		return this.generate(player, locations, visualType, canOverwrite);
	}


	public LinkedHashMap<Location, VisualBlockData> generate(Player player, Iterable<Location> locations, VisualType visualType, boolean canOverwrite){
		Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized(table){
			LinkedHashMap<Location, VisualBlockData> results = new LinkedHashMap<Location, VisualBlockData>();
			ArrayList<VisualBlockData> filled = visualType.blockFiller().bulkGenerate(player, locations);
			if(filled != null){
				int count = 0;
				for(Location location : locations){
					Material previousType;
					if(!canOverwrite && this.storedVisualises.contains((Object) player.getUniqueId(), (Object) location) || (previousType = location.getBlock().getType()).isSolid() || previousType != Material.AIR)
						continue;
					VisualBlockData visualBlockData = filled.get(count++);
					results.put(location, visualBlockData);
					player.sendBlockChange(location, visualBlockData.getBlockType(), visualBlockData.getData());
					this.storedVisualises.put(player.getUniqueId(), location, new VisualBlock(visualType, visualBlockData, location));
				}
			}
			return results;
		}
	}

	public boolean clearVisualBlock(Player player, Location location){
		return this.clearVisualBlock(player, location, true);
	}


	public boolean clearVisualBlock(Player player, Location location, boolean sendRemovalPacket){
		Table<UUID, Location, VisualBlock> table = this.storedVisualises;
		synchronized(table){
			VisualBlock visualBlock = (VisualBlock) this.storedVisualises.remove((Object) player.getUniqueId(), (Object) location);
			if(sendRemovalPacket && visualBlock != null){
				Block block = location.getBlock();
				VisualBlockData visualBlockData = visualBlock.getBlockData();
				if(visualBlockData.getBlockType() != block.getType() || visualBlockData.getData() != block.getData()){
					player.sendBlockChange(location, block.getType(), block.getData());
				}
				return true;
			}
		}
		return false;
	}

	public Map<Location, VisualBlock> clearVisualBlocks(Player player){
		return this.clearVisualBlocks(player, null, null);
	}

	public Map<Location, VisualBlock> clearVisualBlocks(Player player, VisualType visualType, Predicate<VisualBlock> predicate){
		return this.clearVisualBlocks(player, visualType, predicate, true);
	}

	@Deprecated
	public Map<Location, VisualBlock> clearVisualBlocks(final Player player, final VisualType visualType, final Predicate<VisualBlock> predicate, final boolean sendRemovalPackets){
		synchronized(this.storedVisualises){
			if(!this.storedVisualises.containsRow((Object) player.getUniqueId())){
				return Collections.emptyMap();
			}
			final Map<Location, VisualBlock> results = new HashMap<Location, VisualBlock>(this.storedVisualises.row(player.getUniqueId()));
			final Map<Location, VisualBlock> removed = new HashMap<Location, VisualBlock>();
			for(final Map.Entry<Location, VisualBlock> entry : results.entrySet()){
				final VisualBlock visualBlock = entry.getValue();
				if((predicate == null || predicate.apply(visualBlock)) && (visualType == null || visualBlock.getVisualType() == visualType)){
					final Location location = entry.getKey();
					if(removed.put(location, visualBlock) != null){
						continue;
					}
					this.clearVisualBlock(player, location, sendRemovalPackets);
				}
			}
			return removed;
		}
	}
}

