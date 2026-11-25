package stanhebben.zenscript.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class NFA {
   public static final int NOFINAL = Integer.MIN_VALUE;
   public static final int EPSILON = -2147483647;
   public static final int UNICODE_ESCAPE = 256;
   private final NFA.NFAState initial;
   private HashMap<NFA.NodeSet, DFA.DFAState> converted;

   public NFA(NFA.NFAState initial) {
      this.initial = initial;
   }

   public NFA(String regexp) {
      NFA.Partial main = this.processRegExp(new CharStream(regexp));
      this.initial = new NFA.NFAState();
      this.initial.addTransition(main.tailLabel, main.tail);
      main.head.setFinal(1);
   }

   public NFA(String[] regexp, int[] finals) {
      this.initial = new NFA.NFAState();
      NFA.Partial[] partials = new NFA.Partial[regexp.length];

      for(int i = 0; i < regexp.length; ++i) {
         partials[i] = this.processRegExp(new CharStream(regexp[i]));
         partials[i].head.setFinal(finals[i]);
         this.initial.addTransition(partials[i].tailLabel, partials[i].tail);
      }

   }

   public DFA toDFA() {
      this.converted = new HashMap();
      HashSet<NFA.NFAState> closure = new HashSet();
      this.initial.closure(closure);
      DFA.DFAState init = this.convert(new NFA.NodeSet(closure));
      return new DFA(init);
   }

   private DFA.DFAState convert(NFA.NodeSet nodes) {
      if (this.converted.containsKey(nodes)) {
         return (DFA.DFAState)this.converted.get(nodes);
      } else {
         DFA.DFAState node = new DFA.DFAState();
         this.converted.put(nodes, node);
         HashSet<Integer> edgeSet = new HashSet();

         for(NFA.NFAState n : nodes.nodes) {
            n.alphabet(edgeSet);
         }

         Iterator var11 = edgeSet.iterator();

         while(var11.hasNext()) {
            int i = ((Integer)var11.next()).intValue();
            HashSet<NFA.NFAState> edge = new HashSet();

            for(NFA.NFAState n : nodes.nodes) {
               n.edge(i, edge);
            }

            NFA.NodeSet set = new NFA.NodeSet(edge);
            node.addTransition(i, this.convert(set));
         }

         int finalCode = Integer.MIN_VALUE;

         for(NFA.NFAState n : nodes.nodes) {
            if (n.finalCode != Integer.MIN_VALUE) {
               if (finalCode != Integer.MIN_VALUE) {
                  finalCode = Math.min(n.finalCode, finalCode);
               } else {
                  finalCode = n.finalCode;
               }
            }
         }

         node.setFinal(finalCode);
         return node;
      }
   }

   private NFA.Partial processRegExp(CharStream stream) {
      NFA.Partial partial = this.processRegExp0(stream);
      if (!stream.optional('|')) {
         return partial;
      } else {
         ArrayList<NFA.Partial> partials = new ArrayList();
         partials.add(partial);
         partials.add(this.processRegExp0(stream));

         while(stream.optional('|')) {
            partials.add(this.processRegExp0(stream));
         }

         NFA.NFAState head = new NFA.NFAState();
         NFA.NFAState tail = new NFA.NFAState();

         for(NFA.Partial p : partials) {
            tail.addTransition(p.tailLabel, p.tail);
            p.head.addTransition(-2147483647, head);
         }

         return new NFA.Partial(-2147483647, tail, head);
      }
   }

   private NFA.Partial processRegExp0(CharStream stream) {
      NFA.Partial partial;
      NFA.Partial partial2;
      for(partial = this.processRegExp1(stream); !stream.peek(')') && !stream.peek('|') && stream.hasMore(); partial = new NFA.Partial(partial.tailLabel, partial.tail, partial2.head)) {
         partial2 = this.processRegExp1(stream);
         partial.head.addTransition(partial2.tailLabel, partial2.tail);
      }

      return partial;
   }

   private NFA.Partial processRegExp1(CharStream stream) {
      NFA.Partial partial = this.processPartial(stream);
      if (stream.optional('*')) {
         NFA.NFAState node = new NFA.NFAState();
         partial.head.addTransition(-2147483647, node);
         node.addTransition(partial.tailLabel, partial.tail);
         return new NFA.Partial(-2147483647, node, node);
      } else if (stream.optional('+')) {
         NFA.NFAState node = new NFA.NFAState();
         partial.head.addTransition(-2147483647, node);
         node.addTransition(partial.tailLabel, partial.tail);
         return new NFA.Partial(partial.tailLabel, partial.tail, node);
      } else if (stream.optional('?')) {
         NFA.NFAState node = new NFA.NFAState();
         node.addTransition(-2147483647, partial.head);
         node.addTransition(partial.tailLabel, partial.tail);
         return new NFA.Partial(-2147483647, node, partial.head);
      } else if (!stream.optional('{')) {
         return partial;
      } else {
         int amount = this.processInt(stream);
         if (amount < 0) {
            throw new IllegalArgumentException("Repitition count expected");
         } else if (stream.optional(',')) {
            int amount2 = this.processInt(stream);
            stream.required('}');
            if (amount2 < 0) {
               NFA.Partial[] duplicates = new NFA.Partial[amount];

               for(int i = 0; i < duplicates.length - 1; ++i) {
                  duplicates[i] = partial.duplicate();
               }

               duplicates[amount - 1] = partial;

               for(int i = 0; i < duplicates.length - 1; ++i) {
                  duplicates[i].head.addTransition(duplicates[i + 1].tailLabel, duplicates[i + 1].tail);
               }

               duplicates[amount - 1].head.addTransition(duplicates[amount - 1].tailLabel, duplicates[amount - 1].tail);
               return new NFA.Partial(duplicates[0].tailLabel, duplicates[0].tail, duplicates[amount - 1].head);
            } else {
               NFA.Partial[] duplicates = new NFA.Partial[amount2];

               for(int i = 0; i < duplicates.length - 1; ++i) {
                  duplicates[i] = partial.duplicate();
               }

               duplicates[amount2 - 1] = partial;

               for(int i = 0; i < duplicates.length - 1; ++i) {
                  duplicates[i].head.addTransition(duplicates[i + 1].tailLabel, duplicates[i + 1].tail);
               }

               for(int i = amount; i < amount2; ++i) {
                  if (i == 0) {
                     NFA.NFAState additional = new NFA.NFAState();
                     additional.addTransition(duplicates[0].tailLabel, duplicates[0].tail);
                     additional.addTransition(-2147483647, duplicates[amount2 - 1].head);
                     duplicates[0].tailLabel = -2147483647;
                     duplicates[0].tail = additional;
                  } else {
                     duplicates[i - 1].head.addTransition(-2147483647, duplicates[amount2 - 1].head);
                  }
               }

               return new NFA.Partial(duplicates[0].tailLabel, duplicates[0].tail, duplicates[amount2 - 1].head);
            }
         } else {
            stream.required('}');
            NFA.Partial[] duplicates = new NFA.Partial[amount];

            for(int i = 0; i < duplicates.length - 1; ++i) {
               duplicates[i] = partial.duplicate();
            }

            duplicates[amount - 1] = partial;

            for(int i = 0; i < duplicates.length - 1; ++i) {
               duplicates[i].head.addTransition(duplicates[i + 1].tailLabel, duplicates[i + 1].tail);
            }

            return new NFA.Partial(duplicates[0].tailLabel, duplicates[0].tail, duplicates[amount - 1].head);
         }
      }
   }

   private NFA.Partial processPartial(CharStream stream) {
      if (stream.optional('(')) {
         NFA.Partial result = this.processRegExp(stream);
         stream.required(')');
         return result;
      } else if (stream.optional('[')) {
         NFA.NFAState head = new NFA.NFAState();
         NFA.NFAState tail = new NFA.NFAState();
         IteratorI iter = this.processCharList(stream).iterator();

         while(iter.hasNext()) {
            tail.addTransition(iter.next(), head);
         }

         stream.required(']');
         return new NFA.Partial(-2147483647, tail, head);
      } else if (!stream.optional('.')) {
         NFA.NFAState node = new NFA.NFAState();
         return new NFA.Partial(this.processChar(stream), node, node);
      } else {
         NFA.NFAState head = new NFA.NFAState();
         NFA.NFAState tail = new NFA.NFAState();

         for(int i = 0; i <= 256; ++i) {
            tail.addTransition(i, head);
         }

         return new NFA.Partial(-2147483647, tail, head);
      }
   }

   private HashSetI processCharList(CharStream stream) {
      boolean invert = stream.optional('^');
      HashSetI base = new HashSetI();

      while(true) {
         this.processCharPartial(base, stream);
         if (stream.peek(']')) {
            break;
         }
      }

      if (invert) {
         HashSetI result = new HashSetI();

         for(int i = 0; i <= 256; ++i) {
            if (!base.contains(i)) {
               result.add(i);
            }
         }

         return result;
      } else {
         return base;
      }
   }

   private void processCharPartial(HashSetI out, CharStream stream) {
      if (stream.optional('.')) {
         for(int i = 0; i <= 256; ++i) {
            out.add(i);
         }
      } else {
         int from = this.processChar(stream);
         if (stream.optional('-')) {
            int to = this.processChar(stream);

            for(int i = from; i <= to; ++i) {
               out.add(i);
            }
         } else {
            out.add(from);
         }
      }

   }

   private int processChar(CharStream stream) {
      if (stream.optional('\\')) {
         if (stream.optional('u')) {
            return 256;
         } else if (stream.optional('e')) {
            return -1;
         } else if (stream.optional('r')) {
            return 13;
         } else if (stream.optional('n')) {
            return 10;
         } else if (stream.optional('t')) {
            return 9;
         } else if (stream.optional('[')) {
            return 91;
         } else if (stream.optional(']')) {
            return 93;
         } else if (stream.optional('(')) {
            return 40;
         } else if (stream.optional(')')) {
            return 41;
         } else if (stream.optional('.')) {
            return 46;
         } else if (stream.optional('+')) {
            return 43;
         } else if (stream.optional('-')) {
            return 45;
         } else if (stream.optional('\\')) {
            return 92;
         } else if (stream.optional('{')) {
            return 123;
         } else if (stream.optional('}')) {
            return 125;
         } else if (stream.optional('?')) {
            return 63;
         } else if (stream.optional('*')) {
            return 42;
         } else if (stream.optional('~')) {
            return 126;
         } else if (stream.optional('|')) {
            return 124;
         } else if (stream.optional('^')) {
            return 94;
         } else {
            throw new IllegalArgumentException("Invalid character: " + stream.next());
         }
      } else if (!stream.peek('[') && !stream.peek(']') && !stream.peek('(') && !stream.peek(')') && !stream.peek('{') && !stream.peek('}') && !stream.peek('.') && !stream.peek('-') && !stream.peek('+') && !stream.peek('?') && !stream.peek('*')) {
         return stream.next();
      } else {
         throw new IllegalArgumentException("Invalid character: " + stream.next());
      }
   }

   private int processInt(CharStream stream) {
      int data = stream.optional('0', '9') - 48;
      if (data < 0) {
         return -1;
      } else {
         for(char ch = stream.optional('0', '9'); ch != 0; ch = stream.optional('0', '9')) {
            data = data * 10 + (ch - 48);
         }

         return data;
      }
   }

   public static class NFAState {
      private static int counter = 1;
      private ArrayList<NFA.NFAState.Transition> transitions = new ArrayList();
      private ArrayList<NFA.NFAState> closure;
      private int index;
      private int finalCode = Integer.MIN_VALUE;

      public NFAState() {
         this.index = counter++;
      }

      public void addTransition(int label, NFA.NFAState next) {
         this.transitions.add(new NFA.NFAState.Transition(label, next));
      }

      public void setFinal(int finalCode) {
         if (this.finalCode != finalCode) {
            this.finalCode = finalCode;
            this.transitions.stream().filter((t) -> {
               return t.label == -2147483647;
            }).forEach((t) -> {
               t.next.setFinal(finalCode);
            });
         }
      }

      private void closure(HashSet<NFA.NFAState> output) {
         output.addAll(this.closure());
      }

      private ArrayList<NFA.NFAState> closure() {
         if (this.closure == null) {
            HashSet<NFA.NFAState> tmp = new HashSet();
            tmp.add(this);

            for(NFA.NFAState.Transition transition : this.transitions) {
               if (transition.label == -2147483647 && !tmp.contains(transition.next)) {
                  tmp.add(transition.next);
                  transition.next.closure(tmp);
               }
            }

            this.closure = new ArrayList();
            this.closure.addAll(tmp);
         }

         return this.closure;
      }

      private void edge(int label, HashSet<NFA.NFAState> output) {
         this.transitions.stream().filter((transition) -> {
            return (void)(transition.label == label);
         }).filter((transition) -> {
            return (void)(!output.contains(transition.next));
         }).forEach((transition) -> {
            output.add(transition.next);
            transition.next.closure(output);
         });
      }

      private void alphabet(HashSet<Integer> output) {
         for(NFA.NFAState node : this.closure()) {
            for(NFA.NFAState.Transition t : node.transitions) {
               if (t.label != -2147483647) {
                  output.add(Integer.valueOf(t.label));
               }
            }
         }

      }

      private class Transition {
         private int label;
         private NFA.NFAState next;

         public Transition(int input, NFA.NFAState next) {
            this.label = input;
            this.next = next;
         }
      }
   }

   private class NodeSet {
      private NFA.NFAState[] nodes;

      public NodeSet(HashSet<NFA.NFAState> nodes) {
         this.nodes = new NFA.NFAState[nodes.size()];
         int i = 0;

         for(NFA.NFAState node : nodes) {
            this.nodes[i++] = node;
         }

         Arrays.sort(this.nodes, Comparator.comparingInt((o) -> {
            return (void)o.index;
         }));
      }

      public int hashCode() {
         return Arrays.hashCode(this.nodes);
      }

      public boolean equals(Object other) {
         return other.getClass() == NFA.NodeSet.class && Arrays.equals(this.nodes, ((NFA.NodeSet)other).nodes);
      }
   }

   private static class Partial {
      private int tailLabel;
      private NFA.NFAState tail;
      private NFA.NFAState head;

      public Partial(int tailLabel, NFA.NFAState tail, NFA.NFAState head) {
         this.tailLabel = tailLabel;
         this.tail = tail;
         this.head = head;
      }

      public NFA.Partial duplicate() {
         HashMap<NFA.NFAState, NFA.NFAState> nodes = new HashMap();
         Queue<NFA.NFAState> todo = new LinkedList();
         todo.add(this.tail);
         nodes.put(this.tail, new NFA.NFAState());

         while(!todo.isEmpty()) {
            NFA.NFAState node = (NFA.NFAState)todo.poll();

            for(NFA.NFAState.Transition t : node.transitions) {
               if (!nodes.containsKey(t.next)) {
                  nodes.put(t.next, new NFA.NFAState());
                  todo.add(t.next);
               }

               ((NFA.NFAState)nodes.get(node)).addTransition(t.label, (NFA.NFAState)nodes.get(t.next));
            }
         }

         return new NFA.Partial(this.tailLabel, (NFA.NFAState)nodes.get(this.tail), (NFA.NFAState)nodes.get(this.head));
      }
   }
}
