package com.TominoCZ.FBP.block;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.material.FBPMaterial;
import com.TominoCZ.FBP.node.BlockNode;
import com.TominoCZ.FBP.particle.FBPParticleBlock;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FBPAnimationDummyBlock extends Block {
   public ConcurrentHashMap<BlockPos, BlockNode> blockNodes = new ConcurrentHashMap();

   public FBPAnimationDummyBlock() {
      super(new FBPMaterial());
      this.setRegistryName(new ResourceLocation("fbp", "FBPPlaceholderBlock"));
      this.translucent = true;
   }

   public void copyState(World w, BlockPos pos, IBlockState state, FBPParticleBlock p) {
      if (!this.blockNodes.containsKey(pos)) {
         this.blockNodes.put(pos, new BlockNode(state, p));
      }
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (this.blockNodes.containsKey(pos)) {
         BlockNode n = (BlockNode)this.blockNodes.get(pos);

         try {
            return n.originalBlock.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
         } catch (Throwable var12) {
            return false;
         }
      } else {
         return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
      }
   }

   public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.isNormalCube();
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return super.isNormalCube(state, world, pos);
   }

   public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlock().isAir(state, world, pos);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return super.isAir(state, world, pos);
   }

   public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlock().isBed(state, world, pos, player);
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

      return super.isBed(state, world, pos, player);
   }

   public boolean isBedFoot(IBlockAccess world, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlock().isBedFoot(world, pos);
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

      return super.isBedFoot(world, pos);
   }

   public boolean isBurning(IBlockAccess world, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlock().isBurning(world, pos);
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

      return super.isBurning(world, pos);
   }

   public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlock().isFlammable(world, pos, face);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return super.isFlammable(world, pos, face);
   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlock().isLadder(state, world, pos, entity);
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

      return super.isLadder(state, world, pos, entity);
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getMaterial().isReplaceable();
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

      return this.blockMaterial.isReplaceable();
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.originalBlock.isPassable(worldIn, pos);
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

      return !this.blockMaterial.blocksMovement();
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            n.originalBlock.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getCollisionBoundingBox(worldIn, pos);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return Block.FULL_BLOCK_AABB.offset(pos);
   }

   public AxisAlignedBB getBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBoundingBox(worldIn, pos);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return Block.FULL_BLOCK_AABB.offset(pos);
   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
      return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   }

   public float getBlockHardness(IBlockState blockState, World w, BlockPos pos) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode n = (BlockNode)this.blockNodes.get(pos);
            return n.state.getBlockHardness(w, pos);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return blockState.getBlockHardness(w, pos);
   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      try {
         BlockNode node = (BlockNode)FBP.FBPBlock.blockNodes.get(pos);
         if (node == null) {
            return;
         }

         if (worldIn.isRemote && state.getBlock() != node.originalBlock && (worldIn.getBlockState(pos).getBlock() instanceof FBPAnimationDummyBlock || state.getBlock() instanceof FBPAnimationDummyBlock)) {
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, node.originalBlock.getStateFromMeta(node.meta));
         }

         if (node.particle != null) {
            node.particle.killParticle();
         }

         FBP.INSTANCE.eventHandler.removePosEntry(pos);
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean b) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            ((BlockNode)this.blockNodes.get(pos)).state.addCollisionBoxToList(worldIn, pos, entityBox, collidingBoxes, entityIn, b);
         }
      } catch (Exception var9) {
         ;
      }

   }

   public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion ex) {
      return this.blockNodes.containsKey(p) ? ((BlockNode)this.blockNodes.get(p)).originalBlock.getExplosionResistance(w, p, e, ex) : super.getExplosionResistance(w, p, e, ex);
   }

   public float getExplosionResistance(Entity e) {
      return this.blockNodes.containsKey(e.getPosition()) ? ((BlockNode)this.blockNodes.get(e.getPosition())).originalBlock.getExplosionResistance(e) : super.getExplosionResistance(e);
   }

   public float getEnchantPowerBonus(World w, BlockPos p) {
      return this.blockNodes.containsKey(p) ? ((BlockNode)this.blockNodes.get(p)).originalBlock.getEnchantPowerBonus(w, p) : super.getEnchantPowerBonus(w, p);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.getFlammability(world, pos, face) : super.getFlammability(world, pos, face);
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.getFireSpreadSpeed(world, pos, face) : super.getFireSpreadSpeed(world, pos, face);
   }

   public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.getWeakChanges(world, pos) : super.getWeakChanges(world, pos);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            BlockNode node = (BlockNode)this.blockNodes.get(pos);
            if (node.originalBlock != this && node.state.getBlock() == node.originalBlock) {
               return ((BlockNode)this.blockNodes.get(pos)).originalBlock.getPickBlock(node.state, target, world, pos, player);
            }
         }
      } catch (Throwable var7) {
         ;
      }

      return new ItemStack(Blocks.AIR);
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).state.getWeakPower(blockAccess, pos, side) : 0;
   }

   public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.canPlaceTorchOnTop(state, world, pos) : true;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.canPlaceBlockAt(worldIn, pos) : false;
   }

   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.canPlaceBlockOnSide(worldIn, pos, side) : false;
   }

   public IBlockState getExtendedState(IBlockState s, IBlockAccess w, BlockPos p) {
      return this.blockNodes.containsKey(p) ? ((BlockNode)this.blockNodes.get(p)).originalBlock.getExtendedState(s, w, p) : super.getExtendedState(s, w, p);
   }

   public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
      if (!this.blockNodes.containsKey(pos)) {
         return SoundType.STONE;
      } else {
         BlockNode n = (BlockNode)this.blockNodes.get(pos);
         return n.state.getBlock().getSoundType(n.state, world, pos, entity);
      }
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      try {
         if (this.blockNodes.containsKey(pos)) {
            new ItemStack(Item.getItemFromBlock(((BlockNode)this.blockNodes.get(pos)).originalBlock), 1, this.damageDropped(state));
         }

         return new ItemStack(Item.getItemFromBlock(this), 1, this.damageDropped(state));
      } catch (Throwable var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public boolean canConnectRedstone(IBlockState s, IBlockAccess w, BlockPos pos, EnumFacing side) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.canConnectRedstone(s, w, pos, side) : false;
   }

   public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos p) {
      if (this.blockNodes.containsKey(pos)) {
         ((BlockNode)this.blockNodes.get(pos)).originalBlock.onNeighborChange(w, pos, p);
      }

   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (this.blockNodes.containsKey(pos)) {
         ((BlockNode)this.blockNodes.get(pos)).originalBlock.onBlockAdded(worldIn, pos, state);
      }

   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.getDrops(world, pos, state, fortune) : super.getDrops(world, pos, state, fortune);
   }

   public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.getExpDrop(state, world, pos, fortune) : super.getExpDrop(state, world, pos, fortune);
   }

   public Item getItemDropped(IBlockState state, Random r, int i) {
      return null;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockState state) {
      return 1.0F;
   }

   public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return this.blockNodes.containsKey(pos) ? ((BlockNode)this.blockNodes.get(pos)).originalBlock.isSideSolid(base_state, world, pos, side) : true;
   }
}
