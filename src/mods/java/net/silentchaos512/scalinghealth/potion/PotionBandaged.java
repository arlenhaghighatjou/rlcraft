package net.silentchaos512.scalinghealth.potion;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.scalinghealth.config.Config;

public class PotionBandaged extends Potion {
   private static final float BASE_HEAL_RATE = 0.005F;
   private static final double SPEED_MODIFIER = -0.25D;

   public PotionBandaged() {
      super(false, 16243885);
      this.setPotionName("effect.scalinghealth.bandaged");
      this.setBeneficial();
      this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "732486d8-f730-41a2-868f-eb988738986f", -0.25D, 2);
   }

   public void performEffect(EntityLivingBase entityLiving, int amplifier) {
      if (entityLiving.getHealth() >= entityLiving.getMaxHealth()) {
         entityLiving.removePotionEffect(this);
      }

      float healAmount = 0.005F * entityLiving.getMaxHealth() * (float)(amplifier + 1);
      EntityHelper.heal(entityLiving, healAmount, Config.Items.healingItemFireEvent);
   }

   public boolean isReady(int duration, int amplifier) {
      return duration % 20 == 0;
   }

   public List<ItemStack> getCurativeItems() {
      return ImmutableList.of();
   }

   public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
      return modifier.getAmount();
   }
}
