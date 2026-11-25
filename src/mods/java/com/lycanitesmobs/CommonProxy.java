package com.lycanitesmobs;

import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.Subspecies;
import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy {
   public CommonProxy() {
   }

   public void registerEvents() {
   }

   public File getMinecraftDir() {
      return new File(".");
   }

   public void initLanguageManager() {
   }

   public void registerRenders(ModInfo groupInfo) {
   }

   public void registerTextures() {
   }

   public void registerModels(ModInfo groupInfo) {
   }

   public EntityPlayer getClientPlayer() {
      return null;
   }

   public void addBlockRender(ModInfo group, Block block) {
   }

   public void addItemRender(ModInfo group, Item item) {
   }

   public void loadCreatureModel(CreatureInfo creature, String modelClassName) throws ClassNotFoundException {
   }

   public void loadSubspeciesModel(Subspecies subspecies, String modelClassName) throws ClassNotFoundException {
   }

   @SideOnly(Side.CLIENT)
   public FontRenderer getFontRenderer() {
      return null;
   }

   public EntityPlayer getPlayer(MessageContext ctx) {
      return ctx.getServerHandler().player;
   }
}
