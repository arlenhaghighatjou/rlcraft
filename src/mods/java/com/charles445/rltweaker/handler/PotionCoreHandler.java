package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.function.Consumer;
import meldexun.reflectionutil.ReflectionField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PotionCoreHandler {
   boolean controllableJumpBoost = false;
   public static final ReflectionField<IAttribute> DAMAGE_RESISTANCE = new ReflectionField<IAttribute>("com.tmtravlr.potioncore.PotionCoreAttributes", "DAMAGE_RESISTANCE", "DAMAGE_RESISTANCE");

   public PotionCoreHandler() {
      MinecraftForge.EVENT_BUS.register(this);

      try {
         if (ModConfig.server.potioncore.capJumpBoost) {
            CompatUtil.wrapSpecificHandler("PCJump", (x$0) -> {
               new PotionCoreHandler.PCJump(x$0);
            }, "com.tmtravlr.potioncore.PotionCoreEventHandler", "onLivingJump");
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup PotionCoreHandler!", var2);
         ErrorUtil.logSilent("PotionCore Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SubscribeEvent
   public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
      if (DAMAGE_RESISTANCE.isPresent()) {
         if (ModConfig.patches.patchPotionCoreResistance) {
            if (!event.getEntity().world.isRemote) {
               if (event.getEntity() instanceof EntityLivingBase) {
                  EntityLivingBase entity = (EntityLivingBase)event.getEntity();
                  PotionEffect potionEffect = entity.getActivePotionEffect(MobEffects.RESISTANCE);
                  if (potionEffect != null) {
                     Potion potion = potionEffect.getPotion();
                     potion.removeAttributesModifiersFromEntity(entity, entity.getAttributeMap(), potionEffect.getAmplifier());
                     potion.applyAttributesModifiersToEntity(entity, entity.getAttributeMap(), potionEffect.getAmplifier());
                  }

               }
            }
         }
      }
   }

   public class PCJump {
      private IEventListener handler;

      public PCJump(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST
      )
      public void onJump(LivingJumpEvent event) {
         EntityLivingBase living = event.getEntityLiving();
         PotionEffect jumpEffect = living.getActivePotionEffect(MobEffects.JUMP_BOOST);
         if (jumpEffect == null || jumpEffect.getAmplifier() >= 0 && jumpEffect.getAmplifier() <= 127) {
            this.handler.invoke(event);
            if (jumpEffect == null && living.world.isRemote) {
               living.getEntityData().setBoolean("Potion Core - Jump Boost Jumping", false);
            }

         } else {
            if (living.world.isRemote) {
               living.getEntityData().setBoolean("Potion Core - Jump Boost Jumping", false);
            }

         }
      }

      @SubscribeEvent
      public void onPlayerUpdate(PlayerTickEvent event) {
         if (event.phase != Phase.END) {
            EntityPlayer player = event.player;
            if (player != null) {
               World world = player.world;
               if (world != null) {
                  if (world.isRemote) {
                     double jbh = event.player.getEntityData().getDouble("Potion Core - Jump Boost Height");
                     if (jbh > 127.0D) {
                        ErrorUtil.logSilent("PotionCore Invalid Jump Boost Height");
                        event.player.getEntityData().setDouble("Potion Core - Jump Boost Height", 1.75D);
                     }

                  }
               }
            }
         }
      }

      @SubscribeEvent
      public void potionApplicable(PotionApplicableEvent event) {
         EntityLivingBase living = event.getEntityLiving();
         PotionEffect newEffect = event.getPotionEffect();
         if (newEffect != null) {
            if (living instanceof EntityPlayer) {
               if (living.world.isRemote) {
                  if (newEffect.getPotion() == MobEffects.JUMP_BOOST) {
                     PotionEffect oldEffect = living.getActivePotionEffect(MobEffects.JUMP_BOOST);
                     if (oldEffect == null) {
                        return;
                     }

                     if (newEffect.getAmplifier() < 0 && oldEffect.getAmplifier() >= 0) {
                        living.removePotionEffect(MobEffects.JUMP_BOOST);
                     }
                  }

               }
            }
         }
      }
   }
}
