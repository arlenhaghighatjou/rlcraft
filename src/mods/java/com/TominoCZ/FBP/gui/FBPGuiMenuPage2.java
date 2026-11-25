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
public class FBPGuiMenuPage2 extends GuiScreen {
   GuiButton Reload;
   GuiButton Done;
   GuiButton Defaults;
   GuiButton Back;
   GuiButton Next;
   GuiButton ReportBug;
   GuiButton Enable;
   GuiButton b1;
   GuiButton b2;
   GuiButton b3;
   GuiButton b4;
   GuiButton b5;
   GuiButton b6;
   String b1Text = "Random Rotation";
   String b2Text = "Cartoon Mode";
   String b3Text = "Randomized Scale";
   String b4Text = "Random Fade Speed";
   String b5Text = "Spawn Redstone Block Particles";
   String b6Text = "Spawn Particles in Freeze Mode";
   String description = "";
   boolean reachedEnd = false;
   long time;
   long lastTime;
   double offsetX = 0.0D;
   int GUIOffsetY = 4;

   public FBPGuiMenuPage2() {
   }

   public void initGui() {
      this.buttonList.clear();
      int x = this.width / 2 - 100;
      this.b1 = new FBPGuiButton(1, x, this.height / 5 - 10 + this.GUIOffsetY, this.b1Text, FBP.randomRotation, true);
      this.b2 = new FBPGuiButton(2, x, this.b1.y + this.b1.height + 1, this.b2Text, FBP.cartoonMode, true);
      this.b3 = new FBPGuiButton(3, x, this.b2.y + this.b2.height + 6, this.b3Text, FBP.randomizedScale, true);
      this.b4 = new FBPGuiButton(4, x, this.b3.y + this.b3.height + 1, this.b4Text, FBP.randomFadingSpeed, true);
      this.b5 = new FBPGuiButton(5, x, this.b4.y + this.b4.height + 6, this.b5Text, FBP.spawnRedstoneBlockParticles, true);
      this.b6 = new FBPGuiButton(6, x, this.b5.y + this.b5.height + 1, this.b6Text, FBP.spawnWhileFrozen, true);
      this.Back = new FBPGuiButton(-3, this.b6.x - 44, this.b6.y + 10 - this.GUIOffsetY, "<<", false, false);
      this.Next = new FBPGuiButton(-5, this.b6.x + this.b6.width + 25, this.b6.y + 10 - this.GUIOffsetY, ">>", false, false);
      this.Defaults = new FBPGuiButton(0, this.width / 2 + 2, this.b6.y + this.b6.height + 24 - this.GUIOffsetY, "Defaults", false, false);
      this.Done = new FBPGuiButton(-1, this.width / 2 - 100, this.Defaults.y, "Done", false, false);
      this.Reload = new FBPGuiButton(-2, this.width / 2 - 100, this.Defaults.y + this.Defaults.height + 1, "Reload Config", false, false);
      this.ReportBug = new FBPGuiButtonBugReport(-4, this.width - 27, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Enable = new FBPGuiButtonEnable(-6, this.width - 25 - 27 - 4, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Defaults.width = this.Done.width = 98;
      this.Reload.width = this.b1.width = this.b2.width = this.b3.width = this.b4.width = this.b5.width = this.b6.width = 200;
      this.Back.width = this.Next.width = 20;
      this.buttonList.addAll(Arrays.asList(this.b1, this.b2, this.b3, this.b4, this.b5, this.b6, this.Defaults, this.Done, this.Reload, this.Back, this.Next, this.Enable, this.ReportBug));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case -6:
         FBP.setEnabled(!FBP.enabled);
         break;
      case -5:
         this.mc.displayGuiScreen(new FBPGuiMenuPage3());
         break;
      case -4:
         try {
            Desktop.getDesktop().browse(new URI("https://github.com/TominoCZ/FancyBlockParticles/issues"));
         } catch (Exception var3) {
            ;
         }
         break;
      case -3:
         this.mc.displayGuiScreen(new FBPGuiMenuPage1());
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
         FBP.randomRotation = !FBP.randomRotation;
         break;
      case 2:
         FBP.cartoonMode = !FBP.cartoonMode;
         break;
      case 3:
         FBP.randomizedScale = !FBP.randomizedScale;
         break;
      case 4:
         FBP.randomFadingSpeed = !FBP.randomFadingSpeed;
         break;
      case 5:
         FBP.spawnRedstoneBlockParticles = !FBP.spawnRedstoneBlockParticles;
         break;
      case 6:
         FBP.spawnWhileFrozen = !FBP.spawnWhileFrozen;
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

   private void getDescription() {
      for(GuiButton b : this.buttonList) {
         if (b.isMouseOver()) {
            switch(b.id) {
            case 1:
               this.description = "Enables §6random §aand §6simple rotation §amath.";
               break;
            case 2:
               this.description = "Makes the particles look §6cartoon§a-ish.";
               break;
            case 3:
               this.description = "Makes the particle §6scale§a slightly §6randomized§a.";
               break;
            case 4:
               this.description = "Enables §6random §aparticle §6fade away§a-transition speed.";
               break;
            case 5:
               this.description = "Allows spawning §6redstone block§a particles.";
               break;
            case 6:
               this.description = "Allows spawning particles in §6freeze mode§a.";
            }
         }
      }

   }
}
