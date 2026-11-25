package noppes.vc.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.CommonUtils;
import noppes.vc.VCBlocks;
import noppes.vc.VariedCommodities;
import noppes.vc.blocks.tiles.TileCampfire;

public class BlockCampfire extends BlockBasicLightable {
   public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

   public BlockCampfire(boolean lit) {
      super(Blocks.COBBLESTONE, lit);
      this.setSoundType(SoundType.STONE);
      if (lit) {
         this.setLightLevel(0.9375F);
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return AABB;
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileCampfire();
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack item = player.inventory.getCurrentItem();
      if (item != null && hand == EnumHand.MAIN_HAND) {
         if ((item.getItem() == Items.FLINT || item.getItem() == Items.FLINT_AND_STEEL) && this.unlitBlock() == this) {
            if (world.rand.nextInt(3) == 0 && !world.isRemote) {
               super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
            }

            VariedCommodities.proxy.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), 0.0D, 0.0D, 0.0D, 2.0F);
            if (item.getItem() == Items.FLINT) {
               CommonUtils.ConsumeItemStack(1, player);
            } else {
               item.damageItem(1, player);
            }

            return true;
         } else {
            if (item.getItem() == Item.getItemFromBlock(Blocks.SAND) && this.litBlock() == this) {
               super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public int maxRotation() {
      return 8;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom rand) {
      if (state.getBlock() == VCBlocks.campfire) {
         if (rand.nextInt(36) == 0) {
            world.playSound((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
         }

         TileCampfire tile = (TileCampfire)world.getTileEntity(pos);
         float xOffset = 0.5F;
         float yOffset = 0.7F;
         float zOffset = 0.5F;
         double d0 = (double)((float)pos.getX() + xOffset);
         double d1 = (double)((float)pos.getY() + yOffset);
         double d2 = (double)((float)pos.getZ() + zOffset);
         GlStateManager.pushMatrix();
         VariedCommodities.proxy.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D, 2.0F);
         VariedCommodities.proxy.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, 4.0F);
         GlStateManager.popMatrix();
      }
   }

   public Block unlitBlock() {
      return VCBlocks.campfire_unlit;
   }

   public Block litBlock() {
      return VCBlocks.campfire;
   }
}
