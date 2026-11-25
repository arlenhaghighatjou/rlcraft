package reborncore.client.gui.builder.slot.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.fluids.RebornFluidTank;

public class ConfigFluidElement extends ElementBase {
   SlotType type;
   RebornFluidTank tank;
   public List<ElementBase> elements = new ArrayList();
   boolean filter = false;

   public ConfigFluidElement(RebornFluidTank tank, SlotType type, int x, int y, GuiBase gui) {
      super(x, y, type.getButtonSprite());
      this.type = type;
      this.tank = tank;
      FluidConfigPopupElement popupElement;
      this.elements.add(popupElement = new FluidConfigPopupElement(x - 22, y - 22, this));
      this.elements.add((new ButtonElement(x + 37, y - 25, Sprite.EXIT_BUTTON)).addReleaseAction((element, gui1, provider, mouseX, mouseY) -> {
         GuiBase.slotConfigType = GuiBase.SlotConfigType.NONE;
         return true;
      }));
      this.elements.add((new CheckBoxElement("Pull In", -1, x - 26, y + 42, "input", 0, Sprite.LIGHT_CHECK_BOX, gui.getMachine(), (checkBoxElement) -> {
         return checkBoxElement.machineBase.fluidConfiguration.autoInput();
      })).addPressAction((element, gui12, provider, mouseX, mouseY) -> {
         popupElement.updateCheckBox((CheckBoxElement)element, "input", gui12);
         return true;
      }));
      this.elements.add((new CheckBoxElement("Pump Out", -1, x - 26, y + 57, "output", 0, Sprite.LIGHT_CHECK_BOX, gui.getMachine(), (checkBoxElement) -> {
         return checkBoxElement.machineBase.fluidConfiguration.autoOutput();
      })).addPressAction((element, gui13, provider, mouseX, mouseY) -> {
         popupElement.updateCheckBox((CheckBoxElement)element, "output", gui13);
         return true;
      }));
      this.setWidth(85);
      this.setHeight(105 + (this.filter ? 15 : 0));
   }

   public void draw(GuiBase gui) {
      super.draw(gui);
      if (this.isHovering) {
         this.drawSprite(gui, this.type.getButtonHoverOverlay(), this.x, this.y);
      }

      this.elements.forEach((elementBase) -> {
         elementBase.draw(gui);
      });
   }

   public SlotType getType() {
      return this.type;
   }
}
