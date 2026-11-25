package goblinbob.mobends.standard.animation.bit.wolf;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.standard.data.WolfData;

public class WolfIdleAnimationBit extends AnimationBit<WolfData> {
   private static final String[] ACTIONS = new String[]{"idle"};

   public WolfIdleAnimationBit() {
   }

   public String[] getActions(WolfData entityData) {
      return ACTIONS;
   }

   public void perform(WolfData data) {
      data.head.rotation.orientX(0.0F);
   }
}
