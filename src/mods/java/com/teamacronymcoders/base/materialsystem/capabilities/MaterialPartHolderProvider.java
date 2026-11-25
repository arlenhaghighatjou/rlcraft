package com.teamacronymcoders.base.materialsystem.capabilities;

import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class MaterialPartHolderProvider implements ICapabilityProvider {
   private MaterialPartHolder holder;

   public MaterialPartHolderProvider(MaterialPart materialPart) {
      this.holder = new MaterialPartHolder(materialPart);
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == MaterialPartCapability.MATERIAL_PART_HOLDER;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(this.hasCapability(capability, facing) ? MaterialPartCapability.MATERIAL_PART_HOLDER.cast(this.holder) : null);
   }
}
