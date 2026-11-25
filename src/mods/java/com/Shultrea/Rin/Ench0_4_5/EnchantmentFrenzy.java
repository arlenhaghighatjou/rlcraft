package com.Shultrea.Rin.Ench0_4_5;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentFrenzy extends EnchantmentBase {
   public EnchantmentFrenzy() {
      super(Rarity.RARE, EnumList.SWORD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Frenzy");
      this.setRegistryName("Frenzy");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Frenzy;
   }

   public int getMaxLevel() {
      return ModConfig.level.Frenzy;
   }

   public int getMinEnchantability(int par1) {
      return 30 + (par1 - 1) * 15;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onAttack(LivingAttackEvent event) {
      if (!event.isCanceled() && event.getAmount() > 0.0F && event.getSource().getTrueSource() != null && event.getSource().damageType.equals("player")) {
         World world = event.getEntityLiving().world;
         EaglercraftRandom random = new EaglercraftRandom();
         EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
         EntityLivingBase victim = event.getEntityLiving();
         if (attacker.getHeldItemMainhand().isEmpty()) {
            return;
         }

         if (victim instanceof EntityPlayer) {
            return;
         }

         int level = Math.min(10, EnchantmentHelper.getEnchantmentLevel(this, attacker.getHeldItemMainhand()));
         boolean roll = random.nextInt(10) < level * 200;
         if (roll) {
            List list = world.getEntitiesWithinAABBExcludingEntity(attacker, victim.getEntityBoundingBox().grow((double)(5 + level * 5), (double)(5 + level * 5), (double)(5 + level * 5)));
            Collections.shuffle(list);

            for(int x = 0; x < list.size(); ++x) {
               if (list.get(x) instanceof EntityLivingBase) {
                  EntityLivingBase randomTarget = (EntityLivingBase)list.get(x);
                  if (victim.getDistanceSq(randomTarget) <= (double)(36 + (level - 1) * 28) && victim.getMaxHealth() <= victim.getHealth()) {
                     victim.setRevengeTarget(randomTarget);
                     World victimWorld = victim.getEntityWorld();
                     EntityLivingBase revenge = victim.getRevengeTarget();
                     if (victimWorld instanceof WorldServer && revenge != null) {
                        ((WorldServer)world).addScheduledTask(() -> {
                           try {
                              if (victim != null && attacker != null && revenge != null && !victim.isDead && victim instanceof EntityLiving && !victim.getUniqueID().equals(revenge.getUniqueID())) {
                                 EntityLiving victimLiving = (EntityLiving)victim;
                                 victimLiving.setRevengeTarget(revenge);
                                 victimLiving.setAttackTarget(revenge);
                              }
                           } catch (Exception var4) {
                              ;
                           }

                        });
                     }
                  }
               }
            }
         }
      }

   }
}
