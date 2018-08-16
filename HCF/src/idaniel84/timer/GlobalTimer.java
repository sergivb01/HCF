package idaniel84.timer;

import idaniel84.timer.event.TimerExtendEvent;
import idaniel84.timer.event.TimerPauseEvent;
import idaniel84.timer.event.TimerStartEvent;
import org.bukkit.Bukkit;

public abstract class GlobalTimer
		extends Timer{
	private TimerRunnable runnable;

	public GlobalTimer(String name, long defaultCooldown){
		super(name, defaultCooldown);
	}

	public boolean clearCooldown(){
		if(this.runnable != null){
			this.runnable.cancel();
			this.runnable = null;
			return true;
		}
		return false;
	}

	public boolean isPaused(){
		return (this.runnable != null) && (this.runnable.isPaused());
	}

	public void setPaused(boolean paused){
		if((this.runnable != null) && (this.runnable.isPaused() != paused)){
			TimerPauseEvent event = new TimerPauseEvent(this, paused);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				this.runnable.setPaused(paused);
			}
		}
	}

	public long getRemaining(){
		return this.runnable == null ? 0L : this.runnable.getRemaining();
	}

	public long getRemaining1(){
		return this.runnable == null ? 0L : this.runnable.getRemaining();
	}

	public boolean setRemaining(){
		return setRemaining(this.defaultCooldown, false);
	}

	public boolean setRemaining(long duration, boolean overwrite){
		boolean hadCooldown = false;
		if(this.runnable != null){
			if(!overwrite){
				return false;
			}
			TimerExtendEvent event = new TimerExtendEvent(this, this.runnable.getRemaining(), duration);
			Bukkit.getPluginManager().callEvent(event);
			if(event.isCancelled()){
				return false;
			}
			hadCooldown = this.runnable.getRemaining() > 0L;
			this.runnable.setRemaining(duration);
		}else{
			Bukkit.getPluginManager().callEvent(new TimerStartEvent(this, duration));
			this.runnable = new TimerRunnable(this, duration);
		}
		return !hadCooldown;
	}
}