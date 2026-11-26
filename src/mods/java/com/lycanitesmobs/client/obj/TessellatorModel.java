package com.lycanitesmobs.client.obj;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class TessellatorModel extends ObjModel {
   public static final EventBus MODEL_RENDERING_BUS = new EventBus();

   public TessellatorModel(ResourceLocation resourceLocation) {
      super(resourceLocation.getResourcePath());
      String path = resourceLocation.toString();

      try {
         InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
         String content = new String(this.read(inputStream), "UTF-8");
         String startPath = path.substring(0, path.lastIndexOf(47) + 1);
         HashMap<ObjObject, IndexedModel> map = (new OBJLoader()).loadModel(startPath, content);
         this.objObjects.clear();

         for(ObjObject object : map.keySet()) {
            Mesh mesh = new Mesh();
            object.mesh = mesh;
            this.objObjects.add(object);
            ((IndexedModel)map.get(object)).toMesh(mesh);
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   public void renderImpl() {
      Collections.sort(this.objObjects, (a, b) -> {
         Vec3d v = Minecraft.getMinecraft().getRenderViewEntity().getPositionVector();
         double aDist = v.distanceTo(new Vec3d((double)a.center.x, (double)a.center.y, (double)a.center.z));
         double bDist = v.distanceTo(new Vec3d((double)b.center.x, (double)b.center.y, (double)b.center.z));
         return Double.compare(aDist, bDist);
      });

      for(ObjObject object : this.objObjects) {
         this.renderGroup(object);
      }

   }

   public void renderGroupsImpl(String group) {
      for(ObjObject object : this.objObjects) {
         if (object.getName().equals(group)) {
            this.renderGroup(object);
         }
      }

   }

   public void renderGroupImpl(ObjObject obj, Vector4f color, Vector2f textureOffset, VertexFormat vertexFormat) {
      Tessellator tess = Tessellator.getInstance();
      WorldRenderer bufferBuilder = tess.getBuffer();
      if (obj.mesh != null) {
         int[] indices = obj.mesh.indices;
         Vertex[] vertices = obj.mesh.vertices;
         if (obj.mesh.normals == null) {
            obj.mesh.normals = new Vector3f[indices.length];
         }

         bufferBuilder.begin(4, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

         for(int i = 0; i < indices.length; i += 3) {
            Vector3f normal = obj.mesh.normals[i];
            if (normal == null) {
               normal = this.getNormal(vertices[indices[i]].getPos(), vertices[indices[i + 1]].getPos(), vertices[indices[i + 2]].getPos());
               obj.mesh.normals[i] = normal;
            }

            for(int iv = 0; iv < 3; ++iv) {
               Vertex v = obj.mesh.vertices[indices[i + iv]];
               bufferBuilder.pos((double)v.getPos().x, (double)v.getPos().y, (double)v.getPos().z).tex((double)(v.getTexCoords().x + textureOffset.getX() * 0.01F), (double)(1.0F - (v.getTexCoords().y + textureOffset.getY() * 0.01F))).color(color.x, color.y, color.z, color.w).normal(normal.x, normal.y, normal.z).endVertex();
            }
         }

         tess.draw();
      }
   }

   public boolean fireEvent(ObjEvent event) {
      Event evt = null;
      if (event.type == ObjEvent.EventType.PRE_RENDER_GROUP) {
         evt = new TessellatorModelEvent.RenderGroupEvent.Pre(((ObjObject)event.data[1]).getName(), this);
      } else if (event.type == ObjEvent.EventType.POST_RENDER_GROUP) {
         evt = new TessellatorModelEvent.RenderGroupEvent.Post(((ObjObject)event.data[1]).getName(), this);
      } else if (event.type == ObjEvent.EventType.PRE_RENDER_ALL) {
         evt = new TessellatorModelEvent.RenderPre(this);
      } else if (event.type == ObjEvent.EventType.POST_RENDER_ALL) {
         evt = new TessellatorModelEvent.RenderPost(this);
      }

      if (evt != null) {
         return !MODEL_RENDERING_BUS.post(evt);
      } else {
         return true;
      }
   }

   public Vector3f getNormal(Vector3f p1, Vector3f p2, Vector3f p3) {
      Vector3f output = new Vector3f();
      Vector3f calU = new Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
      Vector3f calV = new Vector3f(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
      output.x = calU.y * calV.z - calU.z * calV.y;
      output.y = calU.z * calV.x - calU.x * calV.z;
      output.z = calU.x * calV.y - calU.y * calV.x;
      output.normalize();
      return output;
   }
}
