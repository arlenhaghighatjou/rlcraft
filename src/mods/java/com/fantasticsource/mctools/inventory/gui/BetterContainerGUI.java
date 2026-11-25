package com.fantasticsource.mctools.inventory.gui;

import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiContainerEvent.DrawForeground;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

@SideOnly(Side.CLIENT)
public class BetterContainerGUI extends GuiContainer {
   protected boolean buttonClicked_;
   protected Slot hoveredSlot_;
   protected ItemStack draggedStack_ = ItemStack.EMPTY;
   protected boolean isRightMouseClick_;
   protected int dragSplittingRemnant_;
   protected ItemStack returningStack_ = ItemStack.EMPTY;
   protected long returningStackTime_;
   protected Slot returningStackDestSlot_;
   protected int touchUpX_;
   protected int touchUpY_;
   protected boolean doubleClick_;
   protected Slot lastClickSlot_;
   protected long lastClickTime_;
   protected int lastClickButton_;
   protected boolean ignoreMouseUp_ = true;
   protected Slot clickedSlot_;
   protected ItemStack shiftClickedStack_ = ItemStack.EMPTY;
   protected int dragSplittingButton_;
   protected int dragSplittingLimit_;

   public BetterContainerGUI(Container container) {
      super(container);
      this.allowUserInput = true;
      this.mc = Minecraft.getMinecraft();
      this.fontRenderer = this.mc.fontRenderer;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();

      for(GuiButton button : this.buttonList) {
         button.drawButton(this.mc, mouseX, mouseY, partialTicks);
      }

      for(GuiLabel label : this.labelList) {
         label.drawLabel(this.mc, mouseX, mouseY);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate((float)this.guiLeft, (float)this.guiTop, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.inventorySlots != null) {
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.enableRescaleNormal();
         this.hoveredSlot_ = null;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

         for(int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
            if (slot.isEnabled()) {
               this.drawSlot(slot);
            }

            if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
               this.hoveredSlot_ = slot;
               GlStateManager.disableLighting();
               GlStateManager.disableDepth();
               GlStateManager.colorMask(true, true, true, false);
               this.drawGradientRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, -2130706433, -2130706433);
               GlStateManager.colorMask(true, true, true, true);
               GlStateManager.enableLighting();
               GlStateManager.enableDepth();
            }
         }
      }

      RenderHelper.disableStandardItemLighting();
      this.drawGuiContainerForegroundLayer(mouseX, mouseY);
      RenderHelper.enableGUIStandardItemLighting();
      MinecraftForge.EVENT_BUS.post(new DrawForeground(this, mouseX, mouseY));
      GlStateManager.popMatrix();
      if (this.inventorySlots != null) {
         InventoryPlayer inventoryplayer = this.mc.player.inventory;
         ItemStack itemstack = this.draggedStack_.isEmpty() ? inventoryplayer.getItemStack() : this.draggedStack_;
         if (!itemstack.isEmpty()) {
            String s = null;
            if (!this.draggedStack_.isEmpty() && this.isRightMouseClick_) {
               itemstack = itemstack.copy();
               itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
            } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
               itemstack = itemstack.copy();
               itemstack.setCount(this.dragSplittingRemnant_);
               if (itemstack.isEmpty()) {
                  s = "" + TextFormatting.YELLOW + "0";
               }
            }

            this.drawItemStack(itemstack, mouseX - 8, mouseY - (this.draggedStack_.isEmpty() ? 8 : 16), s);
         }

