package net.theawesomegem.fishingmadebetter.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.theawesomegem.fishingmadebetter.common.block.BlockManager;
import net.theawesomegem.fishingmadebetter.common.item.ItemManager;

public class RegistryManager {
   public RegistryManager() {
   }

   @EventBusSubscriber
   public static class RegistryHandler {
      public static SoundEvent FISH_SPLASHING_EVENT;
      public static SoundEvent REELING_EVENT;

      public RegistryHandler() {
      }

      @SubscribeEvent
      public static void onRegisterBlock(Register<Block> e) {
         BlockManager.registerBlocks(e.getRegistry());
      }

      @SubscribeEvent
      public static void onRegisterItem(Register<Item> e) {
         BlockManager.registerItemBlocks(e.getRegistry());
         ItemManager.register(e.getRegistry());
      }

      @SubscribeEvent
      public static void onRegisterSound(Register<SoundEvent> e) {
         FISH_SPLASHING_EVENT = (SoundEvent)(new SoundEvent(new ResourceLocation("fishingmadebetter", "fishsplashing"))).setRegistryName("soundfishsplashing");
         REELING_EVENT = (SoundEvent)(new SoundEvent(new ResourceLocation("fishingmadebetter", "reeling"))).setRegistryName("soundreeling");
         e.getRegistry().registerAll(new SoundEvent[]{FISH_SPLASHING_EVENT, REELING_EVENT});
      }
   }
}
