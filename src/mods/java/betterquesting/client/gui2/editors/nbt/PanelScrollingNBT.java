package betterquesting.client.gui2.editors.nbt;

import betterquesting.api.misc.ICallback;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.callbacks.CallbackMulti;
import betterquesting.api2.client.gui.controls.callbacks.CallbackNBTPrimitive;
import betterquesting.api2.client.gui.controls.callbacks.CallbackNBTTagString;
import betterquesting.api2.client.gui.controls.filters.FieldFilterNumber;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.PanelEvent;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import betterquesting.client.gui2.editors.GuiTextEditor;
import betterquesting.client.gui2.editors.nbt.callback.NbtEntityCallback;
import betterquesting.client.gui2.editors.nbt.callback.NbtFluidCallback;
import betterquesting.client.gui2.editors.nbt.callback.NbtItemCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;

public class PanelScrollingNBT extends CanvasScrolling implements IPEventListener {
   private NBTBase nbt;
   private final int btnEdit;
   private final int btnAdv;
   private final int btnInsert;
   private final int btnDelete;
   private final Stack<NBTBase> nbtStack;
   private final Minecraft mc;

   public PanelScrollingNBT(IGuiRect rect, NBTTagCompound tag, int btnEdit, int btnAdv, int btnInsert, int btnDelete) {
      this(rect, btnEdit, btnAdv, btnInsert, btnDelete);
      this.setNBT(tag);
   }

   public PanelScrollingNBT(IGuiRect rect, NBTTagList tag, int btnEdit, int btnAdv, int btnInsert, int btnDelete) {
      this(rect, btnEdit, btnAdv, btnInsert, btnDelete);
      this.setNBT(tag);
   }

   private PanelScrollingNBT(IGuiRect rect, int btnEdit, int btnAdv, int btnInsert, int btnDelete) {
      super(rect);
      this.nbtStack = new Stack();
      this.mc = Minecraft.getMinecraft();
      this.btnEdit = btnEdit;
      this.btnAdv = btnAdv;
      this.btnInsert = btnInsert;
      this.btnDelete = btnDelete;
      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      Keyboard.enableRepeatEvents(true);
   }

   public PanelScrollingNBT setNBT(NBTTagCompound tag) {
      this.nbt = tag;
      this.refreshList();
      return this;
   }

   public PanelScrollingNBT setNBT(NBTTagList list) {
      this.nbt = list;
      this.refreshList();
      return this;
   }

   public void initPanel() {
      super.initPanel();
      this.refreshList();
   }

