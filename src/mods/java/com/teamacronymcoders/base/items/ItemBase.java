package com.teamacronymcoders.base.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;

public class ItemBase extends ItemBaseNoModel implements IHasModel, IHasGeneratedModel {
   public ItemBase(String name) {
      this("", name);
   }

   public ItemBase(String texturePath, String name) {
      super(texturePath, name);
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.texturePath + this.name);
      return modelNames;
   }

   public List<IGeneratedModel> getGeneratedModels() {
      List<IGeneratedModel> models = Lists.newArrayList();
      this.getResourceLocations(Lists.newArrayList()).forEach((resourceLocation) -> {
         TemplateFile templateFile = TemplateManager.getTemplateFile("item_model");
         Map<String, String> replacements = Maps.newHashMap();
         replacements.put("texture", (new ResourceLocation(resourceLocation.getResourceDomain(), "items/" + resourceLocation.getResourcePath())).toString());
         templateFile.replaceContents(replacements);
         models.add(new GeneratedModel(resourceLocation.getResourcePath(), ModelType.ITEM_MODEL, templateFile.getFileContents()));
      });
      return models;
   }
}
