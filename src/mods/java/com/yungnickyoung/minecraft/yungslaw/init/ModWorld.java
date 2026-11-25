package com.yungnickyoung.minecraft.yungslaw.init;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import com.yungnickyoung.minecraft.yungslaw.event.EventWorld;
import com.yungnickyoung.minecraft.yungslaw.world.BlockGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModWorld {
   public ModWorld() {
   }

   public static void preInit() {
      GameRegistry.registerWorldGenerator(YungsLaw.generator = new BlockGenerator(), Integer.MAX_VALUE);
   }

   public static void init() {
      MinecraftForge.EVENT_BUS.register(new EventWorld());
   }
}
