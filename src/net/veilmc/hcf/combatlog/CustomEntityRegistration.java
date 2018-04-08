package net.veilmc.hcf.combatlog;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.server.v1_7_R4.EntityTypes;

public class CustomEntityRegistration{
	public static void registerCustomEntities(){
		try{
			CustomEntityRegistration.registerCustomEntity(LoggerEntity.class, "CraftVillager", 120);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void registerCustomEntity(Class entityClass, String name, int id){
		CustomEntityRegistration.setFieldPrivateStaticMap("d", entityClass, name);
		CustomEntityRegistration.setFieldPrivateStaticMap("f", entityClass, id);
	}

	public static void unregisterCustomEntities(){
	}

	public static void setFieldPrivateStaticMap(String fieldName, Object key, Object value){
		try{
			Field field = EntityTypes.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			Map map = (Map) field.get(null);
			map.put(key, value);
			field.set(null, map);
		}catch(IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex){
			ex.printStackTrace();
		}
	}

	public static void setField(String fieldName, Object key, Object value){
		try{
			Field field = key.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(key, value);
			field.setAccessible(false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
