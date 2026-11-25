package de.Whitedraco.switchbow.block;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.tileentity.TileEntityPodest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPodest extends BlockContainer {
   protected static final AxisAlignedBB AABB_BLOCK = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D);

   public BlockPodest(String Name) {
      super(Material.ROCK);
      this.setRegistryName(Name);
      this.setHardness(7.5F);
      this.setUnlocalizedName(Name);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(Initial.tabSwitchbow);
      this.setLightLevel(3.0F);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB_BLOCK;
   }

   public boolean isFullBlock(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         if (playerIn.getHeldItemMainhand() != ItemStack.EMPTY && playerIn.getHeldItemMainhand().getItem() instanceof ItemArrow) {
            ItemStack stackArrow = playerIn.getHeldItemMainhand();
            if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityPodest) {
               TileEntityPodest tePodest = (TileEntityPodest)worldIn.getTileEntity(pos);
               if (tePodest.getItemTransformingOutput(stackArrow.getItem()) != null && tePodest.getStackInSlot(0) == ItemStack.EMPTY) {
                  playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                  tePodest.setInventorySlotContents(0, stackArrow);
                  tePodest.setStartTransformation(true);
                  return true;
               }
            }
         } else if (playerIn.getHeldItemMainhand() == ItemStack.EMPTY && worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityPodest) {
            TileEntityPodest tePodest = (TileEntityPodest)worldIn.getTileEntity(pos);
            if (tePodest.getStackInSlot(0) != ItemStack.EMPTY) {
               playerIn.setHeldItem(EnumHand.MAIN_HAND, tePodest.getStackInSlot(0));
               tePodest.setInventorySlotContents(0, ItemStack.EMPTY);
               return true;
            }
         }

         return true;
      }
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityPodest();
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityPodest) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityPodest)tileentity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }
}