   private void refreshList() {
      this.resetCanvas();
      if (this.nbt != null) {
         int preSX = this.getScrollX();
         int preSY = this.getScrollY();
         int width = this.getTransform().getWidth();
         int lw = (int)((float)width / 3.0F);
         int rw = width - lw;
         if (this.nbt.getId() == 10) {
            NBTTagCompound tag = (NBTTagCompound)this.nbt;
            List<String> sortedKeys = new ArrayList(tag.getKeySet());
            Collections.sort(sortedKeys);
            Iterator<String> keys = sortedKeys.iterator();

            int i;
            for(i = 0; keys.hasNext(); ++i) {
               String k = (String)keys.next();
               NBTBase entry = tag.getTag(k);
               PanelTextBox name = (new PanelTextBox(new GuiRectangle(0, i * 16 + 4, lw - 8, 12, 0), k)).setAlignment(2);
               name.setColor(PresetColor.TEXT_MAIN.getColor());
               this.addPanel(name);
               if (entry.getId() == 10) {
                  PanelButtonStorage<String> btn = new PanelButtonStorage<String>(new GuiRectangle(lw, i * 16, rw - 48, 16, 0), this.btnEdit, this.getButtonTitle((NBTTagCompound)entry), k);
                  this.addPanel(btn);
                  btn = new PanelButtonStorage<String>(new GuiRectangle(width - 48, i * 16, 16, 16, 0), this.btnAdv, "...", k);
                  this.addPanel(btn);
               } else if (entry.getId() == 9) {
                  PanelButtonStorage<String> btn = new PanelButtonStorage<String>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), this.btnEdit, "List...", k);
                  this.addPanel(btn);
               } else if (entry.getId() == 8) {
                  PanelTextField<String> text = new PanelTextField<String>(new GuiRectangle(lw, i * 16, rw - 48, 16, 0), "" + ((NBTTagString)entry).getString(), FieldFilterString.INSTANCE);
                  text.setCallback(new CallbackNBTTagString(tag, k)).setMaxLength(Integer.MAX_VALUE);
                  this.addPanel(text);
                  PanelButtonStorage<String> btn = new PanelButtonStorage<String>(new GuiRectangle(width - 48, i * 16, 16, 16, 0), this.btnEdit, "Aa", k);
                  this.addPanel(btn);
               } else if (entry.getId() == 1) {
                  PanelTextField<Byte> text = new PanelTextField<Byte>(new GuiRectangle(lw, i * 16, rw / 2, 16, 0), "" + ((NBTPrimitive)entry).getByte(), FieldFilterNumber.BYTE);
                  text.setMaxLength(Integer.MAX_VALUE);
                  this.addPanel(text);
                  PanelButtonStorage<String> btn = new PanelButtonStorage<String>(new GuiRectangle(lw + rw / 2, i * 16, (int)Math.ceil((double)((float)rw / 2.0F)) - 32, 16, 0), this.btnEdit, ((NBTPrimitive)entry).getByte() > 0 ? "true" : "false", k);
                  this.addPanel(btn);
                  text.setMaxLength(Integer.MAX_VALUE).setCallback(new CallbackMulti(new ICallback[]{new CallbackNBTPrimitive(tag, k, Byte.class), (value) -> {
                     btn.setText(value.byteValue() > 0 ? "true" : "false");
                  }}));
                  btn.setCallback((value) -> {
                     boolean flag = tag.getByte(value) > 0;
                     tag.setByte(value, (byte)(flag ? 0 : 1));
                     text.setText(flag ? "0" : "1");
                     btn.setText(flag ? "false" : "true");
                  });
               } else if (entry.getId() > 1 && entry.getId() < 7) {
                  switch(entry.getId()) {
                  case 2:
                     PanelTextField<Short> text = new PanelTextField<Short>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getShort(), FieldFilterNumber.SHORT);
                     text.setCallback(new CallbackNBTPrimitive(tag, k, Short.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 3:
                     PanelTextField<Integer> text = new PanelTextField<Integer>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getInt(), FieldFilterNumber.INT);
                     text.setCallback(new CallbackNBTPrimitive(tag, k, Integer.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 4:
                     PanelTextField<Long> text = new PanelTextField<Long>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getLong(), FieldFilterNumber.LONG);
                     text.setCallback(new CallbackNBTPrimitive(tag, k, Long.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 5:
                     PanelTextField<Float> text = new PanelTextField<Float>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getFloat(), FieldFilterNumber.FLOAT);
                     text.setCallback(new CallbackNBTPrimitive(tag, k, Float.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 6:
                     PanelTextField<Double> text = new PanelTextField<Double>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getDouble(), FieldFilterNumber.DOUBLE);
                     text.setCallback(new CallbackNBTPrimitive(tag, k, Double.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                  }
               } else {
                  PanelTextBox err = (new PanelTextBox(new GuiRectangle(lw, i * 16 + 4, rw - 48, 12, 0), entry.getClass().getSimpleName() + " Not Supported Yet")).setAlignment(1);
                  err.setColor(PresetColor.TEXT_MAIN.getColor());
                  this.addPanel(err);
               }

               PanelButtonStorage<String> btnI = new PanelButtonStorage<String>(new GuiRectangle(width - 32, i * 16, 16, 16, 0), this.btnInsert, "+", k);
               btnI.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(0, 255, 0, 255), new GuiColorStatic(0, 255, 0, 255));
               this.addPanel(btnI);
               PanelButtonStorage<String> btnD = new PanelButtonStorage<String>(new GuiRectangle(width - 16, i * 16, 16, 16, 0), this.btnDelete, "x", k);
               btnD.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(255, 0, 0, 255), new GuiColorStatic(255, 0, 0, 255));
               this.addPanel(btnD);
            }

            this.addPanel(new PanelGeneric(new GuiRectangle(0, i * 16, width - 32, 16, 0), (IGuiTexture)null));
            PanelButtonStorage<String> btnI = new PanelButtonStorage<String>(new GuiRectangle(width - 32, i * 16, 16, 16, 0), this.btnInsert, "+", "");
            btnI.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(0, 255, 0, 255), new GuiColorStatic(0, 255, 0, 255));
            this.addPanel(btnI);
         } else if (this.nbt.getId() == 9) {
            NBTTagList list = (NBTTagList)this.nbt;

            int i;
            for(i = 0; i < list.tagCount(); ++i) {
               NBTBase entry = list.get(i);
               PanelTextBox name = (new PanelTextBox(new GuiRectangle(0, i * 16 + 4, lw - 8, 16, 0), "#" + i)).setAlignment(2);
               name.setColor(PresetColor.TEXT_MAIN.getColor());
               this.addPanel(name);
               if (entry.getId() == 10) {
                  PanelButtonStorage<Integer> btn = new PanelButtonStorage<Integer>(new GuiRectangle(lw, i * 16, rw - 48, 16, 0), this.btnEdit, this.getButtonTitle((NBTTagCompound)entry), i);
                  this.addPanel(btn);
                  btn = new PanelButtonStorage<Integer>(new GuiRectangle(width - 48, i * 16, 16, 16, 0), this.btnAdv, "...", i);
                  this.addPanel(btn);
               } else if (entry.getId() == 9) {
                  PanelButtonStorage<Integer> btn = new PanelButtonStorage<Integer>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), this.btnEdit, "List...", i);
                  this.addPanel(btn);
               } else if (entry.getId() == 8) {
                  PanelTextField<String> text = new PanelTextField<String>(new GuiRectangle(lw, i * 16, rw - 48, 16, 0), "" + ((NBTTagString)entry).getString(), FieldFilterString.INSTANCE);
                  text.setCallback(new CallbackNBTTagString(list, i)).setMaxLength(Integer.MAX_VALUE);
                  this.addPanel(text);
                  PanelButtonStorage<Integer> btn = new PanelButtonStorage<Integer>(new GuiRectangle(width - 48, i * 16, 16, 16, 0), this.btnEdit, "Aa", i);
                  this.addPanel(btn);
               } else if (entry.getId() == 1) {
                  PanelTextField<Byte> text = new PanelTextField<Byte>(new GuiRectangle(lw, i * 16, rw / 2, 16, 0), "" + ((NBTPrimitive)entry).getByte(), FieldFilterNumber.BYTE);
                  this.addPanel(text);
                  PanelButtonStorage<Integer> btn = new PanelButtonStorage<Integer>(new GuiRectangle(lw + rw / 2, i * 16, (int)Math.ceil((double)((float)rw / 2.0F)) - 32, 16, 0), this.btnEdit, ((NBTPrimitive)entry).getByte() > 0 ? "true" : "false", i);
                  this.addPanel(btn);
                  text.setMaxLength(Integer.MAX_VALUE).setCallback(new CallbackMulti(new ICallback[]{new CallbackNBTPrimitive(list, i, Byte.class), (value) -> {
                     btn.setText(value.byteValue() > 0 ? "true" : "false");
                  }}));
                  btn.setCallback((value) -> {
                     boolean flag = ((NBTTagByte)list.get(value.intValue())).getByte() > 0;
                     list.set(value.intValue(), new NBTTagByte((byte)(flag ? 0 : 1)));
                     text.setText(flag ? "0" : "1");
                     btn.setText(flag ? "false" : "true");
                  });
               } else if (entry.getId() > 1 && entry.getId() < 7) {
                  switch(entry.getId()) {
                  case 2:
                     PanelTextField<Short> text = new PanelTextField<Short>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getShort(), FieldFilterNumber.SHORT);
                     text.setCallback(new CallbackNBTPrimitive(list, i, Short.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 3:
                     PanelTextField<Integer> text = new PanelTextField<Integer>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getInt(), FieldFilterNumber.INT);
                     text.setCallback(new CallbackNBTPrimitive(list, i, Integer.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 4:
                     PanelTextField<Long> text = new PanelTextField<Long>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getLong(), FieldFilterNumber.LONG);
                     text.setCallback(new CallbackNBTPrimitive(list, i, Long.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 5:
                     PanelTextField<Float> text = new PanelTextField<Float>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getFloat(), FieldFilterNumber.FLOAT);
                     text.setCallback(new CallbackNBTPrimitive(list, i, Float.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                     break;
                  case 6:
                     PanelTextField<Double> text = new PanelTextField<Double>(new GuiRectangle(lw, i * 16, rw - 32, 16, 0), "" + ((NBTPrimitive)entry).getDouble(), FieldFilterNumber.DOUBLE);
                     text.setCallback(new CallbackNBTPrimitive(list, i, Double.class)).setMaxLength(Integer.MAX_VALUE);
                     this.addPanel(text);
                  }
               } else {
                  PanelTextBox err = (new PanelTextBox(new GuiRectangle(lw, i * 16 + 4, rw - 48, 12, 0), entry.getClass().getSimpleName() + " Not Supported Yet")).setAlignment(1);
                  err.setColor(PresetColor.TEXT_MAIN.getColor());
                  this.addPanel(err);
               }

               PanelButtonStorage<Integer> btnI = new PanelButtonStorage<Integer>(new GuiRectangle(width - 32, i * 16, 16, 16, 0), this.btnInsert, "+", i);
               btnI.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(0, 255, 0, 255), new GuiColorStatic(0, 255, 0, 255));
               this.addPanel(btnI);
               PanelButtonStorage<Integer> btnD = new PanelButtonStorage<Integer>(new GuiRectangle(width - 16, i * 16, 16, 16, 0), this.btnDelete, "x", i);
               btnD.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(255, 0, 0, 255), new GuiColorStatic(255, 0, 0, 255));
               this.addPanel(btnD);
            }

            this.addPanel(new PanelGeneric(new GuiRectangle(0, i * 16, width - 32, 16, 0), (IGuiTexture)null));
            PanelButtonStorage<Integer> btnI = new PanelButtonStorage<Integer>(new GuiRectangle(width - 32, i * 16, 16, 16, 0), this.btnInsert, "+", i);
            btnI.setTextHighlight(new GuiColorStatic(128, 128, 128, 255), new GuiColorStatic(0, 255, 0, 255), new GuiColorStatic(0, 255, 0, 255));
            this.addPanel(btnI);
         }

         this.setScrollX(preSX);
         this.setScrollY(preSY);
      }
   }

