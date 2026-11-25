package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import javax.annotation.Nullable;

public interface TableCell {
   @Nullable
   String getID();

   void initGui(GuiTable var1);

   void setBounds(Bounds var1);

   Bounds bounds();

   void setHidden(boolean var1);

   boolean isHidden();

   void draw(GuiTable var1, int var2, int var3, float var4);

   void drawFloating(GuiTable var1, int var2, int var3, float var4);

   void update(GuiTable var1);

   boolean keyTyped(char var1, int var2);

   void mouseClicked(int var1, int var2, int var3);

   void buttonClicked(int var1);
}
