package de.Whitedraco.switchbow;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RegisterUtil {
   public RegisterUtil() {
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) throws IOException {
      BlockInit.prepareBlocks();
      BlockInit.registerBlocks(event);
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) throws IOException {
      ItemInit.prepareItems();
      ItemInit.registerItems(event);
   }

   @SubscribeEvent
   public static void registerPotion(Register<Potion> event) {
      PotionInit.preparePotion();
      PotionInit.registerPotion(event);
   }

   @SubscribeEvent
   public static void registerEnchant(Register<Enchantment> event) {
      EnchantInit.prepareEnchant();
      EnchantInit.registerEnchant(event);
   }
}
