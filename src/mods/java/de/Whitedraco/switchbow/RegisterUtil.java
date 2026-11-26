package de.Whitedraco.switchbow;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent.Register;

public class RegisterUtil {
   public RegisterUtil() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, RegisterUtil::registerBlocks);
      bus.registerGeneric(Register.class, Item.class, RegisterUtil::registerItems);
      bus.registerGeneric(Register.class, Potion.class, RegisterUtil::registerPotion);
      bus.registerGeneric(Register.class, Enchantment.class, RegisterUtil::registerEnchant);
   }

   public static void registerBlocks(Register<Block> event) {
      BlockInit.prepareBlocks();
      BlockInit.registerBlocks(event);
   }

   public static void registerItems(Register<Item> event) {
      ItemInit.prepareItems();
      ItemInit.registerItems(event);
   }

   public static void registerPotion(Register<Potion> event) {
      PotionInit.preparePotion();
      PotionInit.registerPotion(event);
   }

   public static void registerEnchant(Register<Enchantment> event) {
      EnchantInit.prepareEnchant();
      EnchantInit.registerEnchant(event);
   }
}
