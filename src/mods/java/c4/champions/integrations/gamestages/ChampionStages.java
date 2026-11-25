package c4.champions.integrations.gamestages;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ChampionStages {
   private static final Map<String, ChampionStages.Info> ENTITY_STAGE_INFO = Maps.newHashMap();
   private static final Map<Integer, ChampionStages.Info> TIER_STAGE_INFO = Maps.newHashMap();

   public ChampionStages() {
   }

   public static void addStage(String entity, String stage) {
      ENTITY_STAGE_INFO.merge(entity, new ChampionStages.Info(stage), (k, v) -> {
         v.addStage(stage);
         return v;
      });
   }

   public static void addStage(String entity, String stage, int dimension) {
      ENTITY_STAGE_INFO.merge(entity, new ChampionStages.Info(stage, dimension), (k, v) -> {
         v.addStage(stage, dimension);
         return v;
      });
   }

   public static void addTierStage(int tier, String stage) {
      TIER_STAGE_INFO.merge(Integer.valueOf(tier), new ChampionStages.Info(stage), (k, v) -> {
         v.addStage(stage);
         return v;
      });
   }

   public static void addTierStage(int tier, String stage, int dimension) {
      TIER_STAGE_INFO.merge(Integer.valueOf(tier), new ChampionStages.Info(stage, dimension), (k, v) -> {
         v.addStage(stage);
         return v;
      });
   }

   @Nullable
   private static ChampionStages.Info getStageInfo(String entity) {
      return (ChampionStages.Info)ENTITY_STAGE_INFO.get(entity);
   }

   @Nullable
   private static ChampionStages.Info getStageInfo(int tier) {
      return (ChampionStages.Info)TIER_STAGE_INFO.get(Integer.valueOf(tier));
   }

   public static boolean canSpawn(EntityLiving living) {
      ResourceLocation rl = EntityList.getKey(living);
      if (rl != null) {
         String id = rl.toString();
         ChampionStages.Info stageInfo = getStageInfo(id);
         if (stageInfo != null) {
            return hasRequiredStages(stageInfo, living);
         }
      }

      return true;
   }

   public static boolean isValidTier(int tier, EntityLiving living) {
      ChampionStages.Info stageInfo = getStageInfo(tier);
      return stageInfo != null ? hasRequiredStages(stageInfo, living) : true;
   }

   private static boolean hasRequiredStages(@Nonnull ChampionStages.Info info, @Nonnull EntityLiving living) {
      int dimension = living.dimension;
      Set<String> stages;
      if (info.dimensionalStages.containsKey(Integer.valueOf(dimension))) {
         stages = (Set)info.dimensionalStages.get(Integer.valueOf(dimension));
      } else {
         stages = info.globalStages;
      }

      if (stages.isEmpty()) {
         return true;
      } else {
         for(EntityPlayer player : living.world.playerEntities) {
            if (GameStageHelper.hasAllOf(player, stages) && player.getDistance(living) <= 256.0F) {
               return true;
            }
         }

         return false;
      }
   }

   public static class Info {
      Map<Integer, Set<String>> dimensionalStages = Maps.newHashMap();
      Set<String> globalStages = Sets.newHashSet();

      Info(String stage) {
         this.addStage(stage);
      }

      Info(String stage, int dimension) {
         this.addStage(stage, dimension);
      }

      void addStage(String stage) {
         this.globalStages.add(stage);
      }

      void addStage(String stage, int dimension) {
         this.dimensionalStages.merge(Integer.valueOf(dimension), Sets.newHashSet(new String[]{stage}), (k, v) -> {
            v.add(stage);
            return v;
         });
      }
   }
}
