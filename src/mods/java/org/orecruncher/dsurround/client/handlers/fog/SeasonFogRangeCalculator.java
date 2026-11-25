package org.orecruncher.dsurround.client.handlers.fog;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.SeasonType;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

@SideOnly(Side.CLIENT)
public class SeasonFogRangeCalculator extends MorningFogRangeCalculator {
   private static final Map<SeasonFogRangeCalculator.SeasonKey, MorningFogRangeCalculator.FogType> MAPPING = new Object2ReferenceOpenHashMap();

   public SeasonFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "SeasonFogRangeCalculator";
   }

   public MorningFogRangeCalculator.FogType getFogType() {
      World world = EnvironStateHandler.EnvironState.getWorld();
      ISeasonInfo cap = CapabilitySeasonInfo.getCapability(world);
      if (cap != null) {
         SeasonType t = cap.getSeasonType();
         SeasonType.SubType st = cap.getSeasonSubType();
         MorningFogRangeCalculator.FogType type = (MorningFogRangeCalculator.FogType)MAPPING.get(new SeasonFogRangeCalculator.SeasonKey(t, st));
         if (type != null) {
            return type;
         }
      }

      return super.getFogType();
   }

   static {
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.AUTUMN, SeasonType.SubType.EARLY), MorningFogRangeCalculator.FogType.NORMAL);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.AUTUMN, SeasonType.SubType.MID), MorningFogRangeCalculator.FogType.MEDIUM);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.AUTUMN, SeasonType.SubType.LATE), MorningFogRangeCalculator.FogType.HEAVY);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.WINTER, SeasonType.SubType.EARLY), MorningFogRangeCalculator.FogType.MEDIUM);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.WINTER, SeasonType.SubType.MID), MorningFogRangeCalculator.FogType.LIGHT);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.WINTER, SeasonType.SubType.LATE), MorningFogRangeCalculator.FogType.NORMAL);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.SPRING, SeasonType.SubType.EARLY), MorningFogRangeCalculator.FogType.MEDIUM);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.SPRING, SeasonType.SubType.MID), MorningFogRangeCalculator.FogType.HEAVY);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.SPRING, SeasonType.SubType.LATE), MorningFogRangeCalculator.FogType.NORMAL);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.SUMMER, SeasonType.SubType.EARLY), MorningFogRangeCalculator.FogType.LIGHT);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.SUMMER, SeasonType.SubType.MID), MorningFogRangeCalculator.FogType.NONE);
      MAPPING.put(new SeasonFogRangeCalculator.SeasonKey(SeasonType.SUMMER, SeasonType.SubType.LATE), MorningFogRangeCalculator.FogType.LIGHT);
   }

   private static class SeasonKey implements Entry<SeasonType, SeasonType.SubType> {
      private final SeasonType season;
      private final SeasonType.SubType subType;

      public SeasonKey(@Nonnull SeasonType s, @Nonnull SeasonType.SubType st) {
         this.season = s;
         this.subType = st;
      }

      @Nonnull
      public SeasonType getKey() {
         return this.season;
      }

      @Nonnull
      public SeasonType.SubType getValue() {
         return this.subType;
      }

      public SeasonType.SubType setValue(@Nonnull SeasonType.SubType value) {
         return null;
      }

      public int hashCode() {
         return this.season.hashCode() ^ 31 * this.subType.hashCode();
      }

      public boolean equals(@Nonnull Object key) {
         SeasonFogRangeCalculator.SeasonKey sk = (SeasonFogRangeCalculator.SeasonKey)key;
         return this.season == sk.season && this.subType == sk.subType;
      }
   }
}
