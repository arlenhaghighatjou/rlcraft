package tschipp.carryon.common.handler;

import java.lang.reflect.Method;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import tschipp.carryon.common.config.CarryOnConfig;
import tschipp.carryon.common.item.ItemTile;
import tschipp.carryon.common.scripting.CarryOnOverride;
import tschipp.carryon.common.scripting.ScriptChecker;

public class PickupHandler {
   public PickupHandler() {
   }

   public static boolean canPlayerPickUpBlock(EntityPlayer player, @Nullable TileEntity tile, World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      NBTTagCompound tag = new NBTTagCompound();
      if (tile != null) {
         tile.writeToNBT(tag);
      }

      CarryOnOverride override = ScriptChecker.inspectBlock(world.getBlockState(pos), world, pos, tag);
      if (override != null) {
         return ScriptChecker.fulfillsConditions(override, player) && handleProtections((EntityPlayerMP)player, world, pos, state);
      } else {
         if (CarryOnConfig.settings.useWhitelistBlocks) {
            if (!ListHandler.isAllowed(world.getBlockState(pos).getBlock())) {
               return false;
            }
         } else if (ListHandler.isForbidden(world.getBlockState(pos).getBlock())) {
            return false;
         }

         if (block.getBlockHardness(state, world, pos) != -1.0F || player.isCreative()) {
            double distance = pos.distanceSqToCenter(player.posX, player.posY + 0.5D, player.posZ);
            if (distance < Math.pow(CarryOnConfig.settings.maxDistance, 2.0D) && !ItemTile.isLocked(pos, world)) {
               if (CustomPickupOverrideHandler.hasSpecialPickupConditions(state)) {
                  try {
                     Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
                     Class<?> iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");
                     Method getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", (String)null, new Class[]{EntityPlayer.class});
                     Method hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", (String)null, new Class[]{String.class});
                     Object stageData = getPlayerData.invoke((Object)null, player);
                     String condition = CustomPickupOverrideHandler.getPickupCondition(state);
                     boolean has = ((Boolean)hasStage.invoke(stageData, condition)).booleanValue();
                     if (has) {
                        return handleProtections((EntityPlayerMP)player, world, pos, state);
                     }
                  } catch (Exception var19) {
                     try {
                        Class<?> playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
                        Class<?> iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");
                        Method getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", (String)null, new Class[]{EntityPlayer.class});
                        Method hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", (String)null, new Class[]{String.class});
                        Object stageData = getStageData.invoke((Object)null, player);
                        String condition = CustomPickupOverrideHandler.getPickupCondition(state);
                        boolean has = ((Boolean)hasUnlockedStage.invoke(stageData, condition)).booleanValue();
                        if (has) {
                           return handleProtections((EntityPlayerMP)player, world, pos, state);
                        }
                     } catch (Exception var18) {
                        return handleProtections((EntityPlayerMP)player, world, pos, state);
                     }
                  }
               } else if (CarryOnConfig.settings.pickupAllBlocks || tile != null) {
                  return handleProtections((EntityPlayerMP)player, world, pos, state);
               }
            }
         }

         return false;
      }
   }

