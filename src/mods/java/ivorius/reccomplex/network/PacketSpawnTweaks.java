package ivorius.reccomplex.network;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.procedure.TObjectFloatProcedure;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSpawnTweaks implements IMessage {
   private TObjectFloatMap<String> data;

   public PacketSpawnTweaks() {
   }

   public PacketSpawnTweaks(TObjectFloatMap<String> data) {
      this.data = data;
   }

   public TObjectFloatMap<String> getData() {
      return this.data;
   }

   public void setData(TObjectFloatMap<String> data) {
      this.data = data;
   }

   public void fromBytes(ByteBuf buf) {
      int num = buf.readInt();
      this.data = new TObjectFloatHashMap();

      for(int i = 0; i < num; ++i) {
         String id = ByteBufUtils.readUTF8String(buf);
         this.data.put(id, buf.readFloat());
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.data.size());
      this.data.forEachEntry((id, tweak) -> {
         ByteBufUtils.writeUTF8String(buf, id);
         buf.writeFloat(tweak);
         return true;
      });
   }
}
