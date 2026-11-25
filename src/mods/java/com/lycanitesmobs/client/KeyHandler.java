package com.lycanitesmobs.client;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.CreaturesBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.PetsBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.client.gui.buttons.TabManager;
import com.lycanitesmobs.client.gui.overlays.MinionSelectionOverlay;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.network.MessagePlayerAttack;
import com.lycanitesmobs.core.network.MessagePlayerControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class KeyHandler {
   public static KeyHandler instance;
   public Minecraft mc;
   public boolean inventoryOpen = false;
   public KeyBinding dismount = new KeyBinding("key.mount.dismount", 46, "Lycanites Mobs");
   public KeyBinding descend = new KeyBinding("key.mount.descend", 42, "Lycanites Mobs");
   public KeyBinding mountAbility = new KeyBinding("key.mount.ability", 45, "Lycanites Mobs");
   public KeyBinding mountInventory = new KeyBinding("key.mount.inventory", 0, "Lycanites Mobs");
   public KeyBinding beastiary = new KeyBinding("key.beastiary", 0, "Lycanites Mobs");
   public KeyBinding index = new KeyBinding("key.index", 34, "Lycanites Mobs");
   public KeyBinding pets = new KeyBinding("key.pets", 0, "Lycanites Mobs");
   public KeyBinding summoning = new KeyBinding("key.summoning", 0, "Lycanites Mobs");
   public KeyBinding minionSelection = new KeyBinding("key.minions", 19, "Lycanites Mobs");

   public KeyHandler(Minecraft mc) {
      this.mc = mc;
      instance = this;
      ClientRegistry.registerKeyBinding(this.dismount);
      ClientRegistry.registerKeyBinding(this.descend);
      ClientRegistry.registerKeyBinding(this.mountAbility);
      ClientRegistry.registerKeyBinding(this.mountInventory);
      ClientRegistry.registerKeyBinding(this.index);
      ClientRegistry.registerKeyBinding(this.beastiary);
      ClientRegistry.registerKeyBinding(this.pets);
      ClientRegistry.registerKeyBinding(this.summoning);
      ClientRegistry.registerKeyBinding(this.minionSelection);
   }

   @SubscribeEvent
   public void onPlayerTick(ClientTickEvent event) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
      if (playerExt != null) {
         byte controlStates = 0;
         if (Mouse.isButtonDown(1)) {
            controlStates += ExtendedPlayer.CONTROL_ID.RIGHT_MOUSE.id;
         }

         if (!this.inventoryOpen && this.mc.currentScreen != null && this.mc.currentScreen.getClass() == GuiInventory.class) {
            TabManager.addTabsToInventory(this.mc.currentScreen);
            this.inventoryOpen = true;
         }

         if (this.inventoryOpen && (this.mc.currentScreen == null || this.mc.currentScreen.getClass() != GuiInventory.class)) {
            this.inventoryOpen = false;
         }

         if (this.mountInventory.isPressed()) {
            controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_INVENTORY.id;
         }

         if (this.index.isPressed()) {
            IndexBeastiaryScreen.openToPlayer(this.mc.player);
         }

         if (this.beastiary.isPressed()) {
            CreaturesBeastiaryScreen.openToPlayer(this.mc.player);
         }

         if (this.pets.isPressed()) {
            PetsBeastiaryScreen.openToPlayer(this.mc.player);
         }

         if (this.summoning.isPressed()) {
            SummoningBeastiaryScreen.openToPlayer(this.mc.player);
         }

         try {
            if (!Keyboard.isKeyDown(this.minionSelection.getKeyCode()) && !Mouse.isButtonDown(this.minionSelection.getKeyCode()) && this.mc.currentScreen instanceof MinionSelectionOverlay) {
               this.mc.player.closeScreen();
            }
         } catch (Exception var5) {
            ;
         }

         if (this.mc.inGameHasFocus) {
            if (this.minionSelection.isPressed()) {
               MinionSelectionOverlay.openToPlayer(this.mc.player);
            }

            if (this.mc.gameSettings.keyBindJump.isKeyDown()) {
               controlStates += ExtendedPlayer.CONTROL_ID.JUMP.id;
            }

            if (this.descend.isKeyDown()) {
               controlStates += ExtendedPlayer.CONTROL_ID.DESCEND.id;
            }

            if (this.dismount.isKeyDown()) {
               controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT.id;
            }

            if (this.mountAbility.isKeyDown()) {
               controlStates += ExtendedPlayer.CONTROL_ID.MOUNT_ABILITY.id;
            }

            if (Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown()) {
               controlStates += ExtendedPlayer.CONTROL_ID.ATTACK.id;
            }
         }

         if (controlStates != playerExt.controlStates) {
            MessagePlayerControl message = new MessagePlayerControl(controlStates);
            LycanitesMobs.packetHandler.sendToServer(message);
            playerExt.controlStates = controlStates;
         }
      }
   }

   @SubscribeEvent
   public void onMouseEvent(MouseEvent event) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.mc.player);
      if (this.mc.player != null && playerExt != null && this.mc.objectMouseOver != null) {
         if (event.getButton() == 0 && !this.mc.player.isSpectator() && !this.mc.player.isRowingBoat() && this.mc.objectMouseOver.typeOfHit == Type.ENTITY) {
            Entity entityHit = this.mc.objectMouseOver.entityHit;
            if (playerExt.canMeleeBigEntity(entityHit)) {
               MessagePlayerAttack message = new MessagePlayerAttack(entityHit);
               LycanitesMobs.packetHandler.sendToServer(message);
            }
         }

      }
   }
}
