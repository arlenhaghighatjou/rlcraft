package goblinbob.mobends.standard.data;

import goblinbob.mobends.core.client.model.ModelPartTransform;
import goblinbob.mobends.core.data.LivingEntityData;
import goblinbob.mobends.standard.animation.controller.SquidController;
import net.minecraft.entity.passive.EntitySquid;

public class SquidData extends LivingEntityData<EntitySquid> {
   public static final int TENTACLE_SECTIONS = 9;
   public static final int SECTION_HEIGHT = 2;
   public ModelPartTransform squidBody;
   public ModelPartTransform[][] squidTentacles;
   private final SquidController controller = new SquidController();

   public SquidData(EntitySquid entity) {
      super(entity);
   }

   public SquidController getController() {
      return this.controller;
   }

   public void onTicksRestart() {
   }

   public void initModelPose() {
      super.initModelPose();
      this.squidBody = new ModelPartTransform();
      this.squidBody.rotation.finish();
      this.squidBody.position.set(0.0F, 8.0F, 0.0F);
      this.nameToPartMap.put("body", this.squidBody);
      this.squidTentacles = new ModelPartTransform[8][9];

      for(int i = 0; i < this.squidTentacles.length; ++i) {
         double d0 = (double)i * 3.141592653589793D * 2.0D / (double)this.squidTentacles.length;
         float x = (float)Math.cos(d0) * 4.0F;
         float z = (float)Math.sin(d0) * 4.0F;
         this.squidTentacles[i][0] = new ModelPartTransform();
         this.squidTentacles[i][0].position.set(x, 16.0F, z);

         for(int j = 1; j < 9; ++j) {
            this.squidTentacles[i][j] = new ModelPartTransform();
            this.squidTentacles[i][j].rotation.finish();
            this.squidTentacles[i][j].position.set(0.0F, 2.0F, 0.0F);
            this.nameToPartMap.put("tentacle_" + i + "_" + j, this.squidTentacles[i][j]);
         }

         this.squidTentacles[i][1].position.set(0.0F, 2.0F, 2.0F);
      }

   }

   public void updateParts(float ticksPerFrame) {
      super.updateParts(ticksPerFrame);
      this.squidBody.update(ticksPerFrame);

      for(int i = 0; i < this.squidTentacles.length; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.squidTentacles[i][j].update(ticksPerFrame);
         }
      }

   }
}
