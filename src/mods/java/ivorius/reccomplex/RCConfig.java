package ivorius.reccomplex;

import com.google.common.primitives.Floats;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import ivorius.reccomplex.utils.RawResourceLocation;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BiomeExpression;
import ivorius.reccomplex.utils.expression.CommandExpression;
import ivorius.reccomplex.utils.expression.DimensionExpression;
import ivorius.reccomplex.utils.expression.ResourceExpression;
import ivorius.reccomplex.world.gen.feature.decoration.RCBiomeDecorator;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.StructureSaveHandler;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import ivorius.reccomplex.world.storage.loot.GenericItemCollectionRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.tuple.Pair;

public class RCConfig {
   public static final String CATEGORY_VISUAL = "visual";
   public static final String CATEGORY_BALANCING = "balancing";
   public static final String CATEGORY_DECORATION = "decoration";
   public static final String CATEGORY_CONTROLS = "controls";
   public static final String OPTION_SPAWN_TWEAKS = "spawnTweaks";
   public static Pair<String, Float> customArtifactTag = Pair.of("", Float.valueOf(0.0F));
   public static Pair<String, Float> customBookTag = Pair.of("", Float.valueOf(0.0F));
   public static boolean hideRedundantNegativeSpace;
   public static float minDistToSpawnForGeneration;
   public static final TObjectFloatMap<String> spawnTweaks = new TObjectFloatHashMap(16, 0.75F, 1.0F);
   public static float structureSpawnChanceModifier = 1.0F;
   public static boolean avoidOverlappingGeneration;
   public static boolean honorStructureGenerationOption;
   public static boolean generateNature;
   public static boolean decorationHacks;
   public static final TObjectDoubleMap<RCBiomeDecorator.DecorationType> baseDecorationWeights = new TObjectDoubleHashMap();
   public static int baseVillageSpawnWeight;
   public static double saplingTriggerChance;
   public static double baseSaplingSpawnWeight;
   public static String commandPrefix;
   private static final Map<String, CommandExpression> commandMatchers = new HashMap();
   public static int asCommandPermissionLevel;
   public static boolean savePlayerCache;
   public static boolean notifyAdminOnBlockCommands;
   public static boolean postWorldStatus;
   public static boolean memorizeDecoration;
   public static boolean memorizeSaplings;
   public static int[] blockSelectorModifierKeys;
   private static boolean lightweightMode;
   private static ResourceExpression structureLoadMatcher;
   private static ResourceExpression structureGenerationMatcher;
   private static ResourceExpression inventoryGeneratorLoadMatcher;
   private static ResourceExpression inventoryGeneratorGenerationMatcher;
   private static BiomeExpression universalBiomeExpression = new BiomeExpression();
   private static DimensionExpression universalDimensionExpression = new DimensionExpression();
   private static ResourceExpression failingStructureLogExpression = new ResourceExpression((s) -> {
      return true;
   });
   private static final List<String> universalTransformerPresets = new ArrayList();
   private static TransformerMulti universalTransformer;
   public static float mazePlacementReversesPerRoom;
   public static long mazeTimeout;
   public static final Map<String, Boolean> globalToggles = new HashMap();

   public RCConfig() {
   }

