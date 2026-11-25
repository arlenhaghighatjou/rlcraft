package com.blamejared.ctgui.api;

import com.blamejared.ctgui.client.gui.GuiMenu;
import crafttweaker.mc1120.CraftTweaker;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

public abstract class GuiBase extends GuiContainer {
   public SlotRecipe selectedSlot;
   public GuiTextField editingField;
   public GuiButton add;
   public GuiButton remove;
   protected ContainerBase container;
   private GuiMenu menu = new GuiMenu();
   private boolean shouldOpenMenu;

   public GuiBase(ContainerBase container, int xSize, int ySize, boolean shouldOpenMenu) {
      super(container);
      this.container = container;
      this.xSize = xSize;
      this.ySize = ySize;
      this.shouldOpenMenu = shouldOpenMenu;
   }

   public static boolean isBlock(ItemStack stack) {
      ResourceLocation name = (ResourceLocation)Block.REGISTRY.getNameForObject(Block.getBlockFromItem(stack.getItem()));
      return !name.toString().equals("minecraft:air") && Block.REGISTRY.containsKey(name);
   }

   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      if (this.shouldOpenMenu) {
         this.menu.open(this, this.guiLeft - 102, this.guiTop + 2);
      }

      this.editingField = new GuiTextField(2906, this.fontRenderer, this.guiLeft - 104, this.guiTop - 26, this.xSize + 102, 20);
      this.editingField.setMaxStringLength(Integer.MAX_VALUE);
      this.editingField.setEnabled(true);
      this.add = new GuiButton(2906, this.getGuiLeft() + 86, this.getGuiTop() + 8, this.fontRenderer.getStringWidth("Add") + 4, 20, "Add");
      this.getButtonList().add(this.add);
      this.remove = new GuiButton(2907, this.getGuiLeft() + 86 + 2 + this.fontRenderer.getStringWidth("Add") + 4, this.getGuiTop() + 8, this.fontRenderer.getStringWidth("Remove") + 4, 20, "Remove");
      this.getButtonList().add(this.remove);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.editingField.drawTextBox();
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   private void clearMenu() {
      if (this.shouldOpenMenu) {
         this.menu.setState(false);
         this.menu.toggle(false);

         for(Pair<CheckButtonRecipe, Slider[]> but : this.menu.getButtons()) {
            for(Slider slider : (Slider[])but.getValue()) {
               slider.setValue(0.0D);
               slider.maxValue = 0.0D;
               slider.updateSlider();
               this.buttonList.remove(slider);
            }
         }
      }

   }

