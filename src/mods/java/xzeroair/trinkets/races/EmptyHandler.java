package xzeroair.trinkets.races;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.handlers.SizeHandler;
import xzeroair.trinkets.util.helpers.AttributeHelper;

public class EmptyHandler extends EntityRacePropertiesHandler {
   public EmptyHandler(EntityLivingBase e) {
      this(e, EntityRaces.none);
   }

   public EmptyHandler(EntityLivingBase e, EntityRace race) {
      super(e, race);
   }

   public void startTransformation() {
   }

   public void endTransformation() {
   }

   protected void initAttributes() {
      EntityRace previous = this.properties.getPreviousRace();
      double d = Double.parseDouble(Reference.DECIMALFORMAT.format(1.0D - this.TransformationProgress()));
      if (d != 0.0D) {
         String[] raceAttributes = previous.getRaceAttributes().getAttributes();
         if (raceAttributes.length > 0) {
            for(String entry : raceAttributes) {
               ConfigHelper.AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
               if (attributeShell != null) {
                  String name = attributeShell.getAttribute();
                  double amount = attributeShell.getAmount();
                  int operation = attributeShell.getOperation();
                  boolean isSaved = attributeShell.isSaved();
                  UpdatingAttribute attribute = (new UpdatingAttribute(previous.getName() + "." + name, previous.getUUID(), name)).setSavedInNBT(false);
                  attribute.addModifier(this.entity, amount * d, operation);
               }
            }
         }
      }

   }

   public void onTick() {
      if (this.entity instanceof EntityPlayer) {
         if (this.isTransforming()) {
            SizeHandler.setSize(this.entity, this.getHeight(), this.getWidth());
            this.updateSize();
            this.initAttributes();
            this.eyeHeightHandler();
         } else if (this.firstTransformUpdate) {
            EntityRace previous = this.properties.getPreviousRace();
            AttributeHelper.removeAttributesByUUID(this.entity, previous.getUUID());
            ((EntityPlayer)this.entity).eyeHeight = ((EntityPlayer)this.entity).getDefaultEyeHeight();
            this.firstTransformUpdate = false;
         }
      } else if (this.isTransforming()) {
         this.updateSize();
         this.eyeHeightHandler();
      } else if (this.firstTransformUpdate) {
         float height = this.properties.getDefaultHeight();
         float width = this.properties.getDefaultWidth();
         SizeHandler.setSize(this.entity, height, width);
         this.firstTransformUpdate = false;
      }

   }

   public void interact(PlayerInteractEvent event) {
   }

   public boolean isTransformed() {
      return false;
   }
}
