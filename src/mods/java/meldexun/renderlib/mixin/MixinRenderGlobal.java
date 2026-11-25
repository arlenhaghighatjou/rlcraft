package meldexun.renderlib.mixin;

import meldexun.renderlib.renderer.EntityRenderManager;
import meldexun.renderlib.renderer.TileEntityRenderManager;
import meldexun.renderlib.util.RenderUtil;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderGlobal.class})
public class MixinRenderGlobal {
   public MixinRenderGlobal() {
   }

   @Inject(
      method = {"setupTerrain"},
      at = {@At("HEAD")}
   )
   public void setupCamera(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator, CallbackInfo info) {
      RenderUtil.updateCamera();
   }

   @Inject(
      method = {"renderEntities"},
      at = {@At("HEAD")}
   )
   public void preEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo info) {
      if (MinecraftForgeClient.getRenderPass() == 0) {
         EntityRenderManager.setup(camera, partialTicks);
         TileEntityRenderManager.setup(camera, partialTicks);
      }

   }

   @Inject(
      method = {"renderEntities"},
      at = {@At("RETURN")}
   )
   public void postEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo info) {
      if (MinecraftForgeClient.getRenderPass() == 1) {
         EntityRenderManager.reset();
         TileEntityRenderManager.reset();
      }

   }
}
