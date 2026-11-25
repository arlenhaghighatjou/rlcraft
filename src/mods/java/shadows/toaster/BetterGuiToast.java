package shadows.toaster;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.IToast.Visibility;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class BetterGuiToast extends GuiToast {
   public BetterGuiToast() {
      super(Minecraft.getMinecraft(), ToastControl.ToastControlConfig.toastCount, new ControlledDeque());
   }

   @Override
   protected ToastInstance<?> createInstance(IToast toast) {
      return new BetterToastInstance<>(toast);
   }

   @Override
   public ResourceLocation getToastTexture() {
      if (ToastControl.ToastControlConfig.transparent) {
         return ToastControl.TRANSPARENT;
      }
      return ToastControl.ToastControlConfig.translucent ? ToastControl.TRANSLUCENT : super.getToastTexture();
   }

   void updateToastCount() {
      this.visible = Arrays.copyOf(this.visible, ToastControl.ToastControlConfig.toastCount);
   }

   void tick() {
      for (ToastInstance<?> toast : this.visible) {
         if (toast != null) {
            ((BetterToastInstance<?>)toast).tick();
         }
      }
   }

   public class BetterToastInstance<T extends IToast> extends ToastInstance<T> {
      protected int forcedShowTime = 0;

      protected BetterToastInstance(T toast) {
         super(toast);
      }

      public void tick() {
         if (this.forcedShowTime <= ToastControl.ToastControlConfig.forceTime && this.forcedShowTime < Integer.MAX_VALUE) {
            ++this.forcedShowTime;
         }
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
            this.visibility.playSound(BetterGuiToast.this.getMinecraft().getSoundHandler());
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
            this.visibility.playSound(BetterGuiToast.this.getMinecraft().getSoundHandler());
         }

         return this.forcedShowTime > ToastControl.ToastControlConfig.forceTime && this.visibility == Visibility.HIDE && i - this.animationTime > 600L;
      }
   }
}
