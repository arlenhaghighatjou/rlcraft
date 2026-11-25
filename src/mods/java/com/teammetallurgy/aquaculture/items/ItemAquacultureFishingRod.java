package com.teammetallurgy.aquaculture.items;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teammetallurgy.aquaculture.handlers.EntityCustomFishHook;
import javax.annotation.Nonnull;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAquacultureFishingRod extends ItemFishingRod {
   public int enchantability;

   public ItemAquacultureFishingRod(int maxDamage, ToolMaterial material) {
      this.setMaxDamage(maxDamage);
      this.setMaxStackSize(1);
      this.enchantability = material == ToolMaterial.WOOD ? 10 : material.getEnchantability();
      this.addPropertyOverride(new ResourceLocation("cast"), (stack, world, entity) -> {
         if (entity != null) {
            boolean isMain = entity.getHeldItemMainhand() == stack;
            boolean isOff = entity.getHeldItemOffhand() == stack;
            if (entity.getHeldItemMainhand().getItem() instanceof ItemAquacultureFishingRod) {
               isOff = false;
            }

            return (isMain || isOff) && entity instanceof EntityPlayer && ((EntityPlayer)entity).fishEntity != null ? 1.0F : 0.0F;
         } else {
            return 0.0F;
         }
      });
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }

   public int getItemEnchantability() {
      return this.enchantability;
   }

   @Nonnull
   public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack) {
      return HashMultimap.create();
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entityplayer, @Nonnull EnumHand hand) {
      ItemStack itemstack = entityplayer.getHeldItem(hand);
      if (entityplayer.fishEntity != null) {
         int i = entityplayer.fishEntity.handleHookRetraction();
         itemstack.damageItem(i, entityplayer);
         entityplayer.swingArm(hand);
         if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
         }

         NBTTagCompound tag = itemstack.getTagCompound();
         Preconditions.checkNotNull(tag, "tagCompound");
         tag.setBoolean("using", false);
      } else {
         world.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!world.isRemote) {
            world.spawnEntity(new EntityCustomFishHook(world, entityplayer));
         }

         entityplayer.swingArm(hand);
         if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
         }

         NBTTagCompound tag = itemstack.getTagCompound();
         Preconditions.checkNotNull(tag, "tagCompound");
         tag.setBoolean("using", true);
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }
}
