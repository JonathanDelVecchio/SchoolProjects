package textcollage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * FontChooser class, modelled after JColorChooser, but simplified.
 *
 * @author Bryan Higgs, November 3, 2001
 */

/*
 * Modified:
 * 
 * - Added showDialog() method that accepts initial font values.
 * 
 * - Larger initial component size.
 * 
 * - Escape key handler to hide the dialog when pressed.
 */
public class FontChooser extends JComponent {

  /**
   * Shows a modal font-chooser dialog and blocks until the dialog is hidden. If
   * the user presses the "OK" button, then this method hides/disposes the dialog
   * and returns the selected font. If the user presses the "Cancel" button or
   * closes the dialog without pressing "OK", then this method hides/disposes the
   * dialog and returns <code>null</code>.
   *
   * @param component the parent <code>Component</code> for the dialog
   * @param title     the String containing the dialog's title
   * @return the selected font or <code>null</code> if the user opted out
   */
  public static Font showDialog(Component component, String title) {
    return showDialog(component, title, null);
  }

  /**
   * Shows a modal font-chooser dialog and blocks until the dialog is hidden. If
   * the user presses the "OK" button, then this method hides/disposes the dialog
   * and returns the selected font. If the user presses the "Cancel" button or
   * closes the dialog without pressing "OK", then this method hides/disposes the
   * dialog and returns <code>null</code>.
   *
   * @param component   the parent <code>Component</code> for the dialog
   * @param title       the String containing the dialog's title
   * @param initialFont the selected font from the user
   * @return the selected font or <code>null</code> if the user opted out
   */
  public static Font showDialog(Component component, String title, Font initialFont) {
    final FontChooser pane = new FontChooser(initialFont);

    FontTracker ok = new FontTracker(pane);

    JDialog dialog = createDialog(component, title, true, pane, ok, null);
    dialog.addWindowListener(new FontChooserDialog.Closer());
    dialog.addComponentListener(new FontChooserDialog.DisposeOnClose());

    dialog.setVisible(true);

    return ok.getSelectedFont();
  }

  /**
   * Creates and returns a new dialog containing the specified FontChooser pane,
   * together with "OK" and "Cancel" buttons. If either of the "OK" or "Cancel"
   * buttons is pressed, the dialog is automatically hidden (but not disposed).
   */
  public static JDialog createDialog(Component parent, String title, boolean modal, FontChooser chooserPane,
      ActionListener okListener, ActionListener cancelListener) {
    return new FontChooserDialog(parent, title, modal, chooserPane, okListener, cancelListener);
  }

  /**
   * Constructor: Creates a font chooser pane, which consists of an input pane
   * with three Lists for Font name, style and size, and a preview pane that shows
   * some text in the selected font.
   */
  public FontChooser(Font initialFont) {

    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(600, 400));

    PreviewPanel previewPane = new PreviewPanel();
    fontInputPanel = new InputPanel(previewPane);
    if (initialFont != null)
      fontInputPanel.setInitialFont(initialFont);

