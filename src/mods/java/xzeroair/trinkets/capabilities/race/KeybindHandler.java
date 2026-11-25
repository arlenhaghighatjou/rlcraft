package xzeroair.trinkets.capabilities.race;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.client.keybinds.KeyHandler;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IMovementAbility;

public class KeybindHandler {
   private Map<String, KeyHandler> storage = new HashMap();

   public KeybindHandler() {
      this.addKeyBind(KeybindHandler.keyEnum.LEFT.getName());
      this.addKeyBind(KeybindHandler.keyEnum.RIGHT.getName());
      this.addKeyBind(KeybindHandler.keyEnum.FORWARD.getName());
      this.addKeyBind(KeybindHandler.keyEnum.BACK.getName());
      this.addKeyBind(KeybindHandler.keyEnum.JUMP.getName());
      this.addKeyBind(KeybindHandler.keyEnum.SNEAK.getName());
   }

   public void addKeyBind(String key) {
      if (!this.storage.containsKey(key)) {
         this.storage.put(key, new KeyHandler());
      }

   }

   public KeyHandler getKeyHandler(String key) {
      if (this.storage.containsKey(key)) {
         return (KeyHandler)this.storage.get(key);
      } else {
         KeyHandler kh = new KeyHandler();
         return (KeyHandler)this.storage.put(key, kh);
      }
   }

   public boolean pressKey(Entity entity, String key, int state) {
      KeybindHandler.keyEnum KEY = KeybindHandler.keyEnum.byName(key);
      switch(KEY) {
      case LEFT:
         return this.left(entity, state);
      case RIGHT:
         return this.right(entity, state);
      case FORWARD:
         return this.forward(entity, state);
      case BACK:
         return this.back(entity, state);
      case JUMP:
         return this.jump(entity, state);
      case SNEAK:
         return this.sneak(entity, state);
      default:
         return false;
      }
   }

   public boolean left(Entity entity, int state) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (properties, bool) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = properties.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = holder.getAbility();
               if (ability instanceof IMovementAbility) {
                  boolean cancel = ((IMovementAbility)ability).left(entity, state);
                  if (!cancel) {
                     return false;
                  }
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

         return bool;
      })).booleanValue();
   }

   public boolean right(Entity entity, int state) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (properties, bool) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = properties.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = holder.getAbility();
               if (ability instanceof IMovementAbility) {
                  boolean cancel = ((IMovementAbility)ability).right(entity, state);
                  if (!cancel) {
                     return false;
                  }
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

         return bool;
      })).booleanValue();
   }

   public boolean forward(Entity entity, int state) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (properties, bool) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = properties.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = holder.getAbility();
               if (ability instanceof IMovementAbility) {
                  boolean cancel = ((IMovementAbility)ability).forward(entity, state);
                  if (!cancel) {
                     return false;
                  }
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

         return bool;
      })).booleanValue();
   }

   public boolean back(Entity entity, int state) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (properties, bool) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = properties.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = holder.getAbility();
               if (ability instanceof IMovementAbility) {
                  boolean cancel = ((IMovementAbility)ability).back(entity, state);
                  if (!cancel) {
                     return false;
                  }
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

         return bool;
      })).booleanValue();
   }

   public boolean jump(Entity entity, int state) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (properties, bool) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = properties.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = holder.getAbility();
               if (ability instanceof IMovementAbility) {
                  boolean cancel = ((IMovementAbility)ability).jump(entity, state);
                  if (!cancel) {
                     return false;
                  }
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

         return bool;
      })).booleanValue();
   }

   public boolean sneak(Entity entity, int state) {
      return ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (properties, bool) -> {
         Map<String, AbilityHandler.AbilityHolder> abilities = properties.getAbilityHandler().getActiveAbilities();

         for(Entry<String, AbilityHandler.AbilityHolder> entry : abilities.entrySet()) {
            String key = (String)entry.getKey();
            AbilityHandler.AbilityHolder holder = (AbilityHandler.AbilityHolder)entry.getValue();

            try {
               IAbilityInterface ability = holder.getAbility();
               if (ability instanceof IMovementAbility) {
                  boolean cancel = ((IMovementAbility)ability).sneak(entity, state);
                  if (!cancel) {
                     return false;
                  }
               }
            } catch (Exception var11) {
               Trinkets.log.error("Trinkets had an Error with Ability:" + key);
               var11.printStackTrace();
            }
         }

         return bool;
      })).booleanValue();
   }

   public static enum keyEnum {
      LEFT("Left"),
      RIGHT("Right"),
      FORWARD("Forward"),
      BACK("Backward"),
      JUMP("Jump"),
      SNEAK("Sneak"),
      NONE("None");

      private String name;
      private static final KeybindHandler.keyEnum[] ID = new KeybindHandler.keyEnum[values().length];

      private keyEnum(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static KeybindHandler.keyEnum byName(String name) {
         for(int i = 0; i < values().length; ++i) {
            if (byID(i).getName().contentEquals(name)) {
               return byID(i);
            }
         }

         return NONE;
      }

      public static KeybindHandler.keyEnum byID(int value) {
         if (value < 0 || value >= values().length) {
            value = 0;
         }

         return values()[value];
      }
   }
}
