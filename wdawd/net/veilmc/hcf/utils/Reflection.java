package net.veilmc.hcf.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

public class Reflection{
	private final String name = Bukkit.getServer().getClass().getPackage().getName();
	private final String version = name.substring(name.lastIndexOf('.') + 1);
	private final DecimalFormat format = new DecimalFormat("##.##");

	private Object serverInstance;
	private Field tpsField;


	private Class<?> getNMSClass(String className){
		try{
			return Class.forName("net.minecraft.server." + version + "." + className);
		}catch(ClassNotFoundException e){
			throw new RuntimeException(e);
		}
	}

	public void getTpsRun(){
		try{
			serverInstance = getNMSClass("MinecraftServer").getMethod("getServer").invoke(null);
			tpsField = serverInstance.getClass().getField("recentTps");
		}catch(NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e){
			e.printStackTrace();
		}
	}

	public String getTPS(int time){
		try{
			double[] tps = ((double[]) tpsField.get(serverInstance));
			return format.format(tps[time]);
		}catch(IllegalAccessException e){
			throw new RuntimeException(e);
		}
	}

	public Long getLag(){
		return Math.round((1.0D - Double.parseDouble(getTPS(0).replace(",", ".")) / 20.0D) * 100.0D);
	}
}