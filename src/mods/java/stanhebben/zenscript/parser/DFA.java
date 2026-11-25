package stanhebben.zenscript.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class DFA {
   public static final int NOFINAL = Integer.MIN_VALUE;
   private DFA.DFAState initial;

   public DFA(DFA.DFAState initial) {
      this.initial = initial;
   }

   public CompiledDFA compile() {
      ArrayList<DFA.DFAState> nodeList = new ArrayList();
      HashMap<DFA.DFAState, Integer> nodes = new HashMap();
      nodes.put(this.initial, Integer.valueOf(0));
      nodeList.add(this.initial);
      int counter = 1;
      Queue<DFA.DFAState> todo = new LinkedList();
      todo.add(this.initial);

      while(!todo.isEmpty()) {
         DFA.DFAState current = (DFA.DFAState)todo.poll();
         IteratorI it = current.transitions.keys();

         while(it.hasNext()) {
            int k = it.next();
            DFA.DFAState next = current.transitions.get(k);
            if (!nodes.containsKey(next)) {
               todo.add(next);
               nodes.put(next, Integer.valueOf(counter++));
               nodeList.add(next);
            }
         }
      }

      HashMapII[] transitions = new HashMapII[counter];
      int[] finals2 = new int[counter];

      for(DFA.DFAState node : nodeList) {
         int index = ((Integer)nodes.get(node)).intValue();
         finals2[index] = node.finalCode;
         transitions[index] = new HashMapII();
         IteratorI it = node.transitions.keys();

         while(it.hasNext()) {
            int k = it.next();
            DFA.DFAState next = node.transitions.get(k);
            transitions[index].put(k, ((Integer)nodes.get(next)).intValue());
         }
      }

      return new CompiledDFA(transitions, finals2);
   }

   public DFA optimize() {
      CompiledDFA compiled = this.compile();
      HashMapII[] transitions = compiled.transitions;
      int size = transitions.length;
      HashSetI alphabet = new HashSetI();

      for(HashMapII transition : transitions) {
         IteratorI it = transition.keys();

         while(it.hasNext()) {
            int k = it.next();
            alphabet.add(k);
         }
      }

      boolean[][] distinguishable = new boolean[size + 1][size + 1];

      for(int i = 0; i < size; ++i) {
         for(int j = 0; j < size; ++j) {
            distinguishable[i][j] = compiled.finals[i] != compiled.finals[j];
         }
      }

      for(int i = 0; i < size; ++i) {
         distinguishable[i][size] = true;
         distinguishable[size][i] = true;
      }

      while(true) {
         boolean changed = false;
         IteratorI ita = alphabet.iterator();

         while(ita.hasNext()) {
            int x = ita.next();

            for(int i = 0; i < size; ++i) {
               int ti = transitions[i].get(x, size);

               for(int j = 0; j < size; ++j) {
                  if (!distinguishable[i][j]) {
                     int tj = transitions[j].get(x, size);
                     if (distinguishable[ti][tj]) {
                        distinguishable[i][j] = true;
                        changed = true;
                     }
                  }
               }
            }
         }

         if (!changed) {
            break;
         }
      }

      HashMapI<DFA.DFAState> nodeMap = new HashMapI<DFA.DFAState>();

      for(int i = 0; i < size; ++i) {
         int j = 0;

         while(true) {
            if (j >= size) {
               DFA.DFAState node = new DFA.DFAState();
               node.setFinal(compiled.finals[i]);
               nodeMap.put(i, node);
               break;
            }

            if (!distinguishable[i][j] && nodeMap.containsKey(j)) {
               nodeMap.put(i, nodeMap.get(j));
               if (compiled.finals[i] != Integer.MIN_VALUE && ((DFA.DFAState)nodeMap.get(j)).getFinal() != Integer.MIN_VALUE && ((DFA.DFAState)nodeMap.get(j)).getFinal() != compiled.finals[i]) {
                  throw new RuntimeException("Eh?");
               }
               break;
            }

            ++j;
         }
      }

      for(int i = 0; i < compiled.transitions.length; ++i) {
         IteratorI iter = transitions[i].keys();

         while(iter.hasNext()) {
            int k = iter.next();
            ((DFA.DFAState)nodeMap.get(i)).addTransition(k, nodeMap.get(transitions[i].get(k)));
         }
      }

      return new DFA(nodeMap.get(0));
   }

   public String toString() {
      StringBuilder result = new StringBuilder();
      CompiledDFA dfs = this.compile();

      for(int i = 0; i < dfs.transitions.length; ++i) {
         HashMapII map = dfs.transitions[i];
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

      for(int i = 0; i < dfs.finals.length; ++i) {
         if (dfs.finals[i] != Integer.MIN_VALUE) {
            result.append("final(");
            result.append(i);
            result.append("): ");
            result.append(dfs.finals[i]);
            result.append("\r\n");
         }
      }

      return result.toString();
   }

   public static class DFAState {
      private HashMapI<DFA.DFAState> transitions = new HashMapI<DFA.DFAState>();
      private int finalCode = Integer.MIN_VALUE;

      public DFAState() {
      }

      public void addTransition(int label, DFA.DFAState next) {
         this.transitions.put(label, next);
      }

      public int getFinal() {
         return this.finalCode;
      }

      public void setFinal(int finalCode) {
         this.finalCode = finalCode;
      }
   }
}
