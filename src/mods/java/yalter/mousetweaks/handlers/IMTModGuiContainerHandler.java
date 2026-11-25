package yalter.mousetweaks.handlers;

import java.util.ArrayList;
import java.util.List;
import yalter.mousetweaks.IGuiScreenHandler;
import yalter.mousetweaks.MouseButton;
import yalter.mousetweaks.api.IMTModGuiContainer;

public class IMTModGuiContainerHandler implements IGuiScreenHandler {
   protected IMTModGuiContainer modGuiContainer;

   public IMTModGuiContainerHandler(IMTModGuiContainer modGuiContainer) {
      this.modGuiContainer = modGuiContainer;
   }

   public boolean isMouseTweaksDisabled() {
      return this.modGuiContainer.isMouseTweaksDisabled();
   }

   public boolean isWheelTweakDisabled() {
      return this.modGuiContainer.isWheelTweakDisabled();
   }

   public List<agr> getSlots() {
      List<agr> slots = new ArrayList();
      Object container = this.modGuiContainer.getModContainer();
      int count = this.modGuiContainer.getModSlotCount(container);

      for(int i = 0; i < count; ++i) {
         slots.add((agr)this.modGuiContainer.getModSlot(container, i));
      }

      return slots;
   }

   public agr getSlotUnderMouse() {
      Object container = this.modGuiContainer.getModContainer();
      return (agr)this.modGuiContainer.getModSelectedSlot(container, this.modGuiContainer.getModSlotCount(container));
   }

   public boolean disableRMBDraggingFunctionality() {
      this.modGuiContainer.disableRMBDragIfRequired(this.modGuiContainer.getModContainer(), (Object)null, false);
      return false;
   }

   public void clickSlot(agr slot, MouseButton mouseButton, boolean shiftPressed) {
      this.modGuiContainer.clickModSlot(this.modGuiContainer.getModContainer(), slot, mouseButton.getValue(), shiftPressed);
   }

   public boolean isCraftingOutput(agr slot) {
      return this.modGuiContainer.isCraftingOutputSlot(this.modGuiContainer.getModContainer(), slot);
   }

   public boolean isIgnored(agr slot) {
      return false;
   }
}
