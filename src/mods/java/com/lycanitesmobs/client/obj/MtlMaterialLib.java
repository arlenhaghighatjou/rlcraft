package com.lycanitesmobs.client.obj;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import com.lycanitesmobs.LycanitesMobs;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class MtlMaterialLib {
   public static final String COMMENT = "#";
   public static final String NEW_MATERIAL = "newmtl";
   public static final String AMBIENT_COLOR = "Ka";
   public static final String DIFFUSE_COLOR = "Kd";
   public static final String SPECULAR_COLOR = "Ks";
   public static final String TRANSPARENCY_D = "d";
   public static final String TRANSPARENCY_TR = "Tr";
   public static final String ILLUMINATION = "illum";
   public static final String TEXTURE_AMBIENT = "map_Ka";
   public static final String TEXTURE_DIFFUSE = "map_Kd";
   public static final String TEXTURE_SPECULAR = "map_Ks";
   public static final String TEXTURE_TRANSPARENCY = "map_d";
   private ArrayList<Material> materials;
   private String path;
   private String startPath;

   public MtlMaterialLib(String path) {
      this.path = path;
      this.startPath = path.substring(0, path.lastIndexOf(47) + 1);
      this.materials = new ArrayList();
   }

   public void parse(String content) {
      String[] lines = content.split("\n");
      Material current = null;

      for(int i = 0; i < lines.length; ++i) {
         String line = lines[i].trim();
         String[] parts = line.split(" ");
         if (!parts[0].equals("#")) {
            if (parts[0].equals("newmtl")) {
               Material material = new Material(parts[1]);
               this.materials.add(material);
               current = material;
            } else if (parts[0].equals("Ka")) {
               current.ambientColor = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
            } else if (parts[0].equals("Kd")) {
               current.diffuseColor = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
            } else if (parts[0].equals("map_Kd")) {
               current.diffuseTexture = this.loadTexture(this.startPath + parts[1]);
            } else if (parts[0].equals("map_Ka")) {
               current.ambientTexture = this.loadTexture(this.startPath + parts[1]);
            } else if (parts[0].equals("d") || parts[0].equals("Tr")) {
               current.transparency = (float)Double.parseDouble(parts[1]);
            }
         }
      }

   }

   private int loadTexture(String string) {
      try {
         string = string.replace("models/", "textures/");
         ResourceLocation resourceLocation = new ResourceLocation(string);
         InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
         return loadTexture(TextureUtil.readBufferedImage(inputStream));
      } catch (IOException var4) {
         LycanitesMobs.logWarning("", "Unable to load a texture from the obj material file.");
         var4.printStackTrace();
         return 0;
      }
   }

   public static int loadTexture(ImageData img) {
      int id = GlStateManager.generateTexture();
      TextureUtil.uploadTextureImageAllocate(id, img, false, false);
      return id;
   }

   public List<Material> getMaterials() {
      return this.materials;
   }
}
