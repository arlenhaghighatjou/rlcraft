package ivorius.reccomplex.gui.table.cell;

import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellPresetAction extends TableCellPropertyDefault<String> {
   public static final int DIRECTION_BUTTON_WIDTH = 20;
   protected GuiButton leftButton;
   protected GuiButton rightButton;
   protected final List<TableCellButton> actions = new ArrayList();

   public TableCellPresetAction(String id, List<TableCellButton> actions) {
      super(id, actions.size() > 0 ? ((TableCellButton)actions.get(0)).actionID : "");
      this.actions.addAll(actions);
      this.setPropertyValue((String)this.property);
   }

   public static Stream<TableCellButton> sorted(Stream<TableCellButton> actions) {
      return actions.sorted(Comparator.comparing(TableCellButton::getTitle));
   }

   public void addAction(Consumer<String> consumer) {
      this.actions.forEach((a) -> {
         a.addAction(() -> {
            consumer.accept(a.actionID);
         });
      });
   }

   public List<TableCellButton> getActions() {
      return this.actions;
   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      Bounds bounds = this.bounds();
      int buttonY = bounds.getMinY() + (bounds.getHeight() - 20) / 2;
      int presetButtonWidth = bounds.getWidth() - 40;
      boolean canChange = this.actions.size() > 1 || this.actions.size() == 1 && !Objects.equals(this.getPropertyValue(), ((TableCellButton)this.actions.get(0)).actionID);
      this.leftButton = new GuiButton(-1, bounds.getMinX(), buttonY, 19, 20, TableCellEnum.LEFT_ARROW);
      this.leftButton.visible = !this.isHidden();
      this.leftButton.enabled = canChange;
      screen.addButton(this, 0, this.leftButton);
      this.rightButton = new GuiButton(-1, bounds.getMinX() + 20 + presetButtonWidth + 1, buttonY, 19, 20, TableCellEnum.RIGHT_ARROW);
      this.rightButton.visible = !this.isHidden();
      this.rightButton.enabled = canChange;
      screen.addButton(this, 1, this.rightButton);

      for(TableCellButton action : this.actions) {
         action.initGui(screen);
      }

   }

   public void setBounds(Bounds bounds) {
      super.setBounds(bounds);
      int buttonY = bounds.getMinY() + (bounds.getHeight() - 20) / 2;
      int directionButtonWidth = 20;
      int presetButtonWidth = bounds.getWidth() - directionButtonWidth * 2;
      this.actions.forEach((a) -> {
         a.setBounds(Bounds.fromSize(bounds.getMinX() + directionButtonWidth + 1, buttonY, presetButtonWidth - 2, 20));
      });
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      if (this.leftButton != null) {
         this.leftButton.visible = !hidden;
      }

      if (this.rightButton != null) {
         this.rightButton.visible = !hidden;
      }

      TableCellButton action = this.findAction((String)this.getPropertyValue());
      if (action != null) {
         action.setHidden(hidden);
      }

   }

   public void buttonClicked(int buttonID) {
      super.buttonClicked(buttonID);
      if (buttonID == 0 || buttonID == 1) {
         this.move(buttonID == 0 ? -1 : 1);
      }

   }

   public void setPropertyValue(String value) {
      super.setPropertyValue(value);
      if (this.actions != null) {
         this.actions.forEach((a) -> {
            a.setHidden(true);
         });
         TableCellButton actionB = this.findAction(value);
         if (actionB != null) {
            actionB.setHidden(this.isHidden());
         }
      }

   }

   public void move(int plus) {
      this.setPropertyValue(((TableCellButton)this.actions.get(((this.findIndex((String)this.getPropertyValue()) + plus) % this.actions.size() + this.actions.size()) % this.actions.size())).actionID);
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.draw(screen, mouseX, mouseY, partialTicks);
      TableCellButton action = this.findAction((String)this.getPropertyValue());
      if (action != null) {
         action.draw(screen, mouseX, mouseY, partialTicks);
      }

   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.drawFloating(screen, mouseX, mouseY, partialTicks);
      TableCellButton action = this.findAction((String)this.getPropertyValue());
      if (action != null) {
         action.drawFloating(screen, mouseX, mouseY, partialTicks);
      }

   }

   public TableCellButton findAction(String actionID) {
      int index = this.findIndex(actionID);
      return index >= 0 ? (TableCellButton)this.actions.get(index) : null;
   }

   protected int findIndex(String actionID) {
      int currentIndex = -1;

      for(int i = 0; i < this.actions.size(); ++i) {
         TableCellButton action = (TableCellButton)this.actions.get(i);
         if (action.actionID.equals(actionID)) {
            currentIndex = i;
         }
      }

      return currentIndex;
   }
}
