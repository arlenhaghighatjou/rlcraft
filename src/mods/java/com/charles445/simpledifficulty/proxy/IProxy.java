package com.charles445.simpledifficulty.proxy;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy {
   void preInit();

   void init();

   void postInit();

   Side getSide();

   @Nullable
   EntityPlayer getClientMinecraftPlayer();

   @Nullable
   Boolean isClientConnectedToServer();

   void spawnClientParticle(World var1, String var2, double var3, double var5, double var7, double var9, double var11, double var13);
}
