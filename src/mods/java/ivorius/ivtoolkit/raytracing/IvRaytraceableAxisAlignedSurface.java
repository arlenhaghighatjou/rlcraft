package ivorius.ivtoolkit.raytracing;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class IvRaytraceableAxisAlignedSurface extends IvRaytraceableObject {
   private double x;
   private double y;
   private double z;
   private double width;
   private double height;
   private double depth;
   private Object hitInfo;

   public IvRaytraceableAxisAlignedSurface(Object userInfo, Object hitInfo, double y, double z, double width, double height, double depth, double x) {
      super(userInfo);
      this.x = x;
      this.y = y;
      this.z = z;
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.hitInfo = hitInfo;
   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public double getWidth() {
      return this.width;
   }

   public void setWidth(double width) {
      this.width = width;
   }

   public double getHeight() {
      return this.height;
   }

   public void setHeight(double height) {
      this.height = height;
   }

   public double getDepth() {
      return this.depth;
   }

   public void setDepth(double depth) {
      this.depth = depth;
   }

   public Object getHitInfo() {
      return this.hitInfo;
   }

   public void setHitInfo(Object hitInfo) {
      this.hitInfo = hitInfo;
   }

   public void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir) {
      this.checkValid();
      double t = 0.0D;
      boolean cantHit = false;
      boolean onPlane = false;
      if (this.width == 0.0D) {
         double xDist = this.x - x;
         if (xDist == 0.0D && xDir == 0.0D) {
            onPlane = true;
         } else if (xDist != 0.0D && xDir == 0.0D) {
            cantHit = true;
         } else {
            t = xDist / xDir;
         }
      } else if (this.height == 0.0D) {
         double yDist = this.y - y;
         if (yDist == 0.0D && yDir == 0.0D) {
            onPlane = true;
         } else if (yDist != 0.0D && yDir == 0.0D) {
            cantHit = true;
         } else {
            t = yDist / yDir;
         }
      } else if (this.depth == 0.0D) {
         double zDist = this.z - z;
         if (zDist == 0.0D && zDir == 0.0D) {
            onPlane = true;
         } else if (zDist != 0.0D && zDir == 0.0D) {
            cantHit = true;
         } else {
            t = zDist / zDir;
         }
      }

      if (!onPlane) {
         if (!cantHit) {
            double[] hitPoint = new double[]{x + t * xDir, y + t * yDir, z + t * zDir};
            if (this.withinBounds(hitPoint[0], this.x, this.x + this.width) && this.withinBounds(hitPoint[1], this.y, this.y + this.height) && this.withinBounds(hitPoint[2], this.z, this.z + this.depth)) {
               list.add(new IvRaytracedIntersection(this, this.hitInfo, hitPoint));
            }
         }

      }
   }

   private boolean withinBounds(double value, double min, double max) {
      return value >= min - 9.999999747378752E-5D && value <= max + 9.999999747378752E-5D;
   }

   private void checkValid() {
      int lengths0 = 0;
      if (this.width == 0.0D) {
         ++lengths0;
      }

      if (this.height == 0.0D) {
         ++lengths0;
      }

      if (this.depth == 0.0D) {
         ++lengths0;
      }

      if (lengths0 != 1) {
         throw new ArithmeticException("Axis aligned surface must have exactly one length that is zero. (But has: " + lengths0 + ")");
      }
   }

   public void drawOutlines() {
      WorldRenderer renderer = Tessellator.getInstance().getBuffer();
      renderer.begin(3, DefaultVertexFormats.POSITION);
      if (this.width == 0.0D) {
         renderer.pos(this.x, this.y, this.z).endVertex();
         renderer.pos(this.x, this.y + this.height, this.z).endVertex();
         renderer.pos(this.x, this.y + this.height, this.z + this.depth).endVertex();
         renderer.pos(this.x, this.y, this.z + this.depth).endVertex();
         renderer.pos(this.x, this.y, this.z).endVertex();
      }

      if (this.height == 0.0D) {
         renderer.pos(this.x, this.y, this.z).endVertex();
         renderer.pos(this.x + this.width, this.y, this.z).endVertex();
         renderer.pos(this.x + this.width, this.y, this.z + this.depth).endVertex();
         renderer.pos(this.x, this.y, this.z + this.depth).endVertex();
         renderer.pos(this.x, this.y, this.z).endVertex();
      }

      if (this.depth == 0.0D) {
         renderer.pos(this.x, this.y, this.z).endVertex();
         renderer.pos(this.x + this.width, this.y, this.z).endVertex();
         renderer.pos(this.x + this.width, this.y + this.height, this.z).endVertex();
         renderer.pos(this.x, this.y + this.height, this.z).endVertex();
         renderer.pos(this.x, this.y, this.z).endVertex();
      }

      Tessellator.getInstance().draw();
   }
}
