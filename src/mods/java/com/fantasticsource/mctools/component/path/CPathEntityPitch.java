package com.fantasticsource.mctools.component.path;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class CPathEntityPitch extends CPath {
   public Entity entity;

   public CPathEntityPitch() {
   }

   public CPathEntityPitch(Entity entity) {
      this.entity = entity;
   }

   public VectorN getRelativePositionInternal(long time) {
      return this.entity == null ? null : new VectorN(new double[]{Tools.degtorad((double)this.entity.rotationPitch), 0.0D, 0.0D});
   }

   public CPathEntityPitch write(ByteBuf buf) {
      super.write(buf);
      buf.writeInt(this.entity.getEntityId());
      return this;
   }

   public CPathEntityPitch read(ByteBuf buf) {
      super.read(buf);
      this.entity = MCTools.getValidEntityByID(buf.readInt());
      return this;
   }

   public CPathEntityPitch save(OutputStream stream) {
      super.save(stream);
      (new CInt()).set(this.entity.getEntityId()).save(stream);
      return this;
   }

   public CPathEntityPitch load(InputStream stream) {
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
