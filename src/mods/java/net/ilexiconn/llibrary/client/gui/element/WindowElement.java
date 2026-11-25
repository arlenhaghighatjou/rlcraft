package net.ilexiconn.llibrary.client.gui.element;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WindowElement<T extends IElementGUI> extends Element<T> {
   private String name;
   private float dragOffsetX;
   private float dragOffsetY;
   private boolean isDragging;
   private boolean hasCloseButton;
   private List<Element<T>> elementList;

   public WindowElement(T gui, String name, int width, int height) {
      this(gui, name, width, height, gui.getWidth() / 2 - width / 2, gui.getHeight() / 2 - height / 2, true);
   }

   public WindowElement(T gui, String name, int width, int height, boolean hasCloseButton) {
      this(gui, name, width, height, gui.getWidth() / 2 - width / 2, gui.getHeight() / 2 - height / 2, hasCloseButton);
   }

   public WindowElement(T handler, String name, int width, int height, int posX, int posY, boolean hasCloseButton) {
      super(handler, (float)posX, (float)posY, width, height);
      this.elementList = new ArrayList();
      this.name = name;
      this.hasCloseButton = hasCloseButton;
      if (hasCloseButton) {
         this.addElement((new ButtonElement(this.gui, "x", (float)(this.getWidth() - 14), 0.0F, 14, 14, (v) -> {
            this.gui.removeElement(this);
            return (void)true;
         })).withColorScheme(ButtonElement.CLOSE));
      }

   }

   public void addElement(Element<T> element) {
      element.withParent(this);
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      GlStateManager.pushMatrix();
      this.startScissor();
      this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)this.getHeight(), LLibrary.CONFIG.getPrimaryColor());
      this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), 14.0D, LLibrary.CONFIG.getAccentColor());
      FontRenderer fontRenderer = this.gui.getFontRenderer();
      fontRenderer.drawString(this.name, this.getPosX() + 2.0F, this.getPosY() + 3.0F, LLibrary.CONFIG.getTextColor(), false);

      for(Element<T> element : this.elementList) {
         element.render(mouseX, mouseY, partialTicks);
      }

      GlStateManager.popMatrix();
      this.endScissor();
   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (button == 0 && this.isSelected(mouseX, mouseY)) {
         if (mouseY < this.getPosY() + 14.0F) {
            this.dragOffsetX = mouseX - this.getPosX();
            this.dragOffsetY = mouseY - this.getPosY();
            this.isDragging = true;
            this.gui.sendElementToFront(this);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
      if (this.isDragging) {
         this.setPosX(Math.min(Math.max(mouseX - this.dragOffsetX, 0.0F), (float)(this.gui.getWidth() - this.getWidth())));
         this.setPosY(Math.min(Math.max(mouseY - this.dragOffsetY, 0.0F), (float)(this.gui.getHeight() - this.getHeight())));
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(float mouseX, float mouseY, int button) {
      this.isDragging = false;
      return false;
   }

   public boolean keyPressed(char character, int keyCode) {
      return false;
   }
}
