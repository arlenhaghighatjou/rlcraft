package com.elenai.elenaidodge;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import com.elenai.elenaidodge.command.MainCommandTree;
import com.elenai.elenaidodge.integration.ReskillableTraitDodge;
import com.elenai.elenaidodge.integration.ReskillableTraitLedgeGrab;
import com.elenai.elenaidodge.integration.ReskillableTraitWallJump;
import com.elenai.elenaidodge.proxy.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "elenaidodge",
   name = "Elenai Dodge",
   version = "2.1",
   dependencies = "before:rustic;after:quark",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ElenaiDodge {
   public static final String MODID = "elenaidodge";
   public static final String NAME = "Elenai Dodge";
   public static final String VERSION = "2.1";
   @Instance
   public static ElenaiDodge INSTANCE;
   public static final Logger LOG = LogManager.getLogger("ElenaiDodge");
   @SidedProxy(
      clientSide = "com.elenai.elenaidodge.proxy.ClientProxy",
      serverSide = "com.elenai.elenaidodge.proxy.ServerProxy"
   )
   private static CommonProxy proxy;

   public ElenaiDodge() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
      if (Loader.isModLoaded("reskillable")) {
         ReskillableRegistries.UNLOCKABLES.registerAll(new Unlockable[]{new ReskillableTraitDodge(), new ReskillableTraitWallJump(), new ReskillableTraitLedgeGrab()});
      }

   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new MainCommandTree());
   }
}
