package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import noppes.vc.EntityHolyHandGrenade;
import noppes.vc.VCTabs;
import noppes.vc.client.VCSounds;

public class ItemHolyHandGrenade extends ItemBasic {
   public ItemHolyHandGrenade() {
      this.setCreativeTab(VCTabs.WEAPONS);
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldObj, EntityLivingBase entity, int timeLeft) {
      if (!worldObj.isRemote) {
         int i = this.getMaxItemUseDuration(stack) - timeLeft;
         float f = getArrowVelocity(i);
         EntityHolyHandGrenade projectile = new EntityHolyHandGrenade(worldObj, entity, new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
         projectile.damage = 0.0F;
         projectile.isImmuneToFire();
         projectile.setStickInWall(true);
         projectile.setIs3D(true);
         projectile.setRotating(true);
         projectile.setStickInWall(true);
         projectile.setHasGravity(true);
         projectile.setSpeed((int)(8.0F * f) + 5);
         projectile.timeToLive = 34;
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            projectile.canBePickedUp = false;
            if (!player.capabilities.isCreativeMode) {
               this.consumeItem(player, this);
            }
         }

         projectile.shoot(1.0F);
         playSound(entity, VCSounds.GENERIC_SWOSH, 1.0F, 1.0F);
         worldObj.spawnEntity(projectile);
      }
   }

   public static float getArrowVelocity(int charge) {
      float f = (float)charge / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }
}
