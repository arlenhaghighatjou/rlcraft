package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.Config;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public abstract class BlockBerryBush extends BlockBase implements IColoredBlock, IPlantable, IGrowable {
   public static final PropertyBool BERRIES = PropertyBool.create("berries");
   protected static final AxisAlignedBB BUSH_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

   public BlockBerryBush(String name) {
      super(Material.PLANTS, name);
      this.setCreativeTab(Rustic.farmingTab);
      this.setSoundType(SoundType.PLANT);
      this.setHardness(0.2F);
      this.setLightOpacity(1);
      this.setTickRandomly(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BERRIES, Boolean.valueOf(false)));
      Blocks.FIRE.setFireInfo(this, 40, 80);
   }

   public abstract Item getBerries();

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      super.updateTick(worldIn, pos, state, rand);
      this.checkAndDropBlock(worldIn, pos, state);
      if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && !((Boolean)state.getValue(BERRIES)).booleanValue()) {
         float f = 2.0F;
         if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(40.0F / f) + 1) == 0)) {
            worldIn.setBlockState(pos, state.withProperty(BERRIES, Boolean.valueOf(true)), 3);
            ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
         }
      }

   }

   protected int numNeighbors(World world, BlockPos pos) {
      int n = 0;

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            if ((i != 0 || j != 0) && world.getBlockState(pos.add(i, 0, j)).getBlock() == this) {
               ++n;
            }
         }
      }

      return n;
   }

   protected void growOutward(World world, BlockPos pos, EaglercraftRandom rand) {
      List<BlockPos> positions = new ArrayList();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            if ((i != 0 || j != 0) && this.canPlaceBlockAt(world, pos.add(i, 0, j))) {
               positions.add(pos.add(i, 0, j));
            }
         }
      }

      if (positions.size() > 0) {
         BlockPos placePos = (BlockPos)positions.get(rand.nextInt(positions.size()));
         world.setBlockState(placePos, this.getDefaultState(), 3);
      }

   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> stacks = super.getDrops(world, pos, state, fortune);
      if (((Boolean)state.getValue(BERRIES)).booleanValue() && this.getBerries() != null) {
         stacks.add(new ItemStack(this.getBerries()));
      }

      return stacks;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack heldItem = player.getHeldItem(hand);
      if (((Boolean)state.getValue(BERRIES)).booleanValue() && (heldItem.isEmpty() || heldItem.getItem() != Items.DYE || heldItem.getMetadata() != 15)) {
         world.setBlockState(pos, state.withProperty(BERRIES, Boolean.valueOf(false)), 3);
         ItemStack stack = new ItemStack(this.getBerries());
         if (!player.addItemStackToInventory(stack)) {
            Block.spawnAsEntity(world, pos.offset(player.getHorizontalFacing().getOpposite()), stack);
         }

         return true;
      } else {
         return false;
      }
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BUSH_AABB;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      entity.motionX *= 0.6000000238418579D;
      entity.motionZ *= 0.6000000238418579D;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return EnumPlantType.Plains;
   }

   public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
      return this.getDefaultState();
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      BlockPos down = pos.down();
      IBlockState soil = worldIn.getBlockState(down);
      if (soil.getMaterial().isLiquid()) {
         return false;
      } else {
         return super.canPlaceBlockAt(worldIn, pos) && soil.getBlock().canSustainPlant(soil, worldIn, down, EnumFacing.UP, this);
      }
   }

   protected boolean canSustainBush(IBlockState state) {
      return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.FARMLAND || state.getBlock() == ModBlocks.FERTILE_SOIL;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
      this.checkAndDropBlock(worldIn, pos, state);
   }

   protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
      }

   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getBlock() == this) {
         IBlockState soil = worldIn.getBlockState(pos.down());
         return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this);
      } else {
         return this.canSustainBush(worldIn.getBlockState(pos.down()));
      }
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(this.getDefaultState().withProperty(BERRIES, Boolean.valueOf(false)));
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BERRIES, Boolean.valueOf(meta > 0));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(BERRIES)).booleanValue() ? 1 : 0;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{BERRIES});
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state) {
      if (((Boolean)state.getValue(BERRIES)).booleanValue()) {
         this.growOutward(worldIn, pos, rand);
      } else {
         worldIn.setBlockState(pos, state.withProperty(BERRIES, Boolean.valueOf(true)), 3);
      }

   }

   @SideOnly(Side.CLIENT)
   public EnumOffsetType getOffsetType() {
      return Config.OFFSET_WILDBERRY_BUSHES ? EnumOffsetType.XZ : EnumOffsetType.NONE;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return new IBlockColor() {
         public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
            return worldIn != null && pos != null && tintIndex == 1 ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
         }
      };
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return new IItemColor() {
         public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (!(stack.getItem() instanceof ItemBlock)) {
               return 16777215;
            } else {
               IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
               IBlockColor blockColor = ((IColoredBlock)state.getBlock()).getBlockColor();
               return blockColor == null ? 16777215 : blockColor.colorMultiplier(state, (IBlockAccess)null, (BlockPos)null, tintIndex);
            }
         }
      };
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      super.initModel();
      ClientProxy.addColoredBlock(this);
   }
}
