package com.ferreusveritas.dynamictrees.models;

import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTrackerCacheEntityFallingTree {
   public static Map<Integer, ModelEntityFallingTree> modelMap = new ConcurrentHashMap();
   private static int cleanupCounter = 0;

   public ModelTrackerCacheEntityFallingTree() {
   }

   public static ModelEntityFallingTree getModel(EntityFallingTree entity) {
      return (ModelEntityFallingTree)modelMap.computeIfAbsent(Integer.valueOf(entity.getEntityId()), (e) -> {
         return new ModelEntityFallingTree(entity);
      });
   }

   public static void cleanupModels(World world, EntityFallingTree entity) {
      modelMap.remove(Integer.valueOf(entity.getEntityId()));
      if (++cleanupCounter >= 10) {
         cleanupCounter = 0;
         Iterator<Integer> iter = modelMap.keySet().iterator();

         while(iter.hasNext()) {
            int id = ((Integer)iter.next()).intValue();
            if (world.getEntityByID(id) == null) {
               modelMap.remove(Integer.valueOf(id));
            }
         }
      }

   }
}
