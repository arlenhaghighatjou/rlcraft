package com.Shultrea.Rin.Utility_Sector;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommProxy {
   final List<SoundEvent> SOUND_EVENTS = Lists.newArrayList();

   public CommProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      SMElogM.logger.info("PreInitializing Rin's So Many Enchantments?...");
      SMEnetwork.mainRegistry();
   }

   public void onInit(FMLInitializationEvent event) {
      SMElogM.logger.info("Initializing Rin's So Many Enchantments?...");
   }

   public void postInit(FMLPostInitializationEvent event) {
      SMElogM.logger.info("PostInitializing Rin's So Many Enchantments?...");
   }

   @Nullable
   public EntityPlayer getClientPlayer() {
      return null;
   }
}
