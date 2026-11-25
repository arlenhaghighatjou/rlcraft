package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.PigZombieData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;

public class PigZombieMutator extends BipedMutator<PigZombieData, EntityPigZombie, ModelZombie> {
   protected boolean halfTexture = false;

   public PigZombieMutator(IEntityDataFactory<EntityPigZombie> dataFactory) {
      super(dataFactory);
   }

   public void fetchFields(RenderLivingBase<? extends EntityPigZombie> renderer) {
      super.fetchFields(renderer);
      if (renderer.getMainModel() instanceof ModelZombie) {
         ModelZombie model = (ModelZombie)renderer.getMainModel();
         this.halfTexture = model.textureHeight == 32;
      }

   }

   public void storeVanillaModel(ModelZombie model) {
      ModelZombie vanillaModel = new ModelZombie(0.0F, this.halfTexture);
      this.vanillaModel = (M)vanillaModel;
      super.storeVanillaModel(model);
   }

   public boolean shouldModelBeSkipped(ModelBase model) {
      return !(model instanceof ModelZombie);
   }
}
