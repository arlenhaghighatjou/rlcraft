package stanhebben.zenscript.parser;

import java.util.Arrays;

public class CompiledDFA {
   public static final int NOFINAL = Integer.MIN_VALUE;
   public HashMapII[] transitions;
   public int[] finals;

   public CompiledDFA(HashMapII[] transitions, int[] finals) {
      this.transitions = transitions;
      this.finals = finals;
   }

   public CompiledDFA(int[] definition) {
      int ix = 0;
      int numStates = definition[ix++];
      this.transitions = new HashMapII[numStates];
      this.finals = new int[numStates];

      for(int i = 0; i < numStates; ++i) {
         this.transitions[i] = new HashMapII();
         this.finals[i] = definition[ix++];
         int numRanges = definition[ix++];

         for(int j = 0; j < numRanges; ++j) {
            int from = definition[ix++];
            int to = definition[ix++];
            int state = definition[ix++];

            for(int k = from; k <= to; ++k) {
               this.transitions[i].put(k, state);
            }
         }

         int numSingles = definition[ix++];

         for(int j = 0; j < numSingles; ++j) {
            int label = definition[ix++];
            int state = definition[ix++];
            this.transitions[i].put(label, state);
         }
      }

   }

   public int[] toArray() {
      ArrayListI result = new ArrayListI();
      result.add(this.finals.length);

      for(int i = 0; i < this.finals.length; ++i) {
         result.add(this.finals[i]);
         ArrayListI singles = new ArrayListI();
         ArrayListI rangeFrom = new ArrayListI();
         ArrayListI rangeTo = new ArrayListI();
         int[] keys = this.transitions[i].keysArray();
         Arrays.sort(keys);

         for(int j = 0; j < keys.length; ++j) {
            int from = j;

            for(int state = this.transitions[i].get(j); j + 1 < keys.length && keys[j + 1] == keys[j] + 1 && this.transitions[i].get(j + 1) == state; ++j) {
               ;
            }

            if (j == from) {
               singles.add(keys[j]);
            } else {
               rangeFrom.add(keys[from]);
               rangeTo.add(keys[j]);
            }
         }

         result.add(rangeFrom.size());

         for(int j = 0; j < rangeFrom.size(); ++j) {
            result.add(rangeFrom.get(j));
            result.add(rangeTo.get(j));
            result.add(this.transitions[i].get(rangeFrom.get(j)));
         }

         result.add(singles.size());

         for(int j = 0; j < singles.size(); ++j) {
            result.add(singles.get(j));
            result.add(this.transitions[i].get(singles.get(j)));
         }
      }

      return result.toArray();
   }

   public String toString() {
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < this.transitions.length; ++i) {
         HashMapII map = this.transitions[i];
         IteratorI it = map.keys();

         while(it.hasNext()) {
            int v = it.next();
            result.append("edge(");
            result.append(i);
            result.append(", ");
            result.append(v);
            result.append("): ");
            result.append(map.get(v));
            result.append("\r\n");
         }
      }

      for(int i = 0; i < this.finals.length; ++i) {
         if (this.finals[i] != Integer.MIN_VALUE) {
            result.append("final(");
            result.append(i);
            result.append("): ");
            result.append(this.finals[i]);
            result.append("\r\n");
         }
      }

      return result.toString();
   }
}