    // ListSelectionListener
    add(fontInputPanel, BorderLayout.CENTER);
    add(previewPane, BorderLayout.SOUTH);
  }

  /**
   * Returns the current font from the font chooser. (Delegates the work to the
   * input pane's getFont() method.)
   */
  public Font getSelectedFont() {
    return fontInputPanel.getSelectedFont();
  }

  /////// Private data /////
  private InputPanel fontInputPanel;

  /////// Inner classes /////

  /**
   * Class to present the user with a set of three lists, one each for font name,
   * style and size.
   */
  class InputPanel extends JPanel {

    private FontNameList fontNames = new FontNameList();
    private FontStyleList fontStyles = new FontStyleList();
    private FontSizeList fontSizes = new FontSizeList();

    /**
     * Constructor: Creates an instance of InputPanel.
     *
     * @param listener a list selection listener that will listen for changes in the
     *                 state of the lists.
     */
    public InputPanel(ListSelectionListener listener) {
      setLayout(new BorderLayout());

      // Font name
      Box nameBox = Box.createVerticalBox();
      nameBox.add(Box.createVerticalStrut(10));
      JLabel fontNameLabel = new JLabel("Font Name:");
      nameBox.add(fontNameLabel);

      if (listener != null) {
        fontNames.addListSelectionListener(listener);
      }
      // Restrict to single selection
      fontNames.setSelectionMode(0);

      JScrollPane namePane = new JScrollPane(fontNames);
      nameBox.add(namePane);
      nameBox.add(Box.createVerticalStrut(10));

      // Font style
      Box styleBox = Box.createVerticalBox();
      styleBox.add(Box.createVerticalStrut(10));
      JLabel fontStyleLabel = new JLabel("Font Style:");
      styleBox.add(fontStyleLabel);
      if (listener != null) {
        fontStyles.addListSelectionListener(listener);
      }
      // Restrict to single selection
      fontStyles.setSelectionMode(0);

      JScrollPane stylePane = new JScrollPane(fontStyles);
      styleBox.add(stylePane);
      styleBox.add(Box.createVerticalStrut(10));

      // Font size
      Box sizeBox = Box.createVerticalBox();
      sizeBox.add(Box.createVerticalStrut(10));
      JLabel fontSizeLabel = new JLabel("Size:");
      sizeBox.add(fontSizeLabel);
      if (listener != null) {
        fontSizes.addListSelectionListener(listener);
      }
      // Restrict to single selection
      fontSizes.setSelectionMode(0);

      JScrollPane sizePane = new JScrollPane(fontSizes);
      sizeBox.add(sizePane);
      sizeBox.add(Box.createVerticalStrut(10));

      Box mainBox = Box.createHorizontalBox();
      mainBox.add(Box.createHorizontalStrut(10));
      mainBox.add(nameBox);
      mainBox.add(Box.createHorizontalStrut(10));
      mainBox.add(styleBox);
      mainBox.add(Box.createHorizontalStrut(10));
      mainBox.add(sizeBox);
      mainBox.add(Box.createHorizontalStrut(10));
      add(mainBox, BorderLayout.CENTER);
    }

    /**
     * Returns the selected font, derived from the user's list choices.
     */
    public Font getSelectedFont() {
      return new Font(fontNames.getFontName(), fontStyles.getFontStyle(), fontSizes.getFontSize());
    }

    /**
     * Set font with selected values.
     * 
     * @param initialFont
     */
    void setInitialFont(Font initialFont) {
      String fontName = initialFont.getFamily();

      int style = initialFont.getStyle();
      String fontStyle = "Plain";
      if (style == Font.PLAIN) {
        fontStyle = "Plain";
      } else if (style == Font.ITALIC) {
        fontStyle = "Italic";
      } else if (style == Font.BOLD) {
        fontStyle = "Bold";
      } else if (style == Font.BOLD + Font.ITALIC) {
        fontStyle = "Bold Italic";
      }

      String fontSize = String.valueOf(initialFont.getSize());
      fontNames.setSelectedValue(fontName, true);
      fontStyles.setSelectedValue(fontStyle, true);
      fontSizes.setSelectedValue(fontSize, true);
    }

  }

  /**
   * Class to present a preview panel containing text that shows the selected font
   * as the user chooses font attributes.
   */
  class PreviewPanel extends JPanel implements ListSelectionListener {
    /**
     * Constructor: Creates an instance of PreviewPanel.
     */
    public PreviewPanel() {
      setLayout(new FlowLayout());

      Box box = Box.createVerticalBox();
      JLabel previewLabel = new JLabel("Preview:");
      box.add(previewLabel);

      m_text.setEditable(false);
      m_text.setBackground(Color.white);
      m_text.setForeground(Color.black);
      JScrollPane pane = new JScrollPane(m_text);
      pane.setPreferredSize(new Dimension(500, 100));
      box.add(pane);

      add(box);
    }

    /**
     * ListSelectionListener required method.
     */
    public void valueChanged(ListSelectionEvent ev) {
      m_text.setFont(FontChooser.this.getSelectedFont());
    }

    /////// Private data ///////
    private JTextField m_text = new JTextField("The quick brown fox jumps over the lazy dog");
  }
}

/**
 * Class to present the list of available font names.
 */
class FontNameList extends JList {

  private static final String[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
      .getAvailableFontFamilyNames();

  FontNameList() {
    super(systemFonts);
    setSelectedIndex(0);
    setVisibleRowCount(5);
  }

  /**
   * Returns the selected font name.
   */
  String getFontName() {
    String name = (String) getSelectedValue();
    return name;
  }
}

/**
 * Class to present the available font styles.
 */
class FontStyleList extends JList {
  private static final String[] defaultFontStyles = { "Regular", "Italic", "Bold", "Bold Italic" };

