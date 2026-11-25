package pl.asie.foamfix.shared;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.Set;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import pl.asie.foamfix.FoamFix;

public class FoamFixConfig {
   public boolean lwWeakenResourceCache;
   public boolean lwRemovePackageManifestMap;
   public boolean geDeduplicate;
   public boolean clWipeModelCache;
   public boolean clCleanRedundantModelRegistry;
   public boolean clDynamicItemModels;
   public boolean clCheapMinimumLighter;
   public boolean clInitOptions;
   public boolean clModelLoaderCleanup;
   public boolean clDisableTextureAnimations;
   public boolean geBlacklistLibraryTransformers;
   public boolean geBlockPosPatch;
   public boolean geFasterEntityLookup;
   public boolean geFasterPropertyComparisons;
   public boolean geFasterAirLookup;
   public boolean geFasterEntityDataManager;
   public boolean twDisableRedstoneLight;
   public boolean geSmallPropertyStorage;
   public boolean gePatchChunkSerialization;
   public boolean twImmediateLightingUpdates;
   public boolean gbPatchBeds;
   public boolean geFasterHopper;
   public boolean geFixWorldEntityCleanup;
   public boolean clDeduplicateModels;
   public boolean clDeduplicateIModels;
   public boolean gbNotifyNonUnloadedWorlds;
   public boolean gbForgeGCNonUnloaded;
   public int gbWorldUnloadTime;
   public boolean dbgCountModels;
   public boolean expUnpackBakedQuads;
   public boolean txEnable;
   public boolean gbEnableWrapper;
   public boolean gbWrapperCountNotifyBlock;
   public boolean clClearCachesOnUnload;
   public boolean clModelResourceLocationFastConstruct;
   public int txFasterAnimation;
   public int txMaxAnimationMipLevel;
   public int txCacheAnimationMaxFrames;
   public int geMobSpawnerCheckSpeed;
   public int clDeduplicateStepEvery;
   public boolean clSmallModelConditions;
   public boolean clOpenUrlLinux;
   public boolean neMicroOptimizations;
   public boolean gbPatchFluids;
   public boolean gbPatchFarmland;
   public boolean gbPatchBopGrass;
   public String[] gbCustomRules;
   public boolean clJeiCreativeSearch;
   public int refreshTimes = 0;
   public int clDeduplicateRecursionLevel;
   private final Set<Property> applicableProperties = Sets.newIdentityHashSet();
   private Configuration config;
   private boolean isCoremod;
   public boolean isDeobfEnvironment;
   public boolean geSmallLightingOptimize = false;
   public boolean resourceDirty;

   public FoamFixConfig() {
   }

   private boolean getBoolean(String name, String category, boolean defaultValue, String description, boolean requiresRestart, boolean showInGui) {
      Property prop = this.config.get(category, name, defaultValue);
      prop.setDefaultValue(defaultValue);
      prop.setComment(description + " [default: " + defaultValue + "]");
      prop.setRequiresMcRestart(requiresRestart);
      prop.setShowInGui(showInGui);
      prop.setLanguageKey("foamfix.config." + name);
      this.applicableProperties.add(prop);
      return prop.getBoolean(defaultValue);
   }

