package com.teamacronymcoders.contenttweaker.modules.materials.parttypes;

import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import com.teamacronymcoders.contenttweaker.api.ICTObject;
import com.teamacronymcoders.contenttweaker.modules.materials.functions.IMaterialPartItemStackSupplier;
import com.teamacronymcoders.contenttweaker.modules.materials.functions.IRegisterMaterialPart;
import com.teamacronymcoders.contenttweaker.modules.materials.materialpartdata.IPartDataPiece;
import com.teamacronymcoders.contenttweaker.modules.materials.materialparts.CTMaterialPart;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class CTCreatedPartType extends PartType implements IPartType {
   private final IRegisterMaterialPart materialPartRegister;
   private final IMaterialPartItemStackSupplier materialPartItemStackSupplier;

   public CTCreatedPartType(String name, IRegisterMaterialPart materialPartRegister, IMaterialPartItemStackSupplier materialPartItemStackSupplier) {
      super(name);
      this.materialPartRegister = materialPartRegister;
      this.materialPartItemStackSupplier = materialPartItemStackSupplier;
   }

   public void setup(@Nonnull MaterialPart materialPart, @Nonnull MaterialUser materialUser) {
      this.materialPartRegister.register(new CTMaterialPart(materialPart));
   }

   public void setData(IPartDataPiece[] data) {
      super.setData((List)Arrays.stream(data).map(ICTObject::getInternal).collect(Collectors.toList()));
   }

   public ItemStack getItemStack(MaterialPart materialPart) {
      return CraftTweakerMC.getItemStack(this.materialPartItemStackSupplier.getItemStack(new CTMaterialPart(materialPart)));
   }

   public PartType getInternal() {
      return this;
   }
}
