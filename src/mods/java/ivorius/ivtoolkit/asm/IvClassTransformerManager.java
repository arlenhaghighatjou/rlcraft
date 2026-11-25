package ivorius.ivtoolkit.asm;

import java.util.ArrayList;
import java.util.Hashtable;
import net.minecraft.launchwrapper.IClassTransformer;

public class IvClassTransformerManager implements IClassTransformer {
   public Hashtable<String, IvClassTransformer> transformers = new Hashtable();
   public ArrayList<IvClassTransformer> generalTransformers = new ArrayList();

   public IvClassTransformerManager() {
      IvDevRemapper.setUp();
   }

   public void registerTransformer(String clazz, IvClassTransformer transformer) {
      this.transformers.put(clazz, transformer);
   }

   public void registerTransformer(IvClassTransformer transformer) {
      this.generalTransformers.add(transformer);
   }

   public byte[] transform(String arg0, String arg1, byte[] arg2) {
      if (arg2 != null) {
         byte[] result = arg2;
         IvClassTransformer transformer = (IvClassTransformer)this.transformers.get(arg1);
         if (transformer != null) {
            byte[] data = transformer.transform(arg0, arg1, arg2, arg0.equals(arg1));
            if (data != null) {
               result = data;
            }
         }

         for(IvClassTransformer generalTransformer : this.generalTransformers) {
            byte[] data = generalTransformer.transform(arg0, arg1, result, arg0.equals(arg1));
            if (data != null) {
               result = data;
            }
         }

         return result;
      } else {
         return arg2;
      }
   }
}
