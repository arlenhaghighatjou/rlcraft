package goblinbob.mobends.standard.animation.bit.player;

import goblinbob.mobends.standard.data.PlayerData;

public class WalkAnimationBit extends goblinbob.mobends.standard.animation.bit.biped.WalkAnimationBit<PlayerData> {
   public WalkAnimationBit() {
   }

   public void perform(PlayerData data) {
      super.perform(data);
      if (data.getTicksAfterAttack() < 10.0F) {
         data.head.rotation.setSmoothness(0.5F).orientX(((Float)data.headPitch.get()).floatValue()).rotateY(((Float)data.headYaw.get()).floatValue());
      }

   }
}
