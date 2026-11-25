package ivorius.reccomplex.gui.table.screen;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenEditTable<T extends TableDataSource> extends GuiScreenModalTable {
   public static final int HEIGHT_INSET = 10;
   public static final int MAX_WIDTH = 360;
   protected T t;
   protected Consumer<T> saver;
   protected GuiButton escButton;

   public GuiScreenEditTable() {
   }

   public GuiTable setDataSource(T dataSource, Consumer<T> saver) {
      this.saver = saver;
      GuiTable table = new GuiTable(this, this.t = dataSource);
      this.setTable(table);
      return table;
   }

   public int uHeight() {
      return this.height - 20;
   }

   public int uWidth() {
      return Math.min(this.width - 10, 360);
   }

   public int leftEdge() {
      return (this.width - this.uWidth()) / 2;
   }

   public void initGui() {
      if (this.currentTable() != null) {
         this.currentTable().setBounds(Bounds.fromAxes(this.leftEdge(), this.uWidth(), 10, this.uHeight() - 22));
      }

      super.initGui();
      this.buttonList.add(this.escButton = this.tableStack().size() == 1 ? new GuiButton(1, this.leftEdge(), 10 + this.uHeight() - 20, this.uWidth() / 3 - 1, 20, TextFormatting.GOLD + IvTranslations.get("gui.cancel")) : new GuiButton(2, this.leftEdge(), 10 + this.uHeight() - 20, this.uWidth() / 3 - 1, 20, IvTranslations.format("gui.back.page", new Object[]{this.tableStack().size() - 1})));
      this.buttonList.add(new GuiButton(0, this.leftEdge() + this.uWidth() / 3 + 1, 10 + this.uHeight() - 20, this.uWidth() / 3 - 2, 20, TextFormatting.GREEN + IvTranslations.get("reccomplex.gui.save")));
      GuiButton hideButton = new GuiButton(3, this.leftEdge() + this.uWidth() / 3 * 2 + 1, 10 + this.uHeight() - 20, this.uWidth() / 3 - 1, 20, this.currentTable().getDataSource().canVisualize() ? IvTranslations.get("reccomplex.gui.visualizegui") : IvTranslations.get("reccomplex.gui.hidegui"));
      hideButton.enabled = GuiHider.canHide();
      this.buttonList.add(hideButton);
   }

   protected void keyTyped(char keyChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         if (this.escButton != null) {
            this.actionPerformed(this.escButton);
         }

      } else {
         super.keyTyped(keyChar, keyCode);
      }
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (button.id == 0) {
         this.saver.accept(this.t);
         this.mc.player.closeScreen();
      } else if (button.id == 1) {
         this.mc.player.closeScreen();
      } else if (button.id == 2) {
         this.popTable();
      } else if (button.id == 3) {
         if (this.currentTable().getDataSource().canVisualize()) {
            GuiHider.hideGUI(this.currentTable().getDataSource().visualizer());
         } else {
            GuiHider.hideGUI();
         }
      }

   }
}
