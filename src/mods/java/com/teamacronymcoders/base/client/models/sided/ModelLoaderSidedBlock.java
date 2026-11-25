package com.teamacronymcoders.base.client.models.sided;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.teamacronymcoders.base.blocks.properties.SideType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelLoaderSidedBlock implements ICustomModelLoader {
   private static ModelLoaderSidedBlock instance;
   private static final String RESOURCE_LOCATION = "models/block/side_type/";
   private Map<String, List<BakedQuad>> modelCache = new HashMap();
   private List<String> domains = new ArrayList();

   public ModelLoaderSidedBlock() {
   }

   public static ModelLoaderSidedBlock getInstance() {
      if (instance == null) {
         instance = new ModelLoaderSidedBlock();
         ModelLoaderRegistry.registerLoader(instance);
      }

      return instance;
   }

   public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
      this.modelCache.clear();
   }

   public boolean accepts(@Nonnull ResourceLocation modelLocation) {
      return this.domains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().contains("models/block/side_type/");
   }

   @Nonnull
   public IModel loadModel(@Nonnull ResourceLocation modelLocation) {
      String resourcePath = modelLocation.getResourcePath();
      if (!resourcePath.startsWith("models/block/side_type/")) {
         return ModelLoaderRegistry.getMissingModel();
      } else {
         String sub = resourcePath.replaceFirst("models/block/side_type/", "");
         int firstSlash = sub.indexOf("/");
         String name = sub.substring(firstSlash + 1);
         String type = sub.substring(0, firstSlash);
         Builder<String, ResourceLocation> builder = ImmutableMap.builder();
         ITextureNamer textureNamer = SidedTypeRegistry.getTextureNamer(type);

         for(EnumFacing f : EnumFacing.VALUES) {
            for(SideType cfg : SideType.values()) {
               String key = f.getName() + "_" + cfg.getName();
               String tex = name + "_" + textureNamer.getTextureName(f, cfg);
               builder.put(key, new ResourceLocation(modelLocation.getResourceDomain(), "blocks/" + tex));
            }
         }

         return new ModelSidedBlock(name, type, builder.build());
      }
   }

   public void addDomain(String modid) {
      this.domains.add(modid);
   }

   public Map<String, List<BakedQuad>> getModelCache() {
      return this.modelCache;
   }
}
