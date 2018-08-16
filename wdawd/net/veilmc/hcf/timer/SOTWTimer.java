package net.veilmc.hcf.timer;

import java.util.concurrent.TimeUnit;

import net.veilmc.hcf.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.veilmc.hcf.HCF;

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
