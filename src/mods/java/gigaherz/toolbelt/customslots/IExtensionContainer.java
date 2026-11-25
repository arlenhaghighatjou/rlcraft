package gigaherz.toolbelt.customslots;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;

public interface IExtensionContainer {
   @Nonnull
   EntityLivingBase getOwner();

   @Nonnull
   ImmutableList<IExtensionSlot> getSlots();

   void onContentsChanged(IExtensionSlot var1);
}
