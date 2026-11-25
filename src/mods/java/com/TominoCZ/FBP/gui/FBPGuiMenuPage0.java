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
public class FBPGuiMenuPage0 extends GuiScreen {
   GuiButton Reload;
   GuiButton Done;
   GuiButton Defaults;
   GuiButton Next;
   GuiButton ReportBug;
   GuiButton Enable;
   GuiButton InfiniteDuration;
   GuiButton TimeUnit;
   FBPGuiSlider MinDurationSlider;
   FBPGuiSlider MaxDurationSlider;
   FBPGuiSlider ParticleCountBase;
   FBPGuiSlider ScaleMultSlider;
   FBPGuiSlider GravitiyForceSlider;
   FBPGuiSlider RotSpeedSlider;
   Vector2d lastHandle = new Vector2d(0.0D, 0.0D);
   Vector2d lastSize = new Vector2d(0.0D, 0.0D);
   Vector2d handle = new Vector2d(0.0D, 0.0D);
   Vector2d size = new Vector2d(0.0D, 0.0D);
   long time;
   long lastTime;
   int selected = 0;
   double offsetX = 0.0D;
   boolean canChangeTimeUnit = true;
   int GUIOffsetY = 8;

   public FBPGuiMenuPage0() {
   }

   public void initGui() {
      this.buttonList.clear();
      int x1 = this.width / 2 + 80;
      int x2 = this.width / 2 - 100;
      int X = this.width / 2 - 100;
      this.MinDurationSlider = new FBPGuiSlider(X, this.height / 5 - 10 + this.GUIOffsetY, (double)(FBP.minAge - 10) / 90.0D);
      this.MaxDurationSlider = new FBPGuiSlider(X, this.MinDurationSlider.y + this.MinDurationSlider.height + 1, (double)(FBP.maxAge - 10) / 90.0D);
      this.ParticleCountBase = new FBPGuiSlider(X, this.MaxDurationSlider.y + 6 + this.MaxDurationSlider.height, (double)(FBP.particlesPerAxis - 2) / 3.0D);
      this.ScaleMultSlider = new FBPGuiSlider(X, this.ParticleCountBase.y + this.ParticleCountBase.height + 1, (FBP.scaleMult - 0.75D) / 0.5D);
      this.GravitiyForceSlider = new FBPGuiSlider(X, this.ScaleMultSlider.y + this.ScaleMultSlider.height + 6, (FBP.gravityMult - 0.05D) / 2.95D);
      this.RotSpeedSlider = new FBPGuiSlider(X, this.GravitiyForceSlider.y + this.GravitiyForceSlider.height + 1, FBP.rotationMult / 1.5D);
      this.InfiniteDuration = new FBPGuiButton(11, x1 + 25, this.MinDurationSlider.y + 10, (FBP.infiniteDuration ? "§a" : "§c") + "∞", false, false);
      this.TimeUnit = new FBPGuiButton(12, x2 - 25, this.MinDurationSlider.y + 10, "§a§L" + (FBP.showInMillis ? "ms" : "ti"), false, false);
      this.Defaults = new FBPGuiButton(0, this.width / 2 + 2, this.RotSpeedSlider.y + this.RotSpeedSlider.height + 24 - this.GUIOffsetY, "Defaults", false, false);
      this.Done = new FBPGuiButton(-1, x2, this.Defaults.y, "Done", false, false);
      this.Reload = new FBPGuiButton(-2, x2, this.Defaults.y + this.Defaults.height + 1, "Reload Config", false, false);
      this.ReportBug = new FBPGuiButtonBugReport(-4, this.width - 27, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Enable = new FBPGuiButtonEnable(-6, this.width - 25 - 27 - 4, 2, new Dimension(this.width, this.height), this.fontRenderer);
      this.Defaults.width = this.Done.width = 98;
      this.Reload.width = 200;
      this.Next = new FBPGuiButton(-3, this.RotSpeedSlider.x + this.RotSpeedSlider.width + 25, this.RotSpeedSlider.y + 10 - this.GUIOffsetY, ">>", false, false);
      this.InfiniteDuration.width = this.TimeUnit.width = this.Next.width = 20;
      this.buttonList.addAll(Arrays.asList(this.MinDurationSlider, this.MaxDurationSlider, this.ParticleCountBase, this.ScaleMultSlider, this.GravitiyForceSlider, this.RotSpeedSlider, this.InfiniteDuration, this.TimeUnit, this.Defaults, this.Done, this.Reload, this.Next, this.Enable, this.ReportBug));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      boolean init = true;
      switch(button.id) {
      case -6:
         FBP.setEnabled(!FBP.enabled);
         break;
      case -5:
         if (!this.canChangeTimeUnit) {
            return;
         }

         FBP.showInMillis = !FBP.showInMillis;
         init = false;
         break;
      case -4:
         try {
            Desktop.getDesktop().browse(new URI("https://github.com/TominoCZ/FancyBlockParticles/issues"));
         } catch (Exception var4) {
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
         init = false;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      default:
         break;
      case 11:
         this.InfiniteDuration.displayString = ((FBP.infiniteDuration = !FBP.infiniteDuration) ? "§a" : "§c") + "∞";
         init = false;
         break;
      case 12:
         this.TimeUnit.displayString = "§a§L" + ((FBP.showInMillis = !FBP.showInMillis) ? "ms" : "ti");
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
      FBPGuiHelper.background(this.MinDurationSlider.y - 6 - this.GUIOffsetY, this.Done.y - 4, this.width, this.height);
      int sParticleCountBase = (int)Math.round(2.0D + 3.0D * this.ParticleCountBase.value);
      int sMinAge = (int)(10.0D + 90.0D * this.MinDurationSlider.value);
      int sMaxAge = (int)(10.0D + 90.0D * this.MaxDurationSlider.value);
      double sScaleMult = FBPMathUtil.round(0.75D + 0.5D * this.ScaleMultSlider.value, 2);
      double sGravityForce = FBPMathUtil.round(0.05D + 2.95D * this.GravitiyForceSlider.value, 2);
      double sRotSpeed = FBPMathUtil.round(1.5D * this.RotSpeedSlider.value, 2);
      if (FBP.maxAge < sMinAge) {
         FBP.maxAge = sMinAge;
         this.MaxDurationSlider.value = (double)(FBP.maxAge - 10) / 90.0D;
      }

      if (FBP.minAge > sMaxAge) {
         FBP.minAge = sMaxAge;
         this.MinDurationSlider.value = (double)(FBP.minAge - 10) / 90.0D;
      }

      FBP.minAge = sMinAge;
      FBP.maxAge = sMaxAge;
      FBP.scaleMult = sScaleMult;
      FBP.gravityMult = sGravityForce;
      FBP.rotationMult = sRotSpeed;
      FBP.particlesPerAxis = sParticleCountBase;
      this.ParticleCountBase.value = (double)(FBP.particlesPerAxis - 2) / 3.0D;
      this.canChangeTimeUnit = !this.isMouseOverSliders(mouseX, mouseY);
      this.drawMouseOverSelection(mouseX, mouseY, partialTicks);
      FBPGuiHelper.drawTitle(this.MinDurationSlider.y - this.GUIOffsetY, this.width, this.height, this.fontRenderer);
      this.update();
      this.drawInfo();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private void drawMouseOverSelection(int mouseX, int mouseY, float partialTicks) {
      int posY = this.Done.y - 18;
      if (mouseX >= this.MinDurationSlider.x - 2 && mouseX <= this.MinDurationSlider.x + this.MinDurationSlider.width + 2 && mouseY >= this.MinDurationSlider.y && mouseY <= this.MaxDurationSlider.y + this.MaxDurationSlider.height - 2) {
         this.handle.y = (double)this.MinDurationSlider.y;
         this.size = new Vector2d((double)this.MinDurationSlider.width, 39.0D);
         this.selected = 1;
      } else if (mouseX >= this.ParticleCountBase.x && mouseX <= this.ParticleCountBase.x + this.ParticleCountBase.width && mouseY >= this.ParticleCountBase.y + 1 && mouseY <= this.ParticleCountBase.y + this.ParticleCountBase.height - 1 - 1) {
         this.handle.y = (double)this.ParticleCountBase.y;
         this.size = new Vector2d((double)this.ParticleCountBase.width, 18.0D);
         this.selected = 2;
      } else if (mouseX >= this.ScaleMultSlider.x && mouseX <= this.ScaleMultSlider.x + this.ScaleMultSlider.width && mouseY >= this.ScaleMultSlider.y + 1 && mouseY <= this.ScaleMultSlider.y + this.ScaleMultSlider.height - 1 - 1) {
         this.handle.y = (double)this.ScaleMultSlider.y;
         this.size = new Vector2d((double)this.ScaleMultSlider.width, 18.0D);
         this.selected = 3;
      } else if (mouseX >= this.GravitiyForceSlider.x && mouseX <= this.GravitiyForceSlider.x + this.GravitiyForceSlider.width && mouseY >= this.GravitiyForceSlider.y + 1 && mouseY <= this.GravitiyForceSlider.y + this.GravitiyForceSlider.height - 1) {
         this.handle.y = (double)this.GravitiyForceSlider.y;
         this.size = new Vector2d((double)this.GravitiyForceSlider.width, 18.0D);
         this.selected = 4;
      } else if (mouseX >= this.RotSpeedSlider.x && mouseX <= this.RotSpeedSlider.x + this.RotSpeedSlider.width && mouseY >= this.RotSpeedSlider.y + 1 && mouseY <= this.RotSpeedSlider.y + this.RotSpeedSlider.height - 1) {
         this.handle.y = (double)this.RotSpeedSlider.y;
         this.size = new Vector2d((double)(this.RotSpeedSlider.x - (this.RotSpeedSlider.x + this.RotSpeedSlider.width)), 18.0D);
         this.selected = 5;
      } else if (this.InfiniteDuration.isMouseOver()) {
         this.selected = 6;
      } else if (this.TimeUnit.isMouseOver()) {
         this.selected = 7;
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

         this.lastHandle.x = (double)this.MinDurationSlider.x;
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

         this.lastSize.x = (double)this.GravitiyForceSlider.width;
      }

      String text = "";
      switch(this.selected) {
      case 1:
         if (!FBP.infiniteDuration) {
            String _text = FBP.minAge != FBP.maxAge ? "range§a to between §6" + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + "§a and §6" + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + (FBP.showInMillis ? "ms" : (FBP.maxAge > 1 ? " ticks" : " tick")) : "§ato §6" + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + (FBP.showInMillis ? "ms" : (FBP.maxAge > 1 ? " ticks" : " tick"));
            text = "Sets §6particle life duration " + _text + "§a.";
         } else {
            text = "Sets §6particle life duration §ato §6infinity§a.";
         }
         break;
      case 2:
         text = "Sets the §6block destroy particle count§a to §6" + (int)Math.pow((double)FBP.particlesPerAxis, 3.0D) + " §c[§6" + FBP.particlesPerAxis + "^3§c]§a.";
         break;
      case 3:
         text = "Sets §6particle scale multiplier §ato §6" + FBP.scaleMult + "§a.";
         break;
      case 4:
         text = "Multiplies §6default particle gravity force§a by §6" + FBP.gravityMult + "§a.";
         break;
      case 5:
         text = "Multiplies §6particle rotation§a by §6" + FBP.rotationMult + "§a.";
         break;
      case 6:
         text = (FBP.infiniteDuration ? "§cDisable" : "Enable") + " §6infinite particle life duration§a.";
         break;
      case 7:
         text = "Show the time in §6" + (!FBP.showInMillis ? "milliseconds" : "ticks") + "§a.";
         break;
      default:
         text = "";
      }

      if (mouseX >= this.MinDurationSlider.x - 2 && mouseX <= this.MinDurationSlider.x + this.MinDurationSlider.width + 2 && mouseY < this.RotSpeedSlider.y + this.RotSpeedSlider.height && mouseY >= this.MinDurationSlider.y && (this.lastSize.y <= 20.0D || this.lastSize.y < 50.0D && this.lastSize.y > 20.0D) && this.lastHandle.y >= (double)this.MinDurationSlider.y || this.InfiniteDuration.isMouseOver() || this.TimeUnit.isMouseOver()) {
         this.moveText(text);
         if (this.selected <= 5) {
            FBPGuiHelper.drawRect(this.lastHandle.x - 2.0D, this.lastHandle.y + 2.0D, this.lastSize.x + 4.0D, this.lastSize.y - 2.0D, 200, 200, 200, 35);
         }

         this.drawCenteredString(this.fontRenderer, text, (int)((double)(this.width / 2) + this.offsetX), posY, this.fontRenderer.getColorCode('a'));
      }

   }

   private void drawInfo() {
      int posY = this.Done.y - 18;
      String s = "Destroy Particle Count [§6" + (int)Math.pow((double)FBP.particlesPerAxis, 3.0D) + "§f]";
      this.ParticleCountBase.displayString = s;
      if (FBP.infiniteDuration) {
         s = "Min. Duration [§6∞" + (FBP.showInMillis ? " ms" : " ticks") + "§f]";
      } else {
         s = "Min. Duration [§6" + (FBP.showInMillis ? FBP.minAge * 50 + "ms" : FBP.minAge + (FBP.minAge > 1 ? " ticks" : " tick")) + "§f]";
      }

      this.MinDurationSlider.displayString = s;
      if (FBP.infiniteDuration) {
         s = "Max. Duration [§6∞" + (FBP.showInMillis ? " ms" : " ticks") + "§f]";
      } else {
         s = "Max. Duration [§6" + (FBP.showInMillis ? FBP.maxAge * 50 + "ms" : FBP.maxAge + (FBP.maxAge > 1 ? " ticks" : " tick")) + "§f]";
      }

      this.MaxDurationSlider.displayString = s;
      this.ScaleMultSlider.displayString = "Scale Mult. [§6" + FBP.scaleMult + "§f]";
      this.GravitiyForceSlider.displayString = "Gravity Force Mult. [§6" + FBP.gravityMult + "§f]";
      this.RotSpeedSlider.displayString = "Rotation Speed Mult. [§6" + (FBP.rotationMult != 0.0D ? FBP.rotationMult : "§cOFF") + "§f]";
   }

   boolean isMouseOverSliders(int mouseX, int mouseY) {
      return this.MinDurationSlider.isMouseOver(mouseX, mouseY) || this.MaxDurationSlider.isMouseOver(mouseX, mouseY);
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

   void update() {
      this.MinDurationSlider.enabled = !FBP.infiniteDuration;
      this.MaxDurationSlider.enabled = !FBP.infiniteDuration;
   }

   public void onGuiClosed() {
      FBPConfigHandler.write();
   }
}
