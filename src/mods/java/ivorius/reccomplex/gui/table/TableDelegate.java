package ivorius.reccomplex.gui.table;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiButton;

public interface TableDelegate {
   @Nonnull
   <T extends GuiButton> T addButtonToTable(T var1);

   void redrawTable();

   void reloadData();

   void setLocked(String var1, boolean var2);
}