  /**
   * Constructor
   */
  FontStyleList() {
    super(defaultFontStyles);
    setSelectedIndex(0);
    setVisibleRowCount(5);
  }

  /**
   * Returns the selected font style.
   */
  int getFontStyle() {
    int style = 0;
    String name = (String) getSelectedValue();
    if (name.equals("Regular")) {
      style = Font.PLAIN;
    } else if (name.equals("Italic")) {
      style = Font.ITALIC;
    } else if (name.equals("Bold")) {
      style = Font.BOLD;
    } else {
      style = Font.BOLD + Font.ITALIC;
    }
    return style;
  }
}

/**
 * Class to present the available Font sizes.
 */
class FontSizeList extends JList {
  private static final String[] defaultFontSizes = { "6", "8", "10", "12", "14", "16", "18", "20", "22", "24", "36",
      "72" };

  /**
   * Constructor.
   */
  FontSizeList() {
    super(defaultFontSizes);
    setSelectedIndex(4); // Default to 14 point
    setVisibleRowCount(5);
  }

  /**
   * Returns the selected font size.
   */
  int getFontSize() {
    int size = Integer.parseInt((String) getSelectedValue());
    return size;
  }

}

/**
 * Class to present a font chooser dialog, consisting of a FontChooser panel
 * with "OK" and "Cancel" buttons.
 */
class FontChooserDialog extends JDialog {
  /**
   * Constructor: Creates an instance of a FontChooserDialog.
   *
   * @param component      the parent component of the dialog
   * @param title          the dialog title (for the title bar)
   * @param modal          whether the dialog is modal
   * @param chooserPane    the FontChooser pane to be used
   * @param okListener     an ActionListener that listens to the OK button
   * @param cancelListener an ActionListener that listens to the cancel button
   */
  public FontChooserDialog(Component component, String title, boolean modal, FontChooser chooserPane,
      ActionListener okListener, ActionListener cancelListener) {
    // Invoke JDialog's constructor, passing in the parent
    // component's frame.
    super(JOptionPane.getFrameForComponent(component), title, modal);

    m_chooserPane = chooserPane;

    // Set contents of dialog
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(m_chooserPane, BorderLayout.CENTER);

    // Create lower button panel
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

    // OK Button
    JButton okButton = new JButton("OK");
    getRootPane().setDefaultButton(okButton);
    okButton.setActionCommand("OK");
    if (okListener != null) {
      okButton.addActionListener(okListener);
    }
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false); // just hide the dialog
      }
    });
    buttonPane.add(okButton);

    // Cancel button
    JButton cancelButton = new JButton("Cancel");
    cancelButton.getAccessibleContext().setAccessibleDescription("cancel");

    // esc key handler
    AbstractAction cancelKeyAction = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    };

    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
    InputMap inputMap = cancelButton.getInputMap(condition);
    ActionMap actionMap = cancelButton.getActionMap();
    KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    if (inputMap != null && actionMap != null) {
      inputMap.put(escapeKeyStroke, "cancel");
      actionMap.put("cancel", cancelKeyAction);
    }
    // esc key handler end

    cancelButton.setActionCommand("cancel");
    if (cancelListener != null) {
      cancelButton.addActionListener(cancelListener);
    }
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false); // just hide the dialog
      }
    });

    buttonPane.add(cancelButton);

    contentPane.add(buttonPane, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(component);
  }

  ////////// Static nested classes ///////////

  /**
   * Class to hide the dialog window on window closing event.
   */
  static class Closer extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      Window w = e.getWindow();
      w.setVisible(false);
    }
  }

  /**
   * Class to dispose of the dialog window when the dialog is hidden.
   */
  static class DisposeOnClose extends ComponentAdapter {
    public void componentHidden(ComponentEvent e) {
      Window w = (Window) e.getComponent();
      w.dispose();
    }
  }

  ///// Private data for FontChooserDialog ////
  private FontChooser m_chooserPane;
}

/**
 * Class to track changes in the selected font in the FontChooser.
 */
class FontTracker implements ActionListener {
  private FontChooser chooser;
  private Font font;

  public FontTracker(FontChooser c) {
    chooser = c;
  }

  public void actionPerformed(ActionEvent e) {
    font = chooser.getSelectedFont();
  }

  public Font getSelectedFont() {
    return font;
  }
}