package goblinbob.mobends.standard.data;

import goblinbob.mobends.core.client.event.DataUpdateHandler;
import java.util.Random;
import net.minecraft.entity.monster.EntityZombie;

public abstract class ZombieDataBase<E extends EntityZombie> extends BipedEntityData<E> {
   public static final int ANIMATION_SETS_AMOUNT = 2;
   protected int animationSet = 0;
   protected int currentWalkingState = 0;
   protected float ticksBeforeStateChange = 0.0F;
   private final Random random = new Random();

   public ZombieDataBase(E entity) {
      super(entity);
      this.animationSet = (int)((float)entity.getEntityId() * 3.61352F) % 2;
   }

   public int getAnimationSet() {
      return this.animationSet;
   }

   public int getCurrentWalkingState() {
      return this.currentWalkingState;
   }

   public void update(float partialTicks) {
      super.update(partialTicks);
      this.ticksBeforeStateChange -= DataUpdateHandler.ticksPerFrame;
      if (this.ticksBeforeStateChange <= 0.0F) {
         this.currentWalkingState = this.random.nextInt(2);
         this.ticksBeforeStateChange = (float)(80 + this.random.nextInt(20));
      }

   }
}
