package vazkii.quark.decoration.feature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.BlockChest.Type;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import vazkii.arl.recipe.BlacklistOreIngredient;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.handler.RecipeProcessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCustomChest;
import vazkii.quark.decoration.client.render.RenderTileCustomChest;
import vazkii.quark.decoration.tile.TileCustomChest;
import vazkii.quark.oddities.client.bakery.WrapperWithParticleTexture;

public class VariedChests extends Feature {
   public static final Type CUSTOM_TYPE_QUARK = (Type)EnumHelper.addEnum(Type.class, "QUARK", new Class[0], new Object[0]);
   public static final Type CUSTOM_TYPE_QUARK_TRAP = (Type)EnumHelper.addEnum(Type.class, "QUARK_TRAP", new Class[0], new Object[0]);
   public static BlockCustomChest custom_chest;
   public static BlockCustomChest custom_chest_trap;
   public static boolean renameVanillaChests;
   public static boolean addLogRecipe;
   public static boolean reversionRecipe;
   private static final String DONK_CHEST = "Quark:DonkChest";
   private static Method initHorseChest;
   private static Method playChestEquipSound;
   private static final ThreadLocal<ItemStack> WAIT_TO_REPLACE_CHEST = new ThreadLocal();
   private static boolean fixedTrappedChest = false;

   public VariedChests() {
   }

   public void setupConfig() {
      renameVanillaChests = this.loadPropBool("Rename vanilla chests to Oak (Trapped) Chest", "", true);
      addLogRecipe = this.loadPropBool("Add recipe to craft chests using Logs (makes 4 chests)", "", true);
      reversionRecipe = this.loadPropBool("Enable Conversion to Vanilla Chests", "Set this to true to add a recipe to convert any Quark chest to a vanilla one.\nUse this if some of your mods don't work with the ore dictionary key \"chestWood\".", false);
   }

