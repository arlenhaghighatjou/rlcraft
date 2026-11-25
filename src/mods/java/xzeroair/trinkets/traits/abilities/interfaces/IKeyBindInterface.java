package xzeroair.trinkets.traits.abilities.interfaces;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IKeyBindInterface extends IAbilityInterface {
   @SideOnly(Side.CLIENT)
   String getKey();

   @SideOnly(Side.CLIENT)
   default String getAuxKey() {
      return "";
   }

   default boolean onKeyPress(Entity entity, boolean Aux) {
      return true;
   }

   default boolean onKeyDown(Entity entity, boolean Aux) {
      return true;
   }

   default boolean onKeyRelease(Entity entity, boolean Aux) {
      return true;
   }
}
