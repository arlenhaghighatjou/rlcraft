package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLucisMushroom extends Block {
   public static final PropertyEnum<BlockLucisMushroom.EnumDir> FACING = PropertyEnum.create("facing", BlockLucisMushroom.EnumDir.class);
   public static final PropertyEnum<BlockLucisMushroom.EnumShape> SHAPE = PropertyEnum.create("shape", BlockLucisMushroom.EnumShape.class);
   private static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);

   public BlockLucisMushroom() {
      super(Material.WOOD, MapColor.YELLOW);
      this.setRegistryName("lucis_mushroom");
      this.setUnlocalizedName("lucis_mushroom");
      this.setHardness(1.0F);
      this.setLightLevel(1.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, BlockLucisMushroom.EnumDir.NORTH).withProperty(SHAPE, BlockLucisMushroom.EnumShape.CENTER));
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.GLOWSTONE_DUST;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return random.nextInt(3);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(BlocksRegister.BLOCK_LUCIS_SPORE);
   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote && worldIn.rand.nextBoolean()) {
         this.spawnSeeds(worldIn, pos);
      }

      worldIn.destroyBlock(pos, true);
   }

   public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
      if (!worldIn.isRemote && worldIn.rand.nextInt(4) == 0) {
         this.spawnSeeds(worldIn, pos);
      }

      worldIn.destroyBlock(pos, true);
   }

   private void spawnSeeds(World world, BlockPos pos) {
      ItemStack drop = new ItemStack(BlocksRegister.BLOCK_LUCIS_SPORE, 1);
      EntityItem itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
      world.spawnEntity(itemEntity);
   }

   private void updateState(World worldIn, BlockPos pos, IBlockState state) {
      boolean north = worldIn.getBlockState(pos.north()).getBlock() == this;
      boolean south = worldIn.getBlockState(pos.south()).getBlock() == this;
      boolean east = worldIn.getBlockState(pos.east()).getBlock() == this;
      boolean west = worldIn.getBlockState(pos.west()).getBlock() == this;
      if (north && south && east && west) {
         worldIn.setBlockState(pos, state.withProperty(SHAPE, BlockLucisMushroom.EnumShape.CENTER));
      } else if (north && south) {
         BlockLucisMushroom.EnumDir dir = null;
         if (east) {
            dir = BlockLucisMushroom.EnumDir.WEST;
         } else {
            dir = BlockLucisMushroom.EnumDir.EAST;
         }

         worldIn.setBlockState(pos, state.withProperty(SHAPE, BlockLucisMushroom.EnumShape.SIDE).withProperty(FACING, dir));
      } else if (east && west) {
         BlockLucisMushroom.EnumDir dir = null;
         if (north) {
            dir = BlockLucisMushroom.EnumDir.SOUTH;
         } else {
            dir = BlockLucisMushroom.EnumDir.NORTH;
         }

         worldIn.setBlockState(pos, state.withProperty(SHAPE, BlockLucisMushroom.EnumShape.SIDE).withProperty(FACING, dir));
      } else {
         BlockLucisMushroom.EnumDir dir = BlockLucisMushroom.EnumDir.NORTH;
         if (north) {
            dir = east ? BlockLucisMushroom.EnumDir.NORTH : BlockLucisMushroom.EnumDir.WEST;
         } else if (south) {
            dir = west ? BlockLucisMushroom.EnumDir.SOUTH : BlockLucisMushroom.EnumDir.EAST;
         }

         worldIn.setBlockState(pos, state.withProperty(SHAPE, BlockLucisMushroom.EnumShape.CORNER).withProperty(FACING, dir));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      int type = meta >> 2;
      int face = meta & 3;
      BlockLucisMushroom.EnumDir direc = null;
      BlockLucisMushroom.EnumShape shape = null;
      switch(type) {
      case 0:
         shape = BlockLucisMushroom.EnumShape.CORNER;
         break;
      case 1:
         shape = BlockLucisMushroom.EnumShape.SIDE;
         break;
      case 2:
         shape = BlockLucisMushroom.EnumShape.CENTER;
      }

      switch(face) {
      case 0:
         direc = BlockLucisMushroom.EnumDir.NORTH;
         break;
      case 1:
         direc = BlockLucisMushroom.EnumDir.SOUTH;
         break;
      case 2:
         direc = BlockLucisMushroom.EnumDir.EAST;
         break;
      case 3:
         direc = BlockLucisMushroom.EnumDir.WEST;
      }

      return this.getDefaultState().withProperty(FACING, direc).withProperty(SHAPE, shape);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockLucisMushroom.EnumDir)state.getValue(FACING)).getIndex() | ((BlockLucisMushroom.EnumShape)state.getValue(SHAPE)).getIndex() << 2;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, SHAPE});
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return MUSHROOM_AABB;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      boolean central = state.getValue(SHAPE) == BlockLucisMushroom.EnumShape.CENTER;
      if (face == EnumFacing.DOWN) {
         return central ? BlockFaceShape.SOLID : BlockFaceShape.CENTER_BIG;
      } else {
         return BlockFaceShape.CENTER_BIG;
      }
   }

   public static enum EnumDir implements IStringSerializable {
      NORTH(0, "north"),
      SOUTH(1, "south"),
      EAST(2, "east"),
      WEST(3, "west");

      private final String name;
      private final int index;

      private EnumDir(int index, String name) {
         this.name = name;
         this.index = index;
      }

      public String toString() {
         return this.name;
      }

      public String getName() {
         return this.name;
      }

      public int getIndex() {
         return this.index;
      }
   }

   public static enum EnumShape implements IStringSerializable {
      CORNER(0, "corner"),
      SIDE(1, "side"),
      CENTER(2, "center");

      private final String name;
      private final int index;

      private EnumShape(int index, String name) {
         this.name = name;
         this.index = index;
      }

      public String toString() {
         return this.name;
      }

      public String getName() {
         return this.name;
      }

      public int getIndex() {
         return this.index;
      }
   }
}
