package me.ichun.mods.ichunutil.common.packet.mod;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.ArrayList;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketNewGrabbedEntityId extends AbstractPacket {
   public boolean grabbed;
   public int oldId;
   public int newId;

   public PacketNewGrabbedEntityId() {
   }

   public PacketNewGrabbedEntityId(boolean grabbedd, int oldd, int neww) {
      this.grabbed = grabbedd;
      this.oldId = oldd;
      this.newId = neww;
   }

   public void writeTo(ByteBuf buffer) {
      buffer.writeBoolean(this.grabbed);
      buffer.writeInt(this.oldId);
      buffer.writeInt(this.newId);
   }

   public void readFrom(ByteBuf buffer) {
      this.grabbed = buffer.readBoolean();
      this.oldId = buffer.readInt();
      this.newId = buffer.readInt();
   }

   public void execute(Side side, EntityPlayer player) {
      for(GrabHandler handler : (ArrayList)GrabHandler.grabbedEntities.get(Side.CLIENT)) {
         if (this.grabbed) {
            if (handler.grabbedId == this.oldId) {
               handler.grabbedId = this.newId;
            }
         } else if (handler.grabberId == this.oldId) {
            handler.grabberId = this.newId;
         }
      }

   }

   public Side receivingSide() {
      return Side.CLIENT;
   }
}
