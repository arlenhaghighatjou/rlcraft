package com.tmtravlr.potioncore;

import com.tmtravlr.potioncore.potion.PotionDrown;
import com.tmtravlr.potioncore.potion.PotionPerplexity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.FogMode;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "potioncore"
)
public class PotionCoreEventHandlerClient {
   private static final Minecraft MC = Minecraft.getMinecraft();
   public static boolean inverted = false;
   private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");
   private static final ResourceLocation ARMOR_OVERLAYS = new ResourceLocation("potioncore", "textures/gui/armor_overlays.png");
   private static final ResourceLocation ARMOR_MAGIC_SHIELD = new ResourceLocation("potioncore", "textures/gui/armor_magic_shield.png");

   public PotionCoreEventHandlerClient() {
   }

   @SubscribeEvent
   public static void renderOverlaysPre(Pre event) {
      if (event.getType() == ElementType.AIR) {
         EntityLivingBase player = (EntityLivingBase)MC.getRenderViewEntity();
         if (player != null && PotionDrown.INSTANCE.isEnabled() && player.isPotionActive(PotionDrown.INSTANCE)) {
            event.setCanceled(true);
            if (!player.isInsideOfMaterial(Material.WATER)) {
               int air = player.getEntityData().getInteger("potioncore - drown air");
               MC.mcProfiler.startSection("air");
               GlStateManager.enableBlend();
               int width = event.getResolution().getScaledWidth();
               int height = event.getResolution().getScaledHeight();
               int left = width / 2 + 91;
               int top = height - GuiIngameForge.right_height;
               int full = MathHelper.ceil((double)(air - 2) * 10.0D / 300.0D);
               int partial = MathHelper.ceil((double)air * 10.0D / 300.0D) - full;

               for(int i = 0; i < full + partial; ++i) {
                  MC.ingameGUI.drawTexturedModalRect(left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
               }

               GuiIngameForge.right_height += 10;
               GlStateManager.disableBlend();
               MC.mcProfiler.endSection();
            }
         }
      }

      if (event.getType() == ElementType.ARMOR && ConfigLoader.renderArmorOverlay) {
         EntityLivingBase player = (EntityLivingBase)MC.getRenderViewEntity();
         MC.mcProfiler.startSection("armor");
         GlStateManager.enableBlend();
         ScaledResolution scale = event.getResolution();
         double z = -90.0D;
         int left = scale.getScaledWidth() / 2 - 91;
         int top = scale.getScaledHeight() - GuiIngameForge.left_height;
         double resistance = (player.getEntityAttribute(PotionCoreAttributes.DAMAGE_RESISTANCE).getAttributeValue() - 1.0D) * 20.0D;
         int currentLeft = left;
         MC.getTextureManager().bindTexture(ARMOR_OVERLAYS);

         for(int i = 2; resistance > 0.0D && i <= 20; i += 2) {
            if ((double)i <= resistance) {
               drawArmorOverlayRectangle((double)(currentLeft - 1), (double)(top - 1), z - 10.0D, 27.0D, 0.0D, 11.0D, 11.0D);
            } else if ((double)i > resistance && resistance >= (double)(i - 2)) {
               int partial = MathHelper.floor(resistance % 2.0D * 11.0D / 2.0D);
               drawArmorOverlayRectangle((double)(currentLeft - 1), (double)(top - 1), z - 10.0D, 27.0D, 0.0D, (double)partial, 11.0D);
            }

            currentLeft += 8;
         }

         GlStateManager.disableBlend();
         MC.getTextureManager().bindTexture(GuiIngameForge.ICONS);
         MC.mcProfiler.endSection();
      }

   }

   @SubscribeEvent
   public static void renderOverlaysPost(Post event) {
      if (event.getType() == ElementType.ARMOR && ConfigLoader.renderArmorOverlay) {
         MC.mcProfiler.startSection("armor");
         GlStateManager.enableBlend();
         ScaledResolution scale = event.getResolution();
         double z = -90.0D;
         int left = scale.getScaledWidth() / 2 - 91;
         int top = scale.getScaledHeight() - GuiIngameForge.left_height + 10;
         boolean renderBase = false;
         double magicShield = MC.player.getEntityAttribute(PotionCoreAttributes.MAGIC_SHIELDING).getAttributeValue();
         double resistance = (MC.player.getEntityAttribute(PotionCoreAttributes.DAMAGE_RESISTANCE).getAttributeValue() - 1.0D) * 20.0D;
         double armorToughness = MC.player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
         double armor = (double)ForgeHooks.getTotalArmorValue(MC.player);
         if (magicShield > 0.0D || resistance > 0.0D || armor > 0.0D || armorToughness > 0.0D) {
            renderBase = true;
         }

         if (renderBase && armor == 0.0D) {
            MC.getTextureManager().bindTexture(Gui.ICONS);

            for(int i = 0; i < 10; ++i) {
               MC.ingameGUI.drawTexturedModalRect(left + i * 8, top, 16, 9, 9, 9);
            }
         }

         int currentLeft = left;
         MC.getTextureManager().bindTexture(ARMOR_OVERLAYS);

         for(int i = 21; renderBase && i < 40; i += 2) {
            if ((double)i < armor) {
               drawArmorOverlayRectangle((double)currentLeft, (double)top, z, 9.0D, 0.0D, 9.0D, 9.0D);
            } else if ((double)i == armor) {
               drawArmorOverlayRectangle((double)currentLeft, (double)top, z, 0.0D, 0.0D, 9.0D, 9.0D);
            }

            currentLeft += 8;
         }

         currentLeft = left;

         for(int i = 2; armorToughness > 0.0D && i <= 20; i += 2) {
            if ((double)i <= armorToughness) {
               drawArmorOverlayRectangle((double)currentLeft, (double)top, z + 10.0D, 18.0D, 0.0D, 9.0D, 9.0D);
            } else if ((double)i > armorToughness && armorToughness >= (double)(i - 2)) {
               int partial = MathHelper.floor(armorToughness % 2.0D * 9.0D / 2.0D);
               drawArmorOverlayRectangle((double)currentLeft, (double)top, z + 10.0D, 18.0D, 0.0D, (double)partial, 9.0D);
            }

            currentLeft += 8;
         }

         MC.getTextureManager().bindTexture(ARMOR_MAGIC_SHIELD);
         int glintWidth = Math.min(82, MathHelper.floor(magicShield * 4.0D + 2.0D));
         if (glintWidth == 82) {
            glintWidth = 83;
         }

         if (glintWidth > 2) {
            drawEnchantmentGlint((double)(left - 1), (double)(top - 1), z - 30.0D, (double)glintWidth, 11.0D);
         }

         GlStateManager.disableBlend();
         MC.getTextureManager().bindTexture(GuiIngameForge.ICONS);
         MC.mcProfiler.endSection();
      }

   }

   private static void drawArmorOverlayRectangle(double x, double y, double z, double uStart, double vStart, double width, double height) {
      double uEnd = uStart + width;
      double vEnd = vStart + height;
      double uMult = 0.015625D;
      double vMult = 0.03125D;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexBuffer = tessellator.getBuffer();
      vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexBuffer.pos(x, y + height, z).tex(uStart * uMult, vEnd * vMult).endVertex();
      vertexBuffer.pos(x + width, y + height, z).tex(uEnd * uMult, vEnd * vMult).endVertex();
      vertexBuffer.pos(x + width, y, z).tex(uEnd * uMult, vStart * vMult).endVertex();
      vertexBuffer.pos(x, y, z).tex(uStart * uMult, vStart * vMult).endVertex();
      tessellator.draw();
   }

   private static void drawEnchantmentGlint(double x, double y, double z, double width, double height) {
      float scale = 8.0F;
      GlStateManager.pushMatrix();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(516);
      GlStateManager.blendFunc(SourceFactor.SRC_COLOR, DestFactor.ONE);
      GlStateManager.matrixMode(5890);
      GlStateManager.color(0.95F, 0.75F, 0.95F);
      GlStateManager.pushMatrix();
      GlStateManager.scale(scale, scale, scale);
      float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
      GlStateManager.translate(f, 0.0F, 0.0F);
      GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
      drawGlintRectangle(x, y, z, width, height);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
      GlStateManager.translate(-f1, 0.0F, 0.0F);
      GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
      drawGlintRectangle(x, y, z, width, height);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.cullFace(CullFace.BACK);
      GlStateManager.popMatrix();
   }

   private static void drawGlintRectangle(double x, double y, double z, double width, double height) {
      double uWidth = width / 80.0D;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexBuffer = tessellator.getBuffer();
      vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexBuffer.pos(x, y + height, z).tex(0.0D, 1.0D).endVertex();
      vertexBuffer.pos(x + width, y + height, z).tex(uWidth, 1.0D).endVertex();
      vertexBuffer.pos(x + width, y, z).tex(uWidth, 0.0D).endVertex();
      vertexBuffer.pos(x, y, z).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
   }

   private static int getSpecialArmorDisplay(EntityPlayer player) {
      int ret = 0;

      for(int x = 0; x < player.inventory.armorInventory.size(); ++x) {
         ItemStack stack = (ItemStack)player.inventory.armorInventory.get(x);
         if (!stack.isEmpty() && stack.getItem() instanceof ISpecialArmor) {
            ret += ((ISpecialArmor)stack.getItem()).getArmorDisplay(player, stack, x);
         }
      }

      return ret;
   }

   @SubscribeEvent
   public static void onFogRender(FogDensity event) {
      if (ConfigLoader.fixBlindness && event.getEntity() == MC.player && MC.player.isPotionActive(MobEffects.BLINDNESS)) {
         float f1 = 5.0F;
         int duration = MC.player.getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
         int amplifier = MC.player.getActivePotionEffect(MobEffects.BLINDNESS).getAmplifier() + 1;
         if (duration < 20) {
            f1 = 5.0F + ((float)(MC.gameSettings.renderDistanceChunks * 16) - 5.0F) * (1.0F - (float)duration / 20.0F);
         }

         float multiplier = 0.25F / (float)amplifier;
         GlStateManager.setFog(FogMode.LINEAR);
         GlStateManager.setFogStart(f1 * multiplier);
         GlStateManager.setFogEnd(f1 * multiplier * 4.0F);
         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            GL11.glFogi(34138, 34139);
         }

         event.setDensity(2.0F);
         event.setCanceled(true);
      }

   }

   @SubscribeEvent
   public static void onClientConnectToServer(ClientConnectedToServerEvent event) {
      PotionCoreHelper.hasServer = false;
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      EntityPlayer player = MC.player;
      new NBTTagCompound();
      if (ConfigLoader.fixReach && player != null && MC.playerController != null && !(MC.playerController instanceof PotionCorePlayerController)) {
         NetHandlerPlayClient netHandler = (NetHandlerPlayClient)ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, MC.playerController, new String[]{"netClientHandler", "connection"});
         GameType gameType = MC.playerController.getCurrentGameType();
         MC.playerController = new PotionCorePlayerController(MC, netHandler);
         MC.playerController.setGameType(gameType);
      }

      if (player != null) {
         NBTTagCompound persisted = player.getEntityData().getCompoundTag("PlayerPersisted");
         if (!player.getEntityData().hasKey("PlayerPersisted")) {
            player.getEntityData().setTag("PlayerPersisted", persisted);
         }
      }

      if (MC.world != null && player != null && PotionPerplexity.INSTANCE.isEnabled() && player.isPotionActive(PotionPerplexity.INSTANCE)) {
         if (!inverted) {
            saveInverted(true);
         }
      } else if (inverted) {
         saveInverted(false);
      }

   }

   private static void saveInverted(boolean toSave) {
      inverted = toSave;

      try {
         File options = (File)ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, new String[]{"optionsFile", "optionsFile"});
         if (!options.exists()) {
            return;
         }

         PrintWriter printwriter = new PrintWriter(new FileWriter(options));
         printwriter.println("inverted:" + inverted);
         printwriter.close();
      } catch (Exception var3) {
         PotionCore.logger.warn("Failed to save options", var3);
      }

      KeyBinding temp = MC.gameSettings.keyBindForward;
      MC.gameSettings.keyBindForward = MC.gameSettings.keyBindBack;
      MC.gameSettings.keyBindBack = temp;
      temp = MC.gameSettings.keyBindLeft;
      MC.gameSettings.keyBindLeft = MC.gameSettings.keyBindRight;
      MC.gameSettings.keyBindRight = temp;
      temp = MC.gameSettings.keyBindSneak;
      MC.gameSettings.keyBindSneak = MC.gameSettings.keyBindJump;
      MC.gameSettings.keyBindJump = temp;
      MC.gameSettings.invertMouse = !MC.gameSettings.invertMouse;
      MC.gameSettings.saveOptions();
   }

   public static void loadInverted() {
      File options = (File)ObfuscationReflectionHelper.getPrivateValue(GameSettings.class, Minecraft.getMinecraft().gameSettings, new String[]{"optionsFile", "optionsFile"});
      if (options.exists()) {
         try {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(options));
            String s = "";

            while((s = bufferedreader.readLine()) != null) {
               try {
                  String[] astring = s.split(":");
                  if (astring[0].equals("inverted")) {
                     inverted = Boolean.valueOf(astring[1]).booleanValue();
                  }
               } catch (Exception var4) {
                  PotionCore.logger.warn("Skipping bad option: " + s);
               }
            }

            bufferedreader.close();
         } catch (Exception var5) {
            PotionCore.logger.error("Failed to load options", var5);
         }

      }
   }
}
