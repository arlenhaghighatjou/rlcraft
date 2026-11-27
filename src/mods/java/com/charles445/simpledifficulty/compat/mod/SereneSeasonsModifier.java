package com.charles445.simpledifficulty.compat.mod;

import sereneseasons.api.season.SeasonHelper;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SereneSeasonsModifier extends ModifierBase {
   public SereneSeasonsModifier() {
      super("SereneSeasons");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      if (world.provider.isSurfaceWorld() && ModConfig.server.compatibility.toggles.sereneSeasons) {
         switch(SeasonHelper.getSeasonState(world).getSubSeason()) {
         case EARLY_AUTUMN:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlyAutumn, world, pos);
         case EARLY_SPRING:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlySpring, world, pos);
         case EARLY_SUMMER:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlySummer, world, pos);
         case EARLY_WINTER:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonEarlyWinter, world, pos);
         case LATE_AUTUMN:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateAutumn, world, pos);
         case LATE_SPRING:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateSpring, world, pos);
         case LATE_SUMMER:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateSummer, world, pos);
         case LATE_WINTER:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonLateWinter, world, pos);
         case MID_AUTUMN:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidAutumn, world, pos);
         case MID_SPRING:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidSpring, world, pos);
         case MID_SUMMER:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidSummer, world, pos);
         case MID_WINTER:
            return this.applyUndergroundEffect((float)ModConfig.server.compatibility.sereneseasons.seasonMidWinter, world, pos);
         default:
            return 0.0F;
         }
      }

      return 0.0F;
   }
}
