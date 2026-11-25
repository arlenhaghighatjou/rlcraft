package me.ichun.mods.ichunutil.client.module.tabula.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeGroup;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;

public class ModelTabula extends ModelBase {
   public final ProjectInfo projectInfo;
   public ArrayList<CubeInfo> cubes;

   public ModelTabula(ProjectInfo info) {
      this.projectInfo = info;
      this.textureHeight = this.projectInfo.textureHeight;
      this.textureWidth = this.projectInfo.textureWidth;
      this.cubes = new ArrayList();

      for(int i = 0; i < this.projectInfo.cubeGroups.size(); ++i) {
         this.createGroupCubes((CubeGroup)this.projectInfo.cubeGroups.get(i));
      }

      for(int i = 0; i < this.projectInfo.cubes.size(); ++i) {
         ((CubeInfo)this.projectInfo.cubes.get(i)).createModel(this);
         this.cubes.add(this.projectInfo.cubes.get(i));
      }

   }

   public void render(Entity ent, float f, float f1, float f2, float f3, float f4, float f5) {
      this.render(f5, false, false, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(float f5, boolean useTexture, boolean useOpacity) {
      this.render(f5, useTexture, useOpacity, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void render(float f5, boolean useTexture, boolean useOpacity, float r, float g, float b, float alpha) {
      if (useTexture && this.projectInfo.bufferedTexture != null) {
         if (this.projectInfo.bufferedTextureId == -1) {
            this.projectInfo.bufferedTextureId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), this.projectInfo.bufferedTexture);
         }

         GlStateManager.bindTexture(this.projectInfo.bufferedTextureId);
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(1.0D / this.projectInfo.scale[0], 1.0D / this.projectInfo.scale[1], 1.0D / this.projectInfo.scale[2]);
      this.cubes.stream().filter((info) -> {
         return info.modelCube != null && !info.hidden;
      }).forEach((info) -> {
         GlStateManager.pushMatrix();
         if (useOpacity) {
            GlStateManager.color(r, g, b, alpha * (float)(info.opacity / 100.0D));
         }

         if (info.scale[0] != 1.0D || info.scale[1] != 1.0D || info.scale[2] != 1.0D) {
            GlStateManager.translate(info.modelCube.offsetX, info.modelCube.offsetY, info.modelCube.offsetZ);
            GlStateManager.translate(info.modelCube.rotationPointX * f5, info.modelCube.rotationPointY * f5, info.modelCube.rotationPointZ * f5);
            GlStateManager.scale(info.scale[0], info.scale[1], info.scale[2]);
            GlStateManager.translate(-info.modelCube.offsetX, -info.modelCube.offsetY, -info.modelCube.offsetZ);
            GlStateManager.translate(-info.modelCube.rotationPointX * f5, -info.modelCube.rotationPointY * f5, -info.modelCube.rotationPointZ * f5);
         }

         info.modelCube.render(f5);
         GlStateManager.popMatrix();
      });
      GlStateManager.popMatrix();
   }

   public void bindTexture(BufferedImage image) {
      this.projectInfo.bufferedTexture = image;
      this.projectInfo.bufferedTextureId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), this.projectInfo.bufferedTexture);
   }

   private void createGroupCubes(CubeGroup group) {
      for(int i = 0; i < group.cubeGroups.size(); ++i) {
         this.createGroupCubes((CubeGroup)group.cubeGroups.get(i));
      }

      for(int i = 0; i < group.cubes.size(); ++i) {
         ((CubeInfo)group.cubes.get(i)).createModel(this);
         this.cubes.add(group.cubes.get(i));
      }

   }
}
