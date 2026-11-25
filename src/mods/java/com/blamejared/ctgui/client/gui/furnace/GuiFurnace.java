package com.blamejared.ctgui.client.gui.furnace;

import com.blamejared.ctgui.api.ContainerBase;
import com.blamejared.ctgui.api.GuiBase;
import com.blamejared.ctgui.api.Slider;
import com.blamejared.ctgui.api.SlotRecipe;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiBase {
   private Slider xpSlider;

   public GuiFurnace(ContainerBase container) {
      super(container, 176, 166, false);
   }

   public void initGui() {
      super.initGui();
      this.xpSlider = new Slider("slider", 10, this.getGuiLeft() - 102 - 2, this.getGuiTop() + this.getYSize() + 20, this.getXSize() + 102, 20, "XP= ", "", 0.0D, 50.0D, 0.0D, true, true);
      this.getButtonList().add(this.xpSlider);
   }

   public void updateScreen() {
      super.updateScreen();
      if (!((SlotRecipe)this.container.getRecipeSlots().get(0)).getHasStack()) {
         this.add.enabled = false;
         this.remove.enabled = false;
      }

   }

   public String getOutputAdd() {
      return String.format("furnace.addRecipe(%s, %s, %s);", ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(1)).getItemString(), this.xpSlider.getValue() + 0.0D);
   }

   public String getOutputRemove() {
      return String.format("furnace.remove(%s%s);", ((SlotRecipe)this.container.getRecipeSlots().get(0)).getItemString(), ((SlotRecipe)this.container.getRecipeSlots().get(1)).getHasStack() ? ", " + ((SlotRecipe)this.container.getRecipeSlots().get(1)).getItemString() : "");
   }

   public ResourceLocation getTexture() {
      return new ResourceLocation("textures/gui/container/furnace.png");
   }
}
