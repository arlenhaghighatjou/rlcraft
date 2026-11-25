package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileBiomass;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPExplotion;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.apache.logging.log4j.Level;

public class ParasiteEventEntity {
   public static boolean canSpawnNext = true;

   public ParasiteEventEntity() {
   }

   public static int entityChunkCount(World world, BlockPos pos, Class<? extends EntityLivingBase> mobC) {
      ClassInheritanceMultiMap<Entity>[] arrayE = world.getChunkFromBlockCoords(pos).getEntityLists();
      int v = 0;

      for(int i = 0; i < arrayE.length; ++i) {
         if (arrayE[i] != null) {
            Object[] arrayO = arrayE[i].toArray();

            for(int x = 0; x < arrayO.length; ++x) {
               if (arrayO[x] != null && mobC.isInstance(arrayO[x])) {
                  ++v;
               }
            }
         }
      }

      return v;
   }

   public static boolean checkEntity(EntityLivingBase entity, String[] list, boolean inverted) {
      ResourceLocation enti = EntityList.getKey(entity);
      return enti != null ? checkName(enti.toString(), list, inverted) : false;
   }

   public static boolean checkName(String name, String[] list, boolean inverted) {
      boolean inv = false;
      if (list.length != 0) {
         if (inverted) {
            inv = true;
         }

         for(String i : list) {
            if (inverted) {
               if (name.contains(i)) {
                  return false;
               }
            } else if (name.contains(i)) {
               return true;
            }
         }
      }

      return inv;
   }

   public static void orbApplyEffects(EntityLivingBase target, EntityParasiteBase in, String[] effects, int mobs) {
      String[] here = new String[6];

      for(String i : effects) {
         here = i.split(";");

         try {
            if (here[5] == null) {
               return;
            }
         } catch (Exception var15) {
            return;
         }

         Potion potionE = Potion.getPotionFromResourceLocation(here[3]);
         if (potionE != null) {
            int self = Integer.parseInt(here[0]);
            int duration = Integer.parseInt(here[1]) * 20;
            int amp = Integer.parseInt(here[2]);
            int enemiesA = Integer.parseInt(here[4]);
            int enemiesD = Integer.parseInt(here[5]);
            if (enemiesA != 0) {
               amp += mobs / enemiesA;
            }

            if (enemiesD != 0) {
               duration += mobs / enemiesD * 20;
            }

            if (self == 1) {
               in.addPotionEffect(new PotionEffect(potionE, duration, amp, false, false));
            } else if (self == 2) {
               if (target instanceof EntityParasiteBase) {
                  SRPPotions.applyStackPotion(potionE, target, duration, amp);
               }
            } else if (!(target instanceof EntityParasiteBase)) {
               SRPPotions.applyStackPotion(potionE, target, duration, amp);
            }
         }
      }

   }

