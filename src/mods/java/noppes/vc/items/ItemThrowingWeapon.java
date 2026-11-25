package noppes.vc.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import noppes.vc.EntityProjectile;
import noppes.vc.VCTabs;
import noppes.vc.client.VCSounds;

public class ItemThrowingWeapon extends ItemBasic {
   private boolean rotating = false;
   private int damage = 2;
   private boolean dropItem = false;

   public ItemThrowingWeapon() {
      this.setCreativeTab(VCTabs.WEAPONS);
      this.setFull3D();
   }

   public void onPlayerStoppedUsing(ItemStack par1ItemStack, World worldObj, EntityLivingBase entity, int par4) {
      if (!worldObj.isRemote) {
         EntityProjectile projectile = new EntityProjectile(worldObj, entity, new ItemStack(par1ItemStack.getItem(), 1, par1ItemStack.getItemDamage()));
         projectile.damage = (float)this.damage;
         projectile.setRotating(this.rotating);
         projectile.setIs3D(true);
         projectile.setStickInWall(true);
         projectile.setHasGravity(true);
         projectile.setSpeed(12);
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            projectile.canBePickedUp = !player.capabilities.isCreativeMode && this.dropItem;
            if (!player.capabilities.isCreativeMode) {
               this.consumeItem(player, this);
            }
         }

         projectile.shoot(1.0F);
         playSound(entity, VCSounds.GENERIC_SWOSH, 1.0F, 1.0F);
         worldObj.spawnEntity(projectile);
      }
   }

   public ItemThrowingWeapon setRotating() {
      this.rotating = true;
      return this;
   }

   public ItemThrowingWeapon setDamage(int damage) {
      this.damage = damage;
      return this;
   }

   public ItemThrowingWeapon setDropItem() {
      this.dropItem = true;
      return this;
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
