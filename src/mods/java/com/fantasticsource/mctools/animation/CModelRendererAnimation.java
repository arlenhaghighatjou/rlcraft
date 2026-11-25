package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;

public class CModelRendererAnimation extends Component {
   public CPath.CPathData xPath = new CPath.CPathData();
   public CPath.CPathData yPath = new CPath.CPathData();
   public CPath.CPathData zPath = new CPath.CPathData();
   public CPath.CPathData xRotPath = new CPath.CPathData();
   public CPath.CPathData yRotPath = new CPath.CPathData();
   public CPath.CPathData zRotPath = new CPath.CPathData();
   public CPath.CPathData xScalePath = new CPath.CPathData();
   public CPath.CPathData yScalePath = new CPath.CPathData();
   public CPath.CPathData zScalePath = new CPath.CPathData();

   public CModelRendererAnimation() {
   }

   public CPath.CPathData[] getAllData() {
      return new CPath.CPathData[]{this.xPath, this.yPath, this.zPath, this.xRotPath, this.yRotPath, this.zRotPath, this.xScalePath, this.yScalePath, this.zScalePath};
   }

   public CModelRendererAnimation write(ByteBuf buf) {
      for(CPath.CPathData data : this.getAllData()) {
         writeMarkedOrNull(buf, data);
      }

      return this;
   }

   public CModelRendererAnimation read(ByteBuf buf) {
      this.xPath = (CPath.CPathData)readMarkedOrNull(buf);
      this.yPath = (CPath.CPathData)readMarkedOrNull(buf);
      this.zPath = (CPath.CPathData)readMarkedOrNull(buf);
      this.xRotPath = (CPath.CPathData)readMarkedOrNull(buf);
      this.yRotPath = (CPath.CPathData)readMarkedOrNull(buf);
      this.zRotPath = (CPath.CPathData)readMarkedOrNull(buf);
      this.xScalePath = (CPath.CPathData)readMarkedOrNull(buf);
      this.yScalePath = (CPath.CPathData)readMarkedOrNull(buf);
      this.zScalePath = (CPath.CPathData)readMarkedOrNull(buf);
      return this;
   }

   public CModelRendererAnimation save(OutputStream stream) {
      for(CPath.CPathData data : this.getAllData()) {
         saveMarkedOrNull(stream, data);
      }

      return this;
   }

   public CModelRendererAnimation load(InputStream stream) {
      this.xPath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.yPath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.zPath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.xRotPath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.yRotPath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.zRotPath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.xScalePath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.yScalePath = (CPath.CPathData)loadMarkedOrNull(stream);
      this.zScalePath = (CPath.CPathData)loadMarkedOrNull(stream);
      return this;
   }
}
