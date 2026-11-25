package net.ilexiconn.llibrary.client.model.obj;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Matrix3f;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class Shape {
   private String name;
   private OBJModel model;
   private List<Face> faceList = new ArrayList();
   private List<Vertex> vertexList = new ArrayList();
   private List<TextureCoords> textureCoordsList = new ArrayList();

   public Shape(OBJModel parent, String name) {
      this.model = parent;
      this.name = name;
   }

   public Matrix3f rotationMatrix(float angle, float x, float y, float z) {
      angle = angle * 0.017453292F;
      Vector3f axis = new Vector3f(x, y, z);
      axis.normalise();
      float s = (float)Math.sin((double)angle);
      float c = (float)Math.cos((double)angle);
      float oc = 1.0F - c;
      Matrix3f mat = new Matrix3f();
      mat.m00 = oc * axis.x * axis.x + c;
      mat.m01 = oc * axis.x * axis.y - axis.z * s;
      mat.m02 = oc * axis.z * axis.x + axis.y * s;
      mat.m10 = oc * axis.x * axis.y + axis.z * s;
      mat.m11 = oc * axis.y * axis.y + c;
      mat.m12 = oc * axis.y * axis.z - axis.x * s;
      mat.m20 = oc * axis.z * axis.x - axis.y * s;
      mat.m21 = oc * axis.y * axis.z + axis.x * s;
      mat.m22 = oc * axis.z * axis.z + c;
      return mat;
   }

   public Face addFace(Face face) {
      this.faceList.add(face);
      return face;
   }

   public Vertex addVertex(Vertex vertex) {
      for(Vertex v : this.vertexList) {
         if (v.equals(vertex)) {
            return v;
         }
      }

      this.vertexList.add(vertex);
      vertex.register(this.model);
      return vertex;
   }

   public TextureCoords addTexCoords(TextureCoords textureCoords) {
      for(TextureCoords t : this.textureCoordsList) {
         if (t.equals(textureCoords)) {
            return t;
         }
      }

      this.textureCoordsList.add(textureCoords);
      textureCoords.register(this.model);
      return textureCoords;
   }

   public void translate(Vector3f translationVector) {
      for(Vertex vertex : this.vertexList) {
         Vector3f.add(vertex.getPosition(), translationVector, vertex.getPosition());
      }

   }

   public void scale(Vector3f scaleVector) {
      for(Vertex vertex : this.vertexList) {
         Vector3f var10000 = vertex.getPosition();
         var10000.x *= scaleVector.x;
         var10000 = vertex.getPosition();
         var10000.y *= scaleVector.y;
         var10000 = vertex.getPosition();
         var10000.z *= scaleVector.z;
      }

   }

   public void rotate(float angle, float x, float y, float z) {
      Matrix3f rotationMatrix = this.rotationMatrix(angle, x, y, z);

      for(Vertex vertex : this.vertexList) {
         Matrix3f.transform(rotationMatrix, vertex.getPosition(), vertex.getPosition());
      }

   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("o ").append(this.name).append("\n");

      for(Vertex vertex : this.vertexList) {
         builder.append(vertex.toString()).append("\n");
      }

      for(TextureCoords textureCoords : this.textureCoordsList) {
         builder.append(textureCoords.toString()).append("\n");
      }

      for(Face face : this.faceList) {
         builder.append(face.toString()).append("\n");
      }

      return builder.toString();
   }
}
