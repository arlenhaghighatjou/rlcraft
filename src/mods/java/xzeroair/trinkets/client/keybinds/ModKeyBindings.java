package xzeroair.trinkets.client.keybinds;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

@SideOnly(Side.CLIENT)
public class ModKeyBindings {
   public static KeyBinding TRINKET_GUI = new KeyBinding("key.xat.trinket_gui.desc", 25, "key.xat.category");
   public static KeyBinding DRAGONS_EYE_TARGET = new KeyBinding("key.xat.trinket_target.desc", KeyConflictContext.IN_GAME, 25, "key.xat.category");
   public static KeyBinding DRAGONS_EYE_ABILITY = new KeyBinding("key.xat.trinket_toggle_effect.desc", KeyConflictContext.IN_GAME, 43, "key.xat.category");
   public static KeyBinding POLARIZED_STONE_ABILITY = new KeyBinding("key.xat.magnet_toggle_effect.desc", KeyConflictContext.IN_GAME, 43, "key.xat.category");
   public static KeyBinding AUX_KEY = new KeyBinding("key.xat.aux_key.desc", KeyConflictContext.IN_GAME, 29, "key.xat.category");
   public static KeyBinding ARCING_ORB_ABILITY = new KeyBinding("key.xat.trinket_arcing_attack.desc", KeyConflictContext.IN_GAME, 19, "key.xat.category");
   public static KeyBinding RACE_ABILITY = new KeyBinding("key.xat.race_ability.desc", KeyConflictContext.IN_GAME, 19, "key.xat.category");

   public ModKeyBindings() {
   }

   public static void init() {
      ClientRegistry.registerKeyBinding(TRINKET_GUI);
      ClientRegistry.registerKeyBinding(DRAGONS_EYE_TARGET);
      ClientRegistry.registerKeyBinding(DRAGONS_EYE_ABILITY);
      ClientRegistry.registerKeyBinding(POLARIZED_STONE_ABILITY);
      ClientRegistry.registerKeyBinding(ARCING_ORB_ABILITY);
      ClientRegistry.registerKeyBinding(AUX_KEY);
      ClientRegistry.registerKeyBinding(RACE_ABILITY);
   }

   public static boolean isKeyDownFromName(String key) {
      if (key != null && !key.isEmpty()) {
         boolean isKeysDown = false;

         try {
            String[] MultiCheck = key.split("\\+");

            for(String s : MultiCheck) {
               s = s.trim();
               if (s.equalsIgnoreCase("shift")) {
                  int lShift = Keyboard.getKeyIndex("LSHIFT");
                  int rShift = Keyboard.getKeyIndex("RSHIFT");
                  if (!Keyboard.isKeyDown(lShift) && !Keyboard.isKeyDown(rShift)) {
                     isKeysDown = false;
                     return false;
                  }

                  isKeysDown = true;
               } else if (s.equalsIgnoreCase("ctrl")) {
                  int lCtrl = Keyboard.getKeyIndex("LCONTROL");
                  int rCtrl = Keyboard.getKeyIndex("RCONTROL");
                  if (!Keyboard.isKeyDown(lCtrl) && !Keyboard.isKeyDown(rCtrl)) {
                     isKeysDown = false;
                     return false;
                  }

                  isKeysDown = true;
               } else if (s.equalsIgnoreCase("alt")) {
                  int lCtrl = Keyboard.getKeyIndex("LMENU");
                  int rCtrl = Keyboard.getKeyIndex("RMENU");
                  if (!Keyboard.isKeyDown(lCtrl) && !Keyboard.isKeyDown(rCtrl)) {
                     isKeysDown = false;
                     return false;
                  }

                  isKeysDown = true;
               } else {
                  int keyCode = Keyboard.getKeyIndex(s);
                  if (keyCode == 0) {
                     return false;
                  }

                  if (!Keyboard.isKeyDown(keyCode)) {
                     isKeysDown = false;
                     return false;
                  }

                  isKeysDown = true;
               }
            }
         } catch (Exception var9) {
            ;
         }

         return isKeysDown;
      } else {
         return false;
      }
   }
}
