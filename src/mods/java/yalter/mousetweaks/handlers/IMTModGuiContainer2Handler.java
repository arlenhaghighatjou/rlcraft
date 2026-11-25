package yalter.mousetweaks.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import yalter.mousetweaks.IGuiScreenHandler;
import yalter.mousetweaks.MouseButton;
import yalter.mousetweaks.Reflection;
import yalter.mousetweaks.api.IMTModGuiContainer2;

public class IMTModGuiContainer2Handler implements IGuiScreenHandler {
   protected bib mc = bib.z();
   protected IMTModGuiContainer2 modGuiContainer;
   protected Method handleMouseClick;

   public IMTModGuiContainer2Handler(IMTModGuiContainer2 modGuiContainer) {
      this.modGuiContainer = modGuiContainer;
      this.handleMouseClick = Reflection.getHMCMethod(modGuiContainer);
   }

   public boolean isMouseTweaksDisabled() {
      return this.modGuiContainer.MT_isMouseTweaksDisabled();
   }

   public boolean isWheelTweakDisabled() {
      return this.modGuiContainer.MT_isWheelTweakDisabled();
   }

   public List<agr> getSlots() {
      return this.modGuiContainer.MT_getContainer().c;
   }

   public agr getSlotUnderMouse() {
      return this.modGuiContainer.MT_getSlotUnderMouse();
   }

   public boolean disableRMBDraggingFunctionality() {
      return this.modGuiContainer.MT_disableRMBDraggingFunctionality();
   }

   public void clickSlot(agr slot, MouseButton mouseButton, boolean shiftPressed) {
      if (this.handleMouseClick != null) {
         try {
            this.handleMouseClick.invoke(this.modGuiContainer, slot, slot.e, mouseButton.getValue(), shiftPressed ? afw.b : afw.a);
         } catch (InvocationTargetException var6) {
            b crashreport = b.a(var6, "handleMouseClick() threw an exception when called from MouseTweaks.");
            throw new f(crashreport);
         } catch (IllegalAccessException var7) {
            b crashreport = b.a(var7, "Calling handleMouseClick() from MouseTweaks.");
            throw new f(crashreport);
         }
      } else {
         this.mc.c.a(this.modGuiContainer.MT_getContainer().d, slot.e, mouseButton.getValue(), shiftPressed ? afw.b : afw.a, this.mc.h);
      }

   }

   public boolean isCraftingOutput(agr slot) {
      return this.modGuiContainer.MT_isCraftingOutput(slot);
   }

   public boolean isIgnored(agr slot) {
      return this.modGuiContainer.MT_isIgnored(slot);
   }
}
