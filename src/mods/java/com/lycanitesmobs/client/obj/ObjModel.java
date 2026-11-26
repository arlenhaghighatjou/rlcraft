package com.lycanitesmobs.client.obj;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import java.io.ByteArrayOutputStream;

public abstract class ObjModel extends Model {
   public List<ObjObject> objObjects;
   protected String filename;

   ObjModel() {
      this.objObjects = new ArrayList();
   }

   public ObjModel(String classpathElem) {
      this();
      this.filename = classpathElem;
      if (this.filename.contains("/")) {
         this.setID(this.filename.substring(this.filename.lastIndexOf("/") + 1));
      } else {
         this.setID(this.filename);
      }

   }

   protected byte[] read(InputStream resource) throws IOException {
      byte[] buffer = new byte[65565];
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      int i;
      while((i = resource.read(buffer, 0, buffer.length)) != -1) {
         out.write(buffer, 0, i);
      }

      out.flush();
      out.close();
      return out.toByteArray();
   }

   public void renderGroup(ObjObject group) {
      this.renderGroup(group, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector2f(0.0F, 0.0F), (VertexFormat)null);
   }

   public void renderGroup(ObjObject group, Vector4f color, Vector2f textureOffset, VertexFormat vertexFormat) {
      this.renderGroupImpl(group, color, textureOffset, vertexFormat);
   }

   public void renderGroups(String groupsName) {
      if (this.fireEvent((new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUPS)).setData(groupsName))) {
         this.renderGroupsImpl(groupsName);
      }

      this.fireEvent((new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUPS)).setData(groupsName));
   }

   public void render() {
      if (this.fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_ALL))) {
         this.renderImpl();
      }

      this.fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_ALL));
   }

   protected abstract void renderGroupsImpl(String var1);

   protected abstract void renderGroupImpl(ObjObject var1, Vector4f var2, Vector2f var3, VertexFormat var4);

   protected abstract void renderImpl();

   public abstract boolean fireEvent(ObjEvent var1);
}
