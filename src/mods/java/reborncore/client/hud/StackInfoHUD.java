package reborncore.client.hud;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;
import reborncore.common.util.StringUtils;

public class StackInfoHUD {
   public static final StackInfoHUD instance = new StackInfoHUD();
   public static List<StackInfoElement> ELEMENTS = new ArrayList();
   private static Minecraft mc = Minecraft.getMinecraft();
   private int x = 2;
   private int y = 7;

   public StackInfoHUD() {
   }

   public static void registerElement(StackInfoElement element) {
      ELEMENTS.add(element);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onRenderExperienceBar(RenderGameOverlayEvent event) {
      if (!event.isCancelable() && event.getType() == ElementType.ALL) {
         if ((mc.inGameHasFocus || mc.currentScreen != null && mc.gameSettings.showDebugInfo) && RebornCoreConfig.ShowStackInfoHUD) {
            this.drawStackInfoHud(event.getResolution());
         }

      }
   }

   public void drawStackInfoHud(ScaledResolution res) {
      EntityPlayer player = mc.player;
      List<ItemStack> stacks = new ArrayList();

      for(ItemStack stack : player.getArmorInventoryList()) {
         stacks.add(stack);
      }

      stacks.add(player.getHeldItemOffhand());
      stacks.add(player.getHeldItemMainhand());
      this.x = RebornCoreConfig.stackInfoX;
      if (RebornCoreConfig.stackInfoCorner != 2 && RebornCoreConfig.stackInfoCorner != 3) {
         this.y = RebornCoreConfig.stackInfoY;
      } else {
         stacks = Lists.reverse(stacks);
         this.y = res.getScaledHeight() - 20 - RebornCoreConfig.stackInfoY;
      }

      for(ItemStack stack : stacks) {
         this.addInfo(stack, res);
      }

   }

   public void renderItemStack(ItemStack stack, int x, int y) {
      if (stack != ItemStack.EMPTY) {
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         RenderHelper.enableGUIStandardItemLighting();
         RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
         itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
         GL11.glDisable(2896);
      }

   }

   private void renderStackForInfo(ItemStack stack) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(32826);
      RenderHelper.enableStandardItemLighting();
      RenderHelper.enableGUIStandardItemLighting();
      this.renderItemStack(stack, this.x, this.y - 5);
   }

   private void addInfo(ItemStack stack, ScaledResolution res) {
      if (stack != ItemStack.EMPTY) {
         String text = "";
         if (stack.getItem() instanceof IEnergyItemInfo) {
            IEnergyStorage capEnergy = new ForgePowerItemManager(stack);
            int maxCharge = capEnergy.getMaxEnergyStored();
            int currentCharge = capEnergy.getEnergyStored();
            TextFormatting color = TextFormatting.GREEN;
            double quarter = (double)(maxCharge / 4);
            double half = (double)(maxCharge / 2);
            if ((double)currentCharge <= half) {
               color = TextFormatting.YELLOW;
            }

            if ((double)currentCharge <= quarter) {
               color = TextFormatting.DARK_RED;
            }

            text = color + PowerSystem.getLocaliszedPowerFormattedNoSuffix(currentCharge / RebornCoreConfig.euPerFU) + "/" + PowerSystem.getLocaliszedPowerFormattedNoSuffix(maxCharge / RebornCoreConfig.euPerFU) + " " + PowerSystem.getDisplayPower().abbreviation + TextFormatting.GRAY;
            if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("isActive")) {
               if (stack.getTagCompound().getBoolean("isActive")) {
                  text = text + TextFormatting.GOLD + " (" + StringUtils.t("reborncore.message.active") + TextFormatting.GOLD + ")" + TextFormatting.GRAY;
               } else {
                  text = text + TextFormatting.GOLD + " (" + StringUtils.t("reborncore.message.inactive") + TextFormatting.GOLD + ")" + TextFormatting.GRAY;
               }
            }

            if (RebornCoreConfig.stackInfoCorner == 1 || RebornCoreConfig.stackInfoCorner == 2) {
               int strWidth = mc.fontRenderer.getStringWidth(text);
               this.x = res.getScaledWidth() - strWidth - 18 - RebornCoreConfig.stackInfoX;
            }

            this.renderStackForInfo(stack);
            mc.fontRenderer.drawStringWithShadow(text, (float)(this.x + 18), (float)this.y, 0);
            if (RebornCoreConfig.stackInfoCorner != 0 && RebornCoreConfig.stackInfoCorner != 1) {
               this.y -= 20;
            } else {
               this.y += 20;
            }
         }

         for(StackInfoElement element : ELEMENTS) {
            if (!element.getText(stack).equals("")) {
               this.renderStackForInfo(stack);
               mc.fontRenderer.drawStringWithShadow(element.getText(stack), (float)(this.x + 18), (float)this.y, 0);
               this.y += 20;
            }
         }

      }
   }
}
