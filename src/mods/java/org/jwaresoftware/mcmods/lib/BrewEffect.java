package org.jwaresoftware.mcmods.lib;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.brewing.PotionBrewEvent.Post;

public enum BrewEffect {
   goldenCarrotEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.NIGHT_VISION);
      }
   },
   goldenCarrotEffectII {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.LONG_NIGHT_VISION);
      }
   },
   blazePowderEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.STRENGTH);
      }
   },
   blazePowderEffectII {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.STRONG_STRENGTH);
      }
   },
   ghastTearEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.REGENERATION);
         PotionHelper.addMix(PotionTypes.THICK, is_ingredient, PotionTypes.HEALING);
      }
   },
   ghastTearEffectII {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.STRONG_REGENERATION);
         PotionHelper.addMix(PotionTypes.THICK, is_ingredient, PotionTypes.STRONG_HEALING);
      }
   },
   gunpowderEffect {
      public void register(Item item) {
         PotionHelper.addContainerRecipe(Items.POTIONITEM, item, Items.SPLASH_POTION);
      }

      public void register(Ingredient is_ingredient) {
      }
   },
   spiderEyeEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.WATER, is_ingredient, PotionTypes.POISON);
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.POISON);
      }
   },
   spiderEyeEffectII {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.WATER, is_ingredient, PotionTypes.STRONG_POISON);
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.STRONG_POISON);
         PotionHelper.addMix(PotionTypes.POISON, is_ingredient, PotionTypes.STRONG_POISON);
         PotionHelper.addMix(PotionTypes.THICK, is_ingredient, PotionTypes.STRONG_POISON);
      }
   },
   fermentedSpiderEyeEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.WATER, is_ingredient, PotionTypes.WEAKNESS);
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.HARMING);
         PotionHelper.addMix(PotionTypes.HEALING, is_ingredient, PotionTypes.HARMING);
         PotionHelper.addMix(PotionTypes.STRONG_HEALING, is_ingredient, PotionTypes.STRONG_HARMING);
         PotionHelper.addMix(PotionTypes.SWIFTNESS, is_ingredient, PotionTypes.SLOWNESS);
         PotionHelper.addMix(PotionTypes.LONG_SWIFTNESS, is_ingredient, PotionTypes.LONG_SLOWNESS);
         PotionHelper.addMix(PotionTypes.STRONG_SWIFTNESS, is_ingredient, PotionTypes.LONG_SLOWNESS);
         PotionHelper.addMix(PotionTypes.STRENGTH, is_ingredient, PotionTypes.WEAKNESS);
         PotionHelper.addMix(PotionTypes.STRONG_STRENGTH, is_ingredient, PotionTypes.LONG_WEAKNESS);
         PotionHelper.addMix(PotionTypes.NIGHT_VISION, is_ingredient, PotionTypes.INVISIBILITY);
      }
   },
   magmaCreamEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.FIRE_RESISTANCE);
      }
   },
   magmaCreamEffectII {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.LONG_FIRE_RESISTANCE);
      }
   },
   dragonbreathEffect {
      public void register(Item item) {
         PotionHelper.addContainerRecipe(Items.SPLASH_POTION, item, Items.LINGERING_POTION);
      }

      public void register(Ingredient is_ingredient) {
      }
   },
   dragonbreathEffectII {
      public void register(Item item) {
         PotionHelper.addContainerRecipe(Items.POTIONITEM, item, Items.LINGERING_POTION);
         PotionHelper.addContainerRecipe(Items.SPLASH_POTION, item, Items.LINGERING_POTION);
      }

      public void register(Ingredient is_ingredient) {
      }
   },
   sugarbombEffect {
      public void register(Ingredient is_ingredient) {
         PotionHelper.addMix(PotionTypes.WATER, is_ingredient, PotionTypes.LONG_SWIFTNESS);
         PotionHelper.addMix(PotionTypes.AWKWARD, is_ingredient, PotionTypes.LONG_SWIFTNESS);
         PotionHelper.addMix(PotionTypes.THICK, is_ingredient, PotionTypes.LONG_SWIFTNESS);
      }
   };

   public static final Ingredient IS_DURATION_EXTENDER = Ingredient.fromItem(Items.REDSTONE);
   public static final Ingredient IS_STRENGTH_EXTENDER = Ingredient.fromItem(Items.GLOWSTONE_DUST);

   private BrewEffect() {
   }

   public abstract void register(Ingredient var1);

   public void register(Item item) {
      Ingredient predicate = Ingredient.fromItems(new Item[]{item});
      this.register(predicate);
   }

   public void registerAnyOf(Item item) {
      Ingredient predicate = Ingredient.fromItem(item);
      this.register(predicate);
   }

   public static final void onBrewedSwitchColor(Post event, Map<PotionType, Integer> typeToColorMap) {
      for(int slotindex = 0; slotindex < 3; ++slotindex) {
         ItemStack brewed = event.getItem(slotindex);
         Integer color = null;
         if (!ItemStacks.isEmpty(brewed) && (color = (Integer)typeToColorMap.get(PotionUtils.getPotionFromItem(brewed))) != null) {
            brewed.setTagInfo(SharedGlue.CUSTOM_POTION_COLOR_TAGNAME(), new NBTTagInt(color.intValue()));
         }
      }

   }

   public static void registerAll(Item item, int meta, BrewEffect... effects) {
      Ingredient predicate = Ingredient.fromStacks(new ItemStack[]{new ItemStack(item, 1, meta)});

      for(BrewEffect effect : effects) {
         effect.register(predicate);
      }

   }

   public static final void addWaterConversionFor(Item item, int meta, PotionType named_effect) {
      Ingredient predicate = Ingredient.fromStacks(new ItemStack[]{new ItemStack(item, 1, meta)});
      PotionHelper.addMix(PotionTypes.WATER, predicate, named_effect);
   }

   public static final void addThickConversionFor(Item item, int meta, PotionType named_effect) {
      Ingredient predicate = Ingredient.fromStacks(new ItemStack[]{new ItemStack(item, 1, meta)});
      PotionHelper.addMix(PotionTypes.THICK, predicate, named_effect);
   }

   public static final void addRecipeFor(Item item, int meta, PotionType named_effect) {
      Ingredient predicate = Ingredient.fromStacks(new ItemStack[]{new ItemStack(item, 1, meta)});
      PotionHelper.addMix(PotionTypes.AWKWARD, predicate, named_effect);
   }

   public static final void addRecipeForAnyOf(Item item, PotionType base, PotionType named_effect) {
      Ingredient predicate = Ingredient.fromItem(item);
      PotionHelper.addMix(base == null ? PotionTypes.AWKWARD : base, predicate, named_effect);
   }

   public static final void addConversionFor(@Nonnull PotionType named_base, Item item, int meta, PotionType named_effect) {
      Ingredient predicate = Ingredient.fromStacks(new ItemStack[]{new ItemStack(item, 1, meta)});
      PotionHelper.addMix(named_base, predicate, named_effect);
   }

   public static final void addDurationConversionFor(@Nonnull PotionType named_base, @Nonnull PotionType longer_effect) {
      PotionHelper.addMix(named_base, IS_DURATION_EXTENDER, longer_effect);
   }

   public static final void addStrengthConversionFor(@Nonnull PotionType named_base, @Nonnull PotionType stronger_effect) {
      PotionHelper.addMix(named_base, IS_STRENGTH_EXTENDER, stronger_effect);
   }

   public static final void addRecipeFor(Item item, int meta, PotionType named_effect, @Nullable PotionType stronger_effect, @Nullable PotionType longer_effect) {
      addRecipeFor(item, meta, named_effect);
      if (stronger_effect != null) {
         PotionHelper.addMix(named_effect, IS_STRENGTH_EXTENDER, stronger_effect);
      }

      if (longer_effect != null) {
         PotionHelper.addMix(named_effect, IS_DURATION_EXTENDER, longer_effect);
      }

   }
}
