package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;

public final class OwnerDef {
   public String uuid;
   public String name;

   OwnerDef() {
   }

   OwnerDef(@Nonnull EntityPlayer player) {
      this.uuid = player.getCachedUniqueIdString();
      this.name = player.getName();
   }
}
