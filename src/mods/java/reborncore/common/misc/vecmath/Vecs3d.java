package reborncore.common.misc.vecmath;

import java.util.StringTokenizer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Vecs3d {
   protected double x;
   protected double y;
   protected double z;
   protected World w;

   public Vecs3d(double x, double y, double z) {
      this.w = null;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vecs3d(double x, double y, double z, World w) {
      this(x, y, z);
      this.w = w;
   }

   public Vecs3d(TileEntity te) {
      this((double)te.getPos().getX(), (double)te.getPos().getY(), (double)te.getPos().getZ(), te.getWorld());
   }

   public Vecs3d(Vec3i vec) {
      this((double)vec.getX(), (double)vec.getY(), (double)vec.getZ());
   }

   public Vecs3d(Vec3i vec, World w) {
      this((double)vec.getX(), (double)vec.getY(), (double)vec.getZ());
      this.w = w;
   }

   public boolean hasWorld() {
      return this.w != null;
   }

   public Vecs3d add(double x, double y, double z) {
      this.x += x;
      this.y += y;
      this.z += z;
      return this;
   }

   public Vecs3d add(EnumFacing dir) {
      return this.add((double)dir.getFrontOffsetX(), (double)dir.getFrontOffsetY(), (double)dir.getFrontOffsetZ());
   }

   public Vecs3d add(Vecs3d vec) {
      return this.add(vec.x, vec.y, vec.z);
   }

   public Vecs3d sub(double x, double y, double z) {
      this.x -= x;
      this.y -= y;
      this.z -= z;
      return this;
   }

   public Vecs3d sub(EnumFacing dir) {
      return this.sub((double)dir.getFrontOffsetX(), (double)dir.getFrontOffsetY(), (double)dir.getFrontOffsetZ());
   }

   public Vecs3d sub(Vecs3d vec) {
      return this.sub(vec.x, vec.y, vec.z);
   }

   public Vecs3d mul(double x, double y, double z) {
      this.x *= x;
      this.y *= y;
      this.z *= z;
      return this;
   }

   public Vecs3d mul(double multiplier) {
      return this.mul(multiplier, multiplier, multiplier);
   }

   public Vecs3d mul(EnumFacing direction) {
      return this.mul((double)direction.getFrontOffsetX(), (double)direction.getFrontOffsetY(), (double)direction.getFrontOffsetZ());
   }

   public Vecs3d multiply(Vecs3d v) {
      return this.mul(v.getX(), v.getY(), v.getZ());
   }

   public Vecs3d div(double x, double y, double z) {
      this.x /= x;
      this.y /= y;
      this.z /= z;
      return this;
   }

   public Vecs3d div(double multiplier) {
      return this.div(multiplier, multiplier, multiplier);
   }

   public Vecs3d div(EnumFacing direction) {
      return this.div((double)direction.getFrontOffsetX(), (double)direction.getFrontOffsetY(), (double)direction.getFrontOffsetZ());
   }

   public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public Vecs3d normalize() {
      Vecs3d v = this.clone();
      double len = this.length();
      if (len == 0.0D) {
         return v;
      } else {
         v.x /= len;
         v.y /= len;
         v.z /= len;
         return v;
      }
   }

   public Vecs3d abs() {
      return new Vecs3d(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
   }

   public double dot(Vecs3d v) {
      return this.x * v.getX() + this.y * v.getY() + this.z * v.getZ();
   }

   public Vecs3d cross(Vecs3d v) {
      return new Vecs3d(this.y * v.getZ() - this.z * v.getY(), this.x * v.getZ() - this.z * v.getX(), this.x * v.getY() - this.y * v.getX());
   }

   public Vecs3d getRelative(double x, double y, double z) {
      return this.clone().add(x, y, z);
   }

   public Vecs3d getRelative(EnumFacing dir) {
      return this.getRelative((double)dir.getFrontOffsetX(), (double)dir.getFrontOffsetY(), (double)dir.getFrontOffsetZ());
   }

   public EnumFacing getDirectionTo(Vecs3d vec) {
      for(EnumFacing d : EnumFacing.VALUES) {
         if (this.getBlockX() + d.getFrontOffsetX() == vec.getBlockX() && this.getBlockY() + d.getFrontOffsetY() == vec.getBlockY() && this.getBlockZ() + d.getFrontOffsetZ() == vec.getBlockZ()) {
            return d;
         }
      }

      return null;
   }

   public boolean isZero() {
      return this.x == 0.0D && this.y == 0.0D && this.z == 0.0D;
   }

   public Vecs3d clone() {
      return new Vecs3d(this.x, this.y, this.z, this.w);
   }

   public boolean hasTileEntity() {
      if (this.hasWorld()) {
         return this.w.getTileEntity(this.getBlockPos()) != null;
      } else {
         return false;
      }
   }

   public BlockPos getBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public TileEntity getTileEntity() {
      return this.hasTileEntity() ? this.w.getTileEntity(this.getBlockPos()) : null;
   }

   public boolean isBlock(Block b) {
      return this.isBlock(b, false);
   }

   public boolean isBlock(Block b, boolean checkAir) {
      if (this.hasWorld()) {
         IBlockState state = this.w.getBlockState(this.getBlockPos());
         Block bl = state.getBlock();
         if (b == null && bl == Blocks.AIR) {
            return true;
         } else if (b == null && checkAir && bl.getMaterial(state) == Material.AIR) {
            return true;
         } else {
            return b == null && checkAir && bl.isAir(state, this.w, this.getBlockPos()) ? true : bl.getClass().isInstance(b);
         }
      } else {
         return false;
      }
   }

   public Block getBlock() {
      return this.getBlock(false);
   }

   public Block getBlock(boolean airIsNull) {
      if (this.hasWorld()) {
         return airIsNull && this.isBlock((Block)null, true) ? null : this.w.getBlockState(this.getBlockPos()).getBlock();
      } else {
         return null;
      }
   }

   public World getWorld() {
      return this.w;
   }

   public Vecs3d setWorld(World world) {
      this.w = world;
      return this;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public int getBlockX() {
      return (int)Math.floor(this.x);
   }

   public int getBlockY() {
      return (int)Math.floor(this.y);
   }

   public int getBlockZ() {
      return (int)Math.floor(this.z);
   }

   public double distanceTo(Vecs3d vec) {
      return this.distanceTo(vec.x, vec.y, vec.z);
   }

   public double distanceTo(double x, double y, double z) {
      double dx = x - this.x;
      double dy = y - this.y;
      double dz = z - this.z;
      return dx * dx + dy * dy + dz * dz;
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Vecs3d)) {
         return false;
      } else {
         Vecs3d vec = (Vecs3d)obj;
         return vec.w == this.w && vec.x == this.x && vec.y == this.y && vec.z == this.z;
      }
   }

   public int hashCode() {
      return (new Double(this.x)).hashCode() + (new Double(this.y)).hashCode() << 8 + (new Double(this.z)).hashCode() << 16;
   }

   public Vec3i toVec3() {
      return new Vec3i(this.x, this.y, this.z);
   }

   public String toString() {
      String s = "Vector3{";
      if (this.hasWorld()) {
         s = s + "w=" + this.w.provider.getDimension() + ";";
      }

      s = s + "x=" + this.x + ";y=" + this.y + ";z=" + this.z + "}";
      return s;
   }

   public EnumFacing toForgeDirection() {
      if (this.z == 1.0D) {
         return EnumFacing.SOUTH;
      } else if (this.z == -1.0D) {
         return EnumFacing.NORTH;
      } else if (this.x == 1.0D) {
         return EnumFacing.EAST;
      } else if (this.x == -1.0D) {
         return EnumFacing.WEST;
      } else if (this.y == 1.0D) {
         return EnumFacing.UP;
      } else {
         return this.y == -1.0D ? EnumFacing.DOWN : null;
      }
   }

   public static Vecs3d fromString(String s) {
      if (s.startsWith("Vector3{") && s.endsWith("}")) {
         World w = null;
         double x = 0.0D;
         double y = 0.0D;
         double z = 0.0D;
         String s2 = s.substring(s.indexOf("{") + 1, s.lastIndexOf("}"));
         StringTokenizer st = new StringTokenizer(s2, ";");

         while(st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.toLowerCase().startsWith("w")) {
               int world = Integer.parseInt(t.split("=")[1]);
               if (!FMLCommonHandler.instance().getEffectiveSide().isServer()) {
                  w = getClientWorld(world);
               }
            }

            if (t.toLowerCase().startsWith("x")) {
               x = Double.parseDouble(t.split("=")[1]);
            }

            if (t.toLowerCase().startsWith("y")) {
               y = Double.parseDouble(t.split("=")[1]);
            }

            if (t.toLowerCase().startsWith("z")) {
               z = Double.parseDouble(t.split("=")[1]);
            }
         }

         if (w != null) {
            return new Vecs3d(x, y, z, w);
         } else {
            return new Vecs3d(x, y, z);
         }
      } else {
         return null;
      }
   }

   @SideOnly(Side.CLIENT)
   private static World getClientWorld(int world) {
      return Minecraft.getMinecraft().world.provider.getDimension() != world ? null : Minecraft.getMinecraft().world;
   }
}
