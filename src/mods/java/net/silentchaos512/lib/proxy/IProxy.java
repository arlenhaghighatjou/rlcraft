package net.silentchaos512.lib.proxy;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.registry.SRegistry;

public interface IProxy {
   void preInit(SRegistry var1, FMLPreInitializationEvent var2);

   void init(SRegistry var1, FMLInitializationEvent var2);

   void postInit(SRegistry var1, FMLPostInitializationEvent var2);

   @Nullable
   EntityPlayer getClientPlayer();

   int getParticleSettings();
}
