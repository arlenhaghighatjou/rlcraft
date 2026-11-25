package dev.satyrn.wolfarmor.mixin;

import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EntityLivingBase.class})
public abstract class MixinEntityLivingBase extends MixinEntity {
   @Shadow
   protected boolean isJumping;

   public MixinEntityLivingBase() {
   }

   @Shadow
   public abstract boolean isChild();

   @Shadow
   public void travel(float p_travel_1_, float p_travel_2_, float p_travel_3_) {
   }

   @Shadow
   public abstract void renderBrokenItemStack(ItemStack var1);

   @Shadow
   public abstract IAttributeInstance getEntityAttribute(IAttribute var1);

   @Shadow
   public abstract Random getRNG();

   @Shadow
   protected void onNewPotionEffect(PotionEffect id) {
   }

   @Shadow
   protected void onChangedPotionEffect(PotionEffect potionEffect, boolean sendUpdate) {
   }

   @Shadow
   protected void onFinishedPotionEffect(PotionEffect effect) {
   }

   @Shadow
   protected void damageArmor(float damage) {
   }
}
