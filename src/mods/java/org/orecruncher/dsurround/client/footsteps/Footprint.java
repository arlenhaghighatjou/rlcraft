package org.orecruncher.dsurround.client.footsteps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.footstep.FootprintStyle;

@SideOnly(Side.CLIENT)
public class Footprint {
   private FootprintStyle style;
   private EntityLivingBase entity;
   private Vec3d stepLoc;
   private boolean isRightFoot;
   private float rotation;
   private float scale;

   public Footprint() {
   }

   public static Footprint produce(@Nonnull FootprintStyle style, @Nonnull EntityLivingBase entity, @Nonnull Vec3d stepLoc, float rotation, float scale, boolean rightFoot) {
      Footprint print = new Footprint();
      print.style = style;
      print.entity = entity;
      print.stepLoc = stepLoc;
      print.rotation = rotation;
      print.isRightFoot = rightFoot;
      print.scale = scale;
      return print;
   }

   public FootprintStyle getStyle() {
      return this.style;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   @Nullable
   public Vec3d getStepLocation() {
      return this.stepLoc;
   }

   public boolean isRightFoot() {
      return this.isRightFoot;
   }

   public float getRotation() {
      return this.rotation;
   }

   public float getScale() {
      return this.scale;
   }
}
