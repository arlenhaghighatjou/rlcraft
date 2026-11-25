package org.jwaresoftware.mcmods.lib.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface IWorldSource {
   IWorldSource NONE = new IWorldSource() {
      public World getSourcedWorld() {
         return null;
      }
   };

   World getSourcedWorld();

   public static final class FromPlayer implements IWorldSource {
      private final EntityPlayer _player;

      public FromPlayer(EntityPlayer player) {
         this._player = player;

         assert player != null;

      }

      public World getSourcedWorld() {
         return this._player.getEntityWorld();
      }
   }
}
