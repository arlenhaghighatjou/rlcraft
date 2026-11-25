package betterquesting.api2.client.gui.help;

import java.util.ArrayList;
import java.util.List;

public class HelpRegistry {
   public static final HelpRegistry INSTANCE = new HelpRegistry();
   private final List<HelpTopic> topicList = new ArrayList();

   public HelpRegistry() {
      this.registerTopic(new HelpTopic("betterquesting.btn.help1", "betterquesting.help.page1"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help2", "betterquesting.help.page2"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help3", "betterquesting.help.page3"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help4", "betterquesting.help.page4"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help5", "betterquesting.help.page5"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help6", "betterquesting.help.page6"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help7", "betterquesting.help.page7"));
      this.registerTopic(new HelpTopic("betterquesting.btn.help8", "betterquesting.help.page8"));
   }

   public boolean registerTopic(HelpTopic topic) {
      return this.topicList.contains(topic) ? false : this.topicList.add(topic);
   }

   public HelpTopic[] getTopics() {
      return (HelpTopic[])this.topicList.toArray(new HelpTopic[0]);
   }
}
