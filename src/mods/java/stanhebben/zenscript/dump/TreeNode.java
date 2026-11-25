package stanhebben.zenscript.dump;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> implements Iterable<TreeNode<T>> {
   public T data;
   public TreeNode<T> parent;
   public List<TreeNode<T>> children;
   private List<TreeNode<T>> elementsIndex;

   public boolean isRoot() {
      return this.parent == null;
   }

   public boolean isLeaf() {
      return this.children.size() == 0;
   }

   public TreeNode(T data) {
      this.data = data;
      this.children = new LinkedList();
      this.elementsIndex = new LinkedList();
      this.elementsIndex.add(this);
   }

   public TreeNode<T> addChild(T child) {
      TreeNode<T> childNode = new TreeNode<T>(child);
      childNode.parent = this;
      this.children.add(childNode);
      this.registerChildForSearch(childNode);
      return childNode;
   }

   public int getLevel() {
      return this.isRoot() ? 0 : this.parent.getLevel() + 1;
   }

   private void registerChildForSearch(TreeNode<T> node) {
      this.elementsIndex.add(node);
      if (this.parent != null) {
         this.parent.registerChildForSearch(node);
      }

   }

   public TreeNode<T> findTreeNode(Comparable<T> cmp) {
      for(TreeNode<T> element : this.elementsIndex) {
         T elData = element.data;
         if (cmp.compareTo(elData) == 0) {
            return element;
         }
      }

      return null;
   }

   public String toString() {
      return this.data != null ? this.data.toString() : "[data null]";
   }

   public Iterator<TreeNode<T>> iterator() {
      return new TreeNodeIter<TreeNode<T>>(this);
   }
}
