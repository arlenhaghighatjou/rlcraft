package ivorius.ivtoolkit.tools;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.Highlighter;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JavaCompatibility {
   public static void requireJava8(boolean exit) {
      if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
         final boolean displayExeNotice = SystemUtils.IS_OS_WINDOWS && FMLLaunchHandler.side().isClient();
         Logger logger = LogManager.getLogger("STDERR");
         logger.error("");
         logger.error(StringUtils.repeat('=', 80));
         logger.error("IvToolkit requires Java 8 to be installed.");
         logger.error("Please install the latest Java 8 appropriate for your System from https://java.com/download/" + (displayExeNotice ? " or use the latest .exe launcher from https://minecraft.net/" : ""));
         logger.error("Thank you. The game will exit now.");
         logger.error(StringUtils.repeat('=', 80));
         logger.error("");
         if (!GraphicsEnvironment.isHeadless()) {
            final Object mutex = new Object();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  try {
                     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                  } catch (Exception var8) {
                     ;
                  }

                  JLabel label = new JLabel();
                  Font font = label.getFont();
                  StringBuilder style = (new StringBuilder("font-family:" + font.getFamily() + ";")).append("font-weight:").append(font.isBold() ? "bold" : "normal").append(";").append("font-size:").append(font.getSize()).append("pt;");
                  JTextPane text = new JTextPane();
                  text.setContentType("text/html");
                  text.setText("<html><body style=\"" + style + "\">" + "<strong>IvToolkit requires Java 8 to be installed.</strong><br />" + "Please install the latest Java 8 appropriate for your System from <a href=\"https://java.com/download/\">java.com/download</a>" + (displayExeNotice ? " or use the latest .exe launcher from <a href=\"https://minecraft.net/\">minecraft.net</a>" : "") + ".<br /><br />Thank you. The game will exit now." + "</body></html>");
                  text.setEditable(false);
                  text.setHighlighter((Highlighter)null);
                  text.setBackground(label.getBackground());
                  text.setMargin(new Insets(20, 20, 20, 20));
                  text.addHyperlinkListener(new HyperlinkListener() {
                     public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e.getEventType().equals(EventType.ACTIVATED)) {
                           try {
                              Desktop.getDesktop().browse(e.getURL().toURI());
                           } catch (Exception var3) {
                              ;
                           }
                        }

                     }
                  });
                  final JFrame frame = new JFrame("Java 8 required");
                  JButton button = new JButton("Exit");
                  button.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                     }
                  });
                  JPanel panel = new JPanel();
                  panel.setLayout(new BoxLayout(panel, 1));
                  panel.add(text);
                  panel.add(button);
                  panel.add(Box.createVerticalStrut(20));
                  frame.setContentPane(panel);
                  frame.setDefaultCloseOperation(2);
                  frame.setResizable(false);
                  frame.addWindowListener(new WindowAdapter() {
                     public void windowClosed(WindowEvent e) {
                        synchronized(mutex) {
                           mutex.notify();
                        }
                     }
                  });
                  frame.pack();
                  frame.setLocationRelativeTo((Component)null);
                  frame.setVisible(true);
                  frame.toFront();
               }
            });
            synchronized(mutex) {
               try {
                  mutex.wait();
               } catch (InterruptedException var8) {
                  ;
               }
            }
         }

         if (exit) {
            try {
               Class<?> clazz = Class.forName("java.lang.Shutdown");
               Method method = clazz.getDeclaredMethod("exit", Integer.TYPE);
               method.setAccessible(true);
               method.invoke((Object)null, Integer.valueOf(-1));
            } catch (Throwable var7) {
               FMLCommonHandler.instance().exitJava(-1, false);
            }
         }

      }
   }

   private JavaCompatibility() {
   }
}
