package me.ichun.mods.ichunutil.common.module.tabula.formats.types;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.ProjectInfo;
import me.ichun.mods.ichunutil.common.module.tabula.project.TechneConverter;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeGroup;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;
import me.ichun.mods.ichunutil.common.module.techne.TC1Json;
import me.ichun.mods.ichunutil.common.module.techne.TC2Info;
import org.apache.commons.io.IOUtils;

public class ImportTabula extends Importer {
   public ImportTabula() {
   }

   public ProjectInfo createProjectInfo(File file) {
      try {
         ZipFile zipFile = new ZipFile(file);
         Enumeration entries = zipFile.entries();
         ZipEntry modelInfo = null;
         HashMap<String, InputStream> images = new HashMap();
         boolean tampered = false;

         while(entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            if (!entry.isDirectory()) {
               if (entry.getName().endsWith(".png") && entry.getCrc() != Long.decode("0xf970c898").longValue()) {
                  images.put(entry.getName(), zipFile.getInputStream(entry));
               }

               if (entry.getName().endsWith(".xml") || entry.getName().endsWith(".json")) {
                  modelInfo = entry;
               }

               if (!entry.getName().endsWith(".png") && !entry.getName().endsWith(".xml") && !entry.getName().endsWith(".json")) {
                  tampered = true;
               }
            }
         }

         ProjectInfo info = null;
         if (modelInfo != null) {
            if (modelInfo.getName().endsWith(".xml")) {
               info = convertTechneFile(TC2Info.convertTechneFile(zipFile.getInputStream(modelInfo), images), file.getName());
            } else {
               info = readModelFile(zipFile.getInputStream(modelInfo), images, file.getName());
            }
         }

         zipFile.close();
         if (tampered) {
            info.tampered = true;
            iChunUtil.LOGGER.warn(file.getName() + " is a tampered model file.");
         }

         return info;
      } catch (Exception var8) {
         var8.printStackTrace();
         return null;
      }
   }

   public int getProjectVersion() {
      return 4;
   }

   public static ProjectInfo convertTechneFile(TC2Info tc2Info, String fileName) {
      if (tc2Info == null) {
         return null;
      } else {
         ProjectInfo project = new ProjectInfo(fileName, "TechneToTabulaImporter");
         project.projVersion = 4;
         if (!tc2Info.Techne.Author.equals("ZeuX") && !tc2Info.Techne.Author.equals("NotZeux")) {
            project.authorName = tc2Info.Techne.Author;
         }

         float scaleX = 1.0F;
         float scaleY = 1.0F;
         float scaleZ = 1.0F;

         for(TC2Info.Model model : tc2Info.Techne.Models) {
            try {
               String[] textureSize = model.Model.TextureSize.split(",");
               project.textureWidth = Integer.parseInt(textureSize[0]);
               project.textureHeight = Integer.parseInt(textureSize[1]);
               if (project.bufferedTexture == null) {
                  project.bufferedTexture = model.Model.image;
               }

               String[] scale = model.Model.GlScale.split(",");
               scaleX = Float.parseFloat(scale[0]);
               scaleY = Float.parseFloat(scale[1]);
               scaleZ = Float.parseFloat(scale[2]);
               boolean degrees = determineAngleType(model.Model.Geometry);
               project.cubeCount += exploreTC2Info(project.cubes, project.cubeGroups, model.Model.Geometry, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, scaleX, scaleY, scaleZ, degrees);
            } catch (NumberFormatException var13) {
               iChunUtil.LOGGER.warn("Error parsing Techne 2 model for Tabula: Invalid number");
               var13.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException var14) {
               iChunUtil.LOGGER.warn("Error parsing Techne 2 model for Tabula: Array too short");
            }
         }

         return project;
      }
   }

   private static boolean determineAngleType(TC2Info.Group geometry) {
      boolean degrees = false;
      return degrees;
   }

