package org.orecruncher.dsurround.client.aurora;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class AuroraBand {
   protected static final float ANGLE1 = 0.19634955F;
   protected static final float ANGLE2 = MathStuff.toRadians(12.857142F);
   protected static final float AURORA_SPEED = 0.75F;
   public static final float AURORA_AMPLITUDE = 18.0F;
   protected final Random random;
   protected Panel[] nodes;
   protected float cycle = 0.0F;
   protected int alphaLimit = 128;
   protected int length;
   protected float nodeLength;
   protected float nodeWidth;

   public AuroraBand(Random random, AuroraFactory.AuroraGeometry geo, boolean noTaper, boolean fixedHeight) {
      this.random = random;
      this.preset(geo);
      this.generateBands(noTaper, fixedHeight);
      this.translate(0.0F);
   }

   protected AuroraBand(Panel[] nodes, AuroraBand band) {
      this.random = band.random;
      this.nodes = nodes;
      this.cycle = band.cycle;
      this.length = band.length;
      this.nodeLength = band.nodeLength;
      this.nodeWidth = band.nodeWidth;
      this.alphaLimit = band.alphaLimit;
      this.translate(0.0F);
   }

   public int getAlphaLimit() {
      return this.alphaLimit;
   }

   @Nonnull
   public Panel[] getNodeList() {
      return this.nodes;
   }

   public float getNodeWidth() {
      return this.nodeWidth;
   }

   public float getCycle() {
      return this.cycle;
   }

   public void update() {
      if ((this.cycle += 0.75F) >= 360.0F) {
         this.cycle -= 360.0F;
      }

   }

   public AuroraBand copy(int offset) {
      Panel[] newNodes = new Panel[this.nodes.length];

      for(int i = 0; i < this.nodes.length; ++i) {
         newNodes[i] = new Panel(this.nodes[i], offset);
      }

      return new AuroraBand(newNodes, this);
   }

   public void translate(float partialTick) {
      float c = this.cycle + 0.75F * partialTick;

      for(int i = 0; i < this.nodes.length; ++i) {
         float f = MathStuff.cos(MathStuff.toRadians((float)(i << 3) + c));
         Panel node = this.nodes[i];
         node.dZ = f * 18.0F;
         node.dY = f * 3.0F;
         float mZ = node.getModdedZ();
         node.tetZ = mZ + node.sinDeg90;
         node.tetZ2 = mZ + node.sinDeg270;
      }

   }

   protected void preset(AuroraFactory.AuroraGeometry geo) {
      this.length = geo.length;
      this.nodeLength = geo.nodeLength;
      this.nodeWidth = geo.nodeWidth;
      this.alphaLimit = geo.alphaLimit;
   }

   protected void generateBands(boolean noTaper, boolean fixedHeight) {
      this.nodes = this.populate(fixedHeight);
      float factor = 3.1415927F / ((float)this.length / 4.0F);
      int lowerBound = this.length / 8 + 1;
      int upperBound = this.length * 7 / 8 - 1;
      int count = 0;

      for(int i = 0; i < this.length; ++i) {
         float width;
         if (noTaper) {
            width = this.nodeWidth;
         } else if (i < lowerBound) {
            width = MathStuff.sin(factor * (float)(count++)) * this.nodeWidth;
         } else if (i > upperBound) {
            width = MathStuff.sin(factor * (float)(count--)) * this.nodeWidth;
         } else {
            width = this.nodeWidth;
         }

         this.nodes[i].setWidth(width);
      }

   }

   @Nonnull
   protected Panel[] populate(boolean fixedHeight) {
      Panel[] nodeList = new Panel[this.length];
      int bound = this.length / 2 - 1;
      float angleTotal = 0.0F;

      for(int i = this.length / 8 / 2 - 1; i >= 0; --i) {
         float angle = (this.random.nextFloat() - 0.5F) * 8.0F;
         angleTotal += angle;
         if (MathStuff.abs(angleTotal) > 180.0F) {
            angle = -angle;
            angleTotal += angle;
         }

         for(int k = 7; k >= 0; --k) {
            int idx = i * 8 + k;
            if (idx == bound) {
               float amplitude = fixedHeight ? 18.0F : 7.0F + this.random.nextFloat();
               nodeList[idx] = new Panel(0.0F, amplitude, 0.0F, angle);
            } else {
               float y;
               if (fixedHeight) {
                  y = 18.0F;
               } else if (i == 0) {
                  y = MathStuff.sin(0.19634955F * (float)k) * 7.0F + this.random.nextFloat() / 2.0F;
               } else {
                  y = 10.0F + this.random.nextFloat() * 5.0F;
               }

               Panel node = nodeList[idx + 1];
               float subAngle = node.angle + angle;
               float subAngleRads = MathStuff.toRadians(subAngle);
               float z = node.posZ - MathStuff.sin(subAngleRads) * this.nodeLength;
               float x = node.posX - MathStuff.cos(subAngleRads) * this.nodeLength;
               nodeList[idx] = new Panel(x, y, z, subAngle);
            }
         }
      }

      angleTotal = 0.0F;

      for(int j = this.length / 8 / 2; j < this.length / 8; ++j) {
         float angle = (this.random.nextFloat() - 0.5F) * 8.0F;
         angleTotal += angle;
         if (MathStuff.abs(angleTotal) > 180.0F) {
            angle = -angle;
            angleTotal += angle;
         }

         for(int h = 0; h < 8; ++h) {
            float y;
            if (fixedHeight) {
               y = 18.0F;
            } else if (j == this.length / 8 - 1) {
               y = MathStuff.cos(ANGLE2 * (float)h) * 7.0F + this.random.nextFloat() / 2.0F;
            } else {
               y = 10.0F + this.random.nextFloat() * 5.0F;
            }

            Panel node = nodeList[j * 8 + h - 1];
            float subAngle = node.angle + angle;
            float subAngleRads = MathStuff.toRadians(subAngle);
            float z = node.posZ + MathStuff.sin(subAngleRads) * this.nodeLength;
            float x = node.posX + MathStuff.cos(subAngleRads) * this.nodeLength;
            nodeList[j * 8 + h] = new Panel(x, y, z, subAngle);
         }
      }

      return nodeList;
   }
}
