package rustic.common.blocks.crops;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public abstract class BlockHerbBase extends BlockBush implements IGrowable, IPlantable {
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
   private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D)};

   public BlockHerbBase(String name, boolean hasItemBlock) {
      this.setRegistryName(name);
      this.setUnlocalizedName("rustic." + name);
      GameRegistry.findRegistry(Block.class).register(this);
      if (!hasItemBlock) {
         GameRegistry.findRegistry(Item.class).register((new BlockHerbBase.ItemHerbBase(this)).setRegistryName(this.getRegistryName()));
      }

      this.setHardness(0.0F);
      this.setTickRandomly(true);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(Rustic.alchemyTab);
      this.setDefaultState(this.blockState.getBaseState().withProperty(this.getAgeProperty(), Integer.valueOf(0)));
   }

   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName().toString(), "inventory"));
   }

   public abstract EnumPlantType getPlantType(IBlockAccess var1, BlockPos var2);

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return CROPS_AABB[((Integer)state.getValue(this.getAgeProperty())).intValue()];
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int i = this.getAge(state);
         if (i < this.getMaxAge()) {
            float f = getGrowthChance(this, worldIn, pos);
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
               worldIn.setBlockState(pos, this.withAge(i + 1), 2);
               ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
         }
      }

   }

   protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
      return 4.0F;
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
      int age = this.getAge(state);
      EaglercraftRandom rand = world instanceof World ? ((World)world).rand : new EaglercraftRandom();
      if (age >= this.getMaxAge()) {
         int k = 2 + fortune;

         for(int i = 0; i < k; ++i) {
            if (rand.nextInt(2 * this.getMaxAge()) <= age) {
               ret.add(new ItemStack(this.getHerb()));
            }
         }
      }

      return ret;
   }

   public abstract Item getHerb();

   protected PropertyInteger getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 3;
   }

   protected int getAge(IBlockState state) {
      return ((Integer)state.getValue(this.getAgeProperty())).intValue();
   }

   public IBlockState withAge(int age) {
      return this.getDefaultState().withProperty(this.getAgeProperty(), Integer.valueOf(age));
   }

   public boolean isMaxAge(IBlockState state) {
      return ((Integer)state.getValue(this.getAgeProperty())).intValue() >= this.getMaxAge();
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return !this.isMaxAge(state);
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
      int j = this.getMaxAge();
      if (i > j) {
         i = j;
      }

      worldIn.setBlockState(pos, this.withAge(i), 2);
   }

   protected int getBonemealAgeIncrease(World worldIn) {
      return MathHelper.getInt(worldIn.rand, 2, 5);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.withAge(meta);
   }

   public int getMetaFromState(IBlockState state) {
      return this.getAge(state);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE});
   }

   @SideOnly(Side.CLIENT)
   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XZ;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this.getHerb());
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return this.getHerb();
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }

   public static class ItemHerbBase extends ItemBlock implements IPlantable {
      private final BlockHerbBase herbBlock;

      public ItemHerbBase(BlockHerbBase block) {
         super(block);
         this.herbBlock = block;
      }

      public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
         return this.herbBlock.getPlantType(world, pos);
      }

      public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
         return this.herbBlock.getPlant(world, pos);
      }
   }
}
