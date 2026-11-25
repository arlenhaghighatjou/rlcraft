package com.teammetallurgy.aquaculture.handlers;

import com.google.common.base.Preconditions;
import java.util.function.Function;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AquacultureEntitySpawnHandler {
   public AquacultureEntitySpawnHandler() {
   }

   @SideOnly(Side.CLIENT)
   public static void init() {
      EntityRegistration customFishHookRegistration = EntityRegistry.instance().lookupModSpawn(EntityCustomFishHook.class, false);
      Function<EntitySpawnMessage, Entity> fishHookSpawnHandler = (input) -> {
         int anglerId = 0;
         double posX = 0.0D;
         double posY = 0.0D;
         double posZ = 0.0D;

         try {
            anglerId = ReflectionHelper.findField(EntitySpawnMessage.class, new String[]{"throwerId"}).getInt(input);
            posX = ReflectionHelper.findField(EntitySpawnMessage.class, new String[]{"rawX"}).getDouble(input);
            posY = ReflectionHelper.findField(EntitySpawnMessage.class, new String[]{"rawY"}).getDouble(input);
            posZ = ReflectionHelper.findField(EntitySpawnMessage.class, new String[]{"rawZ"}).getDouble(input);
         } catch (IllegalAccessException | IllegalArgumentException var10) {
            var10.printStackTrace();
         }

         WorldClient world = FMLClientHandler.instance().getWorldClient();
         Entity angler = world.getEntityByID(anglerId);
         return !(angler instanceof EntityPlayer) ? null : new EntityCustomFishHook(world, (EntityPlayer)angler, posX, posY, posZ);
      };
      Preconditions.checkNotNull(customFishHookRegistration, "fishHookRegistration");
      customFishHookRegistration.setCustomSpawning(fishHookSpawnHandler, false);
   }
}
