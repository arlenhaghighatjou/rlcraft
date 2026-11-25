package org.orecruncher.dsurround.registry.sound;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.util.SoundCategory;
import org.orecruncher.dsurround.registry.config.SoundMetadataConfig;

public class SoundMetadata {
   protected final String title;
   protected final SoundCategory category;
   protected final List<String> credits;

   protected SoundMetadata(@Nonnull SoundMetadataConfig cfg) {
      this.title = cfg.title;
      this.category = (SoundCategory)MoreObjects.firstNonNull(this.translate(cfg.category), SoundCategory.NEUTRAL);
      this.credits = ImmutableList.copyOf(cfg.credits);
   }

   protected SoundCategory translate(@Nonnull String catName) {
      if ("ds_biome".equals(catName)) {
         return SoundRegistry.BIOME;
      } else {
         return "ds_footsteps".equals(catName) ? SoundRegistry.FOOTSTEPS : SoundCategory.getByName(catName);
      }
   }

   public String getTitle() {
      return this.title;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public List<String> getCredits() {
      return this.credits;
   }
}
