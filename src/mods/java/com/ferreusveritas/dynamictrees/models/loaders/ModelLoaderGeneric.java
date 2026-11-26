package com.ferreusveritas.dynamictrees.models.loaders;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.IOUtils;

@SideOnly(Side.CLIENT)
public abstract class ModelLoaderGeneric implements ICustomModelLoader {
   public ResourceLocation resourceName;
   public String code;
   protected IResourceManager resourceManager;

   public ModelLoaderGeneric(String code, ResourceLocation resourceName) {
      this.code = "#" + code;
      this.resourceName = resourceName;
   }

   public boolean accepts(ResourceLocation resourceLocation) {
      return resourceLocation.getResourcePath().endsWith(this.code);
   }

   public IModel loadModel(ResourceLocation resourceLocation) {
      ModelBlock baseModelBlock = this.getBaseModelBlock(resourceLocation);
      return baseModelBlock != null ? this.loadModel(resourceLocation, baseModelBlock) : ModelLoaderRegistry.getMissingModel();
   }

   protected abstract IModel loadModel(ResourceLocation var1, ModelBlock var2);

   protected ModelBlock getBaseModelBlock(ResourceLocation virtualLocation) {
      if (!this.accepts(virtualLocation)) {
         return null;
      } else {
         String path = virtualLocation.getResourcePath();
         path = path.substring(0, path.length() - this.code.length());
         ResourceLocation location = new ResourceLocation(virtualLocation.getResourceDomain(), path);
         ModelBlock modelBlock = null;
         Reader reader = null;
         IResource iresource = null;

         try {
            iresource = this.resourceManager.getResource(this.getModelLocation(location));
            Reader var11 = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
            modelBlock = ModelBlock.deserialize(var11);
            modelBlock.name = location.toString();

            ModelBlock rootParent;
            for(rootParent = modelBlock; rootParent.parent != null; rootParent = rootParent.parent) {
               ;
            }

            return rootParent.getParentLocation() != null && rootParent.getParentLocation().equals(this.resourceName) ? modelBlock : null;
         } catch (IOException var8) {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(iresource);
            return null;
         }
      }
   }

   protected ResourceLocation getModelLocation(ResourceLocation location) {
      return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }
}
