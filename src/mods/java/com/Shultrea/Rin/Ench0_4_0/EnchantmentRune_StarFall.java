package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Prop_Sector.ArrowPropertiesProvider;
import com.Shultrea.Rin.Prop_Sector.IArrowProperties;
import java.util.ArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EnchantmentRune_StarFall extends Enchantment {
   public EnchantmentRune_StarFall() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("rune_starfall");
      this.setRegistryName("rune_starfall");
   }

   public int getMaxLevel() {
      return 2;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 50;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH,
      receiveCanceled = true
   )
   public void setDidStarFall(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)event.getEntity();
         EntityLivingBase shooter = (EntityLivingBase)arrow.shootingEntity;
         if (shooter == null) {
            return;
         }

         ItemStack bow = shooter.getActiveItemStack();
         if (bow == ItemStack.EMPTY) {
            bow = shooter.getHeldItemOffhand();
            if (bow == ItemStack.EMPTY) {
               return;
            }
         }

         if (arrow.hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
            IArrowProperties properties = (IArrowProperties)arrow.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
            if (EnchantmentHelper.getEnchantmentLevel(Smc_040.rune_starfall, bow) > 0) {
               int l = EnchantmentHelper.getMaxEnchantmentLevel(Smc_040.rune_starfall, shooter);
               properties.setLevel(l);
               properties.setDidStarFall(false);
            }
         }
      }

   }

   @SubscribeEvent
   public void whenitsHigh(WorldTickEvent fEvent) {
      if (fEvent.world.getTotalWorldTime() % 30L == 0L) {
         ArrayList<Entity> entities = (ArrayList)fEvent.world.loadedEntityList;
         int size = entities.size();
         EntityArrow arrow = null;

         for(int a = 0; a < size; ++a) {
            Entity f = (Entity)entities.get(a);
            if (f instanceof Entity && f instanceof EntityArrow) {
               if (f instanceof EntityTippedArrow) {
                  arrow = (EntityTippedArrow)f;
               } else {
                  arrow = (EntityArrow)f;
               }
            }
         }

         if (arrow != null) {
            if (arrow.motionX != 0.0D || arrow.motionZ != 0.0D || arrow.motionY != 0.0D) {
               Entity shoote = arrow.shootingEntity;
               if (shoote != null) {
                  if (shoote instanceof EntityLivingBase) {
                     EntityLivingBase shooter = (EntityLivingBase)shoote;
                     if (arrow.hasCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null)) {
                        IArrowProperties properties = (IArrowProperties)arrow.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);

                        try {
                           boolean flag = properties.didStarFall();
                           if (properties.getIsStarFallMade()) {
                              return;
                           }

                           if (!flag && properties.getLevel() > 0 && arrow.posY > 60.0D + arrow.shootingEntity.posY) {
                              for(int i = 0; i < properties.getLevel() * 700; ++i) {
                                 if (!properties.didStarFall()) {
                                    properties.setDidStarFall(true);
                                 }

                                 ItemArrow itemarrow = new ItemArrow();
                                 EntityArrow arrows = itemarrow.createArrow(shooter.getEntityWorld(), new ItemStack(Items.ARROW), shooter);
                                 arrow.setDead();
                                 arrows.shoot(shooter, -90.0F, (float)((double)(shooter.getRNG().nextFloat() * 75.0F) - Math.random() * 75.0D), 0.0F, (float)properties.getLevel() * 7.0F * (float)Math.random(), 1.0F + shooter.getRNG().nextFloat() * 2.0F);
                                 arrows.posX = arrow.posX + Math.random() * -12.0D + shooter.getRNG().nextDouble() * 12.0D;
                                 arrows.posY = arrow.posY + 60.0D + Math.random() * 2.0D + shooter.getRNG().nextDouble() * 16.0D;
                                 arrows.posZ = arrow.posZ + Math.random() * -12.0D + shooter.getRNG().nextDouble() * 12.0D;
                                 arrows.setIsCritical(true);
                                 IArrowProperties arrowsp = (IArrowProperties)arrows.getCapability(ArrowPropertiesProvider.ARROWPROPERTIES_CAP, (EnumFacing)null);
                                 arrowsp.setIsStarFallMade(true);
                                 arrowsp.setDidStarFall(true);
                                 shooter.getEntityWorld().spawnEntity(arrows);
                              }
                           }
                        } catch (Exception var13) {
                           ;
                        }
                     }

                  }
               }
            }
         }
      }
   }
}
