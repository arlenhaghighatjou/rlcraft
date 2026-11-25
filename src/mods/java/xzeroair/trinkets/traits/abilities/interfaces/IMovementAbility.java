package xzeroair.trinkets.traits.abilities.interfaces;

import net.minecraft.entity.Entity;

public interface IMovementAbility extends IAbilityInterface {
   default boolean left(Entity entity, int state) {
      return true;
   }

   default boolean right(Entity entity, int state) {
      return true;
   }

   default boolean forward(Entity entity, int state) {
      return true;
   }

   default boolean back(Entity entity, int state) {
      return true;
   }

   default boolean jump(Entity entity, int state) {
      return true;
   }

   default boolean sneak(Entity entity, int state) {
      return true;
   }
}
