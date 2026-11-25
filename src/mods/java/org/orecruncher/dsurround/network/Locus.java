package org.orecruncher.dsurround.network;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class Locus extends TargetPoint {
   public final int entityId;

   public Locus(@Nonnull Entity entity, double range) {
      this(entity, entity.posX, entity.posY, entity.posZ, range);
   }

   public Locus(@Nonnull Entity entity, @Nonnull Vec3d pos, double range) {
      this(entity, pos.x, pos.y, pos.z, range);
   }

   public Locus(@Nonnull Entity entity, double x, double y, double z, double range) {
      super(entity.world.provider.getDimension(), x, y, z, range);
      this.entityId = entity.getEntityId();
   }

   public Locus(@Nonnull ByteBuf buf) {
      super(buf.readInt(), (double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat(), (double)buf.readFloat());
      this.entityId = buf.readInt();
   }

   public Locus(@Nonnull Locus locus, int range) {
      super(locus.dimension, locus.x, locus.y, locus.z, (double)range);
      this.entityId = locus.entityId;
   }

   @Nonnull
   public Vec3d getCoords() {
      return new Vec3d(this.x, this.y, this.z);
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeInt(this.dimension);
      buf.writeFloat((float)this.x);
      buf.writeFloat((float)this.y);
      buf.writeFloat((float)this.z);
      buf.writeFloat((float)this.range);
      buf.writeInt(this.entityId);
   }
}
