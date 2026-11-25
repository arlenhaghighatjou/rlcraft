package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class SRPEffectBase extends Potion {
   private ResourceLocation icon;

   public SRPEffectBase(String name, boolean isBadEffectIn, int liquidColorIn, int IconIndexX, int IconIndexY) {
      super(isBadEffectIn, liquidColorIn);
      this.setRegistryName("srparasites:" + name);
      this.setPotionName("mob_effect." + this.getRegistryName());
      this.setIconIndex(IconIndexX, IconIndexY);
      this.icon = new ResourceLocation("srparasites:textures/gui/potion_" + name + ".png");
   }

   public boolean hasStatusIcon() {
      return false;
   }

   public List<ItemStack> getCurativeItems() {
      return new ArrayList();
   }

   public boolean isReady(int duration, int amplifier) {
      if (this != SRPPotions.BLEED_E && this != SRPPotions.EPEL_E && this != SRPPotions.DLER_E && this != SRPPotions.CORRO_E && this != SRPPotions.FOSTER_E) {
         return true;
      } else {
         int j = 25 >> amplifier;
         if (j > 0) {
            return duration % j == 0;
         } else {
            return true;
         }
      }
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (this == SRPPotions.BLEED_E) {
         if (entity.world.isRemote) {
            return;
         }

         float damage = entity.getMaxHealth() * SRPConfigSystems.bleedingDamage;
         if (entity.posX != entity.prevPosX || entity.posZ != entity.prevPosZ) {
            damage *= (float)(amplifier + 1);
         }

         damage = Math.min(damage, SRPConfigSystems.bleedingDamageCap);
         entity.attackEntityFrom(DamageSource.MAGIC, damage);
      } else if (this == SRPPotions.CORRO_E) {
         if (entity.world.isRemote) {
            return;
         }

         this.effectCORRO(entity, amplifier);
      } else if (this == SRPPotions.COTH_E) {
         if (entity.world.isRemote) {
            return;
         }

         this.effectCOTH(entity, amplifier);
      } else if (this == SRPPotions.RAGE_E) {
         if (entity.world.isRemote) {
            for(int i = 0; i <= 3; ++i) {
               this.spawnParticles(SRPEnumParticle.GCLOUD, new Random(), entity);
            }
         }
      } else if (this == SRPPotions.PREY_E) {
         if (entity.world.isRemote) {
            return;
         }

         this.effectPrey(entity, amplifier);
      } else if (this == SRPPotions.DLER_E) {
         if (entity.world.isRemote) {
            return;
         }

         this.effectNeedler(entity, amplifier);
      } else if (this == SRPPotions.FOSTER_E) {
         if (entity.world.isRemote) {
            return;
         }

         this.effectFoster(entity, amplifier);
      }

   }

   private void spawnParticles(SRPEnumParticle particleType, Random rand, EntityLivingBase entity) {
      double d0 = rand.nextGaussian() * 0.02D;
      double d1 = rand.nextGaussian() * 0.02D;
      double d2 = rand.nextGaussian() * 0.02D;
      ParticleSpawner.spawnParticle(particleType, entity.posX + (double)(rand.nextFloat() * entity.width * 2.0F) - (double)entity.width, entity.posY + 0.5D + (double)(rand.nextFloat() * entity.height), entity.posZ + (double)(rand.nextFloat() * entity.width * 2.0F) - (double)entity.width, d0, d1, d2, 0, 0, 0);
   }

   private void InfectNearby(EntityLivingBase entity, int range) {
      if (SRPConfigSystems.cothAura != 0) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D)).grow((double)range);

         for(EntityLivingBase mob : entity.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (mob != entity && !mob.isPotionActive(SRPPotions.COTH_E)) {
               if (mob.isPotionActive(SRPPotions.EPEL_E)) {
                  if (mob.world.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                     mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 4800, 0, false, false));
                  }
               } else if (entity.canEntityBeSeen(mob)) {
                  mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 4800, 0, false, false));
               }
            }
         }

      }
   }

   private void effectCOTH(EntityLivingBase entity, int amplifier) {
      if (!(entity instanceof EntityPlayer)) {
         boolean flag = !entity.world.isRemote;
         boolean particle = false;
         int dur = entity.getActivePotionEffect(SRPPotions.COTH_E).getDuration();
         switch(entity.getActivePotionEffect(SRPPotions.COTH_E).getAmplifier()) {
         case 0:
            if (dur < 200 && flag) {
               NBTTagCompound tags = entity.getEntityData();
               if (tags.hasKey("srpcothimmunity")) {
                  if (tags.getInteger("srpcothimmunity") != 0) {
                     entity.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 1, false, false));
                  } else {
                     entity.removeActivePotionEffect(SRPPotions.COTH_E);
                  }
               }
            }
            break;
         case 1:
            if (flag) {
               boolean tickFlag = entity.ticksExisted % 20 == 0;
               if (tickFlag) {
                  this.InfectNearby(entity, SRPConfigSystems.cothAura);
               }

               if (entity.getMaxHealth() * SRPConfigSystems.cothUnhide > entity.getHealth() && entity.getHealth() > 0.0F) {
                  NBTTagCompound tags = entity.getEntityData();
                  if (tags.hasKey("srpcothimmunity")) {
                     if (tags.getInteger("srpcothimmunity") != 0 && tickFlag) {
                        ParasiteEventEntity.spawnInsider(entity, entity.world, tags);
                     }

                     if (tags.getInteger("srpcothimmunity") > 1) {
                        particle = true;
                     }
                  } else {
                     entity.removeActivePotionEffect(SRPPotions.COTH_E);
                  }
               }

               if (dur < 200) {
                  NBTTagCompound tags = entity.getEntityData();
                  if (tags.hasKey("srpcothimmunity")) {
                     if (tags.getInteger("srpcothimmunity") != 0) {
                        entity.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
                        if (!entity.world.isRemote && SRPConfigSystems.useEvolution) {
                           SRPWorldData data = SRPWorldData.get(entity.world);
                           data.setTotalKills(SRPConfigSystems.valueCOTH, true, entity.world, true);
                           if (data.getEvolutionPhase() >= SRPConfigSystems.evolutionAssimilatedDehiding) {
                              int key = tags.getInteger("srpcothimmunity");
                              ++key;
                              tags.setInteger("srpcothimmunity", key);
                           }
                        }
                     } else {
                        entity.removeActivePotionEffect(SRPPotions.COTH_E);
                     }
                  }
               }
            }
            break;
         default:
            if (flag) {
               boolean tickFlag = entity.ticksExisted % 20 == 0;
               NBTTagCompound tags = entity.getEntityData();
               if (tags.hasKey("srpcothimmunity")) {
                  if (tags.getInteger("srpcothimmunity") > 1) {
                     if (tickFlag) {
                        ParasiteEventEntity.convertEntity(entity, tags, false, SRPConfigSystems.COTHVictimParasite);
                     }

                     particle = true;
                  } else {
                     if (tickFlag) {
                        this.InfectNearby(entity, SRPConfigSystems.cothAura);
                     }

                     if (entity.getMaxHealth() * SRPConfigSystems.cothUnhide > entity.getHealth() && entity.getHealth() > 0.0F && tickFlag) {
                        if (tags.getInteger("srpcothimmunity") != 0) {
                           ParasiteEventEntity.convertEntity(entity, tags, false, SRPConfigSystems.COTHVictimParasite);
                        }

                        if (tags.getInteger("srpcothimmunity") > 1) {
                           particle = true;
                        }
                     }

                     if (dur < 200) {
                        entity.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 2, false, false));
                        if (SRPConfigSystems.useEvolution && SRPWorldData.get(entity.world).getEvolutionPhase() >= SRPConfigSystems.evolutionAssimilatedDehiding) {
                           int key = tags.getInteger("srpcothimmunity");
                           ++key;
                           tags.setInteger("srpcothimmunity", key);
                        }
                     }
                  }
               } else {
                  entity.removeActivePotionEffect(SRPPotions.COTH_E);
               }
            }
         }

         if (particle) {
            SRPMain.network.sendToAll(new SRPPacketParticle(entity.posX, entity.posY, entity.posZ, entity.width, entity.height, (byte)1));
         }

      }
   }

   private void effectPrey(EntityLivingBase entity, int amplifier) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer pa = (EntityPlayer)entity;
         if (pa.isSpectator() || pa.isCreative()) {
            return;
         }
      }

      if (entity.ticksExisted % 40 == 0) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D)).grow(64.0D);

         for(EntityParasiticScent mob : entity.world.getEntitiesWithinAABB(EntityParasiticScent.class, axisalignedbb)) {
            if (mob.getTargetToKill() == entity && mob.getCanFollow()) {
               return;
            }
         }

         byte phase = SRPWorldData.get(entity.world).getEvolutionPhase();
         EntityParasiticScent nut = new EntityParasiticScent(entity.world, 1, entity);
         nut.copyLocationAndAnglesFrom(entity);
         nut.setScentLife(SRPConfigSystems.scentLifeObserver * 20);
         nut.increaseDanger(ParasiteEventEntity.getScentBonus(phase), true);
         nut.setScentReaction(ParasiteEventEntity.getScentReactionBonus(phase), false);
         nut.setCanFollow(true);
         entity.world.spawnEntity(nut);
      }

   }

   private void effectCORRO(EntityLivingBase entity, int amplifier) {
      ArrayList<ItemStack> off = new ArrayList();
      Iterable<ItemStack> gear = entity.getArmorInventoryList();
      if (gear != null && !gear.equals(Collections.emptyList())) {
         for(ItemStack part : gear) {
            if (!part.isEmpty() && part.isItemStackDamageable()) {
               off.add(part);
            }
         }

         if (!off.isEmpty()) {
            for(ItemStack part : off) {
               if ((double)part.getMaxDamage() * SRPConfigSystems.corrNot <= (double)(part.getMaxDamage() - part.getItemDamage())) {
                  part.damageItem(SRPConfigSystems.corroValue, entity);
               }
            }
         }
      }

   }

   private void effectNeedler(EntityLivingBase entity, int amplifier) {
      if (amplifier >= SRPConfigSystems.needlerTerminal) {
         amplifier = amplifier - SRPConfigSystems.needlerTerminal;
         entity.removeActivePotionEffect(SRPPotions.DLER_E);
         String mobname = null;

         try {
            mobname = EntityList.getKey(entity).toString();
         } catch (Exception var12) {
            SRPMain.logger.log(Level.ERROR, "Problem with needler and an entity", var12);
            return;
         }

         if (ParasiteEventEntity.checkName(mobname, SRPConfigSystems.needlerImmuneList, SRPConfigSystems.needlerImmuneListWhite)) {
            return;
         }

         entity.addPotionEffect(new PotionEffect(SRPPotions.DLER_E, 400, amplifier, false, false));
         float f1 = entity.getHealth();
         if (f1 <= 0.0F) {
            return;
         }

         float damagee = entity.getMaxHealth() * SRPConfigSystems.needlerDamage;
         entity.setHealth(f1 - damagee);
         entity.world.setEntityState(entity, (byte)2);
         entity.world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, 4.0F, false);
         if (entity.getHealth() <= 0.0F) {
            ItemStack itemstack = null;

            for(EnumHand enumhand : EnumHand.values()) {
               ItemStack itemstack1 = entity.getHeldItem(enumhand);
               if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
                  itemstack = itemstack1.copy();
                  itemstack1.shrink(1);
                  break;
               }
            }

            if (itemstack != null) {
               if (entity instanceof EntityPlayerMP) {
                  EntityPlayerMP entityplayermp = (EntityPlayerMP)entity;
                  entityplayermp.addStat(StatList.getObjectUseStats(Items.TOTEM_OF_UNDYING));
                  CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
               }

               entity.setHealth(1.0F);
               entity.clearActivePotions();
               entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
               entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
               entity.world.setEntityState(entity, (byte)35);
            } else {
               entity.onDeath(DamageSource.MAGIC);
            }
         }
      }

   }

   private void effectFoster(EntityLivingBase entity, int amplifier) {
      if (entity instanceof EntityPMalleable) {
         ((EntityPMalleable)entity).increaseAllResistances();
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (mc.currentScreen != null) {
         mc.getTextureManager().bindTexture(this.icon);
         Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      mc.getTextureManager().bindTexture(this.icon);
      Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
   }
}
