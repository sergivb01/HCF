package idaniel84.config;

import org.bukkit.Location;

import java.util.List;

public interface EventManager {

    Boolean isSumoLocation(Location location);

    String addSumoEventLocation(Location location);

    String removeSumoEventLocation(Location location);

    List<String> getSumoList();

    Boolean isThimbleLocation(Location location);

    String addThimbleEventLocation(Location location);

    String removeThimbleEventLocation(Location location);

    List<String> getThimbleList();

    void saveEventsData();
}
