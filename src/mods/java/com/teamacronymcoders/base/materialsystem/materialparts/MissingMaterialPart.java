package com.teamacronymcoders.base.materialsystem.materialparts;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.materials.Material;
import com.teamacronymcoders.base.materialsystem.parts.PartBuilder;
import com.teamacronymcoders.base.materialsystem.parttype.NullPartType;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class MissingMaterialPart extends MaterialPart {
   public MissingMaterialPart() throws MaterialException {
      super((MaterialUser)null, (Material)null, (new PartBuilder()).setName("Missing").setPartType(new NullPartType()).build());
   }

   public String getUnlocalizedName() {
      return "Missing Part";
   }

   public String getLocalizedName() {
      return TextFormatting.RED + Base.languageHelper.error("null_part");
   }

   public boolean hasEffect() {
      return false;
   }

   public ResourceLocation getTextureLocation() {
      return Blocks.BARRIER.getRegistryName();
   }

   public ItemStack getItemStack() {
      return new ItemStack(Blocks.BARRIER, 1);
   }

   public int getColor() {
      return -1;
   }

   public boolean matchesPartType(PartType partType) {
      return false;
   }
}
