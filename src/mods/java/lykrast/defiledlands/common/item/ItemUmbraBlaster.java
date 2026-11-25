package lykrast.defiledlands.common.item;

import lykrast.defiledlands.common.entity.projectile.EntityBlastemFruit;
import lykrast.defiledlands.common.entity.projectile.EntityBlastemFruitBlazing;
import lykrast.defiledlands.common.init.ModEnchantments;
import lykrast.defiledlands.common.init.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemUmbraBlaster extends ItemGun implements IEnchantDestructive {
   public ItemUmbraBlaster(int durability) {
      super(durability);
   }

   protected boolean isAmmo(ItemStack stack) {
      return stack.getItem() instanceof ItemBlastemFruit;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      boolean flag = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) > 0;
      ItemStack ammo = this.findAmmo(playerIn);
      if (ammo.isEmpty() && !flag) {
         return new ActionResult(EnumActionResult.FAIL, itemstack);
      } else {
         if (ammo.isEmpty()) {
            ammo = new ItemStack(ModItems.blastemFruit);
         }

         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         playerIn.getCooldownTracker().setCooldown(this, 10);
         if (!worldIn.isRemote) {
            EntityBlastemFruit projectile;
            if (ammo.getItem() != ModItems.blastemFruitBlazing && EnchantmentHelper.getEnchantmentLevel(ModEnchantments.blazing, itemstack) <= 0) {
               projectile = new EntityBlastemFruit(worldIn, playerIn);
            } else {
               projectile = new EntityBlastemFruitBlazing(worldIn, playerIn);
            }

            float f = this.getSharpshooterBonus(itemstack);
            projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F * f, 1.0F / f);
            if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.safeguard, itemstack) > 0) {
               projectile.setDestructive(false);
            }

            float j = this.getDestructiveBonus(itemstack);
            projectile.setDamage(projectile.getDamage() * j);
            projectile.setExplosionStrength(projectile.getExplosionStrength() * j);
            worldIn.spawnEntity(projectile);
            itemstack.damageItem(1, playerIn);
         }

         if (!flag) {
            this.consumeAmmo(itemstack, ammo, playerIn, worldIn.rand);
         }

         playerIn.addStat(StatList.getObjectUseStats(this));
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      }
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == ModItems.umbriumIngot;
   }
}
