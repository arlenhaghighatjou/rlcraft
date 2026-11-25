package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class EnchantmentLightWeight extends EnchantmentBase {
   public EnchantmentLightWeight() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
      this.setName("LightWeight");
      this.setRegistryName("LightWeight");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.LightWeight;
   }

   public int getMaxLevel() {
      return ModConfig.level.LightWeight;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = true
   )
   public void onExist(PlayerTickEvent e) {
      if (e.phase != Phase.START) {
         if (e.player != null) {
            int level = EnchantmentHelper.getMaxEnchantmentLevel(this, e.player);
            if (level > 0 && !e.player.onGround) {
               if (e.player.isCreative()) {
                  if (!e.player.capabilities.isFlying) {
                     this.lightStep(level, e.player);
                  }
               } else {
                  this.lightStep(level, e.player);
               }
            }

         }
      }
   }

   public void lightStep(int level, EntityPlayer player) {
      float cloud = MathHelper.clamp((float)(level / 3), 0.33F, 1.0F);
      if (player.fallDistance >= 3.0F && player.world.isRemote) {
         for(int i = 0; i < 3; ++i) {
            player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX, player.posY - 2.0D, player.posZ, (double)((player.getRNG().nextFloat() - 0.5F) / 2.0F * cloud), -0.5D * (double)cloud, (double)((player.getRNG().nextFloat() - 0.5F) / 2.0F * cloud), new int[0]);
         }
      }

      if (player.isSprinting() && player.world.isRemote) {
         player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX, player.posY - 1.5D, player.posZ, (double)((player.getRNG().nextFloat() - 0.5F) / 2.0F * cloud), 0.1D * (double)cloud, (double)((player.getRNG().nextFloat() - 0.5F) / 2.0F * cloud), new int[0]);
      }

      if (!player.onGround && !player.world.isRemote) {
         player.jumpMovementFactor += 0.0075F * (float)level + 0.0025F;
      }

      if (player.collidedHorizontally) {
         player.stepHeight = 1.0F;
      } else {
         player.stepHeight = 0.5F;
      }

   }

   @SubscribeEvent
   public void onJump(LivingJumpEvent e) {
      if (e.getEntityLiving() != null) {
         if (EnchantmentHelper.getMaxEnchantmentLevel(this, e.getEntityLiving()) > 0) {
            EntityLivingBase var10000 = e.getEntityLiving();
            var10000.motionY *= 1.05D + (double)EnchantmentHelper.getMaxEnchantmentLevel(this, e.getEntityLiving()) * 0.15D;
         }

      }
   }

   @SubscribeEvent
   public void onEvent(LivingFallEvent e) {
      int level = EnchantmentHelper.getMaxEnchantmentLevel(this, e.getEntityLiving());
      if (level > 0) {
         e.setDistance(MathHelper.clamp(e.getDistance() - 4.0F - (float)level * 2.0F, 0.0F, 20.0F));
      }

   }
}
