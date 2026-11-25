package com.fantasticsource.tools.component.path;

import com.fantasticsource.mctools.component.NBTSerializableComponent;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.VectorN;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CPath extends NBTSerializableComponent {
   public ArrayList<CPath.CPathTransform> transforms = new ArrayList();

   public CPath() {
   }

   public CPath add(CPath path) {
      this.transforms.add(new CPath.CPathTransform(0, new CPath[]{path}));
      return this;
   }

   public CPath mult(CPath path) {
      this.transforms.add(new CPath.CPathTransform(1, new CPath[]{path}));
      return this;
   }

   public CPath rotate(CPath axisPath, CPath thetaPath) {
      this.transforms.add(new CPath.CPathTransform(2, new CPath[]{axisPath, thetaPath}));
      return this;
   }

   public CPath crossProduct(CPath other) {
      this.transforms.add(new CPath.CPathTransform(3, new CPath[]{other}));
      return this;
   }

   public CPath power(CPath other) {
      this.transforms.add(new CPath.CPathTransform(4, new CPath[]{other}));
      return this;
   }

   public CPath mod(CPath other) {
      this.transforms.add(new CPath.CPathTransform(5, new CPath[]{other}));
      return this;
   }

   public CPath posMod(CPath other) {
      this.transforms.add(new CPath.CPathTransform(6, new CPath[]{other}));
      return this;
   }

   public CPath lowLimit(CPath other) {
      this.transforms.add(new CPath.CPathTransform(7, new CPath[]{other}));
      return this;
   }

   public CPath highLimit(CPath other) {
      this.transforms.add(new CPath.CPathTransform(8, new CPath[]{other}));
      return this;
   }

   public CPath round() {
      this.transforms.add(new CPath.CPathTransform(9, new CPath[0]));
      return this;
   }

   public CPath floor() {
      this.transforms.add(new CPath.CPathTransform(10, new CPath[0]));
      return this;
   }

   public CPath ceil() {
      this.transforms.add(new CPath.CPathTransform(11, new CPath[0]));
      return this;
   }

   protected VectorN getRelativePositionInternal(long time) {
      return new VectorN(new double[]{0.0D, 0.0D, 0.0D});
   }

   public final VectorN getRelativePosition(long time) {
      VectorN result = this.getRelativePositionInternal(time);

      for(CPath.CPathTransform transform : this.transforms) {
         transform.applyTo(result, time);
      }

      return result;
   }

   public final VectorN getPosition(long time, VectorN origin) {
      VectorN relative = this.getRelativePosition(time);
      return relative == null ? null : origin.copy().add(relative);
   }

   public CPath write(ByteBuf buf) {
      buf.writeInt(this.transforms.size());

      for(CPath.CPathTransform transform : this.transforms) {
         transform.write(buf);
      }

      return this;
   }

   public CPath read(ByteBuf buf) {
      this.transforms.clear();

      for(int i = buf.readInt(); i > 0; --i) {
         this.transforms.add((new CPath.CPathTransform()).read(buf));
      }

      return this;
   }

   public CPath save(OutputStream stream) {
      (new CInt()).set(this.transforms.size()).save(stream);

      for(CPath.CPathTransform transform : this.transforms) {
         transform.save(stream);
      }

      return this;
   }

   public CPath load(InputStream stream) {
      this.transforms.clear();

      for(int i = (new CInt()).load(stream).value; i > 0; --i) {
         this.transforms.add((new CPath.CPathTransform()).load(stream));
      }

      return this;
   }

   public CPath copy() {
      return (CPath)super.copy();
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      if (this.transforms.size() > 0) {
         NBTTagList list = new NBTTagList();

         for(CPath.CPathTransform transform : this.transforms) {
            list.appendTag(transform.serializeNBT());
         }

         compound.setTag("transforms", list);
      }

      return compound;
   }

   public void deserializeNBT(NBTBase nbt) {
      NBTTagCompound compound = (NBTTagCompound)nbt;
      this.transforms.clear();
      if (compound.hasKey("transforms")) {
         NBTTagList list = (NBTTagList)compound.getTag("transforms");

         for(int i = 0; i < list.tagCount(); ++i) {
            CPath.CPathTransform transform = new CPath.CPathTransform();
            transform.deserializeNBT(list.getCompoundTagAt(i));
            this.transforms.add(transform);
         }
      }

   }

   public static class CPathData extends Component {
      public CPath path;
      public long startTime;
      public long pauseTime;
      public double rate;

      public CPathData() {
         this.path = null;
         this.startTime = 0L;
         this.pauseTime = -1L;
         this.rate = 1.0D;
      }

      public CPathData(CPath path) {
         this(path, System.currentTimeMillis());
      }

      public CPathData(CPath path, long startTime) {
         this.path = null;
         this.startTime = 0L;
         this.pauseTime = -1L;
         this.rate = 1.0D;
         this.path = path;
         this.startTime = startTime;
      }

      public VectorN getRelativePosition() {
         return this.getRelativePosition(System.currentTimeMillis());
      }

      public VectorN getRelativePosition(long time) {
         return this.pauseTime > -1L ? this.path.getRelativePosition((long)((double)(this.pauseTime - this.startTime) * this.rate)) : this.path.getRelativePosition((long)((double)(time - this.startTime) * this.rate));
      }

      public CPath.CPathData pause() {
         return this.pause(System.currentTimeMillis());
      }

      public CPath.CPathData pause(long time) {
         this.pauseTime = time;
         return this;
      }

      public CPath.CPathData unpause() {
         return this.unpause(System.currentTimeMillis());
      }

      public CPath.CPathData unpause(long time) {
         if (this.pauseTime > -1L) {
            this.startTime += time - this.pauseTime;
            this.pauseTime = -1L;
         }

         return this;
      }

      public CPath.CPathData write(ByteBuf buf) {
         writeMarkedOrNull(buf, this.path);
         buf.writeLong(this.startTime);
         buf.writeLong(this.pauseTime);
         buf.writeDouble(this.rate);
         return this;
      }

      public CPath.CPathData read(ByteBuf buf) {
         this.path = (CPath)readMarkedOrNull(buf);
         this.startTime = buf.readLong();
         this.pauseTime = buf.readLong();
         this.rate = buf.readDouble();
         return this;
      }

      public CPath.CPathData save(OutputStream stream) {
         saveMarkedOrNull(stream, this.path);
         (new CLong()).set(this.startTime).save(stream).set(this.pauseTime).save(stream);
         (new CDouble()).set(this.rate).save(stream);
         return this;
      }

      public CPath.CPathData load(InputStream stream) {
         CLong cl = new CLong();
         this.path = (CPath)loadMarkedOrNull(stream);
         this.startTime = cl.load(stream).value;
         this.pauseTime = cl.load(stream).value;
         this.rate = (new CDouble()).load(stream).value;
         return this;
      }
   }

   public static class CPathTransform extends NBTSerializableComponent {
      public static final int TYPE_ADD = 0;
      public static final int TYPE_MULT = 1;
      public static final int TYPE_ROTATE = 2;
      public static final int TYPE_CROSS_PRODUCT = 3;
      public static final int TYPE_POWER = 4;
      public static final int TYPE_MOD = 5;
      public static final int TYPE_POSMOD = 6;
      public static final int TYPE_LOW_LIMIT = 7;
      public static final int TYPE_HIGH_LIMIT = 8;
      public static final int TYPE_ROUND = 9;
      public static final int TYPE_FLOOR = 10;
      public static final int TYPE_CEIL = 11;
      public int type = 0;
      public CPath[] paths;

      public CPathTransform() {
      }

      public CPathTransform(int type, CPath... paths) {
         this.type = type;
         this.paths = paths;
      }

      public void applyTo(VectorN vectorN, long time) {
         switch(this.type) {
         case 0:
            vectorN.add(this.paths[0].getRelativePosition(time));
            break;
         case 1:
            vectorN.multiply(this.paths[0].getRelativePosition(time));
            break;
         case 2:
            vectorN.rotate(this.paths[0].getRelativePosition(time), this.paths[1].getRelativePosition(time).values[0]);
            break;
         case 3:
            vectorN.crossProduct(this.paths[0].getRelativePosition(time));
            break;
         case 4:
            vectorN.power(this.paths[0].getRelativePosition(time));
            break;
         case 5:
            vectorN.mod(this.paths[0].getRelativePosition(time));
            break;
         case 6:
            vectorN.posMod(this.paths[0].getRelativePosition(time));
            break;
         case 7:
            vectorN.lowLimit(this.paths[0].getRelativePosition(time));
            break;
         case 8:
            vectorN.highLimit(this.paths[0].getRelativePosition(time));
            break;
         case 9:
            vectorN.round();
            break;
         case 10:
            vectorN.floor();
            break;
         case 11:
            vectorN.ceil();
         }

      }

      public CPath.CPathTransform write(ByteBuf buf) {
         buf.writeInt(this.type);
         buf.writeInt(this.paths.length);

         for(CPath path : this.paths) {
            writeMarked(buf, path);
         }

         return this;
      }

      public CPath.CPathTransform read(ByteBuf buf) {
         CInt ci = new CInt();
         this.type = ci.read(buf).value;
         this.paths = new CPath[ci.read(buf).value];

         for(int i = 0; i < this.paths.length; ++i) {
            this.paths[i] = (CPath)readMarked(buf);
         }

         return this;
      }

      public CPath.CPathTransform save(OutputStream stream) {
         (new CInt()).set(this.type).save(stream).set(this.paths.length).save(stream);

         for(CPath path : this.paths) {
            saveMarked(stream, path);
         }

         return this;
      }

      public CPath.CPathTransform load(InputStream stream) {
         CInt ci = new CInt();
         this.type = ci.load(stream).value;
         this.paths = new CPath[ci.load(stream).value];

         for(int i = 0; i < this.paths.length; ++i) {
            this.paths[i] = (CPath)loadMarked(stream);
         }

         return this;
      }

      public NBTTagCompound serializeNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setInteger("type", this.type);
         if (this.paths.length > 0) {
            NBTTagList list = new NBTTagList();

            for(CPath path : this.paths) {
               list.appendTag(serializeMarked(path));
            }
         }

         return compound;
      }

      public void deserializeNBT(NBTBase nbt) {
         NBTTagCompound compound = (NBTTagCompound)nbt;
         this.type = compound.getInteger("type");
         if (compound.hasKey("paths")) {
            NBTTagList list = (NBTTagList)compound.getTag("paths");
            this.paths = new CPath[list.tagCount()];

            for(int i = 0; i < this.paths.length; ++i) {
               this.paths[i] = (CPath)deserializeMarked(list.getCompoundTagAt(i));
            }
         } else {
            this.paths = new CPath[0];
         }

      }
   }
}
