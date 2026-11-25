package me.ichun.mods.ichunutil.common.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ConditionalIngredient extends Ingredient {
   private final BooleanSupplier isConditionMet;
   private final ItemStack stackConditionMet;
   private final ItemStack stackConditionNotMet;
   private IntList packedConditionMet;
   private IntList packedConditionNotMet;

   public ConditionalIngredient(BooleanSupplier isConditionMet, ItemStack stackConditionMet, ItemStack stackConditionNotMet) {
      super(new ItemStack[0]);
      this.isConditionMet = isConditionMet;
      this.stackConditionMet = stackConditionMet;
      this.stackConditionNotMet = stackConditionNotMet;
   }

   public boolean apply(@Nullable ItemStack input) {
      if (input == null) {
         return false;
      } else {
         ItemStack itemStack = this.isConditionMet.getAsBoolean() ? this.stackConditionMet : this.stackConditionNotMet;
         if (itemStack.getItem() == input.getItem()) {
            int i = itemStack.getMetadata();
            if (i == 32767 || i == input.getMetadata()) {
               return true;
            }
         }

         return false;
      }
   }

   @Nonnull
   public ItemStack[] getMatchingStacks() {
      return new ItemStack[]{this.isConditionMet.getAsBoolean() ? this.stackConditionMet : this.stackConditionNotMet};
   }

   @Nonnull
   public IntList getValidItemStacksPacked() {
      boolean normal = this.isConditionMet.getAsBoolean();
      IntList validStacks = normal ? this.packedConditionMet : this.packedConditionNotMet;
      if (validStacks == null) {
         validStacks = IntLists.singleton(RecipeItemHelper.pack(normal ? this.stackConditionMet : this.stackConditionNotMet));
         if (normal) {
            this.packedConditionMet = validStacks;
         } else {
            this.packedConditionNotMet = validStacks;
         }
      }

      return validStacks;
   }

   protected void invalidate() {
      super.invalidate();
      this.packedConditionMet = null;
      this.packedConditionNotMet = null;
   }

   public static ItemStack parseItemStack(JsonObject json, String identifier) {
      ResourceLocation normal = new ResourceLocation(JsonUtils.getString(json, identifier));
      return new ItemStack((Item)Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(normal)), JsonUtils.getInt(json, identifier + "Count", 1), JsonUtils.getInt(json, identifier + "Data", 0));
   }

   public static ConditionalIngredient parseWithCondition(BooleanSupplier condition, JsonContext context, JsonObject json) {
      ItemStack stackMet = parseItemStack(json, "met");
      ItemStack stackNotMet = parseItemStack(json, "notmet");
      return new ConditionalIngredient(condition, stackMet, stackNotMet);
   }
}
