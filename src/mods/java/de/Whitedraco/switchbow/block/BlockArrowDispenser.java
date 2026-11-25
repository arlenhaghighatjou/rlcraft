package de.Whitedraco.switchbow.block;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.entity.EntityArrowLauncher;
import de.Whitedraco.switchbow.tileentity.TileEntityArrowDispenser;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockSourceImpl;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryDefaulted;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockArrowDispenser extends BlockContainer {
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
   public static final RegistryDefaulted<Item, IBehaviorDispenseItem> DISPENSE_BEHAVIOR_REGISTRY = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
   private Random rand = new Random();

   public BlockArrowDispenser(String Name) {
      super(Material.WOOD);
      this.setRegistryName(Name);
      this.setUnlocalizedName(Name);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(Initial.tabSwitchbow);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, Boolean.valueOf(false)));
   }

   public int tickRate(World worldIn) {
      return 4;
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

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.setDefaultFacing(worldIn, pos, state);
   }

   private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         IBlockState iblockstate = worldIn.getBlockState(pos.north());
         IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
         IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
         IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
         EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
         if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
            enumfacing = EnumFacing.SOUTH;
         } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
            enumfacing = EnumFacing.NORTH;
         } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
            enumfacing = EnumFacing.EAST;
         } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
            enumfacing = EnumFacing.WEST;
         }

         worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing.getOpposite()).withProperty(TRIGGERED, Boolean.valueOf(false)), 2);
      }

   }

   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
      TileEntity te = worldIn.getTileEntity(pos);
      if (te != null && te instanceof TileEntityArrowDispenser) {
         TileEntityArrowDispenser tet = (TileEntityArrowDispenser)te;
         tet.setCount(9);
         tet.setShootangle(45.5F);
         tet.setRange(0.8F);
         tet.setCanshoot(false);
         worldIn.notifyBlockUpdate(pos, state.withProperty(FACING, placer.getHorizontalFacing()), state.withProperty(FACING, placer.getHorizontalFacing()), 3);
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumfacing = EnumFacing.getFront(meta);
      if (enumfacing.getAxis() == Axis.Y) {
         enumfacing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, enumfacing);
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, TRIGGERED});
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityArrowDispenser();
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else if (playerIn.getHeldItemMainhand() != ItemStack.EMPTY && playerIn.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.REDSTONE_TORCH)) {
         this.dispense(worldIn, pos);
         return true;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityArrowDispenser) {
            playerIn.openGui(Initial.instance, 2, worldIn, pos.getX(), pos.getY(), pos.getZ());
         }

         return true;
      }
   }

   private boolean shooting(World worldIn, BlockPos pos, TileEntityArrowDispenser TileEntityArrowDispenser) {
      int i = TileEntityArrowDispenser.getDispenseSlot();
      if (i < 0) {
         worldIn.playEvent(1001, pos, 0);
         return false;
      } else {
         ItemStack itemstack = TileEntityArrowDispenser.getStackInSlot(i);
         if (!(itemstack.getItem() instanceof ItemArrow)) {
            return true;
         } else {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            IBlockState block = worldIn.getBlockState(pos);
            EnumFacing facing = null;
            if (block.getBlock() instanceof BlockArrowDispenser) {
               facing = ((EnumFacing)block.getValue(FACING)).getOpposite();
            }

            float shootangle = TileEntityArrowDispenser.getShootangle();
            float range = TileEntityArrowDispenser.getRange();
            float k = 0.0F;
            float j = 0.0F;
            double posx = 0.0D;
            double posz = 0.0D;
            if (facing == EnumFacing.SOUTH) {
               k = 180.0F;
               posx = (double)x + 0.5D;
               posz = (double)z - 0.1D;
            }

            if (facing == EnumFacing.WEST) {
               k = -90.0F;
               posx = (double)x + 1.1D;
               posz = (double)z + 0.5D;
            }

            if (facing == EnumFacing.NORTH) {
               k = 0.0F;
               posx = (double)x + 0.5D;
               posz = (double)z + 1.1D;
            }

            if (facing == EnumFacing.EAST) {
               k = 90.0F;
               posx = (double)x - 0.1D;
               posz = (double)z + 0.5D;
            }

            double posy = (double)y + 0.8D;
            float ran = 0.0F;
            float ran2 = 0.0F;
            float ran3 = 0.0F;
            boolean Rechnung1 = this.rand.nextBoolean();
            ran = (float)this.rand.nextInt(10) + this.rand.nextFloat();
            if (Rechnung1) {
               ran += k;
            } else if (!Rechnung1) {
               ran = k - ran;
            }

            boolean Rechnung2 = this.rand.nextBoolean();
            ran2 = (float)this.rand.nextInt(5) + this.rand.nextFloat();
            if (Rechnung2) {
               ran2 += shootangle;
            } else if (!Rechnung2) {
               ran2 = shootangle - ran2;
            }

            boolean Rechnung3 = this.rand.nextBoolean();
            float Anzahl = (float)(this.rand.nextInt(100) + 1) / 1000.0F;
            if (Rechnung3) {
               ran3 = range + Anzahl;
            } else if (!Rechnung3) {
               ran3 = range - Anzahl;
            }

            ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
            EntityArrowLauncher fakeCannon = new EntityArrowLauncher(worldIn);
            fakeCannon.setPosition(posx, posy, posz);
            EntityArrow arrow = itemarrow.createArrow(worldIn, itemstack, fakeCannon);
            arrow.setPosition(posx, posy, posz);
            arrow.shoot(fakeCannon, arrow.rotationPitch, arrow.rotationYaw, 0.0F, ran3 * 1.5F, 1.0F);
            arrow.setLocationAndAngles(posx, posy, posz, ran, -ran2);
            arrow.motionX = (double)(-MathHelper.sin(arrow.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(arrow.rotationPitch / 180.0F * 3.1415927F));
            arrow.motionZ = (double)(MathHelper.cos(arrow.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(arrow.rotationPitch / 180.0F * 3.1415927F));
            arrow.motionY = (double)(-MathHelper.sin(arrow.rotationPitch / 180.0F * 3.1415927F));
            arrow.shoot(arrow.motionX, arrow.motionY, arrow.motionZ, ran3 * 1.5F, 1.0F);
            arrow.setIsCritical(true);
            arrow.pickupStatus = PickupStatus.ALLOWED;
            arrow.shootingEntity = null;
            worldIn.spawnEntity(arrow);
            itemstack.shrink(1);
            TileEntityArrowDispenser.setInventorySlotContents(i, itemstack.getCount() <= 0 ? ItemStack.EMPTY : itemstack);
            return true;
         }
      }
   }

   protected void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
      TileEntityArrowDispenser TileEntityArrowLancher = (TileEntityArrowDispenser)blocksourceimpl.getBlockTileEntity();
      if (TileEntityArrowLancher != null) {
         for(int a = 0; a < TileEntityArrowLancher.getCount() && this.shooting(worldIn, pos, TileEntityArrowLancher); ++a) {
            ;
         }
      }

   }

   protected IBehaviorDispenseItem getBehavior(@Nullable ItemStack stack) {
      return (IBehaviorDispenseItem)DISPENSE_BEHAVIOR_REGISTRY.getObject(stack == ItemStack.EMPTY ? null : stack.getItem());
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
      boolean flag1 = ((TileEntityArrowDispenser)worldIn.getTileEntity(pos)).isCanshoot();
      if (flag && !flag1) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
         ((TileEntityArrowDispenser)worldIn.getTileEntity(pos)).setCanshoot(true);
      } else if (!flag && flag1) {
         ((TileEntityArrowDispenser)worldIn.getTileEntity(pos)).setCanshoot(false);
      }

   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         this.dispense(worldIn, pos);
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityArrowDispenser) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityArrowDispenser)tileentity);
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