   public static boolean canPlayerPickUpEntity(EntityPlayer player, Entity toPickUp) {
      BlockPos pos = toPickUp.getPosition();
      if (toPickUp instanceof EntityPlayer) {
         return false;
      } else if (toPickUp.isDead) {
         return false;
      } else {
         CarryOnOverride override = ScriptChecker.inspectEntity(toPickUp);
         if (override != null) {
            return ScriptChecker.fulfillsConditions(override, player) && handleProtections((EntityPlayerMP)player, toPickUp);
         } else {
            if (toPickUp instanceof EntityAgeable && CarryOnConfig.settings.allowBabies) {
               EntityAgeable living = (EntityAgeable)toPickUp;
               if (living.getGrowingAge() < 0 || living.isChild()) {
                  double distance = pos.distanceSqToCenter(player.posX, player.posY + 0.5D, player.posZ);
                  if (distance < Math.pow(CarryOnConfig.settings.maxDistance, 2.0D) && toPickUp instanceof EntityTameable) {
                     EntityTameable tame = (EntityTameable)toPickUp;
                     if (tame.getOwnerId() != null && tame.getOwnerId() != EntityPlayer.getUUID(player.getGameProfile())) {
                        return false;
                     }
                  }

                  if (!CustomPickupOverrideHandler.hasSpecialPickupConditions(toPickUp)) {
                     return handleProtections((EntityPlayerMP)player, toPickUp);
                  }

                  try {
                     Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
                     Class<?> iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");
                     Method getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", (String)null, new Class[]{EntityPlayer.class});
                     Method hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", (String)null, new Class[]{String.class});
                     Object stageData = getPlayerData.invoke((Object)null, player);
                     String condition = CustomPickupOverrideHandler.getPickupCondition(toPickUp);
                     boolean has = ((Boolean)hasStage.invoke(stageData, condition)).booleanValue();
                     if (has) {
                        return handleProtections((EntityPlayerMP)player, toPickUp);
                     }
                  } catch (Exception var22) {
                     try {
                        Class<?> playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
                        Class<?> iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");
                        Method getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", (String)null, new Class[]{EntityPlayer.class});
                        Method hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", (String)null, new Class[]{String.class});
                        Object stageData = getStageData.invoke((Object)null, player);
                        String condition = CustomPickupOverrideHandler.getPickupCondition(toPickUp);
                        boolean has = ((Boolean)hasUnlockedStage.invoke(stageData, condition)).booleanValue();
                        if (has) {
                           return handleProtections((EntityPlayerMP)player, toPickUp);
                        }
                     } catch (Exception var21) {
                        return handleProtections((EntityPlayerMP)player, toPickUp);
                     }
                  }
               }
            }

            if (CarryOnConfig.settings.useWhitelistEntities) {
               if (!ListHandler.isAllowed(toPickUp)) {
                  return false;
               }
            } else if (ListHandler.isForbidden(toPickUp)) {
               return false;
            }

            if (toPickUp instanceof EntityTameable) {
               EntityTameable tamecheck = (EntityTameable)toPickUp;
               UUID OwnerCheck = tamecheck.getOwnerId();
               if ((CarryOnConfig.settings.pickupHostileMobs || !toPickUp.isCreatureType(EnumCreatureType.MONSTER, false) || CarryOnConfig.settings.tamedHostileMobExemption && OwnerCheck != null || player.isCreative()) && (CarryOnConfig.settings.pickupHostileMobs || !toPickUp.isCreatureType(EnumCreatureType.MONSTER, false) || CarryOnConfig.settings.tamedHostileMobExemption && OwnerCheck != null || player.isCreative()) && (toPickUp.height <= CarryOnConfig.settings.maxEntityHeight && toPickUp.width <= CarryOnConfig.settings.maxEntityWidth || player.isCreative())) {
                  double distance = pos.distanceSqToCenter(player.posX, player.posY + 0.5D, player.posZ);
                  if (distance < Math.pow(CarryOnConfig.settings.maxDistance, 2.0D)) {
                     UUID playerID = EntityPlayer.getUUID(player.getGameProfile());
                     if (OwnerCheck != null && !OwnerCheck.equals(playerID)) {
                        return false;
                     }

                     if (!CustomPickupOverrideHandler.hasSpecialPickupConditions(toPickUp)) {
                        return handleProtections((EntityPlayerMP)player, toPickUp);
                     }

                     try {
                        Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
                        Class<?> iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");
                        Method getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", (String)null, new Class[]{EntityPlayer.class});
                        Method hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", (String)null, new Class[]{String.class});
                        Object stageData = getPlayerData.invoke((Object)null, player);
                        String condition = CustomPickupOverrideHandler.getPickupCondition(toPickUp);
                        boolean has = ((Boolean)hasStage.invoke(stageData, condition)).booleanValue();
                        if (has) {
                           return handleProtections((EntityPlayerMP)player, toPickUp);
                        }
                     } catch (Exception var20) {
                        try {
                           Class<?> playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
                           Class<?> iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");
                           Method getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", (String)null, new Class[]{EntityPlayer.class});
                           Method hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", (String)null, new Class[]{String.class});
                           Object stageData = getStageData.invoke((Object)null, player);
                           String condition = CustomPickupOverrideHandler.getPickupCondition(toPickUp);
                           boolean has = ((Boolean)hasUnlockedStage.invoke(stageData, condition)).booleanValue();
                           if (has) {
                              return handleProtections((EntityPlayerMP)player, toPickUp);
                           }
                        } catch (Exception var19) {
                           return handleProtections((EntityPlayerMP)player, toPickUp);
                        }
                     }
                  }
               }
            } else if ((CarryOnConfig.settings.pickupHostileMobs || !toPickUp.isCreatureType(EnumCreatureType.MONSTER, false) || player.isCreative()) && (CarryOnConfig.settings.pickupHostileMobs || !toPickUp.isCreatureType(EnumCreatureType.MONSTER, false) || player.isCreative()) && (toPickUp.height <= CarryOnConfig.settings.maxEntityHeight && toPickUp.width <= CarryOnConfig.settings.maxEntityWidth || player.isCreative())) {
               double distance = pos.distanceSqToCenter(player.posX, player.posY + 0.5D, player.posZ);
               if (distance < Math.pow(CarryOnConfig.settings.maxDistance, 2.0D)) {
                  if (!CustomPickupOverrideHandler.hasSpecialPickupConditions(toPickUp)) {
                     return handleProtections((EntityPlayerMP)player, toPickUp);
                  }

                  try {
                     Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
                     Class<?> iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");
                     Method getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", (String)null, new Class[]{EntityPlayer.class});
                     Method hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", (String)null, new Class[]{String.class});
                     Object stageData = getPlayerData.invoke((Object)null, player);
                     String condition = CustomPickupOverrideHandler.getPickupCondition(toPickUp);
                     boolean has = ((Boolean)hasStage.invoke(stageData, condition)).booleanValue();
                     if (has) {
                        return handleProtections((EntityPlayerMP)player, toPickUp);
                     }
                  } catch (Exception var18) {
                     try {
                        Class<?> playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
                        Class<?> iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");
                        Method getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", (String)null, new Class[]{EntityPlayer.class});
                        Method hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", (String)null, new Class[]{String.class});
                        Object stageData = getStageData.invoke((Object)null, player);
                        String condition = CustomPickupOverrideHandler.getPickupCondition(toPickUp);
                        boolean has = ((Boolean)hasUnlockedStage.invoke(stageData, condition)).booleanValue();
                        if (has) {
                           return handleProtections((EntityPlayerMP)player, toPickUp);
                        }
                     } catch (Exception var17) {
                        return handleProtections((EntityPlayerMP)player, toPickUp);
                     }
                  }
               }
            }

            return false;
         }
      }
   }

   private static boolean handleProtections(EntityPlayerMP player, World world, BlockPos pos, IBlockState state) {
      boolean breakable = true;
      BreakEvent event = new BreakEvent(world, pos, state, player);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         breakable = false;
      }

      return breakable;
   }

   private static boolean handleProtections(EntityPlayerMP player, Entity entity) {
      boolean canPickup = true;
      AttackEntityEvent event = new AttackEntityEvent(player, entity);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         canPickup = false;
      }

      return canPickup;
   }
}
