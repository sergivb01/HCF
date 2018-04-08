package net.veilmc.hcf.command.crate;

import net.veilmc.hcf.HCF;
import net.veilmc.hcf.command.crate.type.*;
import net.veilmc.util.Config;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.veilmc.hcf.command.crate.type.*;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

public class KeyManager{
	private final KothKey kothKey;
	private final ConquestKey conquestKey;
	private final Tier1Key tier1Key;
	private final Tier2Key tier2Key;
	private final Tier3Key tier3Key;
	private final PalaceKey palaceKey;
	private final Table<UUID, String, Integer> depositedCrateMap;
	private final Set<Key> keys;
	private final Config config;

	public KeyManager(final HCF plugin){
		super();
		this.depositedCrateMap = HashBasedTable.create();
		this.config = new Config(plugin, "key-data");
		this.keys = Sets.newHashSet(new Key[]{this.tier3Key = new Tier3Key(), this.tier2Key = new Tier2Key(), this.kothKey = new KothKey(), this.tier1Key = new Tier1Key(), this.palaceKey = new PalaceKey(), this.conquestKey = new ConquestKey()});
		this.reloadKeyData();
	}


	public Map<String, Integer> getDepositedCrateMap(UUID uuid){
		return this.depositedCrateMap.row(uuid);
	}

	public Set<Key> getKeys(){
		return this.keys;
	}

	public PalaceKey getPalaceKey(){
		return this.palaceKey;
	}

	public Tier1Key getTier1Key(){
		return this.tier1Key;
	}

	public Tier2Key getTier2Key(){
		return this.tier2Key;
	}

	public Tier3Key getTier3Key(){
		return this.tier3Key;
	}

	public KothKey getEventKey(){
		return this.kothKey;
	}

	public ConquestKey getConquestKey(){
		return this.conquestKey;
	}

	public Key getKey(String name){
		for(Key key : this.keys){
			if(!key.getName().equalsIgnoreCase(name)) continue;
			return key;
		}
		return null;
	}

	@Deprecated
	public Key getKey(Class<? extends Key> clazz){
		for(Key key : this.keys){
			if(!clazz.isAssignableFrom(key.getClass())) continue;
			return key;
		}
		return null;
	}

	public Key getKey(ItemStack stack){
		if(stack == null || !stack.hasItemMeta()){
			return null;
		}
		for(Key key : this.keys){
			ItemStack item = key.getItemStack();
			if(!item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) continue;
			return key;
		}
		return null;
	}

	public void reloadKeyData(){
		for(Key key : this.keys){
			key.load(this.config);
		}
		Object object = this.config.get("deposited-key-map");
		if(object instanceof MemorySection){
			MemorySection section = (MemorySection) object;
			for(String id : section.getKeys(false)){
				object = this.config.get(section.getCurrentPath() + '.' + id);
				if(!(object instanceof MemorySection)) continue;
				section = (MemorySection) object;
				for(String key2 : section.getKeys(false)){
					this.depositedCrateMap.put(UUID.fromString(id), key2, this.config.getInt("deposited-key-map." + id + '.' + key2));
				}
			}
		}
	}

	public void saveKeyData(){
		for(Key key : this.keys){
			key.save(this.config);
		}
		LinkedHashMap saveMap = new LinkedHashMap(this.depositedCrateMap.size());
		for(Map.Entry entry : this.depositedCrateMap.rowMap().entrySet()){
			saveMap.put(((UUID) entry.getKey()).toString(), entry.getValue());
		}
		this.config.set("deposited-key-map", saveMap);
		this.config.save();
	}
}

