package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.DimensionData;
import hunternif.mc.atlas.core.TileGroup;
import hunternif.mc.atlas.network.AbstractMessage;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class TileGroupsPacket extends AbstractMessage.AbstractClientMessage<TileGroupsPacket> {
   public int atlasID;
   public int dimension;
   public ArrayList<TileGroup> tileGroups;
   public static final int TILE_GROUPS_PER_PACKET = 100;

   public TileGroupsPacket() {
      this.tileGroups = new ArrayList();
   }

   public TileGroupsPacket(ArrayList<TileGroup> tileGroups, int atlasID, int dimension) {
      this.tileGroups = tileGroups;
      this.atlasID = atlasID;
      this.dimension = dimension;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      int length = buffer.readVarInt();
      this.tileGroups = new ArrayList(length);

      for(int i = 0; i < length; ++i) {
         TileGroup newbie = new TileGroup(0, 0);
         newbie.readFromNBT(ByteBufUtils.readTag(buffer));
         this.tileGroups.add(newbie);
      }

   }

   protected void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      buffer.writeVarInt(this.tileGroups.size());

      for(TileGroup t : this.tileGroups) {
         NBTTagCompound me = new NBTTagCompound();
         t.writeToNBT(me);
         ByteBufUtils.writeTag(buffer, me);
      }

   }

   protected void process(EntityPlayer player, Side side) {
      AtlasData atlasData = AntiqueAtlasMod.atlasData.getAtlasData(this.atlasID, player.world);
      DimensionData dimData = atlasData.getDimensionData(this.dimension);

      for(TileGroup t : this.tileGroups) {
         dimData.putTileGroup(t);
      }

   }
}
