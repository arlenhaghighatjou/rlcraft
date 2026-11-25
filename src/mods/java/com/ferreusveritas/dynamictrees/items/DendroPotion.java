package com.ferreusveritas.dynamictrees.items;

import com.ferreusveritas.dynamictrees.ModTabs;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.substances.IEmptiable;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffect;
import com.ferreusveritas.dynamictrees.api.substances.ISubstanceEffectProvider;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceDeplete;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceFertilize;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceFreeze;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceGrowth;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceMega;
import com.ferreusveritas.dynamictrees.systems.substances.SubstanceTransform;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class DendroPotion extends Item implements ISubstanceEffectProvider, IEmptiable {
   public static final String name = "dendropotion";

   public DendroPotion() {
      this("dendropotion");
   }

   public DendroPotion(String name) {
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setMaxStackSize(1);
      this.setHasSubtypes(true);
      this.setCreativeTab(ModTabs.dynamicTreesTab);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(DendroPotion.DendroPotionType type : DendroPotion.DendroPotionType.values()) {
            if (type.getActive()) {
               subItems.add(new ItemStack(this, 1, type.getIndex()));
            }
         }
      }

   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + this.getPotionType(stack).getName();
   }

   public DendroPotion.DendroPotionType getPotionTypeForDamage(int damage) {
      return DendroPotion.DendroPotionType.values()[damage % DendroPotion.DendroPotionType.values().length];
   }

   public DendroPotion.DendroPotionType getPotionType(ItemStack stack) {
      return this.getPotionTypeForDamage(stack.getItemDamage());
   }

   public ISubstanceEffect getSubstanceEffect(ItemStack itemStack) {
      switch(this.getPotionType(itemStack)) {
      case BIOCHAR:
      default:
         return null;
      case BURGEONING:
         return new SubstanceGrowth();
      case GIGAS:
         return new SubstanceMega();
      case DEPLETION:
         return (new SubstanceDeplete()).setAmount(15);
      case FERTILITY:
         return (new SubstanceFertilize()).setAmount(15);
      case PERSISTANCE:
         return new SubstanceFreeze();
      case TRANSFORM:
         return new SubstanceTransform(this.getTargetSpecies(itemStack));
      }
   }

   public Species getTargetSpecies(ItemStack itemStack) {
      if (itemStack.hasTagCompound()) {
         NBTTagCompound nbtTag = itemStack.getTagCompound();
         if (nbtTag.hasKey("target")) {
            String targetTree = nbtTag.getString("target");
            if (!targetTree.equals("")) {
               return TreeRegistry.findSpecies(new ResourceLocation(targetTree));
            }
         }
      }

      return null;
   }

   public ItemStack setTargetTree(ItemStack itemStack, TreeFamily family) {
      return this.setTargetSpecies(itemStack, family.getCommonSpecies());
   }

   public ItemStack setTargetSpecies(ItemStack itemStack, Species species) {
      NBTTagCompound nbtTag = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
      nbtTag.setString("target", species.getRegistryName().toString());
      itemStack.setTagCompound(nbtTag);
      return itemStack;
   }

   public DendroPotion registerRecipes(IForgeRegistry<IRecipe> registry) {
      ItemStack awkwardStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), (PotionType)PotionType.REGISTRY.getObject(new ResourceLocation("awkward")));
      DendroPotion.DendroPotionType.BIOCHAR.ifActive((type) -> {
         this.addRecipe(awkwardStack, new ItemStack(Items.COAL, 1, 1), this.getPotionStack(type));
      });
      DendroPotion.DendroPotionType.DEPLETION.ifActive((type) -> {
         this.addRecipe(Items.SLIME_BALL, type);
      });
      DendroPotion.DendroPotionType.GIGAS.ifActive((type) -> {
         this.addRecipe(Items.PUMPKIN_SEEDS, type);
      });
      DendroPotion.DendroPotionType.BURGEONING.ifActive((type) -> {
         this.addRecipe(Items.GHAST_TEAR, type);
      });
      DendroPotion.DendroPotionType.FERTILITY.ifActive((type) -> {
         this.addRecipe(Items.FISH, type);
      });
      DendroPotion.DendroPotionType.PERSISTANCE.ifActive((type) -> {
         this.addRecipe(Blocks.RED_FLOWER, 1, type);
      });
      DendroPotion.DendroPotionType.TRANSFORM.ifActive((type) -> {
         this.addRecipe(Items.PRISMARINE_CRYSTALS, type);

         for(Species species : TreeRegistry.getPotionTransformableSpecies()) {
            ItemStack outputStack = this.setTargetSpecies(new ItemStack(this, 1, type.getIndex()), species);
            this.addRecipe(this.getPotionStack(type), species.getSeedStack(1), outputStack);
         }

      });
      return this;
   }

   private void addRecipe(Item ingredient, DendroPotion.DendroPotionType typeOut) {
      this.addRecipe(new ItemStack(ingredient), typeOut);
   }

   private void addRecipe(Block ingredient, int ingredientMeta, DendroPotion.DendroPotionType typeOut) {
      this.addRecipe(new ItemStack(ingredient, 1, ingredientMeta), typeOut);
   }

   private void addRecipe(ItemStack ingredient, DendroPotion.DendroPotionType typeOut) {
      this.addRecipe(ingredient, this.getPotionStack(typeOut));
   }

   private void addRecipe(ItemStack ingredientStack, ItemStack stackOut) {
      this.addRecipe(this.getPotionStack(DendroPotion.DendroPotionType.BIOCHAR), ingredientStack, stackOut);
   }

   private void addRecipe(ItemStack stackIn, ItemStack ingredientStack, ItemStack stackOut) {
      BrewingRecipeRegistry.addRecipe(stackIn, ingredientStack, stackOut);
   }

   private ItemStack getPotionStack(DendroPotion.DendroPotionType type) {
      return new ItemStack(this, 1, type.getIndex());
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
      super.addInformation(stack, world, tooltip, tooltipFlag);
      DendroPotion.DendroPotionType potionType = this.getPotionType(stack);
      if (potionType == DendroPotion.DendroPotionType.TRANSFORM) {
         Species species = this.getTargetSpecies(stack);
         if (species == null) {
            tooltip.add(this.getPotionType(stack).getLore());
         } else {
            tooltip.add(getTranslationText("potion.transform.description", species.getLocalizedName()));
         }
      } else {
         tooltip.add(this.getPotionType(stack).getLore());
      }

   }

   public int getColor(ItemStack stack, int tint) {
      return tint == 0 ? this.getPotionType(stack).getColor() : -1;
   }

   public ItemStack getEmptyContainer() {
      return new ItemStack(Items.GLASS_BOTTLE);
   }

   private static String getTranslationText(String path, String... args) {
      return (new TextComponentTranslation(path, (Object[])args)).getUnformattedComponentText();
   }

   public static enum DendroPotionType {
      BIOCHAR(0, true, "biochar", 2564892),
      DEPLETION(1, true, "depletion", 7781997),
      GIGAS(2, true, "gigas", 14913565),
      BURGEONING(3, true, "burgeoning", 11124414),
      FERTILITY(4, false, "fertility", 4903936),
      PERSISTANCE(5, false, "persistance", 3709695),
      TRANSFORM(6, true, "transform", 8370340);

      private final int index;
      private final boolean active;
      private final String name;
      private final int color;

      private DendroPotionType(int index, boolean active, String name, int color) {
         this.index = index;
         this.active = active;
         this.name = name;
         this.color = color;
      }

      public int getIndex() {
         return this.index;
      }

      public boolean getActive() {
         return this.active;
      }

      public void ifActive(Consumer<DendroPotion.DendroPotionType> typeConsumer) {
         if (this.getActive()) {
            typeConsumer.accept(this);
         }

      }

      public String getName() {
         return this.name;
      }

      public int getColor() {
         return this.color;
      }

      public String getLore() {
         return DendroPotion.getTranslationText("potion." + this.name + ".description" + (this == TRANSFORM ? ".empty" : ""), "");
      }
   }
}
