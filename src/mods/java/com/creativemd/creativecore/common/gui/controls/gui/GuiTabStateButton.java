package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class GuiTabStateButton extends GuiControl {
   public String[] states;
   public int selected;

   public GuiTabStateButton(String name, int selected, int x, int y, int height, String... states) {
      super(name, x, y, GuiRenderHelper.instance.getStringWidth(String.join("", states)) + 5 * states.length - 5, height);
      this.selected = selected;
      this.states = states;
      this.marginWidth = 0;
   }

   public void setState(int index) {
      if (this.selected != index) {
         this.selected = index;
         this.raiseEvent(new GuiControlChangedEvent(this));
      }
   }

   public int getState() {
      return this.selected;
   }

   public String getCaption() {
      return this.states[this.selected];
   }

   protected String getDisplay(int index) {
      return this.states[index];
   }

   public boolean mousePressed(int posX, int posY, int button) {
      int x = 0;

      for(int i = 0; i < this.states.length; ++i) {
         int buttonWidth = GuiRenderHelper.instance.getStringWidth(this.states[i]) + 4;
         if (posX >= this.posX + x && posX <= this.posX + x + buttonWidth) {
            this.setState(i);
            playSound(SoundEvents.UI_BUTTON_CLICK);
            return true;
         }

         x += buttonWidth + 1;
      }

      return false;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      int posX = 0;
      Vec3d pos = this.rotateMouseVec(this.getParent().getMousePos());

      for(int i = 0; i < this.states.length; ++i) {
         int buttonWidth = helper.getStringWidth(this.getDisplay(i)) + 4;
         if (i > 0) {
            this.getStyle().getBorder(this).renderStyle(posX - 1, 0, helper, 1, height);
         }

         this.getStyle().getBackground((GuiControl)null).renderStyle(posX, 0, helper, buttonWidth, height);
         if (i == this.selected) {
            if (this.isMouseOver((int)pos.x, (int)pos.y) && pos.x >= (double)(this.posX + posX) && pos.x <= (double)(this.posX + posX + buttonWidth)) {
               this.getStyle().getMouseOverBackground(this).renderStyle(posX, 0, helper, buttonWidth, height);
            }
         } else {
            this.getStyle().getDisableEffect(this).renderStyle(posX, 0, helper, buttonWidth, height);
         }

         helper.font.drawStringWithShadow(this.getDisplay(i), (float)(i + posX + 2), 4.0F, -1);
         posX += buttonWidth + 1;
      }

   }
}
