package lellson.roughTweaks;

import lellson.roughTweaks.items.RoughItemRegistry;
import lellson.roughTweaks.misc.RoughConfig;
import lellson.roughTweaks.misc.RoughEvents;
import lellson.roughTweaks.misc.factories.FactoryBandage;
import lellson.roughTweaks.misc.factories.FactoryMedikit;
import lellson.roughTweaks.misc.factories.FactoryMedikitEnchanted;
import lellson.roughTweaks.misc.factories.FactoryPlaster;
import lellson.roughTweaks.misc.factories.FactorySalve;
import lellson.roughTweaks.proxy.ClientProxy;
import lellson.roughTweaks.proxy.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "roughtweaks",
   name = "Rough Tweaks",
   version = "1.0",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class RoughTweaks {
   public static final String MODID = "roughtweaks";
   private ServerProxy proxy;

   public RoughTweaks() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new ClientProxy() : new ServerProxy();
         RoughItemRegistry.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         init((FMLInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      Configuration config = new Configuration(event.getSuggestedConfigurationFile());
      new RoughConfig(config);
      new RoughEvents().register(MinecraftForge.EVENT_BUS);
      CraftingHelper.register(new ResourceLocation(MODID, "EnableSalve"), new FactorySalve());
      CraftingHelper.register(new ResourceLocation(MODID, "EnablePlaster"), new FactoryPlaster());
      CraftingHelper.register(new ResourceLocation(MODID, "EnableBandage"), new FactoryBandage());
      CraftingHelper.register(new ResourceLocation(MODID, "EnableMedikit"), new FactoryMedikit());
      CraftingHelper.register(new ResourceLocation(MODID, "EnableMedikitEnchanted"), new FactoryMedikitEnchanted());
   }

   public void init(FMLInitializationEvent event) {
      proxy.registerRenderers();
   }
}
