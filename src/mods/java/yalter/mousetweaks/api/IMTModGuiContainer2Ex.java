package yalter.mousetweaks.api;

import java.util.List;

public interface IMTModGuiContainer2Ex {
   boolean MT_isMouseTweaksDisabled();

   boolean MT_isWheelTweakDisabled();

   List<agr> MT_getSlots();

   agr MT_getSlotUnderMouse();

   boolean MT_isCraftingOutput(agr var1);

   boolean MT_isIgnored(agr var1);

   boolean MT_disableRMBDraggingFunctionality();

   void MT_clickSlot(agr var1, int var2, afw var3);
}
