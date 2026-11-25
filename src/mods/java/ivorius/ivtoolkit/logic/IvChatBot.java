package ivorius.ivtoolkit.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class IvChatBot {
   public Random random;
   public List<IvChatLine> sendQueue = new ArrayList();

   public IvChatBot(Random rand) {
      this.random = rand;
   }

   public String update() {
      this.updateIdle();
      if (this.sendQueue.size() > 0) {
         IvChatLine currentLine = (IvChatLine)this.sendQueue.get(0);
         --currentLine.delay;
         if (currentLine.delay <= 0) {
            this.sendQueue.remove(0);
            return currentLine.lineString;
         }
      }

      return null;
   }

   public void addMessageToSendQueue(String message, int delay) {
      this.sendQueue.add(new IvChatLine(delay, message));
   }

   public void addMessageToSendQueue(String message, int minDelay, int maxDelay) {
      this.sendQueue.add(new IvChatLine(this.random.nextInt(maxDelay - minDelay + 1) + minDelay, message));
   }

   public void addMessageToSendQueue(String message) {
      this.addMessageToSendQueue(message, 10, 80);
   }

   public void addMessagesToSendQueue(String[] messages) {
      for(String s : messages) {
         this.addMessageToSendQueue(s);
      }

   }

   public abstract void updateIdle();

   public abstract void receiveChatMessage(String var1);
}
