package net.silentchaos512.lib.registry;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.conditions.LootCondition.Serializer;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraft.world.storage.loot.properties.EntityPropertyManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.block.BlockMetaSubtypes;
import net.silentchaos512.lib.block.IColoredBlock;
import net.silentchaos512.lib.block.ITileEntityBlock;
import net.silentchaos512.lib.client.render.TEISRCustomEnchantedEffect;
import net.silentchaos512.lib.item.IColoredItem;
import net.silentchaos512.lib.item.ICustomEnchantColor;
import net.silentchaos512.lib.item.ItemBlockMetaSubtypes;
import net.silentchaos512.lib.util.GameUtil;
import net.silentchaos512.lib.util.LogHelper;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class SRegistry {
   private static final Pattern PATTERN_REGISTRY_NAME = Pattern.compile("[^a-z0-9_]+");
   private final List<Block> blocks = NonNullList.create();
   private final List<Item> items = NonNullList.create();
   private final List<IAddRecipes> recipeAdders = NonNullList.create();
   private final List<Block> coloredBlocks = NonNullList.create();
   private final List<Item> coloredItems = NonNullList.create();
   private final List<IPhasedInitializer> phasedInitializers = new ArrayList();
   private final Map<Class<? extends IForgeRegistryEntry<?>>, Consumer<SRegistry>> registrationHandlers = new HashMap();
   private Object mod;
   /** @deprecated */
   @Deprecated
   private final LogHelper logHelper;
   private final Logger logger;
   private final String modId;
   private final String resourcePrefix;
   @Nonnull
   private final RecipeMaker recipes;
   @Nullable
   private CreativeTabs defaultCreativeTab = null;
   private int lastEntityId = -1;
   private boolean preInitDone = false;
   private boolean initDone = false;
   private boolean postInitDone = false;

   public SRegistry() {
      ModContainer mod = (ModContainer)Objects.requireNonNull(Loader.instance().activeModContainer());
      this.modId = mod.getModId();
      this.resourcePrefix = this.modId + ":";
      this.logHelper = new LogHelper(mod.getName() + "/SRegistry", 1);
      this.logger = LogManager.getLogger(mod.getName() + "/SRegistry");
      this.recipes = new RecipeMaker(this.modId);
      (new SRegistry.EventHandler(this)).register(MinecraftForge.EVENT_BUS);
   }

   public RecipeMaker getRecipeMaker() {
      return this.recipes;
   }

   public void setMod(Object mod) {
      this.mod = mod;
   }

   /** @deprecated */
   @Deprecated
   private LogHelper logger() {
      return this.logHelper != null ? this.logHelper : SilentLib.logHelper;
   }

   public void addPhasedInitializer(IPhasedInitializer instance) {
      this.phasedInitializers.add(instance);
   }

   public void addRegistrationHandler(Consumer<SRegistry> registerFunction, Class<? extends IForgeRegistryEntry<?>> registryClass) throws RuntimeException {
      if (this.registrationHandlers.containsKey(registryClass)) {
         throw new RuntimeException("Registration handler for class " + registryClass + " already registered!");
      } else {
         this.registrationHandlers.put(registryClass, registerFunction);
      }
   }

   public CreativeTabs makeCreativeTab(String label, final Supplier<ItemStack> icon) {
      CreativeTabs tab = new CreativeTabs(label) {
         public ItemStack getTabIconItem() {
            return (ItemStack)icon.get();
         }
      };
      if (this.defaultCreativeTab == null) {
         this.defaultCreativeTab = tab;
      }

      return tab;
   }

   public <T extends Block> T registerBlock(T block, String key) {
      return (T)this.registerBlock(block, key, this.defaultItemBlock(block));
   }

   @Nonnull
   private <T extends Block> ItemBlock defaultItemBlock(T block) {
      return (ItemBlock)(block instanceof BlockMetaSubtypes ? new ItemBlockMetaSubtypes((BlockMetaSubtypes)block) : new ItemBlock(block));
   }

   public <T extends Block> T registerBlock(T block, String key, ItemBlock itemBlock) {
      this.blocks.add(block);
      block.setUnlocalizedName(this.modId + "." + key);
      this.validateRegistryName(key);
      ResourceLocation name = new ResourceLocation(this.modId, key);
      this.safeSetRegistryName(block, name);
      ForgeRegistries.BLOCKS.register(block);
      this.safeSetRegistryName(itemBlock, name);
      ForgeRegistries.ITEMS.register(itemBlock);
      if (block instanceof ITileEntityBlock) {
         Class<? extends TileEntity> clazz = ((ITileEntityBlock)block).getTileEntityClass();
         this.registerTileEntity((Class)clazz, key, (Supplier)((ITileEntityBlock)block).getTileEntityFactory());
      }

      if (block instanceof IAddRecipes) {
         this.recipeAdders.add((IAddRecipes)block);
      }

      if (GameUtil.isClient() && block instanceof IColoredBlock) {
         this.coloredBlocks.add(block);
      }

      if (this.defaultCreativeTab != null) {
         block.setCreativeTab(this.defaultCreativeTab);
      }

      return block;
   }

   public <T extends Item> T registerItem(T item, String key) {
      this.items.add(item);
      item.setUnlocalizedName(this.modId + "." + key);
      this.validateRegistryName(key);
      ResourceLocation name = new ResourceLocation(this.modId, key);
      this.safeSetRegistryName(item, name);
      ForgeRegistries.ITEMS.register(item);
      if (item instanceof IAddRecipes) {
         this.recipeAdders.add((IAddRecipes)item);
      }

      if (GameUtil.isClient() && item instanceof IColoredItem) {
         this.coloredItems.add(item);
      }

      if (this.defaultCreativeTab != null) {
         item.setCreativeTab(this.defaultCreativeTab);
      }

      return item;
   }

   public void registerEnchantment(Enchantment enchantment, String key) {
      this.validateRegistryName(key);
      ResourceLocation name = new ResourceLocation(this.modId, key);
      this.safeSetRegistryName(enchantment, name);
      enchantment.setName(name.getResourceDomain() + "." + name.getResourcePath());
      ForgeRegistries.ENCHANTMENTS.register(enchantment);
   }

   public <T extends Entity> void registerEntity(Class<T> entityClass, String key, Function<World, T> factory) {
      this.registerEntity(entityClass, key, ++this.lastEntityId, this.mod, 64, 20, true, factory);
   }

   public <T extends Entity> void registerEntity(Class<T> entityClass, String key, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, Function<World, T> factory) {
      this.registerEntity(entityClass, key, ++this.lastEntityId, this.mod, trackingRange, updateFrequency, sendsVelocityUpdates, factory);
   }

   public <T extends Entity> void registerEntity(Class<T> entityClass, String key, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, Function<World, T> factory) {
      ResourceLocation resource = new ResourceLocation(this.modId, key);
      EntityRegistry.registerModEntity(resource, entityClass, key, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, factory);
   }

   public <T extends Entity> void registerEntity(Class<T> entityClass, String key, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary, Function<World, T> factory) {
      this.registerEntity(entityClass, key, ++this.lastEntityId, this.mod, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary, factory);
   }

   public <T extends Entity> void registerEntity(Class<T> entityClass, String key, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary, int eggSecondary, Function<World, T> factory) {
      ResourceLocation resource = new ResourceLocation(this.modId, key);
      EntityRegistry.registerModEntity(resource, entityClass, key, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary, factory);
   }

   @SideOnly(Side.CLIENT)
   public <T extends Entity> void registerEntityRenderer(Class<T> entityClass, IRenderFactory<T> renderFactory) {
      RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
   }

   public void registerPotion(Potion potion, String key) {
      if (potion.getName().isEmpty()) {
         potion.setPotionName("effect." + this.modId + "." + key);
      }

      this.validateRegistryName(key);
      ResourceLocation name = new ResourceLocation(this.modId, key);
      this.safeSetRegistryName(potion, name);
      ForgeRegistries.POTIONS.register(potion);
   }

   public void registerSoundEvent(SoundEvent sound, String key) {
      this.validateRegistryName(key);
      ResourceLocation name = new ResourceLocation(this.modId, key);
      this.safeSetRegistryName(sound, name);
      ForgeRegistries.SOUND_EVENTS.register(sound);
   }

   public void registerLootCondition(Serializer<? extends LootCondition> serializer) {
      LootConditionManager.registerCondition(serializer);
   }

   public void registerLootEntityProperty(net.minecraft.world.storage.loot.properties.EntityProperty.Serializer<? extends EntityProperty> serializer) {
      EntityPropertyManager.registerProperty(serializer);
   }

   public void registerLootFunction(net.minecraft.world.storage.loot.functions.LootFunction.Serializer<? extends LootFunction> serializer) {
      LootFunctionManager.registerFunction(serializer);
   }

   public void registerLootTable(String name) {
      LootTableList.register(new ResourceLocation(this.modId, name));
   }

   private void safeSetRegistryName(IForgeRegistryEntry<?> entry, ResourceLocation name) {
      if (entry.getRegistryName() == null) {
         entry.setRegistryName(name);
      } else {
         this.logger.warn("Registry name for {} has already been set. Was trying to set it to {}.", entry.getRegistryName(), name);
      }

   }

   private void validateRegistryName(String name) {
      if (PATTERN_REGISTRY_NAME.matcher(name).matches()) {
         this.logger.warn("Invalid name for object: {}", name);
      }

   }

   public <T extends ICriterionInstance> ICriterionTrigger<T> registerAdvancementTrigger(ICriterionTrigger<T> trigger) {
      CriteriaTriggers.register(trigger);
      return trigger;
   }

   public <T extends TileEntity> void registerTileEntity(Class<T> tileClass, String key, Supplier<T> factory) {
      GameRegistry.registerTileEntity(tileClass, new ResourceLocation(this.modId, key), factory);
   }

   @SideOnly(Side.CLIENT)
   public <T extends TileEntity> void registerTileEntitySpecialRenderer(Class<T> tileClass, TileEntitySpecialRenderer<T> renderer) {
      ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
   }

   @SideOnly(Side.CLIENT)
   public void setModel(Block block, int meta, String modelPath) {
      this.setModel(Item.getItemFromBlock(block), meta, modelPath, "inventory");
   }

   @SideOnly(Side.CLIENT)
   public void setModel(Block block, int meta, String modelPath, String variant) {
      this.setModel(Item.getItemFromBlock(block), meta, modelPath, variant);
   }

   @SideOnly(Side.CLIENT)
   public void setModel(Item item, int meta, String modelPath) {
      this.setModel(item, meta, modelPath, "inventory");
   }

   @SideOnly(Side.CLIENT)
   public void setModel(Item item, int meta, String modelPath, String variant) {
      ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(this.resourcePrefix + modelPath, variant));
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (this.preInitDone) {
         this.logger.warn("preInit called more than once!");
      } else {
         this.verifyOrFindModObject();
         this.phasedInitializers.forEach((i) -> {
            i.preInit(this, event);
         });
         this.preInitDone = true;
      }
   }

   private void verifyOrFindModObject() {
      if (this.mod == null) {
         this.logger.warn("Mod {} did not manually set its mod object! This is bad and may cause crashes.", this.modId);
         ModContainer container = (ModContainer)Loader.instance().getIndexedModList().get(this.modId);
         if (container != null) {
            this.mod = container.getMod();
            this.logger.warn("Automatically acquired mod object for {}", this.modId);
         } else {
            this.logger.warn("Could not find mod object. The mod ID is likely incorrect.");
         }
      }

   }

   public void init(FMLInitializationEvent event) {
      if (this.initDone) {
         this.logger.warn("init called more than once!");
      } else {
         this.phasedInitializers.forEach((i) -> {
            i.init(this, event);
         });
         this.initDone = true;
      }
   }

   public void postInit(FMLPostInitializationEvent event) {
      if (this.postInitDone) {
         this.logger.warn("postInit called more than once!");
      } else {
         int oldRecipeRegisterCount = this.recipes.getOldRecipeRegisterCount();
         if (oldRecipeRegisterCount > 0) {
            long totalRecipes = ForgeRegistries.RECIPES.getKeys().stream().map(ResourceLocation::getResourceDomain).filter((s) -> {
               return s.equals(this.modId);
            }).count();
            this.logger.warn("Mod '{}' is still registering recipes with RecipeMaker ({} recipes, out of {} total)", this.modId, Integer.valueOf(oldRecipeRegisterCount), Long.valueOf(totalRecipes));
         }

         this.phasedInitializers.forEach((i) -> {
            i.postInit(this, event);
         });
         this.postInitDone = true;
      }
   }

   @SideOnly(Side.CLIENT)
   public void clientPreInit(FMLPreInitializationEvent event) {
   }

   @SideOnly(Side.CLIENT)
   public void clientInit(FMLInitializationEvent event) {
      for(Block block : this.blocks) {
         if (block instanceof ITileEntityBlock) {
            ITileEntityBlock tileBlock = (ITileEntityBlock)block;
            TileEntitySpecialRenderer tesr = tileBlock.getTileRenderer();
            if (tesr != null) {
               ClientRegistry.bindTileEntitySpecialRenderer(tileBlock.getTileEntityClass(), tesr);
            }
         }
      }

      for(Item item : this.items) {
         if (item instanceof ICustomEnchantColor) {
            item.setTileEntityItemStackRenderer(TEISRCustomEnchantedEffect.INSTANCE);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void clientPostInit(FMLPostInitializationEvent event) {
   }

   private void addRecipes() {
      this.recipeAdders.forEach((obj) -> {
         obj.addRecipes(this.recipes);
      });
   }

   private void addOreDictEntries() {
      this.recipeAdders.forEach(IAddRecipes::addOreDict);
   }

   @SideOnly(Side.CLIENT)
   private void registerModels() {
      for(Block block : this.blocks) {
         if (block instanceof ICustomModel) {
            ((ICustomModel)block).registerModels();
         } else {
            ResourceLocation registryName = (ResourceLocation)Objects.requireNonNull(block.getRegistryName());
            ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, model);
         }
      }

      for(Item item : this.items) {
         if (item instanceof ICustomMesh) {
            ICustomMesh customMesh = (ICustomMesh)item;
            ModelBakery.registerItemVariants(item, customMesh.getVariants());
            ModelLoader.setCustomMeshDefinition(item, customMesh.getCustomMesh());
         } else if (item instanceof ICustomModel) {
            ((ICustomModel)item).registerModels();
         } else {
            ResourceLocation registryName = (ResourceLocation)Objects.requireNonNull(item.getRegistryName());
            ModelResourceLocation model = new ModelResourceLocation(registryName, "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, model);
         }
      }

   }

   public List<Block> getBlocks() {
      return this.blocks;
   }

   public List<Item> getItems() {
      return this.items;
   }

   @Nullable
   public CreativeTabs getDefaultCreativeTab() {
      return this.defaultCreativeTab;
   }

   public void setDefaultCreativeTab(@Nullable CreativeTabs defaultCreativeTab) {
      this.defaultCreativeTab = defaultCreativeTab;
   }

   public static class EventHandler {
      private final SRegistry sregistry;

      public EventHandler(SRegistry sregistry) {
         this.sregistry = sregistry;
      }

      private void runRegistrationHandlerIfPresent(Class<? extends IForgeRegistryEntry<?>> registryClass) {
         if (this.sregistry.registrationHandlers.containsKey(registryClass)) {
            ((Consumer)this.sregistry.registrationHandlers.get(registryClass)).accept(this.sregistry);
         }

      }

      public void register(EventBus bus) {
         bus.registerGeneric(Register.class, Block.class, this::registerBlocks);
         bus.registerGeneric(Register.class, Item.class, this::registerItems);
         bus.registerGeneric(Register.class, Potion.class, this::registerPotions);
         bus.registerGeneric(Register.class, Biome.class, this::registerBiomes);
         bus.registerGeneric(Register.class, SoundEvent.class, this::registerSoundEvents);
         bus.registerGeneric(Register.class, PotionType.class, this::registerPotionTypes);
         bus.registerGeneric(Register.class, Enchantment.class, this::registerEnchantments);
         bus.registerGeneric(Register.class, VillagerProfession.class, this::registerVillagerProfessions);
         bus.registerGeneric(Register.class, EntityEntry.class, this::registerEntities);
         bus.registerGeneric(Register.class, IRecipe.class, this::registerRecipes);
         bus.register(ModelRegistryEvent.class, this::registerModels);
         bus.register(net.minecraftforge.client.event.ColorHandlerEvent.Block.class, this::registerBlockColors);
         bus.register(net.minecraftforge.client.event.ColorHandlerEvent.Item.class, this::registerItemColors);
      }

      public void registerBlocks(Register<Block> event) {
         this.runRegistrationHandlerIfPresent(Block.class);
      }

      public void registerItems(Register<Item> event) {
         this.runRegistrationHandlerIfPresent(Item.class);
         this.sregistry.addOreDictEntries();
      }

      public void registerPotions(Register<Potion> event) {
         this.runRegistrationHandlerIfPresent(Potion.class);
      }

      public void registerBiomes(Register<Biome> event) {
         this.runRegistrationHandlerIfPresent(Biome.class);
      }

      public void registerSoundEvents(Register<SoundEvent> event) {
         this.runRegistrationHandlerIfPresent(SoundEvent.class);
      }

      public void registerPotionTypes(Register<PotionType> event) {
         this.runRegistrationHandlerIfPresent(PotionType.class);
      }

      public void registerEnchantments(Register<Enchantment> event) {
         this.runRegistrationHandlerIfPresent(Enchantment.class);
      }

      public void registerVillagerProfessions(Register<VillagerProfession> event) {
         this.runRegistrationHandlerIfPresent(VillagerProfession.class);
      }

      public void registerEntities(Register<EntityEntry> event) {
         this.runRegistrationHandlerIfPresent(EntityEntry.class);
      }

      public void registerRecipes(Register<IRecipe> event) {
         this.runRegistrationHandlerIfPresent(IRecipe.class);
         this.sregistry.addRecipes();
      }

      public void registerModels(ModelRegistryEvent event) {
         this.sregistry.registerModels();
      }

      @SideOnly(Side.CLIENT)
      public void registerBlockColors(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
         BlockColors blockColors = event.getBlockColors();

         for(Block block : this.sregistry.coloredBlocks) {
            blockColors.registerBlockColorHandler(((IColoredBlock)block).getColorHandler(), new Block[]{block});
         }

      }

      @SideOnly(Side.CLIENT)
      public void registerItemColors(net.minecraftforge.client.event.ColorHandlerEvent.Item event) {
         ItemColors itemColors = event.getItemColors();

         for(Block block : this.sregistry.coloredBlocks) {
            itemColors.registerItemColorHandler(((IColoredBlock)block).getItemColorHandler(), new Item[]{Item.getItemFromBlock(block)});
         }

         for(Item item : this.sregistry.coloredItems) {
            itemColors.registerItemColorHandler(((IColoredItem)item).getColorHandler(), new Item[]{item});
         }

      }
   }
}
