package com.teamacronymcoders.base.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.Coloring;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public class ItemCustomRecord extends ItemRecord implements IHasGeneratedModel, IHasModel, IHasItemColor {
   private IBaseMod mod;
   private final Coloring color;
   private final String recordName;

   public ItemCustomRecord(String recordName, SoundEvent soundEvent, Coloring color) {
      super(recordName, soundEvent);
      this.setUnlocalizedName(recordName);
      this.color = color;
      this.recordName = recordName;
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
      return this.color != null && tintIndex == 1 ? this.color.getIntColor() : -1;
   }

   public Item getItem() {
      return this;
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.recordName);
      return modelNames;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();
      Map<String, String> replacements = Maps.newHashMap();
      TemplateFile templateFile = TemplateManager.getTemplateFile("item_model_overlaid");
      replacements.put("texture", "base:items/record_outside");
      replacements.put("texture_overlay", "base:items/record_color");
      templateFile.replaceContents(replacements);
      models.add(new GeneratedModel(this.recordName, ModelType.ITEM_MODEL, templateFile.getFileContents()));
      return models;
   }
}
