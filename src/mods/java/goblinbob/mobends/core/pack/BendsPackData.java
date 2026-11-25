package goblinbob.mobends.core.pack;

import goblinbob.mobends.core.animation.keyframe.KeyframeAnimation;
import goblinbob.mobends.core.kumo.state.IKumoInstancingContext;
import goblinbob.mobends.core.kumo.state.template.AnimatorTemplate;
import java.util.Map;

public class BendsPackData implements IKumoInstancingContext {
   public Map<String, AnimatorTemplate> targets;
   public Map<String, KeyframeAnimation> keyframeAnimations;

   public BendsPackData() {
   }

   public KeyframeAnimation getAnimation(String key) {
      return (KeyframeAnimation)this.keyframeAnimations.get(key);
   }
}
