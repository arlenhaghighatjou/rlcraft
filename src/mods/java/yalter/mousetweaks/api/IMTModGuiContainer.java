package yalter.mousetweaks.api;

/** @deprecated */
@Deprecated
public interface IMTModGuiContainer {
   int getAPIVersion();

   String getModName();

   boolean isMouseTweaksDisabled();

   boolean isWheelTweakDisabled();

   boolean isCraftingOutputSlot(Object var1, Object var2);

   Object getModContainer();

   int getModSlotCount(Object var1);

   Object getModSlot(Object var1, int var2);

   Object getModSelectedSlot(Object var1, int var2);

   void clickModSlot(Object var1, Object var2, int var3, boolean var4);

   void disableRMBDragIfRequired(Object var1, Object var2, boolean var3);
}
