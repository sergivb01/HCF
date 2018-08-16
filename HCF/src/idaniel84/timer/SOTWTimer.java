package idaniel84.timer;

import java.util.concurrent.TimeUnit;

import idaniel84.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import idaniel84.HCF;

public class SOTWTimer
		extends GlobalTimer{
	public SOTWTimer(){
		super(ConfigurationService.SOTW_TIMER, TimeUnit.MINUTES.toMillis(30L));
	}

	public void run(){
		if(getRemaining() % 30L == 0L){
			Bukkit.broadcastMessage(ChatColor.GRAY + "SOTW will start in " + ChatColor.RED + HCF.getRemaining(getRemaining(), true));
		}
	}

	public ChatColor getScoreboardPrefix(){
		return ConfigurationService.SOTW_COLOUR;
	}
}
