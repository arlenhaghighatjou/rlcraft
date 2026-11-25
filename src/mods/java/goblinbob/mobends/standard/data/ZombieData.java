package goblinbob.mobends.standard.data;

import goblinbob.mobends.standard.animation.controller.ZombieController;
import net.minecraft.entity.monster.EntityZombie;

public class ZombieData extends ZombieDataBase<EntityZombie> {
   private final ZombieController controller = new ZombieController();

   public ZombieData(EntityZombie entity) {
      super(entity);
   }

   public ZombieController getController() {
      return this.controller;
   }

   public void onTicksRestart() {
   }
}
