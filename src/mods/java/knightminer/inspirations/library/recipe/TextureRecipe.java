package knightminer.inspirations.library.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import knightminer.inspirations.library.util.TagUtil;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TextureRecipe extends ShapedOreRecipe {
   public final Ingredient texture;

   public TextureRecipe(ResourceLocation group, Ingredient texture, ItemStack result, ShapedPrimer primer) {
      super(group, result, primer);
      this.texture = texture;
   }

   @Nonnull
   public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
      for(int i = 0; i < craftMatrix.getSizeInventory(); ++i) {
         for(ItemStack ore : this.texture.getMatchingStacks()) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (OreDictionary.itemMatches(ore, stack, false) && Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) {
               Block block = Block.getBlockFromItem(this.output.getItem());
               return TextureBlockUtil.createTexturedStack(block, this.output.getItemDamage(), Block.getBlockFromItem(stack.getItem()), stack.getItemDamage());
            }
         }
      }

      return super.getCraftingResult(craftMatrix);
   }

   @Nonnull
   public ItemStack getRecipeOutput() {
      if (this.texture.getMatchingStacks().length != 0 && !this.output.isEmpty()) {
         ItemStack stack = this.texture.getMatchingStacks()[0];
         Block block = Block.getBlockFromItem(this.output.getItem());
         int meta = stack.getItemDamage();
         if (meta == 32767) {
            meta = 0;
         }

         return TextureBlockUtil.createTexturedStack(block, this.output.getItemDamage(), Block.getBlockFromItem(stack.getItem()), meta);
      } else {
         return super.getRecipeOutput();
      }
   }

   public ItemStack getPlainRecipeOutput() {
      return this.output;
   }

   public static class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
         ShapedPrimer primer = new ShapedPrimer();
         primer.width = recipe.getRecipeWidth();
         primer.height = recipe.getRecipeHeight();
         primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
         primer.input = recipe.getIngredients();
         JsonElement elem = TagUtil.getElement(json, "texture");
         return new TextureRecipe(recipe.getGroup().isEmpty() ? null : new ResourceLocation(recipe.getGroup()), CraftingHelper.getIngredient(elem, context), recipe.getRecipeOutput(), primer);
      }
   }
}
