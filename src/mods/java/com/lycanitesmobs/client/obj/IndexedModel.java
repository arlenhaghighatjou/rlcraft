package com.lycanitesmobs.client.obj;

import java.util.ArrayList;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class IndexedModel {
   private ArrayList<Vector3f> vertices = new ArrayList();
   private ArrayList<Vector2f> texCoords = new ArrayList();
   private ArrayList<Vector3f> normals = new ArrayList();
   private ArrayList<Vector3f> tangents = new ArrayList();
   private ArrayList<Integer> indices = new ArrayList();
   private ArrayList<OBJLoader.OBJIndex> objindices = new ArrayList();

   public IndexedModel() {
   }

   public ArrayList<Vector3f> getPositions() {
      return this.vertices;
   }

   public ArrayList<Vector2f> getTexCoords() {
      return this.texCoords;
   }

   public ArrayList<Vector3f> getNormals() {
      return this.normals;
   }

   public ArrayList<Integer> getIndices() {
      return this.indices;
   }

   public ArrayList<Vector3f> getTangents() {
      return this.tangents;
   }

   public void toMesh(Mesh mesh) {
      ArrayList<Vertex> verticesList = new ArrayList();
      int n = Math.min(this.vertices.size(), Math.min(this.texCoords.size(), this.normals.size()));

      for(int i = 0; i < n; ++i) {
         Vertex vertex = new Vertex((Vector3f)this.vertices.get(i), (Vector2f)this.texCoords.get(i), (Vector3f)this.normals.get(i), new Vector3f());
         verticesList.add(vertex);
      }

      Integer[] indicesArray = (Integer[])this.indices.toArray(new Integer[0]);
      Vertex[] verticesArray = (Vertex[])verticesList.toArray(new Vertex[0]);
      int[] indicesArrayInt = new int[indicesArray.length];

      for(int i = 0; i < indicesArray.length; ++i) {
         indicesArrayInt[i] = indicesArray[i].intValue();
      }

      mesh.vertices = verticesArray;
      mesh.indices = indicesArrayInt;
   }

   public void computeNormals() {
      for(int i = 0; i < this.indices.size(); i += 3) {
         int x = ((Integer)this.indices.get(i)).intValue();
         int y = ((Integer)this.indices.get(i + 1)).intValue();
         int z = ((Integer)this.indices.get(i + 2)).intValue();
         Vector3f v = (Vector3f)((Vector3f)this.vertices.get(y)).clone();
         v.sub((Tuple3f)this.vertices.get(x));
         Vector3f var10 = (Vector3f)((Vector3f)this.vertices.get(z)).clone();
         var10.sub((Tuple3f)this.vertices.get(x));
         Vector3f var11 = (Vector3f)v.clone();
         var11.cross(v, var10);
         v = (Vector3f)((Vector3f)this.normals.get(x)).clone();
         v.add(var11);
         this.normals.set(x, v);
         v = (Vector3f)((Vector3f)this.normals.get(y)).clone();
         v.add(var11);
         this.normals.set(y, v);
         v = (Vector3f)((Vector3f)this.normals.get(z)).clone();
         v.add(var11);
         this.normals.set(z, v);
      }

      for(int i = 0; i < this.normals.size(); ++i) {
         ((Vector3f)this.normals.get(i)).normalize();
      }

   }

   public Vector3f getFaceNormal(Vector3f p1, Vector3f p2, Vector3f p3) {
      Vector3f output = new Vector3f();
      Vector3f calU = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
      Vector3f calV = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
      output.x = calU.y * calV.z - calU.z * calV.y;
      output.y = calU.z * calV.x - calU.x * calV.z;
      output.z = calU.x * calV.y - calU.y * calV.x;
      output.normalize();
      return output;
   }

   public void computeTangents() {
      this.tangents.clear();

      for(int i = 0; i < this.vertices.size(); ++i) {
         this.tangents.add(new Vector3f());
      }

      for(int i = 0; i < this.indices.size(); i += 3) {
         int i0 = ((Integer)this.indices.get(i)).intValue();
         int i1 = ((Integer)this.indices.get(i + 1)).intValue();
         int i2 = ((Integer)this.indices.get(i + 2)).intValue();
         Vector3f v = (Vector3f)((Vector3f)this.vertices.get(i1)).clone();
         v.sub((Tuple3f)this.vertices.get(i0));
         Vector3f var23 = (Vector3f)((Vector3f)this.vertices.get(i2)).clone();
         var23.sub((Tuple3f)this.vertices.get(i0));
         double deltaU1 = (double)(((Vector2f)this.texCoords.get(i1)).x - ((Vector2f)this.texCoords.get(i0)).x);
         double deltaU2 = (double)(((Vector2f)this.texCoords.get(i2)).x - ((Vector2f)this.texCoords.get(i0)).x);
         double deltaV1 = (double)(((Vector2f)this.texCoords.get(i1)).y - ((Vector2f)this.texCoords.get(i0)).y);
         double deltaV2 = (double)(((Vector2f)this.texCoords.get(i2)).y - ((Vector2f)this.texCoords.get(i0)).y);
         double dividend = deltaU1 * deltaV2 - deltaU2 * deltaV1;
         double f = dividend == 0.0D ? 0.0D : 1.0D / dividend;
         Vector3f tangent = new Vector3f((float)(f * (deltaV2 * (double)v.x - deltaV1 * (double)var23.x)), (float)(f * (deltaV2 * (double)v.y - deltaV1 * (double)var23.y)), (float)(f * (deltaV2 * (double)v.z - deltaV1 * (double)var23.z)));
         v = (Vector3f)((Vector3f)this.tangents.get(i0)).clone();
         v.add(tangent);
         this.tangents.set(i0, v);
         v = (Vector3f)((Vector3f)this.tangents.get(i1)).clone();
         v.add(tangent);
         this.tangents.set(i1, v);
         v = (Vector3f)((Vector3f)this.tangents.get(i2)).clone();
         v.add(tangent);
         this.tangents.set(i2, v);
      }

      for(int i = 0; i < this.tangents.size(); ++i) {
         ((Vector3f)this.tangents.get(i)).normalize();
      }

   }

   public ArrayList<OBJLoader.OBJIndex> getObjIndices() {
      return this.objindices;
   }

   public org.lwjgl.util.vector.Vector3f computeCenter() {
      float x = 0.0F;
      float y = 0.0F;
      float z = 0.0F;

      for(Vector3f position : this.vertices) {
         x += position.x;
         y += position.y;
         z += position.z;
      }

      x = x / (float)this.vertices.size();
      y = y / (float)this.vertices.size();
      z = z / (float)this.vertices.size();
      return new org.lwjgl.util.vector.Vector3f(x, y, z);
   }
}
