package vazkii.quark.misc.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModDust.EnumAttachPosition;
import vazkii.quark.api.IFuseIgnitable;
import vazkii.quark.base.block.BlockQuarkDust;
import vazkii.quark.misc.feature.PlaceVanillaDusts;

public class BlockGunpowder extends BlockQuarkDust {
   public static final PropertyBool LIT = PropertyBool.create("lit");

   public BlockGunpowder() {
      super("gunpowder_block");
      this.setDefaultState(this.getDefaultState().withProperty(LIT, Boolean.valueOf(false)));
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return null;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!((Boolean)state.getValue(LIT)).booleanValue()) {
         ItemStack stack = playerIn.getHeldItem(hand);
         boolean allow = false;
         SoundEvent sound = null;
         if (stack.getItem() == Items.FLINT_AND_STEEL) {
            stack.damageItem(1, playerIn);
            sound = SoundEvents.ITEM_FLINTANDSTEEL_USE;
            allow = true;
         } else if (stack.getItem() == Items.FIRE_CHARGE) {
            stack.shrink(1);
            sound = SoundEvents.ITEM_FIRECHARGE_USE;
            allow = true;
         }

         if (allow) {
            worldIn.playSound((EntityPlayer)null, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), sound, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.4F + 0.8F);
            this.lightUp(worldIn, pos);
            return true;
         }
      }

      return false;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!((Boolean)state.getValue(LIT)).booleanValue()) {
         IBlockState otherBlock = worldIn.getBlockState(fromPos);
         if (otherBlock.getBlock() == Blocks.FIRE) {
            this.lightUp(worldIn, pos);
         }
      }

      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
   }

   private boolean lightUp(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      if (block != this) {
         if (block == Blocks.TNT) {
            world.setBlockToAir(pos);
            block.onBlockDestroyedByPlayer(world, pos, state.withProperty(BlockTNT.EXPLODE, Boolean.TRUE));
            return true;
         } else if (block instanceof IFuseIgnitable) {
            ((IFuseIgnitable)block).onIgnitedByFuse(world, pos, state);
            return true;
         } else {
            return false;
         }
      } else {
         IBlockState belowState = world.getBlockState(pos.down());
         ResourceLocation loc = belowState.getBlock().getRegistryName();
         IBlockState newState = state.withProperty(LIT, Boolean.valueOf(true));
         world.setBlockState(pos, newState);
         world.scheduleUpdate(pos, newState.getBlock(), loc != null && loc.getResourcePath().contains("netherrack") ? PlaceVanillaDusts.gunpowderDelayNetherrack : PlaceVanillaDusts.gunpowderDelay);
         if (world instanceof WorldServer) {
            float x = (float)pos.getX();
            float y = (float)pos.getY() + 0.2F;
            float z = (float)pos.getZ();
            ((WorldServer)world).spawnParticle(EnumParticleTypes.FLAME, (double)x + 0.5D, (double)y, (double)z + 0.5D, 6, 0.2D, 0.0D, 0.2D, 0.0D, new int[0]);
            ((WorldServer)world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)x + 0.5D, (double)y, (double)z + 0.5D, 6, 0.2D, 0.0D, 0.2D, 0.0D, new int[0]);
         }

         return true;
      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      worldIn.setBlockToAir(pos);
      if (worldIn instanceof WorldServer) {
         ((WorldServer)worldIn).spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 8, 0.2D, 0.0D, 0.2D, 0.0D, new int[0]);
      }

      for(EnumFacing face : EnumFacing.HORIZONTALS) {
         EnumAttachPosition attach = this.getAttachPosition(worldIn, pos, face);
         BlockPos off = pos.offset(face);
         switch(attach) {
         case UP:
            this.lightUp(worldIn, off.up());
            break;
         case SIDE:
            if (!this.lightUp(worldIn, off)) {
               this.lightUp(worldIn, off.down());
            }
         }
      }

   }

   protected boolean canConnectTo(IBlockState blockState, EnumFacing side, IBlockAccess world, BlockPos pos) {
      Block block = blockState.getBlock();
      return block == this || block == Blocks.TNT || block instanceof IFuseIgnitable;
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LIT, Boolean.valueOf(meta != 0));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(LIT)).booleanValue() ? 1 : 0;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, SOUTH, WEST, LIT});
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (((Boolean)stateIn.getValue(LIT)).booleanValue()) {
         float x = (float)pos.getX() + 0.2F + rand.nextFloat() * 0.6F;
         float y = (float)pos.getY() + 0.2F;
         float z = (float)pos.getZ() + 0.2F + rand.nextFloat() * 0.6F;
         worldIn.spawnParticle(EnumParticleTypes.FLAME, (double)x, (double)y, (double)z, 0.0D, 0.0D, 0.0D, new int[0]);
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)x, (double)y, (double)z, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.GUNPOWDER;
   }

   public int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint) {
      return ((Boolean)state.getValue(LIT)).booleanValue() ? 16739328 : 5592405;
   }
}
