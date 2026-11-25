package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.sync.ConfigurationClientPacket;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiCheckBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.type.CheckTree;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class SubGuiClientSync extends SubGui {
   public final CheckTree<ConfigKey> tree;
   public final ICreativeConfigHolder root;
   public CheckTree<ConfigKey>.CheckTreeEntry currentView;
   public boolean changed = false;
   public int nextAction;
   public boolean force;

   public SubGuiClientSync(ICreativeConfigHolder holder) {
      super(200, 200);
      this.root = holder;
      BiConsumer<ConfigKey, Boolean> setter = (x, y) -> {
         x.forceSynchronization = y.booleanValue();
      };
      Function<ConfigKey, Boolean> getter = (x) -> {
         return x.forceSynchronization;
      };
      Function<ConfigKey, Collection<? extends ConfigKey>> getChildren = (x) -> {
         if (!(x.getDefault() instanceof ICreativeConfigHolder)) {
            return null;
         } else {
            List<ConfigKey> keys = new ArrayList();

            for(ConfigKey key : ((ICreativeConfigHolder)x.getDefault()).fields()) {
               if (key.isWithoutForce(Side.CLIENT)) {
                  Object object = key.get();
                  if (!(object instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder)object).isEmptyWithoutForce(Side.CLIENT)) {
                     keys.add(key);
                  }
               }
            }

            return keys;
         }
      };
      List<ConfigKey> keys = new ArrayList();

      for(ConfigKey key : holder.fields()) {
         if (key.isWithoutForce(Side.CLIENT)) {
            Object object = key.get();
            if (!(object instanceof ICreativeConfigHolder) || !((ICreativeConfigHolder)object).isEmptyWithoutForce(Side.CLIENT)) {
               keys.add(key);
            }
         }
      }

      this.tree = new CheckTree<ConfigKey>(keys, setter, getter, getChildren);
      this.currentView = this.tree.root;
   }

   public void createControls() {
      this.load(this.currentView);
   }

   public void save() {
      PacketHandler.sendPacketToServer(new ConfigurationClientPacket(this.root, this.tree));
   }

   public void load(final CheckTree<ConfigKey>.CheckTreeEntry entry) {
      if (!this.controls.isEmpty()) {
         this.controls.clear();
      }

      ICreativeConfigHolder holder = entry.content == null ? this.root : (ICreativeConfigHolder)((ConfigKey)entry.content).get();
      this.controls.add(new GuiLabel("/" + String.join("/", holder.path()), 0, 0));
      if (entry.parent != null) {
         this.controls.add(new GuiButton("back", 170, 0) {
            public void onClicked(int x, int y, int button) {
               SubGuiClientSync.this.load(entry.parent);
            }
         });
      }

      this.currentView = entry;
      GuiScrollBox box = new GuiScrollBox("box", 0, 21, 194, 152);
      this.controls.add(box);
      int offsetX = 20;
      int offsetY = 1;

      for(final CheckTree<ConfigKey>.CheckTreeEntry key : this.currentView.children) {
         box.addControl(new SubGuiClientSync.GuiTreeCheckBox(key, 2, offsetY + 3));
         String caption = translateOrDefault("config." + String.join(".", holder.path() + "." + ((ConfigKey)key.content).name + ".name"), ((ConfigKey)key.content).name);
         String comment = "config." + String.join(".", holder.path()) + "." + ((ConfigKey)key.content).name + ".comment";
         if (key.content != null && ((ConfigKey)key.content).get() instanceof ICreativeConfigHolder) {
            box.addControl((new GuiButton(caption, offsetX, offsetY) {
               public void onClicked(int x, int y, int button) {
                  SubGuiClientSync.this.load(key);
               }
            }).setLangTooltip(comment));
            offsetY += 21;
         } else {
            GuiLabel label = new GuiLabel(caption, offsetX, offsetY + 2);
            box.addControl(label.setLangTooltip(comment));
            offsetY += label.height + 1;
         }
      }

      this.controls.add(new GuiButton("cancel", 0, 180) {
         public void onClicked(int x, int y, int button) {
            SubGuiClientSync.this.nextAction = 0;
            SubGuiClientSync.this.closeGui();
         }
      });
      this.controls.add(new GuiButton("config", 40, 180) {
         public void onClicked(int x, int y, int button) {
            SubGuiClientSync.this.nextAction = 1;
            SubGuiClientSync.this.closeGui();
         }
      });
      this.controls.add(new GuiButton("save", 170, 180) {
         public void onClicked(int x, int y, int button) {
            SubGuiClientSync.this.nextAction = 0;
            SubGuiClientSync.this.force = true;
            SubGuiClientSync.this.save();
            SubGuiClientSync.this.closeGui();
         }
      });
      this.refreshControls();
   }

   public void onDialogClosed(String text, String[] buttons, String clicked) {
      if (clicked.equals("Yes")) {
         this.save();
      }

      if (!clicked.equals("Cancel")) {
         this.force = true;
         this.closeGui();
      }

   }

   public void closeGui() {
      if (!this.force && this.changed) {
         this.openButtonDialogDialog("Do you want to save your changes?", new String[]{"Yes", "No", "Cancel"});
      } else if (this.nextAction == 0) {
         super.closeGui();
      } else if (this.nextAction == 1) {
         GuiHandler.openGui("config", new NBTTagCompound());
      }

   }

   @CustomEventSubscribe
   public void changed(GuiControlChangedEvent event) {
      this.changed = true;
      if (event.source instanceof SubGuiClientSync.GuiTreeCheckBox && !((SubGuiClientSync.GuiTreeCheckBox)event.source).value) {
         ((SubGuiClientSync.GuiTreeCheckBox)event.source).partial = ((SubGuiClientSync.GuiTreeCheckBox)event.source).entry.isChildEnabled();
      }

   }

   public static class GuiTreeCheckBox extends GuiCheckBox {
      public final CheckTree<ConfigKey>.CheckTreeEntry entry;

      public GuiTreeCheckBox(CheckTree<ConfigKey>.CheckTreeEntry entry, int x, int y) {
         super(((ConfigKey)entry.content).name, "", x, y, entry.isEnabled());
         this.entry = entry;
         if (!this.value) {
            this.partial = entry.isChildEnabled();
         }

      }

      public boolean mousePressed(int posX, int posY, int button) {
         playSound(SoundEvents.UI_BUTTON_CLICK);
         this.value = !this.value;
         if (this.value) {
            this.entry.enable();
         } else {
            this.entry.disable();
         }

         this.raiseEvent(new GuiControlChangedEvent(this));
         return true;
      }
   }
}
