package me.ichun.mods.ichunutil.common.packet.mod;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketPatronInfo extends AbstractPacket {
   public String playerId;
   public int patronRewardType;
   public boolean showPatronReward;

   public PacketPatronInfo() {
   }

   public PacketPatronInfo(String playerId, int patronRewardType, boolean showPatronReward) {
      this.playerId = playerId;
      this.patronRewardType = patronRewardType;
      this.showPatronReward = showPatronReward;
   }

   public void writeTo(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.playerId);
      buf.writeInt(this.patronRewardType);
      buf.writeBoolean(this.showPatronReward);
   }

   public void readFrom(ByteBuf buf) {
      this.playerId = ByteBufUtils.readUTF8String(buf);
      this.patronRewardType = buf.readInt();
      this.showPatronReward = buf.readBoolean();
   }

   public void execute(Side side, EntityPlayer player) {
      PatronInfo info = new PatronInfo(this.playerId, this.patronRewardType, this.showPatronReward);
      iChunUtil.eventHandlerServer.patrons.remove(info);
      iChunUtil.eventHandlerServer.patrons.add(info);
      iChunUtil.channel.sendToAll(new PacketPatrons(info));
   }

   public Side receivingSide() {
      return Side.SERVER;
   }
}
