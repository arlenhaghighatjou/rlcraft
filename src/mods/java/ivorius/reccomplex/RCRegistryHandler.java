package ivorius.reccomplex;

import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.network.CapabilityUpdateRegistry;
import ivorius.ivtoolkit.network.PacketEntityCapabilityData;
import ivorius.ivtoolkit.network.PacketEntityCapabilityDataHandler;
import ivorius.ivtoolkit.network.PacketGuiAction;
import ivorius.ivtoolkit.network.PacketGuiActionHandler;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.NBTCompoundObjectCapabilityStorage;
import ivorius.reccomplex.biome.RCBiomeDictionary;
import ivorius.reccomplex.block.BlockGenericSolid;
import ivorius.reccomplex.block.BlockGenericSpace;
import ivorius.reccomplex.block.BlockScript;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.block.TileEntityBlockScript;
import ivorius.reccomplex.block.legacy.BlockMazeGenerator;
import ivorius.reccomplex.block.legacy.BlockSpawnCommand;
import ivorius.reccomplex.block.legacy.BlockStructureGenerator;
import ivorius.reccomplex.block.materials.MaterialNegativeSpace;
import ivorius.reccomplex.block.materials.RCMaterials;
import ivorius.reccomplex.capability.CapabilitySelection;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.files.loading.FileLoader;
import ivorius.reccomplex.files.loading.FileLoaderRegistryString;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.files.saving.FileSaver;
import ivorius.reccomplex.files.saving.FileSaverString;
import ivorius.reccomplex.gui.RCCreativeTabs;
import ivorius.reccomplex.item.GeneratingItem;
import ivorius.reccomplex.item.ItemArtifactGenerator;
import ivorius.reccomplex.item.ItemBlockGenericSolid;
import ivorius.reccomplex.item.ItemBlockGenericSpace;
import ivorius.reccomplex.item.ItemBlockSelectorBlock;
import ivorius.reccomplex.item.ItemBlockSelectorFloating;
import ivorius.reccomplex.item.ItemBookGenerator;
import ivorius.reccomplex.item.ItemInspector;
import ivorius.reccomplex.item.ItemLootGenMultiTag;
import ivorius.reccomplex.item.ItemLootGenSingleTag;
import ivorius.reccomplex.item.ItemLootTableComponentTag;
import ivorius.reccomplex.item.RCItems;
import ivorius.reccomplex.json.SerializableStringTypeRegistry;
import ivorius.reccomplex.network.PacketEditLootTable;
import ivorius.reccomplex.network.PacketEditLootTableHandler;
import ivorius.reccomplex.network.PacketEditStructure;
import ivorius.reccomplex.network.PacketEditStructureHandler;
import ivorius.reccomplex.network.PacketEditTileEntity;
import ivorius.reccomplex.network.PacketEditTileEntityHandler;
import ivorius.reccomplex.network.PacketInspectBlock;
import ivorius.reccomplex.network.PacketInspectBlockHandler;
import ivorius.reccomplex.network.PacketInspectEntity;
import ivorius.reccomplex.network.PacketInspectEntityHandler;
import ivorius.reccomplex.network.PacketItemEvent;
import ivorius.reccomplex.network.PacketItemEventHandler;
import ivorius.reccomplex.network.PacketOpenGui;
import ivorius.reccomplex.network.PacketOpenGuiHandler;
import ivorius.reccomplex.network.PacketReopenGui;
import ivorius.reccomplex.network.PacketReopenGuiHandler;
import ivorius.reccomplex.network.PacketSaveLootTable;
import ivorius.reccomplex.network.PacketSaveLootTableHandler;
import ivorius.reccomplex.network.PacketSaveStructure;
import ivorius.reccomplex.network.PacketSaveStructureHandler;
import ivorius.reccomplex.network.PacketSpawnTweaks;
import ivorius.reccomplex.network.PacketSpawnTweaksHandler;
import ivorius.reccomplex.network.PacketSyncItem;
import ivorius.reccomplex.network.PacketSyncItemHandler;
import ivorius.reccomplex.network.PacketWorldData;
import ivorius.reccomplex.network.PacketWorldDataHandler;
import ivorius.reccomplex.operation.OperationClearArea;
import ivorius.reccomplex.operation.OperationGenerateSchematic;
import ivorius.reccomplex.operation.OperationGenerateStructure;
import ivorius.reccomplex.operation.OperationMulti;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.random.Poem;
import ivorius.reccomplex.utils.FMLUtils;
import ivorius.reccomplex.utils.presets.PresetRegistry;
import ivorius.reccomplex.world.gen.feature.GenerationSanityChecker;
import ivorius.reccomplex.world.gen.feature.RCWorldgenMonitor;
import ivorius.reccomplex.world.gen.feature.selector.NaturalStructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.StructureSaveHandler;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.ListGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.SaplingGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.StaticGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaDecorationGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.MazeRuleRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved.MazeRuleConnect;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules.saved.MazeRuleConnectAll;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorLimit;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorMatch;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.GenericPlacer;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayAverageMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayDynamicMove;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayDynamicPosition;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayMove;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.BiomeMatcherPresets;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.DimensionMatcherPresets;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.GenericPlacerPresets;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.TransfomerPresets;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.WeightedBlockStatePresets;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.Transformer;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerEnsureBlocks;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerNatural;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerNaturalAir;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerNegativeSpace;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerPillar;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerProperty;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerReplace;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerRuins;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerVillageSpecific;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerWorldScript;
import ivorius.reccomplex.world.gen.script.WorldScriptCommand;
import ivorius.reccomplex.world.gen.script.WorldScriptHolder;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import ivorius.reccomplex.world.gen.script.WorldScriptMulti;
import ivorius.reccomplex.world.gen.script.WorldScriptRegistry;
import ivorius.reccomplex.world.gen.script.WorldScriptStructureGenerator;
import ivorius.reccomplex.world.storage.loot.GenericItemCollectionRegistry;
import ivorius.reccomplex.world.storage.loot.ItemCollectionSaveHandler;
import ivorius.reccomplex.world.storage.loot.RCLoot;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class RCRegistryHandler {
   public RCRegistryHandler() {
   }

   public static void preInit(FMLPreInitializationEvent event, RecurrentComplex mod) {
      if (!RecurrentComplex.isLite()) {
         RCCreativeTabs.tabStructureTools = new CreativeTabs("structureTools") {
            public ItemStack getTabIconItem() {
               return new ItemStack(RCItems.blockSelector);
            }
         };
         RCCreativeTabs.tabLoot = new CreativeTabs("inventoryGenerators") {
            public ItemStack getTabIconItem() {
               return new ItemStack(RCItems.lootGenerationTag);
            }
         };
      }

      RCMaterials.materialNegativeSpace = new MaterialNegativeSpace();
      RCMaterials.materialGenericSolid = new Material(MapColor.STONE);
      CapabilityManager.INSTANCE.register(RCEntityInfo.class, new NBTCompoundObjectCapabilityStorage(RCEntityInfo.class), RCEntityInfo::new);
      CapabilityManager.INSTANCE.register(CapabilitySelection.class, new NBTCompoundObjectCapabilityStorage(CapabilitySelection.class), CapabilitySelection::new);
      RCItems.blockSelector = (new ItemBlockSelectorBlock()).setUnlocalizedName("blockSelector");
      RCItems.blockSelector.setCreativeTab(RCCreativeTabs.tabStructureTools);
      register(RCItems.blockSelector, "block_selector");
      RecurrentComplex.cremapper.registerLegacyIDs(RCItems.blockSelector, "blockSelector");
      RCItems.blockSelectorFloating = (new ItemBlockSelectorFloating()).setUnlocalizedName("blockSelectorFloating");
      RCItems.blockSelectorFloating.setCreativeTab(RCCreativeTabs.tabStructureTools);
      register(RCItems.blockSelectorFloating, "block_selector_floating");
      RecurrentComplex.cremapper.registerLegacyIDs(RCItems.blockSelectorFloating, "blockSelectorFloating");
      RCItems.lootGenerationTag = (ItemLootGenMultiTag)(new ItemLootGenMultiTag()).setUnlocalizedName("inventoryGenerationTag");
      RCItems.lootGenerationTag.setCreativeTab(RCCreativeTabs.tabLoot);
      register(RCItems.lootGenerationTag, "inventory_generation_tag");
      RecurrentComplex.cremapper.registerLegacyIDs(RCItems.lootGenerationTag, "inventoryGenerationTag");
      RCItems.lootGenerationSingleTag = (ItemLootGenSingleTag)(new ItemLootGenSingleTag()).setUnlocalizedName("inventoryGenerationSingleTag");
      RCItems.lootGenerationSingleTag.setCreativeTab(RCCreativeTabs.tabLoot);
      register(RCItems.lootGenerationSingleTag, "inventory_generation_single_tag");
      RecurrentComplex.cremapper.registerLegacyIDs(RCItems.lootGenerationSingleTag, "inventoryGenerationSingleTag");
      RCItems.lootGenerationComponentTag = (ItemLootTableComponentTag)(new ItemLootTableComponentTag()).setUnlocalizedName("inventoryGenerationComponentTag");
      RCItems.lootGenerationComponentTag.setCreativeTab(RCCreativeTabs.tabLoot);
      register(RCItems.lootGenerationComponentTag, "inventory_generation_component_tag");
      RCItems.artifactGenerationTag = (new ItemArtifactGenerator()).setUnlocalizedName("artifactGenerationTag");
      RCItems.artifactGenerationTag.setCreativeTab(RCCreativeTabs.tabLoot);
      register(RCItems.artifactGenerationTag, "artifact_generation_tag");
      RecurrentComplex.cremapper.registerLegacyIDs(RCItems.artifactGenerationTag, "artifactGenerationTag");
      RCItems.bookGenerationTag = (new ItemBookGenerator()).setUnlocalizedName("bookGenerationTag");
      RCItems.bookGenerationTag.setCreativeTab(RCCreativeTabs.tabLoot);
      register(RCItems.bookGenerationTag, "book_generation_tag");
      RecurrentComplex.cremapper.registerLegacyIDs(RCItems.bookGenerationTag, "bookGenerationTag");
      RCBlocks.genericSpace = (BlockGenericSpace)(new BlockGenericSpace()).setUnlocalizedName("negativeSpace");
      RCBlocks.genericSpace.setCreativeTab(RCCreativeTabs.tabStructureTools);
      register(RCBlocks.genericSpace, "generic_space", new ItemBlockGenericSpace(RCBlocks.genericSpace));
      RecurrentComplex.cremapper.registerLegacyIDs(RCBlocks.genericSpace, true, "negativeSpace");
      RCBlocks.genericSolid = (new BlockGenericSolid()).setUnlocalizedName("naturalFloor");
      RCBlocks.genericSolid.setCreativeTab(RCCreativeTabs.tabStructureTools);
      register(RCBlocks.genericSolid, "generic_solid", new ItemBlockGenericSolid(RCBlocks.genericSolid));
      RecurrentComplex.cremapper.registerLegacyIDs(RCBlocks.genericSolid, true, "naturalFloor");
      RCBlocks.structureGenerator = (new BlockStructureGenerator()).setUnlocalizedName("structureGenerator");
      register(RCBlocks.structureGenerator, "structure_generator");
      register(BlockStructureGenerator.TileEntityStructureGenerator.class, "RCStructureGenerator", "SGStructureGenerator");
      RecurrentComplex.cremapper.registerLegacyIDs(RCBlocks.structureGenerator, true, "structureGenerator");
      RCBlocks.mazeGenerator = (new BlockMazeGenerator()).setUnlocalizedName("mazeGenerator");
      register(RCBlocks.mazeGenerator, "maze_generator");
      register(BlockMazeGenerator.TileEntityMazeGenerator.class, "RCMazeGenerator", "SGMazeGenerator");
      RecurrentComplex.cremapper.registerLegacyIDs(RCBlocks.mazeGenerator, true, "mazeGenerator");
      RCBlocks.spawnCommands = (new BlockSpawnCommand()).setUnlocalizedName("spawn_command");
      register(RCBlocks.spawnCommands, "weighted_command_block");
      register(BlockSpawnCommand.TileEntitySpawnCommand.class, "RCSpawnCommand");
      RecurrentComplex.cremapper.registerLegacyIDs(RCBlocks.spawnCommands, true, "spawnCommand");
      RCBlocks.spawnScript = (new BlockScript()).setUnlocalizedName("spawn_script");
      RCBlocks.spawnScript.setCreativeTab(RCCreativeTabs.tabStructureTools);
      register(RCBlocks.spawnScript, "spawn_script");
      register(TileEntityBlockScript.class, "RCSpawnScript");
      RCItems.inspector = (new ItemInspector()).setUnlocalizedName("recinspector");
      RCItems.inspector.setCreativeTab(RCCreativeTabs.tabStructureTools);
      register(RCItems.inspector, "inspector");
      GenericPlacerPresets.instance().getRegistry().register("clear", "reccomplex", PresetRegistry.fullPreset("clear", new GenericPlacer(), new PresetRegistry.Metadata("Clear", "Do not place anywhere")), true, LeveledRegistry.Level.INTERNAL);
      GenericPlacerPresets.instance().setDefault("clear");
      DimensionMatcherPresets.instance().getRegistry().register("clear", "reccomplex", PresetRegistry.fullPreset("clear", new ArrayList(), new PresetRegistry.Metadata("None", "No dimensions")), true, LeveledRegistry.Level.INTERNAL);
      DimensionMatcherPresets.instance().setDefault("clear");
      BiomeMatcherPresets.instance().getRegistry().register("clear", "reccomplex", PresetRegistry.fullPreset("clear", new ArrayList(), new PresetRegistry.Metadata("None", "No biomes")), true, LeveledRegistry.Level.INTERNAL);
      BiomeMatcherPresets.instance().setDefault("clear");
      WeightedBlockStatePresets.instance().getRegistry().register("clear", "reccomplex", PresetRegistry.fullPreset("clear", new ArrayList(), new PresetRegistry.Metadata("None", "No blocks")), true, LeveledRegistry.Level.INTERNAL);
      WeightedBlockStatePresets.instance().setDefault("clear");
      TransfomerPresets.instance().getRegistry().register("clear", "reccomplex", PresetRegistry.fullPreset("clear", new TransformerMulti.Data(), new PresetRegistry.Metadata("None", "No transformers")), true, LeveledRegistry.Level.INTERNAL);
      TransfomerPresets.instance().setDefault("clear");
      GenerationSanityChecker.init();
   }

   public static void register(Item item, String id) {
      item.setRegistryName(id);
      if (!RecurrentComplex.isLite()) {
         ForgeRegistries.ITEMS.register(item);
      } else {
         RecurrentComplex.specialRegistry.register(item.getRegistryName(), item);
      }

   }

   public static void register(Block block, String id, ItemBlock item) {
      block.setRegistryName(id);
      item.setRegistryName(id);
      if (!RecurrentComplex.isLite()) {
         ForgeRegistries.BLOCKS.register(block);
         ForgeRegistries.ITEMS.register(item);
      } else {
         RecurrentComplex.specialRegistry.register(block.getRegistryName(), block);
         RecurrentComplex.specialRegistry.register(item.getRegistryName(), item);
      }

   }

   public static void register(Block block, String id) {
      register(block, id, new ItemBlock(block));
   }

   /** @deprecated */
   @Deprecated
   public static void register(Block block, String id, Class<? extends ItemBlock> itemClass, Object... itemArgs) {
      ItemBlock item = FMLUtils.constructItem(block, itemClass, itemArgs);
      register(block, id, item != null ? item : new ItemBlock(block));
   }

   public static void register(Class<? extends TileEntity> tileEntity, String id, String... alternatives) {
      if (!RecurrentComplex.isLite()) {
         GameRegistry.registerTileEntity(tileEntity, id);
      } else {
         RecurrentComplex.specialRegistry.register(new ResourceLocation(id), tileEntity);
      }

      for(String aid : alternatives) {
         RecurrentComplex.specialRegistry.register(new ResourceLocation(aid), tileEntity);
      }

   }

   public static void load(FMLInitializationEvent event, RecurrentComplex mod) {
      MCRegistry mcRegistry = RecurrentComplex.specialRegistry;
      registerPackets(event);
      CapabilityUpdateRegistry.INSTANCE.register("structureEntityInfo", RCEntityInfo.CAPABILITY);
      CapabilityUpdateRegistry.INSTANCE.register("rc_selection", CapabilitySelection.CAPABILITY);
      RCBiomeDictionary.registerTypes();
      FileLoader var10000 = RecurrentComplex.loader;
      StructureSaveHandler var10003 = StructureSaveHandler.INSTANCE;
      StructureSaveHandler.INSTANCE.getClass();
      var10000.register(var10003.new Loader());
      var10000 = RecurrentComplex.loader;
      GenericItemCollectionRegistry var10004 = GenericItemCollectionRegistry.INSTANCE;
      ItemCollectionSaveHandler var10005 = ItemCollectionSaveHandler.INSTANCE;
      ItemCollectionSaveHandler.INSTANCE.getClass();
      var10000.register(new FileLoaderRegistryString("rcig", var10004, var10005::fromJSON));
      RecurrentComplex.loader.register(new FileLoaderRegistryString("rcpt", Poem.THEME_REGISTRY, Poem.Theme::fromFile));
      RecurrentComplex.loader.register(new FileLoaderRegistryString("rcnc", NaturalStructureSelector.CATEGORY_REGISTRY, NaturalStructureSelector.SimpleCategory.class));
      RecurrentComplex.loader.register(BiomeMatcherPresets.instance().loader());
      RecurrentComplex.loader.register(DimensionMatcherPresets.instance().loader());
      RecurrentComplex.loader.register(WeightedBlockStatePresets.instance().loader());
      RecurrentComplex.loader.register(GenericPlacerPresets.instance().loader());
      RecurrentComplex.loader.register(TransfomerPresets.instance().loader());
      FileSaver var11 = RecurrentComplex.saver;
      var10003 = StructureSaveHandler.INSTANCE;
      StructureSaveHandler.INSTANCE.getClass();
      var11.register(var10003.new Saver("structure"));
      var11 = RecurrentComplex.saver;
      GenericItemCollectionRegistry var14 = GenericItemCollectionRegistry.INSTANCE;
      ItemCollectionSaveHandler var10006 = ItemCollectionSaveHandler.INSTANCE;
      ItemCollectionSaveHandler.INSTANCE.getClass();
      var11.register(new FileSaverString("inventory_generation_component", "rcig", var14, var10006::toJSON));
      RecurrentComplex.saver.register(new FileSaverString("natural_generation_category", "rcnc", NaturalStructureSelector.CATEGORY_REGISTRY, NaturalStructureSelector.SimpleCategory.class));
      RecurrentComplex.saver.register(BiomeMatcherPresets.instance().saver("biome_preset"));
      RecurrentComplex.saver.register(DimensionMatcherPresets.instance().saver("dimension_preset"));
      RecurrentComplex.saver.register(WeightedBlockStatePresets.instance().saver("block_preset"));
      RecurrentComplex.saver.register(GenericPlacerPresets.instance().saver("placer_preset"));
      RecurrentComplex.saver.register(TransfomerPresets.instance().saver("transformer_preset"));
      WorldScriptRegistry worldScriptRegistry = WorldScriptRegistry.INSTANCE;
      worldScriptRegistry.register("multi", WorldScriptMulti.class);
      worldScriptRegistry.register("strucGen", WorldScriptStructureGenerator.class);
      worldScriptRegistry.register("mazeGen", WorldScriptMazeGenerator.class);
      worldScriptRegistry.register("command", WorldScriptCommand.class);
      worldScriptRegistry.register("holder", WorldScriptHolder.class);
      SerializableStringTypeRegistry<Transformer> transformerRegistry = StructureRegistry.TRANSFORMERS;
      transformerRegistry.registerType("multi", TransformerMulti.class, new TransformerMulti.Serializer());
      transformerRegistry.registerType("worldscript", TransformerWorldScript.class, new TransformerWorldScript.Serializer(mcRegistry));
      transformerRegistry.registerType("villagereplace", TransformerVillageSpecific.class, new TransformerVillageSpecific.Serializer(mcRegistry));
      transformerRegistry.registerType("natural", TransformerNatural.class, new TransformerNatural.Serializer(mcRegistry));
      transformerRegistry.registerType("naturalAir", TransformerNaturalAir.class, new TransformerNaturalAir.Serializer(mcRegistry));
      transformerRegistry.registerType("pillar", TransformerPillar.class, new TransformerPillar.Serializer(mcRegistry));
      TransformerReplace.Serializer replaceSerializer = new TransformerReplace.Serializer(mcRegistry);
      transformerRegistry.registerType("replaceAll", TransformerReplace.class, replaceSerializer);
      transformerRegistry.registerLegacy("replace", TransformerReplace.class, new TransformerReplace.NonUniformSerializer(replaceSerializer));
      transformerRegistry.registerType("ruins", TransformerRuins.class, new TransformerRuins.Serializer(mcRegistry));
      transformerRegistry.registerType("negativeSpace", TransformerNegativeSpace.class, new TransformerNegativeSpace.Serializer(mcRegistry));
      transformerRegistry.registerType("ensureBlocks", TransformerEnsureBlocks.class, new TransformerEnsureBlocks.Serializer(mcRegistry));
      transformerRegistry.registerType("propertyReplace", TransformerProperty.class, new TransformerProperty.Serializer(mcRegistry));
      SerializableStringTypeRegistry<GenerationType> genInfoRegistry = StructureRegistry.GENERATION_TYPES;
      genInfoRegistry.registerType("natural", NaturalGeneration.class, new NaturalGeneration.Serializer());
      genInfoRegistry.registerType("structureList", ListGeneration.class, new ListGeneration.Serializer());
      genInfoRegistry.registerType("mazeComponent", MazeGeneration.class, new MazeGeneration.Serializer());
      genInfoRegistry.registerType("static", StaticGeneration.class, new StaticGeneration.Serializer());
      genInfoRegistry.registerType("vanilla", VanillaGeneration.class, new VanillaGeneration.Serializer());
      genInfoRegistry.registerType("sapling", SaplingGeneration.class, new SaplingGeneration.Serializer());
      genInfoRegistry.registerType("decoration", VanillaDecorationGeneration.class, new VanillaDecorationGeneration.Serializer());
      StructureRegistry.INSTANCE.registerModule(new NaturalGeneration.Cache());
      StructureRegistry.INSTANCE.registerModule(new VanillaDecorationGeneration.Cache());
      StructureRegistry.INSTANCE.registerModule(new VanillaGeneration.Cache());
      SerializableStringTypeRegistry<GenericPlacer.Factor> placerFactorRegistry = FactorRegistry.INSTANCE.getTypeRegistry();
      placerFactorRegistry.registerType("limit", FactorLimit.class, new FactorLimit.Serializer());
      placerFactorRegistry.registerType("match", FactorMatch.class, new FactorMatch.Serializer());
      SerializableStringTypeRegistry<FactorLimit.Ray> rayRegistry = FactorLimit.getRayRegistry();
      rayRegistry.registerType("dynpos", RayDynamicPosition.class, (JsonSerializer)null);
      rayRegistry.registerType("move", RayMove.class, (JsonSerializer)null);
      rayRegistry.registerType("matcher", RayMatcher.class, new RayMatcher.Serializer());
      rayRegistry.registerType("average", RayAverageMatcher.class, new RayAverageMatcher.Serializer());
      rayRegistry.registerType("dynmove", RayDynamicMove.class, (JsonSerializer)null);
      MazeRuleRegistry mazeRuleRegistry = MazeRuleRegistry.INSTANCE;
      mazeRuleRegistry.register("connect", MazeRuleConnect.class);
      mazeRuleRegistry.register("connectall", MazeRuleConnectAll.class);
      OperationRegistry.register("multi", OperationMulti.class);
      OperationRegistry.register("strucGen", OperationGenerateStructure.class);
      OperationRegistry.register("schemGen", OperationGenerateSchematic.class);
      OperationRegistry.register("clearArea", OperationClearArea.class);
      RCLoot.registerVanillaLootTables();
      GameRegistry.registerFuelHandler((fuel) -> {
         return fuel.getItem() instanceof GeneratingItem ? 1 : 0;
      });
      RCWorldgenMonitor.create();
   }

   protected static <T> void dumpAll(PresetRegistry<T> presets) {
      presets.allIDs().forEach((s) -> {
         RecurrentComplex.saver.trySave(ResourceDirectory.ACTIVE.toPath(), presets.getFileSuffix(), s);
      });
   }

   public static void registerPackets(FMLInitializationEvent event) {
      registerClientPackets();
      registerServerPackets();
   }

   protected static void registerServerPackets() {
      RecurrentComplex.network.registerMessage(PacketGuiActionHandler.class, PacketGuiAction.class, 1, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketSaveLootTableHandler.class, PacketSaveLootTable.class, 2, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketEditTileEntityHandler.class, PacketEditTileEntity.class, 5, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketSaveStructureHandler.class, PacketSaveStructure.class, 7, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketSyncItemHandler.class, PacketSyncItem.class, 9, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketItemEventHandler.class, PacketItemEvent.class, 11, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketInspectBlockHandler.class, PacketInspectBlock.class, 12, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketOpenGuiHandler.class, PacketOpenGui.class, 15, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketInspectEntityHandler.class, PacketInspectEntity.class, 17, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketWorldDataHandler.class, PacketWorldData.class, 20, Side.SERVER);
      RecurrentComplex.network.registerMessage(PacketSpawnTweaksHandler.class, PacketSpawnTweaks.class, 21, Side.SERVER);
   }

   protected static void registerClientPackets() {
      RecurrentComplex.network.registerMessage(PacketEntityCapabilityDataHandler.class, PacketEntityCapabilityData.class, 0, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketEditLootTableHandler.class, PacketEditLootTable.class, 3, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketEditTileEntityHandler.class, PacketEditTileEntity.class, 4, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketEditStructureHandler.class, PacketEditStructure.class, 6, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketSyncItemHandler.class, PacketSyncItem.class, 8, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketItemEventHandler.class, PacketItemEvent.class, 10, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketInspectBlockHandler.class, PacketInspectBlock.class, 13, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketOpenGuiHandler.class, PacketOpenGui.class, 14, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketReopenGuiHandler.class, PacketReopenGui.class, 16, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketInspectEntityHandler.class, PacketInspectEntity.class, 18, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketWorldDataHandler.class, PacketWorldData.class, 19, Side.CLIENT);
      RecurrentComplex.network.registerMessage(PacketSpawnTweaksHandler.class, PacketSpawnTweaks.class, 22, Side.CLIENT);
   }
}
