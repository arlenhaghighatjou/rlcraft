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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AquacultureEntitySpawnHandler {
   public AquacultureEntitySpawnHandler() {
   }

   @SideOnly(Side.CLIENT)
   public static void init() {
      EntityRegistration customFishHookRegistration = EntityRegistry.instance().lookupModSpawn(EntityCustomFishHook.class, false);
      Function<EntitySpawnMessage, Entity> fishHookSpawnHandler = (input) -> {
         int anglerId = input.throwerId;
         double posX = input.rawX;
         double posY = input.rawY;
         double posZ = input.rawZ;

         WorldClient world = FMLClientHandler.instance().getWorldClient();
         Entity angler = world.getEntityByID(anglerId);
         return !(angler instanceof EntityPlayer) ? null : new EntityCustomFishHook(world, (EntityPlayer)angler, posX, posY, posZ);
      };
      Preconditions.checkNotNull(customFishHookRegistration, "fishHookRegistration");
      customFishHookRegistration.setCustomSpawning(fishHookSpawnHandler, false);
   }
}
