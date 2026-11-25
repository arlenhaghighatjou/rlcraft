package noppes.vc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.CommonUtils;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileCouchWool;

public class BlockCouchWool extends BlockBasicContainer {
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 5);
   public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

   public BlockCouchWool() {
      super(Material.WOOD);
      this.setSoundType(SoundType.WOOD);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack item = player.inventory.getCurrentItem();
      if (item != null && item.getItem() == Items.DYE && hand == EnumHand.MAIN_HAND) {
         TileBasicRotation tile = (TileBasicRotation)par1World.getTileEntity(pos);
         int color = EnumDyeColor.byDyeDamage(item.getItemDamage()).getMetadata();
         if (tile.color != color) {
            CommonUtils.ConsumeItemStack(1, player);
            tile.color = color;
            tile.markDirty();
            par1World.notifyBlockUpdate(pos, state, state, 3);
         }

         return true;
      } else {
         return BlockChair.MountBlock(par1World, pos, player);
      }
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return AABB;
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
      par3List.add(new ItemStack(this, 1, 3));
      par3List.add(new ItemStack(this, 1, 4));
      par3List.add(new ItemStack(this, 1, 5));
   }

   public int getMetaFromState(IBlockState state) {
      return this.damageDropped(state);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DAMAGE, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{DAMAGE});
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      int l = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
      l = l % 4;
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      TileCouchWool tile = (TileCouchWool)world.getTileEntity(pos);
      tile.rotation = l;
      tile.color = 15 - stack.getItemDamage();
      this.updateModel(world, pos, tile);
      this.neighborChanged(state, world, pos.east(), this, pos);
      this.neighborChanged(state, world, pos.west(), this, pos);
      this.neighborChanged(state, world, pos.north(), this, pos);
      this.neighborChanged(state, world, pos.south(), this, pos);
      this.updateModel(world, pos, tile);
      tile.markDirty();
      world.notifyBlockUpdate(pos, state, state, 3);
   }

   public void neighborChanged(IBlockState state, World worldObj, BlockPos pos, Block block, BlockPos pos2) {
      if (!worldObj.isRemote && block == this) {
         TileEntity tile = worldObj.getTileEntity(pos);
         if (tile != null && tile instanceof TileCouchWool) {
            this.updateModel(worldObj, pos, (TileCouchWool)tile);
            tile.markDirty();
            worldObj.notifyBlockUpdate(pos, state, state, 3);
         }
      }
   }

   private void updateModel(World world, BlockPos pos, TileCouchWool tile) {
      if (!world.isRemote) {
         int meta = tile.getBlockMetadata();
         if (tile.rotation == 0) {
            tile.hasCornerLeft = this.compareCornerTiles(tile, pos.north(), world, meta, true);
            tile.hasCornerRight = this.compareCornerTiles(tile, pos.north(), world, meta, false);
            tile.hasLeft = this.compareTiles(tile, pos.west(), world, meta);
            tile.hasRight = this.compareTiles(tile, pos.east(), world, meta);
         } else if (tile.rotation == 2) {
            tile.hasCornerLeft = this.compareCornerTiles(tile, pos.south(), world, meta, true);
            tile.hasCornerRight = this.compareCornerTiles(tile, pos.south(), world, meta, false);
            tile.hasLeft = this.compareTiles(tile, pos.east(), world, meta);
            tile.hasRight = this.compareTiles(tile, pos.west(), world, meta);
         } else if (tile.rotation == 1) {
            tile.hasCornerLeft = this.compareCornerTiles(tile, pos.east(), world, meta, true);
            tile.hasCornerRight = this.compareCornerTiles(tile, pos.east(), world, meta, false);
            tile.hasLeft = this.compareTiles(tile, pos.north(), world, meta);
            tile.hasRight = this.compareTiles(tile, pos.south(), world, meta);
         } else if (tile.rotation == 3) {
            tile.hasCornerLeft = this.compareCornerTiles(tile, pos.west(), world, meta, true);
            tile.hasCornerRight = this.compareCornerTiles(tile, pos.west(), world, meta, false);
            tile.hasLeft = this.compareTiles(tile, pos.south(), world, meta);
            tile.hasRight = this.compareTiles(tile, pos.north(), world, meta);
         }

      }
   }

   private boolean compareCornerTiles(TileCouchWool tile, BlockPos pos, World world, int meta, boolean isLeft) {
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() != this) {
         return false;
      } else {
         int meta2 = ((Integer)state.getValue(DAMAGE)).intValue();
         if (meta2 != meta) {
            return false;
         } else {
            TileEntity tile2 = world.getTileEntity(pos);
            int rotation = (tile.rotation + (!isLeft ? 1 : 3)) % 4;
            return tile2 != null & tile2 instanceof TileCouchWool && ((TileCouchWool)tile2).rotation == rotation;
         }
      }
   }

   private boolean compareTiles(TileCouchWool tile, BlockPos pos, World world, int meta) {
      IBlockState state = world.getBlockState(pos);
      if (state.getBlock() != this) {
         return false;
      } else {
         int meta2 = ((Integer)state.getValue(DAMAGE)).intValue();
         if (meta2 != meta) {
            return false;
         } else {
            TileEntity tile2 = world.getTileEntity(pos);
            if (tile2 != null && tile2 instanceof TileCouchWool) {
               TileCouchWool couch = (TileCouchWool)tile2;
               int rotation = couch.rotation;
               if (tile.rotation == rotation) {
                  return true;
               } else {
                  if (couch.hasCornerLeft) {
                     rotation += 3;
                  } else if (couch.hasCornerRight) {
                     ++rotation;
                  }

                  rotation = rotation % 4;
                  return tile.rotation == rotation;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileCouchWool();
   }
}
