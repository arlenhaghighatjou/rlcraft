package ichttt.mods.firstaid.api.damagesystem;

import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractPlayerDamageModel implements Iterable<AbstractDamageablePart>, INBTSerializable<NBTTagCompound> {
   public final AbstractDamageablePart HEAD;
   public final AbstractDamageablePart LEFT_ARM;
   public final AbstractDamageablePart LEFT_LEG;
   public final AbstractDamageablePart LEFT_FOOT;
   public final AbstractDamageablePart BODY;
   public final AbstractDamageablePart RIGHT_ARM;
   public final AbstractDamageablePart RIGHT_LEG;
   public final AbstractDamageablePart RIGHT_FOOT;
   public boolean hasTutorial;

   public AbstractPlayerDamageModel(AbstractDamageablePart head, AbstractDamageablePart leftArm, AbstractDamageablePart leftLeg, AbstractDamageablePart leftFoot, AbstractDamageablePart body, AbstractDamageablePart rightArm, AbstractDamageablePart rightLeg, AbstractDamageablePart rightFoot) {
      this.HEAD = head;
      this.LEFT_ARM = leftArm;
      this.LEFT_LEG = leftLeg;
      this.LEFT_FOOT = leftFoot;
      this.BODY = body;
      this.RIGHT_ARM = rightArm;
      this.RIGHT_LEG = rightLeg;
      this.RIGHT_FOOT = rightFoot;
   }

   public AbstractDamageablePart getFromEnum(EnumPlayerPart part) {
      switch(part) {
      case HEAD:
         return this.HEAD;
      case LEFT_ARM:
         return this.LEFT_ARM;
      case LEFT_LEG:
         return this.LEFT_LEG;
      case BODY:
         return this.BODY;
      case RIGHT_ARM:
         return this.RIGHT_ARM;
      case RIGHT_LEG:
         return this.RIGHT_LEG;
      case LEFT_FOOT:
         return this.LEFT_FOOT;
      case RIGHT_FOOT:
         return this.RIGHT_FOOT;
      default:
         throw new RuntimeException("Unknown enum " + part);
      }
   }

   public abstract void tick(World var1, EntityPlayer var2);

   /** @deprecated */
   @Deprecated
   public abstract void applyMorphine();

   public abstract void applyMorphine(EntityPlayer var1);

   /** @deprecated */
   @Deprecated
   public abstract int getMorphineTicks();

   /** @deprecated */
   @Deprecated
   public abstract float getCurrentHealth();

   public abstract boolean isDead(@Nullable EntityPlayer var1);

   public abstract Float getAbsorption();

   public abstract void setAbsorption(float var1);

   public abstract int getCurrentMaxHealth();

   @SideOnly(Side.CLIENT)
   public abstract int getMaxRenderSize();

   public abstract void sleepHeal(EntityPlayer var1);

   public abstract void stopWaitingForHelp(EntityPlayer var1);

   public abstract boolean isWaitingForHelp();

   /** @deprecated */
   @Deprecated
   public abstract void onNotHelped(EntityPlayer var1);

   /** @deprecated */
   @Deprecated
   public abstract void onHelpedUp(EntityPlayer var1);

   public abstract void revivePlayer(EntityPlayer var1);

   public abstract void runScaleLogic(EntityPlayer var1);

   public abstract void scheduleResync();

   public abstract boolean hasNoCritical();
}
