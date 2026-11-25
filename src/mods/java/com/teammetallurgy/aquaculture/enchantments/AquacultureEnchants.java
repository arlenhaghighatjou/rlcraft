package com.teammetallurgy.aquaculture.enchantments;

import com.teammetallurgy.aquaculture.items.AquacultureItems;
import java.util.function.Predicate;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class AquacultureEnchants {
   public static EnumEnchantmentType enumFishingPole;
   public static EnchantmentFishingPole appealing;
   public static EnchantmentFishingPole magnetic;
   public static EnchantmentFishingPole longcast;
   public static EnchantmentFishingPole shortcast;
   public static EnchantmentFishingPole doubleHook;
   public static EnchantmentFishingPole barbedHook;
   public static EnchantmentFishingPole heavyLine;

   public AquacultureEnchants() {
   }

   public static void init() {
      Predicate<Item> isAquacultureRod = (item) -> {
         return item == AquacultureItems.adminFishingRod || item == AquacultureItems.diamondFishingRod || item == AquacultureItems.goldFishingRod || item == AquacultureItems.ironFishingRod || item == AquacultureItems.woodenFishingRod;
      };
      isAquacultureRod.getClass();
      enumFishingPole = EnumHelper.addEnchantmentType("fishingPole", isAquacultureRod::test);
      appealing = new EnchantmentAppealing(Rarity.RARE);
      appealing.setRegistryName("appealing");
      ForgeRegistries.ENCHANTMENTS.register(appealing);
      magnetic = new EnchantmentMagnetic(Rarity.RARE);
      magnetic.setRegistryName("magnetic");
      ForgeRegistries.ENCHANTMENTS.register(magnetic);
      longcast = new EnchantmentLongCast(Rarity.COMMON);
      longcast.setRegistryName("long_cast");
      ForgeRegistries.ENCHANTMENTS.register(longcast);
      shortcast = new EnchantmentShortCast(Rarity.COMMON);
      shortcast.setRegistryName("short_cast");
      ForgeRegistries.ENCHANTMENTS.register(shortcast);
      doubleHook = new EnchantmentDoubleHook(Rarity.VERY_RARE);
      doubleHook.setRegistryName("double_hook");
      ForgeRegistries.ENCHANTMENTS.register(doubleHook);
      barbedHook = new EnchantmentBardedHook(Rarity.VERY_RARE);
      barbedHook.setRegistryName("barbed_hook");
      ForgeRegistries.ENCHANTMENTS.register(barbedHook);
      heavyLine = new EnchantmentHeavyLine(Rarity.UNCOMMON);
      heavyLine.setRegistryName("heavy_line");
      ForgeRegistries.ENCHANTMENTS.register(heavyLine);
   }
}
