package tschipp.carryon.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import tschipp.carryon.common.scripting.ScriptChecker;

public class CarrySlotPacketHandler implements IMessageHandler<CarrySlotPacket, IMessage> {
   public CarrySlotPacketHandler() {
   }

   public IMessage onMessage(final CarrySlotPacket message, MessageContext ctx) {
      IThreadListener mainThread = Minecraft.getMinecraft();
      mainThread.addScheduledTask(new Runnable() {
         World world;

         {
            this.world = Minecraft.getMinecraft().world;
         }

         public void run() {
            if (this.world != null) {
               Entity e = this.world.getEntityByID(message.entityid);
               if (e != null && e instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)e;
                  if (message.slot >= 9) {
                     player.getEntityData().removeTag("carrySlot");
                     player.getEntityData().removeTag("overrideKey");
                  } else {
                     player.getEntityData().setInteger("carrySlot", message.slot);
                     if (message.carryOverride != 0) {
                        ScriptChecker.setCarryOnOverride(player, message.carryOverride);
                     }
                  }
               }
            }

         }
      });
      return null;
   }
}