   public static void loadConfig(String configID) {
      Configuration config = RecurrentComplex.config;
      if (configID == null || configID.equals("general")) {
         lightweightMode = config.getBoolean("lightweightMode", "general", false, "Enabling this will make the mod register as little as possible, which enables it to be used server-side only. Note that this prevents you from editing or importing structures.");
         commandPrefix = config.getString("commandPrefix", "general", "#", "The String that will be prefixed to every command, e.g. '#' -> '/#gen', '#paste' etc.");
         commandMatchers.clear();
         String[] var10000 = config.getStringList("commandMatchers", "general", new String[0], "List of Command Expressions determining if a command can be executed. Example: #export:#3 | $Ivorforce");
         Function var10002 = Function.identity();
         Function var10004 = (expression) -> {
            return (CommandExpression)ExpressionCache.of(new CommandExpression(), expression);
         };
         Map var10005 = commandMatchers;
         commandMatchers.getClass();
         ConfigUtil.parseMap(var10000, (String)null, var10002, "command matcher", var10004, var10005::put);
         asCommandPermissionLevel = config.getInt("asCommandPermissionLevel", "general", 4, -1, 10, "The required permission level for /#as to function. Set to 2 for command blocks and OPs, 4 for only server, or -1 to disable. Note that this could be a security problem on low levels.");
         savePlayerCache = config.getBoolean("savePlayerCache", "general", true, "Whether player caches like the clipboard and previewed operations will be saved and loaded.");
         notifyAdminOnBlockCommands = config.getBoolean("notifyAdminOnBlockCommands", "general", false, "Disabling this will prevent spawn command blocks from notifying the server admins, as normal commands would.");
         postWorldStatus = config.getBoolean("postWorldStatus", "general", true, "Once per world, post the status of ReC to the admins.");
         memorizeDecoration = config.getBoolean("memorizeDecoration", "general", false, "Memorize decoration spawns like trees or mushrooms (for /#whatisthis). Since decoration is so common, it is recommended to use this only for debugging / balancing purposes.");
         memorizeSaplings = config.getBoolean("memorizeSaplings", "general", false, "Memorize sapling spawns (for /#whatisthis). Since saplings are so common, it is recommended to use this only for debugging / balancing purposes.");
      }

      if (configID == null || configID.equals("balancing")) {
         avoidOverlappingGeneration = config.getBoolean("avoidOverlappingGeneration", "balancing", true, "Enabling this will cancel any structure generation if another structure is present at the cooridnate already.");
         honorStructureGenerationOption = config.getBoolean("honorStructureGenerationOption", "balancing", true, "If disabled, Recurrent Complex will generate structures in worlds without the structure generation option.");
         generateNature = config.getBoolean("generateNature", "balancing", true, "Whether the nature (e.g. trees, mushrooms) added by the mod should be actively generating.");
         decorationHacks = config.getBoolean("decorationHacks", "balancing", true, "Enable hacks for a few decoration types. Disabling this may fix problems but will deactivate ReC overriding those types of structures.");
         minDistToSpawnForGeneration = config.getFloat("minDistToSpawnForGeneration", "balancing", 30.0F, 0.0F, 500.0F, "Within this block radius, default structures won't spawn (in the main dimension).");
         structureSpawnChanceModifier = config.getFloat("structureSpawnChance", "balancing", 1.0F, 0.0F, 10.0F, "How often do structures spawn?");
         spawnTweaks.clear();
         String[] var6 = config.getStringList("spawnTweaks", "balancing", new String[0], "List of spawn chance tweaks to structures: IceThorn:0.5");
         Function var8 = Function.identity();
         Function var10 = Floats::tryParse;
         TObjectFloatMap var12 = spawnTweaks;
         spawnTweaks.getClass();
         ConfigUtil.parseMap(var6, (String)null, var8, "spawn tweak float", var10, var12::put);
         structureLoadMatcher.setExpression(config.getString("structureLoadMatcher", "balancing", "", "Resource Expression that will be applied to each loading structure, determining if it should be loaded."));
         ConfigUtil.logExpressionException(structureLoadMatcher, "structureLoadMatcher", RecurrentComplex.logger);
         structureGenerationMatcher.setExpression(config.getString("structureGenerationMatcher", "balancing", "", "Resource Expression that will be applied to each loading structure, determining if it should be set to 'active'."));
         ConfigUtil.logExpressionException(structureGenerationMatcher, "structureGenerationMatcher", RecurrentComplex.logger);
         inventoryGeneratorLoadMatcher.setExpression(config.getString("inventoryGeneratorLoadMatcher", "balancing", "", "Resource Expression that will be applied to each loading loot table, determining if it should be loaded."));
         ConfigUtil.logExpressionException(inventoryGeneratorLoadMatcher, "inventoryGeneratorLoadMatcher", RecurrentComplex.logger);
         inventoryGeneratorGenerationMatcher.setExpression(config.getString("inventoryGeneratorGenerationMatcher", "balancing", "", "Resource Expression that will be applied to each loading loot table, determining if it should be set to 'active'."));
         ConfigUtil.logExpressionException(inventoryGeneratorGenerationMatcher, "inventoryGeneratorGenerationMatcher", RecurrentComplex.logger);
         universalBiomeExpression.setExpression(config.getString("universalBiomeMatcher", "balancing", "", "Biome Expression that will be checked for every single structure. Use this if you want to blacklist / whitelist specific biomes that shouldn't have structures."));
         ConfigUtil.logExpressionException(universalBiomeExpression, "universalBiomeMatcher", RecurrentComplex.logger);
         universalDimensionExpression.setExpression(config.getString("universalDimensionMatcher", "balancing", "", "Dimension Expression that will be checked for every single structure. Use this if you want to blacklist / whitelist specific dimensions that shouldn't have structures."));
         ConfigUtil.logExpressionException(universalDimensionExpression, "universalDimensionMatcher", RecurrentComplex.logger);
         failingStructureLogExpression.setExpression(config.getString("failingStructureLogExpression", "balancing", "", "Resource Expression that will restrict logging of structures that fail to generate."));
         ConfigUtil.logExpressionException(failingStructureLogExpression, "failingStructureLogExpression", RecurrentComplex.logger);
         customArtifactTag = Pair.of(config.getString("customArtifactTag", "balancing", "", "Custom Loot Table to override when an artifact generation tag fires."), Float.valueOf(config.getFloat("customArtifactChance", "balancing", 0.0F, 0.0F, 1.0F, "Chance to use the customArtifactTag when an artifact generation tag fires.")));
         customBookTag = Pair.of(config.getString("customBookTag", "balancing", "", "Custom Loot Table to override when a book generation tag fires."), Float.valueOf(config.getFloat("customBookChance", "balancing", 0.0F, 0.0F, 1.0F, "Chance to use the customArtifactTag when a book generation tag fires.")));
         mazePlacementReversesPerRoom = config.getFloat("mazePlacementReversesPerRoom", "balancing", 3.0F, -1.0F, 100.0F, "Maximum number of reverses per room the maze generator can do. A higher number results in a better generation success rate, but may freeze the server temporarily.");
         mazeTimeout = (long)config.getInt("mazeTimeout", "balancing", 20000, -1, 600000, "Maze generation timeout, in milliseconds. After the time is over, the maze generation will just give up.");
         universalTransformer = null;
         Collections.addAll(universalTransformerPresets, config.getStringList("universalTransformerPresets", "balancing", new String[0], "Transformer preset names that are gonna be applied to every single generating structure. Use this if you need to enforce specific rules (e.g. \"don't ever spawn wood blocks\" (with a replace transformer)."));
         globalToggles.clear();
         var6 = config.getStringList("globalToggles", "balancing", new String[]{"treeLeavesDecay: true"}, "Global toggles that can be used in expressions. You can also add your own. Ex: 'treeLeavesDecay: true'.");
         var8 = Function.identity();
         var10 = Boolean::valueOf;
         Map var13 = globalToggles;
         globalToggles.getClass();
         ConfigUtil.parseMap(var6, (String)null, var8, "global toggle boolean", var10, var13::put);
      }

      if (configID == null || configID.equals("decoration")) {
         baseVillageSpawnWeight = config.getInt("baseVillageSpawnWeight", "decoration", 10, 0, 100000, "The base weight of RC village generation types. Vanilla average is about 10 - if you want to fully replace vanilla structures in villages, crank this up to something big.");
         saplingTriggerChance = (double)config.getFloat("saplingTriggerChance", "decoration", 1.0F, 0.0F, 1.0F, "The chance to trigger any special sapling spawns at all. If you want to disable the big trees, set this to 0.");
         baseSaplingSpawnWeight = (double)config.getFloat("baseSaplingSpawnWeight", "decoration", 0.2F, 0.0F, 100000.0F, "The base weight of RC sapling generation types. The vanilla tree weight is 1 - if you want to fully replace vanilla trees, crank this up to something big.");
         baseDecorationWeights.clear();

         for(RCBiomeDecorator.DecorationType decorationType : RCBiomeDecorator.DecorationType.values()) {
            baseDecorationWeights.put(decorationType, (double)config.getFloat("baseWeight_" + decorationType.id(), "decoration", 0.2F, 0.0F, 1000.0F, "The base weight of this decoration type. The vanilla decorator has a weight of 1 - if you want to fully replace vanilla decoration, crank this up to something big."));
         }
      }

      RecurrentComplex.proxy.loadConfig(configID);
   }

