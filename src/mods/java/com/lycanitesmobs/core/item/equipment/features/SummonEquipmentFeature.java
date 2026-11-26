package com.lycanitesmobs.core.item.equipment.features;

import net.minecraft.entity.EntityList;
import com.google.gson.JsonObject;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SummonEquipmentFeature extends EquipmentFeature {
   public String summonMobId;
   public double summonChance = 0.05D;
   public int summonDuration = 60;
   public int summonCountMin = 1;
   public int summonCountMax = 1;
   public double sizeScale = 1.0D;

   public SummonEquipmentFeature() {
   }

   public void loadFromJSON(JsonObject json) {
      super.loadFromJSON(json);
      this.summonMobId = json.get("summonMobId").getAsString();
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

   public String getDescription(ItemStack itemStack, int level) {
      if (!this.isActive(itemStack, level)) {
         return null;
      } else {
         String description = LanguageManager.translate("equipment.feature." + this.featureType) + " " + LanguageManager.translate("entity." + this.summonMobId.replace("lycanitesmobs.", "") + ".name");
         if (this.summonCountMin != this.summonCountMax) {
            description = description + " x" + this.summonCountMin + " - " + this.summonCountMax;
         } else {
            description = description + " x" + this.summonCountMax;
         }

         description = description + " " + Math.round(this.summonChance * 100.0D) + "%";
         if (this.summonDuration > 0) {
            description = description + " " + (float)this.summonDuration / 20.0F + "s";
         }

         return description;
      }
   }

   public String getSummary(ItemStack itemStack, int level) {
      return !this.isActive(itemStack, level) ? null : LanguageManager.translate("entity." + this.summonMobId.replace("lycanitesmobs.", "") + ".name");
   }

   public boolean onHitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase attacker) {
      if (target != null && attacker != null && !attacker.getEntityWorld().isRemote) {
         int summonCount = this.summonCountMin;
         if (this.summonCountMax > this.summonCountMin) {
            summonCount = this.summonCountMin + attacker.getRNG().nextInt(this.summonCountMax - this.summonCountMin);
         }

         int summonedCreatures = 0;

         for(int i = 0; i < summonCount; ++i) {
            if (attacker.getRNG().nextDouble() <= this.summonChance) {
               try {
                  Class entityClass = null;
                  CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(this.summonMobId);
                  if (creatureInfo != null) {
                     entityClass = creatureInfo.entityClass;
                  } else {
                     EntityEntry entry = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.summonMobId));
                     if (entry != null) {
                        entityClass = entry.getEntityClass();
                     }
                  }

                  if (entityClass != null) {
                     EntityLiving entity = (EntityLiving)EntityList.newEntity(entityClass, attacker.getEntityWorld());
                     if (entity instanceof BaseCreatureEntity) {
                        BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
                        entityCreature.setMinion(true);
                        entityCreature.setTemporary(this.summonDuration * 20);
                        entityCreature.setSizeScale(this.sizeScale);
                        if (attacker instanceof EntityPlayer && entityCreature instanceof TameableCreatureEntity) {
                           TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                           entityTameable.setPlayerOwner((EntityPlayer)attacker);
                           entityTameable.setSitting(false);
                           entityTameable.setFollowing(true);
                           entityTameable.setPassive(false);
                           entityTameable.setAssist(true);
                           entityTameable.setAggressive(true);
                           entityTameable.setPVP(target instanceof EntityPlayer);
                        }

                        float randomAngle = 45.0F + 45.0F * attacker.getRNG().nextFloat();
                        if (attacker.getRNG().nextBoolean()) {
                           randomAngle = -randomAngle;
                        }

                        BlockPos spawnPos = entityCreature.getFacingPosition(attacker, -1.0D, (double)randomAngle);
                        entity.setLocationAndAngles((double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), attacker.rotationYaw, 0.0F);
                        attacker.getEntityWorld().spawnEntity(entity);
                        entityCreature.setAttackTarget(target);
                        ++summonedCreatures;
                     }
                  }
               } catch (Exception var13) {
                  var13.printStackTrace();
               }
            }
         }

         return summonedCreatures > 0;
      } else {
         return false;
      }
   }
}
