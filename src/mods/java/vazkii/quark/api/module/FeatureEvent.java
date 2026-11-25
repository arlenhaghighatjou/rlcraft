package vazkii.quark.api.module;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class FeatureEvent extends Event {
   private final IFeature feature;

   public FeatureEvent(IFeature feature) {
      this.feature = feature;
   }

   public IFeature getFeature() {
      return this.feature;
   }

   public static class Disabled extends FeatureEvent {
      public Disabled(IFeature feature) {
         super(feature);
      }
   }

   public static class Enabled extends FeatureEvent {
      public Enabled(IFeature feature) {
         super(feature);
      }
   }

   @Cancelable
   public static class Loaded extends FeatureEvent {
      public Loaded(IFeature feature) {
         super(feature);
      }
   }

   public static class PostDisable extends FeatureEvent {
      public PostDisable(IFeature feature) {
         super(feature);
      }
   }

   public static class PostEnable extends FeatureEvent {
      public PostEnable(IFeature feature) {
         super(feature);
      }
   }
}
