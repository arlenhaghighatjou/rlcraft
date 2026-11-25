package com.alcatrazescapee.alcatrazcore.item.tool;

import com.alcatrazescapee.alcatrazcore.client.IModelProvider;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class ItemToolCore extends ItemTool implements IModelProvider {
   protected final int harvestLevel;
   protected Set<Material> effectiveMaterials = new HashSet();

   public ItemToolCore(ToolMaterial material) {
      super(material, Sets.newHashSet());
      this.harvestLevel = material.getHarvestLevel();
   }

   public ItemToolCore(ToolMaterial material, float damage, float speed) {
      super(damage, speed, material, Sets.newHashSet());
      this.harvestLevel = material.getHarvestLevel();
   }

   @SideOnly(Side.CLIENT)
   public void registerModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName().toString()));
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return this.effectiveMaterials.contains(state.getMaterial()) ? this.efficiency : super.getDestroySpeed(stack, state);
   }

   public boolean canHarvestBlock(IBlockState state) {
      return this.effectiveMaterials.contains(state.getMaterial()) || this.harvestLevel > state.getBlock().getHarvestLevel(state);
   }

   protected void addToolClass(ItemToolCore.ToolClass clazz) {
      this.setHarvestLevel(clazz.name().toLowerCase(), this.harvestLevel);
   }

   protected static enum ToolClass {
      PICKAXE,
      SHOVEL,
      AXE;

      private ToolClass() {
      }
   }
}
