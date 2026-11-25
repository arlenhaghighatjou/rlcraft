package yalter.mousetweaks;

import java.util.List;

public interface IGuiScreenHandler {
   boolean isMouseTweaksDisabled();

   boolean isWheelTweakDisabled();

   List<agr> getSlots();

   agr getSlotUnderMouse();

   boolean disableRMBDraggingFunctionality();

   void clickSlot(agr var1, MouseButton var2, boolean var3);

   boolean isCraftingOutput(agr var1);

   boolean isIgnored(agr var1);
}
