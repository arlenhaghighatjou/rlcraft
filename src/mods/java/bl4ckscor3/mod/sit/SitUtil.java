package bl4ckscor3.mod.sit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SitUtil {
   private static final Map<Integer, Map<BlockPos, EntitySit>> OCCUPIED = new HashMap();

   public SitUtil() {
   }

   public static boolean addSitEntity(World world, BlockPos pos, EntitySit entity) {
      if (!world.isRemote) {
         int id = getDimensionTypeId(world);
         if (!OCCUPIED.containsKey(Integer.valueOf(id))) {
            OCCUPIED.put(Integer.valueOf(id), new HashMap());
         }

         ((Map)OCCUPIED.get(Integer.valueOf(id))).put(pos, entity);
         return true;
      } else {
         return false;
      }
   }

   public static boolean removeSitEntity(World world, BlockPos pos) {
      if (!world.isRemote) {
         int id = getDimensionTypeId(world);
         if (OCCUPIED.containsKey(Integer.valueOf(id))) {
            ((Map)OCCUPIED.get(Integer.valueOf(id))).remove(pos);
            return true;
         }
      }

      return false;
   }

   public static EntitySit getSitEntity(World world, BlockPos pos) {
      if (!world.isRemote) {
         int id = getDimensionTypeId(world);
         if (OCCUPIED.containsKey(Integer.valueOf(id))) {
            return (EntitySit)((Map)OCCUPIED.get(Integer.valueOf(id))).get(pos);
         }
      }

      return null;
   }

   public static boolean isOccupied(World world, BlockPos pos) {
      int id = getDimensionTypeId(world);
      return OCCUPIED.containsKey(Integer.valueOf(id)) && ((Map)OCCUPIED.get(Integer.valueOf(id))).containsKey(pos);
   }

   public static boolean isPlayerSitting(EntityPlayer player) {
      Iterator var1 = OCCUPIED.keySet().iterator();

      label21:
      while(var1.hasNext()) {
         int i = ((Integer)var1.next()).intValue();
         Iterator var3 = ((Map)OCCUPIED.get(Integer.valueOf(i))).values().iterator();

         while(true) {
            if (!var3.hasNext()) {
               continue label21;
            }

            EntitySit sit = (EntitySit)var3.next();
            if (sit.isPassenger(player)) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   private static int getDimensionTypeId(World world) {
      return world.provider.getDimension();
   }
}
