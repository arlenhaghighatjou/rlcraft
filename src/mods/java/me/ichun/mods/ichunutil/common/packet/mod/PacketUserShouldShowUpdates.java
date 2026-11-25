package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.client.module.update.GuiUpdateNotifier;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUserShouldShowUpdates extends AbstractPacket {
   public boolean notifyUpdate;

   public PacketUserShouldShowUpdates() {
   }

   public PacketUserShouldShowUpdates(boolean update) {
      this.notifyUpdate = update;
   }

   public void writeTo(ByteBuf buf) {
      buf.writeBoolean(this.notifyUpdate);
   }

   public void readFrom(ByteBuf buf) {
      this.notifyUpdate = buf.readBoolean();
   }

   public void execute(Side side, EntityPlayer player) {
      for(UpdateChecker.ModVersionInfo info : UpdateChecker.getModsWithUpdates()) {
         if (this.notifyUpdate || info.isModClientOnly) {
            iChunUtil.LOGGER.info("[NEW UPDATE AVAILABLE] " + info.modName + " - " + info.modVersionNew);
         }
      }

      if (iChunUtil.config.versionNotificationFrequency == 1 || iChunUtil.config.versionNotificationFrequency == 2 && iChunUtil.config.versionSave != EventCalendar.day) {
         this.handleClient(this.notifyUpdate);
      }

   }

   @SideOnly(Side.CLIENT)
   public void handleClient(boolean notifyAll) {
      GuiUpdateNotifier.notifyAll = notifyAll;
      GuiUpdateNotifier.notifyUpdates();
      if (iChunUtil.config.versionNotificationFrequency == 2) {
         iChunUtil.config.versionSave = EventCalendar.day;
         iChunUtil.config.save();
      }

   }

   public Side receivingSide() {
      return Side.CLIENT;
   }
}
