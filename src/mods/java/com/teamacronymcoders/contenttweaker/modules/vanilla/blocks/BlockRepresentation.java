package com.teamacronymcoders.contenttweaker.modules.vanilla.blocks;

import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.api.IRepresentation;
import com.teamacronymcoders.contenttweaker.api.ctobjects.aabb.MCAxisAlignedBB;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.BlockMaterialDefinition;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockmaterial.IBlockMaterialDefinition;
import com.teamacronymcoders.contenttweaker.api.ctobjects.color.CTColor;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.FaceShape;
import com.teamacronymcoders.contenttweaker.api.ctobjects.enums.PushReaction;
import com.teamacronymcoders.contenttweaker.api.ctobjects.resourcelocation.CTResourceLocation;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockAction;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockColorSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockDropHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IBlockFaceSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemColorSupplier;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ICreativeTab;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.creativetab.MCCreativeTab;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds.ISoundTypeDefinition;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds.SoundTypeDefinition;
import com.teamacronymcoders.contenttweaker.modules.vanilla.tileentity.TileEntityRepresentation;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.contenttweaker.Block")
public class BlockRepresentation implements IRepresentation<Block> {
   @ZenProperty
   public String unlocalizedName;
   @ZenProperty
   public ICreativeTab creativeTab = new MCCreativeTab(CreativeTabs.MISC);
   @ZenProperty
   public boolean fullBlock = true;
   @ZenProperty
   public boolean gravity = false;
   @ZenProperty
   public int lightOpacity = 255;
   @ZenProperty
   public boolean translucent = false;
   @ZenProperty
   public float lightValue = 0.0F;
   @ZenProperty
   public float blockHardness = 5.0F;
   @ZenProperty
   public float blockResistance = 5.0F;
   @ZenProperty
   public String toolClass = "pickaxe";
   @ZenProperty
   public int toolLevel = 2;
   @ZenProperty
   public ISoundTypeDefinition blockSoundType = new SoundTypeDefinition(SoundType.METAL);
   @ZenProperty
   public IBlockMaterialDefinition blockMaterial = new BlockMaterialDefinition(Material.IRON);
   @ZenProperty
   public float enchantPowerBonus = 0.0F;
   @ZenProperty
   public String enumBlockRenderType = EnumBlockRenderType.MODEL.toString();
   @ZenProperty
   public float slipperiness = 0.6F;
   @ZenProperty
   public IBlockAction onBlockPlace;
   @ZenProperty
   public IBlockAction onBlockBreak;
   @ZenProperty
   public String blockLayer = BlockRenderLayer.SOLID.toString();
   @ZenProperty
   public MCAxisAlignedBB axisAlignedBB = MCAxisAlignedBB.create(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
   @ZenProperty
   public IBlockAction onUpdateTick;
   @ZenProperty
   public IBlockAction onRandomTick;
   @ZenProperty
   public PushReaction mobilityFlag = PushReaction.normal();
   @ZenProperty
   public boolean passable;
   @ZenProperty
   public boolean replaceable;
   @ZenProperty
   public boolean entitySpawnable;
   @ZenProperty
   public boolean witherProof;
   @ZenProperty
   public IBlockDropHandler dropHandler;
   @ZenProperty
   public boolean beaconBase;
   @ZenProperty
   public TileEntityRepresentation tileEntityRepresentation;
   @ZenProperty
   public IBlockColorSupplier blockColorSupplier;
   @ZenProperty
   public IItemColorSupplier itemColorSupplier;
   @ZenProperty
   public IBlockFaceSupplier blockFaceSupplier;
   @ZenProperty
   public CTResourceLocation textureLocation;
   @ZenProperty
   public boolean canSilkHarvest;

   public BlockRepresentation() {
      this.passable = !this.blockMaterial.blocksMovement();
      this.replaceable = this.blockMaterial.isReplaceable();
      this.entitySpawnable = true;
      this.witherProof = false;
      this.beaconBase = false;
      this.tileEntityRepresentation = null;
      this.blockColorSupplier = (state, access, pos, tint) -> {
         return CTColor.fromInt(-1);
      };
      this.itemColorSupplier = (itemStack, tint) -> {
         return CTColor.fromInt(-1);
      };
      this.blockFaceSupplier = (access, state, pos, face) -> {
         return FaceShape.solid();
      };
      this.canSilkHarvest = true;
   }

   @ZenMethod
   public String getUnlocalizedName() {
      return this.unlocalizedName;
   }

   @ZenMethod
   public void setUnlocalizedName(String unlocalizedName) {
      this.unlocalizedName = unlocalizedName;
   }

   @ZenMethod
   public void setCreativeTab(ICreativeTab creativeTab) {
      this.creativeTab = creativeTab;
   }

   @ZenMethod
   public ICreativeTab getCreativeTab() {
      return this.creativeTab;
   }

   @ZenMethod
   public boolean isFullBlock() {
      return this.fullBlock;
   }

   @ZenMethod
   public void setFullBlock(boolean fullBlock) {
      this.fullBlock = fullBlock;
   }

   @ZenMethod
   public boolean hasGravity() {
      return this.gravity;
   }

   @ZenMethod
   public void setHasGravity(boolean gravity) {
      this.gravity = gravity;
   }

   @ZenMethod
   public int getLightOpacity() {
      return this.lightOpacity;
   }

   @ZenMethod
   public void setLightOpacity(int lightOpacity) {
      this.lightOpacity = lightOpacity;
   }

   @ZenMethod
   public boolean isTranslucent() {
      return this.translucent;
   }

   @ZenMethod
   public void setTranslucent(boolean translucent) {
      this.translucent = translucent;
   }

   @ZenMethod
   public float getLightValue() {
      return this.lightValue;
   }

   @ZenMethod
   public void setLightValue(float lightValue) {
      this.lightValue = lightValue;
   }

   @ZenMethod
   public float getBlockHardness() {
      return this.blockHardness;
   }

   @ZenMethod
   public void setBlockHardness(float blockHardness) {
      this.blockHardness = blockHardness;
   }

   @ZenMethod
   public float getBlockResistance() {
      return this.blockResistance;
   }

   @ZenMethod
   public void setBlockResistance(float blockResistance) {
      this.blockResistance = blockResistance;
   }

   @ZenMethod
   public String getToolClass() {
      return this.toolClass;
   }

   @ZenMethod
   public void setToolClass(String toolClass) {
      this.toolClass = toolClass;
   }

   @ZenMethod
   public int getToolLevel() {
      return this.toolLevel;
   }

   @ZenMethod
   public void setToolLevel(int toolLevel) {
      this.toolLevel = toolLevel;
   }

   @ZenMethod
   public ISoundTypeDefinition getBlockSoundType() {
      return this.blockSoundType;
   }

   @ZenMethod
   public void setBlockSoundType(ISoundTypeDefinition blockSoundType) {
      this.blockSoundType = blockSoundType;
   }

   @ZenMethod
   public IBlockMaterialDefinition getBlockMaterial() {
      return this.blockMaterial;
   }

   @ZenMethod
   public void setBlockMaterial(IBlockMaterialDefinition material) {
      this.blockMaterial = material;
   }

   @ZenMethod
   public void setEnchantPowerBonus(float enchantPowerBonus) {
      this.enchantPowerBonus = enchantPowerBonus;
   }

   @ZenMethod
   public float getEnchantPowerBonus() {
      return this.enchantPowerBonus;
   }

   @ZenMethod
   public void setEnumBlockRenderType(String blockRenderType) {
      this.enumBlockRenderType = blockRenderType;
   }

   @ZenMethod
   public String getEnumBlockRenderType() {
      return this.enumBlockRenderType;
   }

   @ZenMethod
   public void setSlipperiness(float slipperiness) {
      this.slipperiness = slipperiness;
   }

   @ZenMethod
   public float getSlipperiness() {
      return this.slipperiness;
   }

   @ZenMethod
   public void setOnBlockBreak(IBlockAction iBlockAction) {
      this.onBlockBreak = iBlockAction;
   }

   @ZenMethod
   public IBlockAction getOnBlockBreak() {
      return this.onBlockBreak;
   }

   @ZenMethod
   public void setOnBlockPlace(IBlockAction iBlockAction) {
      this.onBlockPlace = iBlockAction;
   }

   @ZenMethod
   public IBlockAction getOnBlockPlace() {
      return this.onBlockPlace;
   }

   @ZenMethod
   public void setBlockLayer(String blockLayer) {
      this.blockLayer = blockLayer;
   }

   @ZenMethod
   public String getBlockLayer() {
      return this.blockLayer;
   }

   @ZenMethod
   public MCAxisAlignedBB getAxisAlignedBB() {
      return this.axisAlignedBB;
   }

   @ZenMethod
   public void setAxisAlignedBB(MCAxisAlignedBB axisAlignedBB) {
      this.axisAlignedBB = axisAlignedBB;
   }

   @ZenMethod
   public IBlockAction getOnUpdateTick() {
      return this.onUpdateTick;
   }

   @ZenMethod
   public void setOnUpdateTick(IBlockAction onUpdateTick) {
      this.onUpdateTick = onUpdateTick;
   }

   @ZenMethod
   public IBlockAction getOnRandomTick() {
      return this.onRandomTick;
   }

   @ZenMethod
   public void setOnRandomTick(IBlockAction onRandomTick) {
      this.onRandomTick = onRandomTick;
   }

   @ZenMethod
   public PushReaction getMobilityFlag() {
      return this.mobilityFlag;
   }

   @ZenMethod
   public void setMobilityFlag(PushReaction mobilityFlag) {
      this.mobilityFlag = mobilityFlag;
   }

   @ZenMethod
   public boolean isPassable() {
      return this.passable;
   }

   @ZenMethod
   public void setPassable(boolean passable) {
      this.passable = passable;
   }

   @ZenMethod
   public void setReplaceable(boolean replaceable) {
      this.replaceable = replaceable;
   }

   @ZenMethod
   public boolean isReplaceable() {
      return this.replaceable;
   }

   @ZenMethod
   public void setEntitySpawnable(boolean entitySpawnable) {
      this.entitySpawnable = entitySpawnable;
   }

   @ZenMethod
   public boolean canEntitySpawn() {
      return this.entitySpawnable;
   }

   @ZenMethod
   public void setWitherProof(boolean witherProof) {
      this.witherProof = witherProof;
   }

   @ZenMethod
   public boolean isWitherProof() {
      return this.witherProof;
   }

   @ZenMethod
   public IBlockDropHandler getDropHandler() {
      return this.dropHandler;
   }

   @ZenMethod
   public void setDropHandler(IBlockDropHandler dropHandler) {
      this.dropHandler = dropHandler;
   }

   @ZenMethod
   public IBlockColorSupplier getBlockColorSupplier() {
      return this.blockColorSupplier;
   }

   @ZenMethod
   public void setBlockColorSupplier(IBlockColorSupplier blockColorSupplier) {
      this.blockColorSupplier = blockColorSupplier;
   }

   @ZenMethod
   public IItemColorSupplier getItemColorSupplier() {
      return this.itemColorSupplier;
   }

   @ZenMethod
   public void setItemColorSupplier(IItemColorSupplier itemColorSupplier) {
      this.itemColorSupplier = itemColorSupplier;
   }

   @ZenMethod
   public void setBlockFaceSupplier(IBlockFaceSupplier blockFaceSupplier) {
      this.blockFaceSupplier = blockFaceSupplier;
   }

   @ZenMethod
   public IBlockFaceSupplier getBlockFaceSupplier() {
      return this.blockFaceSupplier;
   }

   @ZenMethod
   public CTResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   @ZenMethod
   public void setTextureLocation(CTResourceLocation resourceLocation) {
      this.textureLocation = resourceLocation;
   }

   public Block getInternal() {
      return (Block)((BlockRegistry)ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK")).get(new ResourceLocation("contenttweaker", this.getUnlocalizedName()));
   }

   public String getName() {
      return this.getUnlocalizedName();
   }

   public String getTypeName() {
      return "BlockRepresentation";
   }

   @ZenMethod
   public void register() {
      ((BlockRegistry)ContentTweaker.instance.getRegistry(BlockRegistry.class, "BLOCK")).register(new BlockContent(this));
   }
}
