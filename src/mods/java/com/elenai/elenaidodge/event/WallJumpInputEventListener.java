package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.api.WallJumpEvent;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Keyboard;

public class WallJumpInputEventListener {
   public static boolean needsInit = true;
   public static HashMap<KeyBinding, Long> keyTimesLastPressed = new HashMap();
   public static HashMap<KeyBinding, Boolean> keyLastState = new HashMap();
   public static HashMap<KeyBinding, String> lookupKeyToDirection = new HashMap();

   public WallJumpInputEventListener() {
   }

   @SubscribeEvent
   public void onKeyInput(ClientTickEvent event) {
      if (event.phase == Phase.START && Minecraft.getMinecraft().currentScreen == null) {
         if (needsInit) {
            needsInit = false;
            tickInit();
         }

         tickWallJump();
      }

   }

   public static void tickInit() {
      lookupKeyToDirection.put(Minecraft.getMinecraft().gameSettings.keyBindJump, "jump");
      keyLastState.put(Minecraft.getMinecraft().gameSettings.keyBindJump, Boolean.valueOf(false));
   }

   public static void tickWallJump() {
      lookupKeyToDirection.forEach((k, d) -> {
         processKey(k, d);
      });
   }

   public static void processKey(KeyBinding key, String direction) {
      long curTime = System.currentTimeMillis();
      long lastTime = getLastKeyTime(key);
      if (key.getKeyCode() > 0) {
         if (Keyboard.isKeyDown(key.getKeyCode()) && !((Boolean)keyLastState.get(key)).booleanValue()) {
            if (lastTime == -1L) {
               setLastKeyTime(key, curTime);
            } else if (lastTime + 70L > curTime) {
               setLastKeyTime(key, -1L);
            } else {
               if (!Minecraft.getMinecraft().player.onGround) {
                  WallJumpEvent ev = new WallJumpEvent.RequestWallJumpEvent();
                  MinecraftForge.EVENT_BUS.post(ev);
               }

               setLastKeyTime(key, curTime);
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
