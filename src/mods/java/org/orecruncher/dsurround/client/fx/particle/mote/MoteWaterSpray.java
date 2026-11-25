package org.orecruncher.dsurround.client.fx.particle.mote;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public class MoteWaterSpray extends MoteMotionBase {
   protected final float scale;
   protected final float texU1;
   protected final float texU2;
   protected final float texV1;
   protected final float texV2;
   protected final float f4;

   public MoteWaterSpray(World world, double x, double y, double z, double dX, double dY, double dZ) {
      super(world, x, y, z, dX, dY, dZ);
      this.maxAge = (int)(8.0F / (RANDOM.nextFloat() * 0.8F + 0.2F));
      this.scale = (RANDOM.nextFloat() * 0.5F + 0.5F) * 2.0F;
      int textureIdx = RANDOM.nextInt(4);
      int texX = textureIdx % 2;
      int texY = textureIdx / 2;
      this.texU1 = (float)texX * 0.5F;
      this.texU2 = this.texU1 + 0.5F;
      this.texV1 = (float)texY * 0.5F;
      this.texV2 = this.texV1 + 0.5F;
      this.f4 = 0.07F * this.scale;
   }

   public void configureColor() {
      Color waterColor = BiomeUtil.getColorForLiquid(this.world, this.position);
      this.red = (int)(waterColor.red * 255.0F);
      this.green = (int)(waterColor.green * 255.0F);
      this.blue = (int)(waterColor.blue * 255.0F);
      this.alpha = 255;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float x = this.renderX(partialTicks);
      float y = this.renderY(partialTicks);
      float z = this.renderZ(partialTicks);
      this.drawVertex(buffer, (double)(x + (-rotationX * this.f4 - rotationXY * this.f4)), (double)(y + -rotationZ * this.f4), (double)(z + (-rotationYZ * this.f4 - rotationXZ * this.f4)), (double)this.texU2, (double)this.texV2);
      this.drawVertex(buffer, (double)(x + -rotationX * this.f4 + rotationXY * this.f4), (double)(y + rotationZ * this.f4), (double)(z + -rotationYZ * this.f4 + rotationXZ * this.f4), (double)this.texU2, (double)this.texV1);
      this.drawVertex(buffer, (double)(x + rotationX * this.f4 + rotationXY * this.f4), (double)(y + rotationZ * this.f4), (double)(z + rotationYZ * this.f4 + rotationXZ * this.f4), (double)this.texU1, (double)this.texV1);
      this.drawVertex(buffer, (double)(x + (rotationX * this.f4 - rotationXY * this.f4)), (double)(y + -rotationZ * this.f4), (double)(z + (rotationYZ * this.f4 - rotationXZ * this.f4)), (double)this.texU1, (double)this.texV2);
   }
}
