package com.yungnickyoung.minecraft.bettermineshafts;

import com.yungnickyoung.minecraft.bettermineshafts.init.ModCompat;
import com.yungnickyoung.minecraft.bettermineshafts.init.ModStructure;
import com.yungnickyoung.minecraft.bettermineshafts.init.ModStructurePieces;
import com.yungnickyoung.minecraft.bettermineshafts.proxy.IProxy;
import io.netty.util.internal.ConcurrentSet;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "bettermineshafts",
   useMetadata = true,
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class BetterMineshafts {
   public static final Logger LOGGER = LogManager.getLogger("bettermineshafts");
   @SidedProxy(
      clientSide = "com.yungnickyoung.minecraft.bettermineshafts.proxy.ClientProxy",
      serverSide = "com.yungnickyoung.minecraft.bettermineshafts.proxy.ServerProxy"
   )
   public static IProxy proxy;
   public static final boolean DEBUG_LOG = false;
   public static ConcurrentSet<Integer> surfaceEntrances = new ConcurrentSet();
   public static AtomicInteger count = new AtomicInteger(0);

   public BetterMineshafts() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      ModStructure.init();
      ModStructurePieces.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      ModCompat.postInit();
   }
}
