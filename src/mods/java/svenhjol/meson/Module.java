package svenhjol.meson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.meson.helper.ConfigHelper;
import svenhjol.meson.iface.IFMLEvents;

public abstract class Module implements IFMLEvents {
   public List<Feature> features = new ArrayList();
   public boolean enabled;
   public MesonLoader loader;
   public Configuration config;

   public Module() {
   }

   public void setup(MesonLoader loader, Configuration config) {
      this.loader = loader;
      this.config = config;
      this.configure();
   }

   public void configure() {
      this.features.forEach((feature) -> {
         this.loader.features.add(feature);
         feature.enabled = ConfigHelper.propBoolean(this.config, feature.getName(), this.getName(), feature.getDescription(), feature.enabledByDefault);
         feature.setup(this);
      });
   }

   public String getName() {
      return this.getClass().getSimpleName();
   }

   public String getDescription() {
      return this.getName();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void preInit(FMLPreInitializationEvent event) {
      Meson.log("Starting module " + this.getName());
      this.features.stream().filter(Feature::isEnabled).forEach((feature) -> {
         this.loader.enabledFeatures.add(feature.getClass());
         Meson.log("Starting feature " + feature.getName());
         feature.preInit(event);
      });
   }

   public void init(FMLInitializationEvent event) {
      this.features.stream().filter(Feature::isEnabled).forEach((f) -> {
         f.init(event);
      });
   }

   public void postInit(FMLPostInitializationEvent event) {
      this.features.stream().filter(Feature::isEnabled).forEach((f) -> {
         f.postInit(event);
      });
   }

   public void serverStarting(FMLServerStartingEvent event) {
      this.features.stream().filter(Feature::isEnabled).forEach((f) -> {
         f.serverStarting(event);
      });
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient(FMLPreInitializationEvent event) {
      this.features.stream().filter(Feature::isEnabled).forEach((f) -> {
         f.preInitClient(event);
      });
   }

   @SideOnly(Side.CLIENT)
   public void initClient(FMLInitializationEvent event) {
      this.features.stream().filter(Feature::isEnabled).forEach((f) -> {
         f.initClient(event);
      });
   }

   @SideOnly(Side.CLIENT)
   public void postInitClient(FMLPostInitializationEvent event) {
      this.features.stream().filter(Feature::isEnabled).forEach((f) -> {
         f.postInitClient(event);
      });
   }
}
