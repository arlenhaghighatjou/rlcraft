package dev.satyrn.wolfarmor.api.compatibility;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.satyrn.wolfarmor.api.compatibility.client.LayerProvider;
import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor;
import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class Compatibility {
   private static final Map<String, CompatibilityProvider> compatibilityProviders = Maps.newLinkedHashMap();
   @SideOnly(Side.CLIENT)
   private static List<LayerProvider> layerOverrides;
   private static final Logger logger = LogManager.getLogger("wolfarmor");

   public Compatibility() {
   }

   public static synchronized boolean register(@Nonnull String providerName) {
      try {
         Class<?> providerClass = Compatibility.class.getClassLoader().loadClass(providerName);
         if (CompatibilityProvider.class.isAssignableFrom(providerClass) && providerClass.isAnnotationPresent(Provider.class)) {
            CompatibilityProvider instance = (CompatibilityProvider)Compatibility.class.getClassLoader().loadClass(providerName).newInstance();
            String modId = instance.getModId();
            CompatibilityProvider registeredProvider = (CompatibilityProvider)compatibilityProviders.get(modId);
            if (registeredProvider == null) {
               compatibilityProviders.put(modId, instance);
               logger.info("Registered new provider {} for mod {}", instance.getClass().getSimpleName(), modId);
               return true;
            } else if (instance.getPriority() == 32767 || registeredProvider.getPriority() < instance.getPriority() && instance.getPriority() != -32768) {
               logger.warn("Replacing provider {} with higher priority provider {} for mod {}", registeredProvider.getClass().getSimpleName(), instance.getClass().getSimpleName(), modId);
               compatibilityProviders.replace(modId, instance);
               return true;
            } else {
               logger.warn("Skipped registering provider {} for {}: the provider {} is already registered for this mod", instance.getClass().getSimpleName(), modId, registeredProvider.getClass().getSimpleName());
               return false;
            }
         } else {
            logger.info("Skipped invalid provider {}.", providerName);
            return false;
         }
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError var5) {
         logger.info("Skipped registering provider {}: {}", providerName, var5.toString());
         return false;
      }
   }

   @SideOnly(Side.CLIENT)
   public static synchronized void registerLayer(@Nonnull LayerProvider provider) {
      if (!layerOverrides.contains(provider)) {
         layerOverrides.add(provider);
      }

   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static synchronized LayerRenderer<?> getArmorLayer(@Nonnull RenderLiving<?> renderer) {
      return getArmorLayer(renderer, (String)null);
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static synchronized LayerRenderer<?> getArmorLayer(@Nonnull RenderLiving<?> renderer, @Nullable String filterSpec) {
      Optional<LayerProvider> provider = Optional.empty();
      if (filterSpec != null) {
         String[] filters = filterSpec.split(",");
         provider = layerOverrides.stream().filter(LayerProvider::getProvidesArmorLayer).filter((layerProvider) -> {
            boolean matches = true;

            for(String filter : filters) {
               if (filter.startsWith("!")) {
                  String exclude = filter.substring(1, filter.length() - 1);
                  matches &= !Objects.equals(layerProvider.getModId(), exclude);
               } else {
                  matches &= Objects.equals(layerProvider.getModId(), filter);
               }
            }

            return matches;
         }).max(new ProviderComparator());
      }

      if (!provider.isPresent()) {
         provider = layerOverrides.stream().filter(LayerProvider::getProvidesArmorLayer).max(new ProviderComparator());
      }

      return (LayerRenderer)provider.map((layerProvider) -> {
         return layerProvider.getArmorLayer(renderer);
      }).orElse(new LayerWolfArmor(renderer));
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static synchronized LayerRenderer<?> getBackpackLayer(@Nonnull RenderLiving<?> renderer) {
      return getBackpackLayer(renderer, (String)null);
   }

   @SideOnly(Side.CLIENT)
   @Nonnull
   public static synchronized LayerRenderer<?> getBackpackLayer(@Nonnull RenderLiving<?> renderer, @Nullable String modId) {
      Optional<LayerProvider> provider = Optional.empty();
      if (modId != null) {
         provider = layerOverrides.stream().filter(LayerProvider::getProvidesBackpackLayer).filter((layerProvider) -> {
            if (modId.startsWith("!")) {
               String excludeModId = modId.substring(1, modId.length() - 1);
               return !Objects.equals(layerProvider.getModId(), excludeModId);
            } else {
               return Objects.equals(layerProvider.getModId(), modId);
            }
         }).max(new ProviderComparator());
      }

      if (!provider.isPresent()) {
         provider = layerOverrides.stream().filter(LayerProvider::getProvidesBackpackLayer).max(new ProviderComparator());
      }

      return (LayerRenderer)provider.map((layerProvider) -> {
         return layerProvider.getBackpackLayer(renderer);
      }).orElse(new LayerWolfBackpack(renderer));
   }

   public static boolean isModLoaded(String modId) {
      List<ModContainer> mods = Loader.instance().getActiveModList();
      Optional<ModContainer> provided = mods.stream().filter((mod) -> {
         return modId.equalsIgnoreCase(mod.getModId());
      }).findFirst();
      return provided.isPresent();
   }

   @SideOnly(Side.CLIENT)
   public static void registerLayerProviders() {
      layerOverrides = Lists.newArrayList(new LayerProvider[]{new LayerProvider()});
      logger.info("Initializing layer providers...");
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Loading layer providers for " + provider.getModId() + "...");
            List<LayerProvider> layerProviders = provider.getLayerProviders();
            layerProviders.forEach(Compatibility::registerLayer);
         }

      });
   }

   @SideOnly(Side.CLIENT)
   public static void loadComplete_Client(FMLLoadCompleteEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing client-side compatibility loadComplete for " + provider.getModId() + "...");
            provider.loadComplete_Client(event);
         }

      });
   }

   public static void loadComplete(FMLLoadCompleteEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing server-side compatibility loadComplete for " + provider.getModId() + "...");
            provider.loadComplete(event);
         }

      });
   }

   @SideOnly(Side.CLIENT)
   public static void postInit_Client(FMLPostInitializationEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing client-side compatibility postInit for " + provider.getModId() + "...");
            provider.postInit_Client(event);
         }

      });
   }

   public static void postInit(FMLPostInitializationEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing server-side compatibility postInit for " + provider.getModId() + "...");
            provider.postInit(event);
         }

      });
   }

   @SideOnly(Side.CLIENT)
   public static void init_Client(FMLInitializationEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing client-side compatibility init for " + provider.getModId() + "...");
            provider.init_Client(event);
         }

      });
   }

   public static void init(FMLInitializationEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing server-side compatibility init for " + provider.getModId() + "...");
            provider.init(event);
         }

      });
   }

   @SideOnly(Side.CLIENT)
   public static void preInit_Client(FMLPreInitializationEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing client-side compatibility preInit for " + provider.getModId() + "...");
            provider.preInit_Client(event);
         }

      });
   }

   public static void preInit(FMLPreInitializationEvent event) {
      compatibilityProviders.values().forEach((provider) -> {
         if (isModLoaded(provider.getModId())) {
            logger.info("Performing server-side compatibility preInit for " + provider.getModId() + "...");
            provider.preInit(event);
         }

      });
   }
}
