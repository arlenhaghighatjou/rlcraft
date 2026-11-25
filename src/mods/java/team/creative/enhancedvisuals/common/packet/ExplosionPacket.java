package team.creative.enhancedvisuals.common.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import team.creative.enhancedvisuals.common.handler.VisualHandlers;

public class ExplosionPacket extends CreativeCorePacket {
   public Vec3d pos;
   public float size;
   public int sourceEntity;

   public ExplosionPacket(Vec3d pos, float size, int sourceEntity) {
      this.pos = pos;
      this.size = size;
      this.sourceEntity = sourceEntity;
   }

   public ExplosionPacket() {
   }

   public void writeBytes(ByteBuf buf) {
      writeVec3d(this.pos, buf);
      buf.writeFloat(this.size);
      buf.writeInt(this.sourceEntity);
   }

   public void readBytes(ByteBuf buf) {
      this.pos = readVec3d(buf);
      this.size = buf.readFloat();
      this.sourceEntity = buf.readInt();
   }

   public void executeClient(EntityPlayer player) {
      if (VisualHandlers.EXPLOSION.isEnabled(player)) {
         VisualHandlers.EXPLOSION.onExploded(player, this.pos, this.size, player.world.getEntityByID(this.sourceEntity));
      }

   }

   public void executeServer(EntityPlayer player) {
   }
}
