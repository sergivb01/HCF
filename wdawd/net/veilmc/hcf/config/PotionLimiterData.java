package net.veilmc.hcf.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class PotionLimiterData{
	static PotionLimiterData instance = new PotionLimiterData();
	Plugin p;
	FileConfiguration Data;
	File Datafile;

	public static PotionLimiterData getInstance(){
		return instance;
	}

	public void setup(Plugin p){
		this.p = p;
		this.Datafile = new File(p.getDataFolder(), "potion-limiter.yml");
		this.Data = YamlConfiguration.loadConfiguration(this.Datafile);
		if(!this.Datafile.exists()){
			try{
				this.Datafile.createNewFile();
				PrintWriter writer = new PrintWriter(new FileWriter(this.Datafile));
				writer.println("#See http://minecraft.gamepedia.com/Potion\n#And http://minecraft.gamepedia.com/Splash_Potion\ndisabled-potions:\n  - 8195\n  - 8201\n  - 8265\n  - 16393\n  - 16457\n  - 16387\n  - 16451");
				writer.flush();
				writer.close();
			}catch(IOException localIOException){

			}
		}
	}

	public FileConfiguration getConfig(){
		return this.Data;
	}

	public void saveConfig(){
		try{
			this.Data.save(this.Datafile);
		}catch(IOException localIOException){
		}
	}

	public PluginDescriptionFile getDescription(){
		return this.p.getDescription();
	}

	public void reloadConfig(){
		this.Data = YamlConfiguration.loadConfiguration(this.Datafile);
	}
}