package vazkii.quark.vanity.feature;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.client.gui.GuiButtonTranslucent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageRequestEmote;
import vazkii.quark.vanity.client.emotes.CustomEmoteIconResourcePack;
import vazkii.quark.vanity.client.emotes.EmoteBase;
import vazkii.quark.vanity.client.emotes.EmoteDescriptor;
import vazkii.quark.vanity.client.emotes.EmoteHandler;
import vazkii.quark.vanity.client.emotes.ModelAccessor;
import vazkii.quark.vanity.client.gui.GuiButtonEmote;

public class EmoteSystem extends Feature {
   private static final String[] EMOTE_NAMES = new String[]{"no", "yes", "wave", "salute", "cheer", "clap", "think", "point", "shrug", "headbang", "weep", "facepalm"};
   private static final Set<String> PATREON_EMOTES = ImmutableSet.of("dance", "tpose", "dab", "jet", "exorcist", "zombie", new String[0]);
   public static final int EMOTE_BUTTON_WIDTH = 25;
   public static final int EMOTES_PER_ROW = 3;
   private static final List<String> EMOTE_NAME_LIST = Lists.newArrayList(EMOTE_NAMES);
   private static final int EMOTE_BUTTON_START = 1800;
   public static boolean emotesVisible = false;
   public static boolean customEmoteDebug;
   public static boolean emoteCommands;
   public static File emotesDir;
   @SideOnly(Side.CLIENT)
   public static CustomEmoteIconResourcePack resourcePack;
   private String[] enabledEmotes;
   private String[] customEmotes;
   private boolean enableKeybinds;

   public EmoteSystem() {
   }

   public void setupConfig() {
      this.enableKeybinds = this.loadPropBool("Enable Keybinds", "Should keybinds for emotes be generated? (They're all unbound by default)", true);
      this.enabledEmotes = this.loadPropStringList("Enabled Emotes", "The enabled default emotes. Remove from this list to disable them. You can also re-order them, if you feel like it.", EMOTE_NAMES);
      this.customEmotes = this.loadPropStringList("Custom Emotes", "The list of Custom Emotes to be loaded.\nWatch the tutorial on Custom Emotes to learn how to make your own: https://youtu.be/ourHUkan6aQ", new String[0]);
      customEmoteDebug = this.loadPropBool("Custom Emote Dev Mode", "Enable this to make custom emotes read the file every time they're triggered so you can edit on the fly.\nDO NOT ship enabled this in a modpack, please.", false);
      emoteCommands = this.loadPropBool("Custom Emote Functions", "Allow custom emotes to run function files when a user prompts them.\nTo attach a function file to any given emote, simply place a .mcfunction file with the same name as your .emote file (sans extension) in /quark_emotes.\nCommand output from emote functions is enabled only if both \"Custom Emote Dev Mode\" and the \"commandBlockOutput\" gamerule are enabled. ", false);
      emotesDir = new File(ModuleLoader.configFile.getParent(), "quark_emotes");
      if (!emotesDir.exists() && !emotesDir.mkdir()) {
         this.customEmotes = new String[0];
      }

   }

