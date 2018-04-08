package net.veilmc.hcf.command.crate.type;

import net.veilmc.hcf.utils.ConfigurationService;
import net.veilmc.hcf.command.crate.EnderChestKey;
import net.veilmc.util.ItemBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class PalaceKey
		extends EnderChestKey{
	public PalaceKey(){
		super("Palace", 4);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 3).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL).intValue()).displayName(ChatColor.YELLOW + "Palace Fire").build(), 3);
		this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 16), 15);
		this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 16), 15);
		this.setupRarity(new ItemStack(Material.IRON_BLOCK, 16), 15);
		this.setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Bard Helmet").build(), 1);
		this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Bard Chestplate").build(), 1);
		this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Bard Leggings").build(), 1);
		this.setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Bard Boots").build(), 1);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.LOOT_BONUS_MOBS, 5).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL).intValue()).displayName(ChatColor.YELLOW + "Palace Looting").build(), 7);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 5).displayName(ChatColor.YELLOW + "Palace Fortune").build(), 5);
		this.setupRarity(new ItemBuilder(Material.SKULL_ITEM, 3).data((short) 1).build(), 2);
		this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 4).data((short) 1).build(), 1);
		this.setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE).intValue()).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).displayName(ChatColor.RED + "Conquest Bow").build(), 3);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Conquest Helmet").build(), 1);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Conquest Chestplate").build(), 1);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Conquest Leggings").build(), 1);
		this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL).intValue()).displayName(ChatColor.RED + "Conquest Boots").build(), 1);
		this.setupRarity(new ItemStack(Material.EMERALD_BLOCK, 16), 7);
		this.setupRarity(new ItemStack(Material.ENDER_PEARL, 32), 5);
		this.setupRarity(new ItemStack(Material.NETHER_STAR, 1), 5);

	}

	@Override
	public ChatColor getColour(){
		return ChatColor.DARK_PURPLE;
	}

	@Override
	public boolean getBroadcastItems(){
		return true;
	}
}

