package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import noppes.vc.EntityProjectile;
import noppes.vc.client.VCSounds;

public class ItemKunai extends ItemWeaponBasic {
   public ItemKunai(ToolMaterial tool) {
      super(tool);
   }

   public void onPlayerStoppedUsing(ItemStack par1ItemStack, World worldObj, EntityLivingBase entity, int par4) {
      if (!worldObj.isRemote) {
         EntityProjectile projectile = new EntityProjectile(worldObj, entity, par1ItemStack);
         projectile.damage = this.getAttackDamage();
         projectile.destroyedOnEntityHit = false;
         projectile.setIs3D(true);
         projectile.setStickInWall(true);
         projectile.setHasGravity(true);
         projectile.setSpeed(12);
         projectile.shoot(1.0F);
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            projectile.canBePickedUp = !player.capabilities.isCreativeMode;
            if (!player.capabilities.isCreativeMode) {
               par1ItemStack.damageItem(1, player);
               if (par1ItemStack.getCount() == 0) {
                  return;
               }

               player.inventory.mainInventory.set(player.inventory.currentItem, ItemStack.EMPTY);
            }
         }

         ItemBasic.playSound(entity, VCSounds.GENERIC_SWOSH, 1.0F, 1.0F);
         worldObj.spawnEntity(projectile);
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }
}
