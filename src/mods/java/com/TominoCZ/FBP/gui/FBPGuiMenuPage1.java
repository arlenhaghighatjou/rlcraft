package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;
import com.TominoCZ.FBP.util.FBPMathUtil;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.vecmath.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPGuiMenuPage1 extends GuiScreen {
   GuiButton Reload;
   GuiButton Done;
   GuiButton Defaults;
   GuiButton Back;
   GuiButton Next;
   GuiButton ReportBug;
   GuiButton Enable;
   FBPGuiSlider WeatherParticleDensity;
   Vector2d lastHandle = new Vector2d(0.0D, 0.0D);
   Vector2d lastSize = new Vector2d(0.0D, 0.0D);
   Vector2d handle = new Vector2d(0.0D, 0.0D);
   Vector2d size = new Vector2d(0.0D, 0.0D);
   long time;
   long lastTime;
   int selected = 0;
   double offsetX = 0.0D;
   int GUIOffsetY = 8;

   public FBPGuiMenuPage1() {
   }

   public void initGui() {
      this.buttonList.clear();
      int x1 = this.width / 2 + 80;
      int x2 = this.width / 2 - 100;
      int X = this.width / 2 - 100;
      this.WeatherParticleDensity = new FBPGuiSlider(X, this.height / 5 - 10 + this.GUIOffsetY, (FBP.weatherParticleDensity - 0.75D) / 4.25D);
      int Y = this.WeatherParticleDensity.y + this.WeatherParticleDensity.height + 2 + 4 * (this.WeatherParticleDensity.height + 1) + 5;
      this.Defaults = new FBPGuiButton(0, this.width / 2 + 2, Y + 20 + 24 - this.GUIOffsetY + 4, "Defaults", false, false);
      this.Done = new FBPGuiButton(-1, x2, this.Defaults.y, "Done", false, false);
      this.Reload = new FBPGuiButton(-2, x2, this.Defaults.y + this.Defaults.height + 1, "Reload Config", false, false);
      this.ReportBug = new FBPGuiButtonBugReport(-4, this.width - 27, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Enable = new FBPGuiButtonEnable(-6, this.width - 25 - 27 - 4, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Defaults.width = this.Done.width = 98;
      this.Reload.width = 200;
      this.Back = new FBPGuiButton(-7, X - 44, Y + 10 - this.GUIOffsetY + 4, "<<", false, false);
      this.Next = new FBPGuiButton(-3, X + 200 + 25, Y + 10 - this.GUIOffsetY + 4, ">>", false, false);
      this.Back.width = this.Next.width = 20;
      this.buttonList.addAll(Arrays.asList(this.WeatherParticleDensity, this.Defaults, this.Done, this.Reload, this.Back, this.Next, this.Enable, this.ReportBug));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      boolean init = true;
      switch(button.id) {
      case -7:
         this.mc.displayGuiScreen(new FBPGuiMenuPage0());
         break;
      case -6:
         FBP.setEnabled(!FBP.enabled);
      case -5:
      default:
         break;
      case -4:
         try {
            Desktop.getDesktop().browse(new URI("https://github.com/TominoCZ/FancyBlockParticles/issues"));
         } catch (Exception var4) {
            ;
         }
         break;
      case -3:
         this.mc.displayGuiScreen(new FBPGuiMenuPage2());
         break;
      case -2:
         FBPConfigHandler.init();
         break;
      case -1:
         this.mc.displayGuiScreen((GuiScreen)null);
         break;
      case 0:
         this.mc.displayGuiScreen(new FBPGuiYesNo(this));
         init = false;
      }

      FBPConfigHandler.write();
      if (init) {
         this.initGui();
      }

   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      FBPGuiHelper.background(this.WeatherParticleDensity.y - 6 - this.GUIOffsetY, this.Done.y - 4, this.width, this.height);
      double sWeatherParticleDensity = FBPMathUtil.round(0.75D + 4.25D * this.WeatherParticleDensity.value, 2);
      FBP.weatherParticleDensity = sWeatherParticleDensity;
      this.drawMouseOverSelection(mouseX, mouseY, partialTicks);
      FBPGuiHelper.drawTitle(this.WeatherParticleDensity.y - this.GUIOffsetY, this.width, this.height, this.fontRenderer);
      this.drawInfo();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private void drawMouseOverSelection(int mouseX, int mouseY, float partialTicks) {
      int posY = this.Done.y - 18;
      if (mouseX >= this.WeatherParticleDensity.x - 2 && mouseX <= this.WeatherParticleDensity.x + this.WeatherParticleDensity.width + 2 && mouseY >= this.WeatherParticleDensity.y && mouseY <= this.WeatherParticleDensity.y + this.WeatherParticleDensity.height - 2) {
         this.handle.y = (double)this.WeatherParticleDensity.y;
         this.size = new Vector2d((double)this.WeatherParticleDensity.width, 18.0D);
         this.selected = 1;
      }

      int step = 1;
      this.time = System.currentTimeMillis();
      if (this.lastTime > 0L) {
         step = (int)(this.time - this.lastTime);
      }

      this.lastTime = this.time;
      if (this.lastHandle != new Vector2d(0.0D, 0.0D)) {
         if (this.lastHandle.y > this.handle.y) {
            if (this.lastHandle.y - this.handle.y <= (double)step) {
               this.lastHandle.y = this.handle.y;
            } else {
               this.lastHandle.y -= (double)step;
            }
         }

         if (this.lastHandle.y < this.handle.y) {
            if (this.handle.y - this.lastHandle.y <= (double)step) {
               this.lastHandle.y = this.handle.y;
            } else {
               this.lastHandle.y += (double)step;
            }
         }

         this.lastHandle.x = (double)this.WeatherParticleDensity.x;
      }

      if (this.lastSize != new Vector2d(0.0D, 0.0D)) {
         if (this.lastSize.y > this.size.y) {
            if (this.lastSize.y - this.size.y <= (double)step) {
               this.lastSize.y = this.size.y;
            } else {
               this.lastSize.y -= (double)step;
            }
         }

         if (this.lastSize.y < this.size.y) {
            if (this.size.y - this.lastSize.y <= (double)step) {
               this.lastSize.y = this.size.y;
            } else {
               this.lastSize.y += (double)step;
            }
         }

         if (this.lastSize.x > this.size.x) {
            this.lastSize.x -= (double)step;
         }

         if (this.lastSize.x < this.size.x) {
            this.lastSize.x += (double)step;
         }

         this.lastSize.x = (double)this.WeatherParticleDensity.width;
      }

      String text = "";
      switch(this.selected) {
      case 1:
         text = "Sets the §6weather particle density §ato §6" + (int)(FBP.weatherParticleDensity * 100.0D) + "%§a.";
         break;
      default:
         text = "";
      }

      if (mouseX >= this.WeatherParticleDensity.x - 2 && mouseX <= this.WeatherParticleDensity.x + this.WeatherParticleDensity.width + 2 && mouseY < this.WeatherParticleDensity.y + this.WeatherParticleDensity.height && mouseY >= this.WeatherParticleDensity.y && (this.lastSize.y <= 20.0D || this.lastSize.y < 50.0D && this.lastSize.y > 20.0D) && this.lastHandle.y >= (double)this.WeatherParticleDensity.y) {
         this.moveText(text);
         if (this.selected <= 5) {
            FBPGuiHelper.drawRect(this.lastHandle.x - 2.0D, this.lastHandle.y + 2.0D, this.lastSize.x + 4.0D, this.lastSize.y - 2.0D, 200, 200, 200, 35);
         }

         this.drawCenteredString(this.fontRenderer, text, (int)((double)(this.width / 2) + this.offsetX), posY, this.fontRenderer.getColorCode('a'));
      }

   }

   private void drawInfo() {
      int posY = this.Done.y - 18;
      String s = "Weather Particle Density [§6" + (int)(FBP.weatherParticleDensity * 100.0D) + "%§f]";
      this.WeatherParticleDensity.displayString = s;
   }

   private void moveText(String text) {
      int textWidth = this.fontRenderer.getStringWidth(text);
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

   public void onGuiClosed() {
      FBPConfigHandler.write();
   }
}
