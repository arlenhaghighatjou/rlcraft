package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.player.GuiPlayerSelectorHandler;
import com.creativemd.creativecore.common.utils.player.PlayerSelector;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;

public class SubGuiPlayerSelectorDialog extends SubGui {
   public final GuiPlayerSelectorButton button;
   public GuiPlayerSelectorHandler handler;

   public SubGuiPlayerSelectorDialog(GuiPlayerSelectorButton button) {
      super(150, 150);
      this.button = button;
   }

   public void createControls() {
      PlayerSelector selector = this.button.get();
      this.handler = GuiPlayerSelectorHandler.getHandler(selector);
      GuiComboBox box = (GuiComboBox)this.get("type");
      if (box != null) {
         this.handler = GuiPlayerSelectorHandler.getHandler(box.getCaption());
      }

      this.controls.clear();
      ArrayList<String> lines = new ArrayList(GuiPlayerSelectorHandler.getNames());
      box = new GuiComboBox("type", 0, 0, 144, lines);
      box.select(lines.indexOf(this.handler.getName()));
      this.controls.add(box);
      this.handler.createControls(this, selector);
      this.controls.add(new GuiButton("Cancel", 0, 130, 41) {
         public void onClicked(int x, int y, int button) {
            SubGuiPlayerSelectorDialog.this.closeLayer(new NBTTagCompound());
         }
      });
      this.controls.add(new GuiButton("Save", 103, 130, 41) {
         public void onClicked(int x, int y, int button) {
            PlayerSelector selector = SubGuiPlayerSelectorDialog.this.handler.parseSelector(SubGuiPlayerSelectorDialog.this);
            if (selector != null) {
               SubGuiPlayerSelectorDialog.this.button.set(selector);
               SubGuiPlayerSelectorDialog.this.closeLayer(new NBTTagCompound());
            }

         }
      });
   }

   @CustomEventSubscribe
   public void onChanged(GuiControlChangedEvent event) {
      if (event.source.is("type")) {
         this.createControls();
         this.refreshControls();
      } else {
         this.handler.onChanged(this, event);
      }

   }
}
