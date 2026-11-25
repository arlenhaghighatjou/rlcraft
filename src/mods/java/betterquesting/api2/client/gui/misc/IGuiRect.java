package betterquesting.api2.client.gui.misc;

public interface IGuiRect extends Comparable<IGuiRect> {
   int getX();

   int getY();

   int getWidth();

   int getHeight();

   int getDepth();

   IGuiRect getParent();

   void setParent(IGuiRect var1);

   boolean contains(int var1, int var2);
}
