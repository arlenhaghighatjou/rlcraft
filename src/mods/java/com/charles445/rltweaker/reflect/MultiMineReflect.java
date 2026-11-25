package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;

public class MultiMineReflect {
   public final Class c_MultiMine = Class.forName("atomicstryker.multimine.common.MultiMine");
   public final Field f_MultiMine_instance;
   public final Object o_MultiMine_instance;
   public final Field f_MultiMine_networkHelper;
   public final Object o_MultiMine_networkHelper;
   public final Class c_NetworkHelper;
   public final Field f_NetworkHelper_isCurrentlySendingSemaphor;

   public MultiMineReflect() throws Exception {
      this.f_MultiMine_instance = ReflectUtil.findField(this.c_MultiMine, "instance");
      this.o_MultiMine_instance = this.f_MultiMine_instance.get((Object)null);
      this.f_MultiMine_networkHelper = ReflectUtil.findField(this.c_MultiMine, "networkHelper");
      this.o_MultiMine_networkHelper = this.f_MultiMine_networkHelper.get(this.o_MultiMine_instance);
      this.c_NetworkHelper = Class.forName("atomicstryker.multimine.common.network.NetworkHelper");
      this.f_NetworkHelper_isCurrentlySendingSemaphor = ReflectUtil.findField(this.c_NetworkHelper, "isCurrentlySendingSemaphor");
   }

   public boolean getSemaphor() throws IllegalArgumentException, IllegalAccessException {
      return this.f_NetworkHelper_isCurrentlySendingSemaphor.getBoolean(this.o_MultiMine_networkHelper);
   }

   public void setSemaphor(boolean val) throws IllegalArgumentException, IllegalAccessException {
      this.f_NetworkHelper_isCurrentlySendingSemaphor.setBoolean(this.o_MultiMine_networkHelper, val);
   }
}
