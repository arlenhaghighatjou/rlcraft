package goblinbob.mobends.standard.previewer;

import goblinbob.mobends.standard.data.ZombieData;

public class ZombiePreviewer extends BipedPreviewer<ZombieData> {
   public ZombiePreviewer() {
   }

   public void prePreview(ZombieData data, String animationToPreview) {
      super.prePreview(data, animationToPreview);
   }

   public void postPreview(ZombieData data, String animationToPreview) {
   }
}
