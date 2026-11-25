package com.alcatrazescapee.alcatrazcore.item.tool;

import com.alcatrazescapee.alcatrazcore.client.IModelProvider;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSwordCore extends ItemSword implements IModelProvider {
   public ItemSwordCore(ToolMaterial material) {
      super(material);
   }

   @SideOnly(Side.CLIENT)
   public void registerModel() {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName().toString()));
   }
}
