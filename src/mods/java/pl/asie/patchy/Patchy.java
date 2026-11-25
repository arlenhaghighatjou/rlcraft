package pl.asie.patchy;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.IClassTransformer;

public class Patchy implements IClassTransformer {
   private final Map<Class, TransformerHandler> handlerMap = new HashMap();
   private final Table<String, Class, List<Object>> localTransformers = HashBasedTable.create();
   private final Map<Class, List<Object>> globalTransformers = new HashMap();
   private final List<String> activeTransformers = new ArrayList();

   public Patchy() {
   }

   public void addTransformerId(String id) {
      this.activeTransformers.add(id);
   }

   public <T> void registerHandler(Class<T> cls, TransformerHandler<T> handler) {
      this.handlerMap.put(cls, handler);
   }

   public <T> TransformerHandler<T> getHandler(Class<T> cls) {
      return (TransformerHandler)this.handlerMap.get(cls);
   }

   private byte[] transformWithMap(byte[] data, Map<Class, List<Object>> map) {
      if (map != null) {
         for(Class c : map.keySet()) {
            TransformerHandler handler = (TransformerHandler)this.handlerMap.get(c);
            data = handler.process(data, (List)map.get(c));
         }
      }

      return data;
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (basicClass == null) {
         return null;
      } else {
         basicClass = this.transformWithMap(basicClass, this.globalTransformers);
         basicClass = this.transformWithMap(basicClass, this.localTransformers.row(transformedName));
         return basicClass;
      }
   }

   <T> void registerGlobalTransformer(Class<T> type, TransformerFunction<T> function) {
      if (!this.globalTransformers.containsKey(type)) {
         this.globalTransformers.put(type, Lists.newArrayList(new Object[]{function}));
      } else {
         ((List)this.globalTransformers.get(type)).add(function);
      }

   }

   <T> void registerLocalTransformer(String s, Class<T> type, TransformerFunction<T> function) {
      if (!this.localTransformers.contains(s, type)) {
         this.localTransformers.put(s, type, Lists.newArrayList(new Object[]{function}));
      } else {
         ((List)this.localTransformers.get(s, type)).add(function);
      }

   }
}
