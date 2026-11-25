package com.creativemd.creativecore.common.gui.controls.gui.custom;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.type.HashMapList;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class GuiStackSelector extends GuiComboBox {
   protected HashMapList<String, ItemStack> stacks;
   public EntityPlayer player;
   public boolean searchBar;
   protected ItemStack selected = ItemStack.EMPTY;

   public GuiStackSelector(String name, int x, int y, int width, EntityPlayer player, boolean searchBar) {
      super(name, x, y, width, new ArrayList());
      this.searchBar = searchBar;
      this.player = player;
      this.updateCollectedStacks();
      this.selectFirst();
   }

   public boolean selectFirst() {
      if (this.stacks != null) {
         ItemStack first = this.stacks.getFirst();
         if (first != null) {
            this.setSelected(first);
            return true;
         }
      }

      return false;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   protected abstract HashMapList<String, ItemStack> collectItems();

   public void updateCollectedStacks() {
      this.stacks = this.collectItems();
   }

   public boolean setSelected(ItemStack stack) {
      if (this.stacks.contains(stack)) {
         String display;
         try {
            display = stack.getDisplayName();
         } catch (Exception var4) {
            display = ((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).toString();
         }

         this.caption = display;
         this.selected = stack;
         this.raiseEvent(new GuiControlChangedEvent(this));
         return true;
      } else {
         return false;
      }
   }

   public HashMapList<String, ItemStack> getStacks() {
      return this.stacks;
   }

   public ItemStack getSelected() {
      return this.selected;
   }

   protected int getAdditionalSize() {
      return 22;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      super.renderContent(helper, style, width, height);
      ItemStack stack = this.getSelected();
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)(width / 2 - (helper.getStringWidth(this.caption) + this.getAdditionalSize()) / 2), (float)(height / 2 - 8), 0.0F);
         helper.drawItemStack(stack, 0, 0, 16, 16);
         GlStateManager.popMatrix();
      }

   }

   protected GuiComboBoxExtension createBox() {
      return new GuiStackSelectorExtension(this.name + "extension", this.getPlayer(), this.posX, this.posY + this.height, this.width - this.getContentOffset() * 2, 80, this);
   }

   public boolean select(String line) {
      return false;
   }
}
