package invtweaks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class InvTweaksItemTreeBuilder {
   private static final Logger log = InvTweaks.log;
   private DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
   private Document newDoc;

   public InvTweaksItemTreeBuilder(@NotNull File file) throws ParserConfigurationException, SAXException, IOException {
      this.newDoc = this.docBuilder.parse(file);
   }

   public static boolean buildNewTree() {
      if (!InvTweaksConst.INVTWEAKS_TREES_DIR.exists()) {
         return false;
      } else {
         File base = new File(InvTweaksConst.INVTWEAKS_TREES_DIR, "minecraft.tree");
         if (!base.exists()) {
            base = InvTweaksConst.CONFIG_TREE_FILE;
         }

         if (!base.exists()) {
            return false;
         } else {
            try {
               log.info("Merging tree files.");
               InvTweaksItemTreeBuilder newTree = new InvTweaksItemTreeBuilder(base);
               File[] treeFiles = InvTweaksConst.INVTWEAKS_CONFIG_DIR.listFiles();

               for(File tree : treeFiles) {
                  if (tree.getName().endsWith(".tree")) {
                     try {
                        if (!tree.equals(base)) {
                           newTree.add(tree);
                        }
                     } catch (Exception var8) {
                        log.error("Unable to process partial Tree: " + tree.getName() + " " + var8.getMessage());
                     }
                  }
               }

               Transformer transformer = TransformerFactory.newInstance().newTransformer();
               transformer.setOutputProperty("indent", "yes");
               transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
               File treeFile = null;
               if (InvTweaksConst.TEMP_DIR.exists()) {
                  treeFile = InvTweaksConst.MERGED_TREE_FILE;
                  log.info("Saving merged tree in TEMP folder.");
               } else {
                  treeFile = InvTweaksConst.MERGED_TREE_FILE_ALT;
                  log.info("Saving merged tree in 'trees' folder.");
               }

               Result output = new StreamResult(treeFile);
               Source input = new DOMSource(newTree.newDoc);
               transformer.transform(input, output);
               log.info("Merged tree files.");
               return true;
            } catch (Exception var9) {
               log.error("Fail Building New Tree: " + var9.getMessage());
               return false;
            }
         }
      }
   }

   public static List<Element> getChildrenByTagName(Element parent, String name) {
      List<Element> nodeList = new ArrayList();

      for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
         if (child.getNodeType() == 1 && name.equals(child.getNodeName())) {
            nodeList.add((Element)child);
         }
      }

      return nodeList;
   }

   private static boolean matchingAttributes(Element i, Element j) {
      if (!i.hasAttributes() && !j.hasAttributes()) {
         return true;
      } else {
         String iClass = i.getAttribute("class");
         String jClass = j.getAttribute("class");
         String iId = i.getAttribute("id");
         String jId = j.getAttribute("id");
         String iDamage = i.getAttribute("damage");
         String jDamage = j.getAttribute("damage");
         String iData = i.getAttribute("data");
         String jData = j.getAttribute("data");
         String iOre = i.getAttribute("oreDictName");
         String jOre = j.getAttribute("oreDictName");
         if (iClass.equals(jClass) && iId.equals(jId) && iOre.equals(jOre)) {
            if (iClass.length() == 0 && iId.length() == 0) {
               return true;
            }

            if (iId.length() > 0 && iDamage.equals(jDamage) && iData.equals(jData)) {
               return true;
            }

            if (iClass.length() > 0 && iData.equals(jData)) {
               return true;
            }
         }

         return false;
      }
   }

   public void add(@NotNull File file) throws ParserConfigurationException, SAXException, IOException {
      Document otherDoc = this.docBuilder.parse(file);
      Element newElement = this.newDoc.getDocumentElement();
      Element otherElement = otherDoc.getDocumentElement();
      this.combine(newElement, otherElement);
   }

   private int getTreeOrder(Element element, int previousOrder) {
      String treeOrder = element.getAttribute("treeOrder");
      if (treeOrder.length() == 0) {
         return previousOrder;
      } else {
         try {
            return Integer.parseInt(treeOrder);
         } catch (NumberFormatException var5) {
            return previousOrder;
         }
      }
   }

   private void combine(Element existing, Element other) {
      int prevOtherChildNodeOrder = Integer.MAX_VALUE;

      for(Node otherChildNode = other.getFirstChild(); otherChildNode != null; otherChildNode = otherChildNode.getNextSibling()) {
         Element newChild = null;
         if (otherChildNode.getNodeType() == 1) {
            Element otherChildElement = (Element)otherChildNode;
            int otherChildOrder = this.getTreeOrder(otherChildElement, prevOtherChildNodeOrder);
            prevOtherChildNodeOrder = otherChildOrder;

            for(Element candidate : getChildrenByTagName(existing, otherChildElement.getTagName())) {
               if (matchingAttributes(candidate, otherChildElement)) {
                  newChild = candidate;
                  break;
               }
            }

            if (newChild != null) {
               this.combine(newChild, otherChildElement);
            } else {
               newChild = (Element)this.newDoc.importNode(otherChildElement, true);
               Node addBeforeNode = null;
               int prevMainChildOrder = 0;

               for(Node mainChildNode = existing.getFirstChild(); mainChildNode != null; mainChildNode = mainChildNode.getNextSibling()) {
                  int mainChildOrder = prevMainChildOrder;
                  if (mainChildNode.getNodeType() == 1) {
                     mainChildOrder = this.getTreeOrder((Element)mainChildNode, prevMainChildOrder);
                     prevMainChildOrder = mainChildOrder;
                  }

                  if (mainChildOrder > otherChildOrder) {
                     addBeforeNode = mainChildNode;
                     break;
                  }
               }

               existing.insertBefore(newChild, addBeforeNode);
            }
         }
      }

   }
}
