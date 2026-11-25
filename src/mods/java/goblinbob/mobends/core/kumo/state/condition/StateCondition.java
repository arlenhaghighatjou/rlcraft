package goblinbob.mobends.core.kumo.state.condition;

import goblinbob.mobends.core.data.EntityData;
import goblinbob.mobends.core.kumo.state.template.MalformedKumoTemplateException;
import goblinbob.mobends.core.kumo.state.template.TriggerConditionTemplate;

public class StateCondition implements ITriggerCondition {
   private final StateCondition.State state;

   public StateCondition(StateCondition.Template template) throws MalformedKumoTemplateException {
      if (template.state == null) {
         throw new MalformedKumoTemplateException("No 'state' property given for trigger condition.");
      } else {
         this.state = template.state;
      }
   }

   public boolean isConditionMet(ITriggerConditionContext context) {
      EntityData<?> entityData = context.getEntityData();
      switch(this.state) {
      case ON_GROUND:
         return entityData.isOnGround();
      case AIRBORNE:
         return !entityData.isOnGround();
      case SPRINTING:
         return entityData.getEntity().isSprinting();
      case STANDING_STILL:
         return entityData.isStillHorizontally();
      case MOVING_HORIZONTALLY:
         return !entityData.isStillHorizontally();
      default:
         return false;
      }
   }

   public static enum State {
      ON_GROUND,
      AIRBORNE,
      SPRINTING,
      STANDING_STILL,
      MOVING_HORIZONTALLY;

      private State() {
      }
   }

   public static class Template extends TriggerConditionTemplate {
      public StateCondition.State state;

      public Template() {
      }
   }
}
