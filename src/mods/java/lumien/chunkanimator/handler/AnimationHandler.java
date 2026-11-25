package lumien.chunkanimator.handler;

import lumien.chunkanimator.ChunkAnimator;
import lumien.chunkanimator.config.ChunkAnimatorConfig;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import penner.easing.Back;
import penner.easing.Bounce;
import penner.easing.Circ;
import penner.easing.Cubic;
import penner.easing.Elastic;
import penner.easing.Expo;
import penner.easing.Linear;
import penner.easing.Quad;
import penner.easing.Quart;
import penner.easing.Quint;
import penner.easing.Sine;

public class AnimationHandler {
   private int cooldown;

   public void startCooldown() {
      this.cooldown = 10;
   }

   public void tick() {
      if (this.cooldown > 0) {
         --this.cooldown;
      }
   }

   public void setOrigin(RenderChunk chunk) {
      chunk.animationStart = RenderChunk.NO_ANIMATION;
      chunk.animationFacing = null;
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (player == null) {
         return;
      }

      ChunkAnimatorConfig config = ChunkAnimator.INSTANCE.config;
      BlockPos pos = chunk.getPosition();
      int dx = MathHelper.floor(player.posX) - pos.getX() - 8;
      int dz = MathHelper.floor(player.posZ) - pos.getZ() - 8;
      if ((config.disableAroundPlayer() || this.cooldown > 0)
          && (double)dx * dx + (double)dz * dz <= 4096.0D) {
         return;
      }

      chunk.animationStart = RenderChunk.ANIMATION_PENDING;
      if (config.getMode() == 3) {
         chunk.animationFacing = getFacing(dx, dz);
      }
   }

   public void preRender(RenderChunk chunk) {
      if (chunk.animationStart == RenderChunk.NO_ANIMATION) {
         return;
      }

      long now = EagRuntime.steadyTimeMillis();
      ChunkAnimatorConfig config = ChunkAnimator.INSTANCE.config;
      int duration = config.getAnimationDuration();
      int mode = config.getMode();
      BlockPos pos = chunk.getPosition();
      if (chunk.animationStart == RenderChunk.ANIMATION_PENDING) {
         chunk.animationStart = now;
         EntityPlayerSP player = Minecraft.getMinecraft().player;
         if (mode == 4 && player != null) {
            chunk.animationFacing = getFacing(MathHelper.floor(player.posX) - pos.getX() - 8,
               MathHelper.floor(player.posZ) - pos.getZ() - 8);
         }
      }

      long elapsed = now - chunk.animationStart;
      if (elapsed >= duration) {
         chunk.animationStart = RenderChunk.NO_ANIMATION;
         return;
      }

      if (mode == 2) {
         mode = pos.getY() < Minecraft.getMinecraft().world.provider.getHorizon() ? 0 : 1;
      }
      float remaining = 1.0F - this.getFunctionValue(elapsed, 0.0F, 1.0F, duration);
      switch (mode) {
      case 0:
         GlStateManager.translate(0.0F, -pos.getY() * remaining, 0.0F);
         break;
      case 1:
         GlStateManager.translate(0.0F, (256 - pos.getY()) * remaining, 0.0F);
         break;
      case 3:
      case 4:
         if (chunk.animationFacing != null) {
            float offset = -200.0F * remaining;
            GlStateManager.translate(chunk.animationFacing.getFrontOffsetX() * offset, 0.0F,
               chunk.animationFacing.getFrontOffsetZ() * offset);
         }
         break;
      default:
         break;
      }
   }

   private static EnumFacing getFacing(int dx, int dz) {
      if (Math.abs(dx) > Math.abs(dz)) {
         return dx > 0 ? EnumFacing.EAST : EnumFacing.WEST;
      }
      return dz > 0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
   }

   private float getFunctionValue(float t, float b, float c, float d) {
      switch (ChunkAnimator.INSTANCE.config.getEasingFunction()) {
      case 0:
         return Linear.easeOut(t, b, c, d);
      case 1:
         return Quad.easeOut(t, b, c, d);
      case 2:
         return Cubic.easeOut(t, b, c, d);
      case 3:
         return Quart.easeOut(t, b, c, d);
      case 4:
         return Quint.easeOut(t, b, c, d);
      case 5:
         return Expo.easeOut(t, b, c, d);
      case 7:
         return Circ.easeOut(t, b, c, d);
      case 8:
         return Back.easeOut(t, b, c, d);
      case 9:
         return Bounce.easeOut(t, b, c, d);
      case 10:
         return Elastic.easeOut(t, b, c, d);
      case 6:
      default:
         return Sine.easeOut(t, b, c, d);
      }
   }
}
