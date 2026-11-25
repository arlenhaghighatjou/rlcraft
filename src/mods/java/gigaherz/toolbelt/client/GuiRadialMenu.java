package gigaherz.toolbelt.client;

import com.google.common.collect.Lists;
import gigaherz.toolbelt.BeltFinder;
import gigaherz.toolbelt.Config;
import gigaherz.toolbelt.ToolBelt;
import gigaherz.toolbelt.network.SwapItems;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.input.Mouse;

@EventBusSubscriber({Side.CLIENT})
public class GuiRadialMenu extends GuiScreen {
   private final BeltFinder.BeltGetter getter;
   private ItemStack stackEquipped;
   private IItemHandler inventory;
   private List<ItemStack> cachedStacks = null;
   private boolean closing;
   private boolean doneClosing;
   private double startAnimation;
   private int selectedItem = -1;
   private boolean keyCycleBeforeL = false;
   private boolean keyCycleBeforeR = false;
   private static final float PRECISION = 5.0F;

   GuiRadialMenu(BeltFinder.BeltGetter getter) {
      this.getter = getter;
      this.stackEquipped = getter.getBelt();
      this.inventory = this.stackEquipped.getCount() > 0 ? (IItemHandler)this.stackEquipped.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null) : null;
      this.startAnimation = (double)Minecraft.getMinecraft().world.getTotalWorldTime() + (double)Minecraft.getMinecraft().getRenderPartialTicks();
   }

   @SubscribeEvent
   public static void overlayEvent(Pre event) {
      if (event.getType() == ElementType.CROSSHAIRS) {
         if (Minecraft.getMinecraft().currentScreen instanceof GuiRadialMenu) {
            event.setCanceled(true);
         }

      }
   }

   public void updateScreen() {
      super.updateScreen();
      if (!this.closing) {
         ItemStack inHand = this.mc.player.getHeldItemMainhand();
         if (!Config.isItemStackAllowed(inHand)) {
            this.inventory = null;
         } else {
            ItemStack stack = this.getter.getBelt();
            if (stack.getCount() <= 0) {
               this.inventory = null;
               this.stackEquipped = null;
            } else if (this.stackEquipped != stack) {
               this.stackEquipped = stack;
               this.inventory = (IItemHandler)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
               this.cachedStacks = null;
            }
         }

         if (this.inventory == null) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
         } else if (!GameSettings.isKeyDown(ClientProxy.keyOpenToolMenu)) {
            if (Config.releaseToSwap) {
               this.processClick(false);
            } else {
               this.animateClose();
            }
         }

      } else {
         if (this.doneClosing || this.inventory == null) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
            ClientProxy.wipeOpen();
         }

      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.processClick(true);
   }

   protected void processClick(boolean triggeredByMouse) {
      if (!this.closing) {
         if (this.inventory != null) {
            ItemStack inHand = this.mc.player.getHeldItemMainhand();
            if (Config.isItemStackAllowed(inHand)) {
               List<Integer> items = Lists.newArrayList();

               for(int i = 0; i < this.inventory.getSlots(); ++i) {
                  ItemStack inSlot = this.inventory.getStackInSlot(i);
                  if (inSlot.getCount() > 0 || Config.displayEmptySlots) {
                     items.add(Integer.valueOf(i));
                  }
               }

               boolean hasAddButton = false;
               int numItems = items.size();
               if (!Config.displayEmptySlots && numItems < this.inventory.getSlots() && inHand.getCount() > 0) {
                  hasAddButton = true;
                  ++numItems;
               }

               if (numItems > 0) {
                  if (this.selectedItem >= 0) {
                     int swapWith;
                     if (hasAddButton) {
                        swapWith = this.selectedItem == 0 ? -1 : ((Integer)items.get(this.selectedItem - 1)).intValue();
                     } else {
                        swapWith = ((Integer)items.get(this.selectedItem)).intValue();
                     }

                     if (inHand.getCount() <= 0 && this.inventory.getStackInSlot(swapWith).getCount() <= 0) {
                        if (triggeredByMouse) {
                           return;
                        }
                     } else {
                        SwapItems.swapItem(swapWith, this.mc.player);
                        ToolBelt.channel.sendToServer(new SwapItems(swapWith));
                     }
                  }

                  this.animateClose();
               }
            }
         }
      }
   }

   private void animateClose() {
      this.closing = true;
      this.doneClosing = false;
      this.startAnimation = (double)Minecraft.getMinecraft().world.getTotalWorldTime() + (double)Minecraft.getMinecraft().getRenderPartialTicks();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.inventory != null) {
         List<ItemStack> items = this.cachedStacks;
         if (items == null) {
            items = Lists.newArrayList();

            for(int i = 0; i < this.inventory.getSlots(); ++i) {
               ItemStack inSlot = this.inventory.getStackInSlot(i);
               if (inSlot.getCount() > 0 || Config.displayEmptySlots) {
                  items.add(inSlot);
               }
            }

            this.cachedStacks = items;
         }

         ItemStack inHand = this.mc.player.getHeldItemMainhand();
         if (Config.isItemStackAllowed(inHand)) {
            boolean hasAddButton = false;
            int numItems = items.size();
            if (!Config.displayEmptySlots && numItems < this.inventory.getSlots() && inHand.getCount() > 0) {
               hasAddButton = true;
               ++numItems;
            }

            if (numItems <= 0) {
               this.drawCenteredString(this.fontRenderer, I18n.format("text.toolbelt.empty", new Object[0]), this.width / 2, (this.height - this.fontRenderer.FONT_HEIGHT) / 2, -1);
               if (this.closing) {
                  this.doneClosing = true;
               }

            } else {
               if (hasAddButton) {
                  this.drawCenteredString(this.fontRenderer, I18n.format("text.toolbelt.insert", new Object[0]), this.width / 2, this.height / 2 + 45 - this.fontRenderer.FONT_HEIGHT / 2, -1);
               }

               float OPEN_ANIMATION_LENGTH = 2.5F;
               float openAnimation = this.closing ? (float)(1.0D - ((double)((float)Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks) - this.startAnimation) / 2.5D) : (float)(((double)((float)Minecraft.getMinecraft().world.getTotalWorldTime() + partialTicks) - this.startAnimation) / 2.5D);
               if (this.closing && openAnimation <= 0.0F) {
                  this.doneClosing = true;
               }

               float animProgress = MathHelper.clamp(openAnimation, 0.0F, 1.0F);
               float radiusIn = Math.max(0.1F, 30.0F * animProgress);
               float radiusOut = radiusIn * 2.0F;
               float itemRadius = (radiusIn + radiusOut) * 0.5F;
               float animTop = (1.0F - animProgress) * (float)this.height / 2.0F;
               int x = this.width / 2;
               int y = this.height / 2;
               if (!this.closing) {
                  if (GameSettings.isKeyDown(ClientProxy.keyCycleToolMenuL)) {
                     if (!this.keyCycleBeforeL) {
                        --this.selectedItem;
                        if (this.selectedItem < 0) {
                           this.selectedItem = numItems - 1;
                        }

                        this.setMousePosition((double)x + (double)itemRadius * Math.cos(-1.5707963267948966D - (double)(this.selectedItem * 2) * 3.141592653589793D / (double)numItems), (double)y + (double)itemRadius * Math.sin(-1.5707963267948966D + (double)(this.selectedItem * 2) * 3.141592653589793D / (double)numItems));
                     }

                     this.keyCycleBeforeL = true;
                  } else {
                     this.keyCycleBeforeL = false;
                  }

                  if (GameSettings.isKeyDown(ClientProxy.keyCycleToolMenuR)) {
                     if (!this.keyCycleBeforeR) {
                        if (this.selectedItem < 0) {
                           this.selectedItem = 0;
                        } else {
                           ++this.selectedItem;
                           if (this.selectedItem >= numItems) {
                              this.selectedItem = 0;
                           }
                        }

                        this.setMousePosition((double)x + (double)itemRadius * Math.cos(-1.5707963267948966D - (double)(this.selectedItem * 2) * 3.141592653589793D / (double)numItems), (double)y + (double)itemRadius * Math.sin(-1.5707963267948966D + (double)(this.selectedItem * 2) * 3.141592653589793D / (double)numItems));
                     }

                     this.keyCycleBeforeR = true;
                  } else {
                     this.keyCycleBeforeR = false;
                  }
               }

               double a = Math.toDegrees(Math.atan2((double)(mouseY - y), (double)(mouseX - x)));
               double d = Math.sqrt(Math.pow((double)(mouseX - x), 2.0D) + Math.pow((double)(mouseY - y), 2.0D));
               float s0 = (-0.5F / (float)numItems + 0.25F) * 360.0F;
               if (a < (double)s0) {
                  a += 360.0D;
               }

               GlStateManager.pushMatrix();
               GlStateManager.disableAlpha();
               GlStateManager.enableBlend();
               GlStateManager.disableTexture2D();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.translate(0.0F, animTop, 0.0F);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder buffer = tessellator.getBuffer();
               buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               boolean hasMouseOver = false;
               ItemStack itemMouseOver = ItemStack.EMPTY;
               if (!this.closing) {
                  this.selectedItem = -1;

                  for(int i = 0; i < numItems; ++i) {
                     float s = (((float)i - 0.5F) / (float)numItems + 0.25F) * 360.0F;
                     float e = (((float)i + 0.5F) / (float)numItems + 0.25F) * 360.0F;
                     if (a >= (double)s && a < (double)e && d >= (double)radiusIn && d < (double)radiusOut && a >= (double)s && a < (double)e && d >= (double)radiusIn && (d < (double)radiusOut || Config.clipMouseToCircle || Config.allowClickOutsideBounds)) {
                        this.selectedItem = i;
                        break;
                     }
                  }
               }

               for(int i = 0; i < numItems; ++i) {
                  float s = (((float)i - 0.5F) / (float)numItems + 0.25F) * 360.0F;
                  float e = (((float)i + 0.5F) / (float)numItems + 0.25F) * 360.0F;
                  if (this.selectedItem == i) {
                     this.drawPieArc(buffer, (float)x, (float)y, this.zLevel, radiusIn, radiusOut, s, e, 255, 255, 255, 64);
                     if (i > 0 || !hasAddButton) {
                        hasMouseOver = true;
                        ItemStack inSlot = ItemStack.EMPTY;
                        if (hasAddButton) {
                           if (i > 0) {
                              inSlot = (ItemStack)items.get(i - 1);
                           }
                        } else {
                           inSlot = (ItemStack)items.get(i);
                        }

                        itemMouseOver = inSlot;
                     }
                  } else {
                     this.drawPieArc(buffer, (float)x, (float)y, this.zLevel, radiusIn, radiusOut, s, e, 0, 0, 0, 64);
                  }
               }

               if (Config.clipMouseToCircle) {
                  double scaledX = (double)((float)Mouse.getX() - (float)this.mc.displayWidth / 2.0F);
                  double scaledY = (double)((float)Mouse.getY() - (float)this.mc.displayHeight / 2.0F);
                  double distance = Math.sqrt(scaledX * scaledX + scaledY * scaledY);
                  double radius = 60.0D * (double)(this.mc.displayWidth / this.width);
                  if (distance > radius) {
                     double fixedX = scaledX * radius / distance;
                     double fixedY = scaledY * radius / distance;
                     Mouse.setCursorPosition((int)((double)(this.mc.displayWidth / 2) + fixedX), (int)((double)(this.mc.displayHeight / 2) + fixedY));
                  }
               }

               tessellator.draw();
               GlStateManager.enableTexture2D();
               boolean hasItemInHand = inHand.getCount() > 0;
               if (hasMouseOver) {
                  if (hasItemInHand) {
                     if (itemMouseOver.getCount() > 0) {
                        this.drawCenteredString(this.fontRenderer, I18n.format("text.toolbelt.swap", new Object[0]), this.width / 2, (this.height - this.fontRenderer.FONT_HEIGHT) / 2, -1);
                     } else {
                        this.drawCenteredString(this.fontRenderer, I18n.format("text.toolbelt.insert", new Object[0]), this.width / 2, (this.height - this.fontRenderer.FONT_HEIGHT) / 2, -1);
                     }
                  } else if (itemMouseOver.getCount() > 0) {
                     this.drawCenteredString(this.fontRenderer, I18n.format("text.toolbelt.extract", new Object[0]), this.width / 2, (this.height - this.fontRenderer.FONT_HEIGHT) / 2, -1);
                  }
               }

               if (Config.displayEmptySlots) {
                  for(int i = 0; i < this.inventory.getSlots(); ++i) {
                     ItemStack inSlot = this.inventory.getStackInSlot(i);
                     if (inSlot.getCount() <= 0) {
                        float angle1 = ((float)i / (float)numItems + 0.25F) * 2.0F * 3.1415927F;
                        float posX = (float)x + itemRadius * (float)Math.cos((double)angle1);
                        float posY = (float)y + itemRadius * (float)Math.sin((double)angle1);
                        this.drawCenteredString(this.fontRenderer, I18n.format("text.toolbelt.empty", new Object[0]), (int)posX, (int)posY - this.fontRenderer.FONT_HEIGHT / 2, Integer.MAX_VALUE);
                     }
                  }
               }

               RenderHelper.enableGUIStandardItemLighting();

               for(int i = 0; i < numItems; ++i) {
                  float angle1 = ((float)i / (float)numItems + 0.25F) * 2.0F * 3.1415927F;
                  float posX = (float)(x - 8) + itemRadius * (float)Math.cos((double)angle1);
                  float posY = (float)(y - 8) + itemRadius * (float)Math.sin((double)angle1);
                  ItemStack inSlot = ItemStack.EMPTY;
                  if (hasAddButton) {
                     if (i > 0) {
                        inSlot = (ItemStack)items.get(i - 1);
                     }
                  } else {
                     inSlot = (ItemStack)items.get(i);
                  }

                  if (inSlot.getCount() > 0) {
                     this.itemRender.renderItemAndEffectIntoGUI(inSlot, (int)posX, (int)posY);
                     this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, inSlot, (int)posX, (int)posY, "");
                  }
               }

               RenderHelper.disableStandardItemLighting();
               GlStateManager.popMatrix();
               if (itemMouseOver.getCount() > 0) {
                  this.renderToolTip(itemMouseOver, mouseX, mouseY);
               }

            }
         }
      }
   }

   private void setMousePosition(double x, double y) {
      Mouse.setCursorPosition((int)(x * (double)this.mc.displayWidth / (double)this.width), (int)(y * (double)this.mc.displayHeight / (double)this.height));
   }

   private void drawPieArc(BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
      float angle = endAngle - startAngle;
      int sections = Math.max(1, MathHelper.ceil(angle / 5.0F));
      startAngle = (float)Math.toRadians((double)startAngle);
      endAngle = (float)Math.toRadians((double)endAngle);
      angle = endAngle - startAngle;

      for(int i = 0; i < sections; ++i) {
         float angle1 = startAngle + (float)i / (float)sections * angle;
         float angle2 = startAngle + (float)(i + 1) / (float)sections * angle;
         float pos1InX = x + radiusIn * (float)Math.cos((double)angle1);
         float pos1InY = y + radiusIn * (float)Math.sin((double)angle1);
         float pos1OutX = x + radiusOut * (float)Math.cos((double)angle1);
         float pos1OutY = y + radiusOut * (float)Math.sin((double)angle1);
         float pos2OutX = x + radiusOut * (float)Math.cos((double)angle2);
         float pos2OutY = y + radiusOut * (float)Math.sin((double)angle2);
         float pos2InX = x + radiusIn * (float)Math.cos((double)angle2);
         float pos2InY = y + radiusIn * (float)Math.sin((double)angle2);
         buffer.pos((double)pos1OutX, (double)pos1OutY, (double)z).color(r, g, b, a).endVertex();
         buffer.pos((double)pos1InX, (double)pos1InY, (double)z).color(r, g, b, a).endVertex();
         buffer.pos((double)pos2InX, (double)pos2InY, (double)z).color(r, g, b, a).endVertex();
         buffer.pos((double)pos2OutX, (double)pos2OutY, (double)z).color(r, g, b, a).endVertex();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
