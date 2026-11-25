package me.ichun.mods.ichunutil.common.module.techne;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import me.ichun.mods.ichunutil.common.iChunUtil;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TC2Info {
   public TC2Info.Techne Techne = new TC2Info.Techne();
   public transient boolean tampered;

   public TC2Info() {
   }

   public static TC2Info readTechneFile(File file) {
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

         TC2Info info = null;
         if (modelInfo != null) {
            if (modelInfo.getName().endsWith(".xml")) {
               info = convertTechneFile(zipFile.getInputStream(modelInfo), images);
            } else {
               info = readTechne2File(zipFile.getInputStream(modelInfo), images);
            }
         }

         zipFile.close();
         if (info != null) {
            info.tampered = tampered;
         }

         return info;
      } catch (Exception var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public static TC2Info readTechneFile(InputStream stream) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         IOUtils.copy(stream, baos);
         baos.flush();
         ZipInputStream cloneXML = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
         ZipEntry entry = null;
         boolean hasXML = false;
         boolean hasJSON = false;

         while((entry = cloneXML.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
               if (entry.getName().endsWith(".xml")) {
                  hasXML = true;
                  break;
               }

               if (entry.getName().endsWith(".json")) {
                  hasJSON = true;
                  break;
               }
            }
         }

         entry = null;
         ZipInputStream clonePNG = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
         HashMap<String, InputStream> images = new HashMap();
         boolean tampered = false;

         while((entry = clonePNG.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
               if (entry.getName().endsWith(".png") && !images.containsKey(entry.getName()) && entry.getCrc() != Long.decode("0xf970c898").longValue()) {
                  images.put(entry.getName(), clonePNG);
                  clonePNG = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
               } else if (!entry.getName().endsWith(".png") && !entry.getName().endsWith(".xml") && !entry.getName().endsWith(".json")) {
                  tampered = true;
               }
            }
         }

         stream.close();
         if (!images.isEmpty()) {
            if (hasXML) {
               TC2Info info = convertTechneFile(cloneXML, images);
               if (info != null) {
                  info.tampered = tampered;
               }

               return info;
            }

            if (hasJSON) {
               TC2Info info = readTechne2File(cloneXML, images);
               if (info != null) {
                  info.tampered = tampered;
               }

               return info;
            }
         }

         return null;
      } catch (Exception var10) {
         return null;
      }
   }

   public static TC2Info convertTechneFile(InputStream xml, HashMap<String, InputStream> images) throws IOException, ParserConfigurationException, SAXException {
      if (xml != null && images != null) {
         TC2Info info = new TC2Info();
         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = builder.parse(xml);
         info.Techne.Version = doc.getElementsByTagName("Techne").item(0).getAttributes().item(0).getNodeValue();
         info.Techne.Author = doc.getElementsByTagName("Author").item(0).getTextContent().equals("ZeuX") ? "NotZeux" : doc.getElementsByTagName("Techne").item(0).getTextContent();
         info.Techne.Name = doc.getElementsByTagName("Name").item(0).getTextContent();
         info.Techne.PreviewImage = doc.getElementsByTagName("PreviewImage").item(0).getTextContent();
         info.Techne.ProjectName = doc.getElementsByTagName("ProjectName").item(0).getTextContent();
         info.Techne.ProjectType = doc.getElementsByTagName("ProjectType").item(0).getTextContent();
         info.Techne.Description = doc.getElementsByTagName("Description").item(0).getTextContent();
         info.Techne.DateCreated = doc.getElementsByTagName("DateCreated").item(0).getTextContent();
         NodeList list = doc.getElementsByTagName("Model");
         info.Techne.createNewModelArray(list.getLength());
         int id = 1;

         for(int i = 0; i < list.getLength(); ++i) {
            TC2Info.Model model = info.Techne.Models[i];
            Node node = list.item(i);

            for(int j = 0; j < node.getAttributes().getLength(); ++j) {
               Node attribute = node.getAttributes().item(j);
               if (attribute.getNodeName().equals("texture")) {
                  model.Model.texture = attribute.getNodeValue();
                  InputStream stream = (InputStream)images.get(model.Model.texture);
                  if (stream != null) {
                     model.Model.image = ImageIO.read(stream);
                  }
               }
            }

            Node Geometry = null;
            int shapeCount = 0;

            for(int k = 0; k < node.getChildNodes().getLength(); ++k) {
               Node child = node.getChildNodes().item(k);
               if (child.getNodeName().equals("GlScale")) {
                  model.Model.GlScale = child.getTextContent();
               } else if (child.getNodeName().equals("Name")) {
                  model.Model.Name = child.getTextContent();
               } else if (child.getNodeName().equals("TextureSize")) {
                  model.Model.TextureSize = child.getTextContent();
               } else if (child.getNodeName().equals("BaseClass")) {
                  model.Model.BaseClass = child.getTextContent();
               } else if (child.getNodeName().equals("Geometry")) {
                  id = createShapesFromNode(id, child, model.Model.Geometry);
               }
            }
         }

         xml.close();

         for(Entry<String, InputStream> img : images.entrySet()) {
            ((InputStream)img.getValue()).close();
         }

         return info;
      } else {
         return null;
      }
   }

   private static int createShapesFromNode(int id, Node node, TC2Info.Group group) {
      int shapeCount = 0;

      for(int l = 0; l < node.getChildNodes().getLength(); ++l) {
         Node child = node.getChildNodes().item(l);
         if (child.getNodeName().equals("Shape")) {
            for(int j = 0; j < child.getAttributes().getLength(); ++j) {
               Node attribute = child.getAttributes().item(j);
               if (attribute.getNodeName().equalsIgnoreCase("type") && (attribute.getNodeValue().equalsIgnoreCase("d9e621f7-957f-4b77-b1ae-20dcd0da7751") || attribute.getNodeValue().equalsIgnoreCase("de81aa14-bd60-4228-8d8d-5238bcd3caaa"))) {
                  ++shapeCount;
               }
            }
         } else if (child.getNodeName().equals("Folder") || child.getNodeName().equals("Piece")) {
            group.extendNullArray();

            for(int j = 0; j < child.getAttributes().getLength(); ++j) {
               Node attribute = child.getAttributes().item(j);
               if (attribute.getNodeName().equals("Name")) {
                  group.Null[group.Null.length - 1].Name = attribute.getNodeValue();
               }
            }

            if (child.getNodeName().equals("Piece")) {
               for(int k = 0; k < child.getChildNodes().getLength(); ++k) {
                  if (child.getChildNodes().item(k).equals("Position")) {
                     group.Null[group.Null.length - 1].Position = child.getChildNodes().item(k).getTextContent();
                  }
               }
            }

            createShapesFromNode(id, child, group.Null[group.Null.length - 1].Children);
         }
      }

      group.createNewShapeArray(shapeCount);
      int shapeNum = 0;

      for(int k = 0; k < node.getChildNodes().getLength(); ++k) {
         Node child = node.getChildNodes().item(k);
         if (child.getNodeName().equals("Shape")) {
            for(int j = 0; j < child.getAttributes().getLength(); ++j) {
               Node attribute = child.getAttributes().item(j);
               if (attribute.getNodeName().equals("type") && (attribute.getNodeValue().equalsIgnoreCase("d9e621f7-957f-4b77-b1ae-20dcd0da7751") || attribute.getNodeValue().equalsIgnoreCase("de81aa14-bd60-4228-8d8d-5238bcd3caaa")) && shapeNum < shapeCount) {
                  TC2Info.Shape shape = group.Shape[shapeNum];
                  shape.Id = id++;

                  for(int jj = 0; jj < child.getAttributes().getLength(); ++jj) {
                     Node attribute1 = child.getAttributes().item(jj);
                     if (attribute1.getNodeName().equals("name")) {
                        shape.Name = attribute1.getNodeValue();
                        break;
                     }
                  }

                  for(int kk = 0; kk < child.getChildNodes().getLength(); ++kk) {
                     Node child1 = child.getChildNodes().item(kk);
                     if (child1.getNodeName().equals("IsDecorative")) {
                        shape.IsDecorative = child1.getTextContent();
                     } else if (child1.getNodeName().equals("IsFixed")) {
                        shape.IsFixed = child1.getTextContent();
                     } else if (child1.getNodeName().equals("IsMirrored")) {
                        shape.IsMirrored = child1.getTextContent();
                     } else if (child1.getNodeName().equals("Offset")) {
                        shape.Offset = child1.getTextContent();
                     } else if (child1.getNodeName().equals("Position")) {
                        shape.Position = child1.getTextContent();
                     } else if (child1.getNodeName().equals("Rotation")) {
                        String[] rotation = child1.getTextContent().split(",");
                        shape.Rotation = (rotation[0].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[0])))) + "," + (rotation[1].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[1])))) + "," + (rotation[2].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[2]))));
                     } else if (child1.getNodeName().equals("Size")) {
                        shape.Size = child1.getTextContent();
                     } else if (child1.getNodeName().equals("TextureOffset")) {
                        shape.TextureOffset = child1.getTextContent();
                     }
                  }

                  ++shapeNum;
               }
            }
         }
      }

      return id;
   }

   public static TC2Info readTechne2File(InputStream json, HashMap<String, InputStream> images) throws IOException {
      if (json != null && images != null) {
         StringWriter writer = new StringWriter();
         IOUtils.copy(json, writer);
         String jsonString = writer.toString();

         TC2Info info;
         try {
            info = (TC2Info)(new Gson()).fromJson(jsonString, TC2Info.class);
         } catch (JsonSyntaxException var10) {
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
         }

         json.close();

         for(Entry<String, InputStream> img : images.entrySet()) {
            ((InputStream)img.getValue()).close();
         }

         return info;
      } else {
         return null;
      }
   }

   public boolean saveAsFile(File file, boolean overwrite) {
      try {
         if (!overwrite && file.exists()) {
            return false;
         } else {
            file.getParentFile().mkdirs();
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
            out.setLevel(0);
            ZipEntry entry = new ZipEntry("model.json");
            out.putNextEntry(entry);
            byte[] data = (new Gson()).toJson(this).getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            for(TC2Info.Model model : this.Techne.Models) {
               if (model.Model.image != null) {
                  entry = new ZipEntry(model.Model.texture);
                  out.putNextEntry(entry);
                  ImageIO.write(model.Model.image, "png", out);
                  out.closeEntry();
               }
            }

            out.close();
            return true;
         }
      } catch (Exception var10) {
         iChunUtil.LOGGER.warn("Failed to save Techne 2 Model.");
         var10.printStackTrace();
         return false;
      }
   }

   public class Circular extends TC2Info.Null {
      public int Count;
      public int Radius;

      public Circular() {
         super();
         this.Type = "16932820-ef7c-4b4b-bf05-b72063b3d23c";
         this.Name = "Circular Array";
         this.Count = 5;
         this.Radius = 16;
      }
   }

   public class Group {
      public TC2Info.Circular[] Circular = new TC2Info.Circular[0];
      public TC2Info.Shape[] Shape = new TC2Info.Shape[0];
      public TC2Info.Linear[] Linear = new TC2Info.Linear[0];
      public TC2Info.Null[] Null = new TC2Info.Null[0];

      public Group() {
      }

      public void createNewShapeArray(int size) {
         this.Shape = new TC2Info.Shape[size];

         for(int i = 0; i < this.Shape.length; ++i) {
            this.Shape[i] = TC2Info.this.new Shape();
         }

      }

      public void extendNullArray() {
         TC2Info.Null[] nulls = new TC2Info.Null[this.Null.length + 1];

         for(int i = 0; i < this.Null.length; ++i) {
            nulls[i] = this.Null[i];
         }

         nulls[this.Null.length] = TC2Info.this.new Null();
         this.Null = nulls;
      }
   }

   public class Linear extends TC2Info.Null {
      public String Count;
      public String Spacing;

      public Linear() {
         super();
         this.Type = "fc4f63c9-8296-4c97-abd8-414f20e49bd5";
         this.Name = "Linear Array";
         this.Count = "0,0,0";
         this.Spacing = "0,0,0";
      }
   }

   public class Model {
      public TC2Info.ModelInfo Model = TC2Info.this.new ModelInfo();

      public Model() {
      }
   }

   public class ModelInfo {
      public String GlScale = "1,1,1";
      public String Name = "";
      public String TextureSize = "64,32";
      @SerializedName("@texture")
      public String texture = "texture.png";
      public String BaseClass = "ModelBase";
      public TC2Info.Group Geometry = TC2Info.this.new Group();
      public transient BufferedImage image;

      public ModelInfo() {
      }
   }

   public class Null {
      @SerializedName("@Type")
      public String Type = "3b3bb6e5-2f8b-4bbd-8dbb-478b67762fd0";
      @SerializedName("@Name")
      public String Name = "null element";
      public String Position = "0,0,0";
      public String Rotation = "0,0,0";
      public TC2Info.Group Children = TC2Info.this.new Group();

      public Null() {
      }
   }

   public class Shape {
      public int Id = 1;
      @SerializedName("@Type")
      public String Type = "d9e621f7-957f-4b77-b1ae-20dcd0da7751";
      @SerializedName("@Name")
      public String Name = "new cube";
      public String IsDecorative = "False";
      public String IsFixed = "False";
      public String IsMirrored = "False";
      public String Position = "0,0,0";
      public String Offset = "0,0,0";
      public String Rotation = "0,0,0";
      public String Size = "1,1,1";
      public String TextureOffset = "0,0";

      public Shape() {
      }
   }

   public class Techne {
      @SerializedName("@Version")
      public String Version = "2.2";
      public String Author = "NotZeuX";
      public String Name = "";
      public String PreviewImage = "";
      public String ProjectName = "";
      public String ProjectType = "";
      public String Description = "";
      public String DateCreated = "";
      public TC2Info.Model[] Models = new TC2Info.Model[0];
      private String _comment = "Generated using iChunUtil";

      public Techne() {
      }

      public void createNewModelArray(int size) {
         this.Models = new TC2Info.Model[size];

         for(int i = 0; i < this.Models.length; ++i) {
            this.Models[i] = TC2Info.this.new Model();
         }

      }
   }
}
