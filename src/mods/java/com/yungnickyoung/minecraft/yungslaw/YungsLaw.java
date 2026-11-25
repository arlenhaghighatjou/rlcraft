package com.yungnickyoung.minecraft.yungslaw;

import com.yungnickyoung.minecraft.yungslaw.config.util.ConfigHolder;
import com.yungnickyoung.minecraft.yungslaw.proxy.Proxy;
import com.yungnickyoung.minecraft.yungslaw.world.BlockGenerator;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "yungslaw",
   name = "YUNG's Law",
   version = "1.12.2",
   useMetadata = true,
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class YungsLaw {
   public static final Logger LOGGER = LogManager.getLogger("yungslaw");
   @SidedProxy(
      clientSide = "com.yungnickyoung.minecraft.yungslaw.proxy.ClientProxy",
      serverSide = "com.yungnickyoung.minecraft.yungslaw.proxy.ServerProxy"
   )
   private static Proxy proxy;
   public static File customConfigDir;
   public static Map<Integer, ConfigHolder> configMap = new HashMap();
   public static BlockGenerator generator;

   public YungsLaw() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }
}
