package goblinbob.mobends.core.client.gui.elements;

public interface IGuiElement extends IGuiPositioned {
   void update(int var1, int var2);

   boolean handleMouseClicked(int var1, int var2, int var3);

   boolean handleMouseReleased(int var1, int var2, int var3);

   void initGui();

   void draw(float var1);

   IGuiElement getParent();

   default float getViewX() {
      return (float)this.getX();
   }

   default float getViewY() {
      return (float)this.getY();
   }

   default float getAbsoluteX() {
      IGuiElement parent = this.getParent();
      return parent != null ? parent.getAbsoluteX() + this.getViewX() : (float)this.getX();
   }

   default float getAbsoluteY() {
      IGuiElement parent = this.getParent();
      return parent != null ? parent.getAbsoluteY() + this.getViewY() : (float)this.getY();
   }
}
