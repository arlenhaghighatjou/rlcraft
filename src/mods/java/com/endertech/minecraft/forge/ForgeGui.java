package com.endertech.minecraft.forge;

import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForgeGui implements IGuiHandler {
   private final ForgeMod mod;

   public ForgeGui(ForgeMod mod) {
      this.mod = mod;
      mod.getRegistrator().addGuiHandler(this);
   }

   public void openFor(EntityPlayer player, int guiId, BlockPos pos) {
      if (ForgeEntity.isClientSide(player)) {
         player.openGui(this.mod, guiId, player.world, pos.getX(), pos.getY(), pos.getZ());
      }

   }

   public static class GuiTexture {
      public final Dimension size;
      public final ResourceLocation path;

      public GuiTexture(ForgeMod mod, String name, int width, int height) {
         this.path = ForgeLocating.guiTexture(mod.getId(), name);
         this.size = new Dimension(width, height);
      }
   }

   @SideOnly(Side.CLIENT)
   public abstract class Screen extends GuiScreen {
      private Dimension controlSize = new Dimension(150, 20);
      private int controlIndent = 5;
      private int columnsAmount = 1;
      private List<Gui> controlList = new ArrayList();
      private GuiButtonExt buttonAccept;
      private GuiButtonExt buttonCancel;

      public Screen() {
      }

      public void initGui() {
         this.addFinalButtons();
         super.initGui();
      }

      public void drawScreen(int mouseX, int mouseY, float partialTicks) {
         this.drawDefaultBackground();
         super.drawScreen(mouseX, mouseY, partialTicks);
      }

      public void bindTexture(ResourceLocation texture) {
         this.mc.getTextureManager().bindTexture(texture);
      }

      public void close() {
         this.mc.setIngameFocus();
      }

      public Point getBasePoint(Dimension textureSize) {
         return new Point((this.width - textureSize.width) / 2, (this.height - textureSize.height) / 2);
      }

      protected int getControlsAmount() {
         return this.controlList.size();
      }

      protected Rectangle getControlRect(int controlId) {
         int col = Math.floorMod(controlId, this.getColumnsAmount());
         int x = (this.width - this.controlSize.width * this.getColumnsAmount() - this.controlIndent * (this.getColumnsAmount() - 1)) / 2 + (this.controlSize.width + this.controlIndent) * col;
         int rowsAmount = this.getControlsAmount() / this.getColumnsAmount() + 1;
         int row = controlId / this.getColumnsAmount();
         int y = (this.height - this.controlSize.height * rowsAmount - this.controlIndent * (rowsAmount - 1)) / 2 + (this.controlSize.height + this.controlIndent) * row;
         return new Rectangle(new Point(x, y), this.controlSize);
      }

      protected int getNextControlId() {
         return this.controlList.size();
      }

      public GuiButtonExt addButton(String label) {
         Rectangle rect = this.getControlRect(this.getNextControlId());
         GuiButtonExt button = new GuiButtonExt(this.getNextControlId(), rect.x, rect.y, rect.width, rect.height, label);
         this.addControl(button);
         return button;
      }

      public GuiSlider addSlider(String label, FloatBounds bounds, float value, @Nullable ISlider handler) {
         Rectangle rect = this.getControlRect(this.getNextControlId());
         GuiSlider slider = new GuiSlider(this.getNextControlId(), rect.x, rect.y, rect.width, rect.height, this.getFormatedLabel(label), "", (double)bounds.getMin().floatValue(), (double)bounds.getMax().floatValue(), (double)value, true, true, handler);
         slider.precision = 2;
         slider.updateSlider();
         this.addControl(slider);
         return slider;
      }

      public GuiSlider addSlider(String label, IntBounds bounds, int value, @Nullable ISlider handler) {
         Rectangle rect = this.getControlRect(this.getNextControlId());
         GuiSlider slider = new GuiSlider(this.getNextControlId(), rect.x, rect.y, rect.width, rect.height, this.getFormatedLabel(label), "", (double)bounds.getMin().intValue(), (double)bounds.getMax().intValue(), (double)value, false, true, handler);
         this.addControl(slider);
         return slider;
      }

      public GuiLabel addLabel(String text, int color) {
         Rectangle rect = this.getControlRect(this.getNextControlId());
         GuiLabel label = new GuiLabel(this.fontRenderer, this.getNextControlId(), rect.x, rect.y, rect.width, rect.height, color);
         label.addLine(text);
         label.setCentered();
         this.addControl(label);
         return label;
      }

      public GuiCheckBox addCheckBox(String label, boolean checked) {
         Rectangle rect = this.getControlRect(this.getNextControlId());
         GuiCheckBox checkbox = new GuiCheckBox(this.getNextControlId(), rect.x, rect.y, label, checked);
         checkbox.width = rect.width;
         checkbox.height = rect.height;
         this.addControl(checkbox);
         return checkbox;
      }

      protected String getFormatedLabel(String label) {
         return label + ": ";
      }

      protected void addControl(Gui control) {
         this.controlList.add(control);
         if (control instanceof GuiButton) {
            this.buttonList.add((GuiButton)control);
         } else if (control instanceof GuiLabel) {
            this.labelList.add((GuiLabel)control);
         }

         for(int i = 0; i < this.controlList.size(); ++i) {
            Rectangle rect = this.getControlRect(i);
            Gui element = (Gui)this.controlList.get(i);
            if (element instanceof GuiButton) {
               ((GuiButton)element).x = rect.x;
               ((GuiButton)element).y = rect.y;
            } else if (element instanceof GuiLabel) {
               ((GuiLabel)element).x = rect.x;
               ((GuiLabel)element).y = rect.y;
            }
         }

      }

      public void addFinalButtons() {
         while(Math.floorMod(this.getControlsAmount(), this.getColumnsAmount()) + 1 != Math.max(1, this.getColumnsAmount() - 1)) {
            this.addControl((Gui)null);
         }

         this.buttonAccept = this.addButton(I18n.format("gui.done", new Object[0]));
         this.buttonCancel = this.addButton(I18n.format("gui.cancel", new Object[0]));
      }

      protected void actionPerformed(GuiButton button) throws IOException {
         if (button == this.buttonAccept) {
            this.onAccept();
            this.close();
         } else if (button == this.buttonCancel) {
            this.close();
         }

      }

      protected abstract void onAccept();

      public int getColumnsAmount() {
         return this.columnsAmount;
      }

      public void setColumnsAmount(int columnsAmount) {
         this.columnsAmount = columnsAmount;
      }
   }
}
