package ichttt.mods.firstaid.api.debuff;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public interface IDebuff {
   void handleDamageTaken(float var1, float var2, EntityPlayerMP var3);

   void handleHealing(float var1, float var2, EntityPlayerMP var3);

   default boolean isEnabled() {
      return true;
   }

   default void update(EntityPlayer player) {
   }

   default void update(EntityPlayer player, float healthPerMax) {
      this.update(player);
   }
}
