package com.teamacronymcoders.base.materialsystem.blocks;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.partdata.DataPartParsers;
import com.teamacronymcoders.base.materialsystem.partdata.MaterialPartData;
import com.teamacronymcoders.base.materialsystem.parts.Part;
import com.teamacronymcoders.base.subblocksystem.blocks.SubBlockBase;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SubBlockPart extends SubBlockBase {
   private MaterialPart materialPart;
   private CreativeTabs creativeTabs;
   private float hardness = 5.0F;
   private int resistance = 30;
   private int harvestLevel = 1;
   private String harvestTool = "pickaxe";
   private boolean transparent = false;

   public SubBlockPart(MaterialPart materialPart, CreativeTabs creativeTab) {
      super(materialPart.getUnlocalizedName());
      this.materialPart = materialPart;
      MaterialPartData data = this.materialPart.getData();
      this.hardness = ((Float)data.getValue("hardness", Float.valueOf(this.hardness), DataPartParsers::getFloat)).floatValue();
      this.resistance = ((Integer)data.getValue("resistance", Integer.valueOf(this.resistance), DataPartParsers::getInt)).intValue();
      this.harvestLevel = ((Integer)data.getValue("harvestLevel", Integer.valueOf(this.harvestLevel), DataPartParsers::getInt)).intValue();
      this.harvestTool = (String)data.getValue("harvestTool", this.harvestTool, DataPartParsers::getString);
      this.transparent = ((Boolean)data.getValue("transparent", Boolean.valueOf(this.transparent), DataPartParsers::getBool)).booleanValue();
      this.creativeTabs = creativeTab;
   }

   public void setHardness(float hardness) {
      this.hardness = hardness;
   }

   public void setResistance(int resistance) {
      this.resistance = resistance;
   }

   public void setHarvestLevel(int harvestLevel) {
      this.harvestLevel = harvestLevel;
   }

   public void setHarvestTool(String harvestTool) {
      this.harvestTool = harvestTool;
   }

   public String getLocalizedName() {
      return this.materialPart.getLocalizedName();
   }

   public String getUnLocalizedName() {
      return this.materialPart.getUnlocalizedName();
   }

   public int getColor() {
      return this.materialPart.getColor();
   }

   public float getHardness() {
      return this.hardness;
   }

   public int getResistance() {
      return this.resistance;
   }

   public int getHarvestLevel() {
      return this.harvestLevel;
   }

   public String getHarvestTool() {
      return this.harvestTool;
   }

   public String getOreDict() {
      return this.materialPart.getOreDictString();
   }

   @Nullable
   public CreativeTabs getCreativeTab() {
      return this.creativeTabs;
   }

   public ResourceLocation getTextureLocation() {
      ResourceLocation location = this.materialPart.getTextureLocation();
      return new ResourceLocation(location.getResourceDomain(), this.getModelPrefix() + this.getUnLocalizedName());
   }

   public IGeneratedModel getGeneratedModel() {
      TemplateFile templateFile = TemplateManager.getTemplateFile("colored_block");
      Map<String, String> replacements = Maps.newHashMap();
      Part part = this.materialPart.getPart();
      replacements.put("texture", String.format("%s:blocks/%s", part.getOwnerId(), part.getShortUnlocalizedName()));
      templateFile.replaceContents(replacements);
      return new GeneratedModel(this.getModelPrefix() + this.getUnLocalizedName(), ModelType.BLOCKSTATE, templateFile.getFileContents());
   }

   protected String getModelPrefix() {
      return "materials/";
   }

   public MaterialPart getMaterialPart() {
      return this.materialPart;
   }

   public boolean isSideSolid(EnumFacing side) {
      return this.transparent;
   }

   public boolean isTopSolid() {
      return this.transparent;
   }

   public BlockFaceShape getBlockFaceShape() {
      return BlockFaceShape.SOLID;
   }

   public AxisAlignedBB getBoundingBox() {
      return Block.FULL_BLOCK_AABB;
   }

   public boolean isFullCube() {
      return !this.transparent;
   }

   public boolean isOpaqueCube() {
      return !this.transparent;
   }

   public boolean isPassable() {
      return false;
   }

   public boolean isFullBlock() {
      return !this.transparent;
   }

   public int getLightOpacity() {
      return this.transparent ? 255 : 0;
   }

   public boolean canSilkHarvest() {
      return true;
   }

   public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos) {
      return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
   }

   public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player) {
      return false;
   }
}
