package reborncore.common.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class LogicBlock extends BlockContainer {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public static PropertyBool ACTIVE = PropertyBool.create("active");
   @Nonnull
   LogicController logicController;

   public LogicBlock(LogicController logicController) {
      super(Material.IRON);
      this.logicController = logicController;
      this.setUnlocalizedName(logicController.getName());
      this.logicController.initBlock(this);
      this.setHardness(logicController.getHardness());
      if (logicController.hasRotation()) {
         this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, Boolean.valueOf(false)));
      }

   }

   @Nullable
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return this.logicController != null ? this.logicController.createNewTileEntity(worldIn, meta) : null;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return this.logicController.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      super.addInformation(stack, world, tooltip, advanced);
      this.logicController.addInformation(stack, world, tooltip, advanced.isAdvanced());
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      if (this.logicController != null) {
         this.logicController.getRenderType(state);
      }

      return EnumBlockRenderType.MODEL;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return this.logicController != null ? this.logicController.getBoundingBox(state, source, pos) : super.getBoundingBox(state, source, pos);
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.logicController.onBlockAdded(worldIn, pos, state);
      super.onBlockAdded(worldIn, pos, state);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      this.logicController.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      if (this.logicController.hasRotation()) {
         this.setFacing(placer.getHorizontalFacing().getOpposite(), worldIn, pos);
      }

      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.dropInv()) {
         this.dropInventory(worldIn, pos);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean dropInv() {
      return this.logicController.dropInv();
   }

   protected void dropInventory(World world, BlockPos pos) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity != null) {
         if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)) {
            ItemStackHandler inventory = (ItemStackHandler)tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
            List<ItemStack> items = new ArrayList();
            this.addItemsToList(inventory, items);

            for(ItemStack itemStack : items) {
               Random rand = new Random();
               float dX = rand.nextFloat() * 0.8F + 0.1F;
               float dY = rand.nextFloat() * 0.8F + 0.1F;
               float dZ = rand.nextFloat() * 0.8F + 0.1F;
               EntityItem entityItem = new EntityItem(world, (double)((float)pos.getX() + dX), (double)((float)pos.getY() + dY), (double)((float)pos.getZ() + dZ), itemStack.copy());
               if (itemStack.hasTagCompound()) {
                  entityItem.getItem().setTagCompound(itemStack.getTagCompound().copy());
               }

               float factor = 0.05F;
               entityItem.motionX = rand.nextGaussian() * (double)factor;
               entityItem.motionY = rand.nextGaussian() * (double)factor + 0.20000000298023224D;
               entityItem.motionZ = rand.nextGaussian() * (double)factor;
               world.spawnEntity(entityItem);
               itemStack.setCount(0);
            }

         }
      }
   }

   private void addItemsToList(IItemHandler inventory, List<ItemStack> items) {
      for(int i = 0; i < inventory.getSlots(); ++i) {
         ItemStack itemStack = inventory.getStackInSlot(i);
         if (itemStack.isEmpty() || itemStack.getCount() <= 0 || !(itemStack.getItem() instanceof ItemBlock) || !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockFluidBase) && !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockStaticLiquid) && !(((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockDynamicLiquid)) {
            items.add(itemStack.copy());
         }
      }

   }

   protected BlockStateContainer createBlockState() {
      FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
      ACTIVE = PropertyBool.create("active");
      return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE});
   }

   public void setFacing(EnumFacing facing, World world, BlockPos pos) {
      if (this.logicController.hasRotation()) {
         world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, facing));
      }

   }

   public int getMetaFromState(IBlockState state) {
      int facingInt = this.getSideFromEnum((EnumFacing)state.getValue(FACING));
      int activeInt = ((Boolean)state.getValue(ACTIVE)).booleanValue() ? 0 : 4;
      return facingInt + activeInt;
   }

   public int getSideFromEnum(EnumFacing facing) {
      if (facing == EnumFacing.NORTH) {
         return 0;
      } else if (facing == EnumFacing.SOUTH) {
         return 1;
      } else if (facing == EnumFacing.EAST) {
         return 2;
      } else {
         return facing == EnumFacing.WEST ? 3 : 0;
      }
   }
}
