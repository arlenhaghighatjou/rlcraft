package com.Shultrea.Rin.Ench0_1_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentSwifterSlashes extends EnchantmentBase {
   public static final EaglercraftUUID CACHED_UUID = EaglercraftUUID.fromString("e6109481-134f-4c54-a535-29c3ae5c7a21");

   public EnchantmentSwifterSlashes() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("SwifterSlashes");
      this.setRegistryName("SwifterSlashes");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.SwifterSlashesEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.SwifterSlashes;
   }

   public int getMinEnchantability(int par1) {
      return 5 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && fTest != Smc_010.Bluntness && fTest != Enchantments.KNOCKBACK && fTest != Smc_010.BlessedEdge && fTest != Smc_010.ReviledBlade && fTest != Smc_010.CursedEdge;
   }

   public boolean isValidPlayer(Entity entity) {
      return entity instanceof EntityPlayer && ((EntityPlayer)entity).getHeldItemMainhand() != null && this.level(((EntityPlayer)entity).getHeldItemMainhand()) > 0;
   }

   public boolean isValidMob(Entity entity) {
      if (entity instanceof EntityMob || entity instanceof EntityAnimal) {
         if (((EntityMob)entity).getHeldItemMainhand() != null && this.level(((EntityMob)entity).getHeldItemMainhand()) > 0) {
            return true;
         }

         if (((EntityAnimal)entity).getHeldItemMainhand() != null && this.level(((EntityAnimal)entity).getHeldItemMainhand()) > 0) {
            return true;
         }
      }

      return false;
   }

   public int level(ItemStack stack) {
      return EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, stack);
   }

   public void register(EventBus bus) {
      bus.register(AttackEntityEvent.class, this::onEntityHit);
      bus.register(LivingHurtEvent.class, EventPriority.HIGH, false, this::HandleEnchant);
      bus.register(LivingUpdateEvent.class, this::HandleEnchant);
   }

   public void onEntityHit(AttackEntityEvent event) {
      if (this.isValidPlayer(event.getEntityPlayer()) || this.isValidMob(event.getEntityLiving())) {
         ItemStack stack = event.getEntityLiving().getHeldItemMainhand();
         if (EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, stack) == 0) {
            return;
         }

         int levelFasterStrikes = EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, stack);
         if (event.getEntity().world.rand.nextInt(100) < 25 + this.level(stack)) {
            event.getTarget().hurtResistantTime = 23 - this.level(stack) * 3;
         }
      }

   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();
            if (weapon != null) {
               int level = EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, weapon);
               if (EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, weapon) > 0) {
                  if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                     if (fEvent.getEntityLiving().world.rand.nextInt(100) < 25 + level * 4) {
                        fEvent.getEntityLiving().hurtResistantTime = 23 - level * 3;
                     }

                  }
               }
            }
         }
      }
   }

   public void HandleEnchant(LivingUpdateEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityPlayer) {
         EntityLivingBase entity = (EntityLivingBase)fEvent.getEntity();
         ItemStack weapon = entity.getHeldItemMainhand();
         if (weapon == null) {
            this.RemoveAttackSpeedBuff(entity);
         } else {
            int level = EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, weapon);
            if (level > 0) {
               this.AddAttackSpeedBuff(entity);
            } else {
               this.RemoveAttackSpeedBuff(entity);
            }

         }
      }
   }

   private void AddAttackSpeedBuff(EntityLivingBase fEntity) {
      ItemStack weapon = fEntity.getHeldItemMainhand();
      int level = EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, weapon);
      IAttributeInstance speedAttr = fEntity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
      AttributeModifier modSpeed = new AttributeModifier(CACHED_UUID, "attackSpeed", (double)level * 0.45D + 0.04D * (double)level, 1);
      speedAttr.removeModifier(modSpeed);
      speedAttr.applyModifier(modSpeed);
      if (speedAttr.getModifier(CACHED_UUID) == null) {
         ;
      }
   }

   private void RemoveAttackSpeedBuff(EntityLivingBase fEntity) {
      ItemStack weapon = fEntity.getHeldItemMainhand();
      int level = EnchantmentHelper.getEnchantmentLevel(Smc_010.SwifterSlashes, weapon);
      IAttributeInstance speedAttr = fEntity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
      if (speedAttr.getModifier(CACHED_UUID) != null) {
         AttributeModifier modSpeed = new AttributeModifier(CACHED_UUID, "attackSpeed", (double)level * 0.45D + 0.04D * (double)level, 1);
         speedAttr.removeModifier(modSpeed);
      }
   }
}
