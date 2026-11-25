package svenhjol.charm.world.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import svenhjol.charm.world.feature.EndPortalRunes;
import svenhjol.meson.MesonMessage;

public class MessagePortalInteract extends MesonMessage {
   public BlockPos pos;
   public int linkType;

   public MessagePortalInteract(BlockPos pos, int linkType) {
      this.pos = pos;
      this.linkType = linkType;
   }

   public MessagePortalInteract() {
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
      buf.writeInt(this.linkType);
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
      this.linkType = buf.readInt();
   }

   public IMessage handle(MessageContext context) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         switch(this.linkType) {
         case 0:
            EndPortalRunes.effectPortalUnlinked(this.pos);
            break;
         case 1:
            EndPortalRunes.effectPortalLinked(this.pos);
            break;
         case 2:
            EndPortalRunes.effectPortalTravelled(this.pos);
         }

      });
      return null;
   }
}
