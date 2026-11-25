package crafttweaker.mc1120.recipes;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.ICraftingInventory;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IRecipeAction;
import crafttweaker.api.recipes.IRecipeFunction;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public abstract class MCRecipeBase implements IRecipe, ICraftingRecipe {
   protected final ItemStack outputStack;
   protected final IItemStack output;
   protected final IRecipeFunction recipeFunction;
   protected final IRecipeAction recipeAction;
   protected final boolean hidden;
   protected NonNullList<Ingredient> ingredientList;
   protected ResourceLocation recipeNameLocation = new ResourceLocation("crafttweaker", "unInitializedRecipeName");

   MCRecipeBase(IItemStack output, NonNullList<Ingredient> ingredientList, IRecipeFunction recipeFunction, IRecipeAction recipeAction, boolean hidden) {
      this.output = output;
      this.outputStack = CraftTweakerMC.getItemStack(output);
      this.ingredientList = ingredientList;
      this.recipeFunction = recipeFunction;
      this.recipeAction = recipeAction;
      this.hidden = hidden;
   }

   public ItemStack getRecipeOutput() {
      return this.outputStack.copy();
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.ingredientList;
   }

   public boolean isDynamic() {
      return this.hidden;
   }

   public String getGroup() {
      return "";
   }

   public IRecipe setRegistryName(ResourceLocation name) {
      this.recipeNameLocation = name;
      return this;
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.recipeNameLocation;
   }

   public Class<IRecipe> getRegistryType() {
      return IRecipe.class;
   }

   public IRecipeAction getRecipeAction() {
      return this.recipeAction;
   }

   public boolean hasRecipeAction() {
      return this.recipeAction != null;
   }

   public boolean hasRecipeFunction() {
      return this.recipeFunction != null;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public String getFullResourceName() {
      return String.valueOf(this.getRegistryName());
   }

   public String getResourceDomain() {
      return this.getRegistryName() == null ? "null" : this.getRegistryName().getResourceDomain();
   }

   public String getName() {
      return this.getRegistryName() != null ? this.getRegistryName().getResourcePath() : "null";
   }

   public IItemStack getOutput() {
      return this.output;
   }

   public boolean isVisible() {
      return !this.isDynamic();
   }

   public void applyTransformers(ICraftingInventory inventory, IPlayer byPlayer) {
      if (inventory.getInternal() instanceof InventoryCrafting) {
         this.applyTransformers((InventoryCrafting)inventory.getInternal(), byPlayer);
      }

   }

   public abstract void applyTransformers(InventoryCrafting var1, IPlayer var2);

   public boolean matches(ICraftingInventory inventory) {
      return inventory.getInternal() instanceof InventoryCrafting && this.matches((InventoryCrafting)inventory.getInternal(), inventory.getPlayer() == null ? null : CraftTweakerMC.getWorld(inventory.getPlayer().getWorld()));
   }

   public IItemStack getCraftingResult(ICraftingInventory inventory) {
      return inventory.getInternal() instanceof InventoryCrafting ? CraftTweakerMC.getIItemStack(this.getCraftingResult((InventoryCrafting)inventory)) : null;
   }

   public abstract MCRecipeBase update();
}
