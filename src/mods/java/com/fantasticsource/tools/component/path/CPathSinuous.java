package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class CPathSinuous extends CPath {
   public CPath highPointOffsetPath;
   public double thetaPerSec;
   public double thetaOffset;

   public CPathSinuous() {
   }

   public CPathSinuous(CPath highPointOffsetPath, double thetaPerSec) {
      this(highPointOffsetPath, thetaPerSec, 0.0D);
   }

   public CPathSinuous(CPath highPointOffsetPath, double thetaPerSec, double thetaOffset) {
      this.highPointOffsetPath = highPointOffsetPath;
      this.thetaPerSec = thetaPerSec;
      this.thetaOffset = thetaOffset;
   }

   public VectorN getRelativePositionInternal(long time) {
      double normalizedScalar = TrigLookupTable.TRIG_TABLE_1024.sin(6.283185307179586D * (this.thetaOffset + this.thetaPerSec * (double)time / 1000.0D));
      return this.highPointOffsetPath.getRelativePosition(time).scale(normalizedScalar);
   }

   public CPathSinuous write(ByteBuf buf) {
      super.write(buf);
      writeMarked(buf, this.highPointOffsetPath);
      buf.writeDouble(this.thetaPerSec);
      buf.writeDouble(this.thetaOffset);
      return this;
   }

   public CPathSinuous read(ByteBuf buf) {
      super.read(buf);
      this.highPointOffsetPath = (CPath)readMarked(buf);
      this.thetaPerSec = buf.readDouble();
      this.thetaOffset = buf.readDouble();
      return this;
   }

   public CPathSinuous save(OutputStream stream) {
      super.save(stream);
      saveMarked(stream, this.highPointOffsetPath);
      (new CDouble()).set(this.thetaPerSec).save(stream).set(this.thetaOffset).save(stream);
      return this;
   }

   public CPathSinuous load(InputStream stream) {
      super.load(stream);
      CDouble cd = new CDouble();
      this.highPointOffsetPath = (CPath)loadMarked(stream);
      this.thetaPerSec = cd.load(stream).value;
      this.thetaOffset = cd.load(stream).value;
      return this;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = super.serializeNBT();
      compound.setTag("highPointOffsetPath", serializeMarked(this.highPointOffsetPath));
      compound.setDouble("thetaPerSec", this.thetaPerSec);
      compound.setDouble("thetaOffset", this.thetaOffset);
      return compound;
   }

   public void deserializeNBT(NBTBase nbt) {
      super.deserializeNBT(nbt);
      NBTTagCompound compound = (NBTTagCompound)nbt;
      this.highPointOffsetPath = (CPath)deserializeMarked(compound.getCompoundTag("highPointOffsetPath"));
   }
}
