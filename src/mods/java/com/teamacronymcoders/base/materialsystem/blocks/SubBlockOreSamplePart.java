package com.teamacronymcoders.base.materialsystem.blocks;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.GeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.IGeneratedModel;
import com.teamacronymcoders.base.client.models.generator.generatedmodel.ModelType;
import com.teamacronymcoders.base.event.PlaceWaypointEvent;
import com.teamacronymcoders.base.items.IDropTable;
import com.teamacronymcoders.base.items.ItemStackDropTable;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.MaterialUser;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.partdata.DataPartParsers;
import com.teamacronymcoders.base.materialsystem.partdata.MaterialPartData;
import com.teamacronymcoders.base.materialsystem.parts.Part;
import com.teamacronymcoders.base.util.DropUtils;
import com.teamacronymcoders.base.util.files.templates.TemplateFile;
import com.teamacronymcoders.base.util.files.templates.TemplateManager;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class SubBlockOreSamplePart extends SubBlockPart {
   private String itemDrop;
   private boolean requireTool;
   private String activatedText;
   private IDropTable dropTable = null;

   public SubBlockOreSamplePart(MaterialPart materialPart, MaterialUser materialUser) {
      super(materialPart, MaterialSystem.materialCreativeTab);
      MaterialPartData data = materialPart.getData();
      this.setHardness(((Float)data.getValue("hardness", Float.valueOf(0.125F), DataPartParsers::getFloat)).floatValue());
      this.setResistance(((Integer)data.getValue("resistance", Integer.valueOf(2), DataPartParsers::getInt)).intValue());
      this.setHarvestLevel(((Integer)data.getValue("harvestLevel", Integer.valueOf(-1), DataPartParsers::getInt)).intValue());
      this.setHarvestTool((String)data.getValue("harvestTool", (Object)null, DataPartParsers::getString));
      this.itemDrop = (String)data.getValue("drops", this.itemDrop, DataPartParsers::getString);
      this.requireTool = ((Boolean)data.getValue("requireTool", Boolean.valueOf(this.requireTool), DataPartParsers::getBool)).booleanValue();
      this.activatedText = (String)data.getValue("activatedText", this.activatedText, DataPartParsers::getString);
   }

   public Material getMaterial() {
      return this.requireTool ? Material.ROCK : Material.GROUND;
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

   public IGeneratedModel getGeneratedModel() {
      TemplateFile templateFile = TemplateManager.getTemplateFile("ore_sample_block");
      Map<String, String> replacements = Maps.newHashMap();
      Part part = this.getMaterialPart().getPart();
      replacements.put("texture", String.format("%s:blocks/%s", "minecraft", "gravel"));
      replacements.put("particle", String.format("%s:blocks/%s", "minecraft", "stone"));
      replacements.put("ore", String.format("%s:blocks/%s", part.getOwnerId(), part.getShortUnlocalizedName()));
      templateFile.replaceContents(replacements);
      return new GeneratedModel(this.getModelPrefix() + this.getUnLocalizedName(), ModelType.BLOCKSTATE, templateFile.getFileContents());
   }

   public boolean isSideSolid(EnumFacing side) {
      return false;
   }

   public boolean isTopSolid() {
      return false;
   }

   public BlockFaceShape getBlockFaceShape() {
      return BlockFaceShape.UNDEFINED;
   }

   public AxisAlignedBB getBoundingBox() {
      return new AxisAlignedBB(0.20000000298023224D, 0.0D, 0.20000000298023224D, 0.800000011920929D, 0.25D, 0.800000011920929D);
   }

   public boolean isFullCube() {
      return false;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isPassable() {
      return true;
   }

   public boolean isFullBlock() {
      return false;
   }

   public int getLightOpacity() {
      return 0;
   }

   public boolean canSilkHarvest() {
      return false;
   }

   public void onNeighborChange(World world, BlockPos pos, Block block, BlockPos fromPos) {
      if (!world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP)) {
         if (!this.requireTool) {
            this.getBlock().dropBlockAsItem(world, pos, this.getBlockState(), 0);
         }

         world.setBlockToAir(pos);
      }

   }

   public boolean canPlaceBlockAt(World world, @Nonnull BlockPos pos) {
      return world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP);
   }

   public boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player) {
      if (this.activatedText != null && !this.activatedText.isEmpty()) {
         player.sendStatusMessage(new TextComponentString(this.activatedText), true);
         world.setBlockToAir(pos);
      } else if (!this.requireTool) {
         this.getBlock().dropBlockAsItem(world, pos, this.getBlockState(), 0);
         world.setBlockToAir(pos);
      }

      if (player.isSneaking()) {
         MinecraftForge.EVENT_BUS.post(new PlaceWaypointEvent(this.getLocalizedName(), world.provider.getDimension(), pos, this.getColor()));
      }

      player.swingArm(EnumHand.MAIN_HAND);
      return true;
   }
}
