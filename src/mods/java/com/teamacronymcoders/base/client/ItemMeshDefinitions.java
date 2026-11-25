package com.teamacronymcoders.base.client;

import com.teamacronymcoders.base.items.IHasItemMeshDefinition;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemMeshDefinitions {
   public ItemMeshDefinitions() {
   }

   public static void registerItemMeshDefinitions(final IHasItemMeshDefinition itemMeshDefinition) {
      ModelLoader.setCustomMeshDefinition(itemMeshDefinition.getItem(), new ItemMeshDefinition() {
         @Nonnull
         public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return new ModelResourceLocation(itemMeshDefinition.getResourceLocation(stack), itemMeshDefinition.getVariant(stack));
         }
      });
      ModelBakery.registerItemVariants(itemMeshDefinition.getItem(), (ResourceLocation[])itemMeshDefinition.getAllVariants().toArray(new ResourceLocation[0]));
   }
}