   private static int exploreTC2Info(ArrayList<CubeInfo> cubes, ArrayList<CubeGroup> groups, TC2Info.Group geometry, double posX, double posY, double posZ, double rotX, double rotY, double rotZ, float scaleX, float scaleY, float scaleZ, boolean isDegrees) {
      int cubeCount = 0;
      if (geometry.Shape != null) {
         for(TC2Info.Shape shape : geometry.Shape) {
            CubeInfo info = new CubeInfo(shape.Name == null ? "shape" + Integer.toString(cubeCount + 1) : shape.Name);
            String[] textureOffset = shape.TextureOffset.split(",");
            info.txOffset = new int[]{Integer.parseInt(textureOffset[0]), Integer.parseInt(textureOffset[1])};
            info.txMirror = !shape.IsMirrored.equals("False");
            String[] pos = shape.Position.split(",");
            String[] rot = shape.Rotation.split(",");
            String[] size = shape.Size.split(",");
            String[] offset = new String[]{"0", "0", "0"};
            if (shape.Offset != null) {
               offset = shape.Offset.split(",");
            }

            info.position = new double[]{(double)Float.parseFloat(pos[0]) + posX, (double)Float.parseFloat(pos[1]) + posY, (double)Float.parseFloat(pos[2]) + posZ};
            info.offset = new double[]{(double)Float.parseFloat(offset[0]), (double)Float.parseFloat(offset[1]), (double)Float.parseFloat(offset[2])};
            info.dimensions = new int[]{Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2])};
            double rX = (double)Float.parseFloat(rot[0]) + rotX;
            double rY = (double)Float.parseFloat(rot[1]) + rotY;
            double rZ = (double)Float.parseFloat(rot[2]) + rotZ;
            TechneConverter.Rotation rotation;
            if (isDegrees) {
               rotation = TechneConverter.fromTechne(TechneConverter.Rotation.createFromDegrees(rX, rY, rZ));
            } else {
               rotation = TechneConverter.fromTechne(TechneConverter.Rotation.createFromRadians(rX, rY, rZ));
            }

            info.rotation = new double[]{rotation.getDegreesX(), rotation.getDegreesY(), rotation.getDegreesZ()};
            info.scale = new double[]{(double)scaleX, (double)scaleY, (double)scaleZ};
            cubes.add(info);
            ++cubeCount;
         }
      }

      if (geometry.Null != null) {
         for(TC2Info.Null nul : geometry.Null) {
            CubeGroup group = new CubeGroup(nul.Name);
            groups.add(group);
            String[] pos = nul.Position.split(",");
            String[] rot = nul.Rotation.split(",");
            cubeCount += exploreTC2Info(group.cubes, group.cubeGroups, nul.Children, (double)Float.parseFloat(pos[0]), (double)Float.parseFloat(pos[1]), (double)Float.parseFloat(pos[2]), (double)Float.parseFloat(rot[0]), (double)Float.parseFloat(rot[1]), (double)Float.parseFloat(rot[2]), scaleX, scaleY, scaleZ, isDegrees);
         }
      }

      return cubeCount;
   }

   public static ProjectInfo readModelFile(InputStream json, HashMap<String, InputStream> images, String fileName) throws IOException {
      if (json != null && images != null) {
         StringWriter writer = new StringWriter();
         IOUtils.copy(json, writer);
         String jsonString = writer.toString();
         ProjectInfo project = null;
         if ((jsonString.contains("\"Techne\"") || jsonString.contains("\"\u0000T\u0000e\u0000c\u0000h\u0000n\u0000e\u0000\"")) && (jsonString.contains("@Version") || jsonString.contains("\u0000@\u0000V\u0000e\u0000r\u0000s\u0000i\u0000o\u0000n\u0000")) && !jsonString.contains("TechneToTabulaImporter")) {
            TC2Info info;
            try {
               info = (TC2Info)(new Gson()).fromJson(jsonString, TC2Info.class);
            } catch (JsonSyntaxException var12) {
               info = ((TC1Json)(new Gson()).fromJson(jsonString.replaceAll("\u0000", ""), TC1Json.class)).toTC2Info();
            }

            if (info != null) {
               for(TC2Info.Model model : info.Techne.Models) {
                  InputStream stream = (InputStream)images.get(model.Model.texture);
                  if (stream != null) {
                     model.Model.image = ImageIO.read(stream);
                     stream.close();
                  }
               }

               project = convertTechneFile(info, fileName);
            }
         } else {
            project = (ProjectInfo)(new Gson()).fromJson(jsonString, ProjectInfo.class);
            InputStream stream = (InputStream)images.get("texture.png");
            if (stream != null) {
               project.bufferedTexture = ImageIO.read(stream);
               stream.close();
            }
         }

         json.close();

         for(Entry<String, InputStream> img : images.entrySet()) {
            ((InputStream)img.getValue()).close();
         }

         return project;
      } else {
         return null;
      }
   }
}
