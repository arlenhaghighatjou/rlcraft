package vazkii.quark.base.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.module.FeatureEvent;
import vazkii.quark.api.module.IFeature;
import vazkii.quark.api.module.IModule;
import vazkii.quark.base.Quark;

public class Module implements IModule {
   public final String name = this.makeName();
   public final Map<String, Feature> features = new HashMap();
   public final List<Feature> enabledFeatures = new ArrayList();
   public boolean enabled;
   public Property prop;

   public Module() {
   }

   public void addFeatures() {
   }

   public void registerFeature(Feature feature) {
      this.registerFeature(feature, this.convertName(feature.getClass().getSimpleName()));
   }

   public void registerFeature(Feature feature, boolean enabledByDefault) {
      this.registerFeature(feature, this.convertName(feature.getClass().getSimpleName()), enabledByDefault);
   }

   public String convertName(String origName) {
      String withSpaces = origName.replaceAll("(?<=.)([A-Z])", " $1").toLowerCase();
      return Character.toUpperCase(withSpaces.charAt(0)) + withSpaces.substring(1);
   }

   public void registerFeature(Feature feature, String name) {
      this.registerFeature(feature, name, true);
   }

   public void registerFeature(Feature feature, String name, boolean enabledByDefault) {
      Class<? extends Feature> clazz = feature.getClass();
      if (ModuleLoader.featureInstances.containsKey(clazz)) {
         throw new IllegalArgumentException("Feature " + clazz + " is already registered!");
      } else {
         feature.enabledByDefault = enabledByDefault;
         feature.prevEnabled = false;
         feature.module = this;
         feature.configName = name;
         feature.configCategory = this.name + "." + name;
         if (!MinecraftForge.EVENT_BUS.post(new FeatureEvent.Loaded(feature))) {
            ModuleLoader.featureInstances.put(clazz, feature);
            ModuleLoader.featureClassnames.put(clazz.getSimpleName(), feature);
            this.features.put(name, feature);
         }

      }
   }

   public void setupConfig() {
      if (this.features.isEmpty()) {
         this.addFeatures();
      }

      this.forEachFeature((feature) -> {
         ConfigHelper.needsRestart = feature.requiresMinecraftRestartToEnable();
         feature.enabled = this.loadPropBool(feature.configName, feature.getFeatureDescription(), feature.enabledByDefault) && this.enabled;
         feature.prop = ConfigHelper.lastProp;
         feature.setupConstantConfig();
         if (!feature.forceLoad && GlobalConfig.enableAntiOverlap) {
            String[] incompatibilities = feature.getIncompatibleMods();
            if (incompatibilities != null) {
               List<String> failures = new ArrayList();

               for(String s : incompatibilities) {
                  if (Loader.isModLoaded(s)) {
                     feature.enabled = false;
                     failures.add(s);
                  }
               }

               if (!failures.isEmpty()) {
                  Quark.LOG.info("'" + feature.configName + "' is forcefully disabled as it's incompatible with the following loaded mods: " + failures);
               }
            }
         }

         if (!feature.loadtimeDone) {
            feature.enabledAtLoadtime = feature.enabled;
            feature.loadtimeDone = true;
         }

         if (feature.enabled && !this.enabledFeatures.contains(feature)) {
            this.enabledFeatures.add(feature);
         } else if (!feature.enabled) {
            this.enabledFeatures.remove(feature);
         }

         feature.setupConfig();
         if (!feature.enabled && feature.prevEnabled) {
            MinecraftForge.EVENT_BUS.post(new FeatureEvent.Disabled(feature));
            if (feature.hasSubscriptions()) {
               MinecraftForge.EVENT_BUS.unregister(feature);
            }

            if (feature.hasTerrainSubscriptions()) {
               MinecraftForge.TERRAIN_GEN_BUS.unregister(feature);
            }

            if (feature.hasOreGenSubscriptions()) {
               MinecraftForge.ORE_GEN_BUS.unregister(feature);
            }

            feature.onDisabled();
            MinecraftForge.EVENT_BUS.post(new FeatureEvent.PostDisable(feature));
         } else if (feature.enabled && (feature.enabledAtLoadtime || !feature.requiresMinecraftRestartToEnable()) && !feature.prevEnabled) {
            MinecraftForge.EVENT_BUS.post(new FeatureEvent.Enabled(feature));
            if (feature.hasSubscriptions()) {
               MinecraftForge.EVENT_BUS.register(feature);
            }

            if (feature.hasTerrainSubscriptions()) {
               MinecraftForge.TERRAIN_GEN_BUS.register(feature);
            }

            if (feature.hasOreGenSubscriptions()) {
               MinecraftForge.ORE_GEN_BUS.register(feature);
            }

            feature.onEnabled();
            MinecraftForge.EVENT_BUS.post(new FeatureEvent.PostEnable(feature));
         }

         feature.prevEnabled = feature.enabled;
      });
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.forEachEnabled((feature) -> {
         feature.preInit(event);
      });
   }

   public void postPreInit() {
      this.forEachEnabled(Feature::postPreInit);
   }

   public void init() {
      this.forEachEnabled(Feature::init);
   }

   public void postInit() {
      this.forEachEnabled(Feature::postInit);
   }

   public void finalInit() {
      this.forEachEnabled(Feature::finalInit);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      this.forEachEnabled(Feature::preInitClient);
   }

   @SideOnly(Side.CLIENT)
   public void initClient() {
      this.forEachEnabled(Feature::initClient);
   }

   @SideOnly(Side.CLIENT)
   public void postInitClient() {
      this.forEachEnabled(Feature::postInitClient);
   }

   public void serverStarting() {
      this.forEachEnabled(Feature::serverStarting);
   }

   public boolean canBeDisabled() {
      return true;
   }

   public boolean isEnabledByDefault() {
      return true;
   }

   String makeName() {
      return this.getClass().getSimpleName().replaceAll("Quark", "").toLowerCase();
   }

   public String getModuleDescription() {
      return "";
   }

   public ItemStack getIconStack() {
      return new ItemStack(Blocks.BARRIER);
   }

   public final void forEachFeature(Consumer<Feature> consumer) {
      this.features.values().forEach(consumer);
   }

   public final void forEachEnabled(Consumer<Feature> consumer) {
      this.enabledFeatures.forEach(consumer);
   }

   public final int loadPropInt(String propName, String desc, int default_) {
      return ConfigHelper.loadPropInt(propName, this.name, desc, default_);
   }

   public final double loadPropDouble(String propName, String desc, double default_) {
      return ConfigHelper.loadPropDouble(propName, this.name, desc, default_);
   }

   public final boolean loadPropBool(String propName, String desc, boolean default_) {
      return ConfigHelper.loadPropBool(propName, this.name, desc, default_);
   }

   public final String loadPropString(String propName, String desc, String default_) {
      return ConfigHelper.loadPropString(propName, this.name, desc, default_);
   }

   public String getName() {
      return this.name;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public Map<String, ? extends IFeature> getFeatures() {
      return this.features;
   }

   public List<? extends IFeature> getEnabledFeatures() {
      return this.enabledFeatures;
   }
}
