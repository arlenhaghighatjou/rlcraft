package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.HashSet;

public class GrapplemodReflect {
   public final Class c_grapplemod = Class.forName("com.yyon.grapplinghook.grapplemod");
   public final Field f_grapplemod_attached;
   public HashSet<Integer> o_grapplemod_attached;
   public final Class c_grappleArrow;
   public final Field f_grappleArrow_shootingEntity;

   public GrapplemodReflect() throws Exception {
      this.f_grapplemod_attached = ReflectUtil.findField(this.c_grapplemod, "attached");
      this.c_grappleArrow = Class.forName("com.yyon.grapplinghook.entities.grappleArrow");
      this.f_grappleArrow_shootingEntity = ReflectUtil.findField(this.c_grappleArrow, "shootingEntity");
   }

   public boolean getIsAttached(int id) {
      if (this.o_grapplemod_attached == null) {
         try {
            this.o_grapplemod_attached = (HashSet)this.f_grapplemod_attached.get((Object)null);
         } catch (IllegalAccessException | IllegalArgumentException var3) {
            ErrorUtil.logSilent("Grapplemod Error getIsAttached");
            return false;
         }

         if (this.o_grapplemod_attached == null) {
            return false;
         }
      }

      return this.o_grapplemod_attached.contains(Integer.valueOf(id));
   }
}
