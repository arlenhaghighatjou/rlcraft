package com.teamacronymcoders.base.materialsystem.capabilities;

import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.materials.Material;
import com.teamacronymcoders.base.materialsystem.parts.Part;
import net.minecraft.nbt.NBTTagCompound;

public class MaterialPartHolder implements IMaterialPartHolder {
   private MaterialPart materialPart;

   public MaterialPartHolder(MaterialPart materialPart) {
      this.materialPart = materialPart;
   }

   public MaterialPartHolder() {
   }

   public MaterialPart getMaterialPart() {
      return this.materialPart;
   }

   public Material getMaterial() {
      return this.materialPart.getMaterial();
   }

   public Part getPart() {
      return this.materialPart.getPart();
   }

   public MaterialUser getOwner() {
      return this.materialPart.getMaterialUser();
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("modid", this.getOwner().getId());
      compound.setInteger("materialPartId", this.getMaterialPart().getId());
      return compound;
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      MaterialUser user = MaterialSystem.getUser(nbt.getString("modid"));
      if (user != null) {
         this.materialPart = user.getMaterialPart(nbt.getInteger("materialPartId"));
      } else {
         this.materialPart = MaterialSystem.MISSING_MATERIAL_PART;
      }

   }
}