   public void updateScreen() {
      super.updateScreen();
      if (this.selectedSlot != null) {
         if (!this.selectedSlot.getHasStack()) {
            this.selectedSlot = null;
            this.editingField.setText("");
            this.clearMenu();
         } else {
            this.editingField.setText(this.selectedSlot.getItemString());
         }
      } else {
         this.editingField.setText("");
         this.clearMenu();
      }

      this.editingField.updateCursorCounter();
      boolean[] canCreate = new boolean[]{true};
      this.container.getRecipeSlots().stream().filter((slot) -> {
         return slot instanceof SlotRecipeOutput;
      }).filter((slot) -> {
         return !slot.getHasStack();
      }).forEach((slot) -> {
         canCreate[0] = false;
      });
      if (!canCreate[0]) {
         this.add.enabled = false;
         this.remove.enabled = false;
      } else {
         this.add.enabled = true;
         this.remove.enabled = true;
      }

      if (this.shouldOpenMenu && this.selectedSlot != null && this.selectedSlot.getHasStack() && !this.selectedSlot.getStack().hasTagCompound()) {
         ((CheckButtonRecipe)this.menu.nbt.getLeft()).enabled = false;
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      boolean[] clickedSlot = new boolean[]{false};

      for(SlotRecipe slot : this.container.getRecipeSlots()) {
         Rectangle rectangle = new Rectangle(this.guiLeft + slot.xPos, this.guiTop + slot.yPos, 18, 18);
         if (rectangle.contains(mouseX, mouseY)) {
            switch(mouseButton) {
            case 0:
            default:
               break;
            case 1:
               if (!slot.getHasStack() || this.editingField == null) {
                  break;
               }

               clickedSlot[0] = true;
               this.selectedSlot = slot;
               this.editingField.setText(slot.getItemString());
               if (!this.shouldOpenMenu) {
                  break;
               }

               this.menu.toggle(true);

               for(Pair<CheckButtonRecipe, Slider[]> but : this.menu.getButtons()) {
                  this.buttonList.removeAll(Arrays.asList(but.getValue()));
               }

               this.menu.getFromMap(slot.getPropertyMap(), slot.getProperties());
               break;
            case 2:
               clickedSlot[0] = true;
               this.selectedSlot = null;
               if (this.shouldOpenMenu) {
                  this.menu.toggle(false);

                  for(Pair<CheckButtonRecipe, Slider[]> but : this.menu.getButtons()) {
                     for(Slider slider : (Slider[])but.getValue()) {
                        slider.updateSlider();
                        this.buttonList.remove(slider);
                     }
                  }
               }

               if (this.editingField != null) {
                  this.editingField.setText("");
               }
            }
         }
      }

      if (!clickedSlot[0]) {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      }

   }

   public abstract String getOutputAdd();

   public abstract String getOutputRemove();

   public String getFileName() {
      return "recipes";
   }

   protected void actionPerformed(GuiButton btn) {
      if (btn.equals(this.add)) {
         File scriptFile = new File(new File("scripts"), String.format("/%s.zs", this.getFileName()));
         if (!scriptFile.exists()) {
            this.generateFile(scriptFile);
         }

         try {
            List<String> lines = new LinkedList();
            BufferedReader reader = new BufferedReader(new FileReader(scriptFile));

            String line;
            while((line = reader.readLine()) != null) {
               lines.add(line);
            }

            if (lines.isEmpty()) {
               this.generateFile(scriptFile);

               while((line = reader.readLine()) != null) {
                  lines.add(line);
               }
            }

            reader.close();
            PrintWriter writer = new PrintWriter(new FileWriter(scriptFile));

            for(int i = 0; i < lines.size(); ++i) {
               String beforeLine = "";
               if (i > 0) {
                  beforeLine = (String)lines.get(i - 1);
               }

               String lined = (String)lines.get(i);
               if (beforeLine.trim().equals("//#Add")) {
                  writer.println(this.getOutputAdd());
               }

               if (!lined.isEmpty()) {
                  writer.println(lined);
               }
            }

            writer.close();
         } catch (IOException var23) {
            CraftTweaker.LOG.catching(var23);
         }
      }

      if (btn.equals(this.remove)) {
         File scriptFile = new File(new File("scripts"), String.format("/%s.zs", this.getFileName()));
         if (!scriptFile.exists()) {
            this.generateFile(scriptFile);
         }

         try {
            BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
            Throwable var26 = null;

            try {
               List<String> lines = new LinkedList();

               String line;
               while((line = reader.readLine()) != null) {
                  lines.add(line);
               }

               if (lines.isEmpty()) {
                  this.generateFile(scriptFile);

                  while((line = reader.readLine()) != null) {
                     lines.add(line);
                  }
               }

               PrintWriter writer = new PrintWriter(new FileWriter(scriptFile));

               for(int i = 0; i < lines.size(); ++i) {
                  String beforeLine = "";
                  if (i > 0) {
                     beforeLine = (String)lines.get(i - 1);
                  }

                  String lined = (String)lines.get(i);
                  if (beforeLine.trim().equals("//#Remove")) {
                     writer.println(this.getOutputRemove());
                  }

                  if (!lined.isEmpty()) {
                     writer.println(lined);
                  }
               }

               writer.close();
            } catch (Throwable var20) {
               var26 = var20;
               throw var20;
            } finally {
               if (reader != null) {
                  if (var26 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var19) {
                        var26.addSuppressed(var19);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (IOException var22) {
            CraftTweaker.LOG.catching(var22);
         }
      }

   }

   public abstract ResourceLocation getTexture();

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.pushAttrib();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().renderEngine.bindTexture(this.getTexture());
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      GlStateManager.popAttrib();
      if (this.selectedSlot != null) {
         GlStateManager.colorMask(true, true, true, false);
         this.drawGradientRect(this.guiLeft + this.selectedSlot.xPos, this.guiTop + this.selectedSlot.yPos, this.guiLeft + this.selectedSlot.xPos + 16, this.guiTop + this.selectedSlot.yPos + 16, Color.cyan.darker().getRGB(), Color.cyan.darker().getRGB());
         GlStateManager.colorMask(true, true, true, true);
      }

   }

   public List<GuiButton> getButtonList() {
      return this.buttonList;
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

   public GuiMenu getMenu() {
      return this.menu;
   }

   public void generateFile(File f) {
      try {
         f.createNewFile();
         PrintWriter writer = new PrintWriter(new FileWriter(f));
         writer.println("//This file was created via CT-GUI! Editing it is not advised!");
         writer.println("//Don't touch me!");
         writer.println("//#Remove");
         writer.println();
         writer.println("//Don't touch me!");
         writer.println("//#Add");
         writer.println();
         writer.println("//File End");
         writer.close();
      } catch (IOException var3) {
         CraftTweaker.LOG.catching(var3);
      }

   }
}
