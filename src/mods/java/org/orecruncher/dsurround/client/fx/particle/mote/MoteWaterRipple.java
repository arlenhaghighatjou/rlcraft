package org.orecruncher.dsurround.client.fx.particle.mote;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public class MoteWaterRipple extends MoteAgeable {
   private static final float TEX_SIZE_HALF = 0.5F;
   protected final float growthRate;
   protected float scale;
   protected float scaledWidth;
   protected float texU1;
   protected float texU2;
   protected float texV1;
   protected float texV2;

   public MoteWaterRipple(World world, double x, double y, double z) {
      super(world, x, y, z);
      RippleStyle style = RippleStyle.get();
      this.maxAge = style.getMaxAge();
      if (style.doScaling()) {
         this.growthRate = (float)this.maxAge / 500.0F;
         this.scale = this.growthRate;
         this.scaledWidth = this.scale * 0.5F;
      } else {
         this.growthRate = 0.0F;
         this.scale = 0.0F;
         this.scaledWidth = 0.5F;
      }

      this.posY -= 0.2D;
      Color waterColor = BiomeUtil.getColorForLiquid(world, this.position);
      this.red = (int)(waterColor.red * 255.0F);
      this.green = (int)(waterColor.green * 255.0F);
      this.blue = (int)(waterColor.blue * 255.0F);
      this.texU1 = style.getU1(this.age);
      this.texU2 = style.getU2(this.age);
      this.texV1 = style.getV1(this.age);
      this.texV2 = style.getV2(this.age);
   }

   public void update() {
      RippleStyle style = RippleStyle.get();
      if (style.doScaling()) {
         this.scale += this.growthRate;
         this.scaledWidth = this.scale * 0.5F;
      }

      if (style.doAlpha()) {
         this.alpha = (int)((float)(this.maxAge - this.age) / (float)(this.maxAge + 3) * 255.0F);
      }

      this.texU1 = style.getU1(this.age);
      this.texU2 = style.getU2(this.age);
      this.texV1 = style.getV1(this.age);
      this.texV2 = style.getV2(this.age);
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float x = this.renderX(partialTicks);
      float y = this.renderY(partialTicks);
      float z = this.renderZ(partialTicks);
      this.drawVertex(buffer, (double)(-this.scaledWidth + x), (double)y, (double)(this.scaledWidth + z), (double)this.texU2, (double)this.texV2);
      this.drawVertex(buffer, (double)(this.scaledWidth + x), (double)y, (double)(this.scaledWidth + z), (double)this.texU2, (double)this.texV1);
      this.drawVertex(buffer, (double)(this.scaledWidth + x), (double)y, (double)(-this.scaledWidth + z), (double)this.texU1, (double)this.texV1);
      this.drawVertex(buffer, (double)(-this.scaledWidth + x), (double)y, (double)(-this.scaledWidth + z), (double)this.texU1, (double)this.texV2);
   }
}
