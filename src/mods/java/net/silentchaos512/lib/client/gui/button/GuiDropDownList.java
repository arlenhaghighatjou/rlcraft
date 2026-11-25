package net.silentchaos512.lib.client.gui.button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiDropDownList extends GuiButton {
   static final int ELEMENT_WIDTH = 100;
   static final int ELEMENT_HEIGHT = 12;
   private final GuiDropDownList.ExpandDirection expandDirection;
   private final List<GuiDropDownElement> children = new ArrayList();
   private final List<Consumer<GuiDropDownElement>> listeners = new ArrayList();
   private boolean expanded = false;

   public GuiDropDownList(int buttonId, int x, int y, String buttonText, GuiDropDownList.ExpandDirection direction) {
      super(buttonId, x, y, 100, 20, buttonText);
      this.expandDirection = direction;
   }

   public void addElement(GuiDropDownElement element, @Nullable Collection<GuiButton> buttonList) {
      element.parent = this;
      element.visible = this.expanded;
      element.x = this.x + this.expandDirection.offsetX(this);
      element.y = this.y + this.expandDirection.offsetY(this);
      if (this.expandDirection == GuiDropDownList.ExpandDirection.UP) {
         this.children.add(0, element);
      } else {
         this.children.add(element);
      }

      if (buttonList != null) {
         buttonList.add(element);
      }

   }

   public void addListener(Consumer<GuiDropDownElement> listener) {
      this.listeners.add(listener);
   }

   public void mouseReleased(int mouseX, int mouseY) {
      this.setExpanded(!this.expanded);
   }

   void onElementSelected(GuiDropDownElement child) {
      this.listeners.forEach((listener) -> {
         listener.accept(child);
      });
      this.setExpanded(false);
   }

   private void setExpanded(boolean value) {
      this.expanded = value;

      for(GuiDropDownElement b : this.children) {
         b.visible = this.expanded;
      }

      this.enabled = !this.expanded;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      super.drawButton(mc, mouseX, mouseY, partialTicks);
      if (this.expanded) {
         for(GuiDropDownElement child : this.children) {
            child.drawButton(mc, mouseX, mouseY, partialTicks);
         }
      }

   }

   public static enum ExpandDirection {
      UP(0, -1),
      DOWN(0, 1);

      private final int scaleX;
      private final int scaleY;

      private ExpandDirection(int x, int y) {
         this.scaleX = x;
         this.scaleY = y;
      }

      private int offsetX(GuiDropDownList list) {
         return 0;
      }

      private int offsetY(GuiDropDownList list) {
         int offset = this == DOWN ? 7 : 0;
         return this.scaleY * 12 * (list.children.size() + 1) + offset;
      }
   }
}
