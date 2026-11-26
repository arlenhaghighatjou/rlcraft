package com.lycanitesmobs.core.info.projectile.behaviours;

import net.minecraft.entity.EntityList;
import com.google.gson.JsonObject;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageGUIRequest;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ProjectileBehaviourSummon extends ProjectileBehaviour {
   public String summonMobId;
   public boolean summonMinion = false;
   public double summonChance = 0.05D;
   public int summonDuration = 60;
   public int summonCountMin = 1;
   public int summonCountMax = 1;
   public double sizeScale = 1.0D;

   public ProjectileBehaviourSummon() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("summonMobId")) {
         this.summonMobId = json.get("summonMobId").getAsString();
      }

      if (json.has("summonMinion")) {
         this.summonMinion = json.get("summonMinion").getAsBoolean();
      }

      if (json.has("summonChance")) {
         this.summonChance = json.get("summonChance").getAsDouble();
      }

      if (json.has("summonDuration")) {
         this.summonDuration = json.get("summonDuration").getAsInt();
      }

      if (json.has("summonCountMin")) {
         this.summonCountMin = json.get("summonCountMin").getAsInt();
      }

      if (json.has("summonCountMax")) {
         this.summonCountMax = json.get("summonCountMax").getAsInt();
      }

      if (json.has("sizeScale")) {
         this.sizeScale = json.get("sizeScale").getAsDouble();
      }

   }

   public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
      if (projectile != null && !projectile.getEntityWorld().isRemote) {
         if (!(projectile.getThrower() instanceof EntityPlayer) || CreatureManager.getInstance().config.isSummoningAllowed(world)) {
            Class entityClass = null;
            SummonSet summonSet = null;
            if (this.summonMinion) {
               if (!(projectile.getThrower() instanceof EntityPlayer)) {
                  return;
               }

               EntityPlayer player = (EntityPlayer)projectile.getThrower();
               ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
               if (extendedPlayer == null) {
                  return;
               }

               summonSet = extendedPlayer.getSelectedSummonSet();
               if (summonSet == null || summonSet.getCreatureClass() == null) {
                  if (player instanceof EntityPlayerMP) {
                     MessageGUIRequest messageGUIRequest = new MessageGUIRequest(GuiHandler.GuiType.BEASTIARY.id);
                     LycanitesMobs.packetHandler.sendToPlayer(messageGUIRequest, (EntityPlayerMP)player);
                  }

                  return;
               }

               entityClass = summonSet.getCreatureClass();
            }

            if (entityClass == null && this.summonMobId != null) {
               CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(this.summonMobId);
               if (creatureInfo != null) {
                  entityClass = creatureInfo.entityClass;
               } else {
                  EntityEntry entry = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.summonMobId));
                  if (entry != null) {
                     entityClass = entry.getEntityClass();
                  }
               }
            }

            if (entityClass != null) {
               int summonCount = this.summonCountMin;
               if (this.summonCountMax > this.summonCountMin) {
                  int var10002 = this.summonCountMax - this.summonCountMin;
                  summonCount = this.summonCountMin + projectile.getEntityWorld().rand.nextInt(var10002);
               }

               for(int i = 0; i < summonCount; ++i) {
                  if (projectile.getEntityWorld().rand.nextDouble() <= this.summonChance) {
                     try {
                        EntityLiving entity = (EntityLiving)EntityList.newEntity(entityClass, projectile.getEntityWorld());
                        if (entity instanceof BaseCreatureEntity) {
                           BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
                           entityCreature.setMinion(true);
                           entityCreature.setTemporary(this.summonDuration);
                           entityCreature.setSizeScale(this.sizeScale);
                           if (projectile.getThrower() instanceof EntityPlayer && entityCreature instanceof TameableCreatureEntity) {
                              TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                              entityTameable.setPlayerOwner((EntityPlayer)projectile.getThrower());
                              entityTameable.setSitting(false);
                              entityTameable.setFollowing(true);
                              entityTameable.setPassive(false);
                              entityTameable.setAssist(true);
                              entityTameable.setAggressive(true);
                              if (summonSet != null) {
                                 summonSet.applyBehaviour(entityTameable);
                                 entityTameable.setSubspecies(summonSet.subspecies);
                                 entityTameable.applyVariant(summonSet.variant);
                              }
                           }

                           float randomAngle = 45.0F + 45.0F * projectile.getEntityWorld().rand.nextFloat();
                           if (projectile.getEntityWorld().rand.nextBoolean()) {
                              randomAngle = -randomAngle;
                           }

                           BlockPos spawnPos = entityCreature.getFacingPosition(projectile, -1.0D, (double)randomAngle);
                           entity.setLocationAndAngles((double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), projectile.rotationYaw, 0.0F);
                           projectile.getEntityWorld().spawnEntity(entity);
                        }
                     } catch (Exception var12) {
                        var12.printStackTrace();
                     }
                  }
               }

            }
         }
      }
   }
}
