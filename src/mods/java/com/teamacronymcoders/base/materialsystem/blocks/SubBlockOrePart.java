package com.teamacronymcoders.base.materialsystem.blocks;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.items.IDropTable;
import com.teamacronymcoders.base.items.ItemStackDropTable;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.partdata.MaterialPartData;
import com.teamacronymcoders.base.util.DropUtils;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;

public class SubBlockOrePart extends SubBlockPart {
   private String itemDrop;
   private IBaseMod mod;
   private ResourceLocation variantLocation;
   private IDropTable dropTable = null;

   public SubBlockOrePart(MaterialPart materialPart, ResourceLocation variantLocation, MaterialUser materialUser) {
      super(materialPart, MaterialSystem.materialCreativeTab);
      MaterialPartData data = materialPart.getData();
      this.mod = materialUser.getMod();
      if (data.containsDataPiece("drops")) {
         this.itemDrop = data.getDataPiece("drops");
      }

      this.variantLocation = variantLocation;
   }

   public void getDrops(int fortune, List<ItemStack> itemStacks) {
      if (this.dropTable == null) {
         if (this.itemDrop != null && !this.itemDrop.isEmpty()) {
            this.dropTable = DropUtils.parseDrops(this.itemDrop);
         } else {
            this.dropTable = new ItemStackDropTable(this.getItemStack());
         }
      }

      itemStacks.addAll(this.dropTable.getDrops(fortune));
   }

   public ResourceLocation getTextureLocation() {
      return new ResourceLocation(this.mod.getID(), "materials/" + this.getUnLocalizedName());
   }

   public IGeneratedModel getGeneratedModel() {
      TemplateFile templateFile = TemplateManager.getTemplateFile("ore_block_state");
      Map<String, String> replacements = Maps.newHashMap();
      String unlocalizedName = this.getMaterialPart().getPart().getShortUnlocalizedName();
      String variantTexture = (new ResourceLocation(this.variantLocation.getResourceDomain(), "blocks/" + this.variantLocation.getResourcePath())).toString();
      replacements.put("texture", variantTexture);
      replacements.put("particle", variantTexture);
      String modid = this.getMaterialPart().getPart().getOwnerId();
      replacements.put("ore_shadow", modid + ":blocks/" + unlocalizedName + "_shadow");
      replacements.put("ore", modid + ":blocks/" + unlocalizedName);
      templateFile.replaceContents(replacements);
      return new GeneratedModel("materials/" + this.getMaterialPart().getUnlocalizedName(), ModelType.BLOCKSTATE, templateFile.getFileContents());
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }
}
