package rustic.compat.jei;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import rustic.common.blocks.ModBlocks;

public class CabinetRecipeWrapper implements IShapedCraftingRecipeWrapper {
   protected ItemStack output;
   protected ItemStack material = ItemStack.EMPTY;
   protected List<List<ItemStack>> inputs;

   public CabinetRecipeWrapper(ItemStack output) {
      this.output = output.copy();
      if (output.hasTagCompound()) {
         NBTTagCompound tag = output.getTagCompound();
         if (tag.hasKey("material", 10)) {
            this.material = new ItemStack(tag.getCompoundTag("material"));
         }
      }

      this.inputs = new ArrayList();
      List<ItemStack> trapdoors = new ArrayList();
      trapdoors.addAll(OreDictionary.getOres("trapdoorWood"));
      if (trapdoors.size() == 0) {
         trapdoors.add(new ItemStack(Blocks.TRAPDOOR));
      }

      this.inputs.add(this.getMaterialAsList());
      this.inputs.add(this.getMaterialAsList());
      this.inputs.add(this.getMaterialAsList());
      this.inputs.add(this.getMaterialAsList());
      this.inputs.add(Lists.newArrayList(new ItemStack[]{ItemStack.EMPTY}));
      this.inputs.add(trapdoors);
      this.inputs.add(this.getMaterialAsList());
      this.inputs.add(this.getMaterialAsList());
      this.inputs.add(this.getMaterialAsList());
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInputLists(ItemStack.class, this.inputs);
      ingredients.setOutput(ItemStack.class, this.output);
   }

   protected List<ItemStack> getMaterialAsList() {
      return (List<ItemStack>)(this.material.isEmpty() ? OreDictionary.getOres("plankWood") : Lists.newArrayList(new ItemStack[]{this.material.copy()}));
   }

   public int getWidth() {
      return 3;
   }

   public int getHeight() {
      return 3;
   }

   public static List<CabinetRecipeWrapper> getCabinetRecipes() {
      List<CabinetRecipeWrapper> recipes = new ArrayList();
      ItemStack output = new ItemStack(ModBlocks.CABINET);
      CabinetRecipeWrapper recipe = new CabinetRecipeWrapper(output);
      recipes.add(recipe);
      return recipes;
   }
}
