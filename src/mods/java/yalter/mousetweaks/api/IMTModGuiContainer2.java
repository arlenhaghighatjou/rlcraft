package yalter.mousetweaks.api;

public interface IMTModGuiContainer2 {
   boolean MT_isMouseTweaksDisabled();

   boolean MT_isWheelTweakDisabled();

   afr MT_getContainer();

   agr MT_getSlotUnderMouse();

   boolean MT_isCraftingOutput(agr var1);

   boolean MT_isIgnored(agr var1);

   boolean MT_disableRMBDraggingFunctionality();
}
