package idaniel84.timer;

import org.bukkit.ChatColor;
import net.veilmc.util.Config;

public abstract class Timer{
	protected final String name;
	protected final long defaultCooldown;

	public Timer(String name, long defaultCooldown){
		if(name == null){
			throw new IllegalStateException("Can not create timer with a null name");
		}
		this.name = name;
		this.defaultCooldown = defaultCooldown;
	}

	public abstract ChatColor getScoreboardPrefix();

	public String getName(){
		return this.name;
	}

	public final String getDisplayName(){
		return this.getScoreboardPrefix() + this.name;
	}

	public void load(Config config){
	}

	public void onDisable(Config config){
	}
}

