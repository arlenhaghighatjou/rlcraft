package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.sync.ConfigurationChangePacket;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiScrollBox;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.opener.GuiHandler;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.mc.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SubGuiConfig extends SubGui {
   public JsonObject ROOT = new JsonObject();
   public Side side;
   public final ICreativeConfigHolder rootHolder;
   public ICreativeConfigHolder holder;
   public boolean changed = false;
   public int nextAction;
   public boolean force;

   public SubGuiConfig(ICreativeConfigHolder holder, Side side) {
      super(420, 234);
      this.rootHolder = holder;
      this.holder = holder;
      this.side = side;
   }

   public void createControls() {
      this.loadHolder(this.holder);
   }

   public void savePage() {
      GuiScrollBox box = (GuiScrollBox)this.get("box");
      JsonObject parent = null;

      for(GuiControl control : box.controls) {
         if (control instanceof GuiConfigControl) {
            JsonElement element = ((GuiConfigControl)control).save();
            if (element != null) {
               if (parent == null) {
                  parent = JsonUtils.get(this.ROOT, this.holder.path());
               }

               parent.add(((GuiConfigControl)control).field.name, element);
            }
         }
      }

   }

   public void loadHolder(final ICreativeConfigHolder holder) {
      if (!this.controls.isEmpty()) {
         this.savePage();
         this.controls.clear();
      }

      this.controls.add(new GuiLabel("/" + String.join("/", holder.path()), 0, 2));
      if (holder != this.rootHolder) {
         this.controls.add(new GuiButton("back", 391, 0) {
            public void onClicked(int x, int y, int button) {
               SubGuiConfig.this.loadHolder(holder.parent());
            }
         });
      }

      this.holder = holder;
      GuiScrollBox box = new GuiScrollBox("box", 0, 21, 414, 186);
      this.controls.add(box);
      JsonObject json = JsonUtils.tryGet(this.ROOT, holder.path());
      int offsetX = 1;
      int offsetY = 1;

      for(ConfigKey key : holder.fields()) {
         if (!key.requiresRestart) {
            final Object value = key.get();
            String caption = translateOrDefault("config." + String.join(".", holder.path()) + "." + key.name + ".name", key.name);
            String comment = "config." + String.join(".", holder.path()) + "." + key.name + ".comment";
            if (value instanceof ICreativeConfigHolder) {
               if (!((ICreativeConfigHolder)value).isEmpty(this.side)) {
                  box.addControl((new GuiButton(caption, offsetX, offsetY, 100) {
                     public void onClicked(int x, int y, int button) {
                        SubGuiConfig.this.loadHolder((ICreativeConfigHolder)value);
                     }
                  }).setLangTooltip(comment));
                  offsetY += 21;
               }
            } else if (key.is(this.side)) {
               GuiLabel label = new GuiLabel(caption + ":", offsetX, offsetY + 2);
               final GuiConfigControl config = new GuiConfigControl((ConfigKey.ConfigKeyField)key, 0, offsetY, 100, 14, this.side);
               GuiButton resetButton = new GuiButton("r", offsetX + 390, offsetY, 14) {
                  public void onClicked(int x, int y, int button) {
                     config.reset();
                     SubGuiConfig.this.changed = true;
                  }
               };
               int labelWidth = label.width;
               config.posX = label.posX + labelWidth + 2;
               config.width = 380 - config.posX;
               config.init(json != null ? json.get(key.name) : null);
               box.addControl(label.setLangTooltip(comment));
               box.addControl(config);
               box.addControl(resetButton.setCustomTooltip(new String[]{"reset to default"}));
               config.setResetButton(resetButton);
               offsetY += config.height + 1;
            }
         }
      }

      this.controls.add(new GuiButton("cancel", 0, 214) {
         public void onClicked(int x, int y, int button) {
            SubGuiConfig.this.nextAction = 0;
            SubGuiConfig.this.closeGui();
         }
      });
      if (this.side == Side.SERVER) {
         this.controls.add(new GuiButton("client-config", 40, 214) {
            public void onClicked(int x, int y, int button) {
               SubGuiConfig.this.nextAction = 1;
               SubGuiConfig.this.closeGui();
            }
         });
      }

      this.controls.add(new GuiButton("save", 390, 214) {
         public void onClicked(int x, int y, int button) {
            SubGuiConfig.this.nextAction = 0;
            SubGuiConfig.this.savePage();
            SubGuiConfig.this.sendUpdate();
            SubGuiConfig.this.force = true;
            SubGuiConfig.this.closeGui();
         }
      });
      this.refreshControls();
   }

   public void sendUpdate() {
      if (this.side == Side.SERVER) {
         PacketHandler.sendPacketToServer(new ConfigurationChangePacket(this.rootHolder, this.ROOT));
      } else {
         this.rootHolder.load(false, true, JsonUtils.get(this.ROOT, this.rootHolder.path()), Side.CLIENT);
         CreativeCore.configHandler.save(Side.CLIENT);
      }

   }

   public void onDialogClosed(String text, String[] buttons, String clicked) {
      if (clicked.equals("Yes")) {
         this.savePage();
         this.sendUpdate();
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
         GuiHandler.openGui("clientconfig", new NBTTagCompound());
      }

   }

   @CustomEventSubscribe
   public void changed(GuiControlChangedEvent event) {
      GuiConfigControl config = getConfigControl((GuiControl)event.source);
      if (config != null) {
         this.changed = true;
         config.changed();
      }

   }

   private static GuiConfigControl getConfigControl(GuiControl control) {
      if (control instanceof GuiConfigControl) {
         return (GuiConfigControl)control;
      } else {
         return control.parent != null ? getConfigControl((GuiControl)control.parent) : null;
      }
   }
}
