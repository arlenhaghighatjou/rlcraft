package tschipp.callablehorses.client.gui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tschipp.callablehorses.common.capabilities.horseowner.IHorseOwner;
import tschipp.callablehorses.common.helper.HorseHelper;

public class GuiStatViewer extends GuiScreen {
   private int xSize = 176;
   private int ySize = 138;
   private static final ResourceLocation TEXTURE = new ResourceLocation("callablehorses", "textures/gui/horse_stat_viewer.png");
   private EntityPlayer player;
   private IHorseOwner owner;
   private AbstractHorse horse;
   private float speed;
   private float jumpHeight;
   private float health;
   private float maxHealth;
   private BlockPos lastPos;
   private int lastDim;
   private String lastDimString;

   public GuiStatViewer(EntityPlayer player) {
      this.player = player;
      this.owner = HorseHelper.getOwnerCap(player);
      this.horse = this.owner.getHorseEntity(player.world);
      SharedMonsterAttributes.setAttributeModifiers(this.horse.getAttributeMap(), this.owner.getHorseNBT().getTagList("Attributes", 10));
      this.horse.readFromNBT(this.owner.getHorseNBT());
      IItemHandler horseInventory = (IItemHandler)this.horse.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      this.horse.setHorseSaddled(!horseInventory.getStackInSlot(0).isEmpty() && this.horse.canBeSaddled());
      if (this.horse instanceof EntityLlama) {
         Method setColor = ReflectionHelper.findMethod(EntityLlama.class, "setColor", "setColor", new Class[]{EnumDyeColor.class});
         if (setColor != null) {
            try {
               ItemStack stack = horseInventory.getStackInSlot(1);
               if (this.horse.isArmor(stack)) {
                  setColor.invoke(this.horse, EnumDyeColor.byMetadata(stack.getMetadata()));
               } else {
                  setColor.invoke(this.horse, (EnumDyeColor)null);
               }
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var5) {
               var5.printStackTrace();
            }
         }
      }

      this.health = (float)Math.floor((double)this.horse.getHealth());
      this.maxHealth = (float)(Math.floor((double)(this.horse.getMaxHealth() * 10.0F)) / 10.0D);
      this.speed = (float)(Math.floor(this.horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 100.0D) / 10.0D);
      this.jumpHeight = (float)(Math.floor(this.horse.getHorseJumpStrength() * 100.0D) / 10.0D);
      this.lastPos = this.owner.getLastSeenPosition();
      this.lastDim = this.owner.getLastSeenDim();
      this.lastDimString = DimensionManager.getProviderType(this.lastDim).getName();
   }

   public void initGui() {
      super.initGui();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      super.drawScreen(mouseX, mouseY, partialTicks);
      GuiInventory.drawEntityOnScreen(i + 43, j + 68, 25, (float)(i + 51) - (float)mouseX, (float)(j + 75 - 50) - (float)mouseY, this.horse);
      this.drawString(this.fontRenderer, this.horse.getName(), i + 84, j + 10, EnumDyeColor.WHITE.getColorValue());
      this.drawString(this.fontRenderer, "Health:", i + 84, j + 30, EnumDyeColor.SILVER.getColorValue());
      this.drawString(this.fontRenderer, this.health + "/" + this.maxHealth, i + 120, j + 30, EnumDyeColor.WHITE.getColorValue());
      this.drawString(this.fontRenderer, "Speed:", i + 84, j + 45, EnumDyeColor.SILVER.getColorValue());
      this.drawString(this.fontRenderer, this.speed + "", i + 120, j + 45, EnumDyeColor.WHITE.getColorValue());
      this.drawString(this.fontRenderer, "Jump Height:", i + 84, j + 60, EnumDyeColor.SILVER.getColorValue());
      this.drawString(this.fontRenderer, this.jumpHeight + "", i + 148, j + 60, EnumDyeColor.WHITE.getColorValue());
      this.drawString(this.fontRenderer, "Last known position:", i + 8, j + 84, EnumDyeColor.SILVER.getColorValue());
      this.drawString(this.fontRenderer, this.lastPos.equals(BlockPos.ORIGIN) ? "Unknown" : "xyz = " + this.lastPos.getX() + " " + this.lastPos.getY() + " " + this.lastPos.getZ(), i + 8, j + 94, EnumDyeColor.WHITE.getColorValue());
      this.drawString(this.fontRenderer, "Last known dimension:", i + 8, j + 110, EnumDyeColor.SILVER.getColorValue());
      this.drawString(this.fontRenderer, this.lastDimString, i + 8, j + 120, EnumDyeColor.WHITE.getColorValue());
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
         this.mc.player.closeScreen();
      }

   }
}