   public static boolean isLightweightMode() {
      return lightweightMode;
   }

   public static boolean shouldResourceLoad(String fileSuffix, String id, String domain) {
      if (fileSuffix.equals(StructureSaveHandler.INSTANCE.suffix)) {
         return structureLoadMatcher.test(new RawResourceLocation(domain, id));
      } else {
         return fileSuffix.equals("rcig") ? inventoryGeneratorLoadMatcher.test(new RawResourceLocation(domain, id)) : true;
      }
   }

   public static boolean shouldStructureGenerate(String id, String domain) {
      if (!structureGenerationMatcher.test(new RawResourceLocation(domain, id))) {
         return false;
      } else {
         return tweakedSpawnRate(id) > 0.0F;
      }
   }

   public static boolean shouldLootGenerate(String tableID, String domain) {
      return inventoryGeneratorGenerationMatcher.test(new RawResourceLocation(domain, tableID));
   }

   public static float tweakedSpawnRate(String structure) {
      return structure != null ? spawnTweaks.get(structure) : 1.0F;
   }

   public static boolean isGenerationEnabled(Biome biome) {
      return !universalBiomeExpression.isExpressionValid() || universalBiomeExpression.test(biome);
   }

   public static boolean isGenerationEnabled(WorldProvider provider) {
      return !universalDimensionExpression.isExpressionValid() || universalDimensionExpression.test(provider);
   }

