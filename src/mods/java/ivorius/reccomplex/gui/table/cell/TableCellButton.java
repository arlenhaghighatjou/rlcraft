package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.GuiTexturedButton;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellButton extends TableCellDefault {
   protected final GuiTexturedButton button = new GuiTexturedButton(-1, 0, 0, 0, 0, "");
   public String actionID;
   public List<String> tooltip;
   private List<Runnable> listeners = new ArrayList();

   public TableCellButton(String id, String actionID, String title, List<String> tooltip, boolean enabled) {
      super(id);
      this.actionID = actionID;
      this.setTitle(title);
      this.setTooltip(tooltip);
      this.setEnabled(enabled);
   }

   public TableCellButton(String id, String actionID, String title, List<String> tooltip) {
      super(id);
      this.actionID = actionID;
      this.setTitle(title);
      this.setTooltip(tooltip);
   }

   public TableCellButton(String id, String actionID, String title, boolean enabled) {
      super(id);
      this.actionID = actionID;
      this.setTitle(title);
      this.setEnabled(enabled);
   }

   public TableCellButton(String id, String actionID, String title) {
      super(id);
      this.actionID = actionID;
      this.setTitle(title);
   }

   public void addAction(Runnable action) {
      this.listeners.add(action);
   }

   public void removeAction(Runnable action) {
      this.listeners.remove(action);
   }

   public List<Runnable> actions() {
      return Collections.unmodifiableList(this.listeners);
   }

   public String getTitle() {
      return this.button.displayString;
   }

   public void setTitle(String title) {
      this.button.displayString = title;
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      this.button.enabled = enabled;
   }

   public ResourceLocation getTexture() {
      return this.button.getTexture();
   }

   public void setTexture(ResourceLocation texture) {
      this.button.setTexture(texture);
   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      screen.addButton(this, 0, this.button);
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      this.button.visible = !hidden;
   }

   public void buttonClicked(int buttonID) {
      super.buttonClicked(buttonID);

      for(Runnable listener : this.listeners) {
         listener.run();
      }

   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      Bounds.set(this.button, bounds);
   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.drawFloating(screen, mouseX, mouseY, partialTicks);
      if (this.tooltip != null) {
         screen.drawTooltipRect(this.tooltip, Bounds.fromButton(this.button), mouseX, mouseY, this.getFontRenderer());
      }

   }
}
