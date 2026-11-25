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

public class CPathFollowEntity extends CPath {
   public Entity entity;

   public CPathFollowEntity() {
   }

   public CPathFollowEntity(Entity entity) {
      this.entity = entity;
   }

   public VectorN getRelativePositionInternal(long time) {
      return this.entity == null ? null : new VectorN(new double[]{this.entity.posX, this.entity.posY, this.entity.posZ});
   }

   public CPathFollowEntity write(ByteBuf buf) {
      super.write(buf);
      buf.writeInt(this.entity.getEntityId());
      return this;
   }

   public CPathFollowEntity read(ByteBuf buf) {
      super.read(buf);
      this.entity = MCTools.getValidEntityByID(buf.readInt());
      return this;
   }

   public CPathFollowEntity save(OutputStream stream) {
      super.save(stream);
      (new CInt()).set(this.entity.getEntityId()).save(stream);
      return this;
   }

   public CPathFollowEntity load(InputStream stream) {
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
