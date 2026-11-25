package xzeroair.trinkets.util.compat.artemislib;

import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.TrinketsConfig;

public class SizeAttribute {
   private final String id = "xat.";
   private String name;
   protected final UUID uuid = UUID.fromString("02a31a03-4d5b-46ed-947b-322c4f004a75");
   protected final UUID uuidW = UUID.fromString("4ff77810-5a91-44ce-8e93-fc05a8d00def");
   private int operation;
   private EntityLivingBase entity;
   private double height;
   private double width;

   public SizeAttribute(EntityLivingBase entity, double height, double width, int operation) {
      this.entity = entity;
      this.name = "xat.artemislib.size.attribute";
      this.height = height;
      this.width = width;
      this.operation = operation;
   }

   public void addHeightModifier() {
      if (!TrinketsConfig.compat.artemislib) {
         this.removeHeightModifier();
      } else {
         IAttributeInstance AttributeInstance = this.entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT);
         if (AttributeInstance != null) {
            double clampH = MathHelper.clamp(this.height, -0.75D, 1024.0D);
            if (AttributeInstance.getModifier(this.uuid) != null && AttributeInstance.getModifier(this.uuid).getAmount() != clampH) {
               this.removeHeightModifier();
            }

            if (AttributeInstance.getModifier(this.uuid) == null) {
               AttributeModifier modifier = (new AttributeModifier(this.uuid, this.name + ".height", clampH, this.operation)).setSaved(false);
               AttributeInstance.applyModifier(modifier);
            }

         }
      }
   }

   public void addWidthModifier() {
      if (!TrinketsConfig.compat.artemislib) {
         this.removeWidthModifier();
      } else {
         IAttributeInstance AttributeInstance = this.entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH);
         if (AttributeInstance != null) {
            double clampW = MathHelper.clamp(this.width, -0.75D, 1024.0D);
            if (AttributeInstance.getModifier(this.uuidW) != null && AttributeInstance.getModifier(this.uuidW).getAmount() != clampW) {
               this.removeWidthModifier();
            }

            if (AttributeInstance.getModifier(this.uuidW) == null) {
               AttributeModifier modifier = (new AttributeModifier(this.uuidW, this.name + ".width", clampW, this.operation)).setSaved(false);
               AttributeInstance.applyModifier(modifier);
            }

         }
      }
   }

   public void addModifiers() {
      if (Trinkets.ArtemisLib) {
         if (this.height != 0.0D) {
            this.addHeightModifier();
         }

         if (this.width != 0.0D) {
            this.addWidthModifier();
         }
      }

   }

   public void removeModifiers() {
      if (Trinkets.ArtemisLib) {
         this.removeHeightModifier();
         this.removeWidthModifier();
      }

   }

   public void removeHeightModifier() {
      IAttributeInstance AttributeInstance = this.entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT);
      if (AttributeInstance != null) {
         if (AttributeInstance.getModifier(this.uuid) != null) {
            AttributeInstance.removeModifier(this.uuid);
         }

      }
   }

   public void removeWidthModifier() {
      IAttributeInstance AttributeInstanceW = this.entity.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH);
      if (AttributeInstanceW != null) {
         if (AttributeInstanceW.getModifier(this.uuidW) != null) {
            AttributeInstanceW.removeModifier(this.uuidW);
         }

      }
   }
}
