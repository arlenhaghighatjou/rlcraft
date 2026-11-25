package goblinbob.mobends.standard.data;

import goblinbob.mobends.standard.animation.controller.ZombieVillagerController;
import net.minecraft.entity.monster.EntityZombieVillager;

public class ZombieVillagerData extends ZombieDataBase<EntityZombieVillager> {
   private final ZombieVillagerController controller = new ZombieVillagerController();

   public ZombieVillagerData(EntityZombieVillager entity) {
      super(entity);
   }

   public ZombieVillagerController getController() {
      return this.controller;
   }

   public void onTicksRestart() {
   }
}