   public static boolean canUseCommand(String command, ICommandSender sender) {
      CommandExpression matcher = (CommandExpression)commandMatchers.get(command);
      return matcher == null || matcher.test(new CommandExpression.Argument(command, sender));
   }

   public static boolean logFailingStructure(Structure structure) {
      RawResourceLocation loc = StructureRegistry.INSTANCE.resourceLocation(structure);
      return loc == null || failingStructureLogExpression.test(loc);
   }

   public static TransformerMulti getUniversalTransformer() {
      if (universalTransformer == null) {
         universalTransformer = new TransformerMulti("rc_universal", "");
         universalTransformer.getData().setToCustom();
         universalTransformer.getTransformers().clear();
         Stream var10000 = universalTransformerPresets.stream().map(TransformerMulti::fromPreset);
         List var10001 = universalTransformer.getTransformers();
         var10000.forEach(var10001::add);
      }

      return universalTransformer;
   }

   public static void writeSpawnTweaks() {
      List<String> writable = ConfigUtil.writeMap(spawnTweaks);
      ConfigCategory category = RecurrentComplex.config.getCategory("balancing");
      Property property = category.get("spawnTweaks");
      property.set((String[])writable.toArray(new String[0]));
   }

   static {
      StructureRegistry var10002 = StructureRegistry.INSTANCE;
      StructureRegistry.INSTANCE.getClass();
      structureLoadMatcher = new ResourceExpression(var10002::has);
      var10002 = StructureRegistry.INSTANCE;
      StructureRegistry.INSTANCE.getClass();
      structureGenerationMatcher = new ResourceExpression(var10002::has);
      GenericItemCollectionRegistry var1 = GenericItemCollectionRegistry.INSTANCE;
      GenericItemCollectionRegistry.INSTANCE.getClass();
      inventoryGeneratorLoadMatcher = new ResourceExpression(var1::has);
      var1 = GenericItemCollectionRegistry.INSTANCE;
      GenericItemCollectionRegistry.INSTANCE.getClass();
      inventoryGeneratorGenerationMatcher = new ResourceExpression(var1::has);
   }
}
