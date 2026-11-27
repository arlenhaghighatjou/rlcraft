package snownee.kiwi;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import java.util.Map;
import java.util.function.Supplier;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import snownee.kiwi.block.IModBlock;
import snownee.kiwi.client.AdvancedFontRenderer;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.potion.PotionMod;

@Mod(
   modid = "kiwi",
   name = "Kiwi",
   version = "0.5.3.32",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class Kiwi {
   public static final String MODID = "kiwi";
   public static final String NAME = "Kiwi";
   private static final Kiwi INSTANCE = new Kiwi();
   public static Logger logger;

   public Kiwi() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("kiwi", "no_containers"), new snownee.kiwi.crafting.RecipeNoContainers.Factory());
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("kiwi", "is_loaded"), new snownee.kiwi.crafting.ConditionModuleLoaded());
         snownee.kiwi.KiwiConfig.register();
         snownee.kiwi.KiwiManager.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post.class, snownee.kiwi.client.AdvancedFontRenderer::onLanguageGuiOpening);
         }
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            snownee.kiwi.client.ModelRendering.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   @InstanceFactory
   public static Kiwi getInstance() {
      return INSTANCE;
   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      logger.info("Processing " + KiwiModules.MODULES.size() + " KiwiModule annotations");
      Map<String, ModContainer> map = Loader.instance().getIndexedModList();

      for(KiwiModules.Info data : KiwiModules.MODULES) {
         String modid = data.modid;
         String name = data.name;
         if (name == null) {
            name = modid;
         }

         if (data.optional) {
            Boolean enabled = (Boolean)KiwiConfig.MODULES.modules.get(modid + ":" + name);
            if (enabled == null) {
               KiwiConfig.MODULES.modules.put(modid + ":" + name, Boolean.valueOf(!data.disabledByDefault));
               if (data.disabledByDefault) {
                  continue;
               }
            } else if (enabled == Boolean.FALSE) {
               continue;
            }
         }

         String dependency = data.dependency;
         if (dependency == null || Loader.isModLoaded(dependency)) {
            Loader.instance().setActiveModContainer((ModContainer)map.get(modid));
            IModule instance = (IModule)data.factory.get();
            KiwiManager.addInstance(new ResourceLocation(modid, name), instance);
            KiwiModules.ENTRIES.put(instance, data.entries);
            Loader.instance().setActiveModContainer((ModContainer)null);
         }
      }

      ConfigManager.sync("kiwi", Type.INSTANCE);

      for(Entry<ResourceLocation, IModule> entry : KiwiManager.MODULES.entrySet()) {
         int countBlock = 0;
         int countItem = 0;
         String modid = ((ResourceLocation)entry.getKey()).getResourceDomain();
         String name = ((ResourceLocation)entry.getKey()).getResourcePath();

         for(Object o : (Object[])((Supplier)KiwiModules.ENTRIES.get(entry.getValue())).get()) {
            {
               if (o != null) {
                  if (o instanceof IModBlock) {
                     KiwiManager.BLOCKS.put((IModBlock)o, modid);
                     ++countBlock;
                  } else if (o instanceof IModItem) {
                     KiwiManager.ITEMS.put((IModItem)o, modid);
                     ++countItem;
                  } else if (o instanceof PotionMod) {
                     KiwiManager.POTIONS.put((PotionMod)o, modid);
                  }
               }
            }
         }

         logger.info("[{}:{}]: Block: {}, Item: {}", modid, name, Integer.valueOf(countBlock), Integer.valueOf(countItem));
      }

      Loader.instance().setActiveModContainer((ModContainer)null);
      KiwiManager.MODULES.values().forEach(IModule::preInit);
   }

   public void init(FMLInitializationEvent event) {
      if (KiwiConfig.GENERAL.replaceDefaultFontRenderer && event.getSide() == Side.CLIENT) {
         replaceFontRenderer();
      }

      Loader.instance().setActiveModContainer((ModContainer)null);
      KiwiManager.MODULES.values().forEach(IModule::init);
   }

   public void postInit(FMLPostInitializationEvent event) {
      Loader.instance().setActiveModContainer((ModContainer)null);
      KiwiManager.MODULES.values().forEach(IModule::postInit);
      KiwiManager.BLOCKS.clear();
      KiwiManager.BLOCKS = null;
      KiwiManager.ITEMS.clear();
      KiwiManager.ITEMS = null;
      KiwiManager.POTIONS.clear();
      KiwiManager.POTIONS = null;
   }

   public static boolean isOptionalModuleLoaded(String modid, String name) {
      return isLoaded(new ResourceLocation(modid, name));
   }

   public static boolean isLoaded(ResourceLocation module) {
      return KiwiManager.ENABLED_MODULES.contains(module);
   }

   @SideOnly(Side.CLIENT)
   private static void replaceFontRenderer() {
      Minecraft.getMinecraft().fontRenderer = AdvancedFontRenderer.INSTANCE;
   }
}
