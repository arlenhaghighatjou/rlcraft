package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigBase;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import org.apache.commons.io.FilenameUtils;

public class Utilities {
   protected static Calendar easterCalendar;

   public Utilities() {
   }

   public static void addDungeonLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
      ConfigBase config = LycanitesMobs.config;
      config.setCategoryComment("Dungeon Loot Enabled", "Here you can enable/disable dungeon loot for various types of dungeons.");
      if (config.getBool("Dungeon Loot Enabled", "Dungeons", true, "These are most dungeons from underground mob spawner dungeons to pyramids, mineshafts and jungle temples.")) {
         ;
      }

   }

   public static void addStrongholdLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
      ConfigBase config = LycanitesMobs.config;
      if (config.getBool("Dungeon Loot Enabled", "Strongholds", true, "Stronghold dungeons including corridors, libraries and other parts.")) {
         ;
      }

   }

   public static void addVillageLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
      ConfigBase config = LycanitesMobs.config;
      if (config.getBool("Dungeon Loot Enabled", "Blacksmiths", true, "These are the chests found in village blacksmiths homes.")) {
         ;
      }

   }

   public static RayTraceResult raytrace(World world, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, float rayWidth, @Nullable Predicate<Entity> entityPredicate) {
      RayTraceResult blockResult = raytraceBlocks(world, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox);
      RayTraceResult entityResult = raytraceEntities(world, start, end, rayWidth, entityPredicate);
      if (blockResult != null && blockResult.typeOfHit != Type.MISS) {
         if (entityResult != null && entityResult.typeOfHit != Type.MISS) {
            return start.squareDistanceTo(blockResult.hitVec) <= start.squareDistanceTo(entityResult.hitVec) ? blockResult : entityResult;
         } else {
            return blockResult;
         }
      } else {
         return entityResult != null && entityResult.typeOfHit != Type.MISS ? entityResult : new RayTraceResult(Type.MISS, end, (EnumFacing)null, new BlockPos(end));
      }
   }

   public static RayTraceResult raytraceBlocks(World world, Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
      RayTraceResult result = world.rayTraceBlocks(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, false);
      return result != null && result.typeOfHit != Type.MISS ? result : new RayTraceResult(Type.MISS, end, (EnumFacing)null, new BlockPos(end));
   }

   public static <T extends Entity> RayTraceResult raytraceEntities(World world, Vec3d start, Vec3d end, float rayWidth, @Nullable Predicate<Entity> predicate) {
      AxisAlignedBB aabb = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
      if (rayWidth != 0.0F) {
         aabb = aabb.grow((double)rayWidth);
      }

      List<Entity> possibleEntities = world.getEntitiesWithinAABB(Entity.class, aabb, predicate != null ? predicate::test : null);
      Entity closestHitEntity = null;
      Vec3d closestHitLocation = null;
      double closestHitDistSqr = Double.POSITIVE_INFINITY;

      for(Entity entity : possibleEntities) {
         AxisAlignedBB entityAabb = entity.getEntityBoundingBox();
         if (rayWidth + entity.getCollisionBorderSize() != 0.0F) {
            entityAabb = entityAabb.grow((double)(rayWidth + entity.getCollisionBorderSize()));
         }

         RayTraceResult currentHit = entityAabb.calculateIntercept(start, end);
         if (currentHit != null) {
            double currentHitDistSqr = start.squareDistanceTo(currentHit.hitVec);
            if (currentHitDistSqr < closestHitDistSqr) {
               closestHitEntity = entity;
               closestHitLocation = currentHit.hitVec;
               closestHitDistSqr = currentHitDistSqr;
            }
         }
      }

      if (closestHitEntity != null && closestHitLocation != null) {
         return new RayTraceResult(closestHitEntity, closestHitLocation);
      } else {
         return new RayTraceResult(Type.MISS, end, (EnumFacing)null, new BlockPos(end));
      }
   }

   public static Predicate<Entity> collidable() {
      return (entity) -> {
         return EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith();
      };
   }

   public static Predicate<Entity> collidableExlcuding(Entity entityToIgnore) {
      return entityToIgnore == null ? collidable() : (entity) -> {
         return EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith() && entity != entityToIgnore;
      };
   }

   public static Predicate<Entity> collidableExlcuding(Entity entityToIgnore1, Entity entityToIgnore2) {
      if (entityToIgnore1 == null) {
         return collidableExlcuding(entityToIgnore2);
      } else {
         return entityToIgnore2 == null ? collidableExlcuding(entityToIgnore1) : (entity) -> {
            return EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith() && entity != entityToIgnore1 && entity != entityToIgnore2;
         };
      }
   }

   public static Predicate<Entity> collidableExlcuding(Entity entityToIgnore1, Entity entityToIgnore2, Entity entityToIgnore3) {
      if (entityToIgnore1 == null) {
         return collidableExlcuding(entityToIgnore2, entityToIgnore3);
      } else if (entityToIgnore2 == null) {
         return collidableExlcuding(entityToIgnore1, entityToIgnore3);
      } else {
         return entityToIgnore3 == null ? collidableExlcuding(entityToIgnore1, entityToIgnore2) : (entity) -> {
            return EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith() && entity != entityToIgnore1 && entity != entityToIgnore2 && entity != entityToIgnore3;
         };
      }
   }

   public static Predicate<Entity> collidableExlcuding(Entity... entities) {
      if (entities.length >= 0 && entities.length < 4) {
         if (entities.length == 0) {
            return collidable();
         }

         if (entities.length == 1) {
            return collidableExlcuding(entities[0]);
         }

         if (entities.length == 2) {
            return collidableExlcuding(entities[0], entities[1]);
         }

         if (entities.length == 3) {
            return collidableExlcuding(entities[0], entities[1], entities[2]);
         }
      }

      Set<Entity> entitySet = (Set)Arrays.stream(entities).filter(Objects::nonNull).collect(Collectors.toSet());
      return (entity) -> {
         return EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith() && !entitySet.contains(entity);
      };
   }

   public static boolean isValentines() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) == 1 && calendar.get(5) >= 7 && calendar.get(5) <= 14;
   }

   public static boolean isEaster() {
      Calendar calendar = Calendar.getInstance();
      if (easterCalendar == null) {
         int Y = calendar.get(1);
         int a = Y % 19;
         int b = Y / 100;
         int c = Y % 100;
         int d = b / 4;
         int e = b % 4;
         int f = (b + 8) / 25;
         int g = (b - f + 1) / 3;
         int h = (19 * a + b - d - g + 15) % 30;
         int i = c / 4;
         int k = c % 4;
         int L = (32 + 2 * e + 2 * i - h - k) % 7;
         int m = (a + 11 * h + 22 * L) / 451;
         int easterMonth = (h + L - 7 * m + 114) / 31;
         int easterDay = (h + L - 7 * m + 114) % 31 + 1;
         easterCalendar = new GregorianCalendar(Y, easterMonth, easterDay);
      }

      long daysUntilEaster = ChronoUnit.DAYS.between(calendar.toInstant(), easterCalendar.toInstant());
      return daysUntilEaster <= 7L && daysUntilEaster >= 0L;
   }

   public static boolean isMidsummer() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) == 6 && calendar.get(5) >= 10 && calendar.get(5) <= 20;
   }

   public static boolean isHalloween() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(5) >= 25 && calendar.get(2) == 9 || calendar.get(5) == 1 && calendar.get(2) == 10;
   }

   public static boolean isYuletide() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) == 11 && calendar.get(5) >= 10 && calendar.get(5) <= 25;
   }

   public static boolean isYuletidePeak() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) == 11 && calendar.get(5) == 25;
   }

   public static boolean isNewYear() {
      Calendar calendar = Calendar.getInstance();
      return calendar.get(2) == 0 && calendar.get(5) == 1;
   }

   public static int daysBetween(Date d1, Date d2) {
      return (int)((d2.getTime() - d1.getTime()) / 86400000L);
   }

   public static Path getAssetPath(Class clazz, String assetDomain, String assetPath) {
      Path path = null;
      String assetDir = "/assets/" + assetDomain + (!"".equals(assetPath) ? "/" + assetPath : "");

      try {
         URL url = clazz.getResource("/assets/" + assetDomain + "/.root");
         URI uri = url.toURI();
         if ("file".equals(uri.getScheme())) {
            path = Paths.get(clazz.getResource(assetDir).toURI());
         } else {
            if (!"jar".equals(uri.getScheme())) {
               LycanitesMobs.logWarning("", "Unsupported file scheme: " + uri.getScheme());
               return null;
            }

            FileSystem filesystem;
            try {
               filesystem = FileSystems.getFileSystem(uri);
            } catch (Exception var9) {
               filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            }

            path = filesystem.getPath(assetDir);
         }
      } catch (Exception var10) {
         LycanitesMobs.logWarning("", "No data found in: " + assetDir);
      }

      return path;
   }

   public static List<ResourceLocation> getPathResourceLocations(Path path, String assetDomain, String fileType) {
      List<ResourceLocation> resourceLocations = new ArrayList();

      try {
         Iterator<Path> iterator = Files.walk(path).iterator();

         while(iterator.hasNext()) {
            Path filePath = (Path)iterator.next();
            if (fileType == null || fileType.equals(FilenameUtils.getExtension(filePath.toString()))) {
               Path relativePath = path.relativize(filePath);
               String resourceLocationPath = FilenameUtils.removeExtension(relativePath.toString()).replaceAll("\\\\", "/");
               ResourceLocation resourceLocation = new ResourceLocation(assetDomain, resourceLocationPath);
               resourceLocations.add(resourceLocation);
            }
         }
      } catch (Exception var9) {
         LycanitesMobs.logWarning("", "There was a problem getting ResourceLocations for: " + path + ", " + fileType + ",  \n" + var9.toString());
      }

      return resourceLocations;
   }
}
