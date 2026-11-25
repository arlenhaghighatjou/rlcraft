package xzeroair.trinkets.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.container.TrinketInventoryContainer;
import xzeroair.trinkets.container.TrinketSlot;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.DrawingHelper;

public class TrinketGui extends TrinketInventoryEffectRenderer {
   public static ResourceLocation background = null;
   private float oldMouseX;
   private float oldMouseY;

   public TrinketGui(EntityPlayer player) {
      super(new TrinketInventoryContainer(player.inventory, !player.getEntityWorld().isRemote, player));
      this.allowUserInput = true;
   }

   private void resetGuiLeft() {
      this.guiLeft = (this.width - this.xSize) / 2;
   }

   protected void renderPotionIcons() {
      boolean hasVisibleEffect = false;

      for(PotionEffect potioneffect : this.mc.player.getActivePotionEffects()) {
         Potion potion = potioneffect.getPotion();
         if (potion.shouldRender(potioneffect)) {
            hasVisibleEffect = true;
            break;
         }
      }

      if (!this.mc.player.getActivePotionEffects().isEmpty() && hasVisibleEffect) {
         this.hasActivePotionEffects = TrinketsConfig.CLIENT.GUI.PotionIcons;
      } else {
         this.hasActivePotionEffects = false;
      }

   }

   public void updateScreen() {
      this.renderPotionIcons();
      this.resetGuiLeft();
   }

