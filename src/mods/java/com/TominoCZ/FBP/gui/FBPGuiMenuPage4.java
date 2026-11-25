package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPGuiMenuPage4 extends GuiScreen {
   GuiButton Reload;
   GuiButton Done;
   GuiButton Defaults;
   GuiButton Back;
   GuiButton ReportBug;
   GuiButton Enable;
   GuiButton b1;
   GuiButton b2;
   GuiButton b3;
   GuiButton b4;
   GuiButton b5;
   GuiButton b6;
   String b1Text = "Fancy Flame";
   String b2Text = "Fancy Smoke";
   String b3Text = "Fancy Rain";
   String b4Text = "Fancy Snow";
   String b5Text = "Water Physics";
   String b6Text = "Rest On Floor";
   String description = "";
   double offsetX = 0.0D;
   int GUIOffsetY = 4;

   public FBPGuiMenuPage4() {
   }

   public void initGui() {
      this.buttonList.clear();
      int x = this.width / 2 - 100;
      this.b1 = new FBPGuiButton(1, x, this.height / 5 - 10 + this.GUIOffsetY, this.b1Text, FBP.fancyFlame, true);
      this.b2 = new FBPGuiButton(2, x, this.b1.y + this.b1.height + 1, this.b2Text, FBP.fancySmoke, true);
      this.b3 = new FBPGuiButton(3, x, this.b2.y + this.b1.height + 6, this.b3Text, FBP.fancyRain, true);
      this.b4 = new FBPGuiButton(4, x, this.b3.y + this.b1.height + 1, this.b4Text, FBP.fancySnow, true);
      this.b5 = new FBPGuiButton(5, x, this.b4.y + this.b1.height + 6, this.b5Text, FBP.waterPhysics, true);
      this.b6 = new FBPGuiButton(6, x, this.b5.y + this.b1.height + 1, this.b6Text, FBP.restOnFloor, true);
      this.Back = new FBPGuiButton(-3, this.width / 2 - 125 - 19, 6 * this.b1.height + this.b1.y - 5 + 10 - this.GUIOffsetY, "<<", false, false);
      this.Defaults = new FBPGuiButton(0, this.width / 2 + 2, 6 * this.b1.height + this.b1.y - 5 + 24 + 20 - this.GUIOffsetY, "Defaults", false, false);
      this.Done = new FBPGuiButton(-1, this.width / 2 - 100, this.Defaults.y, "Done", false, false);
      this.Reload = new FBPGuiButton(-2, this.Done.x, this.Defaults.y + this.Defaults.height + 1, "Reload Config", false, false);
      this.ReportBug = new FBPGuiButtonBugReport(-4, this.width - 27, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Enable = new FBPGuiButtonEnable(-5, this.ReportBug.x - 25 - 4, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Defaults.width = this.Done.width = 98;
      this.Reload.width = this.b1.width = 200;
      this.Back.width = 20;
      this.buttonList.addAll(Arrays.asList(this.b1, this.b2, this.b3, this.b4, this.b5, this.b6, this.Defaults, this.Done, this.Reload, this.Back, this.Enable, this.ReportBug));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case -5:
         FBP.setEnabled(!FBP.enabled);
         break;
      case -4:
         try {
            Desktop.getDesktop().browse(new URI("https://github.com/TominoCZ/FancyBlockParticles/issues"));
         } catch (Exception var3) {
            ;
         }
         break;
      case -3:
         this.mc.displayGuiScreen(new FBPGuiMenuPage3());
         break;
      case -2:
         FBPConfigHandler.init();
         break;
      case -1:
         this.mc.displayGuiScreen((GuiScreen)null);
         break;
      case 0:
         this.mc.displayGuiScreen(new FBPGuiYesNo(this));
         break;
      case 1:
         FBP.fancyFlame = !FBP.fancyFlame;
         break;
      case 2:
         FBP.fancySmoke = !FBP.fancySmoke;
         break;
      case 3:
         FBP.fancyRain = !FBP.fancyRain;
         break;
      case 4:
         FBP.fancySnow = !FBP.fancySnow;
         break;
      case 5:
         FBP.waterPhysics = !FBP.waterPhysics;
         break;
      case 6:
         FBP.restOnFloor = !FBP.restOnFloor;
      }

      if (!FBP.fancyRain && !FBP.fancySnow) {
         this.mc.world.provider.setWeatherRenderer(FBP.originalWeatherRenderer);
      } else {
         this.mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
      }

      FBPConfigHandler.write();
      this.initGui();
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      FBPGuiHelper.background(this.b1.y - 6 - this.GUIOffsetY, this.Done.y - 4, this.width, this.height);
      int posY = this.Done.y - 18;
      this.getDescription();
      if (mouseX >= this.b1.x && mouseX < this.b1.x + this.b1.width && mouseY >= this.b1.y && mouseY < this.b6.y + this.b1.height) {
         this.moveText();
         this.drawCenteredString(this.fontRenderer, this.description, (int)((double)(this.width / 2) + this.offsetX), posY, this.fontRenderer.getColorCode('a'));
      }

      FBPGuiHelper.drawTitle(this.b1.y - this.GUIOffsetY, this.width, this.height, this.fontRenderer);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private void getDescription() {
      for(GuiButton b : this.buttonList) {
         if (b.isMouseOver()) {
            switch(b.id) {
            case 1:
               this.description = "Makes \u00a76flame particles\u00a7a fancy.";
               break;
            case 2:
               this.description = "Makes \u00a76smoke particles\u00a7a fancy.";
               break;
            case 3:
               this.description = "Makes \u00a76rain particles\u00a7a fancy.";
               break;
            case 4:
               this.description = "Makes \u00a76snow particles\u00a7a fancy.";
               break;
            case 5:
               this.description = "Makes \u00a76wood\u00a7a particles \u00a76float\u00a7a and others to \u00a76sink slower\u00a7a.";
               break;
            case 6:
               this.description = "Makes particles rest \u00a76aligned\u00a7a on the ground.";
            }
         }
      }

   }

   private void moveText() {
      int textWidth = this.fontRenderer.getStringWidth(this.description);
      int outsideSizeX = textWidth - this.width;
      if (textWidth > this.width) {
         double speedOfSliding = 2400.0D;
         long time = System.currentTimeMillis();
         float normalValue = (float)((double)time / speedOfSliding % 2.0D);
         if (normalValue > 1.0F) {
            normalValue = 2.0F - normalValue;
         }

         this.offsetX = (double)((float)(outsideSizeX * 2) * normalValue - (float)outsideSizeX);
      } else {
         this.offsetX = 0.0D;
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         for(int i = 0; i < this.buttonList.size(); ++i) {
            GuiButton guibutton = (GuiButton)this.buttonList.get(i);
            if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
               if (!guibutton.isMouseOver()) {
                  return;
               }

               this.actionPerformed(guibutton);
            }
         }
      }

   }
}
