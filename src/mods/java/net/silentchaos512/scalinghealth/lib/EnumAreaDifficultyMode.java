package net.silentchaos512.scalinghealth.lib;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;

public enum EnumAreaDifficultyMode {
   WEIGHTED_AVERAGE,
   AVERAGE,
   MIN_LEVEL,
   MAX_LEVEL,
   DISTANCE_FROM_SPAWN,
   DISTANCE_FROM_ORIGIN,
   DISTANCE_AND_TIME,
   SERVER_WIDE;

   private EnumAreaDifficultyMode() {
   }

   public static EnumAreaDifficultyMode loadFromConfig(Configuration c, EnumAreaDifficultyMode defaultValue) {
      String[] validValues = new String[values().length];

      for(int i = 0; i < values().length; ++i) {
         validValues[i] = values()[i].name();
      }

      String str = c.getString("Area Mode", "main.difficulty", defaultValue.name(), "Defines how the area difficulty is determined when spawning a mob.\n  AVERAGE - The average difficulty level of all nearby players.\n  WEIGHTED_AVERAGE - Similar to average, but closer players have a greater impact on difficulty.\n  MIN_LEVEL - The lowest difficulty level of all nearby players.\n  MAX_LEVEL - The highest difficulty level of all nearby players.\n  DISTANCE_FROM_SPAWN - Based on the mob's distance from spawn.\n  DISTANCE_FROM_ORIGIN - Based on the mob's distance from the origin.\n  DISTANCE_AND_TIME - Mix of DISTANCE_FROM_SPAWN and WEIGHTED_AVERAGE.\n  SERVER_WIDE - Difficulty is tracked at a server level, individual player difficulty has no impact.", validValues);

      for(EnumAreaDifficultyMode mode : values()) {
         if (mode.name().equalsIgnoreCase(str)) {
            return mode;
         }
      }

      return defaultValue;
   }

   public double getAreaDifficulty(World world, BlockPos pos) {
      return this.getAreaDifficulty(world, pos, true);
   }

   public double getAreaDifficulty(World world, BlockPos pos, boolean addGroupBonus) {
      return this.getAreaDifficulty(world, pos, addGroupBonus, true);
   }

   public double getAreaDifficulty(World world, BlockPos pos, boolean addGroupBonus, boolean clampValue) {
      if (!world.isRemote && !world.getGameRules().getBoolean("ScalingHealthDifficulty")) {
         return 0.0D;
      } else {
         int radius = Config.Difficulty.searchRadius;
         long radiusSquared = radius <= 0 ? Long.MAX_VALUE : (long)(radius * radius);
         radius = radius <= 0 ? Integer.MAX_VALUE : radius;
         List<EntityPlayer> players = world.getPlayers(EntityPlayer.class, (p) -> {
            return p.getDistanceSq(pos) <= (double)radiusSquared;
         });
         if (players.isEmpty()) {
            return 0.0D;
         } else {
            double total = 0.0D;
            double ret = 0.0D;
            BlockPos origin = BlockPos.ORIGIN;
            switch(this) {
            case WEIGHTED_AVERAGE:
               int totalWeight = 0;

               for(EntityPlayer player : players) {
                  SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
                  if (data != null) {
                     int distance = (int)pos.getDistance((int)player.posX, pos.getY(), (int)player.posZ);
                     int weight = (radius - distance) / 16 + 1;
                     total += (double)weight * data.getDifficulty();
                     totalWeight += weight;
                  }
               }

               ret = totalWeight <= 0 ? 0.0D : total / (double)totalWeight;
               break;
            case AVERAGE:
               for(EntityPlayer player : players) {
                  SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
                  if (data != null) {
                     total += data.getDifficulty();
                  }
               }

               ret = total / (double)players.size();
               break;
            case MAX_LEVEL:
               double max = 0.0D;

               for(EntityPlayer player : players) {
                  SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
                  if (data != null) {
                     double d = data.getDifficulty();
                     max = Math.max(d, max);
                  }
               }

               ret = max;
               break;
            case MIN_LEVEL:
               double min = (double)Config.Difficulty.maxValue;

               for(EntityPlayer player : players) {
                  SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
                  if (data != null) {
                     double d = data.getDifficulty();
                     min = Math.min(d, min);
                  }
               }

               ret = min;
               break;
            case DISTANCE_FROM_SPAWN:
               origin = world.getSpawnPoint();
            case DISTANCE_FROM_ORIGIN:
               double dx = (double)(pos.getX() - origin.getX());
               double dz = (double)(pos.getZ() - origin.getZ());
               double distance = Math.sqrt(dx * dx + dz * dz);
               ret = distance * (double)Config.Difficulty.perBlock;
               break;
            case DISTANCE_AND_TIME:
               double diffFromPlayers = WEIGHTED_AVERAGE.getAreaDifficulty(world, pos, false, false);
               double diffFromDistance = DISTANCE_FROM_SPAWN.getAreaDifficulty(world, pos, false, false);
               ret = diffFromPlayers + diffFromDistance;
               break;
            case SERVER_WIDE:
               ScalingHealthSavedData data = ScalingHealthSavedData.get(world);
               if (data != null) {
                  ret = data.difficulty;
               }
            }

            if (clampValue) {
               ret = MathHelper.clamp(ret, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
            }

            if (addGroupBonus) {
               ret *= (double)(1.0F + Config.Difficulty.groupAreaBonus * (float)(players.size() - 1));
            }

            SimpleExpression dimensionFactor = (SimpleExpression)Config.Difficulty.DIMENSION_VALUE_FACTOR.get(Integer.valueOf(world.provider.getDimension()));
            if (dimensionFactor != null) {
               ret = dimensionFactor.apply(ret);
            }

            return ret;
         }
      }
   }
}
