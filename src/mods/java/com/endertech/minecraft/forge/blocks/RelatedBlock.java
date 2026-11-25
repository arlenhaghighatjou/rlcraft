package com.endertech.minecraft.forge.blocks;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.IRelatedUnit;
import com.endertech.minecraft.forge.units.UnitId;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RelatedBlock extends ForgeBlock implements IRelatedUnit {
   private final UnitId relatedBlockId;

   public RelatedBlock(ForgeMod mod, UnitConfig config, String name, UnitId relatedBlockId, CreativeTabs tab) {
      super(mod, config, name, getDefaultMaterialOr(relatedBlockId.getBlockState().getBlock(), Material.ROCK), tab);
      this.relatedBlockId = ForgeConfig.getUnitId(config, this.getUnitCategory(), "relatedBlockId", relatedBlockId, "Id is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n");
   }

   public UnitId getRelatedId() {
      return this.relatedBlockId;
   }

   public String getClassRegName() {
      return "";
   }

   public Material getMaterial(IBlockState state) {
      Block block = this.getRelatedBlock();
      return block != null ? block.getDefaultState().getMaterial() : super.getMaterial(state);
   }

   public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
      Block relatedBlock = this.getRelatedBlock();
      if (relatedBlock != null) {
         int meta = this.getRelatedId().getMeta();

         try {
            IBlockState relatedState = relatedBlock.getStateFromMeta(meta);
            return relatedBlock.getMapColor(relatedState, world, pos);
         } catch (Exception var7) {
            return relatedBlock.getMapColor(relatedBlock.getDefaultState(), world, pos);
         }
      } else {
         return super.getMapColor(state, world, pos);
      }
   }

   @Nullable
   protected Block getRelatedBlock() {
      Block block = this.getRelatedId().getBlockState().getBlock();
      return block;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      Block relatedBlock = this.getRelatedBlock();
      return relatedBlock != null ? relatedBlock.getBlockLayer() : super.getBlockLayer();
   }

   public SoundType getSoundType() {
      Block relatedBlock = this.getRelatedBlock();
      return relatedBlock != null ? relatedBlock.getSoundType() : super.getSoundType();
   }

   public boolean isReadyToRegister() {
      String modId = this.getRelatedId().getModId();
      boolean isLoaded = Loader.isModLoaded(modId);
      return isLoaded;
   }

   public void onInit() {
      Block relatedBlock = this.getRelatedBlock();
      if (relatedBlock != null) {
         this.lightValue = relatedBlock.getLightValue(relatedBlock.getDefaultState());
         this.blockResistance = relatedBlock.getExplosionResistance((Entity)null) * 5.0F;
         this.blockHardness = relatedBlock.getBlockHardness(relatedBlock.getDefaultState(), (World)null, (BlockPos)null);
      }

      if (!this.hasVariants()) {
         String name = UnitId.buildRegName(this.getId().getRegName(), IForgeUnit.getClassRegName(this.getClass()));
         this.setUnlocalizedName(name);
      }

      super.onInit();
   }
}
