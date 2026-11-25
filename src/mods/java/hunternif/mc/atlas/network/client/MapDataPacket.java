package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.network.AbstractMessage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MapDataPacket extends AbstractMessage.AbstractClientMessage<MapDataPacket> {
   private int atlasID;
   private NBTTagCompound data;

   public MapDataPacket() {
   }

   public MapDataPacket(int atlasID, NBTTagCompound data) {
      this.atlasID = atlasID;
      this.data = data;
   }

   public void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.data = ByteBufUtils.readTag(buffer);
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      ByteBufUtils.writeTag(buffer, this.data);
   }

   @SideOnly(Side.CLIENT)
   protected void process(EntityPlayer player, Side side) {
      if (this.data != null) {
         AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(this.atlasID, player.getEntityWorld());
         atlasData.readFromNBT(this.data);
         if (SettingsConfig.gameplay.doSaveBrowsingPos && Minecraft.getMinecraft().currentScreen instanceof GuiAtlas) {
            ((GuiAtlas)Minecraft.getMinecraft().currentScreen).loadSavedBrowsingPosition();
         }

      }
   }
}
