package xzeroair.trinkets.races.mixed;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.util.helpers.AttributeHelper;

public class RaceMixed extends EntityRacePropertiesHandler {
   protected EntityRace main;
   protected EntityRace sub;

   public RaceMixed(@Nonnull EntityLivingBase e, @Nonnull EntityRace mainRace, @Nonnull EntityRace subRace) {
      super(e, (EntityRace)null);
      this.main = mainRace;
      this.sub = subRace;
   }

   public boolean isMixed() {
      return true;
   }

   public void startTransformation() {
   }

   public void endTransformation() {
      AttributeHelper.removeAttributes(this.entity, this.race.getUUID());
   }

   public void onTick() {
   }

   public void whileTransformed() {
   }
}
