package vazkii.quark.base.network.message;

import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.module.GlobalConfig;

public class MessageChangeConfig extends NetworkMessage<MessageChangeConfig> {
   public String category;
   public String key;
   public String value;
   public boolean saveToFile;
   public char propertyType;

   public MessageChangeConfig() {
   }

   public MessageChangeConfig(String category, String key, String value, Type propertyType, boolean saveToFile) {
      this.category = category;
      this.key = key;
      this.value = value;
      this.propertyType = propertyType.getID();
      this.saveToFile = saveToFile;
   }

   public IMessage handleMessage(MessageContext context) {
      GlobalConfig.changeConfig(this.category, this.key, this.value, Type.tryParse(this.propertyType), this.saveToFile);
      return null;
   }
}
