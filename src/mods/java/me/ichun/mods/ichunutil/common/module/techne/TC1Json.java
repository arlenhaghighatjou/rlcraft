package me.ichun.mods.ichunutil.common.module.techne;

import com.google.gson.annotations.SerializedName;

public class TC1Json {
   public TC1Json.Techne Techne = new TC1Json.Techne();

   public TC1Json() {
   }

   public TC2Info toTC2Info() {
      TC2Info info = new TC2Info();
      info.Techne.Version = this.Techne.Version;
      info.Techne.Author = this.Techne.Author.equals("ZeuX") ? "NotZeuX" : this.Techne.Author;
      info.Techne.Name = this.Techne.Name;
      info.Techne.PreviewImage = this.Techne.PreviewImage;
      info.Techne.ProjectName = this.Techne.ProjectName;
      info.Techne.ProjectType = this.Techne.ProjectType;
      info.Techne.Description = this.Techne.Description;
      info.Techne.DateCreated = this.Techne.DateCreated;
      info.Techne.createNewModelArray(this.Techne.Models.length);
      int id = 0;

      for(int i = 0; i < this.Techne.Models.length; ++i) {
         info.Techne.Models[i].Model.GlScale = this.Techne.Models[i].Model.GlScale;
         info.Techne.Models[i].Model.Name = this.Techne.Models[i].Model.Name;
         info.Techne.Models[i].Model.TextureSize = this.Techne.Models[i].Model.TextureSize;
         info.Techne.Models[i].Model.texture = "texture.png";
         info.Techne.Models[i].Model.BaseClass = this.Techne.Models[i].Model.BaseClass;
         info.Techne.Models[i].Model.Geometry.createNewShapeArray(this.Techne.Models[i].Model.Geometry.Shape.length);

         for(int j = 0; j < this.Techne.Models[i].Model.Geometry.Shape.length; ++j) {
            info.Techne.Models[i].Model.Geometry.Shape[j].Id = id++;
            info.Techne.Models[i].Model.Geometry.Shape[j].Name = this.Techne.Models[i].Model.Geometry.Shape[j].Name;
            info.Techne.Models[i].Model.Geometry.Shape[j].IsDecorative = this.Techne.Models[i].Model.Geometry.Shape[j].IsDecorative;
            info.Techne.Models[i].Model.Geometry.Shape[j].IsFixed = this.Techne.Models[i].Model.Geometry.Shape[j].IsFixed;
            info.Techne.Models[i].Model.Geometry.Shape[j].IsMirrored = this.Techne.Models[i].Model.Geometry.Shape[j].IsMirrored;
            info.Techne.Models[i].Model.Geometry.Shape[j].Position = this.Techne.Models[i].Model.Geometry.Shape[j].Position;
            info.Techne.Models[i].Model.Geometry.Shape[j].Offset = this.Techne.Models[i].Model.Geometry.Shape[j].Offset;
            String[] rotation = this.Techne.Models[i].Model.Geometry.Shape[j].Rotation.split(",");
            info.Techne.Models[i].Model.Geometry.Shape[j].Rotation = (rotation[0].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[0])))) + "," + (rotation[1].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[1])))) + "," + (rotation[2].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[2]))));
            info.Techne.Models[i].Model.Geometry.Shape[j].Size = this.Techne.Models[i].Model.Geometry.Shape[j].Size;
            info.Techne.Models[i].Model.Geometry.Shape[j].TextureOffset = this.Techne.Models[i].Model.Geometry.Shape[j].TextureOffset;
         }

         if (this.Techne.Models[i].Model.Geometry.Folder != null) {
            for(int j = 0; j < this.Techne.Models[i].Model.Geometry.Folder.length; ++j) {
               info.Techne.Models[i].Model.Geometry.extendNullArray();
               id = this.populateNull(id, info.Techne.Models[i].Model.Geometry.Null[j], this.Techne.Models[i].Model.Geometry.Folder[j]);
            }
         }
      }

      return info;
   }

   public int populateNull(int id, TC2Info.Null tc2Null, TC1Json.Null tc1Null) {
      if (tc1Null.Folder != null) {
         for(int j = 0; j < tc1Null.Folder.length; ++j) {
            tc2Null.Children.extendNullArray();
            id = this.populateNull(id, tc2Null.Children.Null[j], tc1Null.Folder[j]);
         }
      }

      tc2Null.Children.createNewShapeArray(tc1Null.Shape.length);

      for(int j = 0; j < tc1Null.Shape.length; ++j) {
         tc2Null.Children.Shape[j].Id = id++;
         tc2Null.Children.Shape[j].Name = tc1Null.Shape[j].Name;
         tc2Null.Children.Shape[j].IsDecorative = tc1Null.Shape[j].IsDecorative;
         tc2Null.Children.Shape[j].IsFixed = tc1Null.Shape[j].IsFixed;
         tc2Null.Children.Shape[j].IsMirrored = tc1Null.Shape[j].IsMirrored;
         tc2Null.Children.Shape[j].Position = tc1Null.Shape[j].Position;
         tc2Null.Children.Shape[j].Offset = tc1Null.Shape[j].Offset;
         String[] rotation = tc1Null.Shape[j].Rotation.split(",");
         tc2Null.Children.Shape[j].Rotation = (rotation[0].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[0])))) + "," + (rotation[1].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[1])))) + "," + (rotation[2].equals("0") ? "0" : Float.toString((float)Math.toRadians((double)Float.parseFloat(rotation[2]))));
         tc2Null.Children.Shape[j].Size = tc1Null.Shape[j].Size;
         tc2Null.Children.Shape[j].TextureOffset = tc1Null.Shape[j].TextureOffset;
      }

      return id;
   }

   public class Animation {
      public String AnimationAngles = "0,0,0";
      public String AnimationDuration = "0,0,0";
      public String AnimationType = "0,0,0";

      public Animation() {
      }
   }

   public class Group {
      public TC1Json.Shape[] Shape = new TC1Json.Shape[0];
      public TC1Json.Null[] Folder = new TC1Json.Null[0];

      public Group() {
      }
   }

   public class Model {
      public TC1Json.ModelInfo Model = TC1Json.this.new ModelInfo();

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
      public TC1Json.Group Geometry = TC1Json.this.new Group();

      public ModelInfo() {
      }
   }

   public class Null {
      @SerializedName("@type")
      public String Type = "3b3bb6e5-2f8b-4bbd-8dbb-478b67762fd0";
      @SerializedName("@Name")
      public String Name = "null element";
      public TC1Json.Shape[] Shape = new TC1Json.Shape[0];
      public TC1Json.Null[] Folder = new TC1Json.Null[0];

      public Null() {
      }
   }

   public class Shape {
      @SerializedName("@type")
      public String Type = "d9e621f7-957f-4b77-b1ae-20dcd0da7751";
      @SerializedName("@name")
      public String Name = "new cube";
      public TC1Json.Animation Animation = TC1Json.this.new Animation();
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
      public TC1Json.Model[] Models = new TC1Json.Model[0];

      public Techne() {
      }
   }
}
