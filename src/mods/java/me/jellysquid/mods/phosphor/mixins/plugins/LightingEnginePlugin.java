package me.jellysquid.mods.phosphor.mixins.plugins;

import java.util.List;
import java.util.Set;
import me.jellysquid.mods.phosphor.mod.PhosphorConfig;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class LightingEnginePlugin implements IMixinConfigPlugin {
   private static final Logger logger = LogManager.getLogger("Phosphor Plugin");
   public static boolean ENABLE_ILLEGAL_THREAD_ACCESS_WARNINGS = false;
   private PhosphorConfig config;
   private boolean spongePresent;

   public LightingEnginePlugin() {
   }

   public void onLoad(String mixinPackage) {
      logger.debug("Loading configuration");
      this.config = PhosphorConfig.loadConfig();
      if (!this.config.enablePhosphor) {
         logger.warn("Phosphor has been disabled through mod configuration! No patches will be applied...");
      }

      ENABLE_ILLEGAL_THREAD_ACCESS_WARNINGS = this.config.enableIllegalThreadAccessWarnings;

      try {
         Class.forName("org.spongepowered.mod.SpongeCoremod");
         this.spongePresent = true;
      } catch (Exception var3) {
         this.spongePresent = false;
      }

      if (this.spongePresent) {
         logger.info("Sponge has been detected on the classpath! Enabling Sponge specific patches...");
         logger.warn("We cannot currently detect if you are using Sponge's async lighting patch. If you have not already done so, please disable it in your configuration file for SpongeForge or you will run into issues.");
      }

   }

   public String getRefMapperConfig() {
      return Launch.blackboard.get("fml.deobfuscatedEnvironment") == Boolean.TRUE ? null : "mixins.phosphor.refmap.json";
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (!this.config.enablePhosphor) {
         return false;
      } else {
         if (this.spongePresent) {
            if (mixinClassName.endsWith("$Vanilla")) {
               logger.debug("Disabled mixin '{}' because we are in a SpongeForge environment", mixinClassName);
               return false;
            }
         } else if (mixinClassName.endsWith("$Sponge")) {
            logger.debug("Disabled patch '{}' because we are in a standard Vanilla/Forge environment", mixinClassName);
            return false;
         }

         if (targetClassName.startsWith("net.minecraft.client") && MixinEnvironment.getCurrentEnvironment().getSide() != MixinEnvironment.Side.CLIENT) {
            logger.debug("Disabled patch '{}' because it targets an client-side class unavailable in the current environment", mixinClassName);
            return false;
         } else {
            return true;
         }
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
