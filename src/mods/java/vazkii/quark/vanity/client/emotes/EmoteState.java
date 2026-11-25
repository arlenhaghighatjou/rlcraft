package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EmoteState {
   private float[] states = new float[0];
   private final EmoteBase emote;

   public EmoteState(EmoteBase emote) {
      this.emote = emote;
   }

   public void save(ModelBiped model) {
      float[] values = new float[1];

      for(int i = 0; i < 42; ++i) {
         ModelAccessor.INSTANCE.getValues(model, i, values);
         this.states[i] = values[0];
      }

   }

   public void load(ModelBiped model) {
      if (this.states.length == 0) {
         this.states = new float[42];
      } else {
         float[] values = new float[1];

         for(int i = 0; i < 42; ++i) {
            values[0] = this.states[i];
            int part = i / 6 * 6;
            if (this.emote.usesBodyPart(part)) {
               ModelAccessor.INSTANCE.setValues(model, i, values);
            }
         }
      }

   }

   public void rotateAndOffset(EntityPlayer player) {
      if (this.states.length != 0) {
         float offsetX = this.states[39];
         float offsetY = this.states[40];
         float offsetZ = this.states[41];
         float rotX = this.states[36];
         float rotY = this.states[37];
         float rotZ = this.states[38];
         float height = player.height;
         GlStateManager.translate(0.0F, height / 2.0F, 0.0F);
         GlStateManager.translate(offsetX, offsetY, offsetZ);
         if (rotY != 0.0F) {
            GlStateManager.rotate(rotY * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
         }

         if (rotX != 0.0F) {
            GlStateManager.rotate(rotX * 180.0F / 3.1415927F, 1.0F, 0.0F, 0.0F);
         }

         if (rotZ != 0.0F) {
            GlStateManager.rotate(rotZ * 180.0F / 3.1415927F, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.translate(0.0F, -height / 2.0F, 0.0F);
      }
   }
}
