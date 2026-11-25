package me.ichun.mods.ichunutil.common.core.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHelper {
   public static final EaglercraftUUID uuidExample = EaglercraftUUID.fromString("DEADBEEF-DEAD-BEEF-DEAD-DEADBEEFD00D");
   public static HashMap<String, GameProfile> gameProfileCache = new HashMap();
   public static PlayerProfileCache profileCache;
   public static MinecraftSessionService sessionService;
   private static GameProfile dummyProfile = new GameProfile(uuidExample, "ForgeDev");
   public static final Map<EaglercraftUUID, BossInfoClient> BOSS_INFO_STORE = Maps.newLinkedHashMap();

   public EntityHelper() {
   }

   @SideOnly(Side.CLIENT)
   public static void injectMinecraftPlayerGameProfile() {
      gameProfileCache.put(Minecraft.getMinecraft().getSession().getUsername(), Minecraft.getMinecraft().getSession().getProfile());
   }

   public static GameProfile getDummyGameProfile() {
      return dummyProfile;
   }

   public static GameProfile getGameProfile(@Nonnull EaglercraftUUID uuid, @Nullable String playerName) {
      if (playerName != null && gameProfileCache.containsKey(playerName)) {
         return (GameProfile)gameProfileCache.get(playerName);
      } else {
         if (profileCache == null || sessionService == null) {
            iChunUtil.proxy.setGameProfileLookupService();
         }

         GameProfile gameprofile = profileCache.getProfileByUUID(uuid);
         if (gameprofile == null) {
            gameprofile = sessionService.fillProfileProperties(new GameProfile(uuid, (String)null), true);
         }

         Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);
         if (property == null) {
            gameprofile = sessionService.fillProfileProperties(gameprofile, true);
            gameProfileCache.put(gameprofile.getName(), gameprofile);
         }

         return gameprofile;
      }
   }

   public static GameProfile getGameProfile(String playerName) {
      if (gameProfileCache.containsKey(playerName)) {
         return (GameProfile)gameProfileCache.get(playerName);
      } else {
         if (profileCache == null || sessionService == null) {
            iChunUtil.proxy.setGameProfileLookupService();
         }

         GameProfile gameprofile = profileCache.getGameProfileForUsername(playerName);
         if (gameprofile == null) {
            return new GameProfile((EaglercraftUUID)null, playerName);
         } else {
            Property property = (Property)Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);
            if (property == null) {
               gameprofile = sessionService.fillProfileProperties(gameprofile, true);
               gameProfileCache.put(playerName, gameprofile);
            }

            return gameprofile;
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public static void storeBossStatus() {
      BOSS_INFO_STORE.clear();
      Iterator<Entry<EaglercraftUUID, BossInfoClient>> ite = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos.entrySet().iterator();

      while(ite.hasNext()) {
         Entry<EaglercraftUUID, BossInfoClient> e = (Entry)ite.next();
         BOSS_INFO_STORE.put(e.getKey(), e.getValue());
         ite.remove();
      }

   }

   @SideOnly(Side.CLIENT)
   public static void restoreBossStatus() {
      Map<EaglercraftUUID, BossInfoClient> bossMap = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos;
      Iterator<Entry<EaglercraftUUID, BossInfoClient>> ite = BOSS_INFO_STORE.entrySet().iterator();

      while(ite.hasNext()) {
         Entry<EaglercraftUUID, BossInfoClient> e = (Entry)ite.next();
         bossMap.put(e.getKey(), e.getValue());
         ite.remove();
      }

      BOSS_INFO_STORE.clear();
   }

   public static int countInInventory(IInventory inv, Item item, int damage) {
      int totalCount = 0;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack is = inv.getStackInSlot(i);
         if (!is.isEmpty() && is.getItem() == item && (damage <= -1 || is.getMetadata() == damage)) {
            totalCount += is.getCount();
         }
      }

      return totalCount;
   }

   public static boolean consumeInventoryItem(IInventory inventory, Item item) {
      return consumeInventoryItem(inventory, item, -1, 1);
   }

   public static boolean consumeInventoryItem(IInventory inventory, Item itemIn, int metadataIn, int removeCount) {
      if (removeCount > 0 && countInInventory(inventory, itemIn, metadataIn) < removeCount) {
         return false;
      } else {
         int i = 0;

         for(int j = inventory.getSizeInventory() - 1; j >= 0; --j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty() && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn)) {
               int k = removeCount <= 0 ? itemstack.getCount() : Math.min(removeCount - i, itemstack.getCount());
               i += k;
               if (removeCount != 0) {
                  itemstack.shrink(k);
                  if (itemstack.isEmpty()) {
                     inventory.setInventorySlotContents(j, ItemStack.EMPTY);
                  }

                  if (removeCount > 0 && i >= removeCount) {
                     return true;
                  }
               }
            }
         }

         return true;
      }
   }

   public static void playSoundAtEntity(Entity ent, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
      ent.getEntityWorld().playSound(ent.getEntityWorld().isRemote ? iChunUtil.proxy.getMcPlayer() : null, ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ, soundEvent, soundCategory, volume, pitch);
   }

   public static <T extends EntityLivingBase> SoundEvent getHurtSound(T ent, Class clz, DamageSource source) {
      try {
         Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? "getHurtSound" : "getHurtSound", DamageSource.class);
         m.setAccessible(true);
         return (SoundEvent)m.invoke(ent, source);
      } catch (NoSuchMethodException var4) {
         if (clz != EntityLivingBase.class) {
            return getHurtSound(ent, clz.getSuperclass(), source);
         }
      } catch (Throwable var5) {
         var5.printStackTrace();
      }

      return SoundEvents.ENTITY_GENERIC_HURT;
   }

   public static <T extends EntityLivingBase> SoundEvent getDeathSound(T ent, Class clz) {
      try {
         Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? "func_188593_c" : "getDeathSound");
         m.setAccessible(true);
         return (SoundEvent)m.invoke(ent);
      } catch (NoSuchMethodException var3) {
         if (clz != EntityLivingBase.class) {
            return getDeathSound(ent, clz.getSuperclass());
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

      return SoundEvents.ENTITY_GENERIC_DEATH;
   }

   public static void setSize(Class clz, Entity ent, float width, float height) {
      try {
         Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? "setSize" : "setSize", Float.TYPE, Float.TYPE);
         m.setAccessible(true);
         m.invoke(ent, width, height);
      } catch (NoSuchMethodException var5) {
         if (clz != Entity.class) {
            setSize(clz.getSuperclass(), ent, width, height);
         }
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

   }

   public static float updateRotation(float oriRot, float intendedRot, float maxChange) {
      float var4 = MathHelper.wrapDegrees(intendedRot - oriRot);
      if (var4 > maxChange) {
         var4 = maxChange;
      }

      if (var4 < -maxChange) {
         var4 = -maxChange;
      }

      return oriRot + var4;
   }

   public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch) {
      faceLocation(facer, faced.posX, faced instanceof EntityLivingBase ? faced.posY + (double)faced.getEyeHeight() : (faced.getEntityBoundingBox().minY + faced.getEntityBoundingBox().maxY) / 2.0D, faced.posZ, maxYaw, maxPitch);
   }

   public static void faceLocation(Entity facer, double posX, double posY, double posZ, float maxYaw, float maxPitch) {
      double d0 = posX - facer.posX;
      double d1 = posZ - facer.posZ;
      double d2 = posY - (facer.posY + (double)facer.getEyeHeight());
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1);
      float f2 = (float)(Math.atan2(d1, d0) * 180.0D / 3.141592653589793D) - 90.0F;
      float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / 3.141592653589793D));
      facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
      facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
   }

   public static Vec3d getEntityPositionEyes(Entity ent, float partialTicks) {
      if (partialTicks == 1.0F) {
         return new Vec3d(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ);
      } else {
         double d0 = ent.prevPosX + (ent.posX - ent.prevPosX) * (double)partialTicks;
         double d1 = ent.prevPosY + (ent.posY - ent.prevPosY) * (double)partialTicks + (double)ent.getEyeHeight();
         double d2 = ent.prevPosZ + (ent.posZ - ent.prevPosZ) * (double)partialTicks;
         return new Vec3d(d0, d1, d2);
      }
   }

   public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick) {
      float f3;
      for(f3 = nextRotation - prevRotation; f3 < -180.0F; f3 += 360.0F) {
         ;
      }

      while(f3 >= 180.0F) {
         f3 -= 360.0F;
      }

      return prevRotation + partialTick * f3;
   }

   public static float interpolateValues(float prevVal, float nextVal, float partialTick) {
      return prevVal + partialTick * (nextVal - prevVal);
   }

   public static void setVelocity(Entity entity, double d, double d1, double d2) {
      entity.motionX = d;
      entity.motionY = d1;
      entity.motionZ = d2;
   }

   public static RayTraceResult getEntityLook(Entity ent, double d) {
      return getEntityLook(ent, d, false);
   }

   public static RayTraceResult getEntityLook(Entity ent, double d, boolean ignoreEntities) {
      return getEntityLook(ent, d, ignoreEntities, false, true, 1.0F);
   }

   public static RayTraceResult getEntityLook(Entity ent, double d, boolean ignoreEntities, boolean ignoreTransparentBlocks, boolean ignoreLiquid, float renderTick) {
      Vec3d vec3 = getEntityPositionEyes(ent, renderTick);
      Vec3d vec31 = ent.getLook(renderTick);
      Vec3d vec32 = vec3.addVector(vec31.x * d, vec31.y * d, vec31.z * d);
      RayTraceResult rayTrace = rayTraceBlocks(ent.getEntityWorld(), d, vec3, vec32, !ignoreLiquid, ignoreTransparentBlocks, false, true);
      if (!ignoreEntities) {
         double dist = d;
         if (rayTrace != null) {
            dist = rayTrace.hitVec.distanceTo(vec3);
         }

         Entity entityTrace = null;
         Vec3d vec33 = null;
         float f = 1.0F;
         List<Entity> list = ent.getEntityWorld().getEntitiesInAABBexcluding(ent, ent.getEntityBoundingBox().expand(vec31.x * dist, vec31.y * dist, vec31.z * dist).grow((double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
            public boolean apply(Entity p_apply_1_) {
               return p_apply_1_.canBeCollidedWith();
            }
         }));
         double d2 = dist;

         for(int j = 0; j < list.size(); ++j) {
            Entity entity1 = (Entity)list.get(j);
            float f1 = entity1.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)f1);
            RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            if (axisalignedbb.contains(vec3)) {
               if (d2 >= 0.0D) {
                  entityTrace = entity1;
                  vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                  d2 = 0.0D;
               }
            } else if (movingobjectposition != null) {
               double d3 = vec3.distanceTo(movingobjectposition.hitVec);
               if (d3 < d2 || d2 == 0.0D) {
                  if (entity1 == ent.getRidingEntity() && !ent.canRiderInteract()) {
                     if (d2 == 0.0D) {
                        entityTrace = entity1;
                        vec33 = movingobjectposition.hitVec;
                     }
                  } else {
                     entityTrace = entity1;
                     vec33 = movingobjectposition.hitVec;
                     d2 = d3;
                  }
               }
            }
         }

         if (entityTrace != null && (d2 < dist || rayTrace == null)) {
            rayTrace = new RayTraceResult(entityTrace, vec33);
         }
      }

      return rayTrace;
   }

   public static RayTraceResult rayTraceBlocks(World world, double dist, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreTransparentBlocks, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
         if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
            int i = MathHelper.floor(vec32.x);
            int j = MathHelper.floor(vec32.y);
            int k = MathHelper.floor(vec32.z);
            int l = MathHelper.floor(vec31.x);
            int i1 = MathHelper.floor(vec31.y);
            int j1 = MathHelper.floor(vec31.z);
            BlockPos blockpos = new BlockPos(l, i1, j1);
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid) && (!ignoreTransparentBlocks || !isTransparent(block, iblockstate, world, blockpos))) {
               RayTraceResult mop = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);
               if (mop != null) {
                  return mop;
               }
            }

            RayTraceResult movingobjectposition2 = null;
            int k1 = (int)Math.ceil(dist + 1.0D) * 2;

            while(k1-- >= 0) {
               if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                  return null;
               }

               if (l == i && i1 == j && j1 == k) {
                  return returnLastUncollidableBlock ? movingobjectposition2 : null;
               }

               boolean flag2 = true;
               boolean flag = true;
               boolean flag1 = true;
               double d0 = 999.0D;
               double d1 = 999.0D;
               double d2 = 999.0D;
               if (i > l) {
                  d0 = (double)l + 1.0D;
               } else if (i < l) {
                  d0 = (double)l + 0.0D;
               } else {
                  flag2 = false;
               }

               if (j > i1) {
                  d1 = (double)i1 + 1.0D;
               } else if (j < i1) {
                  d1 = (double)i1 + 0.0D;
               } else {
                  flag = false;
               }

               if (k > j1) {
                  d2 = (double)j1 + 1.0D;
               } else if (k < j1) {
                  d2 = (double)j1 + 0.0D;
               } else {
                  flag1 = false;
               }

               double d3 = 999.0D;
               double d4 = 999.0D;
               double d5 = 999.0D;
               double d6 = vec32.x - vec31.x;
               double d7 = vec32.y - vec31.y;
               double d8 = vec32.z - vec31.z;
               if (flag2) {
                  d3 = (d0 - vec31.x) / d6;
               }

               if (flag) {
                  d4 = (d1 - vec31.y) / d7;
               }

               if (flag1) {
                  d5 = (d2 - vec31.z) / d8;
               }

               if (d3 == -0.0D) {
                  d3 = -1.0E-4D;
               }

               if (d4 == -0.0D) {
                  d4 = -1.0E-4D;
               }

               if (d5 == -0.0D) {
                  d5 = -1.0E-4D;
               }

               EnumFacing enumfacing;
               if (d3 < d4 && d3 < d5) {
                  enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                  vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
               } else if (d4 < d5) {
                  enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                  vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
               } else {
                  enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
               }

               l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
               i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
               j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
               blockpos = new BlockPos(l, i1, j1);
               IBlockState iblockstate1 = world.getBlockState(blockpos);
               Block block1 = iblockstate1.getBlock();
               if ((!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && (!ignoreTransparentBlocks || !isTransparent(block1, iblockstate1, world, blockpos))) {
                  if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                     RayTraceResult movingobjectposition1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);
                     if (movingobjectposition1 != null) {
                        return movingobjectposition1;
                     }
                  } else {
                     movingobjectposition2 = new RayTraceResult(Type.MISS, vec31, enumfacing, blockpos);
                  }
               }
            }

            return returnLastUncollidableBlock ? movingobjectposition2 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void putEntityWithinAABB(Entity ent, AxisAlignedBB aabb) {
      if (ent.getEntityBoundingBox().maxX > aabb.maxX) {
         ent.posX += aabb.maxX - ent.getEntityBoundingBox().maxX;
      }

      if (ent.getEntityBoundingBox().minX < aabb.minX) {
         ent.posX += aabb.minX - ent.getEntityBoundingBox().minX;
      }

      if (ent.posY + (double)ent.getEyeHeight() > aabb.maxY) {
         ent.posY += aabb.maxY - ent.posY - (double)ent.getEyeHeight();
      }

      if (ent.posY < aabb.minY) {
         ent.posY += aabb.minY - ent.posY + 0.001D;
      }

      if (ent.getEntityBoundingBox().maxZ > aabb.maxZ) {
         ent.posZ += aabb.maxZ - ent.getEntityBoundingBox().maxZ;
      }

      if (ent.getEntityBoundingBox().minZ < aabb.minZ) {
         ent.posZ += aabb.minZ - ent.getEntityBoundingBox().minZ;
      }

   }

   public static double[] simulateMoveEntity(Entity ent, double x, double y, double z) {
      if (ent.noClip) {
         return new double[]{x, y, z};
      } else {
         boolean flag = ent.onGround && ent.isSneaking() && ent instanceof EntityPlayer;
         if (flag) {
            double d6 = 0.05D;

            while(x != 0.0D && ent.getEntityWorld().getCollisionBoxes(ent, ent.getEntityBoundingBox().offset(x, -1.0D, 0.0D)).isEmpty()) {
               if (x < 0.05D && x >= -0.05D) {
                  x = 0.0D;
               } else if (x > 0.0D) {
                  x -= 0.05D;
               } else {
                  x += 0.05D;
               }
            }

            while(z != 0.0D && ent.getEntityWorld().getCollisionBoxes(ent, ent.getEntityBoundingBox().offset(0.0D, -1.0D, z)).isEmpty()) {
               if (z < 0.05D && z >= -0.05D) {
                  z = 0.0D;
               } else if (z > 0.0D) {
                  z -= 0.05D;
               } else {
                  z += 0.05D;
               }
            }

            while(x != 0.0D && z != 0.0D && ent.getEntityWorld().getCollisionBoxes(ent, ent.getEntityBoundingBox().offset(x, -1.0D, z)).isEmpty()) {
               if (x < 0.05D && x >= -0.05D) {
                  x = 0.0D;
               } else if (x > 0.0D) {
                  x -= 0.05D;
               } else {
                  x += 0.05D;
               }

               if (z < 0.05D && z >= -0.05D) {
                  z = 0.0D;
               } else if (z > 0.0D) {
                  z -= 0.05D;
               } else {
                  z += 0.05D;
               }
            }
         }

         List<AxisAlignedBB> list1 = ent.getEntityWorld().getCollisionBoxes(ent, ent.getEntityBoundingBox().expand(x, y, z));
         AxisAlignedBB axisalignedbb = ent.getEntityBoundingBox();
         int i = 0;

         for(int j = list1.size(); i < j; ++i) {
            y = ((AxisAlignedBB)list1.get(i)).calculateYOffset(ent.getEntityBoundingBox(), y);
         }

         ent.setEntityBoundingBox(ent.getEntityBoundingBox().offset(0.0D, y, 0.0D));
         int j4 = 0;

         for(int k = list1.size(); j4 < k; ++j4) {
            x = ((AxisAlignedBB)list1.get(j4)).calculateXOffset(ent.getEntityBoundingBox(), x);
         }

         ent.setEntityBoundingBox(ent.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
         j4 = 0;

         for(int k4 = list1.size(); j4 < k4; ++j4) {
            z = ((AxisAlignedBB)list1.get(j4)).calculateZOffset(ent.getEntityBoundingBox(), z);
         }

         ent.setEntityBoundingBox(axisalignedbb);
         return new double[]{x, y, z};
      }
   }

   public static void addPosition(Entity living, double offset, boolean subtract, int axis) {
      if (subtract) {
         offset = -offset;
      }

      if (axis == 0) {
         living.lastTickPosX += offset;
         living.prevPosX += offset;
         living.posX += offset;
      } else if (axis == 1) {
         living.lastTickPosY += offset;
         living.prevPosY += offset;
         living.posY += offset;
      } else if (axis == 2) {
         living.lastTickPosZ += offset;
         living.prevPosZ += offset;
         living.posZ += offset;
      }

   }

   public static void relocateEntity(Axis axis, Entity ent, double degree, double originX, double originY, double originZ, double destX, double destY, double destZ, float renderTick) {
      double rads = Math.toRadians(degree);
      if (axis == Axis.Y) {
         iChunUtil.proxy.adjustRotation(ent, (float)(-degree), 0.0F);
      }

   }

   public static AxisAlignedBB rotateAABB(Axis axis, AxisAlignedBB aabb, double degree, double originX, double originY, double originZ) {
      double rads = Math.toRadians(degree);
      if (axis == Axis.X) {
         double oriZ = aabb.minZ - originZ;
         double oriY = aabb.minY - originY;
         double z1 = oriZ * Math.cos(rads) + oriY * Math.sin(rads);
         double y1 = -oriZ * Math.sin(rads) + oriY * Math.cos(rads);
         oriZ = aabb.maxZ - originZ;
         oriY = aabb.maxY - originY;
         double z2 = oriZ * Math.cos(rads) + oriY * Math.sin(rads);
         double y2 = -oriZ * Math.sin(rads) + oriY * Math.cos(rads);
         return new AxisAlignedBB(aabb.minX, Math.min(y1, y2) + originY, Math.min(z1, z2) + originZ, aabb.maxX, Math.max(y1, y2) + originY, Math.max(z1, z2) + originZ);
      } else if (axis == Axis.Y) {
         double oriX = aabb.minX - originX;
         double oriZ = aabb.minZ - originZ;
         double x1 = oriX * Math.cos(rads) + oriZ * Math.sin(rads);
         double z1 = -oriX * Math.sin(rads) + oriZ * Math.cos(rads);
         oriX = aabb.maxX - originX;
         oriZ = aabb.maxZ - originZ;
         double x2 = oriX * Math.cos(rads) + oriZ * Math.sin(rads);
         double z2 = -oriX * Math.sin(rads) + oriZ * Math.cos(rads);
         return new AxisAlignedBB(Math.min(x1, x2) + originX, aabb.minY, Math.min(z1, z2) + originZ, Math.max(x1, x2) + originX, aabb.maxY, Math.max(z1, z2) + originZ);
      } else if (axis == Axis.Z) {
         double oriX = aabb.minX - originX;
         double oriY = aabb.minY - originY;
         double x1 = oriX * Math.cos(rads) + oriY * Math.sin(rads);
         double y1 = -oriX * Math.sin(rads) + oriY * Math.cos(rads);
         oriX = aabb.maxX - originX;
         oriY = aabb.maxY - originY;
         double x2 = oriX * Math.cos(rads) + oriY * Math.sin(rads);
         double y2 = -oriX * Math.sin(rads) + oriY * Math.cos(rads);
         return new AxisAlignedBB(Math.min(x1, x2) + originX, Math.min(y1, y2) + originY, aabb.minZ, Math.max(x1, x2) + originX, Math.max(y1, y2) + originY, aabb.maxZ);
      } else {
         return aabb;
      }
   }

   public static boolean destroyBlocksInAABB(Entity ent, AxisAlignedBB aabb) {
      int i = MathHelper.floor(aabb.minX);
      int j = MathHelper.floor(aabb.minY);
      int k = MathHelper.floor(aabb.minZ);
      int l = MathHelper.floor(aabb.maxX);
      int i1 = MathHelper.floor(aabb.maxY);
      int j1 = MathHelper.floor(aabb.maxZ);
      boolean flag = false;
      boolean flag1 = false;

      for(int k1 = i; k1 <= l; ++k1) {
         for(int l1 = j; l1 <= i1; ++l1) {
            for(int i2 = k; i2 <= j1; ++i2) {
               BlockPos blockpos = new BlockPos(k1, l1, i2);
               IBlockState iblockstate = ent.getEntityWorld().getBlockState(blockpos);
               Block block = ent.getEntityWorld().getBlockState(blockpos).getBlock();
               if (!block.isAir(iblockstate, ent.getEntityWorld(), blockpos) && iblockstate.getMaterial() != Material.FIRE) {
                  if (!ent.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
                     flag = true;
                  } else if (!block.canEntityDestroy(iblockstate, ent.getEntityWorld(), blockpos, ent)) {
                     flag = true;
                  } else if (block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK && block != Blocks.IRON_BARS && block != Blocks.END_GATEWAY) {
                     flag1 = ent.getEntityWorld().setBlockToAir(blockpos) || flag1;
                  } else {
                     flag = true;
                  }
               }
            }
         }
      }

      if (flag1) {
         double d0 = aabb.minX + (aabb.maxX - aabb.minX) * (double)ent.getEntityWorld().rand.nextFloat();
         double d1 = aabb.minY + (aabb.maxY - aabb.minY) * (double)ent.getEntityWorld().rand.nextFloat();
         double d2 = aabb.minZ + (aabb.maxZ - aabb.minZ) * (double)ent.getEntityWorld().rand.nextFloat();
         ent.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      return flag;
   }

   public static NBTTagCompound getPlayerPersistentData(EntityPlayer player) {
      NBTTagCompound persistentTag = player.getEntityData().getCompoundTag("PlayerPersisted");
      player.getEntityData().setTag("PlayerPersisted", persistentTag);
      return persistentTag;
   }

   public static NBTTagCompound getPlayerPersistentData(EntityPlayer player, String name) {
      NBTTagCompound persistentTag = getPlayerPersistentData(player).getCompoundTag(name);
      getPlayerPersistentData(player).setTag(name, persistentTag);
      return persistentTag;
   }

   public static boolean isTransparent(Block block, IBlockState state, World world, BlockPos pos) {
      return block.getLightOpacity(state, world, pos) != 255;
   }
}
