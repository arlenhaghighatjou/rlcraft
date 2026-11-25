package melonslise.locks.common.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.capability.ISelection;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.util.Cuboid6i;
import melonslise.locks.common.util.Lock;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import melonslise.locks.common.util.Orientation;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockItem extends LockingItem {
   public final int length;
   public final int enchantmentValue;
   public final float resistance;
   public static final String KEY_LENGTH = "Length";
   public static final String KEY_OPEN = "Open";

   public LockItem(int length, int enchVal, float resist) {
      this.length = length;
      this.enchantmentValue = enchVal;
      this.resistance = resist;
      this.addPropertyOverride(new ResourceLocation("locks:open"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World world, EntityLivingBase entity) {
            if (stack.getItem() instanceof LockItem) {
               return LockItem.isOpen(stack) ? 1.0F : 0.0F;
            } else {
               return 0.0F;
            }
         }
      });
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public int getItemEnchantability() {
      return this.enchantmentValue;
   }

   public static boolean isOpen(ItemStack stack) {
      NBTTagCompound nbt = LocksUtil.getTag(stack);
      if (!nbt.hasKey("Open")) {
         nbt.setBoolean("Open", false);
      }

      return nbt.getBoolean("Open");
   }

   public static void setOpen(ItemStack stack, boolean open) {
      LocksUtil.getTag(stack).setBoolean("Open", open);
   }

   public static byte getLength(ItemStack stack) {
      NBTTagCompound nbt = LocksUtil.getTag(stack);
      return !nbt.hasKey("Length") ? (byte)((LockItem)stack.getItem()).length : nbt.getByte("Length");
   }

   public static float getResistance(ItemStack stack) {
      return ((LockItem)stack.getItem()).resistance;
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
      ILockableHandler lockables = (ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
      if (LocksConfig.getServer(world).canLock(world, pos) && !lockables.getInChunk(pos).values().stream().anyMatch((lockable1) -> {
         return lockable1.box.intersects(pos);
      })) {
         return LocksConfig.getServer(world).easyLock ? this.easyLock(lockables, player, world, pos, hand, face, hitX, hitY, hitZ) : this.freeLock(lockables, player, world, pos, hand, face, hitX, hitY, hitZ);
      } else {
         return EnumActionResult.PASS;
      }
   }

   public EnumActionResult freeLock(ILockableHandler lockables, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
      ISelection select = (ISelection)player.getCapability(LocksCapabilities.SELECTION, (EnumFacing)null);
      ItemStack stack = player.getHeldItem(hand);
      BlockPos pos1 = select.get();
      if (pos1 == null) {
         select.set(pos);
      } else {
         select.set((BlockPos)null);
         world.playSound(player, pos, LocksSoundEvents.LOCK_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
         if (world.isRemote) {
            return EnumActionResult.SUCCESS;
         }

         ItemStack lockStack = stack.copy();
         lockStack.setCount(1);
         if (!lockables.add(new Lockable(new Cuboid6i(pos1, pos), Lock.from(lockStack), Orientation.fromDirection(face, player.getHorizontalFacing().getOpposite()), lockStack, world))) {
            return EnumActionResult.PASS;
         }

         if (!player.isCreative()) {
            stack.shrink(1);
         }
      }

      return EnumActionResult.SUCCESS;
   }

   public EnumActionResult easyLock(ILockableHandler lockables, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
      world.playSound(player, pos, LocksSoundEvents.LOCK_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F);
      if (world.isRemote) {
         return EnumActionResult.SUCCESS;
      } else {
         IBlockState state = world.getBlockState(pos);
         BlockPos pos1 = pos;
         TileEntity te = world.getTileEntity(pos);
         Orientation placedOrientation = Orientation.fromDirection(face, player.getHorizontalFacing().getOpposite());
         if (te instanceof TileEntityChest) {
            BlockPos adjPos = LocksUtil.getAdjacentChest((TileEntityChest)te);
            if (adjPos != null) {
               pos1 = adjPos;
            }

            if (LocksConfig.COMMON.automaticallyOrientPlacedLocks && state.getBlock() instanceof BlockChest) {
               placedOrientation = Orientation.fromDirection((EnumFacing)world.getBlockState(pos).getValue(BlockChest.FACING), EnumFacing.NORTH);
            }
         } else if (state.getBlock() instanceof BlockDoor) {
            EnumDoorHalf clickedDoorHalf = (EnumDoorHalf)state.getValue(BlockDoor.HALF);
            EnumHingePosition clickedDoorHinge = (EnumHingePosition)state.getValue(BlockDoor.HINGE);
            EnumFacing clickedDoorFacing = (EnumFacing)state.getValue(BlockDoor.FACING);
            boolean isOpen = ((Boolean)state.getValue(BlockDoor.OPEN)).booleanValue();
            if (clickedDoorHalf == EnumDoorHalf.LOWER) {
               pos1 = pos.up();
            } else {
               pos1 = pos.down();
            }

            IBlockState otherHalfState = world.getBlockState(pos1);
            if (clickedDoorHalf == EnumDoorHalf.UPPER && otherHalfState.getBlock() instanceof BlockDoor) {
               isOpen = ((Boolean)otherHalfState.getValue(BlockDoor.OPEN)).booleanValue();
               clickedDoorFacing = (EnumFacing)otherHalfState.getValue(BlockDoor.FACING);
            }

            if (clickedDoorHalf == EnumDoorHalf.LOWER && otherHalfState.getBlock() instanceof BlockDoor) {
               clickedDoorHinge = (EnumHingePosition)otherHalfState.getValue(BlockDoor.HINGE);
            }

            if (otherHalfState.getBlock() instanceof BlockDoor && otherHalfState.getValue(BlockDoor.HALF) != clickedDoorHalf) {
               if (!isOpen) {
                  int xAdd = 0;
                  int zAdd = 0;
                  switch(clickedDoorFacing) {
                  case EAST:
                  default:
                     zAdd = clickedDoorHinge == EnumHingePosition.RIGHT ? -1 : 1;
                     break;
                  case NORTH:
                     xAdd = clickedDoorHinge == EnumHingePosition.RIGHT ? -1 : 1;
                     break;
                  case SOUTH:
                     xAdd = clickedDoorHinge == EnumHingePosition.RIGHT ? 1 : -1;
                     break;
                  case WEST:
                     zAdd = clickedDoorHinge == EnumHingePosition.RIGHT ? 1 : -1;
                  }

                  IBlockState otherTop = world.getBlockState((clickedDoorHalf == EnumDoorHalf.LOWER ? pos1 : pos).add(xAdd, 0, zAdd));
                  IBlockState otherBottom = world.getBlockState((clickedDoorHalf == EnumDoorHalf.LOWER ? pos : pos1).add(xAdd, 0, zAdd));
                  if (otherTop.getBlock() instanceof BlockDoor && otherBottom.getBlock() instanceof BlockDoor && !((Boolean)otherBottom.getValue(BlockDoor.OPEN)).booleanValue() && otherTop.getValue(BlockDoor.HINGE) == (clickedDoorHinge == EnumHingePosition.RIGHT ? EnumHingePosition.LEFT : EnumHingePosition.RIGHT) && otherBottom.getValue(BlockDoor.FACING) == clickedDoorFacing && otherTop.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER && otherBottom.getValue(BlockDoor.HALF) == EnumDoorHalf.LOWER) {
                     pos1 = pos1.add(xAdd, 0, zAdd);
                  }
               }
            } else {
               pos1 = pos;
            }

            if (LocksConfig.COMMON.automaticallyOrientPlacedLocks) {
               if (isOpen) {
                  EnumFacing curfacing = clickedDoorFacing;
                  switch(clickedDoorFacing) {
                  case EAST:
                  case WEST:
                     curfacing = EnumFacing.SOUTH;
                     break;
                  case NORTH:
                  case SOUTH:
                     curfacing = EnumFacing.WEST;
                  }

                  placedOrientation = Orientation.fromDirection(curfacing.getOpposite(), EnumFacing.NORTH);
               } else {
                  placedOrientation = Orientation.fromDirection(clickedDoorFacing.getOpposite(), EnumFacing.NORTH);
               }
            }
         }

         ItemStack stack = player.getHeldItem(hand);
         ItemStack lockStack = stack.copy();
         lockStack.setCount(1);
         if (!lockables.add(new Lockable(new Cuboid6i(pos1, pos), Lock.from(lockStack), placedOrientation, lockStack, world))) {
            return EnumActionResult.PASS;
         } else {
            if (!player.isCreative()) {
               stack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
         }
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (!isOpen(stack)) {
         return super.onItemRightClick(world, player, hand);
      } else {
         setOpen(stack, false);
         world.playSound(player, player.posX, player.posY, player.posZ, LocksSoundEvents.PIN_MATCH, SoundCategory.PLAYERS, 1.0F, 1.0F);
         return super.onItemRightClick(world, player, hand);
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World world, List<String> lines, ITooltipFlag flag) {
      super.addInformation(stack, world, lines, flag);
      int length = LocksUtil.hasKey(stack, "Length") ? stack.getTagCompound().getByte("Length") : this.length;
      ITextComponent txt = new TextComponentTranslation("locks.tooltip.length", new Object[]{ItemStack.DECIMALFORMAT.format((long)length)});
      txt.getStyle().setColor(TextFormatting.DARK_GREEN);
      lines.add(txt.getFormattedText());
      float resist = (float)((int)this.resistance);
      String resistString = ".tooltip.resistance.weak";
      if (resist >= 10.0F) {
         resistString = ".tooltip.resistance.strong";
      }

      if (resist >= 40.0F) {
         resistString = ".tooltip.resistance.supreme";
      }

      ITextComponent txt2 = new TextComponentTranslation("locks" + resistString, new Object[0]);
      txt2.getStyle().setColor(TextFormatting.DARK_GREEN);
      lines.add(txt2.getFormattedText());
   }
}