   public void onPanelEvent(PanelEvent event) {
      if (event instanceof PEventButton) {
         this.onButtonPress((PEventButton)event);
      }

   }

   private void onButtonPress(PEventButton event) {
      if (this.nbt != null) {
         IPanelButton btn = event.getButton();
         if (btn.getButtonID() == this.btnEdit || btn.getButtonID() == this.btnAdv || btn.getButtonID() == this.btnInsert || btn.getButtonID() == this.btnDelete) {
            NBTBase entry;
            if (this.nbt.getId() == 10) {
               entry = ((NBTTagCompound)this.nbt).getTag((String)((PanelButtonStorage)btn).getStoredValue());
            } else {
               if (this.nbt.getId() != 9) {
                  throw new RuntimeException("Invalid NBT tag type!");
               }

               entry = ((NBTTagList)this.nbt).get(((Integer)((PanelButtonStorage)btn).getStoredValue()).intValue());
            }

            if (btn.getButtonID() == this.btnEdit) {
               if (entry.getId() == 10) {
                  NBTTagCompound tag = (NBTTagCompound)entry;
                  if (JsonHelper.isItem(tag)) {
                     this.mc.displayGuiScreen(new GuiItemSelection(this.mc.currentScreen, tag, new NbtItemCallback(tag)));
                  } else if (JsonHelper.isFluid(tag)) {
                     this.mc.displayGuiScreen(new GuiFluidSelection(this.mc.currentScreen, tag, new NbtFluidCallback(tag)));
                  } else if (JsonHelper.isEntity(tag)) {
                     this.mc.displayGuiScreen(new GuiEntitySelection(this.mc.currentScreen, tag, new NbtEntityCallback(tag)));
                  } else {
                     this.mc.displayGuiScreen(new GuiNbtEditor(this.mc.currentScreen, tag, (ICallback)null));
                  }
               } else if (entry.getId() == 9) {
                  this.mc.displayGuiScreen(new GuiNbtEditor(this.mc.currentScreen, (NBTTagList)entry, (ICallback)null));
               } else if (entry.getId() == 8) {
                  if (this.nbt.getId() == 10) {
                     this.mc.displayGuiScreen(new GuiTextEditor(this.mc.currentScreen, ((NBTTagString)entry).getString(), new CallbackNBTTagString((NBTTagCompound)this.nbt, (String)((PanelButtonStorage)btn).getStoredValue())));
                  } else if (this.nbt.getId() == 9) {
                     this.mc.displayGuiScreen(new GuiTextEditor(this.mc.currentScreen, ((NBTTagString)entry).getString(), new CallbackNBTTagString((NBTTagList)this.nbt, ((Integer)((PanelButtonStorage)btn).getStoredValue()).intValue())));
                  }
               } else if (entry.getId() == 7 || entry.getId() == 11 || entry.getId() == 12) {
                  throw new UnsupportedOperationException("NBTTagByteArray, NBTTagIntArray and NBTTagLongArray are not currently supported yet");
               }
            } else if (btn.getButtonID() == this.btnAdv) {
               if (entry.getId() == 10) {
                  this.mc.displayGuiScreen(new GuiNbtType(this.mc.currentScreen, (NBTTagCompound)entry));
               } else if (entry.getId() == 9) {
                  this.mc.displayGuiScreen(new GuiNbtEditor(this.mc.currentScreen, (NBTTagList)entry, (ICallback)null));
               }
            } else if (btn.getButtonID() == this.btnInsert) {
               if (this.nbt.getId() == 10) {
                  this.mc.displayGuiScreen(new GuiNbtAdd(this.mc.currentScreen, (NBTTagCompound)this.nbt));
               } else if (this.nbt.getId() == 9) {
                  this.mc.displayGuiScreen(new GuiNbtAdd(this.mc.currentScreen, (NBTTagList)this.nbt, ((Integer)((PanelButtonStorage)btn).getStoredValue()).intValue()));
               }
            } else if (btn.getButtonID() == this.btnDelete) {
               if (this.nbt.getId() == 10) {
                  ((NBTTagCompound)this.nbt).removeTag((String)((PanelButtonStorage)btn).getStoredValue());
                  this.refreshList();
               } else if (this.nbt.getId() == 9) {
                  ((NBTTagList)this.nbt).removeTag(((Integer)((PanelButtonStorage)btn).getStoredValue()).intValue());
                  this.refreshList();
               }
            }

         }
      }
   }

   private String getButtonTitle(NBTTagCompound tag) {
      if (JsonHelper.isItem(tag)) {
         BigItemStack stack = JsonHelper.JsonToItemStack(tag);
         return QuestTranslation.translate("betterquesting.btn.item") + ": " + stack.getBaseStack().getDisplayName();
      } else if (JsonHelper.isFluid(tag)) {
         FluidStack fluid = JsonHelper.JsonToFluidStack(tag);
         return QuestTranslation.translate("betterquesting.btn.fluid") + ": " + fluid.getLocalizedName();
      } else if (JsonHelper.isEntity(tag)) {
         Entity entity = JsonHelper.JsonToEntity(tag, this.mc.world);
         return QuestTranslation.translate("betterquesting.btn.entity") + ": " + entity.getName();
      } else {
         return "Object...";
      }
   }
}
