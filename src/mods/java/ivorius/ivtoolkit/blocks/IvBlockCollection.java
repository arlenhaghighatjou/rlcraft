package ivorius.ivtoolkit.blocks;

import com.google.common.collect.ImmutableSet.Builder;
import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.ivtoolkit.raytracing.IvRaytracer;
import ivorius.ivtoolkit.tools.MCRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class IvBlockCollection {
   public final int width;
   public final int height;
   public final int length;
   private final IBlockState[] blockStates;

   public IvBlockCollection(int width, int height, int length) {
      this(airArray(width, height, length), width, height, length);
   }

   public IvBlockCollection(IBlockState[] blockStates, int width, int height, int length) {
      if (blockStates.length != width * height * length) {
         throw new IllegalArgumentException();
      } else {
         this.blockStates = (IBlockState[])blockStates.clone();
         this.width = width;
         this.height = height;
         this.length = length;
      }
   }

   public IvBlockCollection(NBTTagCompound compound, MCRegistry registry) {
      this.width = compound.getInteger("width");
      this.height = compound.getInteger("height");
      this.length = compound.getInteger("length");
      IvBlockMapper mapper = new IvBlockMapper(compound, "mapping", registry);
      Block[] blocks = mapper.createBlocksFromNBT(compound.getCompoundTag("blocks"));
      byte[] metas = compound.getByteArray("metadata");
      if (blocks.length != this.width * this.height * this.length) {
         throw new RuntimeException("Block collection length is " + blocks.length + " but should be " + this.width + " * " + this.height + " * " + this.length);
      } else if (metas.length != this.width * this.height * this.length) {
         throw new RuntimeException("Block collection length is " + metas.length + " but should be " + this.width + " * " + this.height + " * " + this.length);
      } else {
         this.blockStates = new IBlockState[this.width * this.height * this.length];

         for(int i = 0; i < this.blockStates.length; ++i) {
            this.blockStates[i] = BlockStates.fromMetadata(blocks[i], metas[i]);
         }

      }
   }

   private static IBlockState[] airArray(int width, int height, int length) {
      IBlockState[] blocks = new IBlockState[width * height * length];
      Arrays.fill(blocks, Blocks.AIR.getDefaultState());
      return blocks;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getLength() {
      return this.length;
   }

   public IBlockState getBlockState(BlockPos coord) {
      if (!this.hasCoord(coord)) {
         return Blocks.AIR.getDefaultState();
      } else {
         IBlockState block = this.blockStates[this.indexFromCoord(coord)];
         return block != null ? block : Blocks.AIR.getDefaultState();
      }
   }

   public void setBlockState(BlockPos coord, IBlockState state) {
      if (state == null) {
         throw new NullPointerException();
      } else if (this.hasCoord(coord)) {
         int index = this.indexFromCoord(coord);
         this.blockStates[index] = state;
      }
   }

   private int indexFromCoord(BlockPos coord) {
      return (coord.getZ() * this.height + coord.getY()) * this.width + coord.getX();
   }

   public boolean hasCoord(BlockPos coord) {
      return coord.getX() >= 0 && coord.getX() < this.width && coord.getY() >= 0 && coord.getY() < this.height && coord.getZ() >= 0 && coord.getZ() < this.length;
   }

   public boolean shouldRenderSide(BlockPos coord, EnumFacing side) {
      BlockPos sideCoord = coord.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
      IBlockState block = this.getBlockState(sideCoord);
      return !block.isOpaqueCube();
   }

   public RayTraceResult rayTrace(Vec3d position, Vec3d direction) {
      IvRaytraceableAxisAlignedBox containingBox = new IvRaytraceableAxisAlignedBox((Object)null, 0.001D, 0.001D, 0.001D, (double)this.width - 0.002D, (double)this.height - 0.002D, (double)this.length - 0.002D);
      IvRaytracedIntersection intersection = IvRaytracer.getFirstIntersection(Collections.singletonList(containingBox), position.x, position.y, position.z, direction.x, direction.y, direction.z);
      if (intersection != null) {
         position = new Vec3d(intersection.getX(), intersection.getY(), intersection.getZ());
         BlockPos curCoord = new BlockPos(position.x, position.y, position.z);

         for(EnumFacing hitSide = ((EnumFacing)intersection.getHitInfo()).getOpposite(); this.hasCoord(curCoord); curCoord = curCoord.add(hitSide.getFrontOffsetX(), hitSide.getFrontOffsetY(), hitSide.getFrontOffsetZ())) {
            if (this.getBlockState(curCoord).getMaterial() != Material.AIR) {
               return new RayTraceResult(position, hitSide.getOpposite(), new BlockPos(curCoord.getX(), curCoord.getY(), curCoord.getZ()));
            }

            hitSide = this.getExitSide(position, direction);
            if (hitSide.getFrontOffsetX() != 0) {
               double offX = hitSide.getFrontOffsetX() > 0 ? 1.0001D : -1.0E-4D;
               double dirLength = ((double)curCoord.getX() + offX - position.x) / direction.x;
               position = new Vec3d((double)curCoord.getX() + offX, position.y + direction.y * dirLength, position.z + direction.z * dirLength);
            } else if (hitSide.getFrontOffsetY() != 0) {
               double offY = hitSide.getFrontOffsetY() > 0 ? 1.0001D : -1.0E-4D;
               double dirLength = ((double)curCoord.getY() + offY - position.y) / direction.y;
               position = new Vec3d(position.x + direction.x * dirLength, (double)curCoord.getY() + offY, position.z + direction.z * dirLength);
            } else {
               double offZ = hitSide.getFrontOffsetZ() > 0 ? 1.0001D : -1.0E-4D;
               double dirLength = ((double)curCoord.getZ() + offZ - position.z) / direction.z;
               position = new Vec3d(position.x + direction.x * dirLength, position.y + direction.y * dirLength, (double)curCoord.getZ() + offZ);
            }
         }
      }

      return null;
   }

   private EnumFacing getExitSide(Vec3d position, Vec3d direction) {
      double innerX = (position.x % 1.0D + 1.0D) % 1.0D;
      double innerY = (position.y % 1.0D + 1.0D) % 1.0D;
      double innerZ = (position.z % 1.0D + 1.0D) % 1.0D;
      double xDist = direction.x > 0.0D ? (1.0D - innerX) / direction.x : innerX / -direction.x;
      double yDist = direction.y > 0.0D ? (1.0D - innerY) / direction.y : innerY / -direction.y;
      double zDist = direction.z > 0.0D ? (1.0D - innerZ) / direction.z : innerZ / -direction.z;
      if (xDist < yDist && xDist < zDist) {
         return direction.x > 0.0D ? EnumFacing.EAST : EnumFacing.WEST;
      } else if (yDist < zDist) {
         return direction.y > 0.0D ? EnumFacing.UP : EnumFacing.DOWN;
      } else {
         return direction.z > 0.0D ? EnumFacing.SOUTH : EnumFacing.NORTH;
      }
   }

   public int getBlockMultiplicity() {
      return (new Builder()).addAll(Arrays.asList(this.blockStates)).build().size();
   }

   public NBTTagCompound createTagCompound() {
      NBTTagCompound compound = new NBTTagCompound();
      IvBlockMapper mapper = new IvBlockMapper();
      compound.setInteger("width", this.width);
      compound.setInteger("height", this.height);
      compound.setInteger("length", this.length);
      byte[] metas = new byte[this.blockStates.length];

      for(int i = 0; i < metas.length; ++i) {
         metas[i] = (byte)this.blockStates[i].getBlock().getMetaFromState(this.blockStates[i]);
      }

      compound.setByteArray("metadata", metas);
      List<Block> blockList = (List)Stream.of(this.blockStates).map(IBlockState::getBlock).collect(Collectors.toList());
      mapper.addMapping(blockList);
      compound.setTag("mapping", mapper.createTagList());
      compound.setTag("blocks", mapper.createNBTForBlocks(blockList));
      return compound;
   }

   public IvBlockCollection copy() {
      return new IvBlockCollection(this.blockStates, this.width, this.height, this.length);
   }

   public String toString() {
      return "IvBlockCollection{length=" + this.length + ", height=" + this.height + ", width=" + this.width + '}';
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         IvBlockCollection that = (IvBlockCollection)o;
         if (this.height != that.height) {
            return false;
         } else if (this.length != that.length) {
            return false;
         } else if (this.width != that.width) {
            return false;
         } else {
            return Arrays.equals(this.blockStates, that.blockStates);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = Arrays.hashCode(this.blockStates);
      result = 31 * result + this.width;
      result = 31 * result + this.height;
      result = 31 * result + this.length;
      return result;
   }

   public BlockArea area() {
      return new BlockArea(BlockPos.ORIGIN, new BlockPos(this.width - 1, this.height - 1, this.length - 1));
   }
}
