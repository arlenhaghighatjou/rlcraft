package com.tmtravlr.potioncore;

import com.tmtravlr.potioncore.network.SToCMessage;
import com.tmtravlr.potioncore.potion.PotionAntidote;
import com.tmtravlr.potioncore.potion.PotionDrown;
import com.tmtravlr.potioncore.potion.PotionFlight;
import com.tmtravlr.potioncore.potion.PotionPotionSickness;
import com.tmtravlr.potioncore.potion.PotionPurity;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import com.tmtravlr.potioncore.potion.PotionRevival;
import com.tmtravlr.potioncore.potion.PotionSlowfall;
import com.tmtravlr.potioncore.potion.PotionStepup;
import com.tmtravlr.potioncore.potion.PotionTeleportSpawn;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.function.Consumer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(
   modid = "potioncore"
)
public class PotionCoreEventHandler {
   private static final Object INVUL_RESET_LOCK = new Object();

   public PotionCoreEventHandler() {
   }

   @SubscribeEvent
   public static void onRegisterPotions(Register<Potion> event) {
      IForgeRegistry<Potion> registry = event.getRegistry();
      PotionCoreEffects.loadPotionEffects(registry);
   }

   @SubscribeEvent
   public static void onRegisterPotionTypes(Register<PotionType> event) {
      IForgeRegistry<PotionType> registry = event.getRegistry();
      PotionCoreTypes.loadPotionTypes(registry);
   }

