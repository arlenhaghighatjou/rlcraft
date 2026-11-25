package me.lucko.spark.common.sampler.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

public abstract class AbstractNode {
   private static final int MAX_STACK_DEPTH = 300;
   private final Map<StackTraceNode.Description, StackTraceNode> children = new ConcurrentHashMap();
   private final LongAdder totalTime = new LongAdder();

   public AbstractNode() {
   }

   public double getTotalTime() {
      return (double)this.totalTime.longValue() / 1000.0D;
   }

   public Collection<StackTraceNode> getChildren() {
      return this.children.values();
   }

   public <T> void log(StackTraceNode.Describer<T> describer, T[] stack, long time) {
      if (stack.length != 0) {
         this.totalTime.add(time);
         AbstractNode node = this;
         T previousElement = null;

         for(int offset = 0; offset < Math.min(300, stack.length); ++offset) {
            T element = (T)stack[stack.length - 1 - offset];
            node = node.resolveChild(describer.describe(element, previousElement));
            node.totalTime.add(time);
            previousElement = element;
         }

      }
   }

   private StackTraceNode resolveChild(StackTraceNode.Description description) {
      StackTraceNode result = (StackTraceNode)this.children.get(description);
      return result != null ? result : (StackTraceNode)this.children.computeIfAbsent(description, StackTraceNode::new);
   }

   protected void merge(AbstractNode other) {
      this.totalTime.add(other.totalTime.longValue());

      for(Entry<StackTraceNode.Description, StackTraceNode> child : other.children.entrySet()) {
         this.resolveChild((StackTraceNode.Description)child.getKey()).merge((AbstractNode)child.getValue());
      }

   }

   protected List<StackTraceNode> exportChildren(MergeMode mergeMode) {
      if (this.children.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<StackTraceNode> list = new ArrayList(this.children.size());

         label27:
         for(StackTraceNode child : this.children.values()) {
            for(StackTraceNode other : list) {
               if (mergeMode.shouldMerge(other, child)) {
                  other.merge(child);
                  continue label27;
               }
            }

            list.add(child);
         }

         list.sort((Comparator)null);
         return list;
      }
   }
}
