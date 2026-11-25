package com.teamacronymcoders.base.client.models.generator;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.util.files.BaseFileUtils;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGenerator {
   private final File blockStatesFolder;
   private final File blockModelsFolder;
   private final File itemModelsFolder;
   private final boolean okayToRun;

   public ModelGenerator(IBaseMod mod) {
      File resourceFolder = mod.getResourceFolder();
      if (resourceFolder != null) {
         this.blockStatesFolder = new File(resourceFolder, "blockstates");
         BaseFileUtils.createFolder(this.blockStatesFolder);
         this.blockModelsFolder = new File(resourceFolder, "models/block");
         BaseFileUtils.createFolder(this.blockModelsFolder);
         this.itemModelsFolder = new File(resourceFolder, "models/item");
         BaseFileUtils.createFolder(this.itemModelsFolder);
         this.okayToRun = true;
      } else {
         this.blockStatesFolder = null;
         this.blockModelsFolder = null;
         this.itemModelsFolder = null;
         this.okayToRun = false;
      }

   }

   public void generate(IHasGeneratedModel model) {
      if (this.okayToRun) {
         List<IGeneratedModel> generatedModels = model.getGeneratedModels();
         generatedModels.forEach((resource) -> {
            File file = null;
            switch(resource.getModelType()) {
            case BLOCKSTATE:
               file = new File(this.blockStatesFolder, resource.getName() + ".json");
               break;
            case BLOCK_MODEL:
               file = new File(this.blockModelsFolder, resource.getName() + ".json");
               break;
            case ITEM_MODEL:
               file = new File(this.itemModelsFolder, resource.getName() + ".json");
            }

            if (!file.exists()) {
               BaseFileUtils.writeStringToFile(resource.getJson(), file);
            }

         });
      }

   }
}
