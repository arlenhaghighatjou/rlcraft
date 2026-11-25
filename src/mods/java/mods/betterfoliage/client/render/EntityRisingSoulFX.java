package mods.betterfoliage.client.render;

import java.util.Deque;
import java.util.LinkedList;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.config.Config;
import mods.octarinecore.client.render.AbstractEntityFX;
import mods.octarinecore.common.Double3;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0014H\u0016R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000b\u001a\u00020\f8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\rR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001a"},
   d2 = {"Lmods/betterfoliage/client/render/EntityRisingSoulFX;", "Lmods/octarinecore/client/render/AbstractEntityFX;", "world", "Lnet/minecraft/world/World;", "pos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", "initialPhase", "", "getInitialPhase", "()I", "isValid", "", "()Z", "particleTrail", "Ljava/util/Deque;", "Lmods/octarinecore/common/Double3;", "getParticleTrail", "()Ljava/util/Deque;", "render", "", "worldRenderer", "Lnet/minecraft/client/renderer/WorldRenderer;", "partialTickTime", "", "update", "BetterFoliage-MC1.12"}
)
public final class EntityRisingSoulFX extends AbstractEntityFX {
   @NotNull
   private final Deque<Double3> particleTrail;
   private final int initialPhase;

   @NotNull
   public final Deque<Double3> getParticleTrail() {
      return this.particleTrail;
   }

   public final int getInitialPhase() {
      return this.initialPhase;
   }

   public boolean isValid() {
      return true;
   }

   public void update() {
      int phase = (this.initialPhase + this.particleAge) % 64;
      this.getVelocity().setTo(AbstractEntityFX.Companion.getCos()[phase].doubleValue() * Config.risingSoul.INSTANCE.getPerturb(), 0.1D, AbstractEntityFX.Companion.getSin()[phase].doubleValue() * Config.risingSoul.INSTANCE.getPerturb());
      this.particleTrail.addFirst(Double3.copy$default(this.getCurrentPos(), 0.0D, 0.0D, 0.0D, 7, (Object)null));

      while(this.particleTrail.size() > Config.risingSoul.INSTANCE.getTrailLength()) {
         this.particleTrail.removeLast();
      }

      if (!Config.INSTANCE.getEnabled()) {
         this.setExpired();
      }

   }

   public void render(@NotNull WorldRenderer worldRenderer, float partialTickTime) {
      Intrinsics.checkParameterIsNotNull(worldRenderer, "worldRenderer");
      float alpha = Config.risingSoul.INSTANCE.getOpacity();
      if (this.particleAge > this.particleMaxAge - 40) {
         alpha *= (float)(this.particleMaxAge - this.particleAge) / 40.0F;
      }

      AbstractEntityFX.renderParticleQuad$default(this, worldRenderer, partialTickTime, (Double3)null, (Double3)null, Config.risingSoul.INSTANCE.getHeadSize() * 0.25D, 0, (EaglerTextureAtlasSprite)null, false, alpha, 236, (Object)null);
      double scale = Config.risingSoul.INSTANCE.getTrailSize() * 0.25D;
      Iterable $this$forEachPairIndexed$iv = (Iterable)this.particleTrail;
      int $i$f$forEachPairIndexed = 0;
      Object previous$iv = null;
      int $i$f$forEachIndexed = 0;
      int index$iv$iv = 0;

      for(Object item$iv$iv : $this$forEachPairIndexed$iv) {
         int var12 = index$iv$iv++;
         boolean var13 = false;
         if (var12 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         int $i$a$-forEachIndexed-Utils$forEachPairIndexed$1 = 0;
         if (previous$iv != null) {
            Double3 previous = (Double3)previous$iv;
            Double3 current = (Double3)item$iv$iv;
            int $i$a$-forEachPairIndexed-EntityRisingSoulFX$render$1 = 0;
            scale *= Config.risingSoul.INSTANCE.getSizeDecay();
            alpha *= Config.risingSoul.INSTANCE.getOpacityDecay();
            if (var12 % Config.risingSoul.INSTANCE.getTrailDensity() == 0) {
               Intrinsics.checkExpressionValueIsNotNull(current, "current");
               EaglerTextureAtlasSprite var10009 = RisingSoulTextures.INSTANCE.getTrackIcon().getIcon();
               if (var10009 == null) {
                  Intrinsics.throwNpe();
               }

               EaglerTextureAtlasSprite var22 = var10009;
               boolean var23 = false;
               AbstractEntityFX.renderParticleQuad$default(this, worldRenderer, partialTickTime, current, previous, scale, 0, var22, var23, alpha, 160, (Object)null);
            }
         }

         previous$iv = item$iv$iv;
      }

   }

   public EntityRisingSoulFX(@NotNull World world, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      super(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D);
      this.particleTrail = (Deque)(new LinkedList());
      this.initialPhase = this.rand.nextInt(64);
      this.motionY = 0.1D;
      this.particleGravity = 0.0F;
      this.particleTexture = RisingSoulTextures.INSTANCE.getHeadIcons().get(this.rand.nextInt(256));
      this.particleMaxAge = MathHelper.floor((0.6D + 0.4D * this.rand.nextDouble()) * Config.risingSoul.INSTANCE.getLifetime() * 20.0D);
   }
}
