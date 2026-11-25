package knightminer.inspirations.tweaks.block;

import knightminer.inspirations.tweaks.InspirationsTweaks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

public class BlockCactusCrop extends BlockBlockCrop {
   private static final AxisAlignedBB[] BOUNDS = new AxisAlignedBB[]{new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.125D, 0.5625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.25D, 0.625D), new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D), new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.625D, 0.8125D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D), new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D)};

   public BlockCactusCrop() {
      super(Blocks.CACTUS, EnumPlantType.Desert, BOUNDS);
      this.setHardness(0.4F);
      this.setSoundType(SoundType.CLOTH);
      this.setUnlocalizedName("cactus");
   }

   public Item getSeed() {
      return InspirationsTweaks.cactusSeeds;
   }

   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      return Blocks.CACTUS.canBlockStay(world, pos);
   }

   /** @deprecated */
   @Deprecated
   public Material getMaterial(IBlockState state) {
      return Material.CACTUS;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return BOUNDS[this.getAge(state)];
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      entity.attackEntityFrom(DamageSource.CACTUS, 1.0F);
   }
}
