package noppes.vc.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemMusic extends ItemBasic {
   private boolean shouldRotate = false;

   public ItemMusic() {
      this.setFull3D();
      this.setMaxStackSize(1);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (world.isRemote) {
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      } else {
         int note = world.rand.nextInt(24);
         float var7 = (float)Math.pow(2.0D, (double)(note - 12) / 12.0D);
         playSound(player, SoundEvents.BLOCK_NOTE_HARP, 3.0F, var7);
         world.spawnParticle(EnumParticleTypes.NOTE, player.posX, player.posY + 1.2D, player.posZ, (double)note / 24.0D, 0.0D, 0.0D, new int[0]);
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      }
   }

   public ItemMusic setRotated() {
      this.shouldRotate = true;
      return this;
   }

   public boolean shouldRotateAroundWhenRendering() {
      return this.shouldRotate;
   }
}
