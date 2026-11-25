package com.endertech.minecraft.forge.regs;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.blocks.ForgeBlock;
import com.endertech.minecraft.forge.blocks.ForgeBlockContainer;
import com.endertech.minecraft.forge.items.ForgeStack;
import com.endertech.minecraft.forge.materials.CraftingRecipe;
import com.endertech.minecraft.forge.materials.SmeltingRecipe;
import com.endertech.minecraft.forge.messages.Connection;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.IRelatedUnit;
import com.endertech.minecraft.forge.units.IUnitWithMeshDefinition;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ForgeRegistrator {
   public static final String FORMAT_REGISTERED = "registered: <{}>";
   public static final String FORMAT_REMAINS_UNREGISTERED = "remains UNREGISTERED: <{}>";
   public static final String FORMAT_SKIPPED = "SKIPPED: <{}>";
   public static final String FORMAT_INVALID = "INVALID: <{}>";
   public static final String FORMAT_NOT_FOUND = "NOT FOUND: <{}>";
   public static final String FORMAT_ALREADY_REGISTERED = "ALREADY REGISTERED: <{}>";
   public static boolean registerDisabledUnits = true;
   private final ForgeMod mod;
   private final SimpleNetworkWrapper channel;
   private final Connection connection;
   protected int discriminator = 0;
   protected List<IForgeUnit> unregisteredUnits = new ArrayList();
   protected List<IForgeUnit> registeredUnits = new ArrayList();
   protected Map<CraftingRecipe, CraftingRecipe> craftings = new HashMap();
   protected List<SmeltingRecipe> smeltings = new ArrayList();
   protected List<IGuiHandler> guiHandlers = new ArrayList();
   protected List<IPostInit> postInits = new ArrayList();

   public ForgeRegistrator(ForgeMod mod) {
      this.mod = mod;
      this.channel = NetworkRegistry.INSTANCE.newSimpleChannel(mod.getId());
      this.connection = new Connection(this.channel);
   }

   public void addUnit(IForgeUnit unit) {
      if (unit.isEnabled() || registerDisabledUnits) {
         this.unregisteredUnits.add(unit);
      }

   }

   public void removeUnit(IForgeUnit unit) {
      this.unregisteredUnits.remove(unit);
   }

   public void addGuiHandler(IGuiHandler handler) {
      this.guiHandlers.add(handler);
   }

   public void addBlockCrafting(String itemId, String blockId) {
      CraftingRecipe recipe = new CraftingRecipe("111 111 111", blockId, 1, new String[]{itemId});
      this.addCrafting(recipe);
   }

   public void addBlockCrafting(IForgeUnit item, IForgeUnit block) {
      this.addBlockCrafting(item.getStringId(), block.getStringId());
   }

   public void addPostInit(IPostInit postInit) {
      this.postInits.add(postInit);
   }

   protected void registerGiuHandlers() {
      Iterator<IGuiHandler> iterator = this.guiHandlers.iterator();

      while(iterator.hasNext()) {
         IGuiHandler handler = (IGuiHandler)iterator.next();
         NetworkRegistry.INSTANCE.registerGuiHandler(this.mod, handler);
         iterator.remove();
      }

   }

   private Logger getLogger() {
      return this.mod.getLogger();
   }

   protected void registerRenders(Side side) {
      if (side == Side.CLIENT) {
         for(IForgeUnit unit : this.unregisteredUnits) {
            if (unit instanceof INeedRender) {
               ((INeedRender)unit).registerRender();
            }
         }
      }

   }

   protected void registerUnits(Side side) {
      String format = "registered: <{}> as {}";
      Iterator<IForgeUnit> iterator = this.unregisteredUnits.iterator();

      while(iterator.hasNext()) {
         IForgeUnit unit = (IForgeUnit)iterator.next();
         if (unit != null) {
            if (!unit.isReadyToRegister()) {
               this.getLogger().debug("SKIPPED: <{}>", unit);
               continue;
            }

            ResourceLocation location = unit.getId().toResLoc();
            if (unit instanceof Block) {
               Block block = (Block)unit;
               ForgeRegistries.BLOCKS.register(block);
               ItemBlock itemBlock = unit instanceof ForgeBlock ? ((ForgeBlock)block).createBlockItem() : new ItemBlock(block);
               ForgeRegistries.ITEMS.register(itemBlock.setRegistryName(location));
               if (side == Side.CLIENT) {
                  this.registerItemModel(itemBlock);
               }

               if (!(unit instanceof IRelatedUnit)) {
                  OreDictionary.registerOre(unit.getId().getDictName(), block);
               }

               if (unit instanceof ForgeBlockContainer) {
                  ForgeBlockContainer<?> container = (ForgeBlockContainer)unit;
                  this.registerTileEntity(container.getTileEntityClass(), container.getId().getRegName());
               }

               this.getLogger().debug("registered: <{}> as {}", unit.getId(), block.toString());
            } else if (unit instanceof Item) {
               Item item = (Item)unit;
               ForgeRegistries.ITEMS.register(item);
               if (side == Side.CLIENT) {
                  this.registerItemModel(item);
               }

               if (!(unit instanceof IRelatedUnit)) {
                  OreDictionary.registerOre(unit.getId().getDictName(), item);
               }

               this.getLogger().debug("registered: <{}> as {}", unit.getId(), item.toString());
            } else {
               this.getLogger().error("FAILED to register: '{}' is neither block nor item!", unit.getId());
            }

            if (unit.isRegistered()) {
               this.registeredUnits.add(unit);
            }
         } else {
            this.getLogger().error("SKIPPED: <{}>", IForgeUnit.getStatus(unit));
         }

         iterator.remove();
      }

   }

   public void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
      String unitName = UnitId.buildRegName(name, "tile");
      UnitId unitId = UnitId.from(this.mod.getId(), unitName);
      GameRegistry.registerTileEntity(clazz, unitId.toString());
   }

   public void addCrafting(CraftingRecipe recipe) {
      this.addCrafting(recipe, (CraftingRecipe)null);
   }

   public void addCrafting(CraftingRecipe main, CraftingRecipe reserve) {
      this.craftings.put(main, reserve);
   }

   public void addSmelting(SmeltingRecipe recipe) {
      this.smeltings.add(recipe);
   }

   protected void processInits(Side side) {
      for(Iterator<IForgeUnit> iterator = this.registeredUnits.iterator(); iterator.hasNext(); iterator.remove()) {
         IForgeUnit unit = (IForgeUnit)iterator.next();
         if (unit != null) {
            if (unit.isRegistered()) {
               unit.onInit();
               if (side == Side.CLIENT) {
                  unit.onInitClient();
               }
            } else {
               this.getLogger().error("Attempt to init NOT REGISTERED unit: {}", unit);
            }
         }
      }

   }

   public void onPreInit(FMLPreInitializationEvent event) {
      this.registerRenders(event.getSide());
      this.registerUnits(event.getSide());
   }

   public void onInit(FMLInitializationEvent event) {
      this.registerUnits(event.getSide());
      this.registerRecipes(false);
      this.registerGiuHandlers();
      this.processInits(event.getSide());
   }

   public void onPostInit(FMLPostInitializationEvent event) {
      this.registerUnits(event.getSide());
      this.registerRecipes(false);
      this.registerRecipes(true);
      this.processInits(event.getSide());

      for(IPostInit post : this.postInits) {
         post.onPostInit(event);
      }

      this.postInits.clear();

      for(IForgeUnit unit : this.unregisteredUnits) {
         this.getLogger().debug("remains UNREGISTERED: <{}>", unit);
      }

      for(Entry<CraftingRecipe, CraftingRecipe> entry : this.craftings.entrySet()) {
         this.getLogger().debug("remains UNREGISTERED: <{}>", "MAIN: " + entry.getKey() + ", RESERVE: " + entry.getValue());
      }

      for(SmeltingRecipe recipe : this.smeltings) {
         this.getLogger().debug("remains UNREGISTERED: <{}>", recipe);
      }

      this.unregisteredUnits.clear();
      this.registeredUnits.clear();
      this.craftings.clear();
      this.smeltings.clear();
   }

   public <M extends IMessage, H extends IMessageHandler<M, IMessage>> void registerNetMessageHandledOnClient(Class<M> msgClass, Class<H> handlerClass) {
      this.channel.registerMessage(handlerClass, msgClass, this.discriminator++, Side.CLIENT);
   }

   public <M extends IMessage, H extends IMessageHandler<M, IMessage>> void registerNetMessageHandledOnServer(Class<M> msgClass, Class<H> handlerClass) {
      this.channel.registerMessage(handlerClass, msgClass, this.discriminator++, Side.SERVER);
   }

   public <M extends IMessage, H extends IMessageHandler<M, IMessage>> void registerNetMessageHandledOnBothSides(Class<M> msgClass, Class<H> handlerClass) {
      this.registerNetMessageHandledOnClient(msgClass, handlerClass);
      this.registerNetMessageHandledOnServer(msgClass, handlerClass);
   }

   public void registerInEventBus(Object target) {
      MinecraftForge.EVENT_BUS.register(target);
   }

   public <T extends Entity> void registerRenderingHandler(Class<T> entityClass, IRenderFactory<? super T> renderFactory) {
      RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
   }

   protected void registerRecipes(boolean useReserve) {
      this.registerCraftings(useReserve);
      this.registerSmeltings();
   }

   protected void registerCraftings(boolean useReserve) {
      Iterator<Entry<CraftingRecipe, CraftingRecipe>> iterator = this.craftings.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<CraftingRecipe, CraftingRecipe> entry = (Entry)iterator.next();
         CraftingRecipe recipe = useReserve ? (CraftingRecipe)entry.getValue() : (CraftingRecipe)entry.getKey();
         if (recipe != null && recipe.isValid()) {
            if (recipe.isReadyToRegister()) {
               ForgeStack outputStack = recipe.getOutputStack();
               ResourceLocation name = new ResourceLocation(recipe.getId().getModId(), outputStack.toString());
               if (!recipe.isShapeless()) {
                  GameRegistry.addShapedRecipe(name, (ResourceLocation)null, outputStack.toItemStack(), recipe.getParams());
               } else {
                  List<Ingredient> ingredientsList = new ArrayList();

                  for(Object obj : recipe.getParams()) {
                     Ingredient ingredient = CraftingHelper.getIngredient(obj);
                     ingredientsList.add(ingredient);
                  }

                  GameRegistry.addShapelessRecipe(name, (ResourceLocation)null, outputStack.toItemStack(), (Ingredient[])ingredientsList.toArray(new Ingredient[0]));
               }

               iterator.remove();
            }
         } else if (recipe != null) {
            this.getLogger().error("INVALID: <{}>", recipe);
            iterator.remove();
         }
      }

   }

   protected void registerSmeltings() {
      Iterator<SmeltingRecipe> iterator = this.smeltings.iterator();

      while(iterator.hasNext()) {
         SmeltingRecipe recipe = (SmeltingRecipe)iterator.next();
         if (recipe != null && recipe.isValid()) {
            if (recipe.isReadyToRegister()) {
               GameRegistry.addSmelting(recipe.getInputStack().toItemStack(), recipe.getOutputStack().toItemStack(), recipe.getSmeltingData().getXp());
               iterator.remove();
            }
         } else if (recipe != null) {
            this.getLogger().error("INVALID: <{}>", recipe);
            iterator.remove();
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Item item) {
      IForgeUnit unit = null;
      if (item instanceof ItemBlock) {
         Block block = ((ItemBlock)item).getBlock();
         if (block instanceof IForgeUnit) {
            unit = (IForgeUnit)block;
         }
      }

      if (item instanceof IForgeUnit) {
         unit = (IForgeUnit)item;
      }

      if (unit != null) {
         if (unit instanceof IUnitWithMeshDefinition) {
            ItemMeshDefinition definition = ((IUnitWithMeshDefinition)unit).getItemMeshDefinition();
            ModelLoader.setCustomMeshDefinition(item, definition);
            if (unit.getUnitVariants().isEmpty()) {
               ModelBakery.registerItemVariants(item, new ResourceLocation[]{unit.getDefaultVariant().toModelResLoc()});
            } else {
               for(UnitVariant variant : unit.getUnitVariants().values()) {
                  ModelBakery.registerItemVariants(item, new ResourceLocation[]{variant.toModelResLoc()});
               }
            }
         } else if (unit.getUnitVariants().isEmpty()) {
            ModelLoader.setCustomModelResourceLocation(item, unit.getId().getMeta(), unit.getDefaultVariant().toModelResLoc());
         } else {
            for(UnitVariant variant : unit.getUnitVariants().values()) {
               ModelLoader.setCustomModelResourceLocation(item, variant.getId().getMeta(), variant.toModelResLoc());
            }
         }
      } else {
         UnitId id = UnitId.from(item);
         ModelLoader.setCustomModelResourceLocation(item, id.getMeta(), id.toModelResLoc_Inventory());
      }

   }

   public <T extends Entity> void registerEntity(Class<T> entityClass, int trackingRange, int updateFrequency, boolean sendVelocity) {
      String entityName = entityClass.getSimpleName();
      ResourceLocation registryName = UnitId.from(this.mod.getId(), entityName).toResLoc();
      EntityRegistry.registerModEntity(registryName, entityClass, entityName, this.discriminator++, this.mod, trackingRange, updateFrequency, sendVelocity);
   }

   public Connection getConnection() {
      return this.connection;
   }
}
