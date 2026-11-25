package com.endertech.minecraft.forge.messages;

import com.endertech.minecraft.forge.world.ForgeWorld;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForgeNetMsgHandler<MSG extends IMessage> implements IMessageHandler<MSG, IMessage> {
   public ForgeNetMsgHandler() {
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   @Nullable
   public EntityPlayer getPlayer(MessageContext ctx) {
      return (EntityPlayer)(this.isClientSide(ctx) ? this.getClientPlayer() : ctx.getServerHandler().player);
   }

   @Nullable
   public World getWorld(MessageContext ctx) {
      EntityPlayer player = this.getPlayer(ctx);
      return player != null ? player.world : null;
   }

   @Nullable
   public Entity findEntity(MessageContext ctx, int id) {
      return ForgeWorld.findEntity(this.getWorld(ctx), id);
   }

   public boolean isServerSide(MessageContext ctx) {
      return ctx.side.isServer();
   }

   public boolean isClientSide(MessageContext ctx) {
      return ctx.side.isClient();
   }

   public Block getBlock(MessageContext ctx, BlockPos pos) {
      return ForgeWorld.getBlock(this.getWorld(ctx), pos);
   }

   protected void addScheduledTask(MessageContext ctx, Runnable runnable) {
      if (this.isClientSide(ctx)) {
         Minecraft.getMinecraft().addScheduledTask(runnable);
      } else {
         ctx.getServerHandler().player.getServerWorld().addScheduledTask(runnable);
      }

   }
}
