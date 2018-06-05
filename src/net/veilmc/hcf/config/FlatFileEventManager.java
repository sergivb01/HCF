package net.veilmc.hcf.config;

import net.veilmc.util.Config;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class FlatFileEventManager implements EventManager {
    private final JavaPlugin plugin;
    @SuppressWarnings({"unchecked", "rawtypes"})
    private ArrayList<String> sumolocations = new ArrayList<>();
    private ArrayList<String> thimblelocations = new ArrayList<>();
    private Config eventsConfig;

    public FlatFileEventManager(JavaPlugin plugin){
        this.plugin = plugin;
        this.eventsConfig = new Config(this.plugin, "events");
        final List<String> sumoevent = this.eventsConfig.getStringList("sumoevent");
        final List<String> thimbleevent = this.eventsConfig.getStringList("thimbleevent");
        sumolocations.addAll(sumoevent);
        thimblelocations.addAll(thimbleevent);
    }

    @Override
    public Boolean isThimbleLocation(Location location) {
        String loc = "X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
        return thimblelocations.contains(loc);
    }

    @Override
    public String addThimbleEventLocation(Location location){
        String loc = "X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
        thimblelocations.add(loc);
        return loc;
    }

    @Override
    public String removeThimbleEventLocation(Location location){
        String loc = "X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
        thimblelocations.remove(loc);
        return loc;
    }

    @Override
    public List<String> getThimbleList() {
        return this.thimblelocations;
    }

    @Override
    public Boolean isSumoLocation(Location location) {
        String loc = "X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
        return sumolocations.contains(loc);
    }

    @Override
    public String addSumoEventLocation(Location location){
        String loc = "X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
        sumolocations.add(loc);
        return loc;
    }

    @Override
    public String removeSumoEventLocation(Location location){
        String loc = "X:" + location.getBlockX() + ", Y:" + location.getBlockY() + ", Z:" + location.getBlockZ();
        sumolocations.remove(loc);
        return loc;
    }

    @Override
    public List<String> getSumoList() {
        return this.sumolocations;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void saveEventsData(){
        this.eventsConfig.set("sumoevent", sumolocations);
        this.eventsConfig.set("thimbleevent", thimblelocations);
        this.eventsConfig.save();
    }
}

