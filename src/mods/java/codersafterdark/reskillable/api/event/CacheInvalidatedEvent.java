package codersafterdark.reskillable.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CacheInvalidatedEvent extends Event {
   private EntityPlayer player;
   private boolean modified;

   public CacheInvalidatedEvent(EntityPlayer player) {
      this(player, false);
   }

   public CacheInvalidatedEvent(EntityPlayer player, boolean modified) {
      this.player = player;
      this.modified = modified;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean anyModified() {
      return this.modified;
   }
}
