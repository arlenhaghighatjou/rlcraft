package org.orecruncher.dshuds.compat;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Localization;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;

@SideOnly(Side.CLIENT)
public class SereneSeasonHandler implements Function<World, String> {
   protected static final String noSeason = Localization.loadString("dshuds.season.noseason");

   public SereneSeasonHandler() {
   }

   public String apply(@Nonnull World world) {
      ISeasonState ss = SeasonHelper.getSeasonState(world);
      SeasonType season = SeasonType.getSeasonType(ss);
      if (season == SeasonType.NONE) {
         return noSeason;
      } else {
         SeasonType.SubType sub = SeasonType.getSeasonSubType(ss);
         String seasonStr = Localization.loadString("dshuds.season." + season.getValue());
         String subSeasonStr = Localization.loadString("dshuds.season." + sub.getValue());
         return Localization.format("dshuds.season.format", new Object[]{subSeasonStr, seasonStr});
      }
   }
}
