package ivorius.ivtoolkit.rendering;

import org.apache.logging.log4j.Logger;

public abstract class IvShaderInstance3D extends IvShaderInstance {
   public IvShaderInstance3D(Logger logger) {
      super(logger);
   }

   public abstract boolean activate(float var1, float var2);

   public abstract void deactivate();
}
