package codersafterdark.reskillable.skill.agility;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.client.base.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TraitSidestep extends Trait {
   public static final int MAX_CD = 20;
   private int leftDown;
   private int rightDown;
   private int cd;

   public TraitSidestep() {
      super(new ResourceLocation("reskillable", "sidestep"), 3, 1, new ResourceLocation("reskillable", "agility"), 10, "reskillable:agility|26", "reskillable:defense|20");
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.register(MinecraftForge.EVENT_BUS);
      }

   }

   public void register(EventBus bus) {
      bus.register(ClientTickEvent.class, this::clientTick);
      bus.register(KeyInputEvent.class, this::onKeyDown);
      bus.register(Post.class, this::renderHUD);
   }

   @SideOnly(Side.CLIENT)
   public void clientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && this.cd > 0) {
         --this.cd;
      }

   }

   @SideOnly(Side.CLIENT)
   public void onKeyDown(KeyInputEvent event) {
      if (this.cd <= 0) {
         Minecraft mc = Minecraft.getMinecraft();
         if (PlayerDataHandler.get(mc.player).getSkillInfo(this.getParentSkill()).isUnlocked(this) && !mc.player.isSneaking()) {
            int threshold = 4;
            if (mc.gameSettings.keyBindLeft.isKeyDown()) {
               int oldLeft = this.leftDown;
               this.leftDown = ClientTickHandler.ticksInGame;
               if (this.leftDown - oldLeft < threshold) {
                  this.dodge(mc.player, true);
               }
            } else if (mc.gameSettings.keyBindRight.isKeyDown()) {
               int oldRight = this.rightDown;
               this.rightDown = ClientTickHandler.ticksInGame;
               if (this.rightDown - oldRight < threshold) {
                  this.dodge(mc.player, false);
               }
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void dodge(EntityPlayer player, boolean left) {
      if (!player.capabilities.isFlying && player.onGround && player.moveForward < 0.0F && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSprint)) {
         float yaw = player.rotationYaw;
         float x = MathHelper.sin(-yaw * 0.017453292F - 3.1415927F);
         float z = MathHelper.cos(-yaw * 0.017453292F - 3.1415927F);
         Vec3d lookVec = new Vec3d((double)x, 0.0D, (double)z);
         Vec3d sideVec = lookVec.crossProduct(new Vec3d(0.0D, left ? 1.0D : -1.0D, 0.0D)).scale(1.25D);
         player.motionX = sideVec.x;
         player.motionY = sideVec.y;
         player.motionZ = sideVec.z;
         this.cd = 20;
      }
   }

   @SideOnly(Side.CLIENT)
   public void renderHUD(Post event) {
      if (event.getType() == ElementType.ALL) {
         ScaledResolution res = event.getResolution();
         Minecraft mc = Minecraft.getMinecraft();
         int xo = res.getScaledWidth() / 2 - 20;
         int y = res.getScaledHeight() / 2 + 20;
         GlStateManager.enableBlend();
         if (!mc.player.capabilities.isFlying) {
            int width = Math.min((int)(((float)this.cd - event.getPartialTicks()) * 2.0F), 40);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (width > 0) {
               Gui.drawRect(xo, y - 2, xo + 40, y - 1, 1140850688);
               Gui.drawRect(xo, y - 2, xo + width, y - 1, -1);
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
