package me.ichun.mods.ichunutil.common.module.tabula.project;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.ichunutil.client.module.tabula.model.ModelBaseDummy;
import me.ichun.mods.ichunutil.client.module.tabula.model.ModelInfo;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.Animation;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeGroup;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProjectInfo {
   public static final int IDENTIFIER_LENGTH = 20;
   public static final int PROJ_VERSION = 4;
   public transient String identifier;
   public transient File saveFile;
   public transient String saveFileMd5;
   public transient boolean saved;
   public transient boolean tampered;
   public transient int lastAutosave;
   public transient boolean autosaved;
   public transient float cameraFov;
   public transient float cameraZoom;
   public transient float cameraYaw;
   public transient float cameraPitch;
   public transient float cameraOffsetX;
   public transient float cameraOffsetY;
   public transient boolean ignoreNextImage;
   public transient File textureFile;
   public transient String textureFileMd5;
   public transient BufferedImage bufferedTexture;
   public transient int bufferedTextureId;
   public transient boolean destroyed;
   public transient ArrayList<String> states;
   public transient int lastState;
   public transient int switchState;
   @SideOnly(Side.CLIENT)
   public transient ModelBaseDummy model;
   public transient ProjectInfo ghostModel;
   public String modelName;
   public String authorName;
   public int projVersion;
   public ArrayList<String> metadata;
   public int textureWidth;
   public int textureHeight;
   public double[] scale;
   public ArrayList<CubeGroup> cubeGroups;
   public ArrayList<CubeInfo> cubes;
   public ArrayList<Animation> anims;
   public int cubeCount;

   public ProjectInfo() {
      this.cameraFov = 30.0F;
      this.cameraZoom = 1.0F;
      this.bufferedTextureId = -1;
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.scale = new double[]{1.0D, 1.0D, 1.0D};
      this.modelName = "";
      this.authorName = "";
      this.metadata = new ArrayList();
      this.cameraFov = 30.0F;
      this.cameraZoom = 1.0F;
      this.cubeGroups = new ArrayList();
      this.cubes = new ArrayList();
      this.anims = new ArrayList();
      this.states = new ArrayList();
      this.switchState = -1;
   }

   public ProjectInfo(String name, String author) {
      this();
      this.modelName = name;
      this.authorName = author;
      this.cubeGroups = new ArrayList();
      this.cubes = new ArrayList();
   }

   public String getAsJson() {
      Gson gson = new Gson();
      return gson.toJson(this);
   }

   @SideOnly(Side.CLIENT)
   public void initClient() {
      this.model = new ModelBaseDummy(this);
      this.cubeGroups.forEach(this::createGroupCubes);
      this.model.cubes.addAll((Collection)this.cubes.stream().map((cube) -> {
         return cube.createModel(this.model);
      }).collect(Collectors.toList()));
   }

   @SideOnly(Side.CLIENT)
   public void destroy() {
      if (this.model != null && !this.destroyed) {
         this.destroyed = true;

         for(int i = this.model.cubes.size() - 1; i >= 0; --i) {
            this.model.removeCubeInfo((CubeInfo)this.model.cubes.get(i));
         }

         this.model.rotationPoint.destroy();
         this.model.rotationControls.destroy();
         this.model.sizeControls.destroy();
         if (this.bufferedTextureId != -1) {
            TextureUtil.deleteTexture(this.bufferedTextureId);
         }
      }

      if (this.ghostModel != null) {
         this.ghostModel.destroy();
      }

   }

   public ArrayList<CubeInfo> getAllCubes() {
      ArrayList<CubeInfo> cubes = new ArrayList();
      this.addAllCubes(cubes, this.cubes);
      this.addAllCubesFromGroups(cubes, this.cubeGroups);
      return cubes;
   }

   public void addAllCubes(ArrayList<CubeInfo> list, ArrayList<CubeInfo> cubes) {
      list.addAll(cubes);

      for(CubeInfo cube : cubes) {
         this.addAllCubes(list, cube.getChildren());
      }

   }

   public void addAllCubesFromGroups(ArrayList<CubeInfo> list, ArrayList<CubeGroup> groups) {
      for(CubeGroup group : groups) {
         this.addAllCubes(list, group.cubes);
         this.addAllCubesFromGroups(list, group.cubeGroups);
      }

   }

   public void createNewGroup() {
      this.cubeGroups.add(new CubeGroup("Group"));
   }

   public void createNewCube() {
      ++this.cubeCount;
      this.cubes.add(new CubeInfo("shape" + Integer.toString(this.cubeCount)));
   }

   public void createNewAnimation(String name, boolean loop) {
      Animation anim = new Animation(name);
      anim.loops = loop;
      this.anims.add(anim);
   }

   private void createGroupCubes(CubeGroup group) {
      for(int i = 0; i < group.cubeGroups.size(); ++i) {
         this.createGroupCubes((CubeGroup)group.cubeGroups.get(i));
      }

      for(int i = 0; i < group.cubes.size(); ++i) {
         this.model.cubes.add(((CubeInfo)group.cubes.get(i)).createModel(this.model));
      }

   }

   public Object getObjectByIdent(String ident) {
      Object obj = null;

      for(CubeInfo inf : this.cubes) {
         if (obj == null) {
            obj = this.findObjectInCube(ident, inf);
         }
      }

      for(CubeGroup group1 : this.cubeGroups) {
         if (obj == null) {
            obj = this.findObjectInGroup(ident, group1);
         }
      }

      return obj;
   }

   public Object findObjectInCube(String ident, CubeInfo cube) {
      if (cube.identifier.equals(ident)) {
         return cube;
      } else {
         Object obj = null;

         for(CubeInfo inf : cube.getChildren()) {
            if (obj == null) {
               obj = this.findObjectInCube(ident, inf);
            }
         }

         return obj;
      }
   }

   public Object findObjectInGroup(String ident, CubeGroup group) {
      if (group.identifier.equals(ident)) {
         return group;
      } else {
         Object obj = null;

         for(CubeInfo inf : group.cubes) {
            if (obj == null) {
               obj = this.findObjectInCube(ident, inf);
            }
         }

         for(CubeGroup group1 : group.cubeGroups) {
            if (obj == null) {
               obj = this.findObjectInGroup(ident, group1);
            }
         }

         return obj;
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean importModel(ModelInfo model, boolean texture) {
      for(Entry<String, ModelRenderer> e : model.modelList.entrySet()) {
         ModelRenderer rend = (ModelRenderer)e.getValue();
         if (this.cubes.isEmpty()) {
            this.textureHeight = (int)rend.textureHeight;
            this.textureWidth = (int)rend.textureWidth;
         }

         CubeInfo firstCreated = null;
         if (rend.cubeList.isEmpty()) {
            CubeInfo info = new CubeInfo((String)e.getKey());
            info.dimensions[0] = info.dimensions[1] = info.dimensions[2] = 0;
            info.scale[0] = info.scale[1] = info.scale[2] = 1.0D;
            ++this.cubeCount;
            this.cubes.add(info);
            firstCreated = info;
         } else {
            for(int j = 0; j < rend.cubeList.size(); ++j) {
               ModelBox box = (ModelBox)rend.cubeList.get(j);
               CubeInfo info = ModelHelper.createCubeInfoFromModelBox(rend, box, box.boxName != null ? box.boxName.substring(box.boxName.lastIndexOf(".") + 1) : (String)e.getKey() + (rend.cubeList.size() == 1 ? "" : " - " + j));
               ++this.cubeCount;
               this.cubes.add(info);
               if (firstCreated == null) {
                  firstCreated = info;
               }
            }
         }

         if (firstCreated != null) {
            this.createChildren((String)e.getKey(), rend, firstCreated);
         }
      }

      if (texture) {
         if (model.texture == null) {
            this.bufferedTexture = null;
            return true;
         }

         InputStream inputstream = null;

         try {
            IResource iresource = Minecraft.getMinecraft().mcResourceManager.getResource(model.texture);
            inputstream = iresource.getInputStream();
            this.bufferedTexture = ImageIO.read(inputstream);
            if (this.bufferedTexture != null) {
               Iterator var14 = model.modelList.entrySet().iterator();
               if (var14.hasNext()) {
                  Entry<String, ModelRenderer> e = (Entry)var14.next();
                  ModelRenderer rend = (ModelRenderer)e.getValue();
                  this.textureHeight = (int)rend.textureHeight;
                  this.textureWidth = (int)rend.textureWidth;
               }

               return true;
            }
         } catch (Exception var10) {
            ;
         }
      }

      return false;
   }

   @SideOnly(Side.CLIENT)
   public void createChildren(String name, ModelRenderer rend, CubeInfo info) {
      if (rend.childModels != null) {
         for(int i = 0; i < rend.childModels.size(); ++i) {
            CubeInfo firstCreated = null;
            ModelRenderer rend1 = (ModelRenderer)rend.childModels.get(i);

            for(int j = 0; j < rend1.cubeList.size(); ++j) {
               String fullName = name + (rend1.cubeList.size() == 1 ? "Child" : "Child - " + j);
               ModelBox box = (ModelBox)rend1.cubeList.get(j);
               if (box.boxName != null) {
                  fullName = box.boxName.substring(box.boxName.lastIndexOf(".") + 1);
               }

               CubeInfo info1 = new CubeInfo(fullName);
               info1.dimensions[0] = (int)Math.abs(box.posX2 - box.posX1);
               info1.dimensions[1] = (int)Math.abs(box.posY2 - box.posY1);
               info1.dimensions[2] = (int)Math.abs(box.posZ2 - box.posZ1);
               info1.position[0] = (double)rend1.rotationPointX;
               info1.position[1] = (double)rend1.rotationPointY;
               info1.position[2] = (double)rend1.rotationPointZ;
               info1.offset[0] = (double)box.posX1;
               info1.offset[1] = (double)box.posY1;
               info1.offset[2] = (double)box.posZ1;
               info1.rotation[0] = Math.toDegrees((double)rend1.rotateAngleX);
               info1.rotation[1] = Math.toDegrees((double)rend1.rotateAngleY);
               info1.rotation[2] = Math.toDegrees((double)rend1.rotateAngleZ);
               info1.scale[0] = info1.scale[1] = info1.scale[2] = 1.0D;
               info1.txOffset[0] = rend1.textureOffsetX;
               info1.txOffset[1] = rend1.textureOffsetY;
               if (box.boxName != null) {
                  TextureOffset textureoffset = rend1.baseModel.getTextureOffset(box.boxName);
                  if (textureoffset != null) {
                     info1.txOffset[0] = textureoffset.textureOffsetX;
                     info1.txOffset[1] = textureoffset.textureOffsetY;
                  }
               }

               PositionTextureVertex[] vertices = box.quadList[1].vertexPositions;
               info1.txMirror = (vertices[info1.txMirror ? 1 : 2].vector3D.y - vertices[info1.txMirror ? 3 : 0].vector3D.y - (double)info1.dimensions[1]) / 2.0D < 0.0D;
               ++this.cubeCount;
               info.addChild(info1);
               if (firstCreated == null) {
                  firstCreated = info1;
               }
            }

            if (firstCreated != null) {
               this.createChildren(name + "Child", rend1, firstCreated);
            }
         }

      }
   }

   public void cloneFrom(ProjectInfo info) {
      this.saveFile = info.saveFile;
      this.saveFileMd5 = info.saveFileMd5;
      this.textureFile = info.textureFile;
      this.ignoreNextImage = info.ignoreNextImage;
      this.bufferedTexture = info.bufferedTexture;
      this.cameraFov = info.cameraFov;
      this.cameraZoom = info.cameraZoom;
      this.cameraYaw = info.cameraYaw;
      this.cameraPitch = info.cameraPitch;
      this.cameraOffsetX = info.cameraOffsetX;
      this.cameraOffsetY = info.cameraOffsetY;
      this.lastAutosave = info.lastAutosave;
   }

   public void inherit(ProjectInfo info) {
      this.cloneFrom(info);
      this.identifier = info.identifier;
      this.textureFileMd5 = info.textureFileMd5;
      this.states = info.states;
      this.lastState = info.lastState;
      this.projVersion = info.projVersion;
      this.textureWidth = info.textureWidth;
      this.textureHeight = info.textureHeight;
      this.switchState = info.switchState;
   }

   public float getMaximumSize() {
      ArrayList<CubeInfo> allCubes = this.getAllCubes();
      double minX = 0.0D;
      double minY = 0.0D;
      double minZ = 0.0D;
      double maxX = 0.0D;
      double maxY = 0.0D;
      double maxZ = 0.0D;

      for(CubeInfo info : allCubes) {
         if ((info.position[0] + info.offset[0]) * info.scale[0] < minX) {
            minX = (info.position[0] + info.offset[0]) * info.scale[0];
         }

         if (((double)info.dimensions[0] + info.position[0] + info.offset[0]) * info.scale[0] > maxX) {
            maxX = ((double)info.dimensions[0] + info.position[0] + info.offset[0]) * info.scale[0];
         }

         if ((info.position[1] + info.offset[1]) * info.scale[1] < minY) {
            minY = (info.position[1] + info.offset[1]) * info.scale[1];
         }

         if (((double)info.dimensions[1] + info.position[1] + info.offset[1]) * info.scale[1] > maxY) {
            maxY = ((double)info.dimensions[1] + info.position[1] + info.offset[1]) * info.scale[1];
         }

         if ((info.position[2] + info.offset[2]) * info.scale[2] < minZ) {
            minZ = (info.position[2] + info.offset[2]) * info.scale[2];
         }

         if (((double)info.dimensions[2] + info.position[2] + info.offset[2]) * info.scale[2] > maxZ) {
            maxZ = ((double)info.dimensions[2] + info.position[2] + info.offset[2]) * info.scale[2];
         }
      }

      return (float)Math.max((maxX - minX) / this.scale[0], Math.max((maxY - minY) / (this.scale[1] * 2.0D), (maxZ - minZ) / this.scale[2]));
   }

   public ProjectInfo repair() {
      for(; this.projVersion < 4; ++this.projVersion) {
         if (this.projVersion == 1) {
            this.scale = new double[]{1.0D, 1.0D, 1.0D};

            for(CubeInfo info : this.getAllCubes()) {
               info.opacity = 100.0D;
            }
         } else if (this.projVersion == 2) {
            this.metadata = new ArrayList();
         } else if (this.projVersion == 3) {
            for(CubeGroup group : this.cubeGroups) {
               group.metadata = new ArrayList();
            }

            for(CubeInfo info : this.getAllCubes()) {
               info.metadata = new ArrayList();
            }
         }
      }

      return this;
   }

   public static boolean saveProject(ProjectInfo info, File file) {
      try {
         file.getParentFile().mkdirs();
         ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
         out.setLevel(9);
         out.putNextEntry(new ZipEntry("model.json"));
         byte[] data = (new Gson()).toJson(info).getBytes();
         out.write(data, 0, data.length);
         out.closeEntry();
         if (info.bufferedTexture != null) {
            out.putNextEntry(new ZipEntry("texture.png"));
            ImageIO.write(info.bufferedTexture, "png", out);
            out.closeEntry();
         }

         out.close();
         info.saved = true;
         return true;
      } catch (Exception var4) {
         iChunUtil.LOGGER.warn("Failed to save model: " + info.modelName);
         var4.printStackTrace();
         return false;
      }
   }
}
