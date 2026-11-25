package com.creativemd.creativecore.client.rendering.model;

import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class QuadCache {
   private int v = 0;
   public VertexFormat format;
   public final ArrayList<int[]> quadDatas = new ArrayList();
   private int[] quadData;

   public QuadCache(VertexFormat format) {
      this.format = format;
   }

   public void addCache(int index, float... cache) {
      if (this.quadData == null) {
         this.quadData = new int[this.format.getNextOffset()];
      }

      LightUtil.pack(cache, this.quadData, this.format, this.v, index);
      if (index == this.format.getElementCount() - 1) {
         ++this.v;
         if (this.v == 4) {
            this.quadDatas.add(this.quadData);
            this.quadData = null;
            this.v = 0;
         }
      }

   }
}
