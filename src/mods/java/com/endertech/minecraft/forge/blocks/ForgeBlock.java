package com.endertech.minecraft.forge.blocks;

import com.endertech.common.Args;
import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeLocating;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.items.ForgeBlockItem;
import com.endertech.minecraft.forge.materials.ForgeMaterial;
import com.endertech.minecraft.forge.tools.ForgeTool;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.UnitEnabled;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeBlock extends Block implements IForgeUnit {
   private final UnitEnabled unitEnabled;
   private final ForgeConfig config;
   private final ForgeMod mod;
   private final UnitId id;
   private Map<Object, UnitVariant> variants;
   private final UnitVariant defaultVariant;
   private ResourceLocation textureLocation;

   public ForgeBlock(ForgeMod mod, UnitConfig config, String name, Material material, CreativeTabs tab) {
      super(material);
      this.variants = new HashMap();
      this.mod = mod;
      this.id = this.createId(mod.getId(), name);
      this.config = config;
      this.unitEnabled = new UnitEnabled(config, this.getUnitCategory(), true);
      this.defaultVariant = UnitVariant.from(this.id, IForgeUnit.Variants.NORMAL);
      this.textureLocation = ForgeLocating.blockTexture(this.getMod().getId(), this.getId().getRegName());
      this.setRegistryName(this.getId().toResLoc());
      this.setUnlocalizedName(this.getId().getRegName());
      this.setCreativeTab(tab);
      this.register();
   }

   public ForgeBlock(ForgeMod mod, UnitConfig config, String name, Material material, float hardness, float resistance, CreativeTabs tab) {
      this(mod, config, name, material, tab);
      this.setHardness(hardness);
      this.setResistance(resistance);
   }

   public static boolean isUnderRain(World world, BlockPos pos) {
      return world.isRainingAt(pos.up());
   }

   public static boolean isUnderOpenSky(World world, BlockPos pos) {
      return world.canSeeSky(pos.up());
   }

   @Nullable
   public static Block getBlockOrNull(Item item) {
      return item instanceof ItemBlock ? ((ItemBlock)item).getBlock() : null;
   }

   public static Material getDefaultMaterialOr(Block block, Material material) {
      return block != null ? block.getDefaultState().getMaterial() : material;
   }

   public static String getStringId(Block block) {
      return UnitId.from(block, 0).toString();
   }

   public static <T extends Enum<T> & IForgeEnum> PropertyEnum<T> createPropertyEnum(Class<T> clazz) {
      return PropertyEnum.create(IForgeUnit.getClassRegName(clazz), clazz);
   }

   public static UnitConfig createDefaultConfig(ForgeMod mod, String blockName) {
      Path baseDir = mod.getConfig().getConfigDir().resolve("blocks");
      String configName = UnitId.dictToRegName(blockName);
      return new UnitConfig(baseDir, configName);
   }

   public float getHardness() {
      return this.blockHardness;
   }

   public float getResistance() {
      return this.blockResistance;
   }

   public ResourceLocation getTextureLocationFor(IBlockState state) {
      return this.textureLocation;
   }

   public ForgeBlock setResistance(float resistance) {
      this.blockResistance = resistance;
      return this;
   }

   public ForgeBlock setLightValue(int value) {
      this.lightValue = value;
      return this;
   }

   public String toString() {
      return ForgeBlock.class.getSimpleName() + Args.group(Args.get("status", IForgeUnit.getStatus(this)), Args.get("regName", this.getId().getRegName()), Args.get("enabled", Boolean.valueOf(this.isEnabled())), Args.get("hardness", Float.valueOf(this.getHardness())), Args.get("resistance", Float.valueOf(this.getResistance())), Args.get("harvestLevel", Integer.valueOf(this.getHarvestLevel(this.getDefaultState()))));
   }

   public boolean isEnabled() {
      return this.unitEnabled.isEnabled();
   }

   public static boolean isRegistered(Block block) {
      return ForgeRegistries.BLOCKS.containsValue(block);
   }

   public boolean isRegistered() {
      return isRegistered(this);
   }

   public static float getHardnessFrom(ForgeConfig config, String category, float defaultValue) {
      return ForgeConfig.getFloat(config, category, "hardness", defaultValue, new FloatBounds(-1.0F, Float.MAX_VALUE), "Defines how many hits it takes to break the block.\n -1 makes the block indestructible.");
   }

   public static float getResistanceFrom(ForgeConfig config, String category, float defaultValue) {
      return ForgeConfig.getFloat(config, category, "resistance", defaultValue, new FloatBounds(0.0F, Float.MAX_VALUE), "Defines the block resistance to explosions.");
   }

   public void onInit() {
      this.blockHardness = getHardnessFrom(this.getConfig(), this.getUnitCategory(), this.getHardness());
      this.blockResistance = getResistanceFrom(this.getConfig(), this.getUnitCategory(), this.getResistance());
      this.saveConfig();
   }

   public boolean isReadyToRegister() {
      return true;
   }

   public UnitId getId() {
      return this.id;
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public ForgeMod getMod() {
      return this.mod;
   }

   public boolean hasVariants() {
      return this.variants.size() > 1;
   }

   public Map<Object, UnitVariant> getUnitVariants() {
      return this.variants;
   }

   public ItemBlock createBlockItem() {
      ItemBlock blockItem = new ForgeBlockItem(this);
      return blockItem;
   }

   @SideOnly(Side.CLIENT)
   public void onInitClient() {
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void setHarvestLevel(Class<? extends ForgeTool> tool, ForgeMaterial material) {
      String toolClass = ForgeTool.getToolClass(tool);
      int harvestLevel = ForgeMaterial.calculateHarvestLevel(material);
      super.setHarvestLevel(toolClass, harvestLevel);
   }

   @Nullable
   public CreativeTabs getCreativeTabToDisplayOn() {
      return this.displayOnCreativeTab() ? super.getCreativeTabToDisplayOn() : null;
   }

   public UnitVariant getDefaultVariant() {
      return this.defaultVariant;
   }

   public <E extends Enum<E> & IForgeEnum> void setUnitVariants(Class<E> clazz) {
      this.variants = this.createUnitVariants(clazz);
   }
}
