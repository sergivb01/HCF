package net.veilmc.hcf.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.veilmc.util.BukkitUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PvpClass{
	public static final long DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8);
	protected final Set<PotionEffect> passiveEffects = new HashSet<PotionEffect>();
	protected final String name;
	protected final long warmupDelay;

	public PvpClass(String name, long warmupDelay){
		this.name = name;
		this.warmupDelay = warmupDelay;
	}

	public String getName(){
		return this.name;
	}

	public long getWarmupDelay(){
		return this.warmupDelay;
	}

	public boolean onEquip(Player p){
		for(PotionEffect effect : this.passiveEffects){
			p.addPotionEffect(effect, true);
		}
		p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + this.name + ChatColor.YELLOW + " Class has been equipped.");
		p.sendMessage(ChatColor.GRAY + " * " + ChatColor.GOLD + "" + ChatColor.BOLD + "veilhcf.us/class" + ChatColor.GRAY + " *");
		p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		if (this.name.contains("Miner")) {
            if (p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) < 250) {
                this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
                p.sendMessage(ChatColor.GOLD + "Miner level: 0 | Type /miner for more info.");
                p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
            }
		    if (p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) >= 250 && p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) < 500) {
				this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
		    	this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
		    	p.sendMessage(ChatColor.GOLD + "Miner level: 1 | Type /miner for more info.");
				p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			}
		    if (p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) >= 500 && p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) < 750) {
				this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				p.sendMessage(ChatColor.GOLD + "Miner level: 2 | Type /miner for more info.");
				p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			}
			if (p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) >= 750 && p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) < 1000) {
				this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
				p.sendMessage(ChatColor.GOLD + "Miner level: 3 | Type /miner for more info.");
				p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			}
			if (p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) >= 1000 && p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) < 2000) {
				this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
		    	this.passiveEffects.add(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0));
				p.sendMessage(ChatColor.GOLD + "Miner level: 4 | Type /miner for more info.");
				p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			}
			if (p.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) >= 2000) {
				this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0));
				this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
				p.sendMessage(ChatColor.GOLD + "Miner level: 5 | Type /miner for more info.");
				p.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
			}
		}


		//player.sendMessage(ConfigurationService.CLASS_EQUIPPED.replace("%class%", this.name));
		return true;
	}

	public void onUnequip(Player player){
		block0:
		for(PotionEffect effect : this.passiveEffects){
			for(PotionEffect active : player.getActivePotionEffects()){
				if((long) active.getDuration() <= DEFAULT_MAX_DURATION || !active.getType().equals(effect.getType()) || active.getAmplifier() != effect.getAmplifier())
					continue;
				player.removePotionEffect(effect.getType());
				continue block0;
			}
		}
		player.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + this.name + ChatColor.YELLOW + " Class has been unequipped.");
		player.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		//player.sendMessage(ConfigurationService.CLASS_UNEQUIPPED.replace("%class%", this.name));
	}

	public abstract boolean isApplicableFor(Player var1);
}

