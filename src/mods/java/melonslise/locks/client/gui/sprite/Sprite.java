package melonslise.locks.client.gui.sprite;

import java.util.ArrayDeque;
import java.util.Queue;
import melonslise.locks.client.gui.sprite.action.IAction;
import melonslise.locks.client.util.LocksClientUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Sprite {
   private Queue<IAction> actions = new ArrayDeque(4);
   public Texture tex;
   public float posX;
   public float posY;
   public float oldPosX;
   public float oldPosY;
   public float speedX;
   public float speedY;
   public float rot;
   public float oldRot;
   public float rotSpeed;
   public float originX;
   public float originY;
   public float alpha = 1.0F;
   public float oldAlpha = 1.0F;

   public Sprite(Texture tex) {
      this.tex = tex;
   }

   public Sprite position(float posX, float posY) {
      this.posX = this.oldPosX = posX;
      this.posY = this.oldPosY = posY;
      return this;
   }

   public Sprite rotation(float rot, float originX, float originY) {
      this.rot = this.oldRot = rot;
      this.originX = originX;
      this.originY = originY;
      return this;
   }

   public Sprite alpha(float alpha) {
      this.alpha = this.oldAlpha = alpha;
      return this;
   }

   public void execute(IAction... actions) {
      for(IAction action : actions) {
         this.actions.offer(action);
      }

   }

   public boolean isExecuting() {
      return !this.actions.isEmpty();
   }

   public void draw(float partialTick) {
      if (this.alpha > 0.0F) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.originX, this.originY, 0.0F);
         GlStateManager.rotate(LocksClientUtil.lerp(this.oldRot, this.rot, partialTick), 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(-this.originX, -this.originY, 0.0F);
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, LocksClientUtil.lerp(this.oldAlpha, this.alpha, partialTick));
         this.tex.draw(LocksClientUtil.lerp(this.oldPosX, this.posX, partialTick), LocksClientUtil.lerp(this.oldPosY, this.posY, partialTick), LocksClientUtil.lerp(this.oldAlpha, this.alpha, partialTick));
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public void update() {
      this.oldPosX = this.posX;
      this.oldPosY = this.posY;
      this.oldRot = this.rot;
      this.oldAlpha = this.alpha;
      this.posX += this.speedX;
      this.posY += this.speedY;
      this.rot += this.rotSpeed;
      IAction action = (IAction)this.actions.peek();
      if (action != null) {
         if (action.isFinished(this)) {
            ((IAction)this.actions.poll()).finish(this);
         } else {
            action.update(this);
         }

      }
   }
}
