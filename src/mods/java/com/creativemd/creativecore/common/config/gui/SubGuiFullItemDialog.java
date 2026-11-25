package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabelClickable;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;
import com.creativemd.creativecore.common.utils.stack.GuiInfoHandler;
import com.creativemd.creativecore.common.utils.stack.InfoStack;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;

public class SubGuiFullItemDialog extends SubGui {
   public static List<InfoStack> latest = new ArrayList();
   public final GuiInfoStackButton button;
   public boolean supportStackSize;
   public GuiInfoHandler handler;

   public SubGuiFullItemDialog(GuiInfoStackButton button, boolean supportStackSize) {
      super(150, 230);
      this.button = button;
      this.supportStackSize = supportStackSize;
   }

   public void createControls() {
      InfoStack info = this.button.get();
      this.handler = GuiInfoHandler.getHandler(info);
      GuiComboBox box = (GuiComboBox)this.get("type");
      if (box != null) {
         this.handler = GuiInfoHandler.getHandler(box.getCaption());
      }

      this.controls.clear();
      ArrayList<String> lines = new ArrayList(GuiInfoHandler.getNames());
      box = new GuiComboBox("type", 0, 0, 144, lines);
      box.select(lines.indexOf(this.handler.getName()));
      this.controls.add(box);
      this.handler.createControls(this, info);
      if (this.supportStackSize) {
         this.controls.add(new GuiLabel("StackSize:", 5, 210));
         GuiTextfield field = (new GuiTextfield("stacksize", "1", 110, 208, 30, 14)).setNumbersOnly();
         if (info != null) {
            field.text = "" + info.stackSize;
         }

         this.controls.add(field);
      }

      GuiScrollBox scroll = new GuiScrollBox("latest", 0, 155, 144, this.supportStackSize ? 45 : 65);
      int latestPerRow = 4;

      for(int i = 0; i < latest.size(); ++i) {
         int row = i / latestPerRow;
         int cell = i - row * latestPerRow;
         GuiAvatarLabelClickable avatar = new GuiAvatarLabelClickable("" + i, cell * 32, row * 18, -1, new AvatarItemStack(((InfoStack)latest.get(i)).getItemStack())) {
            public void onClicked(int x, int y, int button) {
               SubGuiFullItemDialog.this.button.set((InfoStack)SubGuiFullItemDialog.latest.get(Integer.parseInt(this.name)));
               SubGuiFullItemDialog.this.closeLayer(new NBTTagCompound());
            }
         };
         scroll.addControl(avatar);
      }

      this.controls.add(scroll);
      this.controls.add(new GuiButton("Cancel", 0, 130, 41) {
         public void onClicked(int x, int y, int button) {
            SubGuiFullItemDialog.this.closeLayer(new NBTTagCompound());
         }
      });
      this.controls.add(new GuiButton("Save", 100, 130, 41) {
         public void onClicked(int x, int y, int button) {
            int stackSize = 0;

            try {
               if (SubGuiFullItemDialog.this.supportStackSize) {
                  stackSize = Integer.parseInt(((GuiTextfield)SubGuiFullItemDialog.this.get("stacksize")).text);
               } else {
                  stackSize = 1;
               }
            } catch (Exception var6) {
               stackSize = 1;
            }

            InfoStack info = SubGuiFullItemDialog.this.handler.parseInfo(SubGuiFullItemDialog.this, stackSize);
            if (info != null) {
               SubGuiFullItemDialog.this.button.set(info);
               if (!SubGuiFullItemDialog.latest.contains(info)) {
                  SubGuiFullItemDialog.latest.add(0, info.copy());
               }

               SubGuiFullItemDialog.this.closeLayer(new NBTTagCompound());
            }

         }
      });
   }

   @CustomEventSubscribe
   public void onToolTip(GuiToolTipEvent event) {
      if (event.source.is("stacksize")) {
         event.tooltip.add("0: no consumption");
         event.tooltip.add("1: normal");
      }

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
