package com.bloodnbonesgaming.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ConsumableMessage implements IMessage {
   private boolean consumed;

   public ConsumableMessage() {
      this(false);
   }

   public ConsumableMessage(boolean consumed) {
      this.consumed = consumed;
   }

   public boolean isConsumed() {
      return this.consumed;
   }

   public void setConsumed(boolean consumed) {
      this.consumed = consumed;
   }
}
