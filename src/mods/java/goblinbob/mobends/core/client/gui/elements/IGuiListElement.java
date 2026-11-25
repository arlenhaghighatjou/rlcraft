package goblinbob.mobends.core.client.gui.elements;

public interface IGuiListElement {
   void initGui(int var1, int var2);

   boolean handleMouseClicked(int var1, int var2, int var3);

   void update(int var1, int var2);

   void draw(float var1);

   int getX();

   int getY();

   int getHeight();

   int getOrder();

   void setOrder(int var1);
}
