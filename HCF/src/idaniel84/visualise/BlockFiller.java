package idaniel84.visualise;

import com.google.common.collect.Iterables;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

abstract class BlockFiller{
	BlockFiller(){
	}

	abstract VisualBlockData generate(Player var1, Location var2);

	ArrayList<VisualBlockData> bulkGenerate(Player player, Iterable<Location> locations){
		ArrayList<VisualBlockData> data = new ArrayList<VisualBlockData>(Iterables.size(locations));
		for(Location location : locations){
			data.add(this.generate(player, location));
		}
		return data;
	}
}

