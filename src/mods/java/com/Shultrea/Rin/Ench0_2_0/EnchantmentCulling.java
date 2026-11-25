package com.Shultrea.Rin.Ench0_2_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Main_Sector.somanyenchantments;
import com.Shultrea.Rin.Utility_Sector.MsgSP_Particle;
import com.Shultrea.Rin.Utility_Sector.SMEnetwork;
import com.Shultrea.Rin.Utility_Sector.UtilityAccessor;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentCulling extends EnchantmentBase {
   public EnchantmentCulling() {
      super(Rarity.VERY_RARE, EnumList.COMBAT_AXE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Culling");
      this.setRegistryName("Culling");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.CullingEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Culling;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApply(ItemStack fTest) {
      return fTest.getItem() instanceof ItemAxe ? super.canApply(fTest) : false;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_020.Culling, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int level = EnchantmentHelper.getEnchantmentLevel(Smc_020.Culling, dmgSource);
                        float half = (float)level;
                        float maxHealth = fEvent.getEntityLiving().getMaxHealth();
                        float currentHealthPercent = fEvent.getEntityLiving().getHealth() / maxHealth;
                        if (!fEvent.getEntityLiving().isNonBoss()) {
                           half /= 2.0F;
                        }

                        int bonus = 0;
                        if (!(fEvent.getEntityLiving() instanceof EntityPlayer)) {
                           bonus = 4 + level * 2;
                        }

                        if (fEvent.getEntityLiving().getHealth() <= (float)bonus || (double)currentHealthPercent <= 0.10000000149011612D + ((double)half * 0.07D - 0.009999999776482582D)) {
                           attacker.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20 + level * 5, 1));
                        }

                        if ((double)attacker.fallDistance > 0.34D) {
                           attacker.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 4, -1, false, false));
                           if (fEvent.getEntityLiving().getHealth() > (float)bonus && (double)currentHealthPercent > 0.10000000149011612D + ((double)half * 0.07D - 0.009999999776482582D)) {
                              float DamageCull = fEvent.getAmount();
                              UtilityAccessor.damageTarget(fEvent.getEntityLiving(), somanyenchantments.PhysicalDamage, DamageCull * (0.7F + half * 0.33F + 0.01F));
                              fEvent.setAmount(1.0F);
                           } else if (fEvent.getEntityLiving().getHealth() <= (float)bonus || (double)currentHealthPercent <= 0.10000000149011612D + ((double)half * 0.07D - 0.009999999776482582D)) {
                              double X = fEvent.getEntity().posX;
                              double Y = fEvent.getEntity().posY;
                              double Z = fEvent.getEntity().posZ;
                              Random random = fEvent.getEntity().world.rand;

                              for(int i = 0; i < 20; ++i) {
                                 double d0 = random.nextGaussian() * 0.02D;
                                 double d1 = random.nextGaussian() * 0.02D;
                                 double d2 = random.nextGaussian() * 0.02D;
                                 SMEnetwork.net.sendToAll(new MsgSP_Particle("angryVillager", (double)((float)X + random.nextFloat()), Y + (double)random.nextFloat() * 2.0D, (double)((float)Z + random.nextFloat()), d0, d1, d2));
                              }

                              float Damage = maxHealth * 1000.0F;
                              attacker.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60 + level * 20, level - 1));
                              UtilityAccessor.damageTarget(fEvent.getEntityLiving(), somanyenchantments.Culled, Damage);
                              fEvent.setAmount(1.0F);
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void HandleEnchant(LivingDropsEvent fEvent) {
      if (fEvent.getSource() == somanyenchantments.Culled) {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_020.Culling, dmgSource) > 0) {
                     if (Math.random() <= 0.3499999940395355D) {
                        ItemStack itemHead = null;
                        if (fEvent.getEntity() instanceof EntityCreeper) {
                           itemHead = new ItemStack(Items.SKULL, 1, 4);
                        }

                        if (fEvent.getEntity() instanceof EntitySkeleton) {
                           itemHead = new ItemStack(Items.SKULL, 1, 0);
                        }

                        if (fEvent.getEntity() instanceof EntityZombie) {
                           itemHead = new ItemStack(Items.SKULL, 1, 2);
                        }

                        if (fEvent.getEntity() instanceof EntityPlayer) {
                           itemHead = new ItemStack(Items.SKULL, 1, 3);
                           itemHead.setTagCompound(new NBTTagCompound());
                           itemHead.getTagCompound().setString("SkullOwner", fEvent.getEntityLiving().getName());
                        }

                        if (fEvent.getEntity() instanceof EntitySpider) {
                           itemHead = new ItemStack(Items.SKULL, 1, 3);
                           itemHead.setTagCompound(new NBTTagCompound());
                           itemHead.getTagCompound().setString("SkullOwner", "MHF_Spider");
                        }

                        if (fEvent.getEntity() instanceof EntityCaveSpider) {
                           itemHead = new ItemStack(Items.SKULL, 1, 3);
                           itemHead.setTagCompound(new NBTTagCompound());
                           itemHead.getTagCompound().setString("SkullOwner", "MHF_CaveSpider");
                        }

                        if (fEvent.getEntity() instanceof EntityEnderman) {
                           itemHead = new ItemStack(Items.SKULL, 1, 3);
                           itemHead.setTagCompound(new NBTTagCompound());
                           itemHead.getTagCompound().setString("SkullOwner", "MHF_Enderman");
                        }

                        if (fEvent.getEntity() instanceof EntityPigZombie) {
                           itemHead = new ItemStack(Items.SKULL, 1, 3);
                           itemHead.setTagCompound(new NBTTagCompound());
                           itemHead.getTagCompound().setString("SkullOwner", "MHF_PigZombie");
                        }

                        if (fEvent.getEntity() instanceof EntityBlaze) {
                           itemHead = new ItemStack(Items.SKULL, 1, 3);
                           itemHead.setTagCompound(new NBTTagCompound());
                           itemHead.getTagCompound().setString("SkullOwner", "MHF_Blaze");
                        }

                        if (itemHead != null) {
                           EntityItem entityItem = new EntityItem(fEvent.getEntity().world, fEvent.getEntity().posX, fEvent.getEntity().posY, fEvent.getEntity().posZ, itemHead);
                           fEvent.getDrops().add(entityItem);
                        }
                     }

                  }
               }
            }
         }
      }
   }
}
