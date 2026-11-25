package com.teamacronymcoders.base.materialsystem.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.items.IHasItemColor;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemMaterialArmor extends ItemArmor implements IHasModel, IHasItemColor, IHasGeneratedModel {
   private MaterialPart materialPart;
   private IBaseMod mod;
   private NonNullList<ItemStack> repairList;

   public ItemMaterialArmor(MaterialPart materialPart, ArmorMaterial material, EntityEquipmentSlot equipmentSlot) {
      super(material, 0, equipmentSlot);
      this.materialPart = materialPart;
      this.setUnlocalizedName(materialPart.getMaterial().getUnlocalizedName() + "_" + equipmentSlot.getName().toLowerCase(Locale.US));
   }

   public boolean hasColor(@Nonnull ItemStack stack) {
      return this.materialPart.isColorized();
   }

   public int getColor(@Nonnull ItemStack stack) {
      return this.materialPart.getColor();
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack itemStack) {
      String materialName;
      if (I18n.canTranslate(this.materialPart.getMaterial().getTranslationKey())) {
         materialName = I18n.translateToLocal(this.materialPart.getMaterial().getTranslationKey());
      } else {
         materialName = this.materialPart.getMaterial().getName();
      }

      return I18n.translateToLocalFormatted(this.materialPart.getPart().getUnlocalizedName() + "." + this.armorType.getName(), new Object[]{materialName});
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public Item getItem() {
      return this;
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.materialPart.getMaterial().getUnlocalizedName() + "_" + this.armorType.getName().toLowerCase(Locale.US));
      return modelNames;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();
      TemplateFile templateFile = TemplateManager.getTemplateFile("item_model");
      Map<String, String> replacements = Maps.newHashMap();
      replacements.put("texture", "base:items/" + this.armorType.getName().toLowerCase(Locale.US));
      templateFile.replaceContents(replacements);
      models.add(new GeneratedModel(this.materialPart.getMaterial().getUnlocalizedName() + "_" + this.armorType.getName().toLowerCase(Locale.US), ModelType.ITEM_MODEL, templateFile.getFileContents()));
      return models;
   }

   public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
      return this.getColor(stack);
   }

   @SideOnly(Side.CLIENT)
   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return String.format("%s:textures/models/armor/material_layer_%d%s.png", "base", this.isLegSlot(slot) ? 2 : 1, type == null ? "" : String.format("_%s", type));
   }

   public boolean hasOverlay(@Nonnull ItemStack stack) {
      return true;
   }

   private boolean isLegSlot(EntityEquipmentSlot slot) {
      return slot == EntityEquipmentSlot.LEGS;
   }

   public boolean getIsRepairable(ItemStack toRepair, @Nullable ItemStack repair) {
      return repair != null && this.getRepairItems().stream().anyMatch((repairItem) -> {
         return ItemStack.areItemsEqual(repair, repairItem);
      });
   }

   public NonNullList<ItemStack> getRepairItems() {
      if (this.repairList == null) {
         this.repairList = OreDictionary.getOres(this.getIngotName());
      }

      return this.repairList;
   }

   public String getIngotName() {
      return this.materialPart.getOreDictString().replace("armor", "ingot");
   }
}
