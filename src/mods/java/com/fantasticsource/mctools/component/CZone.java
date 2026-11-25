package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.util.math.BlockPos;

public class CZone extends Component {
   protected BlockPos min;
   protected BlockPos max;

   public CZone() {
      this(BlockPos.ORIGIN, BlockPos.ORIGIN);
   }

   public CZone(BlockPos pos1, BlockPos pos2) {
      this.setBounds(pos1, pos2);
   }

   public CZone setBounds(BlockPos pos1, BlockPos pos2) {
      this.min = new BlockPos(Tools.min(pos1.getX(), pos2.getX()), Tools.min(pos1.getY(), pos2.getY()), Tools.min(pos1.getZ(), pos2.getZ()));
      this.max = new BlockPos(Tools.max(pos1.getX(), pos2.getX()), Tools.max(pos1.getY(), pos2.getY()), Tools.max(pos1.getZ(), pos2.getZ()));
      return this;
   }

   public BlockPos getMin() {
      return new BlockPos(this.min);
   }

   public BlockPos getMax() {
      return new BlockPos(this.max);
   }

   public CZone write(ByteBuf buf) {
      buf.writeInt(this.min.getX());
      buf.writeInt(this.min.getY());
      buf.writeInt(this.min.getZ());
      buf.writeInt(this.max.getX());
      buf.writeInt(this.max.getY());
      buf.writeInt(this.max.getZ());
      return this;
   }

   public CZone read(ByteBuf buf) {
      this.min = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      this.max = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      return this;
   }

   public CZone save(OutputStream stream) {
      (new CInt()).set(this.min.getX()).save(stream).set(this.min.getY()).save(stream).set(this.min.getZ()).save(stream).set(this.max.getX()).save(stream).set(this.max.getY()).save(stream).set(this.max.getZ()).save(stream);
      return this;
   }

   public CZone load(InputStream stream) {
      CInt ci = new CInt();
      this.min = new BlockPos(ci.load(stream).value, ci.load(stream).value, ci.load(stream).value);
      this.max = new BlockPos(ci.load(stream).value, ci.load(stream).value, ci.load(stream).value);
      return this;
   }
}
