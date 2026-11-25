package goblinbob.mobends.core.client.gui.elements;

import java.util.LinkedList;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public abstract class GuiElement implements IGuiElement, IGuiElementsContainer {
   protected int x;
   protected int y;
   protected IGuiElement parent;
   protected LinkedList<IGuiElement> children;

   public GuiElement(IGuiElement parent, int x, int y) {
      this.parent = parent;
      this.x = x;
      this.y = y;
      this.children = new LinkedList();
   }

   public void initGui() {
      this.children.clear();
   }

   public IGuiElement getParent() {
      return this.parent;
   }

   public LinkedList<IGuiElement> getElements() {
      return this.children;
   }

   public void update(int mouseX, int mouseY) {
      this.updateChildren(mouseX, mouseY);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int button) {
      return this.handleMouseClickedChildren(mouseX, mouseY, button);
   }

   public boolean handleMouseReleased(int mouseX, int mouseY, int button) {
      return this.handleMouseReleasedChildren(mouseX, mouseY, button);
   }

   public void draw(float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.getViewX(), this.getViewY(), 0.0F);
      this.drawBackground(partialTicks);
      this.drawChildren(partialTicks);
      this.drawForeground(partialTicks);
      GlStateManager.popMatrix();
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   protected abstract void drawBackground(float var1);

   protected abstract void drawForeground(float var1);
}