   @SubscribeEvent
   public static void onEntityConstructing(EntityConstructing event) {
      if (event.getEntity() instanceof EntityLivingBase) {
         ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.PROJECTILE_DAMAGE);
         ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.MAGIC_DAMAGE);
         ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.JUMP_HEIGHT);
         ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.DAMAGE_RESISTANCE);
         ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.MAGIC_SHIELDING);
         if (event.getEntity() instanceof EntityPlayer) {
            ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.STEP_HEIGHT);
            ((EntityLivingBase)event.getEntity()).getAttributeMap().registerAttribute(PotionCoreAttributes.DIG_SPEED);
         }
      }

   }

   @SubscribeEvent
   public static void onLivingJump(LivingJumpEvent event) {
      if (!event.getEntity().world.isRemote || PotionCoreHelper.hasServer) {
         if (ConfigLoader.controllableJumpBoost) {
            double jumpAttribute = event.getEntityLiving().getEntityAttribute(PotionCoreAttributes.JUMP_HEIGHT).getAttributeValue();
            if (event.getEntityLiving().getActivePotionEffect(MobEffects.JUMP_BOOST) != null) {
               double jumpBoost = (double)(event.getEntityLiving().getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1);
               EntityLivingBase var10000 = event.getEntityLiving();
               var10000.motionY -= jumpBoost / 10.0D;
            }

            if (event.getEntityLiving() instanceof EntityPlayer && jumpAttribute > 1.0D) {
               if (event.getEntityLiving().world.isRemote) {
                  event.getEntityLiving().getEntityData().setDouble("Potion Core - Jump Boost Height", jumpAttribute);
                  event.getEntityLiving().getEntityData().setDouble("Potion Core - Jump Boost Base", event.getEntityLiving().posY);
                  event.getEntityLiving().getEntityData().setBoolean("Potion Core - Jump Boost Jumping", true);
               }
            } else {
               event.getEntityLiving().motionY = Math.sqrt(0.1764D * jumpAttribute);
            }
         } else {
            float jump = (float)PotionCoreAttributes.getAdjustedJumpHeightAttribute(event.getEntityLiving()) - 1.0F;
            EntityLivingBase var6 = event.getEntityLiving();
            var6.motionY += (double)jump / 10.0D;
         }

      }
   }

   @SubscribeEvent
   public static void onPlayerDig(BreakSpeed event) {
      if (!event.getEntity().world.isRemote || PotionCoreHelper.hasServer) {
         float speed = event.getNewSpeed();
         AttributeModifier tempModifier = new AttributeModifier(PotionCoreHelper.TEMP_UUID, "Base", PotionCoreAttributes.getAdjustedDigSpeed(event.getEntityPlayer(), (double)speed) - 1.0D, 0);
         event.getEntityPlayer().getEntityAttribute(PotionCoreAttributes.DIG_SPEED).applyModifier(tempModifier);
         speed = (float)event.getEntityPlayer().getEntityAttribute(PotionCoreAttributes.DIG_SPEED).getAttributeValue();
         event.getEntityPlayer().getEntityAttribute(PotionCoreAttributes.DIG_SPEED).removeModifier(tempModifier);
         event.setNewSpeed(speed);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onLivingFall(LivingFallEvent event) {
      if (!event.getEntity().world.isRemote || PotionCoreHelper.hasServer) {
         float jump = (float)PotionCoreAttributes.getAdjustedJumpHeightAttribute(event.getEntityLiving());
         if (jump > 0.0F) {
            event.setDistance(event.getDistance() - jump);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onLivingHurt(LivingHurtEvent event) {
      if (!event.getEntity().world.isRemote || PotionCoreHelper.hasServer) {
         if (!event.getSource().isDamageAbsolute()) {
            float multiplier = 1.0F;
            if (event.getSource().isProjectile() && event.getSource().getTrueSource() instanceof EntityLivingBase) {
               EntityLivingBase source = (EntityLivingBase)event.getSource().getTrueSource();
               AttributeModifier tempModifier = new AttributeModifier(PotionCoreHelper.TEMP_UUID, "Base", (double)event.getAmount() - 1.0D, 0);
               source.getEntityAttribute(PotionCoreAttributes.PROJECTILE_DAMAGE).applyModifier(tempModifier);
               event.setAmount((float)source.getEntityAttribute(PotionCoreAttributes.PROJECTILE_DAMAGE).getAttributeValue());
               source.getEntityAttribute(PotionCoreAttributes.PROJECTILE_DAMAGE).removeModifier(tempModifier);
            }

            if (event.getSource().isMagicDamage() && event.getSource().getTrueSource() instanceof EntityLivingBase) {
               EntityLivingBase source = (EntityLivingBase)event.getSource().getTrueSource();
               AttributeModifier tempModifier = new AttributeModifier(PotionCoreHelper.TEMP_UUID, "Base", (double)event.getAmount() - 1.0D, 0);
               source.getEntityAttribute(PotionCoreAttributes.MAGIC_DAMAGE).applyModifier(tempModifier);
               event.setAmount((float)source.getEntityAttribute(PotionCoreAttributes.MAGIC_DAMAGE).getAttributeValue());
               source.getEntityAttribute(PotionCoreAttributes.MAGIC_DAMAGE).removeModifier(tempModifier);
            }

            if (event.getSource() == DamageSource.FALL && PotionSlowfall.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionSlowfall.INSTANCE)) {
               int level = event.getEntityLiving().getActivePotionEffect(PotionSlowfall.INSTANCE).getAmplifier();
               if (level > 0) {
                  event.setAmount(-1.0F);
                  return;
               }

               event.setAmount(event.getAmount() * 0.5F);
               if (event.getAmount() > 4.0F) {
                  event.setAmount(4.0F);
               }
            }

            double resistance = PotionCoreAttributes.getAdjustedDamageResistanceAttribute(event.getEntityLiving());
            multiplier = (float)((double)multiplier * (1.0D - resistance));
            if (event.getSource().isMagicDamage()) {
               double magicShield = event.getEntityLiving().getEntityAttribute(PotionCoreAttributes.MAGIC_SHIELDING).getAttributeValue();
               multiplier = (float)((double)multiplier * (1.0D - magicShield / 25.0D));
            }

            event.setAmount(event.getAmount() * multiplier);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLivingHurtLow(LivingHurtEvent event) {
      if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
         EntityLivingBase source = (EntityLivingBase)event.getSource().getTrueSource();
         if (PotionRecoil.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionRecoil.INSTANCE) && (!(event.getSource() instanceof EntityDamageSource) || !((EntityDamageSource)event.getSource()).getIsThornsDamage())) {
            float reflectPercent = MathHelper.clamp((float)(event.getEntityLiving().getActivePotionEffect(PotionRecoil.INSTANCE).getAmplifier() + 1) * PotionRecoil.reflectDamage, PotionRecoil.reflectDamage, 9.0F * PotionRecoil.reflectDamage);
            source.attackEntityFrom(DamageSource.causeThornsDamage(source), event.getAmount() * reflectPercent);
         }
      }

      if (ConfigLoader.separateInvulTimers) {
         synchronized(INVUL_RESET_LOCK) {
            if (event.getSource().isProjectile()) {
               PotionCoreHelper.invulResetEntities.add(event.getEntityLiving());
               if (event.getEntityLiving().getEntityData().getInteger("Potion Core - Projectile Invulnerability") <= 0) {
                  event.getEntityLiving().getEntityData().setInteger("Potion Core - Projectile Invulnerability", event.getEntityLiving().maxHurtResistantTime / 2);
               }
            }

            if (event.getSource().isMagicDamage()) {
               PotionCoreHelper.invulResetEntities.add(event.getEntityLiving());
               if (event.getEntityLiving().getEntityData().getInteger("Potion Core - Magic Invulnerability") <= 0) {
                  event.getEntityLiving().getEntityData().setInteger("Potion Core - Magic Invulnerability", event.getEntityLiving().maxHurtResistantTime / 2);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public static void onLivingAttack(LivingAttackEvent event) {
      checkInvulReset();
      if (ConfigLoader.separateInvulTimers) {
         synchronized(INVUL_RESET_LOCK) {
            if (event.getSource().isProjectile()) {
               PotionCoreHelper.invulResetEntities.add(event.getEntityLiving());
               if (event.getEntityLiving().getEntityData().getInteger("Potion Core - Projectile Invulnerability") > 0) {
                  event.setCanceled(true);
                  return;
               }
            }

            if (event.getSource().isMagicDamage()) {
               PotionCoreHelper.invulResetEntities.add(event.getEntityLiving());
               if (event.getEntityLiving().getEntityData().getInteger("Potion Core - Magic Invulnerability") > 0) {
                  event.setCanceled(true);
                  return;
               }
            }
         }
      }

   }

   @SubscribeEvent
   public static void onLivingUpdate(LivingUpdateEvent event) {
      if (!event.getEntity().world.isRemote) {
         event.getEntityLiving().getEntityData().setFloat("Potion Core - Health Fix", event.getEntityLiving().getHealth());
      }

      if (ConfigLoader.separateInvulTimers) {
         int invulTimer = event.getEntityLiving().getEntityData().getInteger("Potion Core - Projectile Invulnerability");
         if (invulTimer > 0) {
            event.getEntityLiving().getEntityData().setInteger("Potion Core - Projectile Invulnerability", invulTimer - 1);
         }

         invulTimer = event.getEntityLiving().getEntityData().getInteger("Potion Core - Magic Invulnerability");
         if (invulTimer > 0) {
            event.getEntityLiving().getEntityData().setInteger("Potion Core - Magic Invulnerability", invulTimer - 1);
         }
      }

      if (PotionDrown.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionDrown.INSTANCE)) {
         if (event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof EntityPlayerMP && event.getEntityLiving().ticksExisted % 20 == 0) {
            PacketBuffer out = new PacketBuffer(Unpooled.buffer());
            out.writeInt(1);
            out.writeInt(event.getEntity().getEntityData().getInteger("potioncore - drown air"));
            SToCMessage packet = new SToCMessage(out);
            PotionCore.networkWrapper.sendTo(packet, (EntityPlayerMP)event.getEntityLiving());
         }

         event.getEntity().getEntityData().setBoolean("potioncore - doing drown", true);
      } else if (event.getEntity().getEntityData().getBoolean("potioncore - doing drown")) {
         event.getEntity().getEntityData().setInteger("potioncore - drown air", 300);
      }

      if (PotionAntidote.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionAntidote.INSTANCE)) {
         event.getEntityLiving().removePotionEffect(MobEffects.POISON);
      }

      if (PotionPurity.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionPurity.INSTANCE)) {
         event.getEntityLiving().removePotionEffect(MobEffects.WITHER);
      }

      if (PotionSlowfall.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionSlowfall.INSTANCE) && event.getEntityLiving().getActivePotionEffect(PotionSlowfall.INSTANCE).getAmplifier() > 0) {
         event.getEntity().fallDistance = 0.0F;
      }

      if (ConfigLoader.fixBlindness && event.getEntityLiving().isPotionActive(MobEffects.BLINDNESS) && event.getEntityLiving() instanceof EntityLiving && ((EntityLiving)event.getEntityLiving()).getAttackTarget() != null) {
         int effectLevel = 3 - event.getEntityLiving().getActivePotionEffect(MobEffects.BLINDNESS).getAmplifier();
         if (effectLevel < 0) {
            effectLevel = 0;
         }

         if (event.getEntityLiving().getDistance(((EntityLiving)event.getEntityLiving()).getAttackTarget()) > (float)effectLevel) {
            ((EntityLiving)event.getEntityLiving()).setAttackTarget((EntityLivingBase)null);
         }
      }

      if (PotionPotionSickness.INSTANCE.isEnabled() && event.getEntityLiving().ticksExisted % 8 == 0) {
         Collection<PotionEffect> potionEffects = event.getEntityLiving().getActivePotionEffects();
         int positivePotions = 0;

         for(PotionEffect potionEffect : potionEffects) {
            if (potionEffect.getPotion() != null && !potionEffect.getPotion().isBadEffect()) {
               ++positivePotions;
            }
         }

         if (positivePotions >= PotionPotionSickness.potionsForSickness) {
            int amplifier = positivePotions - PotionPotionSickness.potionsForSickness;
            event.getEntityLiving().addPotionEffect(new PotionEffect(PotionPotionSickness.INSTANCE, 10, amplifier));
         }
      }

      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         NBTTagCompound persisted = player.getEntityData().getCompoundTag("PlayerPersisted");
         if (!player.getEntityData().hasKey("PlayerPersisted")) {
            player.getEntityData().setTag("PlayerPersisted", persisted);
         }

         if (player.ticksExisted % 11 == 0 && PotionStepup.INSTANCE.isEnabled()) {
            if (!persisted.getBoolean("potion core - step_up")) {
               if (player.getEntityAttribute(PotionCoreAttributes.STEP_HEIGHT).getAttributeValue() != PotionCoreAttributes.STEP_HEIGHT.getDefaultValue()) {
                  persisted.setBoolean("potion core - step_up", true);
               }
            } else {
               player.stepHeight = (float)player.getEntityAttribute(PotionCoreAttributes.STEP_HEIGHT).getAttributeValue();
               if (player.getEntityAttribute(PotionCoreAttributes.STEP_HEIGHT).getAttributeValue() == PotionCoreAttributes.STEP_HEIGHT.getDefaultValue()) {
                  persisted.setBoolean("potion core - step_up", false);
               }
            }
         }

         if (PotionFlight.INSTANCE.isEnabled() && player.isPotionActive(PotionFlight.INSTANCE)) {
            if (!persisted.getBoolean("potion core - flight")) {
               persisted.setBoolean("potion core - flight", true);
            }

            player.capabilities.allowFlying = true;
         } else if (persisted.getBoolean("potion core - flight")) {
            persisted.setBoolean("potion core - flight", false);
            player.fallDistance = 0.0F;
            if (!player.capabilities.isCreativeMode) {
               player.capabilities.isFlying = false;
               player.capabilities.allowFlying = false;
            }
         }

         if (PotionTeleportSpawn.INSTANCE.isEnabled() && player.isPotionActive(PotionTeleportSpawn.INSTANCE)) {
            int spawnDelay = persisted.getInteger("potion core - spawn teleport");
            if (Math.abs(persisted.getDouble("potion core - spawn teleport x") - player.posX) > 0.01D || Math.abs(persisted.getDouble("potion core - spawn teleport y") - player.posY) > 0.01D || Math.abs(persisted.getDouble("potion core - spawn teleport z") - player.posZ) > 0.01D) {
               persisted.setDouble("potion core - spawn teleport x", player.posX);
               persisted.setDouble("potion core - spawn teleport y", player.posY);
               persisted.setDouble("potion core - spawn teleport z", player.posZ);
               spawnDelay = 0;
            }

            if (spawnDelay++ >= PotionTeleportSpawn.teleportDelay * 20) {
               double initialX = player.posX;
               double initialY = player.posY;
               double initialZ = player.posZ;
               if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                  int dimension = player.world.provider.getDimension();
                  World world = player.world.getMinecraftServer().getWorld(dimension);
                  if (world == null) {
                     dimension = 0;
                  } else if (!world.provider.canRespawnHere()) {
                     dimension = world.provider.getRespawnDimension((EntityPlayerMP)player);
                  }

                  world = player.world.getMinecraftServer().getWorld(dimension);
                  BlockPos blockpos = player.getBedLocation(dimension);
                  if (blockpos != null) {
                     blockpos = EntityPlayer.getBedSpawnLocation(world, blockpos, player.isSpawnForced(dimension));
                  }

                  if (blockpos == null) {
                     blockpos = world.getSpawnPoint();
                     blockpos = world.getTopSolidOrLiquidBlock(blockpos);
                  }

                  PotionCoreTeleporter.teleportPlayer((EntityPlayerMP)player, (EnumSet)null, world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.1D, (double)blockpos.getZ() + 0.5D);
               }

               persisted.setInteger("potion core - spawn teleport", 0);
               player.removePotionEffect(PotionTeleportSpawn.INSTANCE);
               if (!player.world.isRemote) {
                  int maxParticles = 128;

                  for(int i = 0; i < maxParticles; ++i) {
                     double scale = (double)i / ((double)maxParticles - 1.0D);
                     double motion = (double)((player.getRNG().nextFloat() - 0.5F) * 0.5F);
                     double posX = initialX + (player.posX - initialX) * scale + (player.getRNG().nextDouble() - 0.5D) * (double)player.width * 2.0D;
                     double posY = initialY + (player.posY - initialY) * scale + player.getRNG().nextDouble() * (double)player.height;
                     double posZ = initialZ + (player.posZ - initialZ) * scale + (player.getRNG().nextDouble() - 0.5D) * (double)player.width * 2.0D;
                     spawnParticle(player.world, EnumParticleTypes.PORTAL, posX, posY, posZ, motion);
                  }

                  player.world.playSound((EntityPlayer)null, initialX, initialY, initialZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
               }
            } else {
               int particles = spawnDelay == 1 ? 0 : MathHelper.ceil((double)spawnDelay / 10.0D);

               for(int i = 0; i < particles; ++i) {
                  spawnParticle(player.world, EnumParticleTypes.FIREWORKS_SPARK, player.posX + (double)(player.getRNG().nextFloat() * 2.0F) - 1.0D, player.posY + (double)(player.getRNG().nextFloat() * 8.0F), player.posZ + (double)(player.getRNG().nextFloat() * 2.0F) - 1.0D, 0.0D);
               }

               persisted.setInteger("potion core - spawn teleport", spawnDelay);
            }
         } else if (persisted.getInteger("potion core - spawn teleport") > 0) {
            persisted.setInteger("potion core - spawn teleport", 0);
         }

         if (player.world.isRemote && event.getEntityLiving().getEntityData().getBoolean("Potion Core - Jump Boost Jumping")) {
            boolean isJumping = ((Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, player, new String[]{"isJumping", "isJumping"})).booleanValue();
            double height = player.getEntityData().getDouble("Potion Core - Jump Boost Height");
            double base = player.getEntityData().getDouble("Potion Core - Jump Boost Base");
            if (isJumping && player.posY > player.prevPosY && player.posY - base + 1.0D < height) {
               player.motionY = 0.44999998807907104D;
            } else {
               event.getEntityLiving().getEntityData().setBoolean("Potion Core - Jump Boost Jumping", false);
            }
         }
      }

   }

   private static void spawnParticle(World world, EnumParticleTypes type, double x, double y, double z, double motion) {
      if (world instanceof WorldServer) {
         WorldServer worldServer = (WorldServer)world;
         worldServer.spawnParticle(type, false, x, y, z, 1, 0.0D, 0.0D, 0.0D, motion, new int[0]);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onLivingDeath(LivingDeathEvent event) {
      if (PotionRevival.INSTANCE.isEnabled() && event.getEntityLiving().isPotionActive(PotionRevival.INSTANCE)) {
         int level = event.getEntityLiving().getActivePotionEffect(PotionRevival.INSTANCE).getAmplifier() + 1;
         event.setCanceled(true);
         event.getEntityLiving().setHealth(PotionRevival.reviveHealth * (float)level);
         event.getEntityLiving().world.playSound((EntityPlayer)null, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, event.getEntityLiving().getSoundCategory(), 1.0F, 0.6F);
         PacketBuffer out = new PacketBuffer(Unpooled.buffer());
         out.writeInt(3);
         out.writeInt(event.getEntityLiving().getEntityId());
         SToCMessage packet = new SToCMessage(out);
         PotionCore.networkWrapper.sendToAllAround(packet, new TargetPoint(event.getEntityLiving().world.provider.getDimension(), event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, 16.0D));
         event.getEntityLiving().removePotionEffect(PotionRevival.INSTANCE);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
      if (event.getEntity() instanceof EntityLiving) {
         EntityLiving entity = (EntityLiving)event.getEntity();
         if (entity.getAttackTarget() != null && event.getTarget() != null && ConfigLoader.fixBlindness && entity.isPotionActive(MobEffects.BLINDNESS)) {
            int effectLevel = 3 - entity.getActivePotionEffect(MobEffects.BLINDNESS).getAmplifier();
            if (effectLevel < 0) {
               effectLevel = 0;
            }

            if (entity.getDistance(event.getTarget()) > (float)effectLevel) {
               entity.setAttackTarget((EntityLivingBase)null);
            }
         }
      }

   }

   @SubscribeEvent
   public static void onPlayerLogIn(PlayerLoggedInEvent event) {
      if (event.player instanceof EntityPlayerMP) {
         PacketBuffer out = new PacketBuffer(Unpooled.buffer());
         out.writeInt(4);
         PotionCore.networkWrapper.sendTo(new SToCMessage(out), (EntityPlayerMP)event.player);
      }

   }

   @SubscribeEvent
   public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase entityLiving = (EntityLivingBase)event.getEntity();
         if (ConfigLoader.fixChangingDimensions && !event.getWorld().isRemote) {
            updateEntityModifiers(entityLiving);
         }

         if (!entityLiving.getEntityData().getBoolean("Potion Core - Jump Boost Updated") && entityLiving.getEntityAttribute(PotionCoreAttributes.JUMP_HEIGHT) != null) {
            if (entityLiving.getEntityAttribute(PotionCoreAttributes.JUMP_HEIGHT).getBaseValue() == 0.0D) {
               entityLiving.getEntityAttribute(PotionCoreAttributes.JUMP_HEIGHT).setBaseValue(1.0D);
            }

            entityLiving.getEntityData().setBoolean("Potion Core - Jump Boost Updated", true);
         }
      }

   }

   private static void updateEntityModifiers(EntityLivingBase entity) {
      for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
         ItemStack stack = entity.getItemStackFromSlot(slot);
         if (!stack.isEmpty()) {
            entity.getAttributeMap().applyAttributeModifiers(stack.getAttributeModifiers(slot));
         }
      }

      if (entity.getEntityData().hasKey("Potion Core - Health Fix")) {
         entity.setHealth(entity.getEntityData().getFloat("Potion Core - Health Fix"));
      } else {
         entity.setHealth(entity.getMaxHealth());
         entity.getEntityData().setFloat("Potion Core - Health Fix", entity.getHealth());
      }

      if (entity instanceof EntityPlayerMP) {
         entity.getEntityData().setFloat("Potion Core - Health Fix Delay", 5.0F);
         PotionCoreHelper.playersToHeal.add((EntityPlayerMP)entity);
      }

      ArrayList<PotionEffect> effectList = new ArrayList();

      for(PotionEffect effect : entity.getActivePotionEffects()) {
         effectList.add(effect);
      }

      for(PotionEffect effect : effectList) {
         if (effect.getPotion() != null) {
            effect.getPotion().removeAttributesModifiersFromEntity(entity, entity.getAttributeMap(), effect.getAmplifier());
            effect.getPotion().applyAttributesModifiersToEntity(entity, entity.getAttributeMap(), effect.getAmplifier());
         }
      }

   }

   @SubscribeEvent
   public static void onWorldTick(WorldTickEvent event) {
      if (event.phase == Phase.END) {
         if (!PotionCoreHelper.playersToHeal.isEmpty()) {
            Iterator<EntityPlayerMP> it = PotionCoreHelper.playersToHeal.iterator();

            while(it.hasNext()) {
               EntityPlayerMP player = (EntityPlayerMP)it.next();
               if (!player.world.isRemote) {
                  int delay = player.getEntityData().getInteger("Potion Core - Health Fix Delay");
                  if (delay < 0) {
                     player.getEntityData().setInteger("Potion Core - Health Fix Delay", delay - 1);
                  } else {
                     player.setPlayerHealthUpdated();
                     player.getEntityData().setInteger("Potion Core - Health Fix Delay", -1);
                     it.remove();
                  }
               }
            }
         }

         checkInvulReset();
      }

   }

   private static void checkInvulReset() {
      synchronized(INVUL_RESET_LOCK) {
         if (!PotionCoreHelper.invulResetEntities.isEmpty()) {
            PotionCoreHelper.invulResetEntities.forEach((entity) -> {
               entity.hurtResistantTime = 0;
            });
         }

         PotionCoreHelper.invulResetEntities.clear();
      }
   }
}
