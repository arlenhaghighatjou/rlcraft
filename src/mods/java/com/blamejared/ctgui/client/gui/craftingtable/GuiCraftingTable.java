package com.blamejared.ctgui.client.gui.craftingtable;

import com.blamejared.ctgui.api.CheckButton;
import com.blamejared.ctgui.api.ContainerBase;
import com.blamejared.ctgui.api.GuiBase;
import com.blamejared.ctgui.api.SlotRecipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class GuiCraftingTable extends GuiBase {
   private CheckButton shaped;
   private CheckButton shapeless;
   private CheckButton mirror;

   public GuiCraftingTable(ContainerBase container) {
      super(container, 176, 166, true);
   }

   public void initGui() {
      super.initGui();
      this.shaped = new CheckButton(this, 2910, this.getGuiLeft(), this.getGuiTop() + this.getYSize() + 4, "Shaped", true);
      this.shapeless = new CheckButton(this, 2911, this.getGuiLeft() + 54, this.getGuiTop() + this.getYSize() + 4, "Shapeless", false);
      this.mirror = new CheckButton(this, 2912, this.getGuiLeft() + 54 + 74, this.getGuiTop() + this.getYSize() + 4, "Mirror", false);
      this.shaped.setBackgroundSize(50);
      this.shapeless.setBackgroundSize(70);
      this.mirror.setBackgroundSize(50);
      this.getButtonList().add(this.shapeless);
      this.getButtonList().add(this.shaped);
      this.getButtonList().add(this.mirror);
      this.shaped.setIncompatible(Collections.singletonList(this.shapeless));
      this.shapeless.setIncompatible(Arrays.asList(this.shaped, this.mirror));
      this.mirror.setIncompatible(Collections.singletonList(this.remove));
      this.shaped.setIsChecked(true);
   }

   public String getOutputAdd() {
      String type = "add";
      boolean shapel = false;
      if (this.shapeless.isChecked()) {
         type = type + "Shapeless";
         shapel = true;
      } else if (this.shaped.isChecked()) {
         type = type + "Shaped";
      }

      if (this.mirror.isChecked()) {
         type = type + "Mirrored";
      }

      if (shapel) {
         List<String> stacks = new ArrayList();

         for(int i = 1; i < this.container.getRecipeSlots().size(); ++i) {
            SlotRecipe slot = (SlotRecipe)this.container.getRecipeSlots().get(i);
            if (slot.getHasStack()) {
               stacks.add(slot.getItemString());
            }
         }

         return String.format("recipes.%s(%s, [%s]);", type, ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString(), String.join(",", stacks));
      } else {
         return String.format("recipes.%s(%s, [[%s, %s, %s],[%s, %s, %s], [%s, %s, %s]]);", type, ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(1)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(2)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(3)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(4)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(5)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(6)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(7)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(8)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(9)).getItemString());
      }
   }

   private String getShapelessItem(SlotRecipe slot, boolean first) {
      String ret = "";
      if (slot.getStack().isEmpty()) {
         return "";
      } else {
         if (slot.getHasStack()) {
            if (!first) {
               ret = ret + ", ";
            }

            ret = ret + slot.getItemString();
         }

         return ret.isEmpty() ? ret : ret;
      }
   }

   public String getOutputRemove() {
      boolean hasrecipe = false;

      for(int i = 1; i < this.container.getRecipeSlots().size(); ++i) {
         SlotRecipe slot = (SlotRecipe)this.container.getRecipeSlots().get(i);
         if (slot.getHasStack()) {
            hasrecipe = true;
         }
      }

      boolean shapel = this.shapeless.isChecked();
      if (shapel) {
         return String.format("recipes.removeShapeless(%s, [%s%s%s%s%s%s%s%s%s]);", ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString(), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(1), true), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(2), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(3), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(4), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(5), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(6), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(7), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(8), false), this.getShapelessItem((SlotRecipe)this.container.getRecipeSlots().get(9), false)).replace("[,", "[");
      } else {
         return hasrecipe ? String.format("recipes.removeShaped(%s, [[%s, %s, %s],[%s, %s, %s], [%s, %s, %s]]);", ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(1)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(2)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(3)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(4)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(5)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(6)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(7)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(8)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(9)).getItemString()) : String.format("recipes.remove(%s);", ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString());
      }
   }

   public ResourceLocation getTexture() {
      return new ResourceLocation("textures/gui/container/crafting_table.png");
   }
}
