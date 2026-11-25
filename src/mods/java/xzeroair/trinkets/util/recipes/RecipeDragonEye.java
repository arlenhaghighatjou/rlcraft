package xzeroair.trinkets.util.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.init.Elements;
import xzeroair.trinkets.util.TrinketsConfig;

public class RecipeDragonEye extends ShapedOreRecipe {
   public RecipeDragonEye(@Nullable ResourceLocation group, ItemStack result, ShapedPrimer primer) {
      super(group, result, primer);
   }

   public boolean matches(InventoryCrafting inv, World world) {
      if (Trinkets.IceAndFire) {
         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.hasTagCompound() && stack.getItem().getRegistryName().toString().equalsIgnoreCase("iceandfire:dragon_skull") && stack.getTagCompound().getInteger("Stage") < TrinketsConfig.SERVER.Items.DRAGON_EYE.compat.iaf.stage) {
               return false;
            }
         }
      }

      return super.matches(inv, world);
   }

   public ItemStack getRecipeOutput() {
      ItemStack output = super.getRecipeOutput().copy();

      for(Ingredient i : this.getIngredients()) {
         for(ItemStack stack : i.getMatchingStacks()) {
            if (!stack.isEmpty() && stack.getItem().getRegistryName().toString().equalsIgnoreCase("iceandfire:dragon_skull")) {
               int meta = stack.getMetadata();
               Capabilities.getTrinketProperties(output, (prop) -> {
                  prop.getElementAttributes().setPrimaryElement(meta == 1 ? Elements.ICE : Elements.FIRE);
               });
               break;
            }
         }
      }

      return output;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack output = super.getCraftingResult(inv);
      if (Trinkets.IceAndFire) {
         try {
            for(int i = 0; i < inv.getSizeInventory(); ++i) {
               ItemStack stack = inv.getStackInSlot(i);
               if (!stack.isEmpty() && stack.getItem().getRegistryName().toString().equalsIgnoreCase("iceandfire:dragon_skull") && !stack.getTagCompound().hasNoTags() && stack.getTagCompound().getInteger("Stage") >= TrinketsConfig.SERVER.Items.DRAGON_EYE.compat.iaf.stage) {
                  NBTTagCompound tagCompound = output.getTagCompound();
                  if (tagCompound == null) {
                     tagCompound = new NBTTagCompound();
                     output.setTagCompound(tagCompound);
                  }

                  if (stack.getItemDamage() != 0 && TrinketsConfig.SERVER.Items.DRAGON_EYE.compat.iaf.ICE_VARIANT) {
                     Capabilities.getTrinketProperties(output, (prop) -> {
                        prop.setVariant(Elements.ICE.getID());
                        prop.getElementAttributes().setPrimaryElement(Elements.ICE);
                     });
                  }
               }
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      return output;
   }

   public String getGroup() {
      return this.group == null ? "" : this.group.toString();
   }

   public static class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         String group = JsonUtils.getString(json, "group", "");
         ShapedPrimer primer = RecipeHelperUtil.parseShaped(context, json);
         ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
         return new RecipeDragonEye(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
      }
   }
}
