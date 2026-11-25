package svenhjol.meson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public abstract class MesonLoader {
   public List<Module> modules = new ArrayList();
   public List<Feature> features = new ArrayList();
   public List<Class<? extends Feature>> enabledFeatures = new ArrayList();
   public List<Class<? extends Module>> enabledModules = new ArrayList();
   public static Map<String, MesonLoader> instances = new HashMap();
   public Configuration config;
   public String id;

   public MesonLoader() {
   }

   public MesonLoader registerModLoader(String id) {
      instances.put(id, this);
      this.id = id;
      return this;
   }

   public void setup(Module... mods) {
      this.modules.addAll(Arrays.asList(mods));
   }

   public void preInit(FMLPreInitializationEvent event) {
      File configFile = new File(event.getModConfigurationDirectory(), this.id + ".cfg");
      this.config = new Configuration(configFile, "1.4", true);
      this.config.load();
      this.modules.forEach((module) -> {
         module.enabled = ConfigHelper.propBoolean(this.config, module.getName(), "@Modules", module.getDescription(), true);
         module.setup(this, this.config);
      });
      ConfigHelper.saveIfChanged(this.config);
      this.modules.stream().filter(Module::isEnabled).forEach((module) -> {
         this.enabledModules.add(module.getClass());
         module.preInit(event);
      });
   }

   public void init(FMLInitializationEvent event) {
      this.modules.stream().filter(Module::isEnabled).forEach((m) -> {
         m.init(event);
      });
   }

   public void postInit(FMLPostInitializationEvent event) {
      this.modules.stream().filter(Module::isEnabled).forEach((m) -> {
         m.postInit(event);
      });
   }

   public void serverStarting(FMLServerStartingEvent event) {
      this.modules.stream().filter(Module::isEnabled).forEach((m) -> {
         m.serverStarting(event);
      });
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient(FMLPreInitializationEvent event) {
      this.modules.stream().filter(Module::isEnabled).forEach((m) -> {
         m.preInitClient(event);
      });
   }

   @SideOnly(Side.CLIENT)
   public void initClient(FMLInitializationEvent event) {
      this.modules.stream().filter(Module::isEnabled).forEach((m) -> {
         m.initClient(event);
      });
   }

   @SideOnly(Side.CLIENT)
   public void postInitClient(FMLPostInitializationEvent event) {
      this.modules.stream().filter(Module::isEnabled).forEach((m) -> {
         m.postInitClient(event);
      });
   }
}
