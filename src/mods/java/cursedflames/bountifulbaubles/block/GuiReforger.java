package cursedflames.bountifulbaubles.block;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.client.gui.GuiBetterButton;
import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import cursedflames.bountifulbaubles.util.Util;
import cursedflames.bountifulbaubles.util.XpUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class GuiReforger extends GuiContainer {
   public static final int WIDTH = 176;
   public static final int HEIGHT = 162;
   private static final ResourceLocation background = new ResourceLocation("bountifulbaubles", "textures/gui/reforger.png");
   private static final String tranStr = "bountifulbaubles.gui.reforger.";
   private ContainerReforger cont;
   private TileReforger te;
   private ItemStackHandler handler;

   public GuiReforger(TileReforger tileEntity, ContainerReforger container) {
      super(container);
      this.cont = container;
      this.te = tileEntity;
      this.handler = (ItemStackHandler)tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      this.xSize = 176;
      this.ySize = 162;
   }

   public void initGui() {
      super.initGui();
      GuiBetterButton button = new GuiBetterButton(0, this.guiLeft + 10, this.guiTop + 26, 85, 16, BountifulBaubles.proxy.translate("bountifulbaubles.gui.reforger.reforge"));
      button.buttonPressSound = false;
      this.buttonList.add(button);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.drawDefaultBackground();
      this.mc.getTextureManager().bindTexture(background);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.guiLeft + 4), (float)(this.guiTop + 4), 0.0F);
      GlStateManager.disableLighting();
      ItemStack stack = this.handler.getStackInSlot(0);
      if (stack.hasTagCompound()) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         int xp = XpUtil.getPlayerXP(player);
         NBTTagCompound tag = stack.getTagCompound();
         int xpCost = 0;
         if (tag.hasKey("reforgeCost")) {
            xpCost = tag.getInteger("reforgeCost");
         }

         boolean inCreative = player.isCreative();
         ((GuiButton)this.buttonList.get(0)).enabled = xp > xpCost || inCreative;
         int levelCost = 1 + (xpCost > xp ? XpUtil.getLevelForExperience(xpCost) : player.experienceLevel - XpUtil.getLevelForExperience(xp - xpCost));
         String xpCostStr = BountifulBaubles.proxy.translate("bountifulbaubles.gui.reforger.xpcost");
         String levelsStr;
         if (BountifulBaubles.proxy.hasTranslationKey("bountifulbaubles.gui.reforger.levels.override." + levelCost)) {
            levelsStr = BountifulBaubles.proxy.translateWithArgs("bountifulbaubles.gui.reforger.levels.override." + levelCost, levelCost);
         } else {
            levelsStr = BountifulBaubles.proxy.translateWithArgs("bountifulbaubles.gui.reforger.levels", levelCost);
         }

         this.fontRenderer.drawString(xpCostStr + " " + xpCost + (inCreative ? "" : " " + levelsStr), 0, 0, xp <= xpCost && !inCreative ? 16733525 : 5635925);
         if (tag.hasKey("baubleModifier")) {
            this.fontRenderer.drawSplitString(BountifulBaubles.proxy.translate("bountifulbaubles.modifier." + tag.getString("baubleModifier") + ".info"), 0, 1 + this.fontRenderer.FONT_HEIGHT, 16777215, 30);
         }
      }

      GlStateManager.popMatrix();
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void actionPerformed(GuiButton button) {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setTag("pos", Util.blockPosToNBT(this.te.getPos()));
      PacketHandler.INSTANCE.sendToServer(new NBTPacket(tag, PacketHandler.HandlerIds.REFORGE.id));
   }
}
