package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.ext.TileIdRegisteredEvent;
import hunternif.mc.atlas.network.AbstractMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileNameIDPacket extends AbstractMessage.AbstractClientMessage<TileNameIDPacket> {
   private final Map<String, Integer> nameToIdMap;

   public TileNameIDPacket() {
      this.nameToIdMap = new HashMap();
   }

   public TileNameIDPacket(Map<String, Integer> nameToIdMap) {
      this.nameToIdMap = nameToIdMap;
   }

   public TileNameIDPacket put(String name, int biomeID) {
      this.nameToIdMap.put(name, Integer.valueOf(biomeID));
      return this;
   }

   public void read(PacketBuffer buffer) throws IOException {
      int size = buffer.readVarInt();

      for(int i = 0; i < size; ++i) {
         String name = ByteBufUtils.readUTF8String(buffer);
         int biomeID = -buffer.readVarInt();
         this.nameToIdMap.put(name, Integer.valueOf(biomeID));
      }

   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.nameToIdMap.size());

      for(Entry<String, Integer> entry : this.nameToIdMap.entrySet()) {
         ByteBufUtils.writeUTF8String(buffer, (String)entry.getKey());
         buffer.writeVarInt(-((Integer)entry.getValue()).intValue());
      }

   }

   @SideOnly(Side.CLIENT)
   protected void process(EntityPlayer player, Side side) {
      for(Entry<String, Integer> entry : this.nameToIdMap.entrySet()) {
         String tileName = (String)entry.getKey();
         int id = ((Integer)entry.getValue()).intValue();
         int oldID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
         if (oldID != -1 && oldID != id) {
            BiomeTextureMap.instance().setTexture(Biome.getBiomeForId(oldID), (TextureSet)null);
         }

         ExtTileIdMap.instance().setPseudoBiomeID(tileName, id);
         TextureSet texture = ExtTileTextureMap.instance().getTexture(tileName);
         BiomeTextureMap.instance().setTexture(id, texture);
      }

      MinecraftForge.EVENT_BUS.post(new TileIdRegisteredEvent(this.nameToIdMap));
   }
}
