package com.teamacronymcoders.base.materialsystem.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.items.IHasItemColor;
import com.teamacronymcoders.base.items.IHasOreDict;
import com.teamacronymcoders.base.items.ItemBase;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class ItemSingularMaterialPart extends ItemBase implements IHasItemColor, IHasOreDict, IHasGeneratedModel {
   private final MaterialPart materialPart;
   private final int burnTime;
   private final int stackSize;

   public ItemSingularMaterialPart(MaterialPart materialPart) {
      super(materialPart.getUnlocalizedName());
      this.materialPart = materialPart;
      this.burnTime = ((Integer)materialPart.getData().getValue("burn", Integer.valueOf(0), Integer::parseInt)).intValue();
      this.stackSize = ((Integer)materialPart.getData().getValue("stacksize", Integer.valueOf(this.getItemStackLimit()), Integer::parseInt)).intValue();
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.materialPart.getUnlocalizedName());
      return modelNames;
   }

   public int getColorFromItemstack(@Nonnull ItemStack itemStack, int tintIndex) {
      MaterialPart materialPart = this.getMaterialPartFromItemStack(itemStack);
      if (materialPart.getPart().hasOverlayTexture()) {
         return tintIndex == 1 ? this.getMaterialPartFromItemStack(itemStack).getColor() : -1;
      } else {
         return tintIndex == 0 ? this.getMaterialPartFromItemStack(itemStack).getColor() : -1;
      }
   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack itemStack) {
      return this.getMaterialPartFromItemStack(itemStack).getLocalizedName();
   }

   public boolean hasEffect(@Nonnull ItemStack itemStack) {
      return this.getMaterialPartFromItemStack(itemStack).hasEffect();
   }

   @Nonnull
   private MaterialPart getMaterialPartFromItemStack(ItemStack itemStack) {
      return this.materialPart;
   }

   @Nonnull
   public Map<ItemStack, String> getOreDictNames(@Nonnull Map<ItemStack, String> names) {
      for(String oreName : this.materialPart.getAllOreDictStrings()) {
         names.put(new ItemStack(this), oreName);
      }

      return names;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();
      Map<String, String> replacements = Maps.newHashMap();
      TemplateFile templateFile;
      if (this.materialPart.getPart().hasOverlayTexture()) {
         templateFile = TemplateManager.getTemplateFile("item_model_overlaid");
         replacements.put("texture", this.materialPart.getPart().getOwnerId() + ":items/" + this.materialPart.getPart().getShortUnlocalizedName());
         replacements.put("texture_overlay", this.materialPart.getPart().getOwnerId() + ":items/" + this.materialPart.getPart().getShortUnlocalizedName() + "_overlay");
      } else {
         templateFile = TemplateManager.getTemplateFile("item_model");
         replacements.put("texture", this.materialPart.getPart().getOwnerId() + ":items/" + this.materialPart.getPart().getShortUnlocalizedName());
      }

      templateFile.replaceContents(replacements);
      models.add(new GeneratedModel(this.materialPart.getUnlocalizedName(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
      return models;
   }

   public int getItemBurnTime(ItemStack itemStack) {
      return this.burnTime;
   }

   public int getItemStackLimit(ItemStack stack) {
      return this.stackSize;
   }
}
