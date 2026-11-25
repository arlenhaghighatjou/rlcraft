package org.jwaresoftware.mcmods.armorunder.runtime;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jwaresoftware.mcmods.lib.api.mod.IModRuntime.WrongSideException;

public final class ModRuntimeDS extends ModRuntimeSkeleton {
   public ModRuntimeDS() {
   }

   protected String getId() {
      return "DS";
   }

   @Nullable
   public EntityPlayer getClientPlayer() {
      throw new WrongSideException("Tried to get client playr on " + this.getId());
   }

   @Nullable
   public World getClientWorld() {
      throw new WrongSideException("Tried to get client world on " + this.getId());
   }

   public IThreadListener getThreadListener(MessageContext context) {
      if (context.side.isServer()) {
         return context.getServerHandler().player.mcServer;
      } else {
         throw new WrongSideException("Tried to get IThreadListener from a client-side MessageContext on " + this.getId());
      }
   }

   public EntityPlayer getPlayer(MessageContext context) {
      if (context.side.isServer()) {
         return context.getServerHandler().player;
      } else {
         throw new WrongSideException("Tried to get player from a client-side MessageContext on " + this.getId());
      }
   }
}
