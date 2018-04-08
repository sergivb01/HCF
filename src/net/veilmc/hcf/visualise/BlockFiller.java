package net.veilmc.hcf.visualise;

import com.google.common.collect.Iterables;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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

