package io.github.xcube16.iseedragons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FakeThirstStatHandler {
   private final Object handler;
   private final Method onPlayerJump;
   private final Method onPlayerHurt;
   private final Method onBlockBreak;
   private final Method getThirstData;
   private final Class thirstHandlerClass;
   private final Method addExhaustion;

   public FakeThirstStatHandler(Object handler) throws Exception {
      this.handler = handler;
      this.onPlayerJump = handler.getClass().getMethod("onPlayerJump", LivingJumpEvent.class);
      this.onPlayerHurt = handler.getClass().getMethod("onPlayerHurt", LivingHurtEvent.class);
      this.onBlockBreak = handler.getClass().getMethod("onBlockBreak", BreakEvent.class);
      this.getThirstData = Class.forName("toughasnails.api.thirst.ThirstHelper").getMethod("getThirstData", EntityPlayer.class);
      this.thirstHandlerClass = Class.forName("toughasnails.thirst.ThirstHandler");
      this.addExhaustion = this.thirstHandlerClass.getMethod("addExhaustion", Float.TYPE);
   }

   @SubscribeEvent
   public void onPlayerJump(LivingJumpEvent event) {
      try {
         this.onPlayerJump.invoke(this.handler, event);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException var3) {
         this.failMethod(var3);
      }

   }

   @SubscribeEvent
   public void onPlayerHurt(LivingHurtEvent event) {
      try {
         this.onPlayerHurt.invoke(this.handler, event);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException var3) {
         this.failMethod(var3);
      }

   }

   @SubscribeEvent
   public void onBlockBreak(BreakEvent event) {
      try {
         this.onBlockBreak.invoke(this.handler, event);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException var3) {
         this.failMethod(var3);
      }

   }

   @SubscribeEvent
   public void onAttackEntity(AttackEntityEvent event) {
      try {
         World world = event.getEntity().world;
         if (world.isRemote) {
            return;
         }

         EntityPlayer player = event.getEntityPlayer();
         Entity monster = event.getTarget();
         if (monster.canBeAttackedWithItem() && !player.isCreative() && !monster.hitByEntity(player)) {
            Object thirstHandler = this.thirstHandlerClass.cast(this.getThirstData.invoke((Object)null, player));
            this.addExhaustion.invoke(thirstHandler, 0.3F);
         }
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException var6) {
         this.failMethod(var6);
      }

   }

   private void failMethod(Exception e) {
      throw new RuntimeException("FakeThirstStatHandler failed reflection", e);
   }
}
