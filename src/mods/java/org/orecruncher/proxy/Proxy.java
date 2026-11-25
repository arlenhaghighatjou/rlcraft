package org.orecruncher.proxy;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.lib.Localization;

public class Proxy {
   public Proxy() {
   }

   public boolean isRunningAsServer() {
      return true;
   }

   public Side effectiveSide() {
      return Side.SERVER;
   }

   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      Localization.initialize(Side.SERVER, "orelib");
   }

   public void init(@Nonnull FMLInitializationEvent event) {
   }

   public void postInit(@Nonnull FMLPostInitializationEvent event) {
   }

   public void loadCompleted(@Nonnull FMLLoadCompleteEvent event) {
   }
}
