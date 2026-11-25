package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.registry.footstep.FootprintStyle;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class MoteFootprint extends MoteAgeable {
   private static final float TEXEL_WIDTH = 0.125F;
   private static final float TEXEL_PRINT_WIDTH = 0.0625F;
   private static final float WIDTH = 0.125F;
   private static final float LENGTH = 0.25F;
   private static final Vec2f FIRST_POINT = new Vec2f(-0.125F, 0.25F);
   private static final Vec2f SECOND_POINT = new Vec2f(0.125F, 0.25F);
   private static final Vec2f THIRD_POINT = new Vec2f(0.125F, -0.25F);
   private static final Vec2f FOURTH_POINT = new Vec2f(-0.125F, -0.25F);
   private static float zFighter = 0.0F;
   protected final boolean isSnowLayer;
   protected final BlockPos downPos;
   protected final float texU1;
   protected final float texU2;
   protected final float texV1;
   protected final float texV2;
   protected final float scale;
   protected final Vec2f firstPoint;
   protected final Vec2f secondPoint;
   protected final Vec2f thirdPoint;
   protected final Vec2f fourthPoint;

   public MoteFootprint(@Nonnull FootprintStyle style, @Nonnull World world, double x, double y, double z, float rotation, float scale, boolean isRight) {
      super(world, x, y, z);
      this.maxAge = 200;
      if (++zFighter > 20.0F) {
         zFighter = 1.0F;
      }

      IBlockState state = WorldUtils.getBlockState(this.world, this.position);
      this.isSnowLayer = state.getBlock() == Blocks.SNOW_LAYER;
      this.posY += (double)(zFighter * 0.001F);
      float fraction = (float)(y - (double)((int)y));
      if (!this.isSnowLayer && fraction > 0.0625F) {
         this.downPos = this.position.toImmutable();
      } else {
         this.downPos = (new BlockPos(this.posX, this.posY, this.posZ)).down();
      }

      float u1 = (float)style.ordinal() * 0.125F + 0.00390625F;
      if (isRight) {
         u1 += 0.0625F;
      }

      this.texU1 = u1;
      this.texU2 = u1 + 0.0625F;
      this.texV1 = 0.0F;
      this.texV2 = 1.0F;
      this.scale = scale;
      float theRotation = MathStuff.toRadians(-rotation + 180.0F);
      this.firstPoint = MathStuff.rotateScale(FIRST_POINT, theRotation, this.scale);
      this.secondPoint = MathStuff.rotateScale(SECOND_POINT, theRotation, this.scale);
      this.thirdPoint = MathStuff.rotateScale(THIRD_POINT, theRotation, this.scale);
      this.fourthPoint = MathStuff.rotateScale(FOURTH_POINT, theRotation, this.scale);
   }

   protected boolean advanceAge() {
      if (Weather.isRaining()) {
         this.age = (int)((float)this.age + Weather.getIntensityLevel() * 100.0F / 25.0F);
      }

      return super.advanceAge();
   }

   protected void update() {
      if (!ClientChunkCache.instance().getBlockState(this.downPos).getMaterial().isSolid()) {
         this.kill();
      } else if (this.isSnowLayer && ClientChunkCache.instance().getBlockState(this.position).getBlock() != Blocks.SNOW_LAYER) {
         this.kill();
      }

   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
      float f = ((float)this.age + partialTicks) / ((float)this.maxAge + 1.0F);
      f = f * f;
      this.alpha = (int)(MathStuff.clamp(1.0F - f, 0.0F, 1.0F) * 255.0F);
      this.alpha = (int)((float)this.alpha * 0.4F);
      double x = (double)this.renderX(partialTicks);
      double y = (double)this.renderY(partialTicks);
      double z = (double)this.renderZ(partialTicks);
      this.drawVertex(buffer, x + (double)this.firstPoint.x, y, z + (double)this.firstPoint.y, (double)this.texU1, (double)this.texV2);
      this.drawVertex(buffer, x + (double)this.secondPoint.x, y, z + (double)this.secondPoint.y, (double)this.texU2, (double)this.texV2);
      this.drawVertex(buffer, x + (double)this.thirdPoint.x, y, z + (double)this.thirdPoint.y, (double)this.texU2, (double)this.texV1);
      this.drawVertex(buffer, x + (double)this.fourthPoint.x, y, z + (double)this.fourthPoint.y, (double)this.texU1, (double)this.texV1);
   }
}
