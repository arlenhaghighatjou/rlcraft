package betterquesting.client.gui2.editors.nbt;

import betterquesting.api.client.gui.misc.IVolatileScreen;
import betterquesting.api.misc.ICallback;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.GuiScreenCanvas;
import betterquesting.api2.client.gui.controls.IPanelButton;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.controls.PanelTextField;
import betterquesting.api2.client.gui.controls.filters.FieldFilterString;
import betterquesting.api2.client.gui.events.IPEventListener;
import betterquesting.api2.client.gui.events.PEventBroadcaster;
import betterquesting.api2.client.gui.events.PanelEvent;
import betterquesting.api2.client.gui.events.types.PEventButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.panels.CanvasTextured;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class GuiNbtAdd extends GuiScreenCanvas implements IPEventListener, IVolatileScreen {
   private final NBTBase nbt;
   private final int index;
   private PanelTextField<String> flKey;
   private final List<PanelButtonStorage<NBTBase>> options = new ArrayList();
   private NBTBase selected = null;
   private PanelButton btnConfirm;

   public GuiNbtAdd(GuiScreen parent, NBTTagCompound compoundTag) {
      super(parent);
      this.nbt = compoundTag;
      this.index = -1;
   }

   public GuiNbtAdd(GuiScreen parent, NBTTagList list, int index) {
      super(parent);
      this.nbt = list;
      this.index = index;
   }

   public void initPanel() {
      super.initPanel();
      PEventBroadcaster.INSTANCE.register(this, PEventButton.class);
      Keyboard.enableRepeatEvents(true);
      CanvasTextured cvBackground = new CanvasTextured(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 0, 0), 0), PresetTexture.PANEL_MAIN.getTexture());
      this.addPanel(cvBackground);
      cvBackground.addPanel(new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, -100, -16, 100, 16, 0), 0, QuestTranslation.translate("gui.cancel")));
      this.btnConfirm = new PanelButton(new GuiTransform(GuiAlign.BOTTOM_CENTER, 0, -16, 100, 16, 0), 1, QuestTranslation.translate("gui.done"));
      cvBackground.addPanel(this.btnConfirm);
      PanelTextBox panTxt = (new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 16, 0, -32), 0), QuestTranslation.translate("betterquesting.title.json_add"))).setAlignment(1);
      panTxt.setColor(PresetColor.TEXT_HEADER.getColor());
      cvBackground.addPanel(panTxt);
      if (this.nbt.getId() == 10) {
         this.btnConfirm.setActive(false);
         PanelTextBox txKeyTitle = new PanelTextBox(new GuiTransform(GuiAlign.TOP_CENTER, -100, 36, 200, 12, 0), TextFormatting.RED + QuestTranslation.translate("betterquesting.gui.no_key"));
         txKeyTitle.setColor(PresetColor.TEXT_MAIN.getColor());
         cvBackground.addPanel(txKeyTitle);
         this.flKey = new PanelTextField<String>(new GuiTransform(GuiAlign.TOP_CENTER, -100, 48, 200, 16, 0), "", FieldFilterString.INSTANCE);
         cvBackground.addPanel(this.flKey);
         this.flKey.setCallback((value) -> {
            if (value.isEmpty()) {
               txKeyTitle.setText(TextFormatting.RED + QuestTranslation.translate("betterquesting.gui.no_key"));
            } else if (((NBTTagCompound)this.nbt).hasKey(value)) {
               txKeyTitle.setText(TextFormatting.RED + QuestTranslation.translate("betterquesting.gui.duplicate_key"));
            } else {
               txKeyTitle.setText(QuestTranslation.translate("betterquesting.gui.key"));
            }

            this.updateConfirm();
         });
      }

      this.options.clear();
      int n = 0;
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n * 16, 100, 16, 0), 2, QuestTranslation.translate("betterquesting.btn.item"), JsonHelper.ItemStackToJson(new BigItemStack(Blocks.STONE), new NBTTagCompound())));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 100, n++ * 16, 92, 16, 0), 2, QuestTranslation.translate("betterquesting.btn.fluid"), JsonHelper.FluidStackToJson(new FluidStack(FluidRegistry.WATER, 1000), new NBTTagCompound())));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, QuestTranslation.translate("betterquesting.btn.entity"), JsonHelper.EntityToJson(new EntityPig(this.mc.world), new NBTTagCompound())));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagString.class.getSimpleName(), new NBTTagString("")));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagCompound.class.getSimpleName(), new NBTTagCompound()));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagList.class.getSimpleName(), new NBTTagList()));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagByte.class.getSimpleName(), new NBTTagByte((byte)0)));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagShort.class.getSimpleName(), new NBTTagShort((short)0)));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagInt.class.getSimpleName(), new NBTTagInt(0)));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagLong.class.getSimpleName(), new NBTTagLong(0L)));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagFloat.class.getSimpleName(), new NBTTagFloat(0.0F)));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagDouble.class.getSimpleName(), new NBTTagDouble(0.0D)));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagByteArray.class.getSimpleName(), new NBTTagByteArray(new byte[0])));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n++ * 16, 192, 16, 0), 2, NBTTagIntArray.class.getSimpleName(), new NBTTagIntArray(new int[0])));
      this.options.add(new PanelButtonStorage(new GuiTransform(GuiAlign.MID_CENTER, 0, n * 16, 192, 16, 0), 2, NBTTagLongArray.class.getSimpleName(), new NBTTagLongArray(new long[0])));
      CanvasScrolling cvOptions = new CanvasScrolling(new GuiTransform(new Vector4f(0.5F, 0.0F, 0.5F, 1.0F), new GuiPadding(-100, 64, -92, 32), 0));
      cvBackground.addPanel(cvOptions);

      for(PanelButtonStorage<NBTBase> btn : this.options) {
         cvOptions.addPanel(btn);
      }

      PanelVScrollBar scOptions = new PanelVScrollBar(new GuiTransform(new Vector4f(0.5F, 0.0F, 0.5F, 1.0F), new GuiPadding(92, 64, -100, 32), 0));
      cvBackground.addPanel(scOptions);
      cvOptions.setScrollDriverY(scOptions);
   }

   public void onPanelEvent(PanelEvent event) {
      if (event instanceof PEventButton) {
         this.onButtonPress((PEventButton)event);
      }

   }

   private void onButtonPress(PEventButton event) {
      IPanelButton btn = event.getButton();
      switch(btn.getButtonID()) {
      case 0:
         this.mc.displayGuiScreen(this.parent);
         break;
      case 1:
         if (this.selected == null) {
            return;
         }

         if (this.nbt.getId() == 10) {
            ((NBTTagCompound)this.nbt).setTag(this.flKey.getValue(), this.selected);
         } else if (this.nbt.getId() == 9) {
            NBTTagList l = (NBTTagList)this.nbt;
            if (this.index == l.tagCount()) {
               l.appendTag(this.selected);
            } else {
               for(int n = l.tagCount() - 1; n >= this.index; --n) {
                  l.set(n + 1, l.get(n));
               }

               l.set(this.index, this.selected);
            }
         }

         this.mc.displayGuiScreen(this.parent);
         break;
      case 2:
         this.selected = (NBTBase)((PanelButtonStorage)btn).getStoredValue();

         for(PanelButtonStorage<NBTBase> b : this.options) {
            b.setActive(true);
         }

         btn.setActive(false);
         this.updateConfirm();
      }

   }

   private void updateConfirm() {
      if (this.flKey == null) {
         this.btnConfirm.setActive(this.selected != null);
      } else if (!((String)this.flKey.getValue()).isEmpty() && !((NBTTagCompound)this.nbt).hasKey(this.flKey.getValue())) {
         this.btnConfirm.setActive(this.selected != null);
      } else {
         this.btnConfirm.setActive(false);
      }

   }
}
