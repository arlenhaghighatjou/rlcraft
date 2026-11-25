package vazkii.quark.decoration.block;

import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.feature.TallowAndCandles;

public class BlockCandle extends BlockMetaVariants<BlockCandle.Variants> implements IQuarkBlock {
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D);

   public BlockCandle() {
      super("candle", Material.CLAY, BlockCandle.Variants.class);
      this.setHardness(0.2F);
      this.setLightLevel(0.9375F);
      this.setLightOpacity(0);
      this.setSoundType(SoundType.CLOTH);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote && TallowAndCandles.candlesFall) {
         this.checkFallable(worldIn, pos);
      }

   }

   public float getEnchantPowerBonus(World world, BlockPos pos) {
      return TallowAndCandles.enchantPower;
   }

   private void checkFallable(World worldIn, BlockPos pos) {
      if ((worldIn.isAirBlock(pos.down()) || BlockFalling.canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0) {
         if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
            if (!worldIn.isRemote) {
               EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
               worldIn.spawnEntity(entityfallingblock);
            }
         } else {
            IBlockState state = worldIn.getBlockState(pos);
            worldIn.setBlockToAir(pos);

            BlockPos blockpos;
            for(blockpos = pos.down(); (worldIn.isAirBlock(blockpos) || BlockFalling.canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {
               ;
            }

            if (blockpos.getY() > 0) {
               worldIn.setBlockState(blockpos.up(), state);
            }
         }
      }

   }

   public int tickRate(World worldIn) {
      return 2;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
      return AABB;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isFullBlock(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.7D;
      double d2 = (double)pos.getZ() + 0.5D;
      worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
      worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
   }

   public static enum Variants implements IStringSerializable {
      CANDLE_WHITE(EnumDyeColor.WHITE),
      CANDLE_ORANGE(EnumDyeColor.ORANGE),
      CANDLE_MAGENTA(EnumDyeColor.MAGENTA),
      CANDLE_LIGHT_BLUE(EnumDyeColor.LIGHT_BLUE),
      CANDLE_YELLOW(EnumDyeColor.YELLOW),
      CANDLE_LIME(EnumDyeColor.LIME),
      CANDLE_PINK(EnumDyeColor.PINK),
      CANDLE_GRAY(EnumDyeColor.GRAY),
      CANDLE_SILVER(EnumDyeColor.SILVER),
      CANDLE_CYAN(EnumDyeColor.CYAN),
      CANDLE_PURPLE(EnumDyeColor.PURPLE),
      CANDLE_BLUE(EnumDyeColor.BLUE),
      CANDLE_BROWN(EnumDyeColor.BROWN),
      CANDLE_GREEN(EnumDyeColor.GREEN),
      CANDLE_RED(EnumDyeColor.RED),
      CANDLE_BLACK(EnumDyeColor.BLACK);

      public final EnumDyeColor color;

      private Variants(EnumDyeColor color) {
         this.color = color;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
