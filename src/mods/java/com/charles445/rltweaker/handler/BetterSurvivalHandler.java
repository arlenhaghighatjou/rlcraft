package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.BetterSurvivalReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import java.lang.reflect.InvocationTargetException;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class BetterSurvivalHandler {
   private final EaglercraftUUID UUID_BLINDNESS = EaglercraftUUID.fromString("a6107045-134f-4c14-a645-75c3ae5c7a27");
   private final EaglercraftUUID UUID_COMPENSATION = EaglercraftUUID.fromString("a6107045-134f-4c04-a645-75c3ae5c7a27");
   private final EaglercraftUUID UUID_SPEED = EaglercraftUUID.fromString("e6107045-134f-4c54-a645-75c3ae5c7a27");
   BetterSurvivalReflect reflector;

   public BetterSurvivalHandler() {
      try {
         this.reflector = new BetterSurvivalReflect();
         CompatUtil.wrapSpecificHandler("BSBreak", (x$0) -> {
            new BetterSurvivalHandler.BSBreak(x$0);
         }, "com.mujmajnkraft.bettersurvival.eventhandlers.ModEnchantmentHandler", "onEvent(Lnet/minecraftforge/event/world/BlockEvent$BreakEvent;)");
         CompatUtil.wrapSpecificHandler("BSComboBlindness", (x$0) -> {
            new BetterSurvivalHandler.BSComboBlindness(x$0);
         }, "com.mujmajnkraft.bettersurvival.eventhandlers.ModWeaponHandler", "onEvent(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)");
         CompatUtil.wrapSpecificHandler("BSArrowSpawn", (x$0) -> {
            new BetterSurvivalHandler.BSArrowSpawn(x$0);
         }, "com.mujmajnkraft.bettersurvival.eventhandlers.ModEnchantmentHandler", "onEvent(Lnet/minecraftforge/event/entity/EntityJoinWorldEvent;)");
         CompatUtil.wrapSpecificHandler("BSEnchantment", (x$0) -> {
            new BetterSurvivalHandler.BSEnchantment(x$0);
         }, "com.mujmajnkraft.bettersurvival.eventhandlers.ModEnchantmentHandler", "onEvent(Lnet/minecraftforge/event/entity/living/LivingEvent$LivingUpdateEvent;)");
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup BetterSurvivalHandler!", var2);
         ErrorUtil.logSilent("Better Survival Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @Nullable
   private Enchantment getEnchantmentByName(String name) {
      return (Enchantment)Enchantment.REGISTRY.getObject(new ResourceLocation("mujmajnkraftsbettersurvival", name));
   }

   public class BSArrowSpawn {
      private IEventListener handler;

      public BSArrowSpawn(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST,
         receiveCanceled = true
      )
      public void onArrowJoinWorld(EntityJoinWorldEvent event) {
         double rangeSpeedMultiplier = ModConfig.server.bettersurvival.rangeSpeedMultiplier;
         if (rangeSpeedMultiplier == 2.0D) {
            this.handler.invoke(event);
         } else {
            if (event.getEntity() instanceof EntityArrow && !event.getWorld().isRemote) {
               EntityArrow arrowEntity = (EntityArrow)event.getEntity();
               if (arrowEntity.shootingEntity instanceof EntityLivingBase) {
                  double cachedX = arrowEntity.motionX;
                  double cachedY = arrowEntity.motionY;
                  double cachedZ = arrowEntity.motionZ;
                  this.handler.invoke(event);
                  if (cachedX != arrowEntity.motionX || cachedY != arrowEntity.motionY || cachedZ != arrowEntity.motionZ) {
                     arrowEntity.motionX = cachedX * rangeSpeedMultiplier;
                     arrowEntity.motionY = cachedY * rangeSpeedMultiplier;
                     arrowEntity.motionZ = cachedZ * rangeSpeedMultiplier;
                  }
               }
            }

         }
      }
   }

   public class BSBreak {
      @Nullable
      private Enchantment tunneling;
      private boolean disabled = false;
      private IEventListener handler;

      public BSBreak(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public void onBreakLowest(BreakEvent event) {
         if (ModConfig.server.bettersurvival.tunnelingCancelable) {
            this.onBreakAny(event);
         }
      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST,
         receiveCanceled = true
      )
      public void onBreakHighCancel(BreakEvent event) {
         if (!ModConfig.server.bettersurvival.tunnelingCancelable) {
            this.onBreakAny(event);
         }
      }

      public void onBreakAny(BreakEvent event) {
         if (this.disabled) {
            this.handler.invoke(event);
         } else {
            if (this.tunneling == null) {
               this.tunneling = BetterSurvivalHandler.this.getEnchantmentByName("tunneling");
               if (this.tunneling == null) {
                  RLTweaker.logger.warn("Couldn't find tunneling enchantment");
                  ErrorUtil.logSilent("Better Survival Missing Enchantment Tunneling");
                  this.disabled = true;
                  this.handler.invoke(event);
                  return;
               }
            }

            EntityPlayer player = event.getPlayer();
            if (EnchantmentHelper.getMaxEnchantmentLevel(this.tunneling, player) != 0) {
               World world = event.getWorld();
               int tunnelingLevel = MathHelper.clamp(EnchantmentHelper.getMaxEnchantmentLevel(this.tunneling, player), 0, 200);
               boolean tagNorth = player.getTags().contains("north");
               boolean tagEast = player.getTags().contains("east");
               boolean tagSouth = player.getTags().contains("south");
               boolean tagWest = player.getTags().contains("west");
               boolean tagUp = player.getTags().contains("up");
               boolean tagDown = player.getTags().contains("down");
               boolean isBlacklistEnabled = ModConfig.server.bettersurvival.tunnelingBlacklistEnabled;
               boolean isWhitelist = ModConfig.server.bettersurvival.tunnelingBlacklistIsWhitelist;
               String[] blacklist = ModConfig.server.bettersurvival.tunnelingBlacklist;

               for(int x = -tunnelingLevel; x <= tunnelingLevel; ++x) {
                  if (!tagWest && !tagEast || x == 0) {
                     for(int y = -tunnelingLevel; y <= tunnelingLevel; ++y) {
                        if (!tagUp && !tagDown || y == 0) {
                           for(int z = -tunnelingLevel; z <= tunnelingLevel; ++z) {
                              if (!tagNorth && !tagSouth || z == 0) {
                                 boolean isInRadius = Math.sqrt((double)(x * x + y * y + z * z)) <= (double)(((float)tunnelingLevel + 1.0F) / 2.0F);
                                 boolean isNotCenter = x != 0 || y != 0 || z != 0;
                                 if (isInRadius && isNotCenter) {
                                    BlockPos newpos = event.getPos().add(x, y, z);
                                    if (!ModConfig.server.bettersurvival.tunnelingBreaksTileEntities && world.getTileEntity(newpos) != null) {
                                       return;
                                    }

                                    Block block = world.getBlockState(newpos).getBlock();
                                    if (block != Blocks.AIR && isBlacklistEnabled) {
                                       String blockRegistry = block.getRegistryName().toString();
                                       boolean inBlacklist = false;

                                       for(String entry : blacklist) {
                                          if (entry.equals(blockRegistry)) {
                                             inBlacklist = true;
                                             break;
                                          }
                                       }

                                       if (inBlacklist != isWhitelist) {
                                          return;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               this.handler.invoke(event);
            }
         }
      }
   }

   public class BSComboBlindness {
      private IEventListener handler;
      private boolean replaceComboHandler;

      public BSComboBlindness(IEventListener handler) {
         this.handler = handler;
         this.replaceComboHandler = ModConfig.server.bettersurvival.replaceComboHandler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      private void replacedComboHandler(LivingUpdateEvent event) {
         if (event.getEntityLiving() instanceof EntityLiving) {
            EntityLiving living = (EntityLiving)event.getEntityLiving();
            living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).removeModifier(BetterSurvivalHandler.this.UUID_BLINDNESS);
            if (living.getActivePotionEffect(MobEffects.BLINDNESS) != null) {
               AttributeModifier modifier = new AttributeModifier(BetterSurvivalHandler.this.UUID_BLINDNESS, "blind", -0.8D, 1);
               living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(modifier);
            }
         } else if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(BetterSurvivalHandler.this.UUID_COMPENSATION);
            if (BetterSurvivalHandler.this.reflector.hasINunchakuCombo(player)) {
               Object combo = BetterSurvivalHandler.this.reflector.getINunchakuCombo(player);

               try {
                  if (BetterSurvivalHandler.this.reflector.c_ItemNunchaku.isInstance(player.getHeldItemMainhand().getItem())) {
                     if (BetterSurvivalHandler.this.reflector.getComboPower(combo) > 0.0F) {
                        BetterSurvivalHandler.this.reflector.countDown(combo);
                     }
                  } else {
                     BetterSurvivalHandler.this.reflector.setComboTime(combo, 0);
                  }
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
                  ErrorUtil.logSilent("BetterSurvival Nunchaku Combo Invocation");
               }
            }
         }

      }

      @SubscribeEvent(
         priority = EventPriority.NORMAL,
         receiveCanceled = true
      )
      public void onLivingUpdate(LivingUpdateEvent event) {
         if (this.replaceComboHandler) {
            this.replacedComboHandler(event);
         } else {
            this.handler.invoke(event);
         }

         if (event.getEntityLiving() instanceof EntityLiving) {
            IAttributeInstance follow_range = ((EntityLiving)event.getEntityLiving()).getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
            if (follow_range.getModifier(BetterSurvivalHandler.this.UUID_BLINDNESS) != null) {
               if (!ModConfig.server.bettersurvival.mobBlindness) {
                  follow_range.removeModifier(BetterSurvivalHandler.this.UUID_BLINDNESS);
                  return;
               }

               EntityEntry entityEntry = EntityRegistry.getEntry(event.getEntityLiving().getClass());
               if (entityEntry != null) {
                  String mobType = entityEntry.getRegistryName().toString();
                  String[] mobBlacklist = ModConfig.server.bettersurvival.mobBlindnessBlacklist;

                  for(int i = 0; i < mobBlacklist.length; ++i) {
                     if (mobType.equals(mobBlacklist[i])) {
                        follow_range.removeModifier(BetterSurvivalHandler.this.UUID_BLINDNESS);
                        return;
                     }
                  }
               }

               double sightValue = ModConfig.server.bettersurvival.mobBlindnessPercentage;
               if (sightValue != 80.0D) {
                  follow_range.removeModifier(BetterSurvivalHandler.this.UUID_BLINDNESS);
                  sightValue = sightValue * -0.01D;
                  follow_range.applyModifier(new AttributeModifier(BetterSurvivalHandler.this.UUID_BLINDNESS, "blind", sightValue, 1));
               }
            }
         }

      }
   }

   public class BSEnchantment {
      private IEventListener handler;
      private boolean replaceEnchantment;

      public BSEnchantment(IEventListener handler) {
         this.handler = handler;
         this.replaceEnchantment = ModConfig.server.bettersurvival.replaceEnchantmentHandler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      private void replacedEnchantment(LivingUpdateEvent event) {
         EntityLivingBase living = event.getEntityLiving();
         if (BetterSurvivalHandler.this.reflector.o_ConfigHandler_agilitylevel > 0) {
            int agilityLevel = EnchantmentHelper.getMaxEnchantmentLevel(BetterSurvivalHandler.this.reflector.o_ModEnchantments_agility, living);
            if (agilityLevel > 0) {
               IAttributeInstance movement_speed = living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
               double strength = 0.01D * (double)agilityLevel;
               AttributeModifier modifier = new AttributeModifier(BetterSurvivalHandler.this.UUID_SPEED, "agility", strength, 0);
               if (!movement_speed.hasModifier(modifier)) {
                  movement_speed.applyModifier(modifier);
               } else if (movement_speed.getModifier(BetterSurvivalHandler.this.UUID_SPEED).getAmount() != strength) {
                  movement_speed.removeModifier(BetterSurvivalHandler.this.UUID_SPEED);
                  movement_speed.applyModifier(modifier);
               }
            } else {
               living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(BetterSurvivalHandler.this.UUID_SPEED);
            }
         } else {
            living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(BetterSurvivalHandler.this.UUID_SPEED);
         }

         if (BetterSurvivalHandler.this.reflector.o_ConfigHandler_vitalitylevel > 0 && living instanceof EntityPlayer) {
            int vitalityLevel = EnchantmentHelper.getMaxEnchantmentLevel(BetterSurvivalHandler.this.reflector.o_ModEnchantments_vitality, living);
            if (vitalityLevel > 0) {
               EntityPlayer player = (EntityPlayer)living;
               if (player.getFoodStats().getFoodLevel() >= 18 && player.getEntityWorld().getGameRules().getBoolean("naturalRegeneration") && player.getHealth() < player.getMaxHealth() && player.ticksExisted % (40 / vitalityLevel) == 0) {
                  player.heal(1.0F);
               }
            }
         }

      }

      @SubscribeEvent(
         priority = EventPriority.HIGHEST,
         receiveCanceled = true
      )
      public void onLivingUpdate(LivingUpdateEvent event) {
         if (this.replaceEnchantment) {
            this.replacedEnchantment(event);
         } else {
            this.handler.invoke(event);
         }

      }
   }
}
