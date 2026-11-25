package com.fantasticsource.mctools.controlintercept;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ControlEvent extends Event {
   protected static Map<String, KeyBinding> keybinds;
   protected static Map<KeyBinding, Boolean> keybindStates = new LinkedHashMap();
   protected static Map<KeyBinding, Boolean> rawKeybindStates = new LinkedHashMap();
   public String name;
   public KeyBinding binding = null;
   public boolean state;
   public Boolean lastState;
   public String identifier = "";
   public EntityPlayerMP player = null;
   protected ArrayList<String> queueToServer = new ArrayList();
   protected boolean cancelOriginal = false;

   protected ControlEvent(String name, KeyBinding binding, boolean state, Boolean lastState) {
      this.name = name;
      this.binding = binding;
      this.state = state;
      this.lastState = lastState;
   }

   public ControlEvent(String name, boolean state, Boolean lastState, String identifier) {
      this.name = name;
      this.state = state;
      this.lastState = lastState;
      this.identifier = identifier;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void interceptControls(InputEvent inputEvent) {
      for(ControlEvent event : updateStatesAndReportChanged()) {
         MinecraftForge.EVENT_BUS.post(event);
         if (event.cancelOriginal) {
            setKeyState(event.binding, event.lastState.booleanValue());
         } else {
            keybindStates.put(event.binding, Boolean.valueOf(event.state));
         }

         for(String identifier : event.queueToServer) {
            event.identifier = identifier;
            Network.WRAPPER.sendToServer(new Network.ControlEventPacket(event));
         }
      }

   }

   protected static ArrayList<ControlEvent> updateStatesAndReportChanged() {
      ArrayList<ControlEvent> result = new ArrayList();

      for(Entry<String, KeyBinding> entry : keybinds.entrySet()) {
         KeyBinding binding = (KeyBinding)entry.getValue();
         boolean state = binding.isKeyDown();
         boolean lastState = ((Boolean)keybindStates.computeIfAbsent(binding, (o) -> {
            return false;
         })).booleanValue();
         boolean lastRawState = ((Boolean)rawKeybindStates.computeIfAbsent(binding, (o) -> {
            return false;
         })).booleanValue();
         if (state != lastState) {
            if (state != lastRawState) {
               result.add(new ControlEvent((String)entry.getKey(), binding, state, lastState));
            } else {
               setKeyState(binding, lastState);
            }
         }

         rawKeybindStates.put(binding, Boolean.valueOf(state));
      }

      return result;
   }

   protected static void setKeyState(KeyBinding binding, boolean state) {
      if (binding.isKeyDown() != state) {
         KeyBinding.setKeyBindState(binding.getKeyCode(), state);
         if (state) {
            KeyBinding.onTick(binding.getKeyCode());
         } else {
            while(true) {
               if (binding.isPressed()) {
                  continue;
               }
            }
         }

      }
   }

   public void cancelOriginal() {
      this.cancelOriginal = true;
   }

   public void sendToServer(String identifier) {
      if (identifier != null && !identifier.equals("")) {
         this.queueToServer.add(identifier);
      } else {
         throw new IllegalArgumentException("Identifier cannot be null or empty!");
      }
   }

   public ControlEvent setPlayer(EntityPlayerMP player) {
      this.player = player;
      return this;
   }

   static {
      try {
         if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            keybinds = (Map)ReflectionTool.getField(KeyBinding.class, "KEYBIND_ARRAY", "KEYBIND_ARRAY").get((Object)null);
            updateStatesAndReportChanged();
            MinecraftForge.EVENT_BUS.register(ControlEvent.class);
         }
      } catch (IllegalAccessException var1) {
         MCTools.crash(var1, true);
      }

   }
}
