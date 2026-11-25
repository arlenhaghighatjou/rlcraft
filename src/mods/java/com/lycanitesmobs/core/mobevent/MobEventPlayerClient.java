package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.gui.overlays.BaseOverlay;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class MobEventPlayerClient {
   public MobEvent mobEvent;
   public int ticks = 0;
   public World world;
   public PositionedSoundRecord sound;
   public boolean extended = false;

   public MobEventPlayerClient(MobEvent mobEvent, World world) {
      this.mobEvent = mobEvent;
      this.world = world;
      if (!world.isRemote) {
         LycanitesMobs.logWarning("", "Created a MobEventClient with a server side world, this shouldn't happen, things are going to get weird!");
      }

   }

   public void onStart(EntityPlayer player) {
      if (!this.extended) {
         this.ticks = 0;
      }

      String eventMessage = LanguageManager.translate("event." + (this.extended ? "extended" : "started"));
      eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
      player.sendMessage(new TextComponentString(eventMessage));
      if (!player.capabilities.isCreativeMode || MobEventPlayerServer.testOnCreative || !"world".equalsIgnoreCase(this.mobEvent.channel)) {
         this.playSound();
      }
   }

   public void playSound() {
      if (AssetManager.getSound("mobevent_" + this.mobEvent.title.toLowerCase()) == null) {
         LycanitesMobs.logWarning("MobEvent", "Sound missing for: " + this.mobEvent.getTitle());
      } else {
         this.sound = new PositionedSoundRecord(AssetManager.getSound("mobevent_" + this.mobEvent.title.toLowerCase()).getSoundName(), SoundCategory.RECORDS, 1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
         Minecraft.getMinecraft().getSoundHandler().playSound(this.sound);
      }
   }

   public void onFinish(EntityPlayer player) {
      String eventMessage = LanguageManager.translate("event.finished");
      eventMessage = eventMessage.replace("%event%", this.mobEvent.getTitle());
      player.sendMessage(new TextComponentString(eventMessage));
   }

   public void onUpdate() {
      ++this.ticks;
   }

   @SideOnly(Side.CLIENT)
   public void onGUIUpdate(BaseOverlay gui, int sWidth, int sHeight) {
      EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
      if (!player.capabilities.isCreativeMode || MobEventPlayerServer.testOnCreative || !"world".equalsIgnoreCase(this.mobEvent.channel)) {
         if (this.world != null && this.world == player.getEntityWorld()) {
            if (this.world.isRemote) {
               int introTime = 240;
               if (this.ticks <= introTime) {
                  int startTime = 40;
                  int stopTime = 80;
                  float animation = 1.0F;
                  if (this.ticks < startTime) {
                     animation = (float)this.ticks / (float)startTime;
                  } else if (this.ticks > introTime - stopTime) {
                     animation = (float)(introTime - this.ticks) / (float)stopTime;
                  }

                  int width = 256;
                  int height = 256;
                  int x = sWidth / 2 - width / 2;
                  int y = sHeight / 2 - height / 2;
                  x = x + (3 - this.ticks % 6);
                  y = y + (2 - this.ticks % 4);
                  gui.minecraft.getTextureManager().bindTexture(this.getTexture());
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, animation);
                  if (animation > 0.0F) {
                     gui.drawTexturedModalRect(x, y, width, height, width, height);
                  }

               }
            }
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public ResourceLocation getTexture() {
      if (AssetManager.getTexture("guimobevent" + this.mobEvent.title) == null) {
         AssetManager.addTexture("guimobevent" + this.mobEvent.title, LycanitesMobs.modInfo, "textures/mobevents/" + this.mobEvent.title.toLowerCase() + ".png");
      }

      return AssetManager.getTexture("guimobevent" + this.mobEvent.title);
   }
}
