package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.ZombieData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.monster.EntityZombie;

public class ZombieMutator extends ZombieMutatorBase<ZombieData, EntityZombie, ModelZombie> {
   public ZombieMutator(IEntityDataFactory<EntityZombie> dataFactory) {
      super(dataFactory);
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
