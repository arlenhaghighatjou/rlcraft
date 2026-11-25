package ivorius.ivtoolkit.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class IvBlockMultiblock extends Block {
   protected IvBlockMultiblock(Material par2Material) {
      super(par2Material);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof IvTileEntityMultiBlock) {
         IvTileEntityMultiBlock tileEntityMultiBlock = (IvTileEntityMultiBlock)tileEntity;
         if (!tileEntityMultiBlock.multiblockInvalid) {
            tileEntityMultiBlock.multiblockInvalid = true;
            if (tileEntityMultiBlock.isParent()) {
               this.destroyChildrenOf(world, pos, state, tileEntityMultiBlock);
            } else {
               this.destroyParentOf(tileEntityMultiBlock);
            }
         }
      }

      super.breakBlock(world, pos, state);
   }

   public void destroyChildrenOf(World world, BlockPos pos, IBlockState state, IvTileEntityMultiBlock tileEntityMultiBlock) {
      if (!world.isRemote) {
         this.parentBlockDropItemContents(world, tileEntityMultiBlock, pos, state);
      }

      BlockPos[] toDestroy = tileEntityMultiBlock.getActiveChildCoords();
      TileEntity[] destroyTEs = new TileEntity[toDestroy.length];

      for(int i = 0; i < toDestroy.length; ++i) {
         TileEntity otherTE = world.getTileEntity(toDestroy[i]);
         if (otherTE instanceof IvTileEntityMultiBlock && !((IvTileEntityMultiBlock)otherTE).multiblockInvalid) {
            ((IvTileEntityMultiBlock)(destroyTEs[i] = otherTE)).multiblockInvalid = true;
         }
      }

      for(TileEntity destroyTE : destroyTEs) {
         if (destroyTE != null) {
            this.triggerBlockDestroyEffects(world, destroyTE.getPos());
            world.setBlockToAir(destroyTE.getPos());
         }
      }

   }

   protected void triggerBlockDestroyEffects(World world, BlockPos pos) {
      world.playBroadcastSound(2001, pos, Block.getStateId(world.getBlockState(pos)));
   }

   public void destroyParentOf(IvTileEntityMultiBlock tileEntityMultiBlock) {
      World world = tileEntityMultiBlock.getWorld();
      TileEntity parentTE = world.getTileEntity(tileEntityMultiBlock.getActiveParentCoords());
      if (parentTE instanceof IvTileEntityMultiBlock && !((IvTileEntityMultiBlock)parentTE).multiblockInvalid) {
         this.triggerBlockDestroyEffects(world, parentTE.getPos());
         world.setBlockToAir(parentTE.getPos());
      }

   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      IvTileEntityMultiBlock tileEntityMultiBlock = getValidatedTotalParent(this, world, pos);
      if (tileEntityMultiBlock != null && !world.isRemote && willHarvest) {
         this.parentBlockHarvestItem(world, tileEntityMultiBlock, pos, world.getBlockState(pos));
      }

      return super.removedByPlayer(state, world, pos, player, willHarvest);
   }

   public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
      IvTileEntityMultiBlock tileEntityMultiBlock = getValidatedTotalParent(this, world, pos);
      if (tileEntityMultiBlock != null && !world.isRemote) {
         this.parentBlockHarvestItem(world, tileEntityMultiBlock, pos, world.getBlockState(pos));
      }

      super.onBlockExploded(world, pos, explosion);
   }

   public void parentBlockDropItemContents(World world, IvTileEntityMultiBlock tileEntity, BlockPos pos, IBlockState state) {
   }

   public void parentBlockHarvestItem(World world, IvTileEntityMultiBlock tileEntity, BlockPos pos, IBlockState state) {
   }

   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
   }

   public static boolean validateMultiblock(Block block, IBlockAccess access, BlockPos pos) {
      if (access.getBlockState(pos) != block) {
         return false;
      } else {
         boolean isValidChild = false;
         boolean destroy = false;
         TileEntity tileEntity = access.getTileEntity(pos);
         if (tileEntity instanceof IvTileEntityMultiBlock) {
            IvTileEntityMultiBlock tileEntityMultiBlock = (IvTileEntityMultiBlock)tileEntity;
            IvTileEntityMultiBlock parent = tileEntityMultiBlock.getParent();
            isValidChild = tileEntityMultiBlock.isParent() || parent != null && parent.isParent();
            destroy = !isValidChild && !tileEntityMultiBlock.multiblockInvalid;
         }

         if (destroy && access instanceof World) {
            ((World)access).setBlockToAir(pos);
         }

         return isValidChild;
      }
   }

   public static IvTileEntityMultiBlock getValidatedIfParent(Block block, World world, BlockPos pos) {
      if (validateMultiblock(block, world, pos)) {
         IvTileEntityMultiBlock tileEntity = (IvTileEntityMultiBlock)world.getTileEntity(pos);
         return tileEntity.isParent() ? tileEntity : null;
      } else {
         return null;
      }
   }

   public static IvTileEntityMultiBlock getValidatedTotalParent(Block block, IBlockAccess access, BlockPos pos) {
      if (validateMultiblock(block, access, pos)) {
         TileEntity tileEntity = access.getTileEntity(pos);
         if (tileEntity instanceof IvTileEntityMultiBlock) {
            return ((IvTileEntityMultiBlock)tileEntity).getTotalParent();
         }
      }

      return null;
   }
}
