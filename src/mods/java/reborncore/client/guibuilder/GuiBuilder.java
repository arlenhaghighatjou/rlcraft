package reborncore.client.guibuilder;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Loader;
import reborncore.ClientProxy;
import reborncore.api.IListInfoProvider;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.util.StringUtils;

public class GuiBuilder {
   public static final ResourceLocation defaultTextureSheet = new ResourceLocation("reborncore", "textures/gui/guielements.png");
   static ResourceLocation resourceLocation;

   public GuiBuilder() {
      resourceLocation = defaultTextureSheet;
   }

   public GuiBuilder(ResourceLocation resourceLocation) {
      resourceLocation = resourceLocation;
   }

   public void drawDefaultBackground(GuiScreen gui, int x, int y, int width, int height) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
      gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
      gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
      gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
   }

   public void drawEnergyBar(GuiBase gui, int x, int y, int height, int energyStored, int maxEnergyStored, int mouseX, int mouseY, String powerType) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 0, 150, 14, height);
      gui.drawTexturedModalRect(x, y + height - 1, 0, 255, 14, 1);
      int draw = (int)((double)energyStored / (double)maxEnergyStored * (double)(height - 2));
      gui.drawTexturedModalRect(x + 1, y + height - draw - 1, 14, height + 150 - draw, 12, draw);
      if (gui.isPointInRect(x, y, 14, height, mouseX, mouseY)) {
         List<String> list = new ArrayList();
         list.add(energyStored + " / " + maxEnergyStored + " " + powerType);
         GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
      }

   }

   public void drawPlayerSlots(GuiScreen gui, int posX, int posY, boolean center) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      if (center) {
         posX -= 81;
      }

      for(int y = 0; y < 3; ++y) {
         for(int x = 0; x < 9; ++x) {
            gui.drawTexturedModalRect(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
         }
      }

      for(int x = 0; x < 9; ++x) {
         gui.drawTexturedModalRect(posX + x * 18, posY + 58, 150, 0, 18, 18);
      }

   }

   public void drawSlot(GuiScreen gui, int posX, int posY) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(posX, posY, 150, 0, 18, 18);
   }

   public void drawString(GuiScreen gui, String string, int x, int y) {
      gui.mc.fontRenderer.drawString(string, x, y, 16777215);
   }

   public void drawString(GuiScreen gui, String string, int x, int y, int color) {
      gui.mc.fontRenderer.drawString(string, x, y, color);
   }

   public void drawProgressBar(GuiScreen gui, double progress, int x, int y) {
      gui.mc.getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 150, 18, 22, 15);
      int j = (int)progress;
      if (j > 0) {
         gui.drawTexturedModalRect(x, y, 150, 34, j + 1, 15);
      }

   }

   public void drawOutputSlot(GuiScreen gui, int x, int y) {
      gui.mc.getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 174, 0, 26, 26);
   }

   public void drawInfoButton(int buttonID, int x, int y, List<GuiButton> buttonList) {
      buttonList.add(new GuiButton(0, x, y, 20, 20, "i"));
   }

   public void handleInfoButtonClick(int buttonID, List<GuiButton> buttonList) {
   }

   public void drawInfo(GuiScreen gui, int x, int y, int height, int width, boolean draw) {
      if (draw) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 0, 0, width / 2, height / 2);
         gui.drawTexturedModalRect(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
         gui.drawTexturedModalRect(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
         gui.drawTexturedModalRect(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
      }

   }

   public void drawJEIButton(GuiBase gui, int x, int y, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (Loader.isModLoaded("jei")) {
            if (layer == GuiBase.Layer.BACKGROUND) {
               x += gui.getGuiLeft();
               y += gui.getGuiTop();
            }

            gui.mc.getTextureManager().bindTexture(resourceLocation);
            gui.drawTexturedModalRect(x, y, 202, 0, 12, 12);
         }

      }
   }

   public void drawLockButton(GuiBase gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer, boolean locked) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 174, 26 + (locked ? 12 : 0), 20, 12);
         if (gui.isPointInRect(x, y, 20, 12, mouseX, mouseY)) {
            List<String> list = new ArrayList();
            if (locked) {
               list.add(StringUtils.t("reborncore.gui.tooltip.unlock_items"));
            } else {
               list.add(StringUtils.t("reborncore.gui.tooltip.lock_items"));
            }

            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft() + gui.getGuiLeft() / 2;
               mouseY -= gui.getGuiTop() - gui.getGuiTop() / 2 - 1;
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }

   public void drawEnergyStorageRedstoneModeButton(GuiBase gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer, byte mode) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(defaultTextureSheet);
         gui.drawTexturedModalRect(x, y, 217, 107, 20, 20);
         if (gui.isPointInRect(x, y, 16, 15, mouseX, mouseY)) {
            List<String> list = new ArrayList();
            list.add(StringUtils.t("reborncore.gui.tooltip.energy_storage_redstone_mode_" + mode));
            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft() + gui.getGuiLeft() / 2;
               mouseY -= gui.getGuiTop() - gui.getGuiTop() / 2 - 1;
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

   }

   public void drawHologramButton(GuiBase gui, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         if (ClientProxy.multiblockRenderEvent.currentMultiblock == null) {
            gui.drawTexturedModalRect(x, y, 174, 50, 20, 12);
         } else {
            gui.drawTexturedModalRect(x, y, 174, 62, 20, 12);
         }

         if (gui.isPointInRect(x, y, 20, 12, mouseX, mouseY)) {
            List<String> list = new ArrayList();
            list.add(StringUtils.t("reborncore.gui.tooltip.hologram"));
            GlStateManager.pushMatrix();
            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft();
               mouseY -= gui.getGuiTop();
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.popMatrix();
         }

      }
   }

   public void drawUpDownButtons(GuiBase gui, int x, int y, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 174, 74, 12, 12);
         gui.drawTexturedModalRect(x + 12, y, 174, 86, 12, 12);
         gui.drawTexturedModalRect(x + 24, y, 174, 98, 12, 12);
         gui.drawTexturedModalRect(x + 36, y, 174, 110, 12, 12);
      }
   }

   public void drawBigHeatBar(GuiBase gui, int x, int y, int value, int max, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 26, 218, 114, 18);
         if (value != 0) {
            int j = (int)((double)value / (double)max * 106.0D);
            if (j < 0) {
               j = 0;
            }

            gui.drawTexturedModalRect(x + 4, y + 4, 26, 246, j, 10);
            gui.drawCentredString(value + StringUtils.t("reborncore.gui.heat"), y + 5, 16777215, layer);
         }

      }
   }

   public void drawBigBlueBar(GuiBase gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, String line2, String format, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         int j = (int)((double)value / (double)max * 106.0D);
         if (j < 0) {
            j = 0;
         }

         gui.drawTexturedModalRect(x + 4, y + 4, 0, 236, j, 10);
         if (!suffix.equals("")) {
            suffix = " " + suffix;
         }

         gui.drawCentredString(format + suffix, y + 5, 16777215, layer);
         if (gui.isPointInRect(x, y, 114, 18, mouseX, mouseY)) {
            int percentage = this.percentage(max, value);
            List<String> list = new ArrayList();
            list.add("" + TextFormatting.GOLD + value + "/" + max + suffix);
            list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.dsu_fullness"));
            list.add(line2);
            if (value > max) {
               list.add(TextFormatting.GRAY + "Yo this is storing more than it should be able to");
               list.add(TextFormatting.GRAY + "prolly a bug");
               list.add(TextFormatting.GRAY + "pls report and tell how tf you did this");
            }

            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft();
               mouseY -= gui.getGuiTop();
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }

   public void drawBigBlueBar(GuiBase gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, GuiBase.Layer layer) {
      this.drawBigBlueBar(gui, x, y, value, max, mouseX, mouseY, suffix, "", Integer.toString(value), layer);
   }

   public void drawBigBlueBar(GuiBase gui, int x, int y, int value, int max, int mouseX, int mouseY, GuiBase.Layer layer) {
      this.drawBigBlueBar(gui, x, y, value, max, mouseX, mouseY, "", "", "", layer);
   }

   public void drawMultiblockMissingBar(GuiBase gui, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         int x = 0;
         int y = 4;
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         GlStateManager.colorMask(true, true, true, false);
         GuiUtils.drawGradientRect(0, x, y, x + 176, y + 20, 0, -1073741824);
         GuiUtils.drawGradientRect(0, x, y + 20, x + 176, y + 20 + 48, -1073741824, -1073741824);
         GuiUtils.drawGradientRect(0, x, y + 68, x + 176, y + 70 + 20, -1073741824, 0);
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.enableDepth();
         gui.drawCentredString(StringUtils.t("reborncore.gui.missingmultiblock"), 43, 16777215, layer);
      }
   }

   public void drawUpgrades(GuiBase gui, int x, int y) {
      gui.mc.getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 217, 0, 24, 81);
   }

   public void drawSlotTab(GuiBase gui, int x, int y, ItemStack stack) {
      gui.mc.getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 217, 82, 24, 24);
      gui.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x + 5, y + 4);
   }

   public void drawSlotTabExpanded(GuiScreen gui, int posX, int posY, int mouseX, int mouseY, boolean upgrades, ItemStack stack) {
      int offset = -1;
      if (!upgrades) {
         offset = 80;
      }

      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(posX - 79, posY + 84 - offset, 0, 0, 80, 4);
      gui.drawTexturedModalRect(posX - 79, posY + 88 - offset, 0, 4, 80, 72);
      gui.drawTexturedModalRect(posX - 79, posY + 160 - offset, 0, 146, 80, 4);
      List<String> tips = new ArrayList();
      tips.add(StringUtils.t("reborncore.gui.slotconfigtip.slot"));
      tips.add(StringUtils.t("reborncore.gui.slotconfigtip.side"));
      tips.add(StringUtils.t("reborncore.gui.slotconfigtip.copy"));
      GuiBuilder.TipsList explanation = new GuiBuilder.TipsList(gui, 75, 76, posY + 108 - offset, posY + 182 - offset, posX - 75, 10, tips);
      explanation.drawScreen(mouseX, mouseY, 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void drawEnergyOutput(GuiBase gui, int x, int y, int maxOutput, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         String text = PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxOutput) + " " + PowerSystem.getDisplayPower().abbreviation + "/t";
         int width = gui.mc.fontRenderer.getStringWidth(text);
         gui.drawString(text, x - width - 2, y + 5, 0, layer);
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 150, 91, 16, 16);
      }
   }

   public void drawProgressBar(GuiBase gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY, GuiBuilder.ProgressDirection direction, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, direction.x, direction.y, direction.width, direction.height);
         int j = (int)((double)progress / (double)maxProgress * 16.0D);
         if (j < 0) {
            j = 0;
         }

         switch(direction) {
         case RIGHT:
            gui.drawTexturedModalRect(x, y, direction.xActive, direction.yActive, j, 10);
            break;
         case LEFT:
            gui.drawTexturedModalRect(x + 16 - j, y, direction.xActive + 16 - j, direction.yActive, j, 10);
            break;
         case UP:
            gui.drawTexturedModalRect(x, y + 16 - j, direction.xActive, direction.yActive + 16 - j, 10, j);
            break;
         case DOWN:
            gui.drawTexturedModalRect(x, y, direction.xActive, direction.yActive, 10, j);
            break;
         default:
            return;
         }

         if (gui.isPointInRect(x, y, direction.width, direction.height, mouseX, mouseY)) {
            int percentage = this.percentage(maxProgress, progress);
            List<String> list = new ArrayList();
            list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%");
            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft();
               mouseY -= gui.getGuiTop();
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }

   public void drawMultiEnergyBar(GuiBase gui, int x, int y, int energyStored, int maxEnergyStored, int mouseX, int mouseY, int buttonID, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         PowerSystem.EnergySystem displayPower = PowerSystem.getDisplayPower();
         Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, displayPower.xBar - 15, displayPower.yBar - 1, 14, 50);
         int draw = (int)((double)energyStored / (double)maxEnergyStored * 48.0D);
         if (energyStored > maxEnergyStored) {
            draw = 48;
         }

         gui.drawTexturedModalRect(x + 1, y + 49 - draw, displayPower.xBar, 48 + displayPower.yBar - draw, 12, draw);
         int percentage = this.percentage(maxEnergyStored, energyStored);
         if (gui.isPointInRect(x + 1, y + 1, 11, 48, mouseX, mouseY)) {
            List<String> list = new ArrayList();
            TextFormatting powerColour = TextFormatting.GOLD;
            list.add(powerColour + PowerSystem.getLocaliszedPowerFormattedNoSuffix(energyStored) + "/" + PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxEnergyStored) + " " + displayPower.abbreviation);
            list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.power_charged"));
            if (gui.tile instanceof IListInfoProvider) {
               if (GuiScreen.isShiftKeyDown()) {
                  ((IListInfoProvider)gui.tile).addInfo(list, true);
                  list.add("");
                  list.add(TextFormatting.BLUE + StringUtils.t("reborncore.gui.tooltip.power_click"));
               } else {
                  list.add("");
                  list.add(TextFormatting.BLUE + "Shift" + TextFormatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.power_moreinfo"));
               }
            }

            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft();
               mouseY -= gui.getGuiTop();
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         gui.addPowerButton(x, y, buttonID, layer);
      }
   }

   public void drawTank(GuiBase gui, int x, int y, int mouseX, int mouseY, FluidStack fluid, int maxCapacity, boolean isTankEmpty, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         int percentage = 0;
         int amount = 0;
         if (!isTankEmpty) {
            amount = fluid.amount;
            percentage = this.percentage(maxCapacity, amount);
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 194, 26, 22, 56);
         if (!isTankEmpty) {
            this.drawFluid(gui, fluid, x + 4, y + 4, 14, 48, maxCapacity);
         }

         gui.drawTexturedModalRect(x + 3, y + 3, 194, 82, 16, 50);
         if (gui.isPointInRect(x, y, 22, 56, mouseX, mouseY)) {
            List<String> list = new ArrayList();
            if (isTankEmpty) {
               list.add(TextFormatting.GOLD + StringUtils.t("reborncore.gui.tooltip.tank_empty"));
            } else {
               list.add(TextFormatting.GOLD + StringUtils.t("reborncore.gui.tooltip.tank_amount", amount, maxCapacity) + " " + fluid.getLocalizedName());
            }

            list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " " + StringUtils.t("reborncore.gui.tooltip.tank_fullness"));
            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft();
               mouseY -= gui.getGuiTop();
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }

   public void drawFluid(GuiBase gui, FluidStack fluid, int x, int y, int width, int height, int maxCapacity) {
      gui.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      y = y + height;
      ResourceLocation still = fluid.getFluid().getStill(fluid);
      EaglerTextureAtlasSprite sprite = gui.mc.getTextureMapBlocks().getAtlasSprite(still.toString());
      int drawHeight = (int)((float)fluid.amount / ((float)maxCapacity * 1.0F) * (float)height);
      int iconHeight = sprite.getIconHeight();
      int offsetHeight = drawHeight;
      int iteration = 0;

      while(offsetHeight != 0) {
         int curHeight = offsetHeight < iconHeight ? offsetHeight : iconHeight;
         gui.drawTexturedModalRect(x, y - offsetHeight, sprite, width, curHeight);
         offsetHeight -= curHeight;
         ++iteration;
         if (iteration > 50) {
            break;
         }
      }

      gui.mc.getTextureManager().bindTexture(resourceLocation);
   }

   public void drawBurnBar(GuiBase gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY, GuiBase.Layer layer) {
      if (GuiBase.slotConfigType == GuiBase.SlotConfigType.NONE) {
         if (layer == GuiBase.Layer.BACKGROUND) {
            x += gui.getGuiLeft();
            y += gui.getGuiTop();
         }

         gui.mc.getTextureManager().bindTexture(resourceLocation);
         gui.drawTexturedModalRect(x, y, 150, 64, 13, 13);
         int j = 13 - (int)((double)progress / (double)maxProgress * 13.0D);
         if (j > 0) {
            gui.drawTexturedModalRect(x, y + j, 150, 51 + j, 13, 13 - j);
         }

         if (gui.isPointInRect(x, y, 12, 12, mouseX, mouseY)) {
            int percentage = this.percentage(maxProgress, progress);
            List<String> list = new ArrayList();
            list.add(StringUtils.getPercentageColour(percentage) + "" + percentage + "%");
            if (layer == GuiBase.Layer.FOREGROUND) {
               mouseX -= gui.getGuiLeft();
               mouseY -= gui.getGuiTop();
            }

            gui.drawHoveringText(list, mouseX, mouseY);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }

   public void drawOutputSlotBar(GuiBase gui, int x, int y, int count) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
      gui.drawTexturedModalRect(x, y, 150, 122, 3, 26);
      x = x + 3;

      for(int i = 1; i <= count; ++i) {
         gui.drawTexturedModalRect(x, y, 153, 122, 20, 26);
         x += 20;
      }

      gui.drawTexturedModalRect(x, y, 173, 122, 3, 26);
   }

   protected int percentage(int MaxValue, int CurrentValue) {
      return CurrentValue == 0 ? 0 : (int)((float)CurrentValue * 100.0F / (float)MaxValue);
   }

   public static enum ProgressDirection {
      RIGHT(58, 150, 74, 150, 16, 10),
      LEFT(74, 160, 58, 160, 16, 10),
      DOWN(78, 170, 88, 170, 10, 16),
      UP(58, 170, 68, 170, 10, 16);

      public int x;
      public int y;
      public int xActive;
      public int yActive;
      public int width;
      public int height;

      private ProgressDirection(int x, int y, int xActive, int yActive, int width, int height) {
         this.x = x;
         this.y = y;
         this.xActive = xActive;
         this.yActive = yActive;
         this.width = width;
         this.height = height;
      }
   }

   private class TipsList extends GuiScrollingList {
      private GuiScreen gui;
      private List<String> tips = null;

      public TipsList(GuiScreen gui, int width, int height, int top, int bottom, int left, int entryHeight, List<String> tips) {
         super(gui.mc, width, height, top, bottom, left, entryHeight, gui.width, gui.height);
         this.gui = gui;
         this.tips = tips;
      }

      protected int getSize() {
         return this.tips.size();
      }

      protected void elementClicked(int index, boolean doubleClick) {
      }

      protected boolean isSelected(int index) {
         return false;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
      }
   }
}
