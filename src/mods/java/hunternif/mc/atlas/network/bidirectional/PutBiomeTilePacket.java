package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.util.Log;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class PutBiomeTilePacket extends AbstractMessage<PutBiomeTilePacket> {
   private int atlasID;
   private int dimension;
   private int x;
   private int z;
   private int biomeID;

   public PutBiomeTilePacket() {
   }

   public PutBiomeTilePacket(int atlasID, int dimension, int x, int z, int biomeID) {
      this.atlasID = atlasID;
      this.dimension = dimension;
      this.x = x;
      this.z = z;
      this.biomeID = biomeID;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      this.x = buffer.readVarInt();
      this.z = buffer.readVarInt();
      this.biomeID = buffer.readVarInt();
   }

   protected void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      buffer.writeVarInt(this.x);
      buffer.writeVarInt(this.z);
      buffer.writeVarInt(this.biomeID);
   }

   protected void process(EntityPlayer player, Side side) {
      if (side.isServer()) {
         if (SettingsConfig.gameplay.itemNeeded && !player.inventory.hasItemStack(new ItemStack(RegistrarAntiqueAtlas.ATLAS, 1, this.atlasID))) {
            Log.warn("Player %s attempted to modify someone else's Atlas #%d", player.getGameProfile().getName(), this.atlasID);
            return;
         }

         AtlasAPI.tiles.putBiomeTile(player.getEntityWorld(), this.atlasID, this.biomeID, this.x, this.z);
      } else {
         AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(this.atlasID, player.getEntityWorld());
         data.setTile(this.dimension, this.x, this.z, new Tile(this.biomeID));
      }

   }
}