   public static void spawnNext(EntityParasiteBase entityin, EntityParasiteBase entityout, boolean effects, boolean thunder) {
      if (!entityin.isDead) {
         boolean flag = entityin.isBurning();
         entityin.setDead();
         entityout.setLocationAndAngles(entityin.posX, entityin.posY, entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
         entityout.onInitialSpawn(entityin.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
         entityout.cannotDespawn(entityin.canDespawn());
         if (entityin.hasCustomName()) {
            entityout.setCustomNameTag(entityin.getCustomNameTag());
            entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
         }

         entityin.world.spawnEntity(entityout);
         if (entityin instanceof EntityPMalleable && entityout instanceof EntityPMalleable) {
            ((EntityPMalleable)entityout).copyResistancesFrom((EntityPMalleable)entityin);
         }

         if (effects) {
            entityout.particleStatus((byte)7);
         }

         if (thunder && SRPConfig.thunderEnable) {
            entityout.world.addWeatherEffect(new EntityLightningBolt(entityout.world, entityout.posX, entityout.posY, entityout.posZ, true));
         }

         if (flag) {
            entityout.setHealth(entityout.getMaxHealth() * 0.5F);
            entityout.setFire(8);
         }

      }
   }

   public static void spawnM(EntityParasiteBase param0, String[] param1, int param2, boolean param3, String param4) {
      // $FF: Couldn't be decompiled
   }

   public static boolean SummonM(EntityParasiteBase entityin, String[] out, int range, @Nullable EntityLivingBase target) {
      if (!entityin.world.isRemote) {
         Random rand = new Random();
         double x = entityin.posX;
         double y = entityin.posY;
         double z = entityin.posZ;
         double randomx = (double)rand.nextInt(range);
         double randomz = (double)rand.nextInt(range);
         double negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         String[] entityC = new String[3];
         int index = rand.nextInt(out.length);
         int limit = 0;
         boolean flag = true;

         while(true) {
            EntityLiving entityout;
            while(true) {
               if (!flag) {
                  return false;
               }

               if (index >= out.length) {
                  index = 0;
                  ++limit;
               }

               if (limit == 2) {
                  return false;
               }

               if (out[index] != null) {
                  entityC = out[index].split(";");
                  double chance = Double.parseDouble(entityC[1]);
                  if (rand.nextDouble() <= chance) {
                     if (entityin.world.getBlockState(new BlockPos(x + randomx, y, z + randomz)).getBlock() == Blocks.AIR && entityin.world.getBlockState(new BlockPos(x + randomx, y - 1.0D, z + randomz)).getBlock() != Blocks.AIR) {
                        entityout = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(entityC[0]), entityin.world);
                        if (entityout == null) {
                           return false;
                        }

                        entityout.setLocationAndAngles(x + randomx, y, z + randomz, entityin.rotationYaw, entityin.rotationPitch);
                        if (entityin.world.getCollisionBoxes(entityout, entityout.getEntityBoundingBox()).isEmpty()) {
                           entityout.onInitialSpawn(entityin.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
                           if (!(entityin instanceof EntityCanSummon)) {
                              break;
                           }

                           EntityCanSummon father = (EntityCanSummon)entityin;
                           int points = Integer.parseInt(entityC[2]);
                           if (father.getTotalParasites() - father.getActualParasites() >= points) {
                              father.setActualParasites(points);
                              father.addID(entityout.getEntityId(), points);
                              break;
                           }

                           entityout.setDead();
                           ++index;
                           continue;
                        }

                        entityout.setDead();
                        ++index;
                        continue;
                     }

                     randomx = (double)rand.nextInt(range);
                     randomz = (double)rand.nextInt(range);
                     negative = (double)rand.nextInt(2);
                     if (negative == 0.0D) {
                        randomx *= -1.0D;
                     }

                     negative = (double)rand.nextInt(2);
                     if (negative == 0.0D) {
                        randomz *= -1.0D;
                     }
                  }
               }

               ++index;
            }

            if (!(entityout instanceof EntityPStationaryArchitect)) {
               entityin.world.spawnEntity(entityout);
               if (target != null) {
                  entityout.setAttackTarget(target);
               }

               flag = false;
               return true;
            }

            if (!SRPConfigSystems.rsSky || entityout.world.canSeeSky(new BlockPos(entityout.posX, entityout.posY + (double)entityout.getEyeHeight(), entityout.posZ))) {
               Block looking = entityin.world.getBlockState(entityout.getPosition().down()).getBlock();
               if (looking == SRPBlocks.InfestedStain) {
                  entityout.setDead();
                  return false;
               }

               EntityVenkrol vOut = (EntityVenkrol)entityout;
               vOut.setRSSpawned(true);
               entityin.world.spawnEntity(vOut);
               if (target != null) {
                  vOut.setAttackTarget(target);
               }

               entityin.world.setEntityState(vOut, (byte)50);
               flag = false;
               return true;
            }

            randomx = (double)rand.nextInt(range);
            randomz = (double)rand.nextInt(range);
            negative = (double)rand.nextInt(2);
            if (negative == 0.0D) {
               randomx *= -1.0D;
            }

            negative = (double)rand.nextInt(2);
            if (negative == 0.0D) {
               randomz *= -1.0D;
            }

            entityout.setDead();
            ++index;
         }
      } else {
         return false;
      }
   }

   public static boolean SummonM(EntityParasiteBase entityin, String[] out, int range, double tx, double ty, double tz, @Nullable EntityLivingBase target, boolean pod) {
      if (!entityin.world.isRemote) {
         Random rand = new Random();
         double x = tx;
         double y = ty;
         double z = tz;
         double randomx = (double)rand.nextInt(range);
         double randomz = (double)rand.nextInt(range);
         double negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         String[] entityC = new String[3];
         int index = rand.nextInt(out.length);
         int limit = 0;
         boolean flag = true;

         EntityLiving entityout;
         while(true) {
            if (!flag) {
               return false;
            }

            if (index >= out.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (out[index] != null) {
               entityC = out[index].split(";");
               double chance = Double.parseDouble(entityC[1]);
               if (rand.nextDouble() <= chance) {
                  if (entityin.world.getBlockState(new BlockPos(x + randomx, y, z + randomz)).getBlock() == Blocks.AIR && entityin.world.getBlockState(new BlockPos(x + randomx, y - 1.0D, z + randomz)).getBlock() != Blocks.AIR || pod) {
                     if (pod) {
                        EntityDropPod entityout2 = new EntityDropPod(entityin.world);
                        entityout2.setLocationAndAngles(x + randomx, y, z + randomz, entityin.rotationYaw, entityin.rotationPitch);
                        entityout2.setOwner(entityin.getParasiteType());
                        entityin.world.spawnEntity(entityout2);
                        flag = false;
                        return true;
                     }

                     entityout = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(entityC[0]), entityin.world);
                     if (entityout == null) {
                        return false;
                     }

                     entityout.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
                     entityout.setLocationAndAngles(x + randomx, y, z + randomz, entityin.rotationYaw, entityin.rotationPitch);
                     entityout.onInitialSpawn(entityin.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
                     if (!(entityin instanceof EntityCanSummon)) {
                        break;
                     }

                     EntityCanSummon father = (EntityCanSummon)entityin;
                     int points = Integer.parseInt(entityC[2]);
                     if (father.getTotalParasites() - father.getActualParasites() >= points) {
                        father.setActualParasites(points);
                        father.addID(entityout.getEntityId(), points);
                        break;
                     }

                     ++index;
                     continue;
                  }

                  randomx = (double)rand.nextInt(range);
                  randomz = (double)rand.nextInt(range);
                  negative = (double)rand.nextInt(2);
                  if (negative == 0.0D) {
                     randomx *= -1.0D;
                  }

                  negative = (double)rand.nextInt(2);
                  if (negative == 0.0D) {
                     randomz *= -1.0D;
                  }
               }
            }

            ++index;
         }

         if (entityout instanceof EntityVenkrol) {
            Block looking = entityin.world.getBlockState(entityout.getPosition().down()).getBlock();
            if (looking == SRPBlocks.InfestedStain) {
               return false;
            }

            if (entityin.getParasiteIDRegister() == 41) {
               EntityPStationaryArchitect out2 = (EntityPStationaryArchitect)entityout;
               out2.setCanGrowTo(false);
            }
         }

         entityin.world.spawnEntity(entityout);
         if (target != null) {
            entityout.setAttackTarget(target);
         }

         flag = false;
         if (entityout instanceof EntityParasiteBase) {
            EntityParasiteBase eOut = (EntityParasiteBase)entityout;
            eOut.setParasiteToFollow(entityin);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void spawnFromList(Entity entityin, String[] out, @Nullable EntityLivingBase target) {
      EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(out[entityin.world.rand.nextInt(out.length)]), entityin.world);
      if (entityout != null) {
         entityout.copyLocationAndAnglesFrom(entityin);
         entityout.onInitialSpawn(entityin.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
         entityin.world.spawnEntity(entityout);
         if (target != null) {
            entityout.setAttackTarget(target);
         }

      }
   }

   public static boolean spawnBiomassFromProjectile(EntityParasiteBase entityin, String[] out, @Nullable EntityLivingBase target) {
      if (!entityin.world.isRemote) {
         Random rand = new Random();
         String[] entityC = new String[3];
         int index = rand.nextInt(out.length);
         int limit = 0;
         boolean flag = true;

         while(flag) {
            if (index >= out.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (out[index] != null) {
               entityC = out[index].split(";");
               double chance = Double.parseDouble(entityC[1]);
               if (rand.nextDouble() <= chance) {
                  EntityCanSummon father = (EntityCanSummon)entityin;
                  int points = Integer.parseInt(entityC[2]);
                  if (father.getTotalParasites() - father.getActualParasites() >= points) {
                     if (target == null) {
                        return false;
                     }

                     double d1 = 4.0D;
                     Vec3d vec3d = entityin.getLook(1.0F);
                     double d2 = target.posX - (entityin.posX + vec3d.x);
                     double d3 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (0.5D + entityin.posY + (double)(entityin.height / 2.0F));
                     double d4 = target.posZ - (entityin.posZ + vec3d.z);
                     EntityProjectileBiomass entityout = new EntityProjectileBiomass(entityin.world, entityin, d2, d3, d4);
                     entityout.posX = entityin.posX + vec3d.x;
                     entityout.posY = entityin.posY + (double)entityin.getEyeHeight() - 0.2D;
                     entityout.posZ = entityin.posZ + vec3d.z;
                     entityout.setParasite(entityC[0], points, 4);
                     father.setActualParasites(points);
                     father.addID(entityout.getEntityId(), points);
                     entityin.world.spawnEntity(entityout);
                     flag = false;
                     return true;
                  }

                  ++index;
                  continue;
               }
            }

            ++index;
         }
      }

      return false;
   }

   public static boolean spawnBiomassFromVomit(EntityParasiteBase entityin, String[] out, @Nullable EntityLivingBase target) {
      if (!entityin.world.isRemote) {
         Random rand = new Random();
         String[] entityC = new String[3];
         int index = rand.nextInt(out.length);
         int limit = 0;
         boolean flag = true;

         while(flag) {
            if (index >= out.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (out[index] != null) {
               entityC = out[index].split(";");
               double chance = Double.parseDouble(entityC[1]);
               if (rand.nextDouble() <= chance) {
                  EntityCanSummon father = (EntityCanSummon)entityin;
                  int points = Integer.parseInt(entityC[2]);
                  if (father.getTotalParasites() - father.getActualParasites() >= points) {
                     EntityBiomass entityout = new EntityBiomass(entityin.world, entityin, target);
                     entityout.setLocationAndAngles(entityin.posX, entityin.posY, entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
                     float f19 = MathHelper.sin(entityin.rotationYaw * 0.017453292F - entityin.randomYawVelocity * 0.01F);
                     float f14 = 0.17453292F;
                     float f16 = MathHelper.cos(f14);
                     float f4 = MathHelper.cos(entityin.rotationYaw * 0.017453292F - entityin.randomYawVelocity * 0.01F);
                     entityout.rotationYaw = entityin.rotationYaw;
                     if (entityout.world.getBlockState(new BlockPos(entityin.posX + -1.0D * (double)(f19 * 3.0F * f16), entityin.posY + (double)entityin.getEyeHeight(), entityin.posZ - -1.0D * (double)(f4 * 3.0F * f16))).getBlock() != Blocks.AIR) {
                        entityout.setDead();
                        return false;
                     }

                     entityout.setPosition(entityin.posX + -1.0D * (double)(f19 * 3.0F * f16), entityin.posY + (double)entityin.getEyeHeight(), entityin.posZ - -1.0D * (double)(f4 * 3.0F * f16));
                     entityout.setFuse(80);
                     entityout.setParasite(entityC[0], points);
                     if (entityin instanceof EntityCanra) {
                        entityout.setSkin(5);
                     } else {
                        entityout.setSkin(6);
                     }

                     father.setActualParasites(points);
                     father.addID(entityout.getEntityId(), points);
                     if (entityin.isBurning()) {
                        entityout.setFire(8);
                     }

                     entityin.world.spawnEntity(entityout);
                     flag = false;
                     return true;
                  }

                  ++index;
                  continue;
               }
            }

            ++index;
         }
      }

      return false;
   }

   public static boolean spawnBiomassFromBeckon(EntityParasiteBase entityin, int stage, EntityLivingBase target, boolean payfather, String[] ground, String[] air) {
      if (!entityin.world.isRemote) {
         String[] mobListG = ground;
         if (entityin.posY + 3.0D <= target.posY) {
            if (stage == 1) {
               return false;
            }

            mobListG = air;
         }

         Random rand = new Random();
         String[] entityC = new String[3];
         int index = rand.nextInt(mobListG.length);
         int limit = 0;
         boolean flag = true;

         while(flag) {
            if (index >= mobListG.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (mobListG[index] != null) {
               entityC = mobListG[index].split(";");
               double chance = Double.parseDouble(entityC[1]);
               if (rand.nextDouble() <= chance) {
                  EntityCanSummon father = (EntityCanSummon)entityin;
                  int points = Integer.parseInt(entityC[2]);
                  if (father.getTotalParasites() - father.getActualParasites() >= points || !payfather) {
                     double b = 0.0D;
                     if (stage == 3) {
                        b = 0.5D;
                     }

                     EntityBiomass entityout = new EntityBiomass(entityin.world, entityin, (float)stage, target, payfather);
                     entityout.setLocationAndAngles(entityin.posX, entityin.posY + (double)entityin.getEyeHeight() + b, entityin.posZ, entityin.rotationYaw, entityin.rotationPitch);
                     double d0 = (double)((float)entityin.posX + entityin.world.rand.nextFloat());
                     double d1 = (double)((float)entityin.posY + entityin.getEyeHeight() + entityin.world.rand.nextFloat());
                     double d2 = (double)((float)entityin.posZ + entityin.world.rand.nextFloat());
                     double d3 = d0 - entityin.posX;
                     double d4 = d1 - entityin.posY;
                     double d5 = d2 - entityin.posZ;
                     double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                     d3 = d3 / d6;
                     d4 = d4 / d6;
                     d5 = d5 / d6;
                     double d7 = 0.5D / (d6 / 4.0D + 0.1D);
                     d7 = d7 * (double)(entityin.world.rand.nextFloat() * entityin.world.rand.nextFloat() + 1.7F);
                     double k = 3.0D;
                     if (stage == 3) {
                        k = 5.0D;
                     }

                     d3 = d3 * d7 * k;
                     d4 = d4 * d7 * 2.0D;
                     d5 = d5 * d7 * k;
                     entityout.setMotion(d3, d4, d5, 0.4D, 0.5D);
                     entityout.setFuse(80);
                     entityout.setParasite(entityC[0], points);
                     entityout.setSkin(stage);
                     if (entityin.isBurning()) {
                        entityout.setFire(8);
                     }

                     entityin.world.spawnEntity(entityout);
                     if (payfather) {
                        father.setActualParasites(points);
                        father.addID(entityout.getEntityId(), points);
                     }

                     flag = false;
                     return true;
                  }

                  ++index;
                  continue;
               }
            }

            ++index;
         }
      }

      return false;
   }

   public static void convertEntity(EntityLivingBase entityin, NBTTagCompound tags, boolean ignoreKey, String[] list) {
      if (entityin != null) {
         if (tags.hasKey("srpcothimmunity")) {
            int key = tags.getInteger("srpcothimmunity");
            if (key == 0 && !ignoreKey) {
               entityin.removeActivePotionEffect(SRPPotions.COTH_E);
               return;
            }

            entityin.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 3, false, false));
            SRPMain.network.sendToAll(new SRPPacketParticle(entityin.posX, entityin.posY, entityin.posZ, entityin.width, entityin.height, (byte)1));
            ++key;
            tags.setInteger("srpcothimmunity", key);
            if (key < 3 && !ignoreKey) {
               return;
            }

            World world = entityin.world;
            if (world.isRemote) {
               return;
            }

            String mobname = null;

            try {
               mobname = EntityList.getKey(entityin).toString();
            } catch (Exception var12) {
               SRPMain.logger.log(Level.ERROR, "Problem while converting entity", var12);
               spawnInsider(entityin, world, tags);
               return;
            }

            if (mobname == null) {
               return;
            }

            String[] here = new String[2];
            boolean flag = true;

            for(int i = 0; i < list.length; ++i) {
               here = list[i].split(";");

               try {
                  if (here[0] == null || here[1] == null) {
                     spawnInsider(entityin, world, tags);
                     return;
                  }
               } catch (Exception var13) {
                  SRPMain.logger.log(Level.ERROR, "Problem while converting entity", var13);
                  spawnInsider(entityin, world, tags);
                  return;
               }

               if (here[0].equals(mobname)) {
                  Entity outOne = EntityList.createEntityByIDFromName(new ResourceLocation(here[1]), world);
                  if (outOne == null) {
                     spawnInsider(entityin, world, tags);
                     return;
                  }

                  if (outOne instanceof EntityPInfected) {
                     EntityPInfected entityout = (EntityPInfected)outOne;
                     entityout.copyLocationAndAnglesFrom(entityin);
                     world.removeEntity(entityin);
                     entityout.setHost(mobname);
                     entityout.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
                     if (entityin.hasCustomName()) {
                        entityout.setCustomNameTag(entityin.getCustomNameTag());
                        entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                     }

                     world.spawnEntity(entityout);
                     world.playEvent((EntityPlayer)null, 1026, new BlockPos(entityout), 0);
                     entityout.particleStatus((byte)7);
                     entityout.cannotDespawn(SRPConfig.convertedDespawn);
                     if (key >= 10) {
                        entityout.addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, 600, 0, false, false));
                     }
                  } else if (outOne instanceof EntityLiving) {
                     EntityLiving entityout = (EntityLiving)outOne;
                     entityout.copyLocationAndAnglesFrom(entityin);
                     world.removeEntity(entityin);
                     entityout.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
                     if (entityin.hasCustomName()) {
                        entityout.setCustomNameTag(entityin.getCustomNameTag());
                        entityout.setAlwaysRenderNameTag(entityin.getAlwaysRenderNameTag());
                     }

                     world.spawnEntity(entityout);
                     world.playEvent((EntityPlayer)null, 1026, new BlockPos(entityout), 0);
                  }

                  flag = false;
               }
            }

            if (flag && !ignoreKey) {
               spawnInsider(entityin, world, tags);
            }
         }

      }
   }

   public static void spawnInsider(EntityLivingBase entity, World world, NBTTagCompound tags) {
      if (SRPConfigMobs.inhooSEnabled && SRPConfigMobs.inhooMEnabled) {
         List<Entity> serverList = world.loadedEntityList;
         int count = 0;

         for(int x = 0; x < serverList.size(); ++x) {
            if (serverList.get(x) instanceof EntityInhooM || serverList.get(x) instanceof EntityInhooS) {
               ++count;
            }
         }

         if (count <= SRPConfig.incompleteCap) {
            if (tags.hasKey("srpcothimmunity")) {
               int key = tags.getInteger("srpcothimmunity");
               if (key == 0) {
                  entity.removeActivePotionEffect(SRPPotions.COTH_E);
                  return;
               }

               entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 3, false, false));
               SRPMain.network.sendToAll(new SRPPacketParticle(entity.posX, entity.posY, entity.posZ, entity.width, entity.height, (byte)1));
               ++key;
               tags.setInteger("srpcothimmunity", key);
               if (key < 3) {
                  return;
               }

               EntityParasiteBase out = new EntityInhooS(world);
               float mass = getEntityArea(entity);
               if ((mass <= 0.11F || mass > 0.517F) && (double)mass > 0.517D) {
                  out = new EntityInhooM(world);
               }

               out.copyLocationAndAnglesFrom(entity);
               world.removeEntity(entity);
               world.spawnEntity(out);
               world.playEvent((EntityPlayer)null, 1026, new BlockPos(out), 0);
               out.particleStatus((byte)7);
               out.cannotDespawn(SRPConfig.convertedDespawn);
            }

         }
      }
   }

   private static float getEntityArea(EntityLivingBase entity) {
      return entity.width * entity.width * entity.height;
   }

   public static boolean merge(EntityParasiteBase entityin, int code, String name) {
      if (SRPConfigSystems.mergeRandom) {
         entityin.particleStatus((byte)7);
         String[] entityC = new String[2];
         Random rand = new Random();
         int index = rand.nextInt(SRPConfigSystems.mergeMobTable.length);
         boolean flag = true;

         for(int limit = 0; flag; ++index) {
            if (index >= SRPConfigSystems.mergeMobTable.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (SRPConfigSystems.mergeMobTable[index] != null) {
               entityC = SRPConfigSystems.mergeMobTable[index].split(";");
               spawnM(entityin, new String[]{entityC[0] + ";1;1"}, 7, true, name);
               flag = false;
               if (SRPConfigSystems.useEvolution) {
                  SRPWorldData data = SRPWorldData.get(entityin.world);
                  data.setTotalKills(SRPConfigSystems.valueMerge, true, entityin.world, true);
               }

               return true;
            }
         }
      } else {
         String[] entityC = new String[2];

         for(int i = 0; i < SRPConfigSystems.mergeMobTable.length; ++i) {
            if (SRPConfigSystems.mergeMobTable[i] != null) {
               entityC = SRPConfigSystems.mergeMobTable[i].split(";");
               int points = Integer.parseInt(entityC[1]);
               if (points == code) {
                  spawnM(entityin, new String[]{entityC[0] + ";1;1"}, 7, true, name);
                  if (SRPConfigSystems.useEvolution) {
                     SRPWorldData data = SRPWorldData.get(entityin.world);
                     data.setTotalKills(SRPConfigSystems.valueMerge, true, entityin.world, true);
                  }

                  return true;
               }
            }
         }

         Random rand = new Random();
         int index = rand.nextInt(SRPConfigSystems.mergeMobTable.length);
         boolean flag = true;

         for(int limit = 0; flag; ++index) {
            if (index >= SRPConfigSystems.mergeMobTable.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (SRPConfigSystems.mergeMobTable[index] != null) {
               entityC = SRPConfigSystems.mergeMobTable[index].split(";");
               spawnM(entityin, new String[]{entityC[0] + ";1;1"}, 7, true, name);
               flag = false;
               if (SRPConfigSystems.useEvolution) {
                  SRPWorldData data = SRPWorldData.get(entityin.world);
                  data.setTotalKills(SRPConfigSystems.valueMerge, true, entityin.world, true);
               }

               return true;
            }
         }
      }

      return false;
   }

   public static void damageEntity(World worldIn, BlockPos pos, Entity entityIn, int stage) {
      double chance = 0.0D;
      float damage = 0.0F;
      if (stage != 1 && stage != 11) {
         if (stage != 2 && stage != 12) {
            if (stage == 3 || stage == 13) {
               chance = (double)SRPConfigMobs.venkrolsiiiBlockChance;
               damage = SRPConfigMobs.venkrolsiiiBlockDamage;
            }
         } else {
            chance = (double)SRPConfigMobs.venkrolsiiBlockChance;
            damage = SRPConfigMobs.venkrolsiiBlockDamage;
         }
      } else {
         chance = (double)SRPConfigMobs.venkrolBlockChance;
         damage = SRPConfigMobs.venkrolBlockDamage;
      }

      if (entityIn instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityIn;

         try {
            double minus = EquipCheck(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().getRegistryName().toString(), player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().getRegistryName().toString(), player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem().getRegistryName().toString(), player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem().getRegistryName().toString());
            Random rand = new Random();
            if (rand.nextDouble() <= chance) {
               if (rand.nextDouble() <= minus) {
                  entityIn.attackEntityFrom(DamageSource.STARVE, damage);
                  return;
               }

               return;
            }

            return;
         } catch (Exception var11) {
            SRPMain.logger.log(Level.ERROR, "Problem with player walking on infested block", var11);
         }
      }

      Random rand = new Random();
      if (rand.nextDouble() <= chance) {
         entityIn.attackEntityFrom(DamageSource.STARVE, damage);
      }

   }

   private static double EquipCheck(String head, String body, String legs, String boots) {
      double flag = 1.0D;
      String[] here = new String[2];

      for(int i = 0; i < SRPAttributes.headEquip.length; ++i) {
         if (SRPAttributes.headEquip[i] != null) {
            here = SRPAttributes.headEquip[i].split(";");
            if (here[0].equals(head)) {
               flag -= Double.parseDouble(here[1]);
               break;
            }
         }
      }

      for(int i = 0; i < SRPAttributes.bodyEquip.length; ++i) {
         if (SRPAttributes.bodyEquip[i] != null) {
            here = SRPAttributes.bodyEquip[i].split(";");
            if (here[0].equals(body)) {
               flag -= Double.parseDouble(here[1]);
               break;
            }
         }
      }

      for(int i = 0; i < SRPAttributes.legsEquip.length; ++i) {
         if (SRPAttributes.legsEquip[i] != null) {
            here = SRPAttributes.legsEquip[i].split(";");
            if (here[0].equals(legs)) {
               flag -= Double.parseDouble(here[1]);
               break;
            }
         }
      }

      for(int i = 0; i < SRPAttributes.bootsEquip.length; ++i) {
         if (SRPAttributes.bootsEquip[i] != null) {
            here = SRPAttributes.bootsEquip[i].split(";");
            if (here[0].equals(boots)) {
               flag -= Double.parseDouble(here[1]);
               break;
            }
         }
      }

      return flag;
   }

   public static BlockPos getFloor(World worldIn, BlockPos pos, int loop) {
      if (loop <= 0) {
         return null;
      } else {
         --loop;
         if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
            return worldIn.getBlockState(pos.down()).getBlock() != Blocks.AIR ? pos : getFloor(worldIn, pos.down(), loop);
         } else {
            return getFloor(worldIn, pos.up(), loop);
         }
      }
   }

   public static BlockPos getFloorBuilding(World worldIn, BlockPos pos, int loop) {
      if (loop <= 0) {
         return null;
      } else {
         --loop;
         if (!worldIn.getBlockState(pos).isFullBlock()) {
            return worldIn.getBlockState(pos.down()).isFullBlock() && !(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockBush) && !(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockLeaves) && !(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockLog) ? pos : getFloorBuilding(worldIn, pos.down(), loop);
         } else {
            return !(worldIn.getBlockState(pos).getBlock() instanceof BlockBush) && !(worldIn.getBlockState(pos).getBlock() instanceof BlockLeaves) && !(worldIn.getBlockState(pos).getBlock() instanceof BlockLog) ? getFloorBuilding(worldIn, pos.up(), loop) : getFloorBuilding(worldIn, pos.down(1), loop);
         }
      }
   }

   public static boolean spawnTurrets(EntityLivingBase entityin, int range, byte type, int stage) {
      if (stage <= 2) {
         return false;
      } else if (entityin.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         return false;
      } else {
         Random rand = new Random();
         World world = entityin.world;
         double randomx = (double)rand.nextInt(range);
         double randomz = (double)rand.nextInt(range);
         double negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         int index = 5;
         int limit = 0;

         for(boolean flag = true; flag; ++limit) {
            if (limit >= 5) {
               return false;
            }

            BlockPos floor = getFloor(world, new BlockPos(entityin.posX + randomx, entityin.posY, entityin.posZ + randomz), 5);
            if (floor != null && world.getBlockState(floor.down()).getBlock() == SRPBlocks.InfestedStain) {
               int flag2 = 0;
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)floor.getX(), (double)floor.getY(), (double)floor.getZ(), (double)(floor.getX() + 1), (double)(floor.getY() + 1), (double)(floor.getZ() + 1))).grow(42.0D, 5.0D, 42.0D);

               for(EntityParasiteBase mob : world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb)) {
                  if (mob.isEntityAlive() && mob.getParasiteType() == 40) {
                     ++flag2;
                  }
               }

               if (flag2 >= 3) {
                  return false;
               }

               if (type == 1) {
                  if (!SRPConfigMobs.unvoEnabled) {
                     return false;
                  }

                  EntityUnvo out = new EntityUnvo(world);
                  out.setLocationAndAngles((double)floor.getX(), (double)floor.getY(), (double)floor.getZ(), 0.0F, 0.0F);
                  world.spawnEntity(out);
                  out.setAttackTarget(entityin);
                  return true;
               }

               if (type == 2) {
                  if (!SRPConfigMobs.tonroEnabled) {
                     return false;
                  }

                  EntityTonro out = new EntityTonro(world);
                  out.setLocationAndAngles((double)floor.getX(), (double)floor.getY(), (double)floor.getZ(), 0.0F, 0.0F);
                  world.spawnEntity(out);
                  out.setAttackTarget(entityin);
                  return true;
               }
            }

            randomx = (double)rand.nextInt(range);
            randomz = (double)rand.nextInt(range);
            negative = (double)rand.nextInt(2);
            if (negative == 0.0D) {
               randomx *= -1.0D;
            }

            negative = (double)rand.nextInt(2);
            if (negative == 0.0D) {
               randomz *= -1.0D;
            }
         }

         return false;
      }
   }

   public static void alertAllPlayerDim(World worldIn, String message, int warning) {
      List<EntityPlayer> playerEntityList = worldIn.playerEntities;

      for(int i = 0; i < playerEntityList.size(); ++i) {
         EntityPlayer theEntity = (EntityPlayer)playerEntityList.get(i);
         theEntity.sendMessage(new TextComponentString(message));
         SRPMain.network.sendToDimension(new SRPPacketMovingSound(warning), worldIn.provider.getDimension());
      }

      if (warning == -7 && message.equals("Phase decreased")) {
         List<Entity> serverList = worldIn.loadedEntityList;

         for(int x = 0; x < serverList.size(); ++x) {
            if (serverList.get(x) instanceof EntityParasiteBase) {
               ((EntityParasiteBase)serverList.get(x)).addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 2400, 1, false, false));
            }
         }
      }

   }

   public static boolean spawnFromBlock(World world, String[] out, int range, BlockPos pos) {
      if (!world.isRemote) {
         Random rand = new Random();
         double x = (double)pos.getX();
         double y = (double)pos.getY();
         double z = (double)pos.getZ();
         double randomx = (double)rand.nextInt(range);
         double randomz = (double)rand.nextInt(range);
         double negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         String[] entityC = new String[3];
         int index = rand.nextInt(out.length);
         int limit = 0;

         for(boolean flag = true; flag; ++index) {
            if (index >= out.length) {
               index = 0;
               ++limit;
            }

            if (limit == 2) {
               return false;
            }

            if (out[index] != null) {
               entityC = out[index].split(";");
               double chance = Double.parseDouble(entityC[1]);
               if (rand.nextDouble() <= chance) {
                  BlockPos helper = getFloor(world, new BlockPos(x + randomx, y, z + randomz), 3);
                  if (helper != null) {
                     EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(entityC[0]), world);
                     if (entityout == null) {
                        return false;
                     }

                     entityout.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
                     entityout.setLocationAndAngles((double)helper.getX(), (double)helper.getY(), (double)helper.getZ(), 0.0F, 0.0F);
                     entityout.onInitialSpawn(world.getDifficultyForLocation(helper), (IEntityLivingData)null);
                     if (entityout instanceof EntityKol) {
                        EntityKol kol = (EntityKol)entityout;
                        SRPWorldData data = SRPWorldData.get(world);
                        BlockPos origin = data.nearestColonyPosition(helper, 0, false);
                        if (origin == null) {
                           return false;
                        }

                        kol.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false, 0));
                     }

                     world.spawnEntity(entityout);
                     flag = false;
                     return true;
                  }

                  randomx = (double)rand.nextInt(range);
                  randomz = (double)rand.nextInt(range);
                  negative = (double)rand.nextInt(2);
                  if (negative == 0.0D) {
                     randomx *= -1.0D;
                  }

                  negative = (double)rand.nextInt(2);
                  if (negative == 0.0D) {
                     randomz *= -1.0D;
                  }
               }
            }
         }
      }

      return false;
   }

   public static void spawnBeckon(World world, DamageSource cause, EntityParasiteBase in) {
      if (SRPConfigSystems.rsEnabled) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(in.posX, in.posY, in.posZ, in.posX + 1.0D, in.posY + 1.0D, in.posZ + 1.0D)).grow((double)(SRPConfigMobs.venkrolsiiiRange - 1), (double)SRPConfigMobs.venkrolsiiiRangeY, (double)(SRPConfigMobs.venkrolsiiiRange - 1));
         List<EntityPStationaryArchitect> moblist = world.getEntitiesWithinAABB(EntityPStationaryArchitect.class, axisalignedbb);
         if (!moblist.isEmpty()) {
            return;
         }

         if (entityChunkCount(world, in.getPosition(), EntityPStationaryArchitect.class) >= SRPConfigSystems.rsMaxVenkrolChunk) {
            return;
         }

         SRPWorldData data = SRPWorldData.get(world);
         if (SRPConfigSystems.rsPlayer) {
            if (cause.getTrueSource() instanceof EntityPlayer) {
               if (SRPConfigSystems.useEvolution) {
                  spawnBeckonE(data, world, in);
               } else {
                  spawnBeckonNE(data, (double)SRPConfigSystems.rschance, world, in);
               }
            }
         } else if (SRPConfigSystems.useEvolution) {
            spawnBeckonE(data, world, in);
         } else {
            spawnBeckonNE(data, (double)SRPConfigSystems.rschance, world, in);
         }
      }

   }

   public static void spawnBeckonNE(SRPWorldData data, double chance, World world, EntityParasiteBase in) {
      long worldT = world.getWorldTime();
      long seconds = (worldT - SRPAttributes.lastTimeD1) / 20L;
      Random rand = new Random();
      if (rand.nextDouble() < chance && data.getCurrentV() < SRPConfigSystems.rsMaxVenkrols && (long)SRPConfigSystems.rsCooldown < Math.abs(seconds) && SummonM(in, new String[]{"srparasites:beckon_si;1;1"}, 7, in.getAttackTarget())) {
         if (SRPConfigSystems.rsSounds) {
            in.playSound(SRPSounds.VENKROLSI, 4.0F, 1.0F);
         }

         data.setCurrentV(1);
         SRPAttributes.lastTimeD1 = worldT;
      }

   }

   public static void spawnBeckonE(SRPWorldData data, World world, EntityParasiteBase in) {
      switch(data.getEvolutionPhase()) {
      case 1:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceOne, world, in);
         break;
      case 2:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceTwo, world, in);
         break;
      case 3:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceThree, world, in);
         break;
      case 4:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceFour, world, in);
         break;
      case 5:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceFive, world, in);
         break;
      case 6:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceSix, world, in);
         break;
      case 7:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceSeven, world, in);
         break;
      case 8:
         spawnBeckonNE(data, SRPConfigSystems.reinforcementSystemChanceEight, world, in);
      }

   }

   public static boolean alertOthers(EntityParasiteBase pin, EntityLivingBase target, World world, int loop) {
      if (target == null) {
         return false;
      } else if (!(target instanceof EntityPlayer) && SRPConfigSystems.oneMindPlayer) {
         return false;
      } else if (!target.isEntityAlive()) {
         return false;
      } else if (loop <= 0) {
         return false;
      } else if (SRPConfigSystems.useEvolution && pin.getPhaseCreated() < SRPConfigSystems.evolutionOneMind) {
         return false;
      } else {
         --loop;
         int cap = SRPConfigSystems.oneMinRangeCap;
         double d0 = pin.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();

         for(EntityParasiteBase entitycreature : world.getEntitiesWithinAABB(EntityParasiteBase.class, (new AxisAlignedBB(pin.posX, pin.posY, pin.posZ, pin.posX + 1.0D, pin.posY + 1.0D, pin.posZ + 1.0D)).grow(d0, 10.0D, d0))) {
            if (pin != entitycreature && entitycreature.isEntityAlive() && !(entitycreature instanceof IRangedAttackMob) && !(entitycreature instanceof EntityPStationary) && !(entitycreature instanceof EntityBiomass) && !(entitycreature instanceof EntityCanFly) && (entitycreature.getAttackTarget() == null || !entitycreature.getAttackTarget().isEntityAlive())) {
               if (entitycreature.canEntityBeSeen(target)) {
                  if (SRPPotions.applySense(entitycreature, 200, entitycreature.getDistanceSq(target), cap)) {
                     entitycreature.setAttackTarget(target);
                     alertOthers(entitycreature, target, world, loop);
                  } else if (SRPPotions.applySense(entitycreature, 200, entitycreature.getDistanceSq(pin), cap)) {
                     entitycreature.setAttackTarget(pin);
                     alertOthers(entitycreature, target, world, loop);
                  }
               } else if (SRPPotions.applySense(entitycreature, 200, entitycreature.getDistanceSq(pin), cap)) {
                  entitycreature.setAttackTarget(pin);
                  alertOthers(entitycreature, target, world, loop);
               }
            }
         }

         return false;
      }
   }

   public static void leaveScent(World world, DamageSource cause, EntityParasiteBase in) {
      if (SRPConfigSystems.useOneMind) {
         if (cause.getTrueSource() instanceof EntityLivingBase) {
            if (cause.getTrueSource() instanceof EntityPlayer || !SRPConfigSystems.oneMindPlayer) {
               if (!SRPConfigSystems.useEvolution || in.getPhaseCreated() >= SRPConfigSystems.evolutionOneMind) {
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(in.posX, in.posY, in.posZ, in.posX + 1.0D, in.posY + 1.0D, in.posZ + 1.0D)).grow(64.0D);

                  for(EntityParasiticScent mob : world.getEntitiesWithinAABB(EntityParasiticScent.class, axisalignedbb)) {
                     if (in.canEntityBeSeen(mob)) {
                        mob.increaseDanger(in.getCCDeathValue(), true);
                        mob.increaseActivity(1, true);
                        mob.setScentLife(mob.getScentLife() + 20 * SRPConfigSystems.scentLifeDeath);
                        mob.setScentReaction(getScentReactionBonus(in.getPhaseCreated()), false);
                        mob.setTargetToKill((EntityLivingBase)cause.getTrueSource());
                        if (cause.getTrueSource() instanceof EntityPlayer) {
                           ((EntityPlayer)cause.getTrueSource()).sendStatusMessage(new TextComponentString("Closest Scent was notified"), true);
                        }

                        return;
                     }
                  }

                  axisalignedbb = (new AxisAlignedBB(in.posX, in.posY, in.posZ, in.posX + 1.0D, in.posY + 1.0D, in.posZ + 1.0D)).grow(64.0D);
                  List<Entity> moblist2 = world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
                  if (moblist2.size() <= 3 && in.getPhaseCreated() >= 0) {
                     EntityParasiticScent nut = new EntityParasiticScent(world, 0, (EntityLivingBase)cause.getTrueSource());
                     nut.copyLocationAndAnglesFrom(cause.getTrueSource());
                     nut.setScentLife(SRPConfigSystems.scentLifeObserver * 20);
                     nut.increaseDanger(in.getCCDeathValue() + getScentBonus(in.getPhaseCreated()), true);
                     nut.setScentReaction(getScentReactionBonus(in.getPhaseCreated()), false);
                     world.spawnEntity(nut);
                     nut.warnPlayers("A Scent has been deployed in this area");
                  }

               }
            }
         }
      }
   }

   public static int getScentBonus(byte in) {
      int q = 1;
      if (SRPConfigSystems.useEvolution) {
         switch(in) {
         case 0:
            q = SRPConfigSystems.phaseScentBonusZero;
            break;
         case 1:
            q = SRPConfigSystems.phaseScentBonusOne;
            break;
         case 2:
            q = SRPConfigSystems.phaseScentBonusTwo;
            break;
         case 3:
            q = SRPConfigSystems.phaseScentBonusThree;
            break;
         case 4:
            q = SRPConfigSystems.phaseScentBonusFour;
            break;
         case 5:
            q = SRPConfigSystems.phaseScentBonusFive;
            break;
         case 6:
            q = SRPConfigSystems.phaseScentBonusSix;
            break;
         case 7:
            q = SRPConfigSystems.phaseScentBonusSeven;
            break;
         case 8:
            q = SRPConfigSystems.phaseScentBonusEight;
         }
      }

      return q;
   }

   public static byte getScentReactionBonus(byte in) {
      byte q = SRPConfigSystems.scentGoActive;
      if (SRPConfigSystems.useEvolution) {
         switch(in) {
         case 0:
            q = SRPConfigSystems.phaseScentReactionZero;
            break;
         case 1:
            q = SRPConfigSystems.phaseScentReactionOne;
            break;
         case 2:
            q = SRPConfigSystems.phaseScentReactionTwo;
            break;
         case 3:
            q = SRPConfigSystems.phaseScentReactionThree;
            break;
         case 4:
            q = SRPConfigSystems.phaseScentReactionFour;
            break;
         case 5:
            q = SRPConfigSystems.phaseScentReactionFive;
            break;
         case 6:
            q = SRPConfigSystems.phaseScentReactionSix;
            break;
         case 7:
            q = SRPConfigSystems.phaseScentReactionSeven;
            break;
         case 8:
            q = SRPConfigSystems.phaseScentReactionEight;
         }
      }

      return q;
   }

   public static void checkColony(World world, DamageSource cause, EntityPMalleable in) {
      if (!in.isBurning() && SRPConfigWorld.coloniesActivated) {
         if (ParasiteEventWorld.numberofColonies(world) > 0) {
            double chance = 0.0D;
            if (in.isPotionActive(SRPPotions.LINK_E)) {
               chance = (double)(in.getActivePotionEffect(SRPPotions.LINK_E).getAmplifier() + 1) * SRPConfigSystems.adapsChance;
            }

            if (ParasiteEventWorld.rangeOfColony(world, in.getPosition(), true) != null || world.rand.nextDouble() < chance) {
               SRPWorldData data = SRPWorldData.get(world);
               if (in.colonySpawned) {
                  in.removeCommonDamage(data.getMostCommonDamageS(), data.getMostCommonDamageI());
               }

               String da = in.getMostCommonDamage();
               if (da == null) {
                  return;
               }

               data.addGlobalResistance(da);
               double d0 = world.rand.nextGaussian() * 0.02D;
               double d1 = world.rand.nextGaussian() * 0.02D;
               double d2 = world.rand.nextGaussian() * 0.02D;
               SRPMain.network.sendToAll(new SRPPacketParticle(in.posX, in.posY, in.posZ, in.width, in.height, (byte)4));
            }

         }
      }
   }

   public static SRPExplotion createExplosion(World worldIn, @Nullable Entity entityIn, double x, double y, double z, float strength, boolean isSmoking) {
      SRPExplotion explosion = new SRPExplotion(worldIn, entityIn, x, y, z, strength, false, isSmoking);
      if (ForgeEventFactory.onExplosionStart(worldIn, explosion)) {
         return explosion;
      } else {
         explosion.doExplosionA();
         explosion.doExplosionB(false);
         return explosion;
      }
   }

   public static boolean teleportDigging(EntityParasiteBase in, float maxHardness, BlockPos posIn, int range, int mini) {
      if (!in.isPotionActive(MobEffects.SLOWNESS)) {
         in.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 4, false, false));
         return false;
      } else {
         Random rand = new Random();
         double x = (double)posIn.getX();
         double y = (double)posIn.getY();
         double z = (double)posIn.getZ();
         double randomx = (double)(rand.nextInt(range) + mini);
         double randomz = (double)(rand.nextInt(range) + mini);
         double negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomx *= -1.0D;
         }

         negative = (double)rand.nextInt(2);
         if (negative == 0.0D) {
            randomz *= -1.0D;
         }

         int limit = 0;

         for(boolean flag = true; flag; ++limit) {
            if (limit >= 5) {
               return false;
            }

            BlockPos pos = new BlockPos(x + randomx, y, z + randomz);
            pos = getFloor(in.world, pos, 5);
            if (pos != null && in.world.getBlockState(pos.down()).isFullCube()) {
               float bHard = 0.0F;
               IBlockState state = in.world.getBlockState(pos.down());
               float atm = state.getBlockHardness(in.world, pos.down());
               if (atm <= 0.0F) {
                  return false;
               }

               bHard = bHard + atm;
               state = in.world.getBlockState(pos.down(2));
               atm = state.getBlockHardness(in.world, pos.down(2));
               if (atm <= 0.0F) {
                  return false;
               }

               bHard = bHard + atm;
               state = in.world.getBlockState(pos.down(3));
               atm = state.getBlockHardness(in.world, pos.down(3));
               if (atm <= 0.0F) {
                  return false;
               }

               bHard = bHard + atm;
               if (bHard >= maxHardness) {
                  return false;
               }

               AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow(1.0D);
               List<EntityLivingBase> moblist = in.world.getEntitiesWithinAABB(EntityNak.class, axisalignedbb);
               if (moblist.isEmpty()) {
                  in.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, in.rotationYaw, in.rotationPitch);
                  flag = false;
                  return true;
               }
            }

            randomx = (double)(rand.nextInt(range) + mini);
            randomz = (double)(rand.nextInt(range) + mini);
            negative = (double)rand.nextInt(2);
            if (negative == 0.0D) {
               randomx *= -1.0D;
            }

            negative = (double)rand.nextInt(2);
            if (negative == 0.0D) {
               randomz *= -1.0D;
            }
         }

         return false;
      }
   }
}
