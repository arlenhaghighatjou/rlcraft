package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageChangeHotbar;

public class ChangeHotbarKeybind extends Feature {
   private static final int[] BAR_KEYS = new int[]{2, 3, 4};
   private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
   private static final int ANIMATION_TIME = 10;
   private static final int MAX_HEIGHT = 90;
   private static final int ANIM_PER_TICK = 9;
   public static int height = 0;
   public static int currentHeldItem = -1;
   public static boolean animating;
   public static boolean keyDown;
   public static boolean hotbarChangeOpen;
   public static boolean shifting;

   public ChangeHotbarKeybind() {
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ModKeybinds.initChangeHotbarKey();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKeyInput(KeyInputEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      boolean down = ModKeybinds.changeHotbarKey.isKeyDown();
      boolean wasDown = keyDown;
      keyDown = down;
      if (mc.inGameHasFocus) {
         if (down && !wasDown) {
            hotbarChangeOpen = !hotbarChangeOpen;
         } else if (hotbarChangeOpen) {
            for(int i = 0; i < BAR_KEYS.length; ++i) {
               if (Keyboard.isKeyDown(BAR_KEYS[i])) {
                  NetworkHandler.INSTANCE.sendToServer(new MessageChangeHotbar(i + 1));
                  hotbarChangeOpen = false;
                  currentHeldItem = mc.player.inventory.currentItem;
                  return;
               }
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void hudPre(Pre event) {
      float shift = -this.getRealHeight(event.getPartialTicks()) + 22.0F;
      if (shift < 0.0F) {
         if (event.getType() == ElementType.HEALTH) {
            GlStateManager.translate(0.0F, shift, 0.0F);
            shifting = true;
         } else if (shifting && (event.getType() == ElementType.DEBUG || event.getType() == ElementType.POTION_ICONS)) {
            GlStateManager.translate(0.0F, -shift, 0.0F);
            shifting = false;
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void hudPost(Post event) {
      if (height > 0) {
         Minecraft mc = Minecraft.getMinecraft();
         EntityPlayer player = mc.player;
         if (event.getType() == ElementType.HOTBAR) {
            ScaledResolution res = event.getResolution();
            float realHeight = this.getRealHeight(event.getPartialTicks());
            float xStart = (float)res.getScaledWidth() / 2.0F - 91.0F;
            float yStart = (float)res.getScaledHeight() - realHeight;
            RenderItem render = mc.getRenderItem();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            mc.renderEngine.bindTexture(WIDGETS);

            for(int i = 0; i < 3; ++i) {
               GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
               mc.ingameGUI.drawTexturedModalRect(xStart, yStart + (float)(i * 21), 0, 0, 182, 22);
            }

            for(int i = 0; i < 3; ++i) {
               mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + Integer.toString(i + 1), xStart - 9.0F, yStart + (float)(i * 21) + 7.0F, 16777215);
            }

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.translate(xStart, yStart, 0.0F);

            for(int i = 0; i < 27; ++i) {
               ItemStack invStack = player.inventory.getStackInSlot(i + 9);
               int x = i % 9 * 20 + 3;
               int y = i / 9 * 21 + 3;
               render.renderItemAndEffectIntoGUI(invStack, x, y);
               render.renderItemOverlays(mc.fontRenderer, invStack, x, y);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
         }

      }
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (player != null && currentHeldItem != -1 && player.inventory.currentItem != currentHeldItem) {
            player.inventory.currentItem = currentHeldItem;
            currentHeldItem = -1;
         }
      }

      if (hotbarChangeOpen && height < 90) {
         height += 9;
         animating = true;
      } else if (!hotbarChangeOpen && height > 0) {
         height -= 9;
         animating = true;
      } else {
         animating = false;
      }

   }

   private float getRealHeight(float part) {
      return !animating ? (float)height : (float)height + part * 9.0F * (float)(hotbarChangeOpen ? 1 : -1);
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
