package prospector.traverse.blocks.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public class BlockTraverseLeaves extends BlockLeaves {
   public Item sapling;
   public int saplingDropChance;

   public BlockTraverseLeaves(String name, Item sapling, int saplingDropChance) {
      this.sapling = sapling;
      this.saplingDropChance = saplingDropChance;
      this.setRegistryName(new ResourceLocation("traverse", name + "_leaves"));
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setCreativeTab(TraverseTab.TAB);
      this.setDefaultState(this.getDefaultState().withProperty(CHECK_DECAY, Boolean.valueOf(true)).withProperty(DECAYABLE, Boolean.valueOf(true)));
      Blocks.FIRE.setFireInfo(this, 30, 60);
      ShootingStar.registerModel(new ModelCompound("traverse", this, "leaves", new IProperty[]{CHECK_DECAY, DECAYABLE}));
   }

   public int quantityDropped(Random random) {
      return random.nextInt(this.saplingDropChance) == 0 ? 1 : 0;
   }

   public EnumType getWoodType(int meta) {
      return null;
   }

   public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      List<ItemStack> list = new ArrayList();
      list.add(new ItemStack(this, 1, 0));
      return list;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return Blocks.LEAVES.getDefaultState().isOpaqueCube();
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return Blocks.LEAVES.getBlockLayer();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{CHECK_DECAY, DECAYABLE});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(DECAYABLE, Boolean.valueOf((meta & 1) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 2) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      int meta = 0;
      if (!((Boolean)state.getValue(DECAYABLE)).booleanValue()) {
         meta |= 1;
      }

      if (((Boolean)state.getValue(CHECK_DECAY)).booleanValue()) {
         meta |= 2;
      }

      return meta;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return this.sapling;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      if (!Minecraft.getMinecraft().gameSettings.fancyGraphics) {
         return !(blockAccess.getBlockState(pos.offset(side)).getBlock() instanceof BlockLeaves);
      } else {
         return true;
      }
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
      return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(DECAYABLE, Boolean.valueOf(false));
   }
}
