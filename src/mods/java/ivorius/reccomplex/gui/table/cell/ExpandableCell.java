package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExpandableCell extends TableCellDefault {
   @Nonnull
   protected TableCellButton camo;
   protected TableCellDefault cell;
   protected boolean expanded;

   public ExpandableCell(String id, String camoTitle, TableCellDefault cell) {
      super(id);
      this.camo = new TableCellButton((String)null, (String)null, "");
      this.setCamoTitle(camoTitle);
      this.cell = cell;
      this.camo.addAction(() -> {
         this.setExpanded(true);
      });
   }

   public ExpandableCell(String camoTitle, TableCellDefault cell) {
      this((String)null, camoTitle, cell);
   }

   public ExpandableCell(String camoTitle, TableCellDefault cell, boolean enabled) {
      this((String)null, camoTitle, cell);
      this.setEnabled(enabled);
   }

   public String getCamoTitle() {
      return this.camo.getTitle();
   }

   public void setCamoTitle(String camoTitle) {
      this.camo.setTitle(camoTitle);
   }

   public TableCell getCell() {
      return this.cell;
   }

   public void setCell(TableCellDefault cell) {
      this.cell = cell;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
      this.setHidden(this.isHidden());
   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      this.cell.setBounds(bounds);
      this.camo.setBounds(bounds);
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      this.cell.setHidden(hidden || !this.expanded);
      this.camo.setHidden(hidden || this.expanded);
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      this.cell.setEnabled(enabled);
      this.camo.setEnabled(enabled);
   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      this.cell.initGui(screen);
      this.camo.initGui(screen);
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.draw(screen, mouseX, mouseY, partialTicks);
      if (this.expanded) {
         this.cell.draw(screen, mouseX, mouseY, partialTicks);
      } else {
         this.camo.draw(screen, mouseX, mouseY, partialTicks);
      }

   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.drawFloating(screen, mouseX, mouseY, partialTicks);
      if (this.expanded) {
         this.cell.drawFloating(screen, mouseX, mouseY, partialTicks);
      } else {
         this.camo.drawFloating(screen, mouseX, mouseY, partialTicks);
      }

   }

   public void update(GuiTable screen) {
      super.update(screen);
      this.cell.update(screen);
      this.camo.update(screen);
   }

   public boolean keyTyped(char keyChar, int keyCode) {
      if (this.expanded) {
         return this.cell.keyTyped(keyChar, keyCode) || super.keyTyped(keyChar, keyCode);
      } else {
         return this.camo.keyTyped(keyChar, keyCode) || super.keyTyped(keyChar, keyCode);
      }
   }

   public void mouseClicked(int button, int x, int y) {
      super.mouseClicked(button, x, y);
      if (this.expanded) {
         this.cell.mouseClicked(button, x, y);
      } else {
         this.camo.mouseClicked(button, x, y);
      }

   }

   public void buttonClicked(int buttonID) {
      super.buttonClicked(buttonID);
      if (this.expanded) {
         this.cell.buttonClicked(buttonID);
      } else {
         this.camo.buttonClicked(buttonID);
      }

   }
}
