package idaniel84.command.crate.type;

import idaniel84.crowbar.Crowbar;
import idaniel84.utils.ConfigurationService;
import idaniel84.crowbar.Crowbar;
import idaniel84.utils.ConfigurationService;
import idaniel84.crowbar.Crowbar;
import idaniel84.utils.ConfigurationService;
import idaniel84.utils.ConfigurationService;
import idaniel84.command.crate.EnderChestKey;
import idaniel84.crowbar.Crowbar;
import net.veilmc.util.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Tier2Key
		extends EnderChestKey{
	public Tier2Key(){
		super("TierII", 4);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).build(), 5);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).build(), 5);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).enchant(Enchantment.DURABILITY, 3).build(), 5);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).build(), 5);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.LOOT_BONUS_MOBS, 3).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL).intValue()).build(), 6);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_BLOCK, 4).build(), 10);
		this.setupRarity(new ItemBuilder(Material.GOLD_BLOCK, 4).build(), 10);
		this.setupRarity(new ItemBuilder(Material.IRON_BLOCK, 4).build(), 10);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).enchant(Enchantment.DURABILITY, 3).build(), 2);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.SILK_TOUCH, 1).enchant(Enchantment.DURABILITY, 3).build(), 2);
		this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 4), 15);
		this.setupRarity(new ItemStack(Material.EMERALD_BLOCK, 4), 10);
		this.setupRarity(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 1);
		this.setupRarity(new ItemStack(Material.SPECKLED_MELON, 10), 10);
		this.setupRarity(new ItemStack(Material.SULPHUR, 8), 10);
		this.setupRarity(new ItemStack(Material.BLAZE_ROD, 4), 7);
		this.setupRarity(new ItemStack(Material.SUGAR, 8), 10);
		this.setupRarity(new ItemStack(Material.SPIDER_EYE, 8), 10);
		this.setupRarity(new ItemStack(Material.GLOWSTONE_DUST, 16), 10);
		this.setupRarity(new ItemStack(Material.GLASS_BOTTLE, 16), 15);
		this.setupRarity(new ItemStack(Material.ENDER_PEARL, 8), 5);
		this.setupRarity(new ItemStack(Material.POTATO, 4), 3);
		this.setupRarity(new ItemStack(Material.ENDER_PORTAL_FRAME, 1), 3);
		this.setupRarity(new Crowbar().getItemIfPresent(), 1);
		this.setupRarity(new ItemStack(Material.BEACON, 1), 1);
	}

	@Override
	public ChatColor getColour(){
		return ChatColor.GREEN;
	}
}

