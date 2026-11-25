package lykrast.defiledlands.common.block;

import java.util.Random;
import lykrast.defiledlands.common.init.ModBlocks;
import lykrast.defiledlands.common.init.ModItems;
import lykrast.defiledlands.common.util.PlantUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlockBlastem extends BlockBush implements IGrowable, ICustomItemBlock {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

   public BlockBlastem() {
      super(Material.PLANTS);
      this.setHardness(0.1F);
      this.setResistance(0.5F);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
      this.setTickRandomly(true);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return super.getBoundingBox(state, source, pos).offset(state.getOffset(source, pos));
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      int age = ((Integer)state.getValue(AGE)).intValue();
      if (age == 15 && PlantUtils.vulnerableToBlastem(entityIn)) {
         worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 2);
         if (!worldIn.isRemote) {
            boolean flag = worldIn.getGameRules().getBoolean("mobGriefing");
            worldIn.createExplosion((Entity)null, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 1.0F, flag);
         }
      }

   }

   public boolean canDropFromExplosion(Explosion explosionIn) {
      return false;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int j = ((Integer)state.getValue(AGE)).intValue();
      if (j < 15 && ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
         worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(j + 1)), 2);
         ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
      }

      super.updateTick(worldIn, pos, state, rand);
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return ModItems.blastemSeed;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(ModItems.blastemSeed);
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      super.getDrops(drops, world, pos, state, fortune);
      if (((Integer)state.getValue(AGE)).intValue() == 15) {
         drops.add(new ItemStack(ModItems.blastemFruit, 1));
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      int age = ((Integer)state.getValue(AGE)).intValue();
      if (age < 15) {
         return false;
      } else {
         worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(10)), 2);
         if (!worldIn.isRemote) {
            EntityItem fruit = new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, new ItemStack(ModItems.blastemFruit));
            worldIn.spawnEntity(fruit);
         }

         return true;
      }
   }

   protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }

   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos.down());
      Block block = state.getBlock();
      return block == ModBlocks.dirtDefiled || block == ModBlocks.grassDefiled;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return this.canPlaceBlockAt(worldIn, pos);
   }

   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XZ;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE});
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return ((Integer)state.getValue(AGE)).intValue() < 15;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      int age = Math.min(15, ((Integer)state.getValue(AGE)).intValue() + MathHelper.getInt(rand, 2, 5));
      worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(age)), 2);
   }

   public ItemBlock getItemBlock() {
      return null;
   }
}
