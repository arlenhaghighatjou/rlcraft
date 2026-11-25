package vazkii.quark.base.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import vazkii.quark.vanity.client.emotes.EmoteDescriptor;
import vazkii.quark.vanity.client.emotes.EmoteHandler;

public class ModKeybinds {
   public static final HashMap<KeyBinding, String> emoteKeys = new HashMap();
   public static final BiMap<KeyBinding, IParentedGui> keyboundButtons = HashBiMap.create();
   public static KeyBinding lockKey = null;
   public static KeyBinding autoJumpKey = null;
   public static KeyBinding changeHotbarKey = null;
   public static KeyBinding dropoffKey = null;
   public static KeyBinding playerSortKey = null;
   public static KeyBinding chestSortKey = null;
   public static KeyBinding chestDropoffKey = null;
   public static KeyBinding chestMergeKey = null;
   public static KeyBinding chestRestockKey = null;
   public static KeyBinding chestExtractKey = null;
   private static final String MISC_GROUP = "quark.gui.keygroupMisc";
   private static final String INV_GROUP = "quark.gui.keygroupInv";
   private static final String EMOTE_GROUP = "quark.gui.keygroupEmote";

   public ModKeybinds() {
   }

   public static void initEmoteKeybinds() {
      for(String emoteName : EmoteHandler.emoteMap.keySet()) {
         EmoteDescriptor desc = (EmoteDescriptor)EmoteHandler.emoteMap.get(emoteName);
         KeyBinding key = init(desc.getTranslationKey(), 0, "quark.gui.keygroupEmote", false);
         emoteKeys.put(key, emoteName);
      }

   }

   public static void initLockKey() {
      lockKey = init("lockBuilding", 37, "quark.gui.keygroupMisc");
   }

   public static void initAutoJumpKey() {
      autoJumpKey = init("toggleAutojump", 48, "quark.gui.keygroupMisc");
   }

   public static void initChangeHotbarKey() {
      changeHotbarKey = init("changeHotbar", 44, "quark.gui.keygroupMisc");
   }

   public static void initDropoffKey() {
      dropoffKey = initAndButtonBind("dropoff", 0);
   }

   public static void initPlayerSortingKey() {
      playerSortKey = initAndButtonBind("playerSort", 0);
   }

   public static void initChestKeys() {
      chestSortKey = initAndButtonBind("chestSort", 0);
      chestDropoffKey = initAndButtonBind("chestDropoff", 0);
      chestMergeKey = initAndButtonBind("chestMerge", 0);
      chestRestockKey = initAndButtonBind("chestRestock", 0);
      chestExtractKey = initAndButtonBind("chestExtract", 0);
   }

   public static void keybindButton(KeyBinding key, IParentedGui ipg) {
      if (key != null) {
         keyboundButtons.put(key, ipg);
      }

   }

   private static KeyBinding initAndButtonBind(String s, int key) {
      KeyBinding kb = init(s, key, "quark.gui.keygroupInv");
      new ModKeybinds.KeybindButtonHandler(kb);
      return kb;
   }

   private static KeyBinding init(String s, int key, String group) {
      return init(s, key, group, true);
   }

   private static KeyBinding init(String s, int key, String group, boolean prefix) {
      KeyBinding kb = new KeyBinding(prefix ? "quark.keybind." + s : s, key, group);
      ClientRegistry.registerKeyBinding(kb);
      return kb;
   }

   public static boolean isKeyDown(KeyBinding keybind) {
      int key = keybind.getKeyCode();
      if (key == 0) {
         return false;
      } else if (key < 0) {
         int button = 100 + key;
         return Mouse.isButtonDown(button);
      } else {
         try {
            return Keyboard.isKeyDown(key);
         } catch (IndexOutOfBoundsException var3) {
            return false;
         }
      }
   }

   private static class KeybindButtonHandler {
      private final KeyBinding ref;
      private boolean down;

      public KeybindButtonHandler(KeyBinding ref) {
         this.ref = ref;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void onKeyInput(Post event) {
         this.updateInput();
      }

      @SubscribeEvent
      public void onMouseInput(net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Post event) {
         this.updateInput();
      }

      public void updateInput() {
         boolean wasDown = this.down;
         this.down = ModKeybinds.isKeyDown(this.ref);
         if (!wasDown && this.down && ModKeybinds.keyboundButtons.containsKey(this.ref)) {
            IParentedGui ipg = (IParentedGui)ModKeybinds.keyboundButtons.get(this.ref);
            GuiScreen curr = Minecraft.getMinecraft().currentScreen;
            if (curr == ipg.getParent()) {
               GuiButton button = (GuiButton)ipg;
               if (button.enabled && button.visible) {
                  List<GuiButton> buttonList = (List)ObfuscationReflectionHelper.getPrivateValue(GuiScreen.class, curr, "buttonList");
                  Pre postEvent = new Pre(curr, button, buttonList);
                  MinecraftForge.EVENT_BUS.post(postEvent);
               }
            }
         }

      }
   }
}
