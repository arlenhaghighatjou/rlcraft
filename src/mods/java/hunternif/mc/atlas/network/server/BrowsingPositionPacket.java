package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.util.Log;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class BrowsingPositionPacket extends AbstractMessage.AbstractServerMessage<BrowsingPositionPacket> {
   public static final double ZOOM_SCALE_FACTOR = 1024.0D;
   private int atlasID;
   private int dimension;
   private int x;
   private int y;
   private double zoom;

   public BrowsingPositionPacket() {
   }

   public BrowsingPositionPacket(int atlasID, int dimension, int x, int y, double zoom) {
      this.atlasID = atlasID;
      this.dimension = dimension;
      this.x = x;
      this.y = y;
      this.zoom = zoom;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      this.x = buffer.readVarInt();
      this.y = buffer.readVarInt();
      this.zoom = (double)buffer.readVarInt() / 1024.0D;
   }

   protected void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      buffer.writeVarInt(this.x);
      buffer.writeVarInt(this.y);
      buffer.writeVarInt((int)Math.round(this.zoom * 1024.0D));
   }

   protected void process(EntityPlayer player, Side side) {
      if (SettingsConfig.gameplay.itemNeeded && !player.inventory.hasItemStack(new ItemStack(RegistrarAntiqueAtlas.ATLAS, 1, this.atlasID))) {
         Log.warn("Player %s attempted to put position marker into someone else's Atlas #%d", player.getGameProfile().getName(), this.atlasID);
      } else {
         AntiqueAtlasMod.atlasData.getAtlasData(this.atlasID, player.getEntityWorld()).getDimensionData(this.dimension).setBrowsingPosition(this.x, this.y, this.zoom);
      }
   }
}
