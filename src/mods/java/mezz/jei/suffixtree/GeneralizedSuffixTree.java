package mezz.jei.suffixtree;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.Objects;
import javax.annotation.Nullable;

public class GeneralizedSuffixTree implements ISearchTree {
   private int highestIndex = -1;
   private final Node root = new Node();
   private Node activeLeaf;

   public GeneralizedSuffixTree() {
      this.activeLeaf = this.root;
   }

   public IntSet search(String word) {
      Node tmpNode = this.searchNode(word);
      if (tmpNode == null) {
         return new IntOpenHashSet();
      } else {
         IntSet ret = new IntOpenHashSet(1000);
         tmpNode.getData(ret);
         return ret;
      }
   }

   @Nullable
   private Node searchNode(String word) {
      Node currentNode = this.root;

      for(int i = 0; i < word.length(); ++i) {
         char ch = word.charAt(i);
         Edge currentEdge = currentNode.getEdge(ch);
         if (null == currentEdge) {
            return null;
         }

         String label = currentEdge.getLabel();
         int lenToMatch = Math.min(word.length() - i, label.length());
         if (!word.regionMatches(i, label, 0, lenToMatch)) {
            return null;
         }

         if (label.length() >= word.length() - i) {
            return currentEdge.getDest();
         }

         currentNode = currentEdge.getDest();
         i += lenToMatch - 1;
      }

      return null;
   }

   public void put(String key, int index) throws IllegalStateException {
      if (index < this.highestIndex) {
         throw new IllegalStateException("The input index must not be less than any of the previously inserted ones. Got " + index + ", expected at least " + this.highestIndex);
      } else {
         this.highestIndex = index;
         this.activeLeaf = this.root;
         Node s = this.root;
         String text = "";

         for(int i = 0; i < key.length(); ++i) {
            GeneralizedSuffixTree.Pair<Node, String> active = this.update(s, text, key.charAt(i), key.substring(i), index);
            s = active.getFirst();
            text = active.getSecond();
         }

         if (null == this.activeLeaf.getSuffix() && this.activeLeaf != this.root && this.activeLeaf != s) {
            this.activeLeaf.setSuffix(s);
         }

      }
   }

   private GeneralizedSuffixTree.Pair<Boolean, Node> testAndSplit(Node inputs, String stringPart, char t, String remainder, int value) {
      GeneralizedSuffixTree.Pair<Node, String> ret = this.canonize(inputs, stringPart);
      Node s = ret.getFirst();
      String str = ret.getSecond();
      if (!"".equals(str)) {
         Edge g = s.getEdge(str.charAt(0));
         Objects.requireNonNull(g);
         String label = g.getLabel();
         if (label.length() > str.length() && label.charAt(str.length()) == t) {
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(true, s);
         } else {
            String newlabel = label.substring(str.length());

            assert label.startsWith(str);

            Node r = new Node();
            Edge newedge = new Edge(str, r);
            g.setLabel(newlabel);
            r.addEdge(newlabel.charAt(0), g);
            s.addEdge(str.charAt(0), newedge);
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(false, r);
         }
      } else {
         Edge e = s.getEdge(t);
         if (null == e) {
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(false, s);
         } else if (remainder.equals(e.getLabel())) {
            e.getDest().addRef(value);
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(true, s);
         } else if (remainder.startsWith(e.getLabel())) {
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(true, s);
         } else if (e.getLabel().startsWith(remainder)) {
            Node newNode = new Node();
            newNode.addRef(value);
            Edge newEdge = new Edge(remainder, newNode);
            e.setLabel(e.getLabel().substring(remainder.length()));
            newNode.addEdge(e.getLabel().charAt(0), e);
            s.addEdge(t, newEdge);
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(false, s);
         } else {
            return new GeneralizedSuffixTree.Pair<Boolean, Node>(true, s);
         }
      }
   }

   private GeneralizedSuffixTree.Pair<Node, String> canonize(Node s, String inputstr) {
      if ("".equals(inputstr)) {
         return new GeneralizedSuffixTree.Pair<Node, String>(s, inputstr);
      } else {
         Node currentNode = s;
         String str = inputstr;
         Edge g = s.getEdge(inputstr.charAt(0));

         while(g != null && str.startsWith(g.getLabel())) {
            str = str.substring(g.getLabel().length());
            currentNode = g.getDest();
            if (str.length() > 0) {
               g = currentNode.getEdge(str.charAt(0));
            }
         }

         return new GeneralizedSuffixTree.Pair<Node, String>(currentNode, str);
      }
   }

   private GeneralizedSuffixTree.Pair<Node, String> update(Node inputNode, String stringPart, char newChar, String rest, int value) {
      Node s = inputNode;
      String tempstr = stringPart + newChar;
      Node oldroot = this.root;
      GeneralizedSuffixTree.Pair<Boolean, Node> ret = this.testAndSplit(inputNode, stringPart, newChar, rest, value);
      Node r = ret.getSecond();

      for(boolean endpoint = ((Boolean)ret.getFirst()).booleanValue(); !endpoint; endpoint = ((Boolean)ret.getFirst()).booleanValue()) {
         Edge tempEdge = r.getEdge(newChar);
         Node leaf;
         if (null != tempEdge) {
            leaf = tempEdge.getDest();
         } else {
            leaf = new Node();
            leaf.addRef(value);
            Edge newedge = new Edge(rest, leaf);
            r.addEdge(newChar, newedge);
         }

         if (this.activeLeaf != this.root) {
            this.activeLeaf.setSuffix(leaf);
         }

         this.activeLeaf = leaf;
         if (oldroot != this.root) {
            oldroot.setSuffix(r);
         }

         oldroot = r;
         if (null == s.getSuffix()) {
            assert this.root == s;

            tempstr = tempstr.substring(1);
         } else {
            GeneralizedSuffixTree.Pair<Node, String> canret = this.canonize(s.getSuffix(), safeCutLastChar(tempstr));
            s = canret.getFirst();
            tempstr = (String)canret.getSecond() + tempstr.charAt(tempstr.length() - 1);
         }

         ret = this.testAndSplit(s, safeCutLastChar(tempstr), newChar, rest, value);
         r = ret.getSecond();
      }

      if (oldroot != this.root) {
         oldroot.setSuffix(r);
      }

      return this.canonize(s, tempstr);
   }

   private static String safeCutLastChar(String seq) {
      return seq.length() == 0 ? "" : seq.substring(0, seq.length() - 1);
   }

   public int getHighestIndex() {
      return this.highestIndex;
   }

   public void trimToSize() {
      ArrayDeque<Node> nodes = new ArrayDeque(128);
      nodes.add(this.root);

      while(!nodes.isEmpty()) {
         Node node = (Node)nodes.remove();
         node.trimToSize();
         Node suffix = node.getSuffix();
         if (suffix != null) {
            suffix.trimToSize();
         }

         ObjectIterator var4 = node.edges().iterator();

         while(var4.hasNext()) {
            Edge edge = (Edge)var4.next();
            nodes.add(edge.getDest());
         }
      }

   }

   private static class Pair<A, B> {
      private final A first;
      private final B second;

      public Pair(A first, B second) {
         this.first = first;
         this.second = second;
      }

      public A getFirst() {
         return this.first;
      }

      public B getSecond() {
         return this.second;
      }

      public String toString() {
         return "Pair (" + this.first + ", " + this.second + ")";
      }
   }
}
