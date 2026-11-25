package vazkii.quark.client.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.text.WordUtils;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.item.ItemTrowel;

public class UsageTicker extends Feature {
   public static List<UsageTicker.TickerElement> elements;
   public static boolean invert;
   public static int shiftLeft;
   public static int shiftRight;

   public UsageTicker() {
   }

   public void setupConfig() {
      elements = new ArrayList();

      for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
         String config = "Enable " + WordUtils.capitalize(slot.getName());
         if (this.loadPropBool(config, "", true)) {
            elements.add(new UsageTicker.TickerElement(slot));
         }
      }

      invert = this.loadPropBool("Invert Displays", "Switch the armor display to the off hand side and the hand display to the main hand side", false);
      shiftLeft = this.loadPropInt("Left Side Offset", "", 0);
      shiftRight = this.loadPropInt("Right Side Offset", "", 0);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void clientTick(ClientTickEvent event) {
      if (event.phase == Phase.START) {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc.player != null) {
            elements.forEach((ticker) -> {
               ticker.tick(mc.player);
            });
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderHUD(Post event) {
      if (event.getType() == ElementType.HOTBAR) {
         ScaledResolution res = event.getResolution();
         EntityPlayer player = Minecraft.getMinecraft().player;
         float partial = event.getPartialTicks();
         elements.forEach((ticker) -> {
            ticker.render(res, player, invert, partial);
         });
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public static class TickerElement {
      private static final int MAX_TIME = 60;
      private static final int ANIM_TIME = 5;
      public int liveTicks;
      public final EntityEquipmentSlot slot;
      public ItemStack currStack = ItemStack.EMPTY;
      public int currCount;

      public TickerElement(EntityEquipmentSlot slot) {
         this.slot = slot;
      }

      @SideOnly(Side.CLIENT)
      public void tick(EntityPlayer player) {
         ItemStack heldStack = this.getStack(player);
         int count = this.getStackCount(player, heldStack);
         heldStack = this.getDisplayedStack(heldStack, count);
         if (heldStack.isEmpty()) {
            this.liveTicks = 0;
         } else if (this.shouldChange(heldStack, this.currStack, count, this.currCount)) {
            boolean done = this.liveTicks == 0;
            boolean animatingIn = this.liveTicks > 55;
            boolean animatingOut = this.liveTicks < 5 && !done;
            if (animatingOut) {
               this.liveTicks = 60 - this.liveTicks;
            } else if (!animatingIn) {
               if (!done) {
                  this.liveTicks = 55;
               } else {
                  this.liveTicks = 60;
               }
            }
         } else if (this.liveTicks > 0) {
            --this.liveTicks;
         }

         this.currCount = count;
         this.currStack = heldStack;
      }

      @SideOnly(Side.CLIENT)
      public void render(ScaledResolution res, EntityPlayer player, boolean invert, float partialTicks) {
         if (this.liveTicks > 0) {
            float animProgress;
            if (this.liveTicks < 5) {
               animProgress = Math.max(0.0F, (float)this.liveTicks - partialTicks) / 5.0F;
            } else {
               animProgress = Math.min(5.0F, (float)(60 - this.liveTicks) + partialTicks) / 5.0F;
            }

            float anim = -animProgress * (animProgress - 2.0F) * 20.0F;
            float x = (float)res.getScaledWidth() / 2.0F;
            float y = (float)res.getScaledHeight() - anim;
            int armorWidth = 80;
            int heldWidth = 40;
            int barWidth = 190;
            boolean armor = this.slot.getSlotType() == Type.ARMOR;
            EnumHandSide primary = player.getPrimaryHand();
            EnumHandSide ourSide = armor != invert ? primary : primary.opposite();
            int slots = armor ? 4 : 2;
            int index = slots - this.slot.getIndex() - 1;
            float mul = ourSide == EnumHandSide.LEFT ? -1.0F : 1.0F;
            if (ourSide != primary && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
               barWidth += 58;
            }

            Minecraft mc = Minecraft.getMinecraft();
            x = x + (float)barWidth / 2.0F * mul + (float)(index * 20);
            if (ourSide == EnumHandSide.LEFT) {
               x = x - (float)(slots * 20);
               x = x + (float)UsageTicker.shiftLeft;
            } else {
               x = x + (float)UsageTicker.shiftRight;
            }

            ItemStack stack = this.getRenderedStack(player);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0.0F);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
            mc.getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRenderer, stack, 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
         }

      }

      @SideOnly(Side.CLIENT)
      public boolean shouldChange(ItemStack currStack, ItemStack prevStack, int currentTotal, int pastTotal) {
         return !prevStack.isItemEqual(currStack) || currentTotal != pastTotal;
      }

      @SideOnly(Side.CLIENT)
      public ItemStack getStack(EntityPlayer player) {
         return player.getItemStackFromSlot(this.slot);
      }

      @SideOnly(Side.CLIENT)
      public ItemStack getDisplayedStack(ItemStack stack, int count) {
         boolean verifySize = true;
         if (stack.getItem() instanceof ItemBow && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) == 0) {
            stack = new ItemStack(Items.ARROW);
            verifySize = false;
         }

         if (stack.getItem() instanceof ItemTrowel) {
            stack = ItemTrowel.getLastStack(stack);
            verifySize = false;
         }

         if (!stack.isStackable() && this.slot.getSlotType() == Type.HAND) {
            return ItemStack.EMPTY;
         } else {
            return verifySize && stack.isStackable() && count == stack.getCount() ? ItemStack.EMPTY : stack;
         }
      }

      @SideOnly(Side.CLIENT)
      public ItemStack getRenderedStack(EntityPlayer player) {
         ItemStack stack = this.getStack(player);
         int count = this.getStackCount(player, stack);
         ItemStack displayStack = this.getDisplayedStack(stack, count).copy();
         if (displayStack != stack) {
            count = this.getStackCount(player, displayStack);
         }

         displayStack.setCount(count);
         return displayStack;
      }

      @SideOnly(Side.CLIENT)
      public int getStackCount(EntityPlayer player, ItemStack stack) {
         if (!stack.isStackable()) {
            return 1;
         } else {
            Predicate<ItemStack> predicate = (stackAtx) -> {
               return ItemStack.areItemsEqual(stackAt, stack) && ItemStack.areItemStackTagsEqual(stackAt, stack);
            };
            if (stack.getItem() == Items.ARROW) {
               predicate = (stackAtx) -> {
                  return stackAt.getItem() instanceof ItemArrow;
               };
            }

            int total = 0;

            for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
               ItemStack stackAt = player.inventory.getStackInSlot(i);
               if (predicate.test(stackAt)) {
                  total += stackAt.getCount();
               }
            }

            return total;
         }
      }
   }
}
