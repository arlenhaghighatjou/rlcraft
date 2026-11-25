package melonslise.locks.common.item;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.CapabilityProvider;
import melonslise.locks.common.capability.KeyRingInventory;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeyRingItem extends Item {
   public final int rows;

   public KeyRingItem(int rows) {
      this.setMaxStackSize(1);
      this.rows = rows;
      this.addPropertyOverride(new ResourceLocation("locks", "keys"), (stack, world, entity) -> {
         IItemHandler inv = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
         int keys = 0;

         for(int a = 0; a < inv.getSlots(); ++a) {
            if (!inv.getStackInSlot(a).isEmpty()) {
               ++keys;
            }
         }

         return (float)keys / (float)inv.getSlots();
      });
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
      return new CapabilityProvider(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new KeyRingInventory(stack, this.rows, 9));
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      if (!player.world.isRemote) {
         player.openGui(Locks.instance, 0, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
      }

      return new ActionResult(EnumActionResult.PASS, player.getHeldItem(hand));
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float x, float y, float z) {
      IItemHandler inv = (IItemHandler)player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      List<Lockable> intersecting = (List)LocksUtil.intersecting(world, pos).collect(Collectors.toList());
      if (intersecting.isEmpty()) {
         return EnumActionResult.PASS;
      } else {
         for(int a = 0; a < inv.getSlots(); ++a) {
            int id = LockingItem.getOrSetId(inv.getStackInSlot(a));
            List<Lockable> matching = (List)intersecting.stream().filter((lockable1) -> {
               return lockable1.lock.id == id;
            }).collect(Collectors.toList());
            if (!matching.isEmpty()) {
               world.playSound(player, pos, LocksSoundEvents.LOCK_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
               if (world.isRemote) {
                  return EnumActionResult.SUCCESS;
               }

               for(Lockable lockable : matching) {
                  lockable.lock.setLocked(!lockable.lock.isLocked());
               }

               return EnumActionResult.SUCCESS;
            }
         }

         return EnumActionResult.SUCCESS;
      }
   }

   public static boolean containsId(ItemStack stack, int id) {
      IItemHandler inv = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);

      for(int a = 0; a < inv.getSlots(); ++a) {
         if (LockingItem.getOrSetId(inv.getStackInSlot(a)) == id) {
            return true;
         }
      }

      return false;
   }
}
