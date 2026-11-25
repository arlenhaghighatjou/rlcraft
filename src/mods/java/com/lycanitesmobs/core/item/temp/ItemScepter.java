package com.lycanitesmobs.core.item.temp;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemScepter extends ItemBase {
   protected float damageScale = 1.0F;
   protected int weaponFlash = 0;

   public ItemScepter() {
      this.setMaxStackSize(1);
      this.setMaxDamage(this.getDurability());
      this.addPropertyOverride(new ResourceLocation("using"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack itemStack, World world, EntityLivingBase entity) {
            return entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
         }
      });
   }

   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
      super.onUpdate(itemStack, world, entity, par4, par5);
   }

   public boolean onEntitySwing(EntityLivingBase entity, ItemStack itemStack) {
      if (entity instanceof EntityPlayer) {
         entity.setActiveHand(EnumHand.MAIN_HAND);
         return true;
      } else {
         return super.onEntitySwing(entity, itemStack);
      }
   }

   public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity) {
      return true;
   }

   public void onItemLeftClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
      if (hand != EnumHand.OFF_HAND) {
         ;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public void onUsingTick(ItemStack itemStack, EntityLivingBase entity, int useRemaining) {
      if (itemStack != null && entity != null && entity.getEntityWorld() != null) {
         int useTime = this.getMaxItemUseDuration(itemStack) - useRemaining;
         if (useTime >= this.getRapidTime(itemStack)) {
            int rapidRemainder = useTime % this.getRapidTime(itemStack);
            if (rapidRemainder == 0 && entity.getEntityWorld() != null && this.rapidAttack(itemStack, entity.getEntityWorld(), entity)) {
               this.damageItemRapid(itemStack, entity);
               this.weaponFlash = Math.max(20, this.getRapidTime(itemStack));
            }
         }

         if (useTime >= this.getChargeTime(itemStack)) {
            this.weaponFlash = Math.max(20, this.getChargeTime(itemStack));
         }

         super.onUsingTick(itemStack, entity, useRemaining);
      }
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      int useTime = this.getMaxItemUseDuration(stack) - timeLeft;
      float power = (float)useTime / (float)this.getChargeTime(stack);
      this.weaponFlash = 0;
      if ((double)power >= 0.1D) {
         if (power > 1.0F) {
            power = 1.0F;
         }

         if (this.chargedAttack(stack, worldIn, entityLiving, power)) {
            this.damageItemCharged(stack, entityLiving, power);
            this.weaponFlash = Math.min(20, this.getChargeTime(stack));
         }

      }
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.BOW;
   }

   public void damageItemRapid(ItemStack itemStack, EntityLivingBase entity) {
      itemStack.damageItem(1, entity);
   }

   public void damageItemCharged(ItemStack itemStack, EntityLivingBase entity, float power) {
      itemStack.damageItem(Math.max((int)power, 1), entity);
   }

   public int getDurability() {
      return 1000;
   }

   public int getMaxItemUseDuration(ItemStack itemStack) {
      return 72000;
   }

   public int getChargeTime(ItemStack itemStack) {
      return this.getMaxItemUseDuration(itemStack);
   }

   public int getRapidTime(ItemStack itemStack) {
      return this.getMaxItemUseDuration(itemStack);
   }

   public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
      if (!world.isRemote) {
         ;
      }

      return false;
   }

   public boolean chargedAttack(ItemStack itemStack, World world, EntityLivingBase entity, float power) {
      if (!world.isRemote) {
         ;
      }

      return false;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      return super.getAttributeModifiers(slot, stack);
   }

   public int getItemEnchantability() {
      return 18;
   }

   public void playSound(ItemStack itemStack, World world, EntityLivingBase entity, float power, BaseProjectileEntity projectile) {
      this.playSound(world, entity.posX, entity.posY, entity.posZ, projectile.getLaunchSound(), SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
   }

   public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
      return super.getIsRepairable(itemStack, repairStack);
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return true;
   }
}
