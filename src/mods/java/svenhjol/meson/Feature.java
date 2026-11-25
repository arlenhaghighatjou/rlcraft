package svenhjol.meson;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.meson.helper.ConfigHelper;
import svenhjol.meson.iface.IFMLEvents;

public abstract class Feature implements IFMLEvents {
   public boolean enabled;
   public boolean enabledByDefault = true;
   protected Module module;
   protected MesonLoader loader;
   protected Configuration config;

   public Feature() {
   }

   public void setup(Module module) {
      this.module = module;
      this.config = module.config;
      this.loader = module.loader;
      Meson.log("Configuring feature " + this.getName());
      this.configure();
   }

   public void configure() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (this.hasSubscriptions()) {
         MinecraftForge.EVENT_BUS.register(this);
      }

      if (this.hasTerrainSubscriptions()) {
         MinecraftForge.TERRAIN_GEN_BUS.register(this);
      }

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

   public boolean isEnabledByDefault() {
      return true;
   }

   public boolean hasSubscriptions() {
      return false;
   }

   public boolean hasTerrainSubscriptions() {
      return false;
   }

   public String getConfigCategoryName() {
      return this.module.getName() + "." + this.getName();
   }

   public int propInt(String name, String description, int def) {
      return ConfigHelper.propInt(this.config, name, this.getConfigCategoryName(), description + " (default " + def + ")", def);
   }

   public double propDouble(String name, String description, double def) {
      return ConfigHelper.propDouble(this.config, name, this.getConfigCategoryName(), description + " (default " + def + ")", def);
   }

   public boolean propBoolean(String name, String description, boolean def) {
      return ConfigHelper.propBoolean(this.config, name, this.getConfigCategoryName(), description + " (default " + def + ")", def);
   }

   public String propString(String name, String description, String def) {
      return ConfigHelper.propString(this.config, name, this.getConfigCategoryName(), description + " (default " + def + ")", def);
   }

   public String[] propStringList(String name, String description, String[] def) {
      return ConfigHelper.propStringList(this.config, name, this.getConfigCategoryName(), description, def);
   }
}
