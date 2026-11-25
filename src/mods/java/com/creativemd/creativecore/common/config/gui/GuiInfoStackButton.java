package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarButton;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.premade.SubContainerEmpty;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.utils.mc.MaterialUtils;
import com.creativemd.creativecore.common.utils.stack.InfoBlock;
import com.creativemd.creativecore.common.utils.stack.InfoContainOre;
import com.creativemd.creativecore.common.utils.stack.InfoFuel;
import com.creativemd.creativecore.common.utils.stack.InfoItem;
import com.creativemd.creativecore.common.utils.stack.InfoItemStack;
import com.creativemd.creativecore.common.utils.stack.InfoMaterial;
import com.creativemd.creativecore.common.utils.stack.InfoName;
import com.creativemd.creativecore.common.utils.stack.InfoOre;
import com.creativemd.creativecore.common.utils.stack.InfoStack;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiInfoStackButton extends GuiAvatarButton {
   private InfoStack info;

   public GuiInfoStackButton(String name, int x, int y, int width, int height, InfoStack info) {
      super(name, getLabelText(info), x, y, width, height, new AvatarItemStack(info.getItemStack()));
      this.info = info;
   }

   public void set(InfoStack info) {
      this.info = info;
      this.avatar = new AvatarItemStack(info.getItemStack());
      this.caption = getLabelText(info);
      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public static String getLabelText(InfoStack value) {
      if (value == null) {
         return "";
      } else if (value instanceof InfoBlock) {
         return "Block: " + ChatFormatting.YELLOW + ((ResourceLocation)Block.REGISTRY.getNameForObject(((InfoBlock)value).block)).toString();
      } else if (value instanceof InfoItem) {
         return "Item: " + ChatFormatting.YELLOW + ((ResourceLocation)Item.REGISTRY.getNameForObject(((InfoItem)value).item)).toString();
      } else if (value instanceof InfoItemStack) {
         return "ItemStack";
      } else if (value instanceof InfoMaterial) {
         return "Material: " + ChatFormatting.YELLOW + MaterialUtils.getNameOrDefault(((InfoMaterial)value).material, "unkown");
      } else if (value instanceof InfoOre) {
         return "Ore: " + ChatFormatting.YELLOW + ((InfoOre)value).ore;
      } else if (value instanceof InfoContainOre) {
         return "Ore (Contains): " + ChatFormatting.YELLOW + ((InfoContainOre)value).ore;
      } else if (value instanceof InfoName) {
         return "Name (Contains): " + ChatFormatting.YELLOW + ((InfoName)value).name;
      } else {
         return value instanceof InfoFuel ? "All fuels" : "no description given.";
      }
   }

   public void onClicked(int x, int y, int button) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setBoolean("dialog", true);
      SubGui gui = new SubGuiFullItemDialog(this, false);
      gui.container = new SubContainerEmpty(this.getPlayer());
      gui.gui = this.getGui().gui;
      this.getGui().gui.addLayer(gui);
      PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, gui.gui.getLayers().size() - 1, false));
      gui.onOpened();
   }

   public InfoStack get() {
      return this.info;
   }
}
