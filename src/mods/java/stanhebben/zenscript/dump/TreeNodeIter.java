package stanhebben.zenscript.dump;

import java.util.Iterator;

public class TreeNodeIter<T> implements Iterator<TreeNode<T>> {
   private TreeNode<T> treeNode;
   private TreeNodeIter.ProcessStages doNext;
   private TreeNode<T> next;
   private Iterator<TreeNode<T>> childrenCurNodeIter;
   private Iterator<TreeNode<T>> childrenSubNodeIter;

   public TreeNodeIter(TreeNode<T> treeNode) {
      this.treeNode = treeNode;
      this.doNext = TreeNodeIter.ProcessStages.ProcessParent;
      this.childrenCurNodeIter = treeNode.children.iterator();
   }

   public boolean hasNext() {
      if (this.doNext == TreeNodeIter.ProcessStages.ProcessParent) {
         this.next = this.treeNode;
         this.doNext = TreeNodeIter.ProcessStages.ProcessChildCurNode;
         return true;
      } else if (this.doNext == TreeNodeIter.ProcessStages.ProcessChildCurNode) {
         if (this.childrenCurNodeIter.hasNext()) {
            TreeNode<T> childDirect = (TreeNode)this.childrenCurNodeIter.next();
            this.childrenSubNodeIter = childDirect.iterator();
            this.doNext = TreeNodeIter.ProcessStages.ProcessChildSubNode;
            return this.hasNext();
         } else {
            this.doNext = null;
            return false;
         }
      } else if (this.doNext == TreeNodeIter.ProcessStages.ProcessChildSubNode) {
         if (this.childrenSubNodeIter.hasNext()) {
            this.next = (TreeNode)this.childrenSubNodeIter.next();
            return true;
         } else {
            this.next = null;
            this.doNext = TreeNodeIter.ProcessStages.ProcessChildCurNode;
            return this.hasNext();
         }
      } else {
         return false;
      }
   }

   public TreeNode<T> next() {
      return this.next;
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   static enum ProcessStages {
      ProcessParent,
      ProcessChildCurNode,
      ProcessChildSubNode;

      private ProcessStages() {
      }
   }
}
