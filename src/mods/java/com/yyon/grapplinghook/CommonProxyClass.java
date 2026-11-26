package com.yyon.grapplinghook;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import java.util.HashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxyClass {
   public CommonProxyClass() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.register(MinecraftForge.EVENT_BUS);

   }

   public void init(FMLInitializationEvent event, grapplemod grapplemod) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   public void sendplayermovementmessage(grappleArrow grappleArrow, int playerid, int arrowid) {
   }

   public void getplayermovement(grappleController control, int playerid) {
   }

   public void resetlaunchertime(int playerid) {
   }

   public void launchplayer(EntityPlayer player) {
   }

   public boolean isSneaking(Entity entity) {
      return entity.isSneaking();
   }

   public void register(EventBus bus) {
      bus.register(BreakEvent.class, this::onBlockBreak);
      bus.register(LivingHurtEvent.class, this::onLivingHurt);
      bus.register(LivingDeathEvent.class, this::onLivingDeath);
   }

   public void onBlockBreak(BreakEvent event) {
      EntityPlayer player = event.getPlayer();
      if (player != null) {
         ItemStack stack = player.getHeldItemMainhand();
         if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof grappleBow) {
               event.setCanceled(true);
               return;
            }
         }
      }

      this.blockbreak(event);
   }

   public void blockbreak(BreakEvent event) {
   }

   public void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource() == DamageSource.IN_WALL && grapplemod.attached.contains(Integer.valueOf(event.getEntity().getEntityId()))) {
         event.setCanceled(true);
      }

   }

   public String getkeyname(CommonProxyClass.keys keyenum) {
      return null;
   }

   public void openModifierScreen(TileEntityGrappleModifier tileent) {
   }

   public String localize(String string) {
      return string;
   }

   public void startrocket(EntityPlayer player, GrappleCustomization custom) {
   }

   public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
   }

   public double getRocketFunctioning() {
      return 0.0D;
   }

   public boolean iswallrunning(Entity entity, vec motion) {
      return false;
   }

   public boolean issliding(Entity entity, vec motion) {
      return false;
   }

   public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
      return null;
   }

   public void playSlideSound(Entity entity) {
   }

   public void onLivingDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      int id = entity.getEntityId();
      boolean isconnected = grapplemod.allarrows.containsKey(Integer.valueOf(id));
      if (isconnected) {
         HashSet<grappleArrow> arrows = (HashSet)grapplemod.allarrows.get(Integer.valueOf(id));

         for(grappleArrow arrow : arrows) {
            arrow.removeServer();
         }

         arrows.clear();
         grapplemod.attached.remove(Integer.valueOf(id));
         if (grapplemod.controllers.containsKey(Integer.valueOf(id))) {
            grapplemod.controllers.remove(Integer.valueOf(id));
         }

         if (grappleBow.grapplearrows1.containsKey(entity)) {
            grappleBow.grapplearrows1.remove(entity);
         }

         if (grappleBow.grapplearrows2.containsKey(entity)) {
            grappleBow.grapplearrows2.remove(entity);
         }

         grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entity.world);
      }

   }

   public void playWallrunJumpSound(Entity entity) {
   }

   public void playSound(ResourceLocation loc, float volume) {
   }

   public int getWallrunTicks() {
      return -1;
   }

   public void setWallrunTicks(int newWallrunTicks) {
   }

   public static enum keys {
      keyBindUseItem,
      keyBindForward,
      keyBindLeft,
      keyBindBack,
      keyBindRight,
      keyBindJump,
      keyBindSneak,
      keyBindAttack;

      private keys() {
      }
   }
}
