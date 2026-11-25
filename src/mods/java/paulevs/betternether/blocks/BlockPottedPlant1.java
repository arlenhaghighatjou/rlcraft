package paulevs.betternether.blocks;

import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPottedPlant1 extends Block {
   public static final PropertyEnum<BlockPottedPlant1.EnumPlant> PLANT = PropertyEnum.create("plant", BlockPottedPlant1.EnumPlant.class);
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, -0.5D, 0.125D, 0.875D, 0.875D, 0.875D);
   private static Map<String, BlockPottedPlant1.EnumPlant> plants;

   public BlockPottedPlant1() {
      super(Material.PLANTS);
      this.setHardness(0.5F);
      this.setResistance(0.5F);
      this.setSoundType(SoundType.PLANT);
      this.setUnlocalizedName("potted_plant_1");
      this.setRegistryName("potted_plant_1");
      plants = new HashMap();

      for(BlockPottedPlant1.EnumPlant plant : BlockPottedPlant1.EnumPlant.values()) {
         if (plant.block != null) {
            plants.put(plant.block.getUnlocalizedName(), plant);
         }
      }

   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public static boolean hasPlant(Block block) {
      return block != null && plants != null && plants.containsKey(block.getUnlocalizedName());
   }

   public IBlockState getBlockState(Block plant) {
      return this.getDefaultState().withProperty(PLANT, (Comparable)plants.get(plant.getUnlocalizedName()));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{PLANT});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(PLANT, BlockPottedPlant1.EnumPlant.values()[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockPottedPlant1.EnumPlant)state.getValue(PLANT)).getIndex();
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockCincinnasitePot)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(((BlockPottedPlant1.EnumPlant)state.getValue(PLANT)).block);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(((BlockPottedPlant1.EnumPlant)state.getValue(PLANT)).block);
   }

   public static enum EnumPlant implements IStringSerializable {
      AGAVE(BlocksRegister.BLOCK_AGAVE),
      BARREL_CACTUS(BlocksRegister.BLOCK_BARREL_CACTUS),
      BLACK_APPLE(BlocksRegister.BLOCK_BLACK_APPLE_SEED),
      BLACK_BUSH(BlocksRegister.BLOCK_BLACK_BUSH),
      EGG_PLANT(BlocksRegister.BLOCK_EGG_PLANT),
      INK_BUSH(BlocksRegister.BLOCK_INK_BUSH_SEED),
      REEDS(BlocksRegister.BLOCK_NETHER_REED),
      NETHER_CACTUS(BlocksRegister.BLOCK_NETHER_CACTUS),
      NETHER_GRASS(BlocksRegister.BLOCK_NETHER_GRASS),
      ORANGE_MUSHROOM(BlocksRegister.BLOCK_ORANGE_MUSHROOM),
      RED_MOLD(BlocksRegister.BLOCK_RED_MOLD),
      GRAY_MOLD(BlocksRegister.BLOCK_GRAY_MOLD),
      MAGMA_FLOWER(BlocksRegister.BLOCK_MAGMA_FLOWER),
      NETHER_WART(BlocksRegister.BLOCK_WART_SEED);

      private final String name = Integer.toString(this.ordinal());
      private final int index;
      private Block block;

      private EnumPlant(Block block) {
         if (block != null) {
            this.block = block;
         } else {
            this.block = Blocks.AIR;
         }

         this.index = this.ordinal();
      }

      public String toString() {
         return this.name;
      }

      public String getName() {
         return this.name;
      }

      public int getIndex() {
         return this.index;
      }
   }
}
