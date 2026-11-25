package me.ichun.mods.ichunutil.common.packet.mod;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.HashSet;
import me.ichun.mods.ichunutil.client.module.patron.PatronEffectRenderer;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketPatrons extends AbstractPacket {
   public PatronInfo info;
   public HashSet<PatronInfo> patrons;

   public PacketPatrons() {
   }

   public PacketPatrons(PatronInfo info) {
      this.info = info;
   }

   public void writeTo(ByteBuf buf) {
      for(PatronInfo info : this.info != null ? new HashSet<PatronInfo>() {
         {
            this.add(PacketPatrons.this.info);
         }
      } : iChunUtil.eventHandlerServer.patrons) {
         ByteBufUtils.writeUTF8String(buf, info.id);
         buf.writeInt(info.effectType);
         buf.writeBoolean(info.showEffect);
      }

      ByteBufUtils.writeUTF8String(buf, "##endPacket");
   }

   public void readFrom(ByteBuf buf) {
      this.patrons = new HashSet();

      for(String s = ByteBufUtils.readUTF8String(buf); !s.equals("##endPacket"); s = ByteBufUtils.readUTF8String(buf)) {
         int type = buf.readInt();
         boolean show = buf.readBoolean();
         this.patrons.add(new PatronInfo(s, type, show));
      }

   }

   public void execute(Side side, EntityPlayer player) {
      for(PatronInfo info : this.patrons) {
         PatronEffectRenderer.patrons.remove(info);
         PatronEffectRenderer.patrons.add(info);
      }

   }

   public Side receivingSide() {
      return Side.CLIENT;
   }
}
