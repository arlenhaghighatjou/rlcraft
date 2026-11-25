package yalter.mousetweaks.handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.lwjgl.input.Mouse;
import yalter.mousetweaks.Constants;
import yalter.mousetweaks.IGuiScreenHandler;
import yalter.mousetweaks.MouseButton;
import yalter.mousetweaks.Reflection;
import yalter.mousetweaks.api.MouseTweaksDisableWheelTweak;
import yalter.mousetweaks.api.MouseTweaksIgnore;

public class GuiContainerHandler implements IGuiScreenHandler {
   protected bib mc = bib.z();
   protected bmg guiContainer;
   protected Method handleMouseClick;

   public GuiContainerHandler(bmg guiContainer) {
      this.guiContainer = guiContainer;
      this.handleMouseClick = Reflection.getHMCMethod(guiContainer);
   }

   private int getDisplayWidth() {
      return this.mc.d;
   }

   private int getDisplayHeight() {
      return this.mc.e;
   }

   private int getRequiredMouseX() {
      return Mouse.getX() * this.guiContainer.l / this.getDisplayWidth();
   }

   private int getRequiredMouseY() {
      return this.guiContainer.m - Mouse.getY() * this.guiContainer.m / this.getDisplayHeight() - 1;
   }

   public boolean isMouseTweaksDisabled() {
      return this.guiContainer.getClass().isAnnotationPresent(MouseTweaksIgnore.class) || Reflection.guiContainerClass == null;
   }

   public boolean isWheelTweakDisabled() {
      return this.guiContainer.getClass().isAnnotationPresent(MouseTweaksDisableWheelTweak.class);
   }

   public List<agr> getSlots() {
      return this.guiContainer.h.c;
   }

   public agr getSlotUnderMouse() {
      try {
         return (agr)Reflection.guiContainerClass.invokeMethod(this.guiContainer, Constants.GETSLOTATPOSITION_NAME.forgeName, this.getRequiredMouseX(), this.getRequiredMouseY());
      } catch (InvocationTargetException var3) {
         b crashreport = b.a(var3, "GuiContainer.getSlotAtPosition() threw an exception when called from MouseTweaks.");
         throw new f(crashreport);
      }
   }

   public boolean disableRMBDraggingFunctionality() {
      Reflection.guiContainerClass.setFieldValue(this.guiContainer, Constants.IGNOREMOUSEUP_NAME.forgeName, Boolean.valueOf(true));
      if (((Boolean)Reflection.guiContainerClass.getFieldValue(this.guiContainer, Constants.DRAGSPLITTING_NAME.forgeName)).booleanValue() && ((Integer)Reflection.guiContainerClass.getFieldValue(this.guiContainer, Constants.DRAGSPLITTINGBUTTON_NAME.forgeName)).intValue() == 1) {
         Reflection.guiContainerClass.setFieldValue(this.guiContainer, Constants.DRAGSPLITTING_NAME.forgeName, Boolean.valueOf(false));
         return true;
      } else {
         return false;
      }
   }

   public void clickSlot(agr slot, MouseButton mouseButton, boolean shiftPressed) {
      try {
         this.handleMouseClick.invoke(this.guiContainer, slot, slot.e, mouseButton.getValue(), shiftPressed ? afw.b : afw.a);
      } catch (InvocationTargetException var6) {
         b crashreport = b.a(var6, "handleMouseClick() threw an exception when called from MouseTweaks.");
         throw new f(crashreport);
      } catch (IllegalAccessException var7) {
         b crashreport = b.a(var7, "Calling handleMouseClick() from MouseTweaks.");
         throw new f(crashreport);
      }
   }

   public boolean isCraftingOutput(agr slot) {
      return slot instanceof ago || slot instanceof age || slot instanceof agl || this.guiContainer.h instanceof afs && slot.e == 2;
   }

   public boolean isIgnored(agr slot) {
      return false;
   }
}
