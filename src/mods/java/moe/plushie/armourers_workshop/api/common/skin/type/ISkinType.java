package moe.plushie.armourers_workshop.api.common.skin.type;

import java.util.ArrayList;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISkinType {
   ArrayList<ISkinPartType> getSkinParts();

   String getRegistryName();

   String getName();

   @SideOnly(Side.CLIENT)
   ResourceLocation getIcon();

   @SideOnly(Side.CLIENT)
   ResourceLocation getSlotIcon();

   boolean showSkinOverlayCheckbox();

   boolean showHelperCheckbox();

   int getVanillaArmourSlotId();

   boolean isHidden();

   boolean enabled();

   ArrayList<ISkinProperty<?>> getProperties();

   boolean haveBoundsChanged(ISkinProperties var1, ISkinProperties var2);
}
