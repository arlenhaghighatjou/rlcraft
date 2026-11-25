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
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemMaterialPart extends ItemBase implements IHasItemColor, IHasOreDict, IHasGeneratedModel {
   private Map<Integer, MaterialPart> itemMaterialParts;
   private final String modid;

   public ItemMaterialPart(String modid) {
      super("material_part");
      this.setHasSubtypes(true);
      this.modid = modid;
   }

   public ItemMaterialPart(String modid, Integer integer) {
      this("material_part_" + integer);
   }

   public List<String> getModelNames(List<String> modelNames) {
      return modelNames;
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      this.getItemMaterialParts().values().forEach((materialPart) -> {
         itemStacks.add(materialPart.getItemStack());
      });
      return itemStacks;
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
      MaterialPart materialPart = (MaterialPart)this.itemMaterialParts.get(Integer.valueOf(itemStack.getItemDamage()));
      return (MaterialPart)(materialPart != null ? materialPart : MaterialSystem.MISSING_MATERIAL_PART);
   }

   public Map<Integer, MaterialPart> getItemMaterialParts() {
      if (this.itemMaterialParts == null) {
         this.itemMaterialParts = new HashMap();
      }

      return this.itemMaterialParts;
   }

   @Nonnull
   public Map<ItemStack, String> getOreDictNames(@Nonnull Map<ItemStack, String> names) {
      for(Entry<Integer, MaterialPart> entry : this.getItemMaterialParts().entrySet()) {
         for(String oreName : ((MaterialPart)entry.getValue()).getAllOreDictStrings()) {
            names.put(new ItemStack(this, 1, ((Integer)entry.getKey()).intValue()), oreName);
         }
      }

      return names;
   }

   public void addMaterialPart(int id, MaterialPart materialPart) {
      this.getItemMaterialParts().put(Integer.valueOf(id), materialPart);
   }

   public List<ResourceLocation> getResourceLocations(List<ResourceLocation> resourceLocations) {
      this.getItemMaterialParts().forEach((id, materialPart) -> {
         resourceLocations.add(new ResourceLocation(this.modid, materialPart.getUnlocalizedName()));
      });
      return resourceLocations;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();

      for(MaterialPart materialPart : this.getItemMaterialParts().values()) {
         Map<String, String> replacements = Maps.newHashMap();
         TemplateFile templateFile;
         if (materialPart.getPart().hasOverlayTexture()) {
            templateFile = TemplateManager.getTemplateFile("item_model_overlaid");
            replacements.put("texture", materialPart.getPart().getOwnerId() + ":items/" + materialPart.getPart().getShortUnlocalizedName());
            replacements.put("texture_overlay", materialPart.getPart().getOwnerId() + ":items/" + materialPart.getPart().getShortUnlocalizedName() + "_overlay");
         } else {
            templateFile = TemplateManager.getTemplateFile("item_model");
            replacements.put("texture", materialPart.getPart().getOwnerId() + ":items/" + materialPart.getPart().getShortUnlocalizedName());
         }

         templateFile.replaceContents(replacements);
         models.add(new GeneratedModel(materialPart.getUnlocalizedName(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
      }

      return models;
   }

   public int getItemBurnTime(ItemStack stack) {
      return ((Integer)this.getMaterialPartFromItemStack(stack).getData().getValue("burn", Integer.valueOf(0), Integer::parseInt)).intValue();
   }

   public int getItemStackLimit(ItemStack stack) {
      return ((Integer)this.getMaterialPartFromItemStack(stack).getData().getValue("stacksize", Integer.valueOf(this.getItemStackLimit()), Integer::parseInt)).intValue();
   }
}
