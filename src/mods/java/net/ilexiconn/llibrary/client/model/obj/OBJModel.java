package net.ilexiconn.llibrary.client.model.obj;

import java.util.ArrayList;
import java.util.List;

public class OBJModel {
   private List<Shape> shapeList = new ArrayList();
   private int vertexIndex = 1;
   private int textureIndex = 1;

   public OBJModel() {
   }

   public Shape addShape(Shape shape) {
      this.shapeList.add(shape);
      return shape;
   }

   public int getVertexIndex() {
      return this.vertexIndex++;
   }

   public int getUVIndex() {
      return this.textureIndex++;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();

      for(Shape shape : this.shapeList) {
         for(String l : shape.toString().split("\n")) {
            builder.append(l).append("\n");
         }
      }

      return builder.toString();
   }
}
