package mods.betterfoliage.client.config;

import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import mods.betterfoliage.client.integration.ShadersModIntegration;
import mods.octarinecore.common.config.ConfigPropertyBase;
import mods.octarinecore.common.config.ConfigPropertyBoolean;
import mods.octarinecore.common.config.ConfigPropertyDelegate;
import mods.octarinecore.common.config.ConfigPropertyDouble;
import mods.octarinecore.common.config.ConfigPropertyFloat;
import mods.octarinecore.common.config.ConfigPropertyInt;
import mods.octarinecore.common.config.ConfigPropertyIntList;
import mods.octarinecore.common.config.ConfigPropertyLong;
import mods.octarinecore.common.config.ConfigurableBlockMatcher;
import mods.octarinecore.common.config.DelegatingConfig;
import mods.octarinecore.common.config.DelegatingConfigKt;
import mods.octarinecore.common.config.ModelTextureListConfigOption;
import mods.octarinecore.common.config.ObsoleteConfigProperty;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\bÇ\u0002\u0018\u00002\u00020\u0001:\u000e\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016R+\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\n\u0010\u000b\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R+\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0014\u0010\u000b\u001a\u0004\b\u0012\u0010\u0007\"\u0004\b\u0013\u0010\t¨\u0006'"},
   d2 = {"Lmods/betterfoliage/client/config/Config;", "Lmods/octarinecore/common/config/DelegatingConfig;", "()V", "<set-?>", "", "enabled", "getEnabled", "()Z", "setEnabled", "(Z)V", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "forceReloadOptions", "", "Lmods/octarinecore/common/config/ConfigPropertyBase;", "getForceReloadOptions", "()Ljava/util/List;", "nVidia", "getNVidia", "setNVidia", "nVidia$delegate", "onChange", "", "event", "Lnet/minecraftforge/fml/client/event/ConfigChangedEvent$PostConfigChangedEvent;", "algae", "blocks", "cactus", "connectedGrass", "coral", "fallingLeaves", "leaves", "lilypad", "netherrack", "reed", "risingSoul", "roundLogs", "shaders", "shortGrass", "BetterFoliage-MC1.12"}
)
public final class Config extends DelegatingConfig {
   @NotNull
   private static final ConfigPropertyBoolean enabled$delegate = DelegatingConfigKt.boolean(true);
   @NotNull
   private static final ConfigPropertyBoolean nVidia$delegate;
   @NotNull
   private static final List<ConfigPropertyBase> forceReloadOptions;
   public static final Config INSTANCE;

