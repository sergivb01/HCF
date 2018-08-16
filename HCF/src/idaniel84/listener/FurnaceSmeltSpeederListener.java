package idaniel84.listener;

import idaniel84.HCF;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

public class FurnaceSmeltSpeederListener implements Listener{
	private HCF plugin;

	public FurnaceSmeltSpeederListener(HCF plugin){
		this.plugin = plugin;
		final ShapedRecipe cmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
		cmelon.shape("AAA", "CBA", "AAA").setIngredient('B', Material.MELON).setIngredient('C', Material.GOLD_NUGGET);
		Bukkit.getServer().addRecipe(cmelon);
	}

	private void startUpdate(final Furnace tile, final int increase){
		new BukkitRunnable(){
			public void run(){
				if((tile.getCookTime() > 0) || (tile.getBurnTime() > 0)){
					tile.setCookTime((short) (tile.getCookTime() + increase));
					tile.update();
				}else{
					cancel();
				}
			}
		}.runTaskTimer(HCF.getInstance(), 1L, 10L);
	}

	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent event){
		startUpdate((Furnace) event.getBlock().getState(), 8);
	}

}