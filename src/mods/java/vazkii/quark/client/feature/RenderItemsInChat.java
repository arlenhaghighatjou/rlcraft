package vazkii.quark.client.feature;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class RenderItemsInChat extends Feature {
   private static int chatX;
   private static int chatY;
   public static final int RGB_MASK = 16777215;
   private static int ALPHA_VALUE = -1;

   public RenderItemsInChat() {
   }

   public static ITextComponent createStackComponent(ITextComponent component) {
      if (!ModuleLoader.isFeatureEnabled(RenderItemsInChat.class)) {
         return component;
      } else {
         ITextComponent out = new TextComponentString("   ");
         out.setStyle(component.getStyle().createDeepCopy());
         return out.appendSibling(component);
      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void getChatPos(Chat event) {
      chatX = event.getPosX();
      chatY = event.getPosY();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderSymbols(Post event) {
      Minecraft mc = Minecraft.getMinecraft();
      GuiIngame gameGui = mc.ingameGUI;
      GuiNewChat chatGui = gameGui.getChatGUI();
      if (event.getType() == ElementType.CHAT) {
         int updateCounter = gameGui.getUpdateCounter();
         List<ChatLine> lines = ClientReflectiveAccessor.getChatDrawnLines(chatGui);
         int shift = ClientReflectiveAccessor.getScrollPos(chatGui);

         for(int idx = shift; idx < lines.size() && idx - shift < chatGui.getLineCount(); ++idx) {
            ChatLine line = (ChatLine)lines.get(idx);
            String before = "";
            String currentText = TextFormatting.getTextWithoutFormattingCodes(line.getChatComponent().getUnformattedComponentText());
            if (currentText != null && currentText.startsWith("   ")) {
               render(mc, chatGui, updateCounter, before, line, idx - shift, line.getChatComponent());
            }

            before = before + currentText;

            for(ITextComponent sibling : line.getChatComponent().getSiblings()) {
               currentText = TextFormatting.getTextWithoutFormattingCodes(sibling.getUnformattedComponentText());
               if (currentText != null && currentText.startsWith("   ")) {
                  render(mc, chatGui, updateCounter, before, line, idx - shift, sibling);
               }

               before = before + currentText;
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   private static void render(Minecraft mc, GuiNewChat chatGui, int updateCounter, String before, ChatLine line, int lineHeight, ITextComponent component) {
      Style style = component.getStyle();
      HoverEvent hoverEvent = style.getHoverEvent();
      if (hoverEvent != null && hoverEvent.getAction() == Action.SHOW_ITEM) {
         ItemStack stack = ItemStack.EMPTY;

         try {
            NBTTagCompound textValue = JsonToNBT.getTagFromJson(hoverEvent.getValue().getUnformattedText());
            stack = new ItemStack(textValue);
         } catch (NBTException var16) {
            ;
         }

         if (stack.isEmpty()) {
            stack = new ItemStack(Blocks.BARRIER);
         }

         int timeSinceCreation = updateCounter - line.getUpdatedCounter();
         if (chatGui.getChatOpen()) {
            timeSinceCreation = 0;
         }

         if (timeSinceCreation < 200) {
            float chatOpacity = mc.gameSettings.chatOpacity * 0.9F + 0.1F;
            float fadeOut = MathHelper.clamp((1.0F - (float)timeSinceCreation / 200.0F) * 10.0F, 0.0F, 1.0F);
            float alpha = fadeOut * fadeOut * chatOpacity;
            int x = chatX + 3 + mc.fontRenderer.getStringWidth(before);
            int y = chatY - mc.fontRenderer.FONT_HEIGHT * lineHeight;
            if (alpha > 0.0F) {
               RenderHelper.enableGUIStandardItemLighting();
               ALPHA_VALUE = (int)(alpha * 255.0F) << 24;
               renderItemIntoGUI(mc, mc.getRenderItem(), stack, x, y);
               ALPHA_VALUE = -1;
               RenderHelper.disableStandardItemLighting();
            }
         }
      }

   }

   public static int transformColor(int src) {
      return ALPHA_VALUE == -1 ? src : src & 16777215 | ALPHA_VALUE;
   }

   @SideOnly(Side.CLIENT)
   private static void renderItemIntoGUI(Minecraft mc, RenderItem render, ItemStack stack, int x, int y) {
      renderItemModelIntoGUI(mc, render, stack, x, y, render.getItemModelWithOverrides(stack, (World)null, (EntityLivingBase)null));
   }

   @SideOnly(Side.CLIENT)
   private static void renderItemModelIntoGUI(Minecraft mc, RenderItem render, ItemStack stack, int x, int y, IBakedModel model) {
      TextureManager textureManager = mc.getTextureManager();
      GlStateManager.pushMatrix();
      textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate(-4.0F, -4.0F, -4.0F);
      ClientReflectiveAccessor.setupGuiTransform(render, x, y, model.isGui3d());
      GlStateManager.scale(0.65D, 0.65D, 0.65D);
      model = ForgeHooksClient.handleCameraTransforms(model, TransformType.GUI, false);
      render.renderItem(stack, model);
      GlStateManager.disableAlpha();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
   }
}
