package com.teamacronymcoders.base.client.models.sided;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.teamacronymcoders.base.blocks.properties.SideType;
import com.teamacronymcoders.base.client.models.ModelUtils;
import java.util.Collection;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public class ModelSidedBlock implements IModel {
   private final String name;
   private final String type;
   private final ImmutableMap<String, ResourceLocation> textures;

   public ModelSidedBlock(String name, String type, ImmutableMap<String, ResourceLocation> textures) {
      this.name = name;
      this.type = type;
      this.textures = textures;
   }

   @Nonnull
   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of();
   }

   @Nonnull
   public Collection<ResourceLocation> getTextures() {
      return this.textures.values();
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
      TextureAtlasSprite[][] tex = new TextureAtlasSprite[6][3];

      for(EnumFacing f : EnumFacing.VALUES) {
         for(SideType cfg : SideType.values()) {
            ResourceLocation resourceLocation = (ResourceLocation)this.textures.get(f.getName() + "_" + cfg.getName());
            if (resourceLocation != null) {
               tex[f.ordinal()][cfg.ordinal()] = ModelUtils.getBlockSprite(resourceLocation);
            }
         }
      }

      return new BakedModelSidedBlock(this.name, tex);
   }

   @Nonnull
   public IModel retexture(ImmutableMap<String, String> textures) {
      String newName = this.name;
      Builder<String, ResourceLocation> builder = ImmutableMap.builder();

      for(EnumFacing f : EnumFacing.VALUES) {
         for(SideType cfg : SideType.values()) {
            String key = f.getName() + "_" + cfg.getName();
            ResourceLocation resourceLocation = (ResourceLocation)this.textures.get(key);
            if (textures.containsKey(key)) {
               resourceLocation = new ResourceLocation((String)textures.get(key));
            } else if (textures.containsKey(f.getName())) {
               ITextureNamer namer = SidedTypeRegistry.getTextureNamer(this.type);
               resourceLocation = new ResourceLocation((String)textures.get(f.getName()));
               if (namer != null) {
                  String c = namer.nameFromCfg(f, cfg);
                  if (c != null) {
                     resourceLocation = new ResourceLocation((String)textures.get(f.getName()) + "_" + c);
                  }
               }
            } else if (textures.containsKey("name")) {
               ITextureNamer namer = SidedTypeRegistry.getTextureNamer(this.type);
               newName = (String)textures.get("name");
               if (namer != null) {
                  resourceLocation = new ResourceLocation(newName + "_" + namer.getTextureName(f, cfg));
               }
            }

            builder.put(key, resourceLocation);
         }
      }

      return new ModelSidedBlock(newName, this.type, builder.build());
   }
}
