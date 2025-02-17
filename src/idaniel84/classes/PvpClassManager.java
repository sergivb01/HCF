package idaniel84.classes;

import idaniel84.classes.archer.ArcherClass;
import idaniel84.classes.bard.BardClass;
import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.classes.event.PvpClassUnequipEvent;
import idaniel84.classes.type.MinerClass;
import idaniel84.classes.type.RogueClass;
import idaniel84.classes.archer.ArcherClass;
import idaniel84.classes.bard.BardClass;
import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.classes.event.PvpClassUnequipEvent;
import idaniel84.classes.type.MinerClass;
import idaniel84.classes.type.RogueClass;
import idaniel84.classes.archer.ArcherClass;
import idaniel84.classes.bard.BardClass;
import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.classes.event.PvpClassUnequipEvent;
import idaniel84.classes.type.MinerClass;
import idaniel84.classes.type.RogueClass;
import idaniel84.HCF;
import idaniel84.classes.archer.ArcherClass;
import idaniel84.classes.bard.BardClass;
import idaniel84.classes.event.PvpClassEquipEvent;
import idaniel84.classes.event.PvpClassUnequipEvent;
import idaniel84.classes.type.MinerClass;
import idaniel84.classes.type.RogueClass;
import idaniel84.utils.ConfigurationService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvpClassManager{
	private final Map<UUID, PvpClass> equippedClass = new HashMap<UUID, PvpClass>();
	private final Map<String, PvpClass> pvpClasses = new HashMap<String, PvpClass>();

	public PvpClassManager(HCF plugin){
		this.pvpClasses.put("Archer", new ArcherClass(plugin));
		this.pvpClasses.put("Bard", new BardClass(plugin));
		this.pvpClasses.put("Miner", new MinerClass(plugin));
		this.pvpClasses.put("Rouge", new RogueClass(plugin));
		for(PvpClass pvpClass : this.pvpClasses.values()){
			if(!(pvpClass instanceof Listener)) continue;
			plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass, plugin);
		}
	}

	public void onDisable(){
		for(Map.Entry<UUID, PvpClass> entry : new HashMap<UUID, PvpClass>(this.equippedClass).entrySet()){
			this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
		}
		this.pvpClasses.clear();
		this.equippedClass.clear();
	}

	public Collection<PvpClass> getPvpClasses(){
		return this.pvpClasses.values();
	}

	public PvpClass getPvpClass(String name){
		return this.pvpClasses.get(name);
	}


	public PvpClass getEquippedClass(Player player){
		Map<UUID, PvpClass> map = this.equippedClass;
		synchronized(map){
			return this.equippedClass.get(player.getUniqueId());
		}
	}

	public boolean hasClassEquipped(Player player, PvpClass pvpClass){
		PvpClass equipped = this.getEquippedClass(player);
		return equipped != null && equipped.equals(pvpClass);
	}

	public void setEquippedClass(Player player, @Nullable PvpClass pvpClass){
		PvpClass equipped = this.getEquippedClass(player);
		if(equipped != null){
			if(pvpClass == null){
				this.equippedClass.remove(player.getUniqueId());
				equipped.onUnequip(player);
				Bukkit.getPluginManager().callEvent(new PvpClassUnequipEvent(player, equipped));
				return;
			}
		}else if(pvpClass == null){
			return;
		}
		if(pvpClass.onEquip(player)){
			this.equippedClass.put(player.getUniqueId(), pvpClass);
			Bukkit.getPluginManager().callEvent(new PvpClassEquipEvent(player, pvpClass));
		}
	}
}

