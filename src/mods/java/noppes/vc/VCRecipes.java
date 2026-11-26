package noppes.vc;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class VCRecipes {
   public static List<VCRecipes.RecipeContainer> List = new ArrayList();
   private static IForgeRegistry<IRecipe> registry;

   public VCRecipes() {
   }

   public void addRecipe(VCRecipes.RecipeContainer r, Object ob, Object... recipe) {
      ItemStack item;
      if (ob instanceof Item) {
         item = new ItemStack((Item)ob);
      } else if (ob instanceof Block) {
         item = new ItemStack((Block)ob);
      } else {
         item = (ItemStack)ob;
      }

      r.add(item, recipe);
   }

   public void addWoodenRecipes(Object ob, Object... recipe) {
      VCRecipes.RecipeContainer r = new VCRecipes.RecipeContainer();
      ItemStack item;
      if (ob instanceof Item) {
         item = new ItemStack((Item)ob);
      } else if (ob instanceof Block) {
         item = new ItemStack((Block)ob);
      } else {
         item = (ItemStack)ob;
      }

      for(int i = 0; i < 6; ++i) {
         ItemStack stack = item.copy();
         stack.setItemDamage(i);
         Object[] rec = Arrays.copyOf(recipe, recipe.length);

         for(int j = 0; j < rec.length; ++j) {
            if (rec[j] == null) {
               rec[j] = new ItemStack(Blocks.PLANKS, 1, i);
            }
         }

         r.add(stack, rec);
      }

   }

   public void addRecipes(Object... recipe) {
      VCRecipes.RecipeContainer r = new VCRecipes.RecipeContainer();
      Map<ItemStack, Object> obs = new HashMap();

      int i;
      for(i = 0; i < recipe.length; i += 2) {
         Object ob = recipe[i];
         ItemStack item;
         if (ob instanceof Item) {
            item = new ItemStack((Item)ob);
         } else if (ob instanceof Block) {
            item = new ItemStack((Block)ob);
         } else {
            if (!(ob instanceof ItemStack)) {
               break;
            }

            item = (ItemStack)ob;
         }

         obs.put(item, recipe[i + 1]);
      }

      recipe = Arrays.copyOfRange(recipe, i, recipe.length);

      for(Entry<ItemStack, Object> ent : obs.entrySet()) {
         Object[] rec = Arrays.copyOf(recipe, recipe.length);

         for(int j = 0; j < rec.length; ++j) {
            if (rec[j] == null) {
               rec[j] = ent.getValue();
            }
         }

         r.add((ItemStack)ent.getKey(), rec);
      }

   }

   public void addSmelting(Object ob, ItemStack output, float experience) {
      ItemStack item;
      if (ob instanceof Item) {
         item = new ItemStack((Item)ob);
      } else if (ob instanceof Block) {
         item = new ItemStack((Block)ob);
      } else {
         item = (ItemStack)ob;
      }

      FurnaceRecipes.instance().addSmeltingRecipe(item, output, experience);
      VCRecipes.RecipeContainer c = new VCRecipes.RecipeContainer();
      c.furnace(item, output);
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, IRecipe.class, this::registerRecipes);
   }

   public void registerRecipes(Register<IRecipe> event) {
      List.clear();
      registry = event.getRegistry();
      this.loadDefaultRecipes();
   }

   public void loadDefaultRecipes() {
      registry.register((new VCRecipes.RecipeReverse()).setRegistryName("variedcommodities", "reverse_items"));
      this.addSmelting(Items.IRON_INGOT, new ItemStack(VCItems.ingot_steel), 0.35F);
      this.addRecipe(new VCRecipes.RecipeContainer(), VCItems.vcrecipes_book, "XX", 'X', Items.BOOK);
      this.addRecipe(new VCRecipes.RecipeContainer(), VCBlocks.carpentry_bench, "XYX", "Z Z", "Z Z", 'X', Blocks.PLANKS, 'Z', Items.STICK, 'Y', Blocks.CRAFTING_TABLE);
      this.addRecipe(new VCRecipes.RecipeContainer(), VCWeapons.mana, "XY", 'X', Items.REDSTONE, 'Y', Items.GLOWSTONE_DUST);
      this.addRecipe(new VCRecipes.RecipeContainer(), new ItemStack(VCWeapons.bullet, 4), "X", 'X', Items.IRON_INGOT);
      this.addRecipes(new ItemStack(VCBlocks.wall_banner, 1, 0), Blocks.PLANKS, new ItemStack(VCBlocks.wall_banner, 1, 1), Blocks.STONE, new ItemStack(VCBlocks.wall_banner, 1, 2), Items.IRON_INGOT, new ItemStack(VCBlocks.wall_banner, 1, 3), Items.GOLD_INGOT, new ItemStack(VCBlocks.wall_banner, 1, 4), Items.DIAMOND, "XXX", "ZZZ", "ZZZ", "Z Z", 'Z', Blocks.WOOL, 'X', null);
      VCRecipes.RecipeContainer r = new VCRecipes.RecipeContainer();
      this.addRecipe(r, new ItemStack(VCBlocks.banner, 1, 0), " X ", " Z ", " Z ", "ZZZ", 'X', new ItemStack(VCBlocks.wall_banner, 1, 0), 'Z', Blocks.PLANKS);
      this.addRecipe(r, new ItemStack(VCBlocks.banner, 1, 1), " X ", " Z ", " Z ", "ZZZ", 'X', new ItemStack(VCBlocks.wall_banner, 1, 1), 'Z', Blocks.STONE);
      this.addRecipe(r, new ItemStack(VCBlocks.banner, 1, 2), " X ", " Z ", " Z ", "ZZZ", 'X', new ItemStack(VCBlocks.wall_banner, 1, 2), 'Z', Items.IRON_INGOT);
      this.addRecipe(r, new ItemStack(VCBlocks.banner, 1, 3), " X ", " Z ", " Z ", "ZZZ", 'X', new ItemStack(VCBlocks.wall_banner, 1, 3), 'Z', Items.GOLD_INGOT);
      this.addRecipe(r, new ItemStack(VCBlocks.banner, 1, 4), " X ", " Z ", " Z ", "ZZZ", 'X', new ItemStack(VCBlocks.wall_banner, 1, 4), 'Z', Items.DIAMOND);
      this.addRecipes(new ItemStack(VCBlocks.tall_lamp, 1, 0), Blocks.PLANKS, new ItemStack(VCBlocks.tall_lamp, 1, 1), Blocks.STONE, new ItemStack(VCBlocks.tall_lamp, 1, 2), Items.IRON_INGOT, new ItemStack(VCBlocks.tall_lamp, 1, 3), Items.GOLD_INGOT, new ItemStack(VCBlocks.tall_lamp, 1, 4), Items.DIAMOND, "YXY", " Z ", " Z ", "ZZZ", 'X', Blocks.TORCH, 'Y', Blocks.WOOL, 'Z', null);
      this.addWoodenRecipes(VCBlocks.chair, "  X", "  X", "XXX", "X X", 'X', null);
      this.addWoodenRecipes(VCBlocks.crate, "XXXX", "X  X", "X  X", "XXXX", 'X', null);
      this.addWoodenRecipes(VCBlocks.weapon_rack, "XXX", "XYX", "XYX", "XXX", 'X', null, 'Y', Items.STICK);
      this.addWoodenRecipes(VCBlocks.couch_wood, "   X", "   X", "XXXX", "X  X", 'X', null);
      this.addWoodenRecipes(VCBlocks.couch_wool, "  ZX", "  ZX", "ZZZX", "XXXX", 'X', null, 'Z', Blocks.WOOL);
      this.addWoodenRecipes(VCBlocks.table, "XXXX", "X  X", "X  X", "X  X", 'X', null);
      this.addWoodenRecipes(VCBlocks.stool, "XXXX", " XX ", "X  X", 'X', null);
      this.addWoodenRecipes(VCBlocks.barrel, "XXX", "X X", "X X", "XXX", 'X', null);
      this.addWoodenRecipes(new ItemStack(VCBlocks.shelf, 3), "XXXX", "X Y ", "XY  ", 'Y', Items.STICK, 'X', null);
      this.addWoodenRecipes(new ItemStack(VCBlocks.beam, 2), "XXXX", 'X', null);
      this.addWoodenRecipes(new ItemStack(VCBlocks.sign, 3), "YYY", "XXX", "XXX", 'X', null, 'Y', Items.IRON_INGOT);
      this.addRecipe(new VCRecipes.RecipeContainer(), new ItemStack(VCBlocks.lamp), "XXX", "Y Y", "YZY", "XXX", 'X', Items.IRON_INGOT, 'Y', Blocks.GLASS, 'Z', Blocks.TORCH);
      this.addRecipe(new VCRecipes.RecipeContainer(), new ItemStack(VCBlocks.candle), "XZX", " X ", 'X', Items.IRON_INGOT, 'Z', Blocks.TORCH);
      this.addRecipe(new VCRecipes.RecipeContainer(), new ItemStack(VCBlocks.big_sign, 2), "XXX", "XXX", "XXX", 'X', Blocks.PLANKS);
      this.addRecipe(new VCRecipes.RecipeContainer(), new ItemStack(VCWeapons.holy_hand_grenade), " X ", "XYX", " X ", 'X', Items.RABBIT_FOOT, 'Y', Blocks.TNT);
      this.addRecipes(VCWeapons.wooden_gun, Blocks.PLANKS, VCWeapons.stone_gun, Blocks.STONE, VCWeapons.iron_gun, Items.IRON_INGOT, VCWeapons.golden_gun, Items.GOLD_INGOT, VCWeapons.diamond_gun, Items.DIAMOND, VCWeapons.emerald_gun, Items.EMERALD, VCWeapons.bronze_gun, VCItems.ingot_bronze, "XXXY", " ZM ", "  M ", 'Y', Blocks.LEVER, 'M', Items.STICK, 'Z', Blocks.STONE_BUTTON, 'X', null);
      this.addRecipes(VCWeapons.wooden_battleaxe, Blocks.PLANKS, VCWeapons.stone_battleaxe, Blocks.STONE, VCWeapons.iron_battleaxe, Items.IRON_INGOT, VCWeapons.golden_battleaxe, Items.GOLD_INGOT, VCWeapons.diamond_battleaxe, Items.DIAMOND, VCWeapons.bronze_battleaxe, VCItems.ingot_bronze, VCWeapons.emerald_battleaxe, Items.EMERALD, VCWeapons.mithril_battleaxe, VCItems.ingot_mithril, VCWeapons.frost_battleaxe, Blocks.PACKED_ICE, VCWeapons.demonic_battleaxe, VCItems.ingot_demonic, "XX", "XY", " Y", " Y", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_halberd, Blocks.PLANKS, VCWeapons.stone_halberd, Blocks.STONE, VCWeapons.iron_halberd, Items.IRON_INGOT, VCWeapons.golden_halberd, Items.GOLD_INGOT, VCWeapons.diamond_halberd, Items.DIAMOND, VCWeapons.bronze_halberd, VCItems.ingot_bronze, VCWeapons.emerald_halberd, Items.EMERALD, VCWeapons.mithril_halberd, VCItems.ingot_mithril, VCWeapons.frost_halberd, Blocks.PACKED_ICE, VCWeapons.demonic_halberd, VCItems.ingot_demonic, "XYX", "XY ", " Y ", " Y ", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_glaive, Blocks.PLANKS, VCWeapons.stone_glaive, Blocks.STONE, VCWeapons.iron_glaive, Items.IRON_INGOT, VCWeapons.golden_glaive, Items.GOLD_INGOT, VCWeapons.diamond_glaive, Items.DIAMOND, VCWeapons.bronze_glaive, VCItems.ingot_bronze, VCWeapons.emerald_glaive, Items.EMERALD, VCWeapons.mithril_glaive, VCItems.ingot_mithril, VCWeapons.frost_glaive, Blocks.PACKED_ICE, VCWeapons.demonic_glaive, VCItems.ingot_demonic, "   X", "  Y ", " Y  ", "X   ", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_trident, Blocks.PLANKS, VCWeapons.stone_trident, Blocks.STONE, VCWeapons.iron_trident, Items.IRON_INGOT, VCWeapons.golden_trident, Items.GOLD_INGOT, VCWeapons.diamond_trident, Items.DIAMOND, VCWeapons.bronze_trident, VCItems.ingot_bronze, VCWeapons.emerald_trident, Items.EMERALD, VCWeapons.mithril_trident, VCItems.ingot_mithril, VCWeapons.frost_trident, Blocks.PACKED_ICE, VCWeapons.demonic_trident, VCItems.ingot_demonic, "X X", " X ", " Y ", " Y ", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_scythe, Blocks.PLANKS, VCWeapons.stone_scythe, Blocks.STONE, VCWeapons.iron_scythe, Items.IRON_INGOT, VCWeapons.golden_scythe, Items.GOLD_INGOT, VCWeapons.diamond_scythe, Items.DIAMOND, VCWeapons.bronze_scythe, VCItems.ingot_bronze, VCWeapons.emerald_scythe, Items.EMERALD, VCWeapons.mithril_scythe, VCItems.ingot_mithril, VCWeapons.frost_scythe, Blocks.PACKED_ICE, VCWeapons.demonic_scythe, VCItems.ingot_demonic, "XXX", "  Y", "  Y", "  Y", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_warhammer, Blocks.PLANKS, VCWeapons.stone_warhammer, Blocks.STONE, VCWeapons.iron_warhammer, Items.IRON_INGOT, VCWeapons.golden_warhammer, Items.GOLD_INGOT, VCWeapons.diamond_warhammer, Items.DIAMOND, VCWeapons.bronze_warhammer, VCItems.ingot_bronze, VCWeapons.emerald_warhammer, Items.EMERALD, VCWeapons.mithril_warhammer, VCItems.ingot_mithril, VCWeapons.frost_warhammer, Blocks.PACKED_ICE, VCWeapons.demonic_warhammer, VCItems.ingot_demonic, "XXX", " Y ", " Y ", " Y ", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_spear, Blocks.PLANKS, VCWeapons.stone_spear, Blocks.STONE, VCWeapons.iron_spear, Items.IRON_INGOT, VCWeapons.golden_spear, Items.GOLD_INGOT, VCWeapons.diamond_spear, Items.DIAMOND, VCWeapons.bronze_spear, VCItems.ingot_bronze, VCWeapons.emerald_spear, Items.EMERALD, VCWeapons.mithril_spear, VCItems.ingot_mithril, VCWeapons.frost_spear, Blocks.PACKED_ICE, VCWeapons.demonic_spear, VCItems.ingot_demonic, "X", "Y", "Y", "Y", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_broadsword, Blocks.PLANKS, VCWeapons.stone_broadsword, Blocks.STONE, VCWeapons.iron_broadsword, Items.IRON_INGOT, VCWeapons.golden_broadsword, Items.GOLD_INGOT, VCWeapons.diamond_broadsword, Items.DIAMOND, VCWeapons.bronze_broadsword, VCItems.ingot_bronze, VCWeapons.emerald_broadsword, Items.EMERALD, VCWeapons.mithril_broadsword, VCItems.ingot_mithril, VCWeapons.frost_broadsword, Blocks.PACKED_ICE, VCWeapons.demonic_broadsword, VCItems.ingot_demonic, "X", "X", "X", "Y", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_dagger, Blocks.PLANKS, VCWeapons.stone_dagger, Blocks.STONE, VCWeapons.iron_dagger, Items.IRON_INGOT, VCWeapons.golden_dagger, Items.GOLD_INGOT, VCWeapons.diamond_dagger, Items.DIAMOND, VCWeapons.bronze_dagger, VCItems.ingot_bronze, VCWeapons.emerald_dagger, Items.EMERALD, VCWeapons.mithril_dagger, VCItems.ingot_mithril, VCWeapons.frost_dagger, Blocks.PACKED_ICE, VCWeapons.demonic_dagger, VCItems.ingot_demonic, " X", "Y ", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.bronze_sword, VCItems.ingot_bronze, VCWeapons.emerald_sword, Items.EMERALD, VCWeapons.mithril_sword, VCItems.ingot_mithril, VCWeapons.frost_sword, Blocks.PACKED_ICE, VCWeapons.demonic_sword, VCItems.ingot_demonic, "X", "X", "Y", 'Y', Items.STICK, 'X', null);
      this.addRecipes(VCWeapons.wooden_shield_round, Blocks.PLANKS, VCWeapons.stone_shield_round, Blocks.STONE, VCWeapons.iron_shield_round, Items.IRON_INGOT, VCWeapons.golden_shield_round, Items.GOLD_INGOT, VCWeapons.diamond_shield_round, Items.DIAMOND, VCWeapons.bronze_shield_round, VCItems.ingot_bronze, VCWeapons.emerald_shield_round, Items.EMERALD, VCWeapons.mithril_shield_round, VCItems.ingot_mithril, VCWeapons.demonic_shield_round, VCItems.ingot_demonic, " XX ", "XXXX", "XXXX", " XX ", 'X', null);
      this.addRecipes(VCWeapons.wooden_shield, Blocks.PLANKS, VCWeapons.stone_shield, Blocks.STONE, VCWeapons.iron_shield, Items.IRON_INGOT, VCWeapons.golden_shield, Items.GOLD_INGOT, VCWeapons.diamond_shield, Items.DIAMOND, VCWeapons.bronze_shield, VCItems.ingot_bronze, VCWeapons.emerald_shield, Items.EMERALD, VCWeapons.frost_shield, Blocks.PACKED_ICE, "X  X", "XXXX", "XXXX", " XX ", 'X', null);
      this.addRecipes(VCArmors.chain_skirt, VCItems.ingot_bronze, VCArmors.leather_skirt, Items.LEATHER, VCArmors.iron_skirt, Items.IRON_INGOT, VCArmors.golden_skirt, Items.GOLD_INGOT, VCArmors.diamond_skirt, Items.DIAMOND, VCArmors.bronze_skirt, VCItems.ingot_bronze, VCArmors.emerald_skirt, Items.EMERALD, VCArmors.mithril_skirt, VCItems.ingot_mithril, VCArmors.demonic_skirt, VCItems.ingot_demonic, " XX ", "XXXX", 'X', null);
      this.addRecipes(VCArmors.mithril_head, VCItems.ingot_mithril, VCArmors.demonic_head, VCItems.ingot_demonic, "XXX", "X X", 'X', null);
      this.addRecipes(VCArmors.mithril_chest, VCItems.ingot_mithril, VCArmors.demonic_chest, VCItems.ingot_demonic, "X X", "XXX", "XXX", 'X', null);
      this.addRecipes(VCArmors.mithril_legs, VCItems.ingot_mithril, VCArmors.demonic_legs, VCItems.ingot_demonic, VCArmors.full_emerald_legs, Items.EMERALD, VCArmors.full_bronze_legs, VCItems.ingot_bronze, "XXX", "X X", "X X", 'X', null);
      this.addRecipes(VCArmors.mithril_boots, VCItems.ingot_mithril, VCArmors.demonic_boots, VCItems.ingot_demonic, VCArmors.full_emerald_boots, Items.EMERALD, VCArmors.full_bronze_boots, VCItems.ingot_bronze, "X X", "X X", 'X', null);
      this.addRecipes(VCArmors.full_leather_head, Items.LEATHER, VCArmors.full_iron_head, Items.IRON_INGOT, VCArmors.full_golden_head, Items.GOLD_INGOT, VCArmors.full_diamond_head, Items.DIAMOND, VCArmors.full_bronze_head, VCItems.ingot_bronze, VCArmors.full_emerald_head, Items.EMERALD, VCArmors.full_wooden_head, Blocks.LOG, VCArmors.full_wooden_head, Blocks.LOG2, "XXXX", "X  X", 'X', null);
      this.addRecipes(VCArmors.full_leather_chest, Items.LEATHER, VCArmors.full_iron_chest, Items.IRON_INGOT, VCArmors.full_golden_chest, Items.GOLD_INGOT, VCArmors.full_diamond_chest, Items.DIAMOND, VCArmors.full_bronze_chest, VCItems.ingot_bronze, VCArmors.full_emerald_chest, Items.EMERALD, VCArmors.full_wooden_chest, Blocks.LOG, VCArmors.full_wooden_chest, Blocks.LOG2, "X X", "XXX", "XXX", "XXX", 'X', null);
   }

   public static IRecipe match(InventoryCrafting inv, World worldIn) {
      for(IRecipe irecipe : registry.getValues()) {
         if (irecipe instanceof ShapedRecipes) {
            ShapedRecipes recipe = (ShapedRecipes)irecipe;

            for(int i = 0; i <= 4 - recipe.recipeWidth; ++i) {
               for(int j = 0; j <= 4 - recipe.recipeHeight; ++j) {
                  if (checkMatch(recipe, inv, i, j, true) || checkMatch(recipe, inv, i, j, false)) {
                     return irecipe;
                  }
               }
            }
         } else if (!(irecipe instanceof ShapedOreRecipe)) {
            if (irecipe.matches(inv, worldIn)) {
               return irecipe;
            }
         } else {
            ShapedOreRecipe recipe = (ShapedOreRecipe)irecipe;

            for(int i = 0; i <= 4 - recipe.getWidth(); ++i) {
               for(int j = 0; j <= 4 - recipe.getHeight(); ++j) {
                  if (checkMatch(recipe, inv, i, j, true) || checkMatch(recipe, inv, i, j, false)) {
                     return irecipe;
                  }
               }
            }
         }
      }

      return null;
   }

   private static boolean checkMatch(ShapedOreRecipe recipe, InventoryCrafting inv, int startX, int startY, boolean mirror) {
      for(int x = 0; x < 4; ++x) {
         for(int y = 0; y < 4; ++y) {
            int subX = x - startX;
            int subY = y - startY;
            Ingredient target = Ingredient.EMPTY;
            if (subX >= 0 && subY >= 0 && subX < recipe.getWidth() && subY < recipe.getHeight()) {
               if (mirror) {
                  target = (Ingredient)recipe.getIngredients().get(recipe.getWidth() - subX - 1 + subY * recipe.getWidth());
               } else {
                  target = (Ingredient)recipe.getIngredients().get(subX + subY * recipe.getWidth());
               }
            }

            if (!target.apply(inv.getStackInRowAndColumn(x, y))) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean checkMatch(ShapedRecipes recipe, InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 4; ++j) {
            int k = i - p_77573_2_;
            int l = j - p_77573_3_;
            Ingredient ingredient = Ingredient.EMPTY;
            if (k >= 0 && l >= 0 && k < recipe.recipeWidth && l < recipe.recipeHeight) {
               if (p_77573_4_) {
                  ingredient = (Ingredient)recipe.recipeItems.get(recipe.recipeWidth - k - 1 + l * recipe.recipeWidth);
               } else {
                  ingredient = (Ingredient)recipe.recipeItems.get(k + l * recipe.recipeWidth);
               }
            }

            if (!ingredient.apply(p_77573_1_.getStackInRowAndColumn(i, j))) {
               return false;
            }
         }
      }

      return true;
   }

   public class RecipeContainer {
      private LinkedHashMap<ItemStack, List<ItemStack>> items = new LinkedHashMap();
      public int width = 1;
      public int height = 1;
      public boolean isFurnace = false;

      public RecipeContainer() {
         VCRecipes.List.add(this);
      }

      public void add(ItemStack output, Object[] params) {
         ShapedPrimer primer = CraftingHelper.parseShaped(params);
         IRecipe r = (IRecipe)(new ShapedRecipes("variedcommodities", primer.width, primer.height, primer.input, output)).setRegistryName(output.getItem().getRegistryName() + "_" + output.getItemDamage());
         if (r.getRecipeOutput() != null && !r.getRecipeOutput().isEmpty()) {
            List<ItemStack> input = new ArrayList();
            if (r instanceof ShapedRecipes) {
               ShapedRecipes recipe = (ShapedRecipes)r;
               this.width = recipe.recipeWidth;
               this.height = recipe.recipeHeight;

               for(Ingredient in : recipe.recipeItems) {
                  if (in == Ingredient.EMPTY) {
                     input.add(ItemStack.EMPTY);
                  } else {
                     input.add(in.getMatchingStacks()[0]);
                  }
               }
            }

            this.items.put(r.getRecipeOutput(), input);
            VCRecipes.registry.register(r);
         }
      }

      public void furnace(ItemStack input, ItemStack output) {
         this.isFurnace = true;
         this.add(input, output);
      }

      public void add(ItemStack input, ItemStack output) {
         List<ItemStack> l = new ArrayList();
         l.add(input);
         this.items.put(output, l);
      }

      public ItemStack output() {
         int i = 0;
         if (this.items.size() > 1) {
            i = (int)(System.currentTimeMillis() / 2000L % (long)this.items.size());
         }

         for(Entry<ItemStack, List<ItemStack>> entry : this.items.entrySet()) {
            if (i == 0) {
               return (ItemStack)entry.getKey();
            }

            --i;
         }

         return null;
      }

      public List<ItemStack> input() {
         int i = 0;
         if (this.items.size() > 1) {
            i = (int)(System.currentTimeMillis() / 2000L % (long)this.items.size());
         }

         for(Entry<ItemStack, List<ItemStack>> entry : this.items.entrySet()) {
            if (i == 0) {
               return (List)entry.getValue();
            }

            --i;
         }

         return null;
      }

      public boolean isFurnace() {
         return this.isFurnace;
      }
   }

   class RecipeReverse extends Impl<IRecipe> implements IRecipe {
      private final Item[] items = new Item[]{VCWeapons.wooden_dagger, VCWeapons.wooden_dagger_reversed, VCWeapons.stone_dagger, VCWeapons.stone_dagger_reversed, VCWeapons.iron_dagger, VCWeapons.iron_dagger_reversed, VCWeapons.golden_dagger, VCWeapons.golden_dagger_reversed, VCWeapons.diamond_dagger, VCWeapons.diamond_dagger_reversed, VCWeapons.bronze_dagger, VCWeapons.bronze_dagger_reversed, VCWeapons.emerald_dagger, VCWeapons.emerald_dagger_reversed, VCWeapons.demonic_dagger, VCWeapons.demonic_dagger_reversed, VCWeapons.frost_dagger, VCWeapons.frost_dagger_reversed, VCWeapons.mithril_dagger, VCWeapons.mithril_dagger_reversed};

      public RecipeReverse() {
         VCRecipes.RecipeContainer c = VCRecipes.this.new RecipeContainer();

         for(int i = 0; i < this.items.length; i += 2) {
            c.add(new ItemStack(this.items[i]), new ItemStack(this.items[i + 1]));
            c.add(new ItemStack(this.items[i + 1]), new ItemStack(this.items[i]));
         }

      }

      public boolean matches(InventoryCrafting inv, World worldIn) {
         return !this.getCraftingResult(inv).isEmpty();
      }

      public ItemStack getCraftingResult(InventoryCrafting inv) {
         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            for(int j = 0; j < this.items.length; ++j) {
               if (this.items[j] == itemstack.getItem()) {
                  return CommonUtils.ChangeItemStack(itemstack, this.items[j + (j % 2 == 1 ? -1 : 1)]);
               }
            }
         }

         return ItemStack.EMPTY;
      }

      public ItemStack getRecipeOutput() {
         return ItemStack.EMPTY;
      }

      public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
         NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

         for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, ForgeHooks.getContainerItem(itemstack));
         }

         return nonnulllist;
      }

      public boolean canFit(int width, int height) {
         return width == 1 && height == 1;
      }
   }
}
