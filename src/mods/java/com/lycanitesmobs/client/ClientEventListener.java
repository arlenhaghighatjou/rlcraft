package com.lycanitesmobs.client;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.item.special.ItemSoulgazer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventListener {
   public static int ITEM_RENDER_MODE = 0;

   public ClientEventListener() {
   }

   public void register(EventBus bus) {
      bus.register(RenderHandEvent.class, this::onRenderHand);
      bus.register(RenderGameOverlayEvent.class, this::onRenderHand);
      bus.register(RenderWorldLastEvent.class, this::onRenderWorldLast);
      bus.register(FogDensity.class, this::onFogDensity);
      bus.register(RenderBlockOverlayEvent.class, this::onBlockOverlay);
      bus.register(ItemTooltipEvent.class, this::onItemTooltip);
   }

   @SideOnly(Side.CLIENT)
   public void onRenderHand(RenderHandEvent event) {
      ITEM_RENDER_MODE = 1;
   }

   @SideOnly(Side.CLIENT)
   public void onRenderHand(RenderGameOverlayEvent event) {
      if (event.getType() == ElementType.EXPERIENCE) {
         ITEM_RENDER_MODE = 2;
      }

   }

   @SideOnly(Side.CLIENT)
   public void onRenderWorldLast(RenderWorldLastEvent event) {
      ITEM_RENDER_MODE = 1;
   }

   @SideOnly(Side.CLIENT)
   public void onFogDensity(FogDensity event) {
      if (event.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase entityLiving = (EntityLivingBase)event.getEntity();
         if (event.getState().getMaterial() == Material.LAVA && (!event.getEntity().isBurning() || entityLiving.isPotionActive(MobEffects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public void onBlockOverlay(RenderBlockOverlayEvent event) {
      if (event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isBurning() || event.getPlayer().isPotionActive(MobEffects.FIRE_RESISTANCE))) {
         event.setCanceled(true);
      }

   }

   public void onItemTooltip(ItemTooltipEvent event) {
      int sharpness = ItemManager.getInstance().getEquipmentSharpnessRepair(event.getItemStack());
      int mana = ItemManager.getInstance().getEquipmentManaRepair(event.getItemStack());
      if (sharpness > 0 || mana > 0) {
         event.getToolTip().add(LanguageManager.translate("equipment.item.repair"));
         if (sharpness > 0) {
            event.getToolTip().add(LanguageManager.translate("equipment.sharpness") + " " + sharpness);
         }

         if (mana > 0) {
            event.getToolTip().add(LanguageManager.translate("equipment.mana") + " " + mana);
         }
      }

      if (event.getItemStack().getItem() instanceof ItemSoulgazer) {
         ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getEntityPlayer());
         if (extendedPlayer != null && extendedPlayer.creatureStudyCooldown > 0) {
            event.getToolTip().add(LanguageManager.translate("message.beastiary.study.cooldown") + " " + HString.format("%.0f", (float)extendedPlayer.creatureStudyCooldown / 20.0F) + "s");
         }
      }

   }
}
