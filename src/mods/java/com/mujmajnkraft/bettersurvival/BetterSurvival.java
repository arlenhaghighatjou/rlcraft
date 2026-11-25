package com.mujmajnkraft.bettersurvival;

import com.mujmajnkraft.bettersurvival.capabilities.extendedarrowproperties.ArrowProperties;
import com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo.NunchakuCombo;
import com.mujmajnkraft.bettersurvival.capabilities.spearsinentity.SpearsIn;
import com.mujmajnkraft.bettersurvival.eventhandlers.CommonEventHandler;
import com.mujmajnkraft.bettersurvival.eventhandlers.TickEventHandler;
import com.mujmajnkraft.bettersurvival.init.ModBlocks;
import com.mujmajnkraft.bettersurvival.init.ModCrafting;
import com.mujmajnkraft.bettersurvival.init.ModEnchantments;
import com.mujmajnkraft.bettersurvival.init.ModEntities;
import com.mujmajnkraft.bettersurvival.init.ModItems;
import com.mujmajnkraft.bettersurvival.init.ModPotionTypes;
import com.mujmajnkraft.bettersurvival.init.ModPotions;
import com.mujmajnkraft.bettersurvival.integration.InspirationsCauldronCompat;
import com.mujmajnkraft.bettersurvival.integration.RLCombatCompatEventHandler;
import com.mujmajnkraft.bettersurvival.packet.BetterSurvivalPacketHandler;
import com.mujmajnkraft.bettersurvival.proxy.CommonProxy;
import com.mujmajnkraft.bettersurvival.tileentities.TileEntityCustomCauldron;
import java.io.File;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "mujmajnkraftsbettersurvival",
   name = "Better Survival",
   version = "1.5.3",
   acceptedMinecraftVersions = "[1.12, 1.13)",
   dependencies = "required-after:reachfix;after:bettercombatmod;after:iceandfire;after:somanyenchantments;after:inspirations"
)
public class BetterSurvival {
   @Instance
   public static BetterSurvival instance;
   public static File config;
   public static Logger LOG = LogManager.getLogger("bettersurvival");
   public static boolean isIafLoaded;
   public static boolean isRLCombatLoaded;
   public static boolean isSMELoaded;
   public static boolean isInspirationsLoaded;
   @SidedProxy(
      clientSide = "com.mujmajnkraft.bettersurvival.proxy.ClientProxy",
      serverSide = "com.mujmajnkraft.bettersurvival.proxy.ServerProxy"
   )
   public static CommonProxy proxy;

   public BetterSurvival() {
   }

   @EventHandler
   public static void preInit(FMLPreInitializationEvent event) {
      if (Loader.isModLoaded("iceandfire")) {
         isIafLoaded = iafVersionChecker(((ModContainer)Loader.instance().getIndexedModList().get("iceandfire")).getVersion());
      }

      if (Loader.isModLoaded("bettercombatmod")) {
         isRLCombatLoaded = rlcombatVersionChecker(((ModContainer)Loader.instance().getIndexedModList().get("bettercombatmod")).getVersion());
      }

      isSMELoaded = Loader.isModLoaded("somanyenchantments");
      isInspirationsLoaded = Loader.isModLoaded("inspirations") && InspirationsCauldronCompat.inspirationsExtendedCauldron();
      ArrowProperties.Register();
      NunchakuCombo.Register();
      SpearsIn.Register();
      config = new File(event.getModConfigurationDirectory() + File.separator + "mujmajnkraftsbettersurvival");
      config.mkdirs();
      ModPotions.init();
      MinecraftForge.EVENT_BUS.register(new ModItems());
      if (!isInspirationsLoaded) {
         MinecraftForge.EVENT_BUS.register(new ModBlocks());
      }

      MinecraftForge.EVENT_BUS.register(new ModPotionTypes());
      MinecraftForge.EVENT_BUS.register(new ModPotions());
      MinecraftForge.EVENT_BUS.register(new ModEnchantments());
      ModEntities.registerEntities();
   }

   @EventHandler
   public static void Init(FMLInitializationEvent event) {
      proxy.init();
      BetterSurvivalPacketHandler.init();
      ModItems.setRepairMaterials();
      MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
      if (isRLCombatLoaded) {
         MinecraftForge.EVENT_BUS.register(new RLCombatCompatEventHandler());
      }

      FMLCommonHandler.instance().bus().register(new TickEventHandler());
      if (!isInspirationsLoaded) {
         GameRegistry.registerTileEntity(TileEntityCustomCauldron.class, new ResourceLocation("mujmajnkraftsbettersurvival", "customcauldron"));
      } else {
         InspirationsCauldronCompat.initCauldronRecipes();
      }

      ModCrafting.register();
   }

   static boolean iafVersionChecker(String str) {
      String[] arrOfStr = str.split("\\.");

      try {
         int i = Integer.parseInt(String.valueOf(arrOfStr[1]));
         if (i >= 5 && i <= 8) {
            return true;
         }
      } catch (Exception var3) {
         ;
      }

      return false;
   }

   static boolean rlcombatVersionChecker(String str) {
      String[] arrOfStr = str.split("\\.");

      try {
         int i = Integer.parseInt(String.valueOf(arrOfStr[0]));
         if (i == 2) {
            return true;
         }
      } catch (Exception var3) {
         ;
      }

      return false;
   }
}
