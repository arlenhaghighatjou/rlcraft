package slimeknights.mantle.typesafe.config.impl;

import java.util.HashMap;
import java.util.Map;

final class ResolveMemos {
   private final Map<MemoKey, AbstractConfigValue> memos;

   private ResolveMemos(Map<MemoKey, AbstractConfigValue> memos) {
      this.memos = memos;
   }

   ResolveMemos() {
      this(new HashMap());
   }

   AbstractConfigValue get(MemoKey key) {
      return (AbstractConfigValue)this.memos.get(key);
   }

   ResolveMemos put(MemoKey key, AbstractConfigValue value) {
      Map<MemoKey, AbstractConfigValue> copy = new HashMap(this.memos);
      copy.put(key, value);
      return new ResolveMemos(copy);
   }
}
