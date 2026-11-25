package com.endertech.minecraft.forge.blocks;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeLocating;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.regs.INeedRender;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.UnitEnabled;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidBlock extends BlockFluidClassic implements IForgeUnit, INeedRender {
   private final UnitEnabled unitEnabled;
   private final ForgeConfig config;
   private final ForgeMod mod;
   private final UnitId id;
   private Map<Object, UnitVariant> variants = new HashMap();
   private final UnitVariant defaultVariant;

   public FluidBlock(ForgeMod mod, UnitConfig config, Fluid fluid, Material material) {
      super(fluid, material);
      this.mod = mod;
      this.id = this.createId(mod.getId(), "");
      this.config = config;
      this.unitEnabled = new UnitEnabled(config, this.getUnitCategory(), true);
      this.defaultVariant = UnitVariant.from(this.id, IForgeUnit.Variants.NORMAL);
      this.setRegistryName(this.getId().toResLoc());
      this.setUnlocalizedName(this.getId().getRegName());
      this.register();
   }

   public static Fluid createFluidFor(ForgeMod mod, Class<? extends BlockFluidBase> blockClass) {
      String fluidName = IForgeUnit.getClassRegName(blockClass);
      ResourceLocation still = ForgeLocating.fluidTexture(mod.getId(), fluidName, false);
      ResourceLocation flowing = ForgeLocating.fluidTexture(mod.getId(), fluidName, true);
      Fluid fluid = new Fluid(fluidName, still, flowing);
      FluidRegistry.addBucketForFluid(fluid);
      return fluid;
   }

   @SideOnly(Side.CLIENT)
   public void registerRender() {
      ModelLoader.setCustomStateMapper(this, (new Builder()).ignore(new IProperty[]{LEVEL}).build());
   }

   public boolean isReadyToRegister() {
      return true;
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public UnitId getId() {
      return this.id;
   }

   public ForgeMod getMod() {
      return this.mod;
   }

   public boolean isEnabled() {
      return this.unitEnabled.isEnabled();
   }

   public boolean isRegistered() {
      return ForgeBlock.isRegistered(this);
   }

   public static int getDensityFrom(ForgeConfig config, String category, int defaultValue) {
      return ForgeConfig.getInt(config, category, "density", defaultValue, IntBounds.INTEGER, "Defines the density of this liquid.");
   }

   public static int getViscosityFrom(ForgeConfig config, String category, int defaultValue) {
      return ForgeConfig.getInt(config, category, "viscosity", defaultValue, IntBounds.INTEGER, "Defines the viscosity of this liquid.");
   }

   public void onInit() {
      this.blockHardness = ForgeBlock.getHardnessFrom(this.getConfig(), this.getUnitCategory(), this.blockHardness);
      this.blockResistance = ForgeBlock.getResistanceFrom(this.getConfig(), this.getUnitCategory(), this.blockResistance);
      this.setDensity(getDensityFrom(this.getConfig(), this.getUnitCategory(), this.density));
      this.getFluid().setDensity(this.density);
      this.getFluid().setViscosity(getViscosityFrom(this.getConfig(), this.getUnitCategory(), this.getFluid().getViscosity()));
      this.saveConfig();
   }

   public void onInitClient() {
   }

   public Map<Object, UnitVariant> getUnitVariants() {
      return this.variants;
   }

   public UnitVariant getDefaultVariant() {
      return this.defaultVariant;
   }

   public <E extends Enum<E> & IForgeEnum> void setUnitVariants(Class<E> clazz) {
      this.variants = this.createUnitVariants(clazz);
   }
}
