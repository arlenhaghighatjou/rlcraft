package meldexun.renderlib.mixin;

import meldexun.renderlib.config.RenderLibConfig;
import meldexun.renderlib.util.GLUtil;
import meldexun.renderlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Shadow
   private boolean isGamePaused;
   @Shadow
   private float renderPartialTicksPaused;
   @Shadow
   private Timer timer;
   @Shadow
   public WorldClient world;
   @Shadow
   private GameSettings gameSettings;

   public MixinMinecraft() {
   }

   @ModifyArg(
      method = {"runGameLoop"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onRenderTickStart(F)V",
   remap = false
)
   )
   public float onRenderTickStart(float partialTick) {
      return this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks;
   }

   @Inject(
      method = {"runGameLoop"},
      at = {@At(
   value = "INVOKE_STRING",
   target = "Lnet/minecraft/profiler/Profiler;endStartSection(Ljava/lang/String;)V",
   args = {"ldc=gameRenderer"}
)}
   )
   public void runGameLoop(CallbackInfo info) {
      RenderUtil.update(this.isGamePaused ? (double)this.renderPartialTicksPaused : (double)this.timer.renderPartialTicks);
   }

   @ModifyArg(
      method = {"runGameLoop"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onRenderTickEnd(F)V",
   remap = false
)
   )
   public float onRenderTickEnd(float partialTick) {
      return this.isGamePaused ? this.renderPartialTicksPaused : this.timer.renderPartialTicks;
   }

   @Inject(
      method = {"getLimitFramerate"},
      cancellable = true,
      at = {@At("HEAD")}
   )
   public void getLimitFramerate(CallbackInfoReturnable<Integer> info) {
      if (this.world == null) {
         if (RenderLibConfig.mainMenuFPSSynced) {
            info.setReturnValue(Integer.valueOf(MathHelper.clamp(this.gameSettings.limitFramerate, 30, 240)));
         } else {
            info.setReturnValue(Integer.valueOf(RenderLibConfig.mainMenuFPS));
         }
      } else {
         info.setReturnValue(Integer.valueOf(this.gameSettings.limitFramerate));
      }

   }

   @Inject(
      method = {"isFramerateLimitBelowMax"},
      cancellable = true,
      at = {@At("HEAD")}
   )
   public void isFramerateLimitBelowMax(CallbackInfoReturnable<Boolean> info) {
      if (this.world == null) {
         info.setReturnValue(Boolean.valueOf(true));
      }

   }

   @Redirect(
      method = {"launchIntegratedServer"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/lang/Thread;sleep(J)V"
)
   )
   public void launchIntegratedServer_sleep(long millis) {
      Display.sync(20);
   }

   @Inject(
      method = {"init"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/client/FMLClientHandler;finishMinecraftLoading()V",
   remap = false,
   shift = Shift.AFTER
)}
   )
   public void init(CallbackInfo info) {
      GLUtil.updateDebugOutput();
   }
}
