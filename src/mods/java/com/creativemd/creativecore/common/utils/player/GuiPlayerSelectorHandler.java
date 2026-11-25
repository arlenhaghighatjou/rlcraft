package com.creativemd.creativecore.common.utils.player;

import com.creativemd.creativecore.common.config.gui.GuiPlayerSelectorButton;
import com.creativemd.creativecore.common.config.gui.SubGuiPlayerSelectorDialog;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiPlayerSelectorHandler<T extends PlayerSelector> {
   @SideOnly(Side.CLIENT)
   private static LinkedHashMap<String, GuiPlayerSelectorHandler> handlers = new LinkedHashMap();
   public static GuiPlayerSelectorHandler defaultHandler;
   private String name;

   public GuiPlayerSelectorHandler() {
   }

   public static void registerGuiSelectorHandler(String name, GuiPlayerSelectorHandler handler) {
      handler.name = name;
      handlers.put(name, handler);
   }

   public static Set<String> getNames() {
      return handlers.keySet();
   }

   public static GuiPlayerSelectorHandler getHandler(PlayerSelector selector) {
      GuiPlayerSelectorHandler handler = selector != null ? (GuiPlayerSelectorHandler)handlers.get(PlayerSelector.get(selector.getClass())) : null;
      return handler != null ? handler : defaultHandler;
   }

   public static GuiPlayerSelectorHandler getHandler(String name) {
      GuiPlayerSelectorHandler handler = (GuiPlayerSelectorHandler)handlers.get(name);
      return handler == null ? defaultHandler : handler;
   }

   public String getName() {
      return this.name;
   }

   public abstract void createControls(SubGuiPlayerSelectorDialog var1, PlayerSelector var2);

   public abstract T parseSelector(SubGuiPlayerSelectorDialog var1);

   public void onChanged(SubGuiPlayerSelectorDialog gui, GuiControlChangedEvent event) {
   }

   static {
      registerGuiSelectorHandler("and", new GuiPlayerSelectorHandler.GuiPlayerSelectorHandlerMultiple<PlayerSelector.PlayerSelectorAnd>() {
         public PlayerSelector.PlayerSelectorAnd parseSelector(PlayerSelector[] selectors) {
            return new PlayerSelector.PlayerSelectorAnd(selectors);
         }
      });
      registerGuiSelectorHandler("or", new GuiPlayerSelectorHandler.GuiPlayerSelectorHandlerMultiple<PlayerSelector.PlayerSelectorOr>() {
         public PlayerSelector.PlayerSelectorOr parseSelector(PlayerSelector[] selectors) {
            return new PlayerSelector.PlayerSelectorOr(selectors);
         }
      });
      registerGuiSelectorHandler("not", new GuiPlayerSelectorHandler<PlayerSelector.PlayerSelectorNot>() {
         public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
            gui.addControl(new GuiPlayerSelectorButton("not", 0, 30, 100, 10, (PlayerSelector)(selector instanceof PlayerSelector.PlayerSelectorNot ? ((PlayerSelector.PlayerSelectorNot)selector).selector : new PlayerSelector.PlayerSelectorLevel(0))));
         }

         public PlayerSelector.PlayerSelectorNot parseSelector(SubGuiPlayerSelectorDialog gui) {
            GuiPlayerSelectorButton button = (GuiPlayerSelectorButton)gui.get("not");
            PlayerSelector selector = button.get();
            return selector != null ? new PlayerSelector.PlayerSelectorNot(selector) : null;
         }
      });
      registerGuiSelectorHandler("level", new GuiPlayerSelectorHandler<PlayerSelector.PlayerSelectorLevel>() {
         public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
            gui.addControl((new GuiTextfield("content", selector instanceof PlayerSelector.PlayerSelectorLevel ? "" + ((PlayerSelector.PlayerSelectorLevel)selector).permissionLevel : "0", 0, 30, 30, 10)).setNumbersOnly());
         }

         public PlayerSelector.PlayerSelectorLevel parseSelector(SubGuiPlayerSelectorDialog gui) {
            GuiTextfield text = (GuiTextfield)gui.get("content");
            return new PlayerSelector.PlayerSelectorLevel(text.parseInteger());
         }
      });
      registerGuiSelectorHandler("mode", new GuiPlayerSelectorHandler<PlayerSelector.PlayerSelectorGamemode>() {
         public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
            gui.addControl(new GuiStateButton("mode", selector instanceof PlayerSelector.PlayerSelectorGamemode ? ((PlayerSelector.PlayerSelectorGamemode)selector).type.getID() : 0, 0, 30, new String[]{"survival", "creative", "adventure", "spectator"}));
         }

         public PlayerSelector.PlayerSelectorGamemode parseSelector(SubGuiPlayerSelectorDialog gui) {
            GuiStateButton mode = (GuiStateButton)gui.get("mode");
            return new PlayerSelector.PlayerSelectorGamemode(GameType.getByID(mode.getState()));
         }
      });
      registerGuiSelectorHandler("command", new GuiPlayerSelectorHandler<PlayerSelector.PlayerSelectorCommand>() {
         public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
            gui.addControl(new GuiTextfield("content", selector instanceof PlayerSelector.PlayerSelectorCommand ? "" + ((PlayerSelector.PlayerSelectorCommand)selector).command : "tell", 0, 30, 100, 10));
         }

         public PlayerSelector.PlayerSelectorCommand parseSelector(SubGuiPlayerSelectorDialog gui) {
            GuiTextfield text = (GuiTextfield)gui.get("content");
            return text.text.isEmpty() ? null : new PlayerSelector.PlayerSelectorCommand(text.text);
         }
      });
      registerGuiSelectorHandler("selector", new GuiPlayerSelectorHandler<PlayerSelector.PlayerSelectorCommandSelector>() {
         public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
            gui.addControl(new GuiTextfield("content", selector instanceof PlayerSelector.PlayerSelectorCommandSelector ? "" + ((PlayerSelector.PlayerSelectorCommandSelector)selector).pattern : "@a[]", 0, 30, 100, 10));
         }

         public PlayerSelector.PlayerSelectorCommandSelector parseSelector(SubGuiPlayerSelectorDialog gui) {
            GuiTextfield text = (GuiTextfield)gui.get("content");
            return text.text.isEmpty() ? null : new PlayerSelector.PlayerSelectorCommandSelector(text.text);
         }
      });
   }

   public abstract static class GuiPlayerSelectorHandlerMultiple<T extends PlayerSelector> extends GuiPlayerSelectorHandler<T> {
      public GuiPlayerSelectorHandlerMultiple() {
      }

      public PlayerSelector[] getChildren(PlayerSelector selector) {
         if (selector instanceof PlayerSelector.PlayerSelectorAnd) {
            return ((PlayerSelector.PlayerSelectorAnd)selector).selectors;
         } else {
            return selector instanceof PlayerSelector.PlayerSelectorOr ? ((PlayerSelector.PlayerSelectorOr)selector).selectors : new PlayerSelector[]{selector};
         }
      }

      public void createControls(SubGuiPlayerSelectorDialog gui, PlayerSelector selector) {
         PlayerSelector[] selectors = this.getChildren(selector);
         List<GuiPlayerSelectorButton> buttons = new ArrayList();
         if (selectors != null) {
            for(int i = 0; i < selectors.length; ++i) {
               buttons.add(new GuiPlayerSelectorButton("" + i, 0, 30, 100, 10, selectors[i]));
            }
         }

         final GuiListBoxBase<GuiPlayerSelectorButton> list = new GuiListBoxBase<GuiPlayerSelectorButton>("list", 0, 21, 144, 100, true, buttons);
         gui.addControl(list);
         gui.addControl(new GuiButton("add", 50, 130) {
            public void onClicked(int x, int y, int button) {
               list.add(new GuiPlayerSelectorButton("new", 0, 0, 100, 10, new PlayerSelector.PlayerSelectorLevel(0)));
            }
         });
      }

      public T parseSelector(SubGuiPlayerSelectorDialog gui) {
         GuiListBoxBase<GuiPlayerSelectorButton> list = (GuiListBoxBase)gui.get("list");
         PlayerSelector[] selectors = new PlayerSelector[list.size()];

         for(int i = 0; i < selectors.length; ++i) {
            selectors[i] = ((GuiPlayerSelectorButton)list.get(i)).get();
         }

         return (T)(selectors.length > 0 ? this.parseSelector(selectors) : null);
      }

      public abstract T parseSelector(PlayerSelector[] var1);
   }
}
