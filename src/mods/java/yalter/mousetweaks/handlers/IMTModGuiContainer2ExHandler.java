package yalter.mousetweaks.handlers;

import java.util.List;
import yalter.mousetweaks.IGuiScreenHandler;
import yalter.mousetweaks.MouseButton;
import yalter.mousetweaks.api.IMTModGuiContainer2Ex;

public class IMTModGuiContainer2ExHandler implements IGuiScreenHandler {
   protected bib mc = bib.z();
   protected IMTModGuiContainer2Ex modGuiContainer;

   public IMTModGuiContainer2ExHandler(IMTModGuiContainer2Ex modGuiContainer) {
      this.modGuiContainer = modGuiContainer;
   }

   public boolean isMouseTweaksDisabled() {
      return this.modGuiContainer.MT_isMouseTweaksDisabled();
   }

   public boolean isWheelTweakDisabled() {
      return this.modGuiContainer.MT_isWheelTweakDisabled();
   }

   public List<agr> getSlots() {
      return this.modGuiContainer.MT_getSlots();
   }

   public agr getSlotUnderMouse() {
      return this.modGuiContainer.MT_getSlotUnderMouse();
   }

   public boolean disableRMBDraggingFunctionality() {
      return this.modGuiContainer.MT_disableRMBDraggingFunctionality();
   }

   public void clickSlot(agr slot, MouseButton mouseButton, boolean shiftPressed) {
      this.modGuiContainer.MT_clickSlot(slot, mouseButton.getValue(), shiftPressed ? afw.b : afw.a);
   }

   public boolean isCraftingOutput(agr slot) {
      return this.modGuiContainer.MT_isCraftingOutput(slot);
   }

   public boolean isIgnored(agr slot) {
      return this.modGuiContainer.MT_isIgnored(slot);
   }
}
