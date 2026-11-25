package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class PlayerSpawnCondition extends SpawnCondition {
   public String username = "";
   public float difficultyMin = -1.0F;
   public float difficultyMax = -1.0F;
   public float spawnDistanceMin = -1.0F;
   public float spawnDistanceMax = -1.0F;
   public BlockPos position = null;
   public float positionDistanceMin = -1.0F;
   public float positionDistanceMax = -1.0F;
   public int levelMin = -1;
   public int levelMax = -1;
   public int timeMin = -1;
   public int timeMax = -1;
   public int lightLevelMin = -1;
   public int lightLevelMax = -1;
   public boolean grounded = false;
   public boolean notGrounded = false;
   public boolean inWater = false;
   public boolean notInWater = false;
   public List<Item> heldItems = new ArrayList();
   public String heldItemsListType = "blacklist";

   public PlayerSpawnCondition() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("username")) {
         this.username = json.get("username").getAsString();
      }

      if (json.has("difficultyMin")) {
         this.difficultyMin = json.get("difficultyMin").getAsFloat();
      }

      if (json.has("difficultyMax")) {
         this.difficultyMax = json.get("difficultyMax").getAsFloat();
      }

      if (json.has("spawnDistanceMin")) {
         this.spawnDistanceMin = json.get("spawnDistanceMin").getAsFloat();
      }

      if (json.has("spawnDistanceMax")) {
         this.spawnDistanceMax = json.get("spawnDistanceMax").getAsFloat();
      }

      if (json.has("position")) {
         this.position = new BlockPos(JSONHelper.getVec3i(json, "position"));
      }

      if (json.has("positionDistanceMin")) {
         this.positionDistanceMin = json.get("positionDistanceMin").getAsFloat();
      }

      if (json.has("positionDistanceMax")) {
         this.positionDistanceMax = json.get("positionDistanceMax").getAsFloat();
      }

      if (json.has("levelMin")) {
         this.levelMin = json.get("levelMin").getAsInt();
      }

      if (json.has("levelMax")) {
         this.levelMax = json.get("levelMax").getAsInt();
      }

      if (json.has("timeMin")) {
         this.timeMin = json.get("timeMin").getAsInt();
      }

      if (json.has("timeMax")) {
         this.timeMax = json.get("timeMax").getAsInt();
      }

      if (json.has("lightLevelMin")) {
         this.lightLevelMin = json.get("lightLevelMin").getAsInt();
      }

      if (json.has("lightLevelMax")) {
         this.lightLevelMax = json.get("lightLevelMax").getAsInt();
      }

      if (json.has("grounded")) {
         this.grounded = json.get("grounded").getAsBoolean();
      }

      if (json.has("notGrounded")) {
         this.notGrounded = json.get("notGrounded").getAsBoolean();
      }

      if (json.has("inWater")) {
         this.inWater = json.get("inWater").getAsBoolean();
      }

      if (json.has("notInWater")) {
         this.notInWater = json.get("notInWater").getAsBoolean();
      }

      if (json.has("heldItems")) {
         this.heldItems = JSONHelper.getJsonItems(json.get("heldItems").getAsJsonArray());
      }

      if (json.has("heldItemsListType")) {
         this.heldItemsListType = json.get("heldItemsListType").getAsString();
      }

      super.loadFromJSON(json);
   }

   public boolean isMet(World world, EntityPlayer player, BlockPos position) {
      if (player == null) {
         return false;
      } else {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         if (!"".equals(this.username) && !this.username.equalsIgnoreCase(player.getName())) {
            return false;
         } else {
            if (this.difficultyMin >= 0.0F || this.difficultyMax >= 0.0F) {
               DifficultyInstance difficultyInstance = world.getDifficultyForLocation(player.getPosition());
               float difficulty = difficultyInstance.getAdditionalDifficulty();
               if (this.difficultyMin >= 0.0F && difficulty < this.difficultyMin) {
                  return false;
               }

               if (this.difficultyMax >= 0.0F && difficulty > this.difficultyMax) {
                  return false;
               }
            }

            double spawnDistance = Math.sqrt(player.getPosition().distanceSq(world.getSpawnPoint()));
            if (this.spawnDistanceMin >= 0.0F && spawnDistance < (double)this.spawnDistanceMin) {
               return false;
            } else if (this.spawnDistanceMax >= 0.0F && spawnDistance > (double)this.spawnDistanceMax) {
               return false;
            } else {
               if (this.position != null) {
                  double positionDistance = player.getPosition().getDistance(this.position.getX(), this.position.getY(), this.position.getZ());
                  if (this.positionDistanceMin >= 0.0F && positionDistance < (double)this.positionDistanceMin) {
                     return false;
                  }

                  if (this.positionDistanceMax >= 0.0F && positionDistance > (double)this.positionDistanceMax) {
                     return false;
                  }
               }

               if (this.levelMin >= 0 && player.experienceLevel < this.levelMin) {
                  return false;
               } else if (this.levelMax >= 0 && player.experienceLevel > this.levelMax) {
                  return false;
               } else if (this.grounded && !player.onGround) {
                  return false;
               } else if (this.notGrounded && player.onGround) {
                  return false;
               } else if (this.inWater && !player.isInWater()) {
                  return false;
               } else if (this.notInWater && player.isInWater()) {
                  return false;
               } else {
                  if (playerExt != null) {
                     if (this.timeMin >= 0 && playerExt.timePlayed < (long)this.timeMin) {
                        return false;
                     }

                     if (this.timeMax >= 0 && playerExt.timePlayed > (long)this.timeMax) {
                        return false;
                     }
                  }

                  int lightLevel = world.getLight(player.getPosition());
                  if (this.lightLevelMin >= 0 && lightLevel < this.lightLevelMin) {
                     return false;
                  } else if (this.lightLevelMax >= 0 && lightLevel > this.lightLevelMax) {
                     return false;
                  } else {
                     if (this.heldItems.size() > 0) {
                        boolean holdingItem = false;
                        if (!player.getHeldItemMainhand().isEmpty() && this.heldItems.contains(player.getHeldItemMainhand().getItem())) {
                           holdingItem = true;
                           if ("blacklist".equalsIgnoreCase(this.heldItemsListType)) {
                              return false;
                           }
                        }

                        if (!player.getHeldItemOffhand().isEmpty() && this.heldItems.contains(player.getHeldItemOffhand().getItem())) {
                           holdingItem = true;
                           if ("blacklist".equalsIgnoreCase(this.heldItemsListType)) {
                              return false;
                           }
                        }

                        if (!holdingItem && "whitelist".equalsIgnoreCase(this.heldItemsListType)) {
                           return false;
                        }
                     } else if ("whitelist".equalsIgnoreCase(this.heldItemsListType)) {
                        return false;
                     }

                     return super.isMet(world, player, position);
                  }
               }
            }
         }
      }
   }
}
