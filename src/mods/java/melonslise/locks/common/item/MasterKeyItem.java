package melonslise.locks.common.item;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import melonslise.locks.Locks;
import melonslise.locks.common.capability.ILockableHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksSoundEvents;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class MasterKeyItem extends Item {
   public MasterKeyItem() {
      this.setMaxStackSize(1);
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing face, float hitX, float hitY, float hitZ) {
      if (Locks.debug && player.isSneaking()) {
         if (world.isRemote) {
            ObjectIterator matching = ((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null)).getLoaded().entrySet().iterator();

            while(matching.hasNext()) {
               Entry<Integer, Lockable> entry = (Entry)matching.next();
               String message = "C:" + entry.getKey() + ":" + ((Lockable)entry.getValue()).toString();
               player.sendMessage(new TextComponentString(message));
            }
         } else {
            ObjectIterator var12 = ((ILockableHandler)world.getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null)).getLoaded().entrySet().iterator();

            while(var12.hasNext()) {
               Entry<Integer, Lockable> entry = (Entry)var12.next();
               String message = "S:" + entry.getKey() + ":" + ((Lockable)entry.getValue()).toString();
               player.sendMessage(new TextComponentString(message));
            }
         }
      }

      List<Lockable> matching = (List)LocksUtil.intersecting(world, pos).collect(Collectors.toList());
      if (matching.isEmpty()) {
         return EnumActionResult.PASS;
      } else {
         world.playSound(player, pos, LocksSoundEvents.LOCK_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
         if (world.isRemote) {
            return EnumActionResult.SUCCESS;
         } else {
            for(Lockable lockable : matching) {
               lockable.lock.setLocked(!lockable.lock.isLocked());
            }

            return EnumActionResult.SUCCESS;
         }
      }
   }
}