   @SideOnly(Side.CLIENT)
   public static void addResourcePack(List<IResourcePack> packs) {
      packs.add(resourcePack = new CustomEmoteIconResourcePack());
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      Tween.registerAccessor(ModelBiped.class, ModelAccessor.INSTANCE);

      for(String s : this.enabledEmotes) {
         if (EMOTE_NAME_LIST.contains(s)) {
            EmoteHandler.addEmote(s);
         }
      }

      for(String s : PATREON_EMOTES) {
         EmoteHandler.addEmote(s);
      }

      for(String s : this.customEmotes) {
         EmoteHandler.addCustomEmote(s);
      }

      if (this.enableKeybinds) {
         ModKeybinds.initEmoteKeybinds();
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void initGui(Post event) {
      GuiScreen gui = event.getGui();
      if (gui instanceof GuiChat) {
         List<GuiButton> list = event.getButtonList();
         list.add(new GuiButtonTranslucent(1800, gui.width - 1 - 75, gui.height - 40, 75, 20, I18n.format("quark.gui.emotes", new Object[0])));
         TIntObjectHashMap<List<EmoteDescriptor>> descriptorSorting = new TIntObjectHashMap();

         for(EmoteDescriptor desc : EmoteHandler.emoteMap.values()) {
            if (desc.getTier() <= ContributorRewardHandler.localPatronTier) {
               List<EmoteDescriptor> descriptors = (List)descriptorSorting.get(desc.getTier());
               if (descriptors == null) {
                  descriptorSorting.put(desc.getTier(), descriptors = Lists.newArrayList());
               }

               descriptors.add(desc);
            }
         }

         int rows = 0;
         int i = 0;
         int row = 0;
         int[] keys = descriptorSorting.keys();
         Arrays.sort(keys);

         for(int tier : keys) {
            List<EmoteDescriptor> descriptors = (List)descriptorSorting.get(tier);
            if (descriptors != null) {
               rows += descriptors.size() / 3;
               if (descriptors.size() % 3 != 0) {
                  ++rows;
               }
            }
         }

         for(int tier : keys) {
            int rowPos = 0;
            int tierRow = 0;
            List<EmoteDescriptor> descriptors = (List)descriptorSorting.get(tier);
            if (descriptors != null) {
               for(EmoteDescriptor desc : descriptors) {
                  int rowSize = Math.min(descriptors.size() - tierRow * 3, 3);
                  int x = gui.width - (((rowPos + 1) * 2 + 3 - rowSize) * 25 / 2 + 1);
                  int y = gui.height - (40 + 25 * (rows - row));
                  GuiButton button = new GuiButtonEmote(1800 + i + 1, x, y, desc);
                  button.visible = emotesVisible;
                  button.enabled = emotesVisible;
                  list.add(button);
                  ++i;
                  ++rowPos;
                  if (rowPos == 3) {
                     ++tierRow;
                     ++row;
                     rowPos = 0;
                  }
               }
            }

            if (rowPos != 0) {
               ++row;
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void performAction(Pre event) {
      GuiButton button = event.getButton();
      if (button.id == 1800) {
         event.getGui();

         for(GuiButton b : event.getButtonList()) {
            if (b instanceof GuiButtonEmote) {
               b.visible = !b.visible;
               b.enabled = !b.enabled;
            }
         }

         emotesVisible = !emotesVisible;
      } else if (button instanceof GuiButtonEmote) {
         String name = ((GuiButtonEmote)button).desc.getRegistryName();
         NetworkHandler.INSTANCE.sendToServer(new MessageRequestEmote(name));
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKeyInput(KeyInputEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.inGameHasFocus && this.enableKeybinds) {
         for(KeyBinding key : ModKeybinds.emoteKeys.keySet()) {
            if (key.isKeyDown()) {
               String emote = (String)ModKeybinds.emoteKeys.get(key);
               NetworkHandler.INSTANCE.sendToServer(new MessageRequestEmote(emote));
               return;
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void drawHUD(net.minecraftforge.client.event.RenderGameOverlayEvent.Post event) {
      if (event.getType() == ElementType.ALL) {
         Minecraft mc = Minecraft.getMinecraft();
         ScaledResolution res = event.getResolution();
         EmoteBase emote = EmoteHandler.getPlayerEmote(mc.player);
         if (emote != null && emote.timeDone < emote.totalTime) {
            ResourceLocation resource = emote.desc.texture;
            int x = res.getScaledWidth() / 2 - 16;
            int y = res.getScaledHeight() / 2 - 60;
            float transparency = 1.0F;
            float tween = 5.0F;
            if (emote.timeDone < tween) {
               transparency = emote.timeDone / tween;
            } else if (emote.timeDone > emote.totalTime - tween) {
               transparency = (emote.totalTime - emote.timeDone) / tween;
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, transparency);
            mc.getTextureManager().bindTexture(resource);
            GuiScreen.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
            GlStateManager.enableBlend();
            String name = I18n.format(emote.desc.getTranslationKey(), new Object[0]);
            mc.fontRenderer.drawStringWithShadow(name, (float)res.getScaledWidth() / 2.0F - (float)mc.fontRenderer.getStringWidth(name) / 2.0F, (float)(y + 34), 16777215 + ((int)(transparency * 255.0F) << 24));
            GlStateManager.popMatrix();
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTick(RenderTickEvent event) {
      EmoteHandler.onRenderTick(Minecraft.getMinecraft());
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   @SideOnly(Side.CLIENT)
   public void preRenderLiving(net.minecraftforge.client.event.RenderLivingEvent.Pre event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EmoteHandler.preRender((EntityPlayer)event.getEntity());
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   @SideOnly(Side.CLIENT)
   public void postRenderLiving(net.minecraftforge.client.event.RenderLivingEvent.Post event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EmoteHandler.postRender((EntityPlayer)event.getEntity());
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
