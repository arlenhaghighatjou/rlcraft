package net.silentchaos512.scalinghealth.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.scalinghealth.config.Config;

public class EntityDifficultyChangeList {
   private Map<String, EntityDifficultyChangeList.DifficultyChanges> map = new HashMap();

   public EntityDifficultyChangeList() {
   }

   @Nonnull
   public EntityDifficultyChangeList.DifficultyChanges get(Entity entity) {
      ResourceLocation resource = EntityList.getKey(entity);
      if (resource == null) {
         return this.defaultValues(entity);
      } else {
         String id = resource.toString();
         String idOld = EntityList.getEntityString(entity);

         for(Entry<String, EntityDifficultyChangeList.DifficultyChanges> entry : this.map.entrySet()) {
            String key = (String)entry.getKey();
            if (key.equalsIgnoreCase(id) || key.equalsIgnoreCase(idOld) || key.equalsIgnoreCase("minecraft:" + id)) {
               return (EntityDifficultyChangeList.DifficultyChanges)entry.getValue();
            }
         }

         return this.defaultValues(entity);
      }
   }

   public void put(String entityId, float onStandardKill, float onBlightKill) {
      this.map.put(entityId, new EntityDifficultyChangeList.DifficultyChanges(onStandardKill, onBlightKill));
   }

   public void clear() {
      this.map.clear();
   }

   public EntityDifficultyChangeList.DifficultyChanges defaultValues(Entity entity) {
      boolean isBoss = !entity.isNonBoss();
      return new EntityDifficultyChangeList.DifficultyChanges(isBoss ? Config.Difficulty.perBossKill : (entity instanceof IMob ? Config.Difficulty.perHostileKill : Config.Difficulty.perPassiveKill), Config.Difficulty.perBlightKill + (isBoss ? Config.Difficulty.perBossKill : 0.0F));
   }

   public static class DifficultyChanges {
      public final float onStandardKill;
      public final float onBlightKill;

      public DifficultyChanges(float standard, float blight) {
         this.onStandardKill = standard;
         this.onBlightKill = blight;
      }
   }
}
