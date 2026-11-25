package goblinbob.mobends.standard.mutators;

import goblinbob.mobends.core.data.IEntityDataFactory;
import goblinbob.mobends.standard.data.ZombieDataBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.EntityZombie;

public abstract class ZombieMutatorBase<D extends ZombieDataBase<E>, E extends EntityZombie, M extends ModelBiped> extends BipedMutator<D, E, M> {
   protected boolean halfTexture = false;

   public ZombieMutatorBase(IEntityDataFactory<E> dataCreationFunction) {
      super(dataCreationFunction);
   }

   public void fetchFields(RenderLivingBase<? extends E> renderer) {
      super.fetchFields(renderer);
      if (renderer.getMainModel() instanceof ModelZombie) {
         ModelZombie model = (ModelZombie)renderer.getMainModel();
         this.halfTexture = model.textureHeight == 32;
      }

   }
}
