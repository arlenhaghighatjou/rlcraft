package svenhjol.meson.decorator;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import svenhjol.meson.Meson;

public abstract class MesonInnerDecorator {
   protected Random rand;
   protected World world;
   protected StructureComponent structure;
   protected StructureBoundingBox box;
   protected MesonDecoratorTheme theme;
   protected MesonDecoratorItems items;

   public MesonInnerDecorator(StructureComponent structure, World world, StructureBoundingBox box) {
      this.structure = structure;
      this.world = world;
      this.box = box;
      this.rand = new Random();
      this.rand.setSeed((long)structure.hashCode());

      try {
         this.theme = (MesonDecoratorTheme)this.getThemeClass().getConstructor(MesonInnerDecorator.class).newInstance(this);
      } catch (Exception var5) {
         Meson.runtimeException("Could not instantiate required decorator classes");
      }

      this.items = new MesonDecoratorItems(this);
   }

   protected abstract Class<? extends MesonDecoratorTheme> getThemeClass();

   public abstract void generate();

   public StructureComponent getStructure() {
      return this.structure;
   }

   public World getWorld() {
      return this.world;
   }

   public StructureBoundingBox getBox() {
      return this.box;
   }

   public Random getRand() {
      return this.rand;
   }

   public MesonDecoratorTheme getTheme() {
      return this.theme;
   }

   public void add(Block block, int x, int y, int z) {
      this.add(block.getDefaultState(), x, y, z, EnumFacing.NORTH);
   }

   public void add(Block block, int x, int y, int z, EnumFacing facing) {
      this.add(block.getDefaultState(), x, y, z, facing);
   }

   public void add(IBlockState state, int x, int y, int z) {
      this.add(state, x, y, z, EnumFacing.NORTH);
   }

   public void add(IBlockState state, int x, int y, int z, EnumFacing facing) {
      BlockPos place = new BlockPos(x, y, z);
      this.setBlockState(state, place, facing);
   }

   public void addRow(List<IBlockState> states, int x, int y, int z, EnumFacing direction, EnumFacing facing) {
      BlockPos pos = new BlockPos(x, y, z);
      int i = 0;

      for(IBlockState state : states) {
         BlockPos offset = pos.offset(direction, i++);
         this.add(state, offset.getX(), offset.getY(), offset.getZ(), facing);
      }

   }

   public void fill(Block block, int x0, int y0, int z0, int x1, int y1, int z1, boolean overwrite) {
      this.fill(block.getDefaultState(), x0, y0, z0, x1, y1, z1, EnumFacing.NORTH, overwrite);
   }

   public void fill(IBlockState state, int x0, int y0, int z0, int x1, int y1, int z1, EnumFacing facing, boolean overwrite) {
      this.fillWithBlocks(state, x0, y0, z0, x1, y1, z1, facing, overwrite);
   }

   public TileEntity addTileEntity(BlockPos pos, IBlockState state, EnumFacing facing) {
      BlockPos place = this.getRelativePos(pos);
      if (this.isNotInBox(place)) {
         return null;
      } else {
         TileEntity tile = this.world.getTileEntity(place);
         if (tile != null) {
            return null;
         } else {
            this.setBlockState(state, pos, facing);
            return this.world.getTileEntity(place);
         }
      }
   }

   public boolean chance(float chance) {
      return this.rand.nextFloat() <= chance;
   }

   protected boolean common() {
      return this.chance(Meson.DEBUG ? 1.0F : 0.85F);
   }

   protected boolean uncommon() {
      return this.chance(Meson.DEBUG ? 1.0F : 0.25F);
   }

   protected boolean valuable() {
      return this.chance(Meson.DEBUG ? 1.0F : 0.05F);
   }

   protected boolean rare() {
      return this.chance(Meson.DEBUG ? 1.0F : 0.005F);
   }

   public void fillWithBlocks(IBlockState state, int x0, int y0, int z0, int x1, int y1, int z1, EnumFacing facing, boolean overwrite) {
      for(int y = y0; y <= y1; ++y) {
         for(int x = x0; x <= x1; ++x) {
            for(int z = z0; z <= z1; ++z) {
               BlockPos place = new BlockPos(x, y, z);
               if (overwrite || this.getBlockStateFromPos(place).getMaterial() == Material.AIR) {
                  this.setBlockState(state, place, facing);
               }
            }
         }
      }

   }

