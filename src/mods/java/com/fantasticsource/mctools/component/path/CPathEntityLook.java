package com.fantasticsource.mctools.component.path;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class CPathEntityLook extends CPath {
   public Entity entity;

   public CPathEntityLook() {
   }

   public CPathEntityLook(Entity entity) {
      this.entity = entity;
   }

   public VectorN getRelativePositionInternal(long time) {
      return this.entity == null ? null : MCTools.getVectorN(Vec3d.fromPitchYaw(this.entity.rotationPitch, this.entity.getRotationYawHead()));
   }

   public CPathEntityLook write(ByteBuf buf) {
      super.write(buf);
      buf.writeInt(this.entity.getEntityId());
      return this;
   }

   public CPathEntityLook read(ByteBuf buf) {
      super.read(buf);
      this.entity = MCTools.getValidEntityByID(buf.readInt());
      return this;
   }

   public CPathEntityLook save(OutputStream stream) {
      super.save(stream);
      (new CInt()).set(this.entity.getEntityId()).save(stream);
      return this;
   }

   public CPathEntityLook load(InputStream stream) {
      super.load(stream);
      this.entity = MCTools.getValidEntityByID((new CInt()).load(stream).value);
      return this;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = super.serializeNBT();
      compound.setInteger("entity", this.entity.getEntityId());
      return compound;
   }

   public void deserializeNBT(NBTBase nbt) {
      super.deserializeNBT(nbt);
      this.entity = MCTools.getValidEntityByID(((NBTTagCompound)nbt).getInteger("entity"));
   }
}
