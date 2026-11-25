package vazkii.quark.base.client;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientReflectiveAccessor {
   private static final MethodHandle GET_BUTTON_POTION;
   private static final MethodHandle GET_BUTTON_SELECTED;
   private static final MethodHandle GET_GUI_Z;
   private static final MethodHandle SET_BUTTON_HOVERED;
   private static final MethodHandle GET_RENDERER_UPDATES;
   private static final MethodHandle APPLY_BOBBING;
   private static final MethodHandle CHAT_DRAWN_LINES;
   private static final MethodHandle CHAT_SCROLL_POS;
   private static final MethodHandle SETUP_ITEM_GUI_TRANSFORM;
   private static final MethodHandle MINECRAFT_SYSTEM_TIME;
   private static final MethodHandle ENTITY_LIVING_BASE_POTION_COLOR;
   private static final MethodHandle RENDER_OUTLINE_MODE;

   public ClientReflectiveAccessor() {
   }

   public static Potion getPotion(GuiButton button) {
      try {
         return GET_BUTTON_POTION.invokeExact(button);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static boolean getButtonSelected(GuiButton button) {
      try {
         return GET_BUTTON_SELECTED.invokeExact(button);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static float getZLevel(Gui gui) {
      try {
         return GET_GUI_Z.invokeExact(gui);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void setButtonHovered(GuiButton button, boolean hovered) {
      try {
         SET_BUTTON_HOVERED.invokeExact(button, hovered);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public static int getUpdateCount(EntityRenderer renderer) {
      try {
         return GET_RENDERER_UPDATES.invokeExact(renderer);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void applyBobbing(EntityRenderer entityRenderer, float partialTicks) {
      try {
         APPLY_BOBBING.invokeExact(entityRenderer, partialTicks);
      } catch (Throwable var3) {
         throw new RuntimeException(var3);
      }
   }

   public static List<ChatLine> getChatDrawnLines(GuiNewChat chat) {
      try {
         return CHAT_DRAWN_LINES.invokeExact(chat);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static int getScrollPos(GuiNewChat chat) {
      try {
         return CHAT_SCROLL_POS.invokeExact(chat);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static void setupGuiTransform(RenderItem render, int x, int y, boolean isGui3d) {
      try {
         SETUP_ITEM_GUI_TRANSFORM.invokeExact(render, x, y, isGui3d);
      } catch (Throwable var5) {
         throw new RuntimeException(var5);
      }
   }

   public static long lastSystemTime(Minecraft mc) {
      try {
         return MINECRAFT_SYSTEM_TIME.invokeExact(mc);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   public static DataParameter<Integer> potionEffectColor() {
      try {
         return ENTITY_LIVING_BASE_POTION_COLOR.invokeExact();
      } catch (Throwable var1) {
         throw new RuntimeException(var1);
      }
   }

   public static boolean renderOutlines(Render render) {
      try {
         return RENDER_OUTLINE_MODE.invokeExact(render);
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }
   }

   static {
      try {
         Class buttonClazz = Class.forName("net.minecraft.client.gui.inventory.GuiBeacon$PowerButton");
         Field f = ObfuscationReflectionHelper.findField(buttonClazz, "effect");
         GET_BUTTON_POTION = MethodHandles.lookup().unreflectGetter(f).asType(MethodType.methodType(Potion.class, GuiButton.class));
         buttonClazz = Class.forName("net.minecraft.client.gui.inventory.GuiBeacon$Button");
         f = ObfuscationReflectionHelper.findField(buttonClazz, "selected");
         GET_BUTTON_SELECTED = MethodHandles.lookup().unreflectGetter(f).asType(MethodType.methodType(Boolean.TYPE, GuiButton.class));
         f = ObfuscationReflectionHelper.findField(Gui.class, "zLevel");
         GET_GUI_Z = MethodHandles.lookup().unreflectGetter(f);
         f = ObfuscationReflectionHelper.findField(GuiButton.class, "hovered");
         SET_BUTTON_HOVERED = MethodHandles.lookup().unreflectSetter(f);
         f = ObfuscationReflectionHelper.findField(EntityRenderer.class, "rendererUpdateCount");
         GET_RENDERER_UPDATES = MethodHandles.lookup().unreflectGetter(f);
         Method m = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "applyBobbing", Void.TYPE, new Class[]{Float.TYPE});
         APPLY_BOBBING = MethodHandles.lookup().unreflect(m);
         f = ObfuscationReflectionHelper.findField(GuiNewChat.class, "drawnChatLines");
         CHAT_DRAWN_LINES = MethodHandles.lookup().unreflectGetter(f);
         f = ObfuscationReflectionHelper.findField(GuiNewChat.class, "scrollPos");
         CHAT_SCROLL_POS = MethodHandles.lookup().unreflectGetter(f);
         m = ObfuscationReflectionHelper.findMethod(RenderItem.class, "setupGuiTransform", Void.TYPE, new Class[]{Integer.TYPE, Integer.TYPE, Boolean.TYPE});
         SETUP_ITEM_GUI_TRANSFORM = MethodHandles.lookup().unreflect(m);
         f = ObfuscationReflectionHelper.findField(Minecraft.class, "systemTime");
         MINECRAFT_SYSTEM_TIME = MethodHandles.lookup().unreflectGetter(f);
         f = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "POTION_EFFECTS");
         ENTITY_LIVING_BASE_POTION_COLOR = MethodHandles.lookup().unreflectGetter(f);
         f = ObfuscationReflectionHelper.findField(Render.class, "renderOutlines");
         RENDER_OUTLINE_MODE = MethodHandles.lookup().unreflectGetter(f);
      } catch (IllegalAccessException | ClassNotFoundException var3) {
         throw new RuntimeException(var3);
      }
   }
}