   public void initGui() {
      this.buttonList.clear();
      super.initGui();
      this.resetGuiLeft();
   }

   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 97, 8, 4210752);
      if (TrinketsConfig.CLIENT.debug.showID) {
         int ID = 0;

         for(int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1) {
            Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i1);
            if (slot instanceof TrinketSlot) {
               if (!slot.getHasStack()) {
                  GlStateManager.pushMatrix();
                  GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
                  int tXpos = slot.xPos;
                  int tYpos = slot.yPos;
                  this.fontRenderer.drawString(I18n.format(TextFormatting.DARK_RED + String.valueOf(ID), new Object[0]), tXpos + 4, tYpos + 4, 0);
                  GlStateManager.popMatrix();
               }

               ++ID;
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
               int tXpos = slot.xPos;
               int tYpos = slot.yPos;
               this.fontRenderer.drawString(I18n.format(TextFormatting.DARK_RED + String.valueOf(i1), new Object[0]), tXpos + 4, tYpos + 4, 0);
               GlStateManager.popMatrix();
            }
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.oldMouseX = (float)mouseX;
      this.oldMouseY = (float)mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      GlStateManager.pushMatrix();
      background = new ResourceLocation(TrinketsConfig.CLIENT.GUI.GuiTex);
      int k = this.guiLeft;
      int l = this.guiTop;
      int X = TrinketsConfig.CLIENT.GUI.X;
      int Y = TrinketsConfig.CLIENT.GUI.Y;
      int GuiSize = TrinketsConfig.CLIENT.GUI.guiTexSize;
      int x = k + X;
      int y = l + Y;
      float[] rgb = ColorHelper.getRGBColor(TrinketsConfig.CLIENT.GUI.GuiColor);
      if (TrinketsConfig.CLIENT.GUI.Z == 0) {
         GlStateManager.color(rgb[0], rgb[1], rgb[2]);
         this.mc.getTextureManager().bindTexture(background);
         if (background.toString().contentEquals(Reference.RESOURCE_PREFIX + "textures/gui/gui_inventory.png")) {
            this.renderTrinketInventory(x, y);
         } else {
            this.drawTexturedModalRect(x, y, 0, 0, GuiSize, GuiSize);
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
      this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
      if (TrinketsConfig.CLIENT.GUI.Z == 1) {
         GlStateManager.color(rgb[0], rgb[1], rgb[2]);
         this.mc.getTextureManager().bindTexture(background);
         if (background.toString().contentEquals(Reference.RESOURCE_PREFIX + "textures/gui/gui_inventory.png")) {
            this.renderTrinketInventory(x, y);
         } else {
            this.drawTexturedModalRect(x, y, 0, 0, GuiSize, GuiSize);
         }
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      DrawingHelper.drawEntityOnScreen(k + 51, l + 75, 30.0D, false, -180.0F, (double)((float)(k + 51) - this.oldMouseX), (double)((float)(l + 75 - 50) - this.oldMouseY), this.mc.player);
      GlStateManager.popMatrix();
   }

   protected void actionPerformed(GuiButton button) {
   }

   protected void keyTyped(char par1, int par2) throws IOException {
      if (par2 == ModKeyBindings.TRINKET_GUI.getKeyCode()) {
         this.mc.player.closeScreen();
      } else {
         super.keyTyped(par1, par2);
      }

   }

   public void displayNormalInventory() {
      GuiInventory gui = new GuiInventory(this.mc.player);
      this.mc.displayGuiScreen(gui);
   }

   private void renderTrinketInventory(int x, int y) {
      int slots = TrinketsConfig.SERVER.GUI.guiSlotsRows;
      int columnLength = Math.min(8, slots);
      int maxColumns = MathHelper.ceil((double)slots / 8.0D);
      int X = x;
      int Y = y;
      int texX = 64;
      int texY = 32;
      int c = 0;
      int l = 0;

      for(int i = 0; i < slots; ++i) {
         boolean isFirstColumn = c == 0;
         boolean isLastColumn = c == maxColumns - 1;
         boolean isFirstRow = i == 0 || l == 0;
         boolean isLastRow = i == slots - 1 || l == columnLength - 1;
         if (isFirstColumn && isLastColumn) {
            if (l == 4 || l == 7) {
               this.drawTexturedModalRect(X - 5 - c * 18, Y - 0 + l * 18, texX + 96, 8, 23, 8);
               Y += 4;
            }

            if (isFirstRow && isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 32, texY + 0, 32, 32);
            } else if (!isFirstRow && !isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 64, texY + 0, 32, 32);
            } else if (isFirstRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 32, texY + 64, 32, 32);
            } else if (isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 32, texY + 32, 32, 32);
            }
         } else if (!isFirstColumn && !isLastColumn) {
            if (l == 4 || l == 7) {
               this.drawTexturedModalRect(X - 5 - c * 18, Y - 0 + l * 18, texX + 96, 8, 23, 8);
               Y += 4;
            }

            if (isFirstRow && isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 0, texY + 32, 32, 32);
            } else if (!isFirstRow && !isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 64, texY + 0, 32, 32);
            } else if (isFirstRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 64, texY + 64, 32, 32);
            } else if (isLastRow) {
               if (l == 7) {
                  this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 64, texY + 32, 32, 32);
               } else {
                  this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 32, texY + 32, 32, 32);
               }
            }
         } else if (isFirstColumn) {
            if (l == 4 || l == 7) {
               this.drawTexturedModalRect(X - 5 - c * 18, Y - 0 + l * 18, texX + 96, 8, 23, 8);
               Y += 4;
            }

            if (isFirstRow && isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 32, texY + 0, 32, 32);
            } else if (!isFirstRow && !isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 64, texY + 0, 32, 32);
            } else if (isFirstRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 32, texY + 64, 32, 32);
            } else if (isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 32, texY + 32, 32, 32);
            }
         } else if (isLastColumn) {
            if (l == 4 || l == 7) {
               this.drawTexturedModalRect(X - 5 - c * 18, Y - 0 + l * 18, texX + 96, 8, 23, 8);
               Y += 4;
            }

            if (isFirstRow && isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 0, texY + 32, 32, 32);
            } else if (!isFirstRow && !isLastRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 64, texY + 0, 32, 32);
            } else if (isFirstRow) {
               this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 4 + l * 18, texX + 64, texY + 64, 32, 32);
            } else if (isLastRow) {
               if (l == 7) {
                  this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 64, texY + 32, 32, 32);
               } else {
                  this.drawTexturedModalRect(X - 14 - 0 - c * 18, Y - 0 + l * 18, texX + 32, texY + 32, 32, 32);
               }
            }
         }

         ++l;
         if (l % 8 == 0) {
            l = 0;
            ++c;
            Y = y;
         }
      }

   }
}