   public void setBlockState(IBlockState state, BlockPos pos, EnumFacing facing) {
      BlockPos place = this.getRelativePos(pos);
      if (this.box.isVecInside(place)) {
         state = this.getRotatedState(facing, state);
         this.world.setBlockState(place, this.getRelativeState(state), 2);
      }
   }

   public IBlockState getBlockStateFromPos(BlockPos pos) {
      int xo = this.getXWithOffset(pos.getX(), pos.getZ());
      int yo = this.getYWithOffset(pos.getY());
      int zo = this.getZWithOffset(pos.getX(), pos.getZ());
      BlockPos newpos = new BlockPos(xo, yo, zo);
      return this.world.getBlockState(newpos);
   }

   public int getXWithOffset(int x, int z) {
      EnumFacing enumfacing = this.structure.getCoordBaseMode();
      if (enumfacing == null) {
         return x;
      } else {
         switch(enumfacing) {
         case NORTH:
         case SOUTH:
            return this.structure.getBoundingBox().minX + x;
         case WEST:
            return this.structure.getBoundingBox().maxX - z;
         case EAST:
            return this.structure.getBoundingBox().minX + z;
         default:
            return x;
         }
      }
   }

   public int getYWithOffset(int y) {
      return this.structure.getCoordBaseMode() == null ? y : y + this.structure.getBoundingBox().minY;
   }

   public int getZWithOffset(int x, int z) {
      EnumFacing enumfacing = this.structure.getCoordBaseMode();
      if (enumfacing == null) {
         return z;
      } else {
         switch(enumfacing) {
         case NORTH:
            return this.structure.getBoundingBox().maxZ - z;
         case SOUTH:
            return this.structure.getBoundingBox().minZ + z;
         case WEST:
         case EAST:
            return this.structure.getBoundingBox().minZ + x;
         default:
            return z;
         }
      }
   }

   public BlockPos getRelativePos(BlockPos pos) {
      return new BlockPos(this.getXWithOffset(pos.getX(), pos.getZ()), this.getYWithOffset(pos.getY()), this.getZWithOffset(pos.getX(), pos.getZ()));
   }

   public IBlockState getRelativeState(IBlockState original) {
      return this.getRotatedState(this.structure.getCoordBaseMode(), original);
   }

   public EnumFacing getRelativeFacing(EnumFacing facing) {
      return this.getRotatedFacing(this.structure.getCoordBaseMode(), facing);
   }

   public IBlockState getRotatedState(EnumFacing axis, IBlockState original) {
      Mirror mirror;
      Rotation rotation;
      if (axis == null) {
         rotation = Rotation.NONE;
         mirror = Mirror.NONE;
      } else {
         switch(axis) {
         case SOUTH:
            mirror = Mirror.LEFT_RIGHT;
            rotation = Rotation.NONE;
            break;
         case WEST:
            mirror = Mirror.LEFT_RIGHT;
            rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            mirror = Mirror.NONE;
            rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            mirror = Mirror.NONE;
            rotation = Rotation.NONE;
         }
      }

      return original.withMirror(mirror).withRotation(rotation);
   }

   public EnumFacing getRotatedFacing(EnumFacing axis, EnumFacing facing) {
      Rotation rotation;
      Mirror mirror;
      if (axis == null) {
         rotation = Rotation.NONE;
         mirror = Mirror.NONE;
      } else {
         switch(axis) {
         case SOUTH:
            mirror = Mirror.LEFT_RIGHT;
            rotation = Rotation.NONE;
            break;
         case WEST:
            mirror = Mirror.LEFT_RIGHT;
            rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            mirror = Mirror.NONE;
            rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            mirror = Mirror.NONE;
            rotation = Rotation.NONE;
         }
      }

      return rotation.add(mirror.toRotation(facing)).rotate(facing);
   }

   public float getRotatedAngle(EnumFacing axis, float angle) {
      float newAngle = angle;
      if (axis != null) {
         switch(axis) {
         case NORTH:
         default:
            break;
         case SOUTH:
            newAngle = angle + 270.0F;
            break;
         case WEST:
            newAngle = angle + 180.0F;
            break;
         case EAST:
            newAngle = angle + 90.0F;
         }
      }

      return newAngle;
   }

   public boolean isNotInBox(BlockPos pos) {
      return !this.box.isVecInside(pos);
   }
}
