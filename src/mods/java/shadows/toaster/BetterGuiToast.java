package shadows.toaster;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.GuiToast.ToastInstance;
import net.minecraft.client.gui.toasts.IToast.Visibility;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.MathHelper;

public class BetterGuiToast extends GuiToast {
   public BetterGuiToast() {
      super(Minecraft.getMinecraft());
      this.toastsQueue = new ControlledDeque();
      this.visible = new BetterGuiToast.BetterToastInstance[ToastControl.ToastControlConfig.toastCount];
   }

   public void drawToast(ScaledResolution resolution) {
      if (!this.mc.gameSettings.hideGUI) {
         RenderHelper.disableStandardItemLighting();

         for(int i = 0; i < this.visible.length; ++i) {
            ToastInstance<?> toastinstance = this.visible[i];
            if (toastinstance != null && toastinstance.render(resolution.getScaledWidth(), i)) {
               this.visible[i] = null;
            }

            if (this.visible[i] == null && !this.toastsQueue.isEmpty()) {
               this.visible[i] = new BetterGuiToast.BetterToastInstance((IToast)this.toastsQueue.removeFirst());
            }
         }
      }

   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.toastsQueue.clear();
   }

   public class BetterToastInstance<T extends IToast> extends ToastInstance<T> {
      protected int forcedShowTime = 0;

      protected BetterToastInstance(T toast) {
         super(BetterGuiToast.this, toast);
         ToastControl.tracker.add(this);
      }

      public void tick() {
         ++this.forcedShowTime;
      }

      protected float getVisibility(long sysTime) {
         float f = MathHelper.clamp((float)(sysTime - this.animationTime) / 600.0F, 0.0F, 1.0F);
         f = f * f;
         if (ToastControl.ToastControlConfig.noSlide) {
            return 1.0F;
         } else {
            return this.forcedShowTime > ToastControl.ToastControlConfig.forceTime && this.visibility == Visibility.HIDE ? 1.0F - f : f;
         }
      }

      public boolean render(int scaledWidth, int arrayPos) {
         long i = Minecraft.getSystemTime();
         if (this.animationTime == -1L) {
            this.animationTime = i;
            this.visibility.playSound(BetterGuiToast.this.mc.getSoundHandler());
         }

         if (this.visibility == Visibility.SHOW && i - this.animationTime <= 600L) {
            this.visibleTime = i;
         }

         GlStateManager.pushMatrix();
         if (ToastControl.ToastControlConfig.startLeft) {
            GlStateManager.translate(-160.0F + 160.0F * this.getVisibility(i), (float)(arrayPos * 32), (float)(500 + arrayPos));
         } else {
            GlStateManager.translate((float)scaledWidth - 160.0F * this.getVisibility(i), (float)(arrayPos * 32), (float)(500 + arrayPos));
         }

         GlStateManager.enableBlend();
         GlStateManager.translate((float)ToastControl.ToastControlConfig.offsetX, (float)ToastControl.ToastControlConfig.offsetY, 0.0F);
         Visibility itoast$visibility = this.toast.draw(BetterGuiToast.this, i - this.visibleTime);
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         if (this.forcedShowTime > ToastControl.ToastControlConfig.forceTime && itoast$visibility != this.visibility) {
            this.animationTime = i - (long)((1.0F - this.getVisibility(i)) * 600.0F);
            this.visibility = itoast$visibility;
            this.visibility.playSound(BetterGuiToast.this.mc.getSoundHandler());
         }

         if (this.forcedShowTime > ToastControl.ToastControlConfig.forceTime) {
            ToastControl.tracker.remove(this);
         }

         return this.forcedShowTime > ToastControl.ToastControlConfig.forceTime && this.visibility == Visibility.HIDE && i - this.animationTime > 600L;
      }
   }
}
