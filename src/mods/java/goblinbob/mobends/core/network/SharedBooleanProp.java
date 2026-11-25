package goblinbob.mobends.core.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

public class SharedBooleanProp extends SharedProperty<Boolean> {
   public SharedBooleanProp(String key, Boolean value, String description) {
      super(key, value, description);
   }

   public Boolean getValue() {
      return (Boolean)super.getValue();
   }

   public void writeToNBT(NBTTagCompound tag) {
      tag.setBoolean(this.key, ((Boolean)this.value).booleanValue());
   }

   public void readFromNBT(NBTTagCompound tag) {
      this.value = (T)tag.getBoolean(this.key);
   }

   public void updateWithConfig(Configuration configuration, String category) {
      this.value = (T)configuration.get(category, this.key, ((Boolean)this.defaultValue).booleanValue(), this.description).getBoolean();
   }
}