   static {
      Config var0 = new Config();
      INSTANCE = var0;
      String var10000 = GL11.glGetString(7936);
      Intrinsics.checkExpressionValueIsNotNull(var10000, "GL11.glGetString(GL11.GL_VENDOR)");
      String var1 = var10000;
      boolean var2 = false;
      if (var1 == null) {
         throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
      } else {
         var10000 = var1.toLowerCase();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).toLowerCase()");
         nVidia$delegate = DelegatingConfigKt.boolean(StringsKt.contains$default((CharSequence)var10000, (CharSequence)"nvidia", false, 2, (Object)null));
         ConfigPropertyBase[] var4 = new ConfigPropertyBase[]{Config.blocks.INSTANCE.getLeavesClasses(), Config.blocks.INSTANCE.getLeavesModels(), Config.blocks.INSTANCE.getGrassClasses(), Config.blocks.INSTANCE.getGrassModels(), null};
         ConfigPropertyBase var10003 = var0.get(Config.shortGrass.INSTANCE, "saturationThreshold");
         if (var10003 == null) {
            Intrinsics.throwNpe();
         }

         var4[4] = var10003;
         forceReloadOptions = CollectionsKt.listOf(var4);
      }
   }

   public final boolean getEnabled() {
      return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
   }

   public final void setEnabled(boolean var1) {
      enabled$delegate.setValue(this, $$delegatedProperties[0], Boolean.valueOf(<set-?>));
   }

   public final boolean getNVidia() {
      return ((Boolean)nVidia$delegate.getValue(this, $$delegatedProperties[1])).booleanValue();
   }

   public final void setNVidia(boolean var1) {
      nVidia$delegate.setValue(this, $$delegatedProperties[1], Boolean.valueOf(<set-?>));
   }

   @NotNull
   public final List<ConfigPropertyBase> getForceReloadOptions() {
      return forceReloadOptions;
   }

   public void onChange(@NotNull PostConfigChangedEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      if (this.hasChanged(forceReloadOptions)) {
         Minecraft.getMinecraft().refreshResources();
      } else {
         Minecraft.getMinecraft().renderGlobal.loadRenderers();
      }

   }

   private Config() {
      super("betterfoliage", "betterfoliage");
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0014\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u001b\u0010\u0010\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\u000f\u001a\u0004\b\u0012\u0010\u0013R\u001b\u0010\u0015\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0017\u0010\u000f\u001a\u0004\b\u0016\u0010\u0013R\u001b\u0010\u0018\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\u000f\u001a\u0004\b\u0019\u0010\u0013R\u001b\u0010\u001b\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u000f\u001a\u0004\b\u001c\u0010\u001dR\u001b\u0010\u001f\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b!\u0010\u000f\u001a\u0004\b \u0010\rR\u001b\u0010\"\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b$\u0010\u000f\u001a\u0004\b#\u0010\u0013¨\u0006%"},
      d2 = {"Lmods/betterfoliage/client/config/Config$algae;", "", "()V", "biomes", "", "", "getBiomes", "()[Ljava/lang/Integer;", "biomes$delegate", "Lmods/octarinecore/common/config/ConfigPropertyIntList;", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "heightMax", "getHeightMax", "heightMax$delegate", "heightMin", "getHeightMin", "heightMin$delegate", "population", "getPopulation", "()I", "population$delegate", "shaderWind", "getShaderWind", "shaderWind$delegate", "size", "getSize", "size$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class algae {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.25D, 0.1D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.5D, 1.5D, 1.0D).lang("size");
      @NotNull
      private static final ConfigPropertyDelegate heightMin$delegate = DelegatingConfigKt.double(0.1D, 1.5D, 0.5D).lang("heightMin");
      @NotNull
      private static final ConfigPropertyDelegate heightMax$delegate = DelegatingConfigKt.double(0.1D, 1.5D, 1.0D).lang("heightMax");
      @NotNull
      private static final ConfigPropertyDelegate population$delegate = DelegatingConfigKt.int$default(0, 64, 48, 1, (Object)null).lang("population");
      @NotNull
      private static final ConfigPropertyIntList biomes$delegate = ConfigKt.biomeList(null.INSTANCE);
      @NotNull
      private static final ConfigPropertyDelegate shaderWind$delegate = DelegatingConfigKt.boolean(true).lang("shaderWind");
      public static final Config.algae INSTANCE;

      static {
         Config.algae var0 = new Config.algae();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getHeightMin() {
         return ((Number)heightMin$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getHeightMax() {
         return ((Number)heightMax$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      public final int getPopulation() {
         return ((Number)population$delegate.getValue(this, $$delegatedProperties[5])).intValue();
      }

      @NotNull
      public final Integer[] getBiomes() {
         return (Integer[])biomes$delegate.getValue(this, $$delegatedProperties[6]);
      }

      public final boolean getShaderWind() {
         return ((Boolean)shaderWind$delegate.getValue(this, $$delegatedProperties[7])).booleanValue();
      }

      private algae() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u001d\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\t\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0006R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000eR\u0011\u0010\u0017\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000eR\u0011\u0010\u0019\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0006R\u0011\u0010\u001b\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014R\u0011\u0010\u001d\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u000eR\u0011\u0010\u001f\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0006R\u0011\u0010!\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0006R\u0011\u0010#\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0014R\u0011\u0010%\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b&\u0010\u000eR\u0011\u0010'\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\u000eR\u0011\u0010)\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b*\u0010\u0006R\u0011\u0010+\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b,\u0010\u0006R\u0011\u0010-\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0006¨\u0006/"},
      d2 = {"Lmods/betterfoliage/client/config/Config$blocks;", "", "()V", "cactus", "Lmods/octarinecore/common/config/ConfigurableBlockMatcher;", "getCactus", "()Lmods/octarinecore/common/config/ConfigurableBlockMatcher;", "crops", "getCrops", "dirt", "getDirt", "grassBlacklist", "Lmods/octarinecore/common/config/ObsoleteConfigProperty;", "getGrassBlacklist", "()Lmods/octarinecore/common/config/ObsoleteConfigProperty;", "grassClasses", "getGrassClasses", "grassModels", "Lmods/octarinecore/common/config/ModelTextureListConfigOption;", "getGrassModels", "()Lmods/octarinecore/common/config/ModelTextureListConfigOption;", "grassWhitelist", "getGrassWhitelist", "leavesBlacklist", "getLeavesBlacklist", "leavesClasses", "getLeavesClasses", "leavesModels", "getLeavesModels", "leavesWhitelist", "getLeavesWhitelist", "lilypad", "getLilypad", "logClasses", "getLogClasses", "logModels", "getLogModels", "logsBlacklist", "getLogsBlacklist", "logsWhitelist", "getLogsWhitelist", "mycelium", "getMycelium", "netherrack", "getNetherrack", "sand", "getSand", "BetterFoliage-MC1.12"}
   )
   public static final class blocks {
      @NotNull
      private static final ConfigurableBlockMatcher leavesClasses = new ConfigurableBlockMatcher("betterfoliage", "leaves_blocks_default.cfg");
      @NotNull
      private static final ModelTextureListConfigOption leavesModels = new ModelTextureListConfigOption("betterfoliage", "leaves_models_default.cfg", 1);
      @NotNull
      private static final ConfigurableBlockMatcher grassClasses = new ConfigurableBlockMatcher("betterfoliage", "grass_blocks_default.cfg");
      @NotNull
      private static final ModelTextureListConfigOption grassModels = new ModelTextureListConfigOption("betterfoliage", "grass_models_default.cfg", 1);
      @NotNull
      private static final ConfigurableBlockMatcher mycelium = new ConfigurableBlockMatcher("betterfoliage", "mycelium_blocks_default.cfg");
      @NotNull
      private static final ConfigurableBlockMatcher dirt = new ConfigurableBlockMatcher("betterfoliage", "dirt_default.cfg");
      @NotNull
      private static final ConfigurableBlockMatcher crops = new ConfigurableBlockMatcher("betterfoliage", "crop_default.cfg");
      @NotNull
      private static final ConfigurableBlockMatcher logClasses = new ConfigurableBlockMatcher("betterfoliage", "log_blocks_default.cfg");
      @NotNull
      private static final ModelTextureListConfigOption logModels = new ModelTextureListConfigOption("betterfoliage", "log_models_default.cfg", 3);
      @NotNull
      private static final ConfigurableBlockMatcher sand = new ConfigurableBlockMatcher("betterfoliage", "sand_default.cfg");
      @NotNull
      private static final ConfigurableBlockMatcher lilypad = new ConfigurableBlockMatcher("betterfoliage", "lilypad_default.cfg");
      @NotNull
      private static final ConfigurableBlockMatcher cactus = new ConfigurableBlockMatcher("betterfoliage", "cactus_default.cfg");
      @NotNull
      private static final ConfigurableBlockMatcher netherrack = new ConfigurableBlockMatcher("betterfoliage", "netherrack_blocks_default.cfg");
      @NotNull
      private static final ObsoleteConfigProperty leavesWhitelist = ConfigKt.access$getOBSOLETE$p();
      @NotNull
      private static final ObsoleteConfigProperty leavesBlacklist = ConfigKt.access$getOBSOLETE$p();
      @NotNull
      private static final ObsoleteConfigProperty grassWhitelist = ConfigKt.access$getOBSOLETE$p();
      @NotNull
      private static final ObsoleteConfigProperty grassBlacklist = ConfigKt.access$getOBSOLETE$p();
      @NotNull
      private static final ObsoleteConfigProperty logsWhitelist = ConfigKt.access$getOBSOLETE$p();
      @NotNull
      private static final ObsoleteConfigProperty logsBlacklist = ConfigKt.access$getOBSOLETE$p();
      public static final Config.blocks INSTANCE;

      @NotNull
      public final ConfigurableBlockMatcher getLeavesClasses() {
         return leavesClasses;
      }

      @NotNull
      public final ModelTextureListConfigOption getLeavesModels() {
         return leavesModels;
      }

      @NotNull
      public final ConfigurableBlockMatcher getGrassClasses() {
         return grassClasses;
      }

      @NotNull
      public final ModelTextureListConfigOption getGrassModels() {
         return grassModels;
      }

      @NotNull
      public final ConfigurableBlockMatcher getMycelium() {
         return mycelium;
      }

      @NotNull
      public final ConfigurableBlockMatcher getDirt() {
         return dirt;
      }

      @NotNull
      public final ConfigurableBlockMatcher getCrops() {
         return crops;
      }

      @NotNull
      public final ConfigurableBlockMatcher getLogClasses() {
         return logClasses;
      }

      @NotNull
      public final ModelTextureListConfigOption getLogModels() {
         return logModels;
      }

      @NotNull
      public final ConfigurableBlockMatcher getSand() {
         return sand;
      }

      @NotNull
      public final ConfigurableBlockMatcher getLilypad() {
         return lilypad;
      }

      @NotNull
      public final ConfigurableBlockMatcher getCactus() {
         return cactus;
      }

      @NotNull
      public final ConfigurableBlockMatcher getNetherrack() {
         return netherrack;
      }

      @NotNull
      public final ObsoleteConfigProperty getLeavesWhitelist() {
         return leavesWhitelist;
      }

      @NotNull
      public final ObsoleteConfigProperty getLeavesBlacklist() {
         return leavesBlacklist;
      }

      @NotNull
      public final ObsoleteConfigProperty getGrassWhitelist() {
         return grassWhitelist;
      }

      @NotNull
      public final ObsoleteConfigProperty getGrassBlacklist() {
         return grassBlacklist;
      }

      @NotNull
      public final ObsoleteConfigProperty getLogsWhitelist() {
         return logsWhitelist;
      }

      @NotNull
      public final ObsoleteConfigProperty getLogsBlacklist() {
         return logsBlacklist;
      }

      private blocks() {
      }

      static {
         Config.blocks var0 = new Config.blocks();
         INSTANCE = var0;
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u000b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000e\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\b\u001a\u0004\b\u000f\u0010\fR\u001b\u0010\u0011\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0012\u0010\f¨\u0006\u0015"},
      d2 = {"Lmods/betterfoliage/client/config/Config$cactus;", "", "()V", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "size", "getSize", "size$delegate", "sizeVariation", "getSizeVariation", "sizeVariation$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDouble;", "BetterFoliage-MC1.12"}
   )
   public static final class cactus {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.5D, 1.5D, 0.8D).lang("size");
      @NotNull
      private static final ConfigPropertyDouble sizeVariation$delegate = DelegatingConfigKt.double$default(0.0D, 0.5D, 0.1D, 1, (Object)null);
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.5D, 0.1D, 1, (Object)null).lang("hOffset");
      public static final Config.cactus INSTANCE;

      static {
         Config.cactus var0 = new Config.cactus();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getSizeVariation() {
         return ((Number)sizeVariation$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      private cactus() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\b\u001a\u0004\b\n\u0010\u0006¨\u0006\f"},
      d2 = {"Lmods/betterfoliage/client/config/Config$connectedGrass;", "", "()V", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "snowEnabled", "getSnowEnabled", "snowEnabled$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class connectedGrass {
      @NotNull
      private static final ConfigPropertyBoolean enabled$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean snowEnabled$delegate = DelegatingConfigKt.boolean(false);
      public static final Config.connectedGrass INSTANCE;

      static {
         Config.connectedGrass var0 = new Config.connectedGrass();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final boolean getSnowEnabled() {
         return ((Boolean)snowEnabled$delegate.getValue(this, $$delegatedProperties[1])).booleanValue();
      }

      private connectedGrass() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0015\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\u00108FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0011\u0010\u0012R\u001b\u0010\u0015\u001a\u00020\u00168FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u0017\u0010\u0018R\u001b\u0010\u001b\u001a\u00020\u00108FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001d\u0010\u001a\u001a\u0004\b\u001c\u0010\u0012R\u001b\u0010\u001e\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b \u0010\u001a\u001a\u0004\b\u001f\u0010\fR\u001b\u0010!\u001a\u00020\u00168FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b#\u0010$\u001a\u0004\b\"\u0010\u0018R\u001b\u0010%\u001a\u00020\u00108FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b'\u0010\u001a\u001a\u0004\b&\u0010\u0012R\u001b\u0010(\u001a\u00020\u00108FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b*\u0010\u001a\u001a\u0004\b)\u0010\u0012¨\u0006+"},
      d2 = {"Lmods/betterfoliage/client/config/Config$coral;", "", "()V", "biomes", "", "", "getBiomes", "()[Ljava/lang/Integer;", "biomes$delegate", "Lmods/octarinecore/common/config/ConfigPropertyIntList;", "chance", "getChance", "()I", "chance$delegate", "Lmods/octarinecore/common/config/ConfigPropertyInt;", "crustSize", "", "getCrustSize", "()D", "crustSize$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDouble;", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "hOffset", "getHOffset", "hOffset$delegate", "population", "getPopulation", "population$delegate", "shallowWater", "getShallowWater", "shallowWater$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "size", "getSize", "size$delegate", "vOffset", "getVOffset", "vOffset$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class coral {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyBoolean shallowWater$delegate = DelegatingConfigKt.boolean(false);
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.2D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyDelegate vOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.1D, 1, (Object)null).lang("vOffset");
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.5D, 1.5D, 0.7D).lang("size");
      @NotNull
      private static final ConfigPropertyDouble crustSize$delegate = DelegatingConfigKt.double(0.5D, 1.5D, 1.4D);
      @NotNull
      private static final ConfigPropertyInt chance$delegate = DelegatingConfigKt.int$default(0, 64, 32, 1, (Object)null);
      @NotNull
      private static final ConfigPropertyDelegate population$delegate = DelegatingConfigKt.int$default(0, 64, 48, 1, (Object)null).lang("population");
      @NotNull
      private static final ConfigPropertyIntList biomes$delegate = ConfigKt.biomeList(null.INSTANCE);
      public static final Config.coral INSTANCE;

      static {
         Config.coral var0 = new Config.coral();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final boolean getShallowWater() {
         return ((Boolean)shallowWater$delegate.getValue(this, $$delegatedProperties[1])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getVOffset() {
         return ((Number)vOffset$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      public final double getCrustSize() {
         return ((Number)crustSize$delegate.getValue(this, $$delegatedProperties[5])).doubleValue();
      }

      public final int getChance() {
         return ((Number)chance$delegate.getValue(this, $$delegatedProperties[6])).intValue();
      }

      public final int getPopulation() {
         return ((Number)population$delegate.getValue(this, $$delegatedProperties[7])).intValue();
      }

      @NotNull
      public final Integer[] getBiomes() {
         return (Integer[])biomes$delegate.getValue(this, $$delegatedProperties[8]);
      }

      private coral() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u001e\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0010\u0010\fR\u001b\u0010\u0013\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0015\u0010\b\u001a\u0004\b\u0014\u0010\u0006R\u001b\u0010\u0016\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0018\u0010\u0012\u001a\u0004\b\u0017\u0010\fR\u001b\u0010\u0019\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001b\u0010\b\u001a\u0004\b\u001a\u0010\u0006R\u001b\u0010\u001c\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u000e\u001a\u0004\b\u001d\u0010\u0006R\u001b\u0010\u001f\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b!\u0010\b\u001a\u0004\b \u0010\u0006R\u001b\u0010\"\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b$\u0010\b\u001a\u0004\b#\u0010\u0006R\u001b\u0010%\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b'\u0010\b\u001a\u0004\b&\u0010\u0006¨\u0006("},
      d2 = {"Lmods/betterfoliage/client/config/Config$fallingLeaves;", "", "()V", "chance", "", "getChance", "()D", "chance$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDouble;", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "fadeOut", "getFadeOut", "fadeOut$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "lifetime", "getLifetime", "lifetime$delegate", "opacityHack", "getOpacityHack", "opacityHack$delegate", "perturb", "getPerturb", "perturb$delegate", "size", "getSize", "size$delegate", "speed", "getSpeed", "speed$delegate", "stormStrength", "getStormStrength", "stormStrength$delegate", "windStrength", "getWindStrength", "windStrength$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class fallingLeaves {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDouble speed$delegate = DelegatingConfigKt.double(0.01D, 0.15D, 0.05D);
      @NotNull
      private static final ConfigPropertyDouble windStrength$delegate = DelegatingConfigKt.double(0.1D, 2.0D, 0.5D);
      @NotNull
      private static final ConfigPropertyDouble stormStrength$delegate = DelegatingConfigKt.double(0.1D, 2.0D, 0.8D);
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.25D, 1.5D, 0.75D).lang("size");
      @NotNull
      private static final ConfigPropertyDouble chance$delegate = DelegatingConfigKt.double(0.001D, 1.0D, 0.05D);
      @NotNull
      private static final ConfigPropertyDouble perturb$delegate = DelegatingConfigKt.double(0.01D, 1.0D, 0.25D);
      @NotNull
      private static final ConfigPropertyDouble lifetime$delegate = DelegatingConfigKt.double(1.0D, 15.0D, 5.0D);
      @NotNull
      private static final ConfigPropertyBoolean opacityHack$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean fadeOut$delegate = DelegatingConfigKt.boolean(true);
      public static final Config.fallingLeaves INSTANCE;

      static {
         Config.fallingLeaves var0 = new Config.fallingLeaves();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getSpeed() {
         return ((Number)speed$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getWindStrength() {
         return ((Number)windStrength$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getStormStrength() {
         return ((Number)stormStrength$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      public final double getChance() {
         return ((Number)chance$delegate.getValue(this, $$delegatedProperties[5])).doubleValue();
      }

      public final double getPerturb() {
         return ((Number)perturb$delegate.getValue(this, $$delegatedProperties[6])).doubleValue();
      }

      public final double getLifetime() {
         return ((Number)lifetime$delegate.getValue(this, $$delegatedProperties[7])).doubleValue();
      }

      public final boolean getOpacityHack() {
         return ((Boolean)opacityHack$delegate.getValue(this, $$delegatedProperties[8])).booleanValue();
      }

      public final boolean getFadeOut() {
         return ((Boolean)fadeOut$delegate.getValue(this, $$delegatedProperties[9])).booleanValue();
      }

      private fallingLeaves() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\u0006\n\u0002\b\u0010\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\f\u001a\u0004\b\n\u0010\u0006R\u001b\u0010\r\u001a\u00020\u000e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\f\u001a\u0004\b\u000f\u0010\u0010R\u001b\u0010\u0012\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\b\u001a\u0004\b\u0013\u0010\u0006R\u001b\u0010\u0015\u001a\u00020\u000e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0017\u0010\f\u001a\u0004\b\u0016\u0010\u0010R\u001b\u0010\u0018\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\b\u001a\u0004\b\u0019\u0010\u0006R\u001b\u0010\u001b\u001a\u00020\u000e8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001d\u0010\f\u001a\u0004\b\u001c\u0010\u0010¨\u0006\u001e"},
      d2 = {"Lmods/betterfoliage/client/config/Config$leaves;", "", "()V", "dense", "", "getDense", "()Z", "dense$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "enabled", "getEnabled", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "hideInternal", "getHideInternal", "hideInternal$delegate", "size", "getSize", "size$delegate", "snowEnabled", "getSnowEnabled", "snowEnabled$delegate", "vOffset", "getVOffset", "vOffset$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class leaves {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyBoolean snowEnabled$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.2D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyDelegate vOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.1D, 1, (Object)null).lang("vOffset");
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.75D, 2.5D, 1.4D).lang("size");
      @NotNull
      private static final ConfigPropertyBoolean dense$delegate = DelegatingConfigKt.boolean(false);
      @NotNull
      private static final ConfigPropertyBoolean hideInternal$delegate = DelegatingConfigKt.boolean(true);
      public static final Config.leaves INSTANCE;

      static {
         Config.leaves var0 = new Config.leaves();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final boolean getSnowEnabled() {
         return ((Boolean)snowEnabled$delegate.getValue(this, $$delegatedProperties[1])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getVOffset() {
         return ((Number)vOffset$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      public final boolean getDense() {
         return ((Boolean)dense$delegate.getValue(this, $$delegatedProperties[5])).booleanValue();
      }

      public final boolean getHideInternal() {
         return ((Boolean)hideInternal$delegate.getValue(this, $$delegatedProperties[6])).booleanValue();
      }

      private leaves() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\u00108FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\b\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u0014"},
      d2 = {"Lmods/betterfoliage/client/config/Config$lilypad;", "", "()V", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "flowerChance", "", "getFlowerChance", "()I", "flowerChance$delegate", "Lmods/octarinecore/common/config/ConfigPropertyInt;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class lilypad {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.25D, 0.1D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyInt flowerChance$delegate;
      public static final Config.lilypad INSTANCE;

      static {
         Config.lilypad var0 = new Config.lilypad();
         INSTANCE = var0;
         byte var1 = 0;
         byte var2 = 16;
         byte var3 = 64;
         flowerChance$delegate = DelegatingConfigKt.int(var1, var3, var2);
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final int getFlowerChance() {
         return ((Number)flowerChance$delegate.getValue(this, $$delegatedProperties[2])).intValue();
      }

      private lilypad() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\r\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000e\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\b\u001a\u0004\b\u000f\u0010\fR\u001b\u0010\u0011\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\b\u001a\u0004\b\u0012\u0010\fR\u001b\u0010\u0014\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0016\u0010\b\u001a\u0004\b\u0015\u0010\f¨\u0006\u0017"},
      d2 = {"Lmods/betterfoliage/client/config/Config$netherrack;", "", "()V", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "heightMax", "getHeightMax", "heightMax$delegate", "heightMin", "getHeightMin", "heightMin$delegate", "size", "getSize", "size$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class netherrack {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.2D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyDelegate heightMin$delegate = DelegatingConfigKt.double(0.1D, 1.5D, 0.6D).lang("heightMin");
      @NotNull
      private static final ConfigPropertyDelegate heightMax$delegate = DelegatingConfigKt.double(0.1D, 1.5D, 0.8D).lang("heightMax");
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.5D, 1.5D, 1.0D).lang("size");
      public static final Config.netherrack INSTANCE;

      static {
         Config.netherrack var0 = new Config.netherrack();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getHeightMin() {
         return ((Number)heightMin$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getHeightMax() {
         return ((Number)heightMax$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      private netherrack() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0011\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R!\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u001b\u0010\u0010\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\u000f\u001a\u0004\b\u0012\u0010\u0013R\u001b\u0010\u0015\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0017\u0010\u000f\u001a\u0004\b\u0016\u0010\u0013R\u001b\u0010\u0018\u001a\u00020\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\u000f\u001a\u0004\b\u0019\u0010\u0013R\u001b\u0010\u001b\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\u000f\u001a\u0004\b\u001c\u0010\u001dR\u001b\u0010\u001f\u001a\u00020\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b!\u0010\u000f\u001a\u0004\b \u0010\r¨\u0006\""},
      d2 = {"Lmods/betterfoliage/client/config/Config$reed;", "", "()V", "biomes", "", "", "getBiomes", "()[Ljava/lang/Integer;", "biomes$delegate", "Lmods/octarinecore/common/config/ConfigPropertyIntList;", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "heightMax", "getHeightMax", "heightMax$delegate", "heightMin", "getHeightMin", "heightMin$delegate", "population", "getPopulation", "()I", "population$delegate", "shaderWind", "getShaderWind", "shaderWind$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class reed {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.2D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyDelegate heightMin$delegate = DelegatingConfigKt.double(1.5D, 3.5D, 1.7D).lang("heightMin");
      @NotNull
      private static final ConfigPropertyDelegate heightMax$delegate = DelegatingConfigKt.double(1.5D, 3.5D, 2.2D).lang("heightMax");
      @NotNull
      private static final ConfigPropertyDelegate population$delegate = DelegatingConfigKt.int$default(0, 64, 32, 1, (Object)null).lang("population");
      @NotNull
      private static final ConfigPropertyIntList biomes$delegate = ConfigKt.biomeList(null.INSTANCE);
      @NotNull
      private static final ConfigPropertyDelegate shaderWind$delegate = DelegatingConfigKt.boolean(true).lang("shaderWind");
      public static final Config.reed INSTANCE;

      static {
         Config.reed var0 = new Config.reed();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getHeightMin() {
         return ((Number)heightMin$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getHeightMax() {
         return ((Number)heightMax$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final int getPopulation() {
         return ((Number)population$delegate.getValue(this, $$delegatedProperties[4])).intValue();
      }

      @NotNull
      public final Integer[] getBiomes() {
         return (Integer[])biomes$delegate.getValue(this, $$delegatedProperties[5]);
      }

      public final boolean getShaderWind() {
         return ((Boolean)shaderWind$delegate.getValue(this, $$delegatedProperties[6])).booleanValue();
      }

      private reed() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u000b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\b\u001a\u0004\b\u0010\u0010\u0006R\u001b\u0010\u0012\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\b\u001a\u0004\b\u0013\u0010\u0006R\u001b\u0010\u0015\u001a\u00020\u00168FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u0017\u0010\u0018R\u001b\u0010\u001b\u001a\u00020\u00168FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001d\u0010\u001a\u001a\u0004\b\u001c\u0010\u0018R\u001b\u0010\u001e\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b \u0010\b\u001a\u0004\b\u001f\u0010\u0006R\u001b\u0010!\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b#\u0010\b\u001a\u0004\b\"\u0010\u0006R\u001b\u0010$\u001a\u00020%8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b(\u0010)\u001a\u0004\b&\u0010'R\u001b\u0010*\u001a\u00020%8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b,\u0010)\u001a\u0004\b+\u0010'R\u001b\u0010-\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b/\u0010\b\u001a\u0004\b.\u0010\u0006¨\u00060"},
      d2 = {"Lmods/betterfoliage/client/config/Config$risingSoul;", "", "()V", "chance", "", "getChance", "()D", "chance$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDouble;", "enabled", "", "getEnabled", "()Z", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "headSize", "getHeadSize", "headSize$delegate", "lifetime", "getLifetime", "lifetime$delegate", "opacity", "", "getOpacity", "()F", "opacity$delegate", "Lmods/octarinecore/common/config/ConfigPropertyFloat;", "opacityDecay", "getOpacityDecay", "opacityDecay$delegate", "perturb", "getPerturb", "perturb$delegate", "sizeDecay", "getSizeDecay", "sizeDecay$delegate", "trailDensity", "", "getTrailDensity", "()I", "trailDensity$delegate", "Lmods/octarinecore/common/config/ConfigPropertyInt;", "trailLength", "getTrailLength", "trailLength$delegate", "trailSize", "getTrailSize", "trailSize$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class risingSoul {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDouble chance$delegate = DelegatingConfigKt.double(0.001D, 1.0D, 0.02D);
      @NotNull
      private static final ConfigPropertyDouble perturb$delegate = DelegatingConfigKt.double(0.01D, 0.25D, 0.05D);
      @NotNull
      private static final ConfigPropertyDouble headSize$delegate = DelegatingConfigKt.double(0.25D, 1.5D, 1.0D);
      @NotNull
      private static final ConfigPropertyDouble trailSize$delegate = DelegatingConfigKt.double(0.25D, 1.5D, 0.75D);
      @NotNull
      private static final ConfigPropertyFloat opacity$delegate = DelegatingConfigKt.float(0.05D, 1.0D, 0.5D);
      @NotNull
      private static final ConfigPropertyDouble sizeDecay$delegate = DelegatingConfigKt.double(0.5D, 1.0D, 0.97D);
      @NotNull
      private static final ConfigPropertyFloat opacityDecay$delegate = DelegatingConfigKt.float(0.5D, 1.0D, 0.97D);
      @NotNull
      private static final ConfigPropertyDouble lifetime$delegate = DelegatingConfigKt.double(1.0D, 15.0D, 4.0D);
      @NotNull
      private static final ConfigPropertyInt trailLength$delegate = DelegatingConfigKt.int(2, 128, 48);
      @NotNull
      private static final ConfigPropertyInt trailDensity$delegate = DelegatingConfigKt.int(1, 16, 3);
      public static final Config.risingSoul INSTANCE;

      static {
         Config.risingSoul var0 = new Config.risingSoul();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getChance() {
         return ((Number)chance$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getPerturb() {
         return ((Number)perturb$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final double getHeadSize() {
         return ((Number)headSize$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getTrailSize() {
         return ((Number)trailSize$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      public final float getOpacity() {
         return ((Number)opacity$delegate.getValue(this, $$delegatedProperties[5])).floatValue();
      }

      public final double getSizeDecay() {
         return ((Number)sizeDecay$delegate.getValue(this, $$delegatedProperties[6])).doubleValue();
      }

      public final float getOpacityDecay() {
         return ((Number)opacityDecay$delegate.getValue(this, $$delegatedProperties[7])).floatValue();
      }

      public final double getLifetime() {
         return ((Number)lifetime$delegate.getValue(this, $$delegatedProperties[8])).doubleValue();
      }

      public final int getTrailLength() {
         return ((Number)trailLength$delegate.getValue(this, $$delegatedProperties[9])).intValue();
      }

      public final int getTrailDensity() {
         return ((Number)trailDensity$delegate.getValue(this, $$delegatedProperties[10])).intValue();
      }

      private risingSoul() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\u0007\n\u0002\b\f\n\u0002\u0010\u0006\n\u0002\b\u000b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\b\u001a\u0004\b\n\u0010\u0006R\u001b\u0010\f\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000e\u0010\b\u001a\u0004\b\r\u0010\u0006R\u001b\u0010\u000f\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\b\u001a\u0004\b\u0010\u0010\u0006R\u001b\u0010\u0012\u001a\u00020\u00138FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0014\u0010\u0015R\u001b\u0010\u0018\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u0019\u0010\u0006R\u001b\u0010\u001c\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001e\u0010\b\u001a\u0004\b\u001d\u0010\u0006R\u001b\u0010\u001f\u001a\u00020 8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b#\u0010$\u001a\u0004\b!\u0010\"R\u001b\u0010%\u001a\u00020 8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b'\u0010$\u001a\u0004\b&\u0010\"R\u001b\u0010(\u001a\u00020 8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b*\u0010$\u001a\u0004\b)\u0010\"¨\u0006+"},
      d2 = {"Lmods/betterfoliage/client/config/Config$roundLogs;", "", "()V", "connectGrass", "", "getConnectGrass", "()Z", "connectGrass$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "connectPerpendicular", "getConnectPerpendicular", "connectPerpendicular$delegate", "connectSolids", "getConnectSolids", "connectSolids$delegate", "defaultY", "getDefaultY", "defaultY$delegate", "dimming", "", "getDimming", "()F", "dimming$delegate", "Lmods/octarinecore/common/config/ConfigPropertyFloat;", "enabled", "getEnabled", "enabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "lenientConnect", "getLenientConnect", "lenientConnect$delegate", "radiusLarge", "", "getRadiusLarge", "()D", "radiusLarge$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDouble;", "radiusSmall", "getRadiusSmall", "radiusSmall$delegate", "zProtection", "getZProtection", "zProtection$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class roundLogs {
      @NotNull
      private static final ConfigPropertyDelegate enabled$delegate = ConfigKt.access$featureEnable();
      @NotNull
      private static final ConfigPropertyDouble radiusSmall$delegate = DelegatingConfigKt.double$default(0.0D, 0.5D, 0.25D, 1, (Object)null);
      @NotNull
      private static final ConfigPropertyDouble radiusLarge$delegate = DelegatingConfigKt.double$default(0.0D, 0.5D, 0.44D, 1, (Object)null);
      @NotNull
      private static final ConfigPropertyFloat dimming$delegate = DelegatingConfigKt.float$default(0.0D, 0.0D, 0.7D, 3, (Object)null);
      @NotNull
      private static final ConfigPropertyBoolean connectSolids$delegate = DelegatingConfigKt.boolean(false);
      @NotNull
      private static final ConfigPropertyBoolean lenientConnect$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean connectPerpendicular$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean connectGrass$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean defaultY$delegate = DelegatingConfigKt.boolean(false);
      @NotNull
      private static final ConfigPropertyDouble zProtection$delegate = DelegatingConfigKt.double$default(0.9D, 0.0D, 0.99D, 2, (Object)null);
      public static final Config.roundLogs INSTANCE;

      static {
         Config.roundLogs var0 = new Config.roundLogs();
         INSTANCE = var0;
      }

      public final boolean getEnabled() {
         return ((Boolean)enabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final double getRadiusSmall() {
         return ((Number)radiusSmall$delegate.getValue(this, $$delegatedProperties[1])).doubleValue();
      }

      public final double getRadiusLarge() {
         return ((Number)radiusLarge$delegate.getValue(this, $$delegatedProperties[2])).doubleValue();
      }

      public final float getDimming() {
         return ((Number)dimming$delegate.getValue(this, $$delegatedProperties[3])).floatValue();
      }

      public final boolean getConnectSolids() {
         return ((Boolean)connectSolids$delegate.getValue(this, $$delegatedProperties[4])).booleanValue();
      }

      public final boolean getLenientConnect() {
         return ((Boolean)lenientConnect$delegate.getValue(this, $$delegatedProperties[5])).booleanValue();
      }

      public final boolean getConnectPerpendicular() {
         return ((Boolean)connectPerpendicular$delegate.getValue(this, $$delegatedProperties[6])).booleanValue();
      }

      public final boolean getConnectGrass() {
         return ((Boolean)connectGrass$delegate.getValue(this, $$delegatedProperties[7])).booleanValue();
      }

      public final boolean getDefaultY() {
         return ((Boolean)defaultY$delegate.getValue(this, $$delegatedProperties[8])).booleanValue();
      }

      public final double getZProtection() {
         return ((Number)zProtection$delegate.getValue(this, $$delegatedProperties[9])).doubleValue();
      }

      private roundLogs() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\b\u001a\u0004\b\n\u0010\u0006¨\u0006\f"},
      d2 = {"Lmods/betterfoliage/client/config/Config$shaders;", "", "()V", "grassId", "", "getGrassId", "()J", "grassId$delegate", "Lmods/octarinecore/common/config/ConfigPropertyLong;", "leavesId", "getLeavesId", "leavesId$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class shaders {
      @NotNull
      private static final ConfigPropertyLong leavesId$delegate = DelegatingConfigKt.long(1, 65535, (int)ShadersModIntegration.INSTANCE.getLeavesDefaultBlockId());
      @NotNull
      private static final ConfigPropertyLong grassId$delegate = DelegatingConfigKt.long(1, 65535, (int)ShadersModIntegration.INSTANCE.getGrassDefaultBlockId());
      public static final Config.shaders INSTANCE;

      static {
         Config.shaders var0 = new Config.shaders();
         INSTANCE = var0;
      }

      public final long getLeavesId() {
         return ((Number)leavesId$delegate.getValue(this, $$delegatedProperties[0])).longValue();
      }

      public final long getGrassId() {
         return ((Number)grassId$delegate.getValue(this, $$delegatedProperties[1])).longValue();
      }

      private shaders() {
      }
   }

   @Metadata(
      mv = {1, 1, 15},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0014\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\u000e\u001a\u0004\b\u0010\u0010\fR\u001b\u0010\u0012\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\u000e\u001a\u0004\b\u0013\u0010\fR\u001b\u0010\u0015\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0017\u0010\b\u001a\u0004\b\u0016\u0010\u0006R\u001b\u0010\u0018\u001a\u00020\u00198FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001c\u0010\u000e\u001a\u0004\b\u001a\u0010\u001bR\u001b\u0010\u001d\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001f\u0010 \u001a\u0004\b\u001e\u0010\fR\u001b\u0010!\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b#\u0010\u000e\u001a\u0004\b\"\u0010\u0006R\u001b\u0010$\u001a\u00020\n8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b&\u0010\u000e\u001a\u0004\b%\u0010\fR\u001b\u0010'\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b)\u0010\b\u001a\u0004\b(\u0010\u0006R\u001b\u0010*\u001a\u00020\u00048FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b,\u0010\b\u001a\u0004\b+\u0010\u0006¨\u0006-"},
      d2 = {"Lmods/betterfoliage/client/config/Config$shortGrass;", "", "()V", "grassEnabled", "", "getGrassEnabled", "()Z", "grassEnabled$delegate", "Lmods/octarinecore/common/config/ConfigPropertyBoolean;", "hOffset", "", "getHOffset", "()D", "hOffset$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDelegate;", "heightMax", "getHeightMax", "heightMax$delegate", "heightMin", "getHeightMin", "heightMin$delegate", "myceliumEnabled", "getMyceliumEnabled", "myceliumEnabled$delegate", "population", "", "getPopulation", "()I", "population$delegate", "saturationThreshold", "getSaturationThreshold", "saturationThreshold$delegate", "Lmods/octarinecore/common/config/ConfigPropertyDouble;", "shaderWind", "getShaderWind", "shaderWind$delegate", "size", "getSize", "size$delegate", "snowEnabled", "getSnowEnabled", "snowEnabled$delegate", "useGenerated", "getUseGenerated", "useGenerated$delegate", "BetterFoliage-MC1.12"}
   )
   public static final class shortGrass {
      @NotNull
      private static final ConfigPropertyBoolean grassEnabled$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean myceliumEnabled$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyBoolean snowEnabled$delegate = DelegatingConfigKt.boolean(true);
      @NotNull
      private static final ConfigPropertyDelegate hOffset$delegate = DelegatingConfigKt.double$default(0.0D, 0.4D, 0.2D, 1, (Object)null).lang("hOffset");
      @NotNull
      private static final ConfigPropertyDelegate heightMin$delegate = DelegatingConfigKt.double(0.1D, 2.5D, 0.6D).lang("heightMin");
      @NotNull
      private static final ConfigPropertyDelegate heightMax$delegate = DelegatingConfigKt.double(0.1D, 2.5D, 0.8D).lang("heightMax");
      @NotNull
      private static final ConfigPropertyDelegate size$delegate = DelegatingConfigKt.double(0.5D, 1.5D, 1.0D).lang("size");
      @NotNull
      private static final ConfigPropertyDelegate population$delegate = DelegatingConfigKt.int$default(0, 64, 64, 1, (Object)null).lang("population");
      @NotNull
      private static final ConfigPropertyBoolean useGenerated$delegate = DelegatingConfigKt.boolean(false);
      @NotNull
      private static final ConfigPropertyDelegate shaderWind$delegate = DelegatingConfigKt.boolean(true).lang("shaderWind");
      @NotNull
      private static final ConfigPropertyDouble saturationThreshold$delegate = DelegatingConfigKt.double$default(0.0D, 0.0D, 0.1D, 3, (Object)null);
      public static final Config.shortGrass INSTANCE;

      static {
         Config.shortGrass var0 = new Config.shortGrass();
         INSTANCE = var0;
      }

      public final boolean getGrassEnabled() {
         return ((Boolean)grassEnabled$delegate.getValue(this, $$delegatedProperties[0])).booleanValue();
      }

      public final boolean getMyceliumEnabled() {
         return ((Boolean)myceliumEnabled$delegate.getValue(this, $$delegatedProperties[1])).booleanValue();
      }

      public final boolean getSnowEnabled() {
         return ((Boolean)snowEnabled$delegate.getValue(this, $$delegatedProperties[2])).booleanValue();
      }

      public final double getHOffset() {
         return ((Number)hOffset$delegate.getValue(this, $$delegatedProperties[3])).doubleValue();
      }

      public final double getHeightMin() {
         return ((Number)heightMin$delegate.getValue(this, $$delegatedProperties[4])).doubleValue();
      }

      public final double getHeightMax() {
         return ((Number)heightMax$delegate.getValue(this, $$delegatedProperties[5])).doubleValue();
      }

      public final double getSize() {
         return ((Number)size$delegate.getValue(this, $$delegatedProperties[6])).doubleValue();
      }

      public final int getPopulation() {
         return ((Number)population$delegate.getValue(this, $$delegatedProperties[7])).intValue();
      }

      public final boolean getUseGenerated() {
         return ((Boolean)useGenerated$delegate.getValue(this, $$delegatedProperties[8])).booleanValue();
      }

      public final boolean getShaderWind() {
         return ((Boolean)shaderWind$delegate.getValue(this, $$delegatedProperties[9])).booleanValue();
      }

      public final double getSaturationThreshold() {
         return ((Number)saturationThreshold$delegate.getValue(this, $$delegatedProperties[10])).doubleValue();
      }

      private shortGrass() {
      }
   }
}
