package org.orecruncher.dsurround.expression;

import java.util.function.Supplier;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.expression.DynamicVariantList;
import org.orecruncher.lib.expression.Dynamic.DynamicBoolean;
import org.orecruncher.lib.expression.Dynamic.DynamicNumber;
import org.orecruncher.lib.expression.Dynamic.DynamicString;

public class PlayerVariables extends DynamicVariantList {
   public PlayerVariables() {
      this.add(new DynamicBoolean("player.isHurt", () -> {
         return EnvironStateHandler.EnvironState.isPlayerHurt();
      }));
      this.add(new DynamicBoolean("player.isHungry", () -> {
         return EnvironStateHandler.EnvironState.isPlayerHungry();
      }));
      this.add(new DynamicBoolean("player.isBurning", () -> {
         return EnvironStateHandler.EnvironState.isPlayerBurning();
      }));
      this.add(new DynamicBoolean("player.isSuffocating", () -> {
         return EnvironStateHandler.EnvironState.isPlayerSuffocating();
      }));
      this.add(new DynamicBoolean("player.isFlying", () -> {
         return EnvironStateHandler.EnvironState.isPlayerFlying();
      }));
      this.add(new DynamicBoolean("player.isSprinting", () -> {
         return EnvironStateHandler.EnvironState.isPlayerSprinting();
      }));
      this.add(new DynamicBoolean("player.isInLava", () -> {
         return EnvironStateHandler.EnvironState.isPlayerInLava();
      }));
      this.add(new DynamicBoolean("player.isInvisible", () -> {
         return EnvironStateHandler.EnvironState.isPlayerInvisible();
      }));
      this.add(new DynamicBoolean("player.isBlind", () -> {
         return EnvironStateHandler.EnvironState.isPlayerBlind();
      }));
      this.add(new DynamicBoolean("player.isInWater", () -> {
         return EnvironStateHandler.EnvironState.isPlayerInWater();
      }));
      this.add(new DynamicBoolean("player.isMoving", () -> {
         return EnvironStateHandler.EnvironState.isPlayerMoving();
      }));
      this.add(new DynamicBoolean("player.isInside", () -> {
         return EnvironStateHandler.EnvironState.isPlayerInside();
      }));
      this.add(new DynamicBoolean("player.isUnderground", () -> {
         return EnvironStateHandler.EnvironState.isPlayerUnderground();
      }));
      this.add(new DynamicBoolean("player.isInSpace", () -> {
         return EnvironStateHandler.EnvironState.isPlayerInSpace();
      }));
      this.add(new DynamicBoolean("player.isInClouds", () -> {
         return EnvironStateHandler.EnvironState.isPlayerInClouds();
      }));
      this.add(new DynamicString("player.temperature", () -> {
         return EnvironStateHandler.EnvironState.getPlayerTemperature().getValue();
      }));
      this.add(new DynamicBoolean("player.inVillage", () -> {
         return EnvironStateHandler.EnvironState.inVillage();
      }));
      this.add(new DynamicNumber("player.X", () -> {
         return (float)EnvironStateHandler.EnvironState.getPlayerPosition().getX();
      }));
      this.add(new DynamicNumber("player.Y", () -> {
         return (float)EnvironStateHandler.EnvironState.getPlayerPosition().getY();
      }));
      this.add(new DynamicNumber("player.Z", () -> {
         return (float)EnvironStateHandler.EnvironState.getPlayerPosition().getZ();
      }));
      this.add(new DynamicNumber("player.lightLevel", () -> {
         return (float)EnvironStateHandler.EnvironState.getLightLevel();
      }));
      this.add(new DynamicBoolean("player.isDead", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null && player.isDead;
      }));
      this.add(new DynamicBoolean("player.isWet", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null && player.isWet();
      }));
      this.add(new DynamicBoolean("player.isUnderwater", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null && player.isInsideOfMaterial(Material.WATER);
      }));
      this.add(new DynamicBoolean("player.isRiding", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null && player.isRiding();
      }));
      this.add(new DynamicBoolean("player.isOnGround", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null && player.onGround;
      }));
      this.add(new DynamicNumber("player.health", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null ? player.getHealth() : 2.14748365E9F;
      }));
      this.add(new DynamicNumber("player.maxHealth", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null ? player.getMaxHealth() : 2.14748365E9F;
      }));
      this.add(new DynamicNumber("player.luck", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null ? player.getLuck() : 0.0F;
      }));
      this.add(new DynamicNumber("player.food.saturation", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null ? player.getFoodStats().getSaturationLevel() : 0.0F;
      }));
      this.add(new DynamicNumber("player.food.level", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null ? (float)player.getFoodStats().getFoodLevel() : 0.0F;
      }));
      this.add(new DynamicBoolean("player.canRainOn", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         if (world == null) {
            return false;
         } else {
            BlockPos pos = EnvironStateHandler.EnvironState.getPlayerPosition().add(0, 2, 0);
            return world.canBlockSeeSky(pos) && world.getTopSolidOrLiquidBlock(pos).getY() <= pos.getY();
         }
      }));
      this.add(new DynamicBoolean("player.canSeeSky", () -> {
         World world = EnvironStateHandler.EnvironState.getWorld();
         if (world != null) {
            BlockPos pos = EnvironStateHandler.EnvironState.getPlayerPosition().add(0, 2, 0);
            return world.canBlockSeeSky(pos);
         } else {
            return false;
         }
      }));
      this.add(new DynamicBoolean("player.inBoat", () -> {
         EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
         return player != null && player.getRidingEntity() instanceof EntityBoat;
      }));
   }
}
