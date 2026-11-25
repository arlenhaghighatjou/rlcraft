package shadows.toaster;

import net.minecraft.client.gui.toasts.AdvancementToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.TutorialToast;
import shadows.placebo.util.collections.BlockedDeque;

public class ControlledDeque extends BlockedDeque<IToast> {
   private static final long serialVersionUID = -5380678178676126928L;

   public ControlledDeque() {
   }

   public boolean isBlocked(IToast toast) {
      if (ToastControl.ToastControlConfig.printClasses) {
         ToastControl.LOGGER.info(toast.getClass());
      }

      if (ToastControl.ToastControlConfig.global) {
         return true;
      } else if (ToastControl.ToastControlConfig.globalVanilla && this.isVanillaToast(toast)) {
         return true;
      } else if (ToastControl.ToastControlConfig.globalModded && !this.isVanillaToast(toast)) {
         return true;
      } else if (ToastControl.BLOCKED_CLASSES.contains(toast.getClass())) {
         return true;
      } else {
         return toast instanceof AdvancementToast && ToastControl.ToastControlConfig.advancements || toast instanceof RecipeToast && ToastControl.ToastControlConfig.recipes || toast instanceof SystemToast && ToastControl.ToastControlConfig.system || toast instanceof TutorialToast && ToastControl.ToastControlConfig.tutorial;
      }
   }

   private boolean isVanillaToast(IToast toast) {
      return toast instanceof AdvancementToast || toast instanceof RecipeToast || toast instanceof SystemToast || toast instanceof TutorialToast;
   }
}