         if (!this.returningStack_.isEmpty()) {
            float f = (float)(Minecraft.getSystemTime() - this.returningStackTime_) / 100.0F;
            if (f >= 1.0F) {
               f = 1.0F;
               this.returningStack_ = ItemStack.EMPTY;
            }

            int l2 = this.returningStackDestSlot_.xPos - this.touchUpX_;
            int i3 = this.returningStackDestSlot_.yPos - this.touchUpY_;
            int l1 = this.touchUpX_ + (int)((float)l2 * f);
            int i2 = this.touchUpY_ + (int)((float)i3 * f);
            this.drawItemStack(this.returningStack_, l1, i2, (String)null);
         }

         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
         this.renderHoveredToolTip(mouseX, mouseY);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
   }

   protected void actionPerformed(GuiButton button) {
      this.buttonClicked_ = true;
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         for(int i = 0; i < this.buttonList.size(); ++i) {
            GuiButton guibutton = (GuiButton)this.buttonList.get(i);
            if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
               Pre event = new Pre(this, guibutton, this.buttonList);
               if (MinecraftForge.EVENT_BUS.post(event)) {
                  break;
               }

               guibutton = event.getButton();
               this.selectedButton = guibutton;
               guibutton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guibutton);
               if (this.equals(this.mc.currentScreen)) {
                  MinecraftForge.EVENT_BUS.post(new Post(this, event.getButton(), this.buttonList));
               }
            }
         }
      }

      if (this.inventorySlots != null) {
         boolean clickedOutside = this.hasClickedOutside(mouseX, mouseY);
         long i = Minecraft.getSystemTime();
         Slot slot = this.getSlotAtPosition(mouseX, mouseY);
         this.doubleClick_ = this.lastClickSlot_ == slot && i - this.lastClickTime_ < 250L && this.lastClickButton_ == mouseButton;
         this.ignoreMouseUp_ = false;
         if (mouseButton == 0 || mouseButton == 1 || this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
            if (slot != null) {
               clickedOutside = false;
            }

            int l = -1;
            if (slot != null) {
               l = slot.slotNumber;
            }

            if (clickedOutside) {
               l = -999;
            }

            if (l != -1 && !this.dragSplitting) {
               if (!this.mc.player.inventory.getItemStack().isEmpty()) {
                  this.dragSplitting = true;
                  this.dragSplittingButton_ = mouseButton;
                  this.dragSplittingSlots.clear();
                  if (mouseButton == 0) {
                     this.dragSplittingLimit_ = 0;
                  } else if (mouseButton == 1) {
                     this.dragSplittingLimit_ = 1;
                  } else if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
                     this.dragSplittingLimit_ = 2;
                  }
               } else {
                  if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100)) {
                     this.handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
                  } else {
                     boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                     ClickType clicktype = ClickType.PICKUP;
                     if (!flag2) {
                        if (l == -999) {
                           clicktype = ClickType.THROW;
                        }
                     } else {
                        this.shiftClickedStack_ = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                        clicktype = ClickType.QUICK_MOVE;
                     }

                     this.handleMouseClick(slot, l, mouseButton, clicktype);
                  }

                  this.ignoreMouseUp_ = true;
               }
            }
         }

         this.lastClickSlot_ = slot;
         this.lastClickTime_ = i;
         this.lastClickButton_ = mouseButton;
      }

   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      if (this.inventorySlots != null) {
         Slot slot = this.getSlotAtPosition(mouseX, mouseY);
         ItemStack itemstack = this.mc.player.inventory.getItemStack();
         if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit_ == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.inventorySlots.canDragIntoSlot(slot)) {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
         }
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      if (this.buttonClicked_) {
         this.buttonClicked_ = false;
      } else {
         if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
         }

         Slot slot = this.getSlotAtPosition(mouseX, mouseY);
         boolean clickedOutside = this.hasClickedOutside(mouseX, mouseY);
         if (slot != null) {
            clickedOutside = false;
         }

         int k = -1;
         if (slot != null) {
            k = slot.slotNumber;
         }

         if (clickedOutside) {
            k = -999;
         }

         if (this.doubleClick_ && slot != null && state == 0 && this.inventorySlots.canMergeSlot(ItemStack.EMPTY, slot)) {
            if (isShiftKeyDown()) {
               if (!this.shiftClickedStack_.isEmpty()) {
                  for(Slot slot2 : this.inventorySlots.inventorySlots) {
                     if (slot2 != null && slot2.canTakeStack(this.mc.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, this.shiftClickedStack_, true)) {
                        this.handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
                     }
                  }
               }
            } else {
               this.handleMouseClick(slot, k, state, ClickType.PICKUP_ALL);
            }

            this.doubleClick_ = false;
            this.lastClickTime_ = 0L;
         } else {
            if (this.dragSplitting && this.dragSplittingButton_ != state) {
               this.dragSplitting = false;
               this.dragSplittingSlots.clear();
               this.ignoreMouseUp_ = true;
               return;
            }

            if (this.ignoreMouseUp_) {
               this.ignoreMouseUp_ = false;
               return;
            }

            if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
               this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit_), ClickType.QUICK_CRAFT);

               for(Slot slot1 : this.dragSplittingSlots) {
                  this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit_), ClickType.QUICK_CRAFT);
               }

               this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit_), ClickType.QUICK_CRAFT);
            } else if (!this.mc.player.inventory.getItemStack().isEmpty()) {
               if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(state - 100)) {
                  this.handleMouseClick(slot, k, state, ClickType.CLONE);
               } else {
                  boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                  if (flag1) {
                     this.shiftClickedStack_ = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                  }

                  this.handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
               }
            }
         }

         if (this.mc.player.inventory.getItemStack().isEmpty()) {
            this.lastClickTime_ = 0L;
         }

         this.dragSplitting = false;
      }

   }

   protected boolean hasClickedOutside(int mouseX, int mouseY) {
      return mouseX < this.guiLeft || mouseY < this.guiTop || mouseX >= this.guiLeft + this.xSize || mouseY >= this.guiTop + this.ySize;
   }

   protected void renderHoveredToolTip(int mouseX, int mouseY) {
      if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot_ != null && this.hoveredSlot_.getHasStack() && (!(this.hoveredSlot_ instanceof BetterSlot) || ((BetterSlot)this.hoveredSlot_).enabled)) {
         this.renderToolTip(this.hoveredSlot_.getStack(), mouseX, mouseY);
      }

   }

   public void drawWorldBackground(int tint) {
      if (this.mc.world == null) {
         this.drawBackground(tint);
      }

   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      if (this.hoveredSlot_ != null && (!(this.hoveredSlot_ instanceof BetterSlot) || ((BetterSlot)this.hoveredSlot_).enabled)) {
         super.drawGradientRect(left, top, right, bottom, startColor, endColor);
      }
   }

   protected void drawSlot(Slot slot) {
      if (!(slot instanceof BetterSlot) || ((BetterSlot)slot).enabled) {
         int x = slot.xPos;
         int y = slot.yPos;
         ItemStack itemstack = slot.getStack();
         boolean flag = false;
         boolean flag1 = slot == this.clickedSlot_ && !this.draggedStack_.isEmpty() && !this.isRightMouseClick_;
         ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
         String s = null;
         if (slot == this.clickedSlot_ && !this.draggedStack_.isEmpty() && this.isRightMouseClick_ && !itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() >> 1);
         } else if (this.dragSplitting && this.dragSplittingSlots.contains(slot) && !itemstack1.isEmpty()) {
            if (this.dragSplittingSlots.size() == 1) {
               return;
            }

            if (Container.canAddItemToSlot(slot, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slot)) {
               itemstack = itemstack1.copy();
               flag = true;
               Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit_, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
               int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));
               if (itemstack.getCount() > k) {
                  s = TextFormatting.YELLOW.toString() + k;
                  itemstack.setCount(k);
               }
            } else {
               this.dragSplittingSlots.remove(slot);
               this.updateDragSplitting();
            }
         }

         this.zLevel = 100.0F;
         this.itemRender.zLevel = 100.0F;
         if (itemstack.isEmpty() && slot.isEnabled()) {
            GlStateManager.disableLighting();
            if (slot instanceof BetterSlot && ((BetterSlot)slot).u >= 0 && ((BetterSlot)slot).v >= 0) {
               BetterSlot betterSlot = (BetterSlot)slot;
               int u = betterSlot.u;
               int v = betterSlot.v;
               this.mc.getTextureManager().bindTexture(betterSlot.texture);
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer bufferbuilder = tessellator.getBuffer();
               bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
               bufferbuilder.pos((double)x, (double)(y + 16), (double)this.zLevel).tex((double)u * betterSlot.uPixel, (double)(v + 16) * betterSlot.vPixel).endVertex();
               bufferbuilder.pos((double)(x + 16), (double)(y + 16), (double)this.zLevel).tex((double)(u + 16) * betterSlot.uPixel, (double)(v + 16) * betterSlot.vPixel).endVertex();
               bufferbuilder.pos((double)(x + 16), (double)y, (double)this.zLevel).tex((double)(u + 16) * betterSlot.uPixel, (double)v * betterSlot.vPixel).endVertex();
               bufferbuilder.pos((double)x, (double)y, (double)this.zLevel).tex((double)u * betterSlot.uPixel, (double)v * betterSlot.vPixel).endVertex();
               tessellator.draw();
            } else {
               EaglerTextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();
               if (textureatlassprite != null) {
                  this.mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                  this.drawTexturedModalRect(x, y, textureatlassprite, 16, 16);
               }
            }

            GlStateManager.enableLighting();
            flag1 = true;
         }

         if (!flag1) {
            if (flag) {
               drawRect(x, y, x + 16, y + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, itemstack, x, y);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, x, y, s);
         }

         this.itemRender.zLevel = 0.0F;
         this.zLevel = 0.0F;
      }
   }

   protected void updateDragSplitting() {
      ItemStack itemstack = this.mc.player.inventory.getItemStack();
      if (!itemstack.isEmpty() && this.dragSplitting) {
         if (this.dragSplittingLimit_ == 2) {
            this.dragSplittingRemnant_ = itemstack.getMaxStackSize();
         } else {
            this.dragSplittingRemnant_ = itemstack.getCount();

            for(Slot slot : this.dragSplittingSlots) {
               ItemStack itemstack1 = itemstack.copy();
               ItemStack itemstack2 = slot.getStack();
               int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
               Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit_, itemstack1, i);
               int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
               if (itemstack1.getCount() > j) {
                  itemstack1.setCount(j);
               }

               this.dragSplittingRemnant_ -= itemstack1.getCount() - i;
            }
         }
      }

   }

   protected void keyTyped(char typedChar, int keyCode) {
      if (keyCode == 1) {
         this.mc.player.closeScreen();
      } else {
         this.checkHotbarKeys(keyCode);
         if (this.hoveredSlot_ != null && this.hoveredSlot_.getHasStack()) {
            if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
               this.handleMouseClick(this.hoveredSlot_, this.hoveredSlot_.slotNumber, 0, ClickType.CLONE);
            } else if (this.mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
               this.handleMouseClick(this.hoveredSlot_, this.hoveredSlot_.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
         }

      }
   }

   protected boolean checkHotbarKeys(int keyCode) {
      if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot_ != null) {
         for(int i = 0; i < 9; ++i) {
            if (this.mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(keyCode)) {
               this.handleMouseClick(this.hoveredSlot_, this.hoveredSlot_.slotNumber, i, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   protected boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
      return this.isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
   }

   protected void drawItemStack(ItemStack stack, int x, int y, String altText) {
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      this.zLevel = 200.0F;
      this.itemRender.zLevel = 200.0F;
      FontRenderer font = stack.getItem().getFontRenderer(stack);
      if (font == null) {
         font = this.fontRenderer;
      }

      this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
      this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack_.isEmpty() ? 0 : 8), altText);
      this.zLevel = 0.0F;
      this.itemRender.zLevel = 0.0F;
   }

   protected Slot getSlotAtPosition(int x, int y) {
      if (this.inventorySlots == null) {
         return null;
      } else {
         for(int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i);
            if (this.isMouseOverSlot(slot, x, y) && slot.isEnabled()) {
               return slot;
            }
         }

         return null;
      }
   }

   public Slot getSlotUnderMouse() {
      return this.hoveredSlot_;
   }

   public int getGuiLeft() {
      return this.guiLeft;
   }

   public int getGuiTop() {
      return this.guiTop;
   }

   public int getXSize() {
      return this.xSize;
   }

   public int getYSize() {
      return this.ySize;
   }
}