   private int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment, boolean requiresRestart, boolean showInGui) {
      Property prop = this.config.get(category, name, defaultValue);
      prop.setMinValue(minValue);
      prop.setMaxValue(maxValue);
      prop.setComment(comment + " [default: " + defaultValue + ", range: " + minValue + "-" + maxValue + "]");
      prop.setRequiresMcRestart(requiresRestart);
      prop.setShowInGui(showInGui);
      prop.setLanguageKey("foamfix.config." + name);
      this.applicableProperties.add(prop);
      return prop.getInt(defaultValue);
   }

   private double getDouble(String name, String category, double defaultValue, double minValue, double maxValue, String comment, boolean requiresRestart, boolean showInGui) {
      Property prop = this.config.get(category, name, defaultValue);
      prop.setMinValue(minValue);
      prop.setMaxValue(maxValue);
      prop.setComment(comment + " [default: " + defaultValue + ", range: " + minValue + "-" + maxValue + "]");
      prop.setRequiresMcRestart(requiresRestart);
      prop.setShowInGui(showInGui);
      prop.setLanguageKey("foamfix.config." + name);
      this.applicableProperties.add(prop);
      return prop.getDouble(defaultValue);
   }

   private boolean getBoolean(String name, String category, boolean defaultValue, String description, boolean requiresRestart, boolean showInGui, String forgeVersionRange) {
      VersionRange range = VersionParser.parseRange(forgeVersionRange);
      DefaultArtifactVersion requiredVersion = new DefaultArtifactVersion("Forge", range);
      return requiredVersion.containsVersion(new DefaultArtifactVersion("Forge", ForgeVersion.getVersion())) ? this.getBoolean(name, category, defaultValue, description, requiresRestart, showInGui) : false;
   }

   private String[] getStringList(String name, String category, String description, boolean requiresRestart, boolean showInGui) {
      Property prop = this.config.get(category, name, new String[0]);
      prop.setDefaultValues(new String[0]);
      prop.setComment(description);
      prop.setRequiresMcRestart(requiresRestart);
      prop.setShowInGui(showInGui);
      prop.setLanguageKey("foamfix.config." + name);
      this.applicableProperties.add(prop);
      return prop.getStringList();
   }

   public void reload() {
      ++this.refreshTimes;
      boolean oldGeDeduplicate = this.geDeduplicate;
      int oldClDeduplicateRecursionLevel = this.clDeduplicateRecursionLevel;
      boolean oldExpUnpackBakedQuads = this.expUnpackBakedQuads;
      boolean oldClDeduplicateIModels = this.clDeduplicateIModels;
      boolean oldClJeiCreativeSearch = this.clJeiCreativeSearch;
      this.dbgCountModels = this.getBoolean("countListBakedModels", "debug", false, "Should FoamFix count and list baked models during deduplication, per mod?", false, false);
      this.lwWeakenResourceCache = this.getBoolean("weakenResourceCache", "launchwrapper", true, "Weaken LaunchWrapper's byte[] resource cache to make it cleanuppable by the GC. Safe.", true, true);
      this.lwRemovePackageManifestMap = this.getBoolean("removePackageManifestMap", "launchwrapper", true, "Remove Launchwrapper package manifest map (which is not used anyway).", true, true);
      this.geDeduplicate = this.getBoolean("deduplicate", "general", true, "Enable deduplication of redundant objects in memory.", false, true);
      this.clDeduplicateRecursionLevel = this.getInt("deduplicateModelsMaxRecursion", "client", 9, 1, Integer.MAX_VALUE, "The maximum amount of levels of recursion for the deduplication process. Smaller values will deduplicate less data, but make the process run faster.", false, true);
      this.clDeduplicateModels = this.getBoolean("deduplicateModels", "client", true, "Deduplicates baked models. The original feature.", false, true);
      this.clDeduplicateIModels = this.getBoolean("deduplicateModelBakers", "client", true, "Deduplicates IModels too. Takes a few seconds more, but shaves off another bit of RAM.", false, true);
      this.clCleanRedundantModelRegistry = this.getBoolean("clearDuplicateModelRegistry", "client", true, "Clears the baked models generated in the first pass *before* entering the second pass, instead of *after*. While this doesn't reduce memory usage in-game, it does reduce it noticeably during loading.", true, true);
      this.expUnpackBakedQuads = this.getBoolean("unpackBakedQuads", "experimental", false, "Unpacks all baked quads. Increases RAM usage, but might speed some things up.", false, true);
      this.gbEnableWrapper = this.getBoolean("enableDebuggingWrapper", "ghostbuster", false, "Wrap ChunkProviderServers to be able to provide the /ghostbuster command for debugging ghost chunkloads.", true, true);
      this.gbWrapperCountNotifyBlock = this.getBoolean("wrapperShowsNeighborUpdates", "ghostbuster", false, "Should the /ghostbuster debugger show neighbor updates?", false, true);
      this.clClearCachesOnUnload = this.getBoolean("clearCachesOnWorldUnload", "client", true, "Clears caches on world unload a bit faster than usual. Prevents temporary memory leaks. More effective in Anarchy.", true, true);
      this.clDeduplicateStepEvery = this.getInt("deduplicateModelsStepEvery", "client", 1, 1, 10000, "Step every X models on the progress bar while deduplicating baked models.", false, false);
      this.gbForgeGCNonUnloaded = this.getBoolean("nonUnloadedWorldsForceGCOnCheck", "ghostbuster", false, "For FoamFix debugging/development purposes only.", false, false);
      this.gbNotifyNonUnloadedWorlds = this.getBoolean("checkNonUnloadedWorldClients", "ghostbuster", true, "Checks if worlds do not unload after a specified amount of time, and notifies the user if that is the case.", true, true);
      this.gbWorldUnloadTime = this.getInt("checkNonUnloadedWorldTimeout", "ghostbuster", 60, 10, 3600, "The amount of time FoamFix should wait for a world to be deemed non-unloaded.", true, true);
      if (this.isCoremod && this.getBoolean("forceDisable", "coremod", false, "Disables all coremod functionality.", true, true)) {
         this.isCoremod = false;
      }

      if (this.isCoremod) {
         int oldTxFasterAnimation = this.txFasterAnimation;
         int oldTxCacheAnimationMaxFrames = this.txCacheAnimationMaxFrames;
         int oldTxMaxAnimationMipLevel = this.txMaxAnimationMipLevel;
         boolean oldClWipeModelCache = this.clWipeModelCache;
         this.geMobSpawnerCheckSpeed = this.getInt("mobSpawnerCheckSpeed", "coremod", 10, 1, 200, "The amount of ticks per player presence check for mob spawners. Set to 1 to disable the patch and match vanilla behaviour.", true, true);
         this.gbPatchFluids = this.getBoolean("patchFluids", "ghostbuster", true, "Should fluids be prevented from ghost chunkloading?", true, true);
         this.gbPatchFarmland = this.getBoolean("patchFarmland", "ghostbuster", true, "Should farmland be prevented from ghost chunkloading?", true, true);
         this.gbPatchBopGrass = this.getBoolean("patchBopGrass", "ghostbuster", true, "Should BoP grass be prevented from ghost chunkloading?", true, true);
         this.gbPatchBeds = this.getBoolean("patchBeds", "ghostbuster", true, "Should beds be prevented from ghost chunkloading?", true, true);
         this.gbCustomRules = this.getStringList("customPatchRulesRadius", "ghostbuster", "Custom patch rules. Format: 'className;methodName;accessAloadPos;posAloadPos;radius'. An AloadPos is the position of the argument in the method - 1 for the first one, 2 for the second one, ...; the radius determines how many blocks have to be around the method for no early return. Untested - please use with care.", true, false);
         this.gePatchChunkSerialization = this.getBoolean("patchChunkSerialization", "coremod", true, "Fix a bug in chunk serialization leading to crashes. Originally found and fixed by Aaron1011 of Sponge.", true, true);
         this.clJeiCreativeSearch = this.getBoolean("jeiCreativeSearch", "client", true, "Makes vanilla creative tab search use JEI's lookups - saves a lot of RAM *and* gives you fancy JEI features!", true, true);
         this.clWipeModelCache = this.getBoolean("wipeModelCache", "client", true, "Wipes the IModel cache after baking is finished. Saves a lot of RAM, as most IModels will not be reloaded.", false, true);
         this.txEnable = this.getBoolean("enable", "textures", true, "If false, disables any patches from this category.", true, true);
         this.txFasterAnimation = this.getInt("fasterAnimation", "textures", 1, 0, 2, "Controls the faster animation path. 0 - disable, 2 - force enable, 1 (default) - enable on devices which have been shown to benefit from it.", false, true);
         this.txCacheAnimationMaxFrames = this.getInt("maxAnimationFramesForCache", "textures", 256, 0, Integer.MAX_VALUE, "The maximum amount of frames an animation can have for it to be cached. If you have a lot of VRAM, set higher.", false, true);
         this.txMaxAnimationMipLevel = this.getInt("maxAnimationMipLevel", "textures", -1, -1, 4, "Set to a number to disable animation updates past that mip level. -1 means update all. Higher numbers update more levels. To disable animation updates altogether, use the option for it.", false, true);
         if (this.refreshTimes > 1) {
            if (oldClWipeModelCache != this.clWipeModelCache || oldTxFasterAnimation != this.txFasterAnimation || oldTxCacheAnimationMaxFrames != this.txCacheAnimationMaxFrames || oldTxMaxAnimationMipLevel != this.txMaxAnimationMipLevel) {
               this.resourceDirty = true;
            }

            if (oldTxFasterAnimation != this.txFasterAnimation) {
               FoamFix.proxy.updateFasterAnimationFlag();
            }
         }

         this.clDisableTextureAnimations = this.getBoolean("disableTextureAnimations", "client", false, "Disables texture animations.", false, true);
         this.clInitOptions = this.getBoolean("initOptions", "client", true, "Initialize the options.txt and forge.cfg files with rendering performance-friendly defaults if not present.", true, false);
         this.clCheapMinimumLighter = false;
         this.twImmediateLightingUpdates = this.getBoolean("immediateLightingUpdates", "tweaks", false, "Do not delay lighting updates over other types of updates.", true, true);
         this.geBlacklistLibraryTransformers = this.getBoolean("blacklistLibraryTransformers", "coremod", true, "Stops certain non-Minecraft-related libraries from being ASM transformed. You shouldn't be transforming those anyway.", true, true);
         this.geSmallPropertyStorage = this.getBoolean("smallPropertyStorage", "coremod", true, "Replaces the default BlockState/ExtendedBlockState implementations with a far more memory-efficient variant.", true, true);
         this.geBlockPosPatch = this.getBoolean("optimizedBlockPos", "coremod", true, "Optimizes BlockPos mutable/immutable getters to run on the same variables, letting them be inlined and thus theoretically increasing performance.", true, true);
         this.clDynamicItemModels = this.getBoolean("dynamicItemModels", "coremod", true, "Make 3D forms of items be rendered dynamically and cached when necessary.", true, true);
         if (!this.isDeobfEnvironment) {
            this.geFasterEntityLookup = this.getBoolean("fasterEntityLookup", "coremod", true, "Speeds up entity lookup by optimizing ClassInheritanceMultiMap.getByClass.", true, true);
         }

         this.geFasterAirLookup = this.getBoolean("fasterAirItemLookup", "coremod", true, "Optimizes ItemStack.isEmpty by removing a map lookup. Initially found by Aikar (Paper/0181).", true, true);
         this.geFasterPropertyComparisons = this.getBoolean("fasterPropertyComparisons", "coremod", true, "Optimizes blockstate property equals and hashCode methods.", true, true);
         this.geFasterEntityDataManager = this.getBoolean("fasterEntityDataManager", "experimental", false, "Optimizes the backing map for EntityDataManager, saving memory *and* CPU time! May cause issues, however - please test and report back!", true, true);
         this.geFasterHopper = this.getBoolean("fasterHopper", "coremod", true, "Speeds up the hopper's calculations.", true, true);
         this.geFixWorldEntityCleanup = this.getBoolean("fixWorldEntityCleanup", "coremod", true, "Fixes the server not removing unloaded entities/tile entities if no chunkloaders are active. Thanks to CreativeMD for finding this!", true, true);
         this.clModelLoaderCleanup = this.getBoolean("modelLoaderCleanup", "client", true, "Remove unnecessary data from a pointlessly cached ModelLoader instance.", true, true);
         this.clModelResourceLocationFastConstruct = this.getBoolean("modelResourceLocationFastConstruct", "client", true, "Faster/less memory-churning construction mechanism for ModelResourceLocation objects. Identified by malte0811 in FerriteCore - implemented in a distinct way.", true, true);
         this.clSmallModelConditions = this.getBoolean("smallModelConditionsV2", "client", true, "More optimized multipart model condition storage. Inspired by malte0811's research. Now not broken!", true, true);
         this.clOpenUrlLinux = this.getBoolean("openUrlLinux", "client", true, "Fix opening URLs on Linux-based installs.", true, true);
         FoamFixShared.neDeflaterCompression = this.getInt("packetCompressionLevel", "network", -1, -1, 9, "Set the packet compression level, from 0 to 9. -1 preserves the default settings.", true, true);
         this.neMicroOptimizations = this.getBoolean("microOptimizations", "network", true, "Enable micro-optimizations to reduce network churn, inspired by the Velocity project and Krypton mod.", true, true);
      }

      if (this.refreshTimes > 1 && (oldClJeiCreativeSearch != this.clJeiCreativeSearch && oldClDeduplicateIModels != this.clDeduplicateIModels || oldGeDeduplicate != this.geDeduplicate || oldClDeduplicateRecursionLevel != this.clDeduplicateRecursionLevel || oldExpUnpackBakedQuads != this.expUnpackBakedQuads)) {
         this.resourceDirty = true;
      }

      this.twDisableRedstoneLight = this.getBoolean("disableRedstoneLight", "tweaks", false, "Prevent redstone from causing light updates by removing its light level.", true, true);
      FoamFixShared.isCoremod = this.isCoremod;
      this.config.save();
   }

   public void init(File file, boolean isCoremod) {
      if (this.config == null) {
         this.config = new Configuration(file);
         this.isCoremod = isCoremod;
         this.isDeobfEnvironment = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
         this.reload();
      }

   }

   public Configuration getConfig() {
      return this.config;
   }

   public boolean isApplicable(Property property) {
      return this.applicableProperties.contains(property);
   }

   public Property getProperty(String name, String category) {
      ConfigCategory cat = this.config.getCategory(category);
      return cat != null && cat.containsKey(name) ? cat.get(name) : null;
   }
}
