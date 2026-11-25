package com.teamacronymcoders.base.client.models.generator.generatedmodel;

public class GeneratedModel implements IGeneratedModel {
   private String name;
   private ModelType modelType;
   private String json;

   public GeneratedModel(String name, ModelType modelType, String json) {
      this.name = name;
      this.modelType = modelType;
      this.json = json;
   }

   public String getName() {
      return this.name;
   }

   public ModelType getModelType() {
      return this.modelType;
   }

   public String getJson() {
      return this.json;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setModelType(ModelType modelType) {
      this.modelType = modelType;
   }

   public void setJson(String json) {
      this.json = json;
   }
}
