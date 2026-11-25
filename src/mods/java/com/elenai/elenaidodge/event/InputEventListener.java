package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.api.DodgeEvent;
import com.elenai.elenaidodge.util.ClientStorage;
import com.elenai.elenaidodge.util.Keybinds;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class InputEventListener {
   public static boolean needsInit = true;
   public static HashMap<KeyBinding, Long> keyTimesLastPressed = new HashMap();
   public static HashMap<KeyBinding, Boolean> keyLastState = new HashMap();
   public static HashMap<KeyBinding, String> lookupKeyToDirection = new HashMap();

   public InputEventListener() {
   }

   @SubscribeEvent
   public void onKeyInput(ClientTickEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (ClientStorage.cooldown <= 0 && event.phase == Phase.START && Minecraft.getMinecraft().currentScreen == null) {
         if (ModConfig.client.controls.doubleTap) {
            if (needsInit) {
               needsInit = false;
               tickInit();
            }

            tickDodging();
            if (Keybinds.dodge.isKeyDown() && !ModConfig.client.controls.doubleTapForwards) {
               DodgeEvent ev = new DodgeEvent.RequestDodgeEvent(DodgeEvent.Direction.FORWARD);
               MinecraftForge.EVENT_BUS.post(ev);
            }
         } else if (Keybinds.dodge.isKeyDown()) {
            if (player.moveStrafing > 0.0F) {
               DodgeEvent ev = new DodgeEvent.RequestDodgeEvent(DodgeEvent.Direction.LEFT);
               MinecraftForge.EVENT_BUS.post(ev);
            } else if (player.moveStrafing < 0.0F) {
               DodgeEvent ev = new DodgeEvent.RequestDodgeEvent(DodgeEvent.Direction.RIGHT);
               MinecraftForge.EVENT_BUS.post(ev);
            } else if (player.moveForward > 0.0F) {
               DodgeEvent ev = new DodgeEvent.RequestDodgeEvent(DodgeEvent.Direction.FORWARD);
               MinecraftForge.EVENT_BUS.post(ev);
            } else if (player.moveForward < 0.0F) {
               DodgeEvent ev = new DodgeEvent.RequestDodgeEvent(DodgeEvent.Direction.BACK);
               MinecraftForge.EVENT_BUS.post(ev);
            }
         }
      }

   }

   public static void tickInit() {
      lookupKeyToDirection.put(Minecraft.getMinecraft().gameSettings.keyBindForward, "forward");
      lookupKeyToDirection.put(Minecraft.getMinecraft().gameSettings.keyBindBack, "back");
      lookupKeyToDirection.put(Minecraft.getMinecraft().gameSettings.keyBindLeft, "left");
      lookupKeyToDirection.put(Minecraft.getMinecraft().gameSettings.keyBindRight, "right");
      keyLastState.put(Minecraft.getMinecraft().gameSettings.keyBindForward, Boolean.valueOf(false));
      keyLastState.put(Minecraft.getMinecraft().gameSettings.keyBindBack, Boolean.valueOf(false));
      keyLastState.put(Minecraft.getMinecraft().gameSettings.keyBindLeft, Boolean.valueOf(false));
      keyLastState.put(Minecraft.getMinecraft().gameSettings.keyBindRight, Boolean.valueOf(false));
   }

   public static void tickDodging() {
      lookupKeyToDirection.forEach((k, d) -> {
         processDodgeKey(k, d);
      });
   }

   public static void processDodgeKey(KeyBinding key, String direction) {
      long curTime = System.currentTimeMillis();
      long lastTime = getLastKeyTime(key);
      if (key.getKeyCode() > 0) {
         if (Keyboard.isKeyDown(key.getKeyCode()) && !((Boolean)keyLastState.get(key)).booleanValue()) {
            if (lastTime == -1L) {
               setLastKeyTime(key, curTime);
            } else if (lastTime + (long)ModConfig.client.controls.doubleTapTicks <= curTime) {
               setLastKeyTime(key, curTime);
            } else {
               if (!direction.toUpperCase().equals("FORWARD") || ModConfig.client.controls.doubleTapForwards) {
                  DodgeEvent ev = new DodgeEvent.RequestDodgeEvent(DodgeEvent.Direction.valueOf(direction.toUpperCase()));
                  MinecraftForge.EVENT_BUS.post(ev);
               }

               setLastKeyTime(key, -1L);
            }
         }

         if (!Keyboard.isKeyDown(key.getKeyCode()) && ((Boolean)keyLastState.get(key)).booleanValue()) {
            for(Entry<KeyBinding, Long> entry : keyTimesLastPressed.entrySet()) {
               if (entry.getKey() != key) {
                  entry.setValue(Long.valueOf(-1L));
               }
            }
         }

         keyLastState.put(key, Boolean.valueOf(Keyboard.isKeyDown(key.getKeyCode())));
      }

   }

   public static long getLastKeyTime(KeyBinding keybind) {
      if (!keyTimesLastPressed.containsKey(keybind)) {
         keyTimesLastPressed.put(keybind, Long.valueOf(-1L));
      }

      return ((Long)keyTimesLastPressed.get(keybind)).longValue();
   }

   public static void setLastKeyTime(KeyBinding keybind, long time) {
      keyTimesLastPressed.put(keybind, Long.valueOf(time));
   }
}
