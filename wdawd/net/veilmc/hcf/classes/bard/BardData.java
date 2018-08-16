package net.veilmc.hcf.classes.bard;

import com.google.common.base.Preconditions;
import org.bukkit.scheduler.BukkitTask;

public class BardData{
	public static final double MIN_ENERGY = 0.0;
	public static final double MAX_ENERGY = 100.0;
	public static final long MAX_ENERGY_MILLIS = 100000;
	private static final double ENERGY_PER_MILLISECOND = 1.25;
	public long buffCooldown;
	public BukkitTask heldTask;
	private long energyStart;

	public long getRemainingBuffDelay(){
		return this.buffCooldown - System.currentTimeMillis();
	}

	public void startEnergyTracking(){
		this.setEnergy(0.0);
	}

	public long getEnergyMillis(){
		if(this.energyStart == 0){
			return 0;
		}
		return Math.min(120000, (long) (1.25 * (double) (System.currentTimeMillis() - this.energyStart)));
	}

	public double getEnergy(){
		double value = (double) this.getEnergyMillis() / 1000.0;
		return (double) Math.round(value * 10.0) / 10.0;
	}

	public void setEnergy(double energy){
		Preconditions.checkArgument(energy >= 0.0, "Energy cannot be less than 0.0");
		Preconditions.checkArgument(energy <= 120.0, "Energy cannot be more than 120.0");
		this.energyStart = (long) ((double) System.currentTimeMillis() - 1000.0 * energy);
	}
}

