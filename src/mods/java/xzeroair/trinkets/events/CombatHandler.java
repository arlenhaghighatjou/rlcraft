package xzeroair.trinkets.events;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Arrow;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IBowAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IHealAbility;

public class CombatHandler extends EventBaseHandler {
   public CombatHandler() {
   }

   @SubscribeEvent
   public void arrowLooseEvent(ArrowLooseEvent event) {
      Capabilities.getEntityProperties(event.getEntityLiving(), (prop) -> {
         try {
            prop.getRaceHandler().bowUsed(event);
         } catch (Exception var9) {
            var9.printStackTrace();
         }

         if (!event.isCanceled()) {
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();
               if (event.isCanceled()) {
                  break;
               }

               try {
                  IAbilityInterface ability = value.getAbility();
                  if (ability instanceof IBowAbility) {
                     ((IBowAbility)ability).looseArrow(event);
                  }
               } catch (Exception var8) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var8.printStackTrace();
               }
            }
         }

      });
   }

   @SubscribeEvent
   public void arrowNockEvent(ArrowNockEvent event) {
      Capabilities.getEntityProperties(event.getEntityLiving(), (prop) -> {
         try {
            prop.getRaceHandler().bowNocked(event);
         } catch (Exception var9) {
            var9.printStackTrace();
         }

         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof IBowAbility) {
                  ((IBowAbility)ability).knockArrow(event);
               }
            } catch (Exception var8) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var8.printStackTrace();
            }
         }

      });
   }

   @SubscribeEvent
   public void ArrowImpactEvent(Arrow event) {
      if (event.getArrow().shootingEntity instanceof EntityLivingBase) {
         Capabilities.getEntityProperties(event.getArrow().shootingEntity, (prop) -> {
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();
               if (event.isCanceled()) {
                  break;
               }

               try {
                  IAbilityInterface ability = value.getAbility();
                  if (ability instanceof IBowAbility) {
                     ((IBowAbility)ability).arrowImpact(event);
                  }
               } catch (Exception var8) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var8.printStackTrace();
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void TargetEvent(LivingSetAttackTargetEvent event) {
      EntityLivingBase enemy = event.getEntityLiving();
      Capabilities.getEntityProperties(event.getTarget(), (prop) -> {
         prop.getRaceHandler().targetedByEnemy(enemy);
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();
            if (enemy instanceof EntityLiving && ((EntityLiving)enemy).getAttackTarget() == null) {
               break;
            }

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof IAttackAbility) {
                  ((IAttackAbility)ability).targetedByEnemy(enemy);
               }
            } catch (Exception var8) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var8.printStackTrace();
            }
         }

      });
   }

   @SubscribeEvent
   public void attackEvent(LivingAttackEvent event) {
      DamageSource source = event.getSource();
      if (!source.canHarmInCreative()) {
         float damage = event.getAmount();
         EntityLivingBase attacked = event.getEntityLiving();
         if (source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            Capabilities.getEntityProperties(attacker, (prop) -> {
               boolean isCanceled = !prop.getRaceHandler().attackedEntity(attacked, source, damage);
               if (!isCanceled) {
                  Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

                  for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                     String key = (String)entry.getKey();
                     AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                     try {
                        IAbilityInterface ability = value.getAbility();
                        if (ability instanceof IAttackAbility) {
                           isCanceled = ((IAttackAbility)ability).attackEntity(attacked, source, damage, isCanceled);
                           if (isCanceled) {
                              break;
                           }
                        }
                     } catch (Exception var13) {
                        Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                        var13.printStackTrace();
                     }
                  }
               }

               if (isCanceled) {
                  this.cancelEvent(event);
               }

            });
         }

         if (!event.isCanceled()) {
            Capabilities.getEntityProperties(attacked, (prop) -> {
               boolean isCanceled = !prop.getRaceHandler().isAttacked(source, damage);
               if (!isCanceled) {
                  Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

                  for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                     String key = (String)entry.getKey();
                     AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                     try {
                        IAbilityInterface ability = value.getAbility();
                        if (ability instanceof IAttackAbility) {
                           isCanceled = ((IAttackAbility)ability).attacked(attacked, source, damage, isCanceled);
                           if (isCanceled) {
                              break;
                           }
                        }
                     } catch (Exception var13) {
                        Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                        var13.printStackTrace();
                     }
                  }
               }

               if (isCanceled) {
                  this.cancelEvent(event);
               }

            });
         }

      }
   }

   @SubscribeEvent
   public void HurtEvent(LivingHurtEvent event) {
      DamageSource source = event.getSource();
      float damage = event.getAmount();
      if (!source.canHarmInCreative() && damage != 0.0F) {
         EntityLivingBase attacked = event.getEntityLiving();
         if (source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            damage = ((Float)Capabilities.getEntityProperties(attacker, Float.valueOf(damage), (prop, dmg) -> {
               dmg = prop.getRaceHandler().hurtEntity(attacked, source, dmg.floatValue());
               if (dmg.floatValue() > 0.0F) {
                  Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

                  for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                     String key = (String)entry.getKey();
                     AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                     try {
                        IAbilityInterface ability = value.getAbility();
                        if (ability instanceof IAttackAbility) {
                           dmg = ((IAttackAbility)ability).hurtEntity(attacked, source, dmg.floatValue());
                           if (dmg.floatValue() <= 0.0F) {
                              break;
                           }
                        }
                     } catch (Exception var10) {
                        Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                        var10.printStackTrace();
                     }
                  }
               }

               return dmg;
            })).floatValue();
         }

         if (damage > 0.0F) {
            damage = ((Float)Capabilities.getEntityProperties(attacked, Float.valueOf(damage), (prop, dmg) -> {
               dmg = prop.getRaceHandler().isHurt(source, dmg.floatValue());
               if (dmg.floatValue() > 0.0F) {
                  Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

                  for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                     String key = (String)entry.getKey();
                     AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                     try {
                        IAbilityInterface ability = value.getAbility();
                        if (ability instanceof IAttackAbility) {
                           dmg = ((IAttackAbility)ability).hurt(attacked, source, dmg.floatValue());
                           if (dmg.floatValue() <= 0.0F) {
                              break;
                           }
                        }
                     } catch (Exception var10) {
                        Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                        var10.printStackTrace();
                     }
                  }
               }

               return dmg;
            })).floatValue();
         }

         if (damage <= 0.0F) {
            this.cancelEvent(event);
         } else {
            event.setAmount(damage);
         }

      }
   }

   @SubscribeEvent
   public void applyDamageEvent(LivingDamageEvent event) {
      DamageSource source = event.getSource();
      float damage = event.getAmount();
      if (!source.canHarmInCreative() && damage != 0.0F) {
         EntityLivingBase attacked = event.getEntityLiving();
         if (source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            damage = ((Float)Capabilities.getEntityProperties(attacker, Float.valueOf(damage), (prop, dmg) -> {
               dmg = prop.getRaceHandler().damagedEntity(attacked, source, dmg.floatValue());
               if (dmg.floatValue() > 0.0F) {
                  Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

                  for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                     String key = (String)entry.getKey();
                     AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                     try {
                        IAbilityInterface ability = value.getAbility();
                        if (ability instanceof IAttackAbility) {
                           dmg = ((IAttackAbility)ability).damageEntity(attacked, source, dmg.floatValue());
                           if (dmg.floatValue() <= 0.0F) {
                              break;
                           }
                        }
                     } catch (Exception var10) {
                        Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                        var10.printStackTrace();
                     }
                  }
               }

               return dmg;
            })).floatValue();
         }

         if (damage > 0.0F) {
            damage = ((Float)Capabilities.getEntityProperties(attacked, Float.valueOf(damage), (prop, dmg) -> {
               dmg = prop.getRaceHandler().isDamaged(source, dmg.floatValue());
               if (dmg.floatValue() > 0.0F) {
                  Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

                  for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                     String key = (String)entry.getKey();
                     AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                     try {
                        IAbilityInterface ability = value.getAbility();
                        if (ability instanceof IAttackAbility) {
                           dmg = ((IAttackAbility)ability).damaged(attacked, source, dmg.floatValue());
                           if (dmg.floatValue() <= 0.0F) {
                              break;
                           }
                        }
                     } catch (Exception var10) {
                        Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                        var10.printStackTrace();
                     }
                  }
               }

               return dmg;
            })).floatValue();
         }

         if (damage <= 0.0F) {
            this.cancelEvent(event);
         } else {
            event.setAmount(damage);
         }

      }
   }

   @SubscribeEvent
   public void onDeathEvent(LivingDeathEvent event) {
      DamageSource source = event.getSource();
      EntityLivingBase attacked = event.getEntityLiving();
      if (!(attacked instanceof EntityPlayer) || !attacked.world.isRemote) {
         if (source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            Capabilities.getEntityProperties(attacker, (prop) -> {
               Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

               for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
                  String key = (String)entry.getKey();
                  AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

                  try {
                     IAbilityInterface ability = value.getAbility();
                     if (ability instanceof IAttackAbility) {
                        ((IAttackAbility)ability).killedEntity(attacked, source);
                     }
                  } catch (Exception var9) {
                     Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                     var9.printStackTrace();
                  }
               }

            });
         }

         Capabilities.getEntityProperties(attacked, (prop) -> {
            boolean notDead = false;
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

               try {
                  IAbilityInterface ability = value.getAbility();
                  if (ability instanceof IAttackAbility) {
                     notDead = ((IAttackAbility)ability).died(attacked, source, notDead);
                     if (notDead) {
                        break;
                     }
                  }
               } catch (Exception var12) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var12.printStackTrace();
               }
            }

            if (notDead) {
               this.cancelEvent(event);
            }

         });
      }
   }

   @SubscribeEvent
   public void experienceDropEvent(LivingExperienceDropEvent event) {
      EntityLivingBase attacked = event.getEntityLiving();

      try {
         NBTTagCompound nbt = attacked.getEntityData();
         if (nbt != null && nbt.hasKey("xat:summoned")) {
            event.setDroppedExperience(0);
            return;
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      EntityLivingBase attacker = event.getAttackingPlayer();
      Capabilities.getEntityProperties(attacker, (prop) -> {
         int originalExp = event.getOriginalExperience();
         int originalDropped = event.getDroppedExperience();
         int droppedExp = originalDropped;
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof IAttackAbility) {
                  droppedExp = ((IAttackAbility)ability).killedEntityExpDrop(attacked, originalExp, droppedExp);
                  if (droppedExp <= 0) {
                     break;
                  }
               }
            } catch (Exception var13) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var13.printStackTrace();
            }
         }

         if (originalDropped != droppedExp) {
            if (droppedExp > 0) {
               event.setDroppedExperience(droppedExp);
            } else if (!this.isEventCanceled(event)) {
               this.cancelEvent(event);
            }
         }

      });
   }

   @SubscribeEvent
   public void ItemDropEvent(LivingDropsEvent event) {
      EntityLivingBase attacked = event.getEntityLiving();

      try {
         NBTTagCompound nbt = attacked.getEntityData();
         if (nbt != null && nbt.hasKey("xat:summoned")) {
            if (!event.getDrops().isEmpty()) {
               event.getDrops().clear();
            }

            return;
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      DamageSource source = event.getSource();
      if (source.getTrueSource() instanceof EntityLivingBase) {
         EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
         Capabilities.getEntityProperties(attacker, (prop) -> {
            Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

            for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
               String key = (String)entry.getKey();
               AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

               try {
                  IAbilityInterface ability = value.getAbility();
                  if (ability instanceof IAttackAbility) {
                     ((IAttackAbility)ability).killedEntityItemDrops(attacked, source, event.getLootingLevel(), event.getDrops());
                  }
               } catch (Exception var10) {
                  Trinkets.log.error("Trinkets had an Error with Ability:" + key);
                  var10.printStackTrace();
               }
            }

         });
      }

   }

   @SubscribeEvent
   public void healEvent(LivingHealEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      Capabilities.getEntityProperties(entity, (prop) -> {
         float amount = event.getAmount();
         float finalAmount = amount;
         Map<String, AbilityHandler.AbilityHolder> abilities = prop.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder value = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = value.getAbility();
               if (ability instanceof IHealAbility) {
                  finalAmount = ((IHealAbility)ability).onHeal(entity, finalAmount);
               }
            } catch (Exception var12) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var12.printStackTrace();
            }
         }

         finalAmount = prop.getRaceHandler().onHeal(finalAmount);
         if (amount != finalAmount) {
            if (finalAmount > 0.0F) {
               event.setAmount(finalAmount);
            } else if (!this.isEventCanceled(event)) {
               this.cancelEvent(event);
            }
         }

      });
   }
}
