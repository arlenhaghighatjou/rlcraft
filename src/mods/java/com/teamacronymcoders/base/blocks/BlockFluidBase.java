package com.teamacronymcoders.base.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.IHasGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidBase extends BlockFluidClassic implements IHasBlockStateMapper, IHasGeneratedModel {
   private String name;

   public BlockFluidBase(String name, Fluid fluid, Material material) {
      super(fluid, material);
      this.name = name;
      this.setUnlocalizedName(name);
   }

   public String getVariant(IBlockState blockState) {
      return "normal";
   }

   public List<IGeneratedModel> getGeneratedModels() {
      TemplateFile templateFile = TemplateManager.getTemplateFile("fluid");
      Map<String, String> replacements = Maps.newHashMap();
      replacements.put("FLUID", this.fluidName);
      templateFile.replaceContents(replacements);
      return Lists.newArrayList(new IGeneratedModel[]{new GeneratedModel(this.getModelPrefix() + this.name, ModelType.BLOCKSTATE, templateFile.getFileContents())});
   }

   protected String getModelPrefix() {
      return "";
   }

   public Block getBlock() {
      return this;
   }
}