   public void preInit(FMLPreInitializationEvent event) {
      custom_chest = new BlockCustomChest("custom_chest", CUSTOM_TYPE_QUARK);
      custom_chest_trap = new BlockCustomChest("custom_chest_trap", CUSTOM_TYPE_QUARK_TRAP);
      registerTile(TileCustomChest.class, "quark_chest");
      ModIntegrationHandler.addCharsetCarry(custom_chest);
      ModIntegrationHandler.addCharsetCarry(custom_chest_trap);
      this.addOreDict();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onModelBake(ModelBakeEvent event) {
      this.overrideModel(custom_chest.getRegistryName(), event);
      this.overrideModel(custom_chest_trap.getRegistryName(), event);
   }

   @SideOnly(Side.CLIENT)
   private void overrideModel(ResourceLocation keyName, ModelBakeEvent event) {
      ModelResourceLocation key = new ModelResourceLocation(keyName, "normal");
      IBakedModel originalModel = (IBakedModel)event.getModelRegistry().getObject(key);
      event.getModelRegistry().putObject(key, new WrapperWithParticleTexture(event.getModelManager().getTextureMap().getAtlasSprite("minecraft:blocks/planks_oak"), originalModel));
   }

   @SubscribeEvent
   public void onClickEntity(EntityInteractSpecific event) {
      Entity target = event.getTarget();
      EntityPlayer player = event.getEntityPlayer();
      ItemStack held = player.getHeldItem(event.getHand());
      if (!held.isEmpty() && target instanceof AbstractChestHorse) {
         AbstractChestHorse horse = (AbstractChestHorse)target;
         if (!horse.hasChest() && held.getItem() != Item.getItemFromBlock(Blocks.CHEST)) {
            int oreId = OreDictionary.getOreID("chestWood");

            for(int checkAgainst : OreDictionary.getOreIDs(held)) {
               if (oreId == checkAgainst) {
                  event.setCanceled(true);
                  event.setCancellationResult(EnumActionResult.SUCCESS);
                  if (!target.world.isRemote) {
                     ItemStack copy = held.copy();
                     copy.setCount(1);
                     held.shrink(1);
                     horse.getEntityData().setTag("Quark:DonkChest", copy.serializeNBT());
                     horse.setChested(true);
                     if (initHorseChest == null) {
                        initHorseChest = ObfuscationReflectionHelper.findMethod(AbstractHorse.class, "initHorseChest", Void.TYPE, new Class[0]);
                     }

                     if (playChestEquipSound == null) {
                        playChestEquipSound = ObfuscationReflectionHelper.findMethod(AbstractChestHorse.class, "playChestEquipSound", Void.TYPE, new Class[0]);
                     }

                     try {
                        initHorseChest.invoke(horse);
                        playChestEquipSound.invoke(horse);
                     } catch (InvocationTargetException | IllegalAccessException var13) {
                        ;
                     }
                  }

                  return;
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onDeath(LivingDeathEvent event) {
      Entity target = event.getEntityLiving();
      if (target instanceof AbstractChestHorse) {
         AbstractChestHorse horse = (AbstractChestHorse)target;
         ItemStack chest = new ItemStack(horse.getEntityData().getCompoundTag("Quark:DonkChest"));
         if (!chest.isEmpty() && horse.hasChest()) {
            WAIT_TO_REPLACE_CHEST.set(chest);
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorld(EntityJoinWorldEvent event) {
      Entity target = event.getEntity();
      if (target instanceof EntityItem && ((EntityItem)target).getItem().getItem() == Item.getItemFromBlock(Blocks.CHEST)) {
         ItemStack local = (ItemStack)WAIT_TO_REPLACE_CHEST.get();
         if (local != null && !local.isEmpty()) {
            ((EntityItem)target).setItem(local);
         }

         WAIT_TO_REPLACE_CHEST.remove();
      }

   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileCustomChest.class, new RenderTileCustomChest());
      ProxyRegistry.getItemMapping(custom_chest).setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
         private final TileCustomChest chest = new TileCustomChest(VariedChests.CUSTOM_TYPE_QUARK);

         public void renderByItem(ItemStack stack, float partialTicks) {
            this.chest.chestType = VariedChests.custom_chest.getCustomType(stack);
            TileEntityRendererDispatcher.instance.render(this.chest, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
         }
      });
      ProxyRegistry.getItemMapping(custom_chest_trap).setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
         private final TileCustomChest chest = new TileCustomChest(VariedChests.CUSTOM_TYPE_QUARK_TRAP);

         public void renderByItem(ItemStack stack, float partialTicks) {
            this.chest.chestType = VariedChests.custom_chest_trap.getCustomType(stack);
            TileEntityRendererDispatcher.instance.render(this.chest, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
         }
      });
   }

   public void postPreInit() {
      if (renameVanillaChests) {
         Blocks.CHEST.setUnlocalizedName("oak_chest");
         Blocks.TRAPPED_CHEST.setUnlocalizedName("oak_chest_trap");
      }

      RecipeProcessor.addWoodReplacements(Blocks.CHEST);
      RecipeProcessor.addConsumer(VariedChests::fixTrappedChestRecipe);
      if (addLogRecipe) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.CHEST, 4), new Object[]{"WWW", "W W", "WWW", 'W', ProxyRegistry.newStack(Blocks.LOG)});
      }

      int i = 1;

      for(VariedChests.ChestType type : VariedChests.ChestType.VALID_TYPES) {
         ItemStack out = ProxyRegistry.newStack(custom_chest);
         custom_chest.setCustomType(out, type);
         RecipeHandler.addOreDictRecipe(out.copy(), new Object[]{"WWW", "W W", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i)});
         if (addLogRecipe) {
            ItemStack outFour = out.copy();
            outFour.setCount(4);
            RecipeHandler.addOreDictRecipe(outFour, new Object[]{"WWW", "W W", "WWW", 'W', ProxyRegistry.newStack(i > 3 ? Blocks.LOG2 : Blocks.LOG, 1, i % 4)});
         }

         ItemStack outTrap = ProxyRegistry.newStack(custom_chest_trap);
         custom_chest.setCustomType(outTrap, type);
         RecipeHandler.addShapelessOreDictRecipe(outTrap, new Object[]{out.copy(), ProxyRegistry.newStack(Blocks.TRIPWIRE_HOOK)});
         ++i;
      }

      Ingredient wood = new BlacklistOreIngredient("plankWood", (stack) -> {
         return stack.getItem() == Item.getItemFromBlock(Blocks.PLANKS);
      });
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.CHEST), new Object[]{"WWW", "W W", "WWW", 'W', wood});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.HOPPER), new Object[]{"I I", "ICI", " I ", 'I', Items.IRON_INGOT, 'C', "chestWood"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.PURPLE_SHULKER_BOX), new Object[]{"S", "C", "S", 'S', ProxyRegistry.newStack(Items.SHULKER_SHELL), 'C', "chest"});
      if (reversionRecipe) {
         RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Blocks.CHEST), new Object[]{"chestWood"});
         RecipeHandler.addShapelessOreDictRecipe(new ItemStack(Blocks.TRAPPED_CHEST), new Object[]{"chestTrapped"});
      }

   }

   private void addOreDict() {
      addOreDict("chest", ProxyRegistry.newStack(custom_chest, 1, 32767));
      addOreDict("chest", ProxyRegistry.newStack(custom_chest_trap, 1, 32767));
      addOreDict("chest", Blocks.CHEST);
      addOreDict("chest", Blocks.TRAPPED_CHEST);
      addOreDict("chestWood", ProxyRegistry.newStack(custom_chest, 1, 32767));
      addOreDict("chestWood", Blocks.CHEST);
      addOreDict("chestTrapped", ProxyRegistry.newStack(custom_chest_trap, 1, 32767));
      addOreDict("chestTrapped", Blocks.TRAPPED_CHEST);
   }

   private static void fixTrappedChestRecipe(IRecipe recipe) {
      if (!fixedTrappedChest) {
         if (Objects.toString(recipe.getRegistryName()).equals("minecraft:trapped_chest")) {
            List<Ingredient> ingredients = recipe.getIngredients();

            for(int i = 0; i < ingredients.size(); ++i) {
               Ingredient ingredient = (Ingredient)ingredients.get(i);
               if (ingredient instanceof OreIngredient) {
                  Ingredient chest = new BlacklistOreIngredient("chestWood", (stack) -> {
                     return stack.getItem() == Item.getItemFromBlock(custom_chest);
                  });
                  ingredients.set(i, chest);
                  break;
               }
            }

            fixedTrappedChest = true;
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static enum ChestType {
      NONE(""),
      SPRUCE("spruce"),
      BIRCH("birch"),
      JUNGLE("jungle"),
      ACACIA("acacia"),
      DARK_OAK("dark_oak");

      public final String name;
      public final ResourceLocation nrmTex;
      public final ResourceLocation dblTex;
      public final ResourceLocation nrmTrapTex;
      public final ResourceLocation dblTrapTex;
      public static final VariedChests.ChestType[] VALID_TYPES = new VariedChests.ChestType[]{SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK};
      public static final Map<String, VariedChests.ChestType> NAME_TO_TYPE = new HashMap();

      private ChestType(String name) {
         this.name = name;
         this.nrmTex = new ResourceLocation("quark:textures/blocks/chests/" + name + ".png");
         this.dblTex = new ResourceLocation("quark:textures/blocks/chests/" + name + "_double.png");
         this.nrmTrapTex = new ResourceLocation("quark:textures/blocks/chests/" + name + "_trap.png");
         this.dblTrapTex = new ResourceLocation("quark:textures/blocks/chests/" + name + "_trap_double.png");
      }

      public static VariedChests.ChestType getType(String type) {
         return (VariedChests.ChestType)NAME_TO_TYPE.getOrDefault(type, NONE);
      }

      static {
         for(VariedChests.ChestType type : VALID_TYPES) {
            NAME_TO_TYPE.put(type.name, type);
         }

      }
   }
}
