package crafttweaker.mc1120.entity;

import crafttweaker.api.entity.IEntityAgeable;
import net.minecraft.entity.EntityAgeable;

public class MCEntityAgeable extends MCEntityCreature implements IEntityAgeable {
   private final EntityAgeable entityAgeable;

   public MCEntityAgeable(EntityAgeable entity) {
      super(entity);
      this.entityAgeable = entity;
   }

   public int getGrowingAge() {
      return this.entityAgeable.getGrowingAge();
   }

   public void setGrowingAge(int age) {
      this.entityAgeable.setGrowingAge(age);
   }

   public void ageUp(int seconds, boolean forced) {
      this.entityAgeable.ageUp(seconds, forced);
   }

   public void addGrowth(int seconds) {
      this.entityAgeable.addGrowth(seconds);
   }

   public void setScaleForAge(boolean scaleForAge) {
      this.entityAgeable.setScaleForAge(scaleForAge);
   }
}
