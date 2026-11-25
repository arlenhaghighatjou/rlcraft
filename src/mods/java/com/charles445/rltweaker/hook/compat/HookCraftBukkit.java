package com.charles445.rltweaker.hook.compat;

import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class HookCraftBukkit {
   public HookCraftBukkit() {
   }

   public static class Charm {
      @Nullable
      private static Field f_TileEntityBeacon_primaryEffect = ReflectUtil.findFieldAnyOrNull(TileEntityBeacon.class, "primaryEffect", "primaryEffect");
      @Nullable
      private static Field f_TileEntityBeacon_secondaryEffect = ReflectUtil.findFieldAnyOrNull(TileEntityBeacon.class, "secondaryEffect", "secondaryEffect");
      @Nullable
      private static Class c_ASMHooks = ReflectUtil.findClassOrNull("svenhjol.charm.base.ASMHooks");
      @Nullable
      private static Method m_ASMHooks_addBeaconEffect = ReflectUtil.findMethodOrNull(c_ASMHooks, "addBeaconEffect");

      public Charm() {
      }

      public static void handleAnimalBeacon(TileEntityBeacon beacon) {
         if (f_TileEntityBeacon_primaryEffect != null && f_TileEntityBeacon_secondaryEffect != null && c_ASMHooks != null && m_ASMHooks_addBeaconEffect != null) {
            World world = beacon.getWorld();
            int x = beacon.getPos().getX();
            int y = beacon.getPos().getY();
            int z = beacon.getPos().getZ();
            int levels = beacon.getLevels();
            AxisAlignedBB aabb = (new AxisAlignedBB((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1))).grow((double)levels).expand(0.0D, (double)world.getHeight(), 0.0D);

            try {
               Potion primaryEffect = (Potion)f_TileEntityBeacon_primaryEffect.get(beacon);
               Potion secondaryEffect = (Potion)f_TileEntityBeacon_secondaryEffect.get(beacon);
               int duration = (9 + levels * 2) * 20;
               int amplifier = 0;
               if (levels >= 4 && primaryEffect == secondaryEffect) {
                  amplifier = 1;
               }

               m_ASMHooks_addBeaconEffect.invoke((Object)null, world, aabb, primaryEffect, secondaryEffect, duration, amplifier);
            } catch (Exception var11) {
               ErrorUtil.logSilent("CraftBukkit Charm TileEntityBeacon Invocation");
            }
         }
      }
   }
}
