package textcollage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * A panel that contains a large drawing area where strings can be drawn. The
 * strings are represented by objects of type DrawTextItem. An input box under
 * the panel allows the user to specify what string will be drawn when the user
 * clicks on the drawing area.
 */

/*
 * Changes and additions:
 * 
 * - [addition] Added modified font chooser to the options. (macOS Shortcut:
 * [Cmd + F], Windows Shortcut: [Ctrl + F])
 * 
 * - [addition] Redo menu item. (macOS Shortcut: [Cmd + Shift + Z], Windows
 * Shortcut: [Ctrl + Z])
 * 
 * - [addition] Set Text Background menu item. (macOS Shortcut: [Cmd + Shift + T], Windows
 * Shortcut: [Ctrl + Shift + T])
 * 
 * - [addition] Reset menu item.
 * 
 * - [changes] Options layout modified.
 * 
 * - [changes] Remove item -> Undo functionality to be able to undo all history.
 * 
 * - [changes] The "Clear" menu item will also clear histories.
 * 
 */
public class DrawTextPanel extends JPanel {
	private final Color CANVAS_DEFAULT_COLOR = Color.LIGHT_GRAY;
	private final String INPUT_DEFAULT = "Hello World!";
	private final Font DEFAULT_TEXT_FONT = null;
	private final double DEFAULT_MAGNIFICATION = 1;
	private final boolean DEFAULT_TEXT_BORDER = false;
	private final double DEFAULT_TEXT_ROTATION_ANGLE = 0;
	private final double DEFAULT_TEXT_TRANSPARENCY = 0;
	private final Color DEFAULT_TEXT_BACKGROUND = null;
	private final double DEFAULT_TEXT_BACKGROUND_TRANSPARENCY = 0;

	// As it now stands, this class can only show one string at at
	// a time! The data for that string is in the DrawTextItem object
	// named theString. (If it's null, nothing is shown. This
	// variable should be replaced by a variable of type
	// ArrayList<DrawStringItem> that can store multiple items.

	private ArrayList<DrawTextItem> theString = new ArrayList<DrawTextItem>(); // change to an ArrayList<DrawTextItem> !

	// To store removed items
	private ArrayList<DrawTextItem> histories = new ArrayList<DrawTextItem>();

	// Default:
	private Font textFont = DEFAULT_TEXT_FONT;
	private double currentMagnification = DEFAULT_MAGNIFICATION;
	private boolean currentBorder = DEFAULT_TEXT_BORDER;
	private double currentRotationAngle = DEFAULT_TEXT_ROTATION_ANGLE;
	private double currentTextTransparency = DEFAULT_TEXT_TRANSPARENCY;
	private Color currentBackground = DEFAULT_TEXT_BACKGROUND;
	private double currentBackgroundTransparency = DEFAULT_TEXT_BACKGROUND_TRANSPARENCY;
	

	private Color currentTextColor = Color.BLACK; // Color applied to new strings.

	private Canvas canvas; // the drawing area.
	private JTextField input; // where the user inputs the string that will be added to the canvas
	private SimpleFileChooser fileChooser; // for letting the user select files
	private JMenuBar menuBar; // a menu bar with command that affect this panel
	private MenuHandler menuHandler; // a listener that responds whenever the user selects a menu command
	private JMenuItem undoMenuItem; // the "Remove Item" command from the edit menu

	private JMenuItem redoMenuItem;
	private JMenuItem resetMenuItem;

	/**
	 * An object of type Canvas is used for the drawing area. The canvas simply
	 * displays all the DrawTextItems that are stored in the ArrayList, strings.
	 */
	private class Canvas extends JPanel {
		Canvas() {
			setPreferredSize(new Dimension(800, 600));
			setBackground(Color.LIGHT_GRAY);
			setFont(new Font("Serif", Font.BOLD, 24));
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (!theString.isEmpty()) {
				for (DrawTextItem drawTextItem : theString) {
					drawTextItem.draw(g);
				}
			}
		}
	}

	/**
	 * An object of type MenuHandler is registered as the ActionListener for all the
	 * commands in the menu bar. The MenuHandler object simply calls doMenuCommand()
	 * when the user selects a command from the menu.
	 */
	private class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			doMenuCommand(evt.getActionCommand());
		}
	}

	/**
	 * Creates a DrawTextPanel. The panel has a large drawing area and a text input
	 * box where the user can specify a string. When the user clicks the drawing
	 * area, the string is added to the drawing area at the point where the user
	 * clicked.
	 */
	public DrawTextPanel() {
		fileChooser = new SimpleFileChooser();
		undoMenuItem = new JMenuItem("Undo");
		undoMenuItem.setEnabled(false);

		redoMenuItem = new JMenuItem("Redo");
		redoMenuItem.setEnabled(false);

		menuHandler = new MenuHandler();
		setLayout(new BorderLayout(3, 3));
		setBackground(Color.BLACK);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas = new Canvas();
		add(canvas, BorderLayout.CENTER);
		JPanel bottom = new JPanel();
		bottom.add(new JLabel("Text to add: "));
		input = new JTextField("Hello World!", 40);
		bottom.add(input);
		add(bottom, BorderLayout.SOUTH);
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doMousePress(e);
			}
		});
	}

	/**
	 * This method is called when the user clicks the drawing area. A new string is
	 * added to the drawing area. The center of the string is at the point where the
	 * user clicked.
	 * 
	 * @param e the mouse event that was generated when the user clicked
	 */
	public void doMousePress(MouseEvent e) {
		String text = input.getText().trim();
		if (text.length() == 0) {
			input.setText("Hello World!");
			text = "Hello World!";
		}

		DrawTextItem s = new DrawTextItem(text, e.getX(), e.getY());
		s.setTextColor(currentTextColor); // Default is null, meaning default color of the canvas (black).

		// SOME OTHER OPTIONS THAT CAN BE APPLIED TO TEXT ITEMS:
		s.setFont(textFont); // Default is
		// null, meaning font of canvas.
		s.setMagnification(currentMagnification); // Default is 1, meaning no magnification.
		s.setBorder(currentBorder); // Default is false, meaning don't draw a border.
		s.setRotationAngle(currentRotationAngle); // Default is 0, meaning no rotation.
		s.setTextTransparency(currentTextTransparency); // Default is 0, meaning text is not at all
		// transparent.
		s.setBackground(currentBackground); // Default is null, meaning don't draw a
		// background area.
		s.setBackgroundTransparency(currentBackgroundTransparency); // Default is 0, meaning background is not
		// transparent.

		theString.add(s); // Set this string as the ONLY string to be drawn on the canvas!
		undoMenuItem.setEnabled(!theString.isEmpty());
		canvas.repaint();
	}

	/**
	 * Returns a menu bar containing commands that affect this panel. The menu bar
	 * is meant to appear in the same window that contains this panel.
	 */
	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();

			boolean isMacOs = System.getProperty("os.name").toLowerCase().contains("mac")
					|| System.getProperty("mrj.version") != null;

			String redoShortcut;
			String commandKey; // for making keyboard accelerators for menu commands
			if (isMacOs) {
				commandKey = "meta ";
				redoShortcut = commandKey + "shift Z";
			} // command key for Mac OS
			else {
				commandKey = "control ";
				redoShortcut = commandKey + "Y";
			} // command key for non-Mac OS

			JMenu fileMenu = new JMenu("File");
			menuBar.add(fileMenu);
			JMenuItem saveItem = new JMenuItem("Save...");
			saveItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "N"));
			saveItem.addActionListener(menuHandler);
			fileMenu.add(saveItem);
			JMenuItem openItem = new JMenuItem("Open...");
			openItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "O"));
			openItem.addActionListener(menuHandler);
			fileMenu.add(openItem);
			fileMenu.addSeparator();
			JMenuItem saveImageItem = new JMenuItem("Save Image...");
			saveImageItem.addActionListener(menuHandler);
			fileMenu.add(saveImageItem);

			JMenu editMenu = new JMenu("Edit");
			menuBar.add(editMenu);
			undoMenuItem.addActionListener(menuHandler); // undoItem was created in the constructor
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "Z"));
			editMenu.add(undoMenuItem);

			// Redo
			redoMenuItem.addActionListener(menuHandler); // undoItem was created in the constructor
			redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(redoShortcut));
			editMenu.add(redoMenuItem);

			editMenu.addSeparator();
			JMenuItem clearItem = new JMenuItem("Clear");
			clearItem.addActionListener(menuHandler);
			editMenu.add(clearItem);

			editMenu.addSeparator();
			resetMenuItem = new JMenuItem("Reset");
			resetMenuItem.addActionListener(menuHandler);
			editMenu.add(resetMenuItem);

			JMenu optionsMenu = new JMenu("Options");
			JMenu textSettings = new JMenu("Text Settings");
			optionsMenu.add(textSettings);
			menuBar.add(optionsMenu);

			// LAYOUT MODIFIED:
			// Text settings menu items
			JMenuItem colorItem = new JMenuItem("Set Text Color...");
			colorItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + "T"));
			colorItem.addActionListener(menuHandler);
			textSettings.add(colorItem);

			// Add font option
			JMenuItem fontOption = new JMenuItem("Set Font...");
			fontOption.setAccelerator(KeyStroke.getKeyStroke(commandKey + "F"));
			fontOption.addActionListener(menuHandler);
			textSettings.add(fontOption);

			// Add text background option
			JMenuItem textBgItem = new JMenuItem("Set Text Background...");
			textBgItem.setAccelerator(KeyStroke.getKeyStroke(commandKey + " shift T"));
			textBgItem.addActionListener(menuHandler);
			textSettings.add(textBgItem);

			optionsMenu.addSeparator();
			JMenuItem bgColorItem = new JMenuItem("Set Background Color...");
			bgColorItem.addActionListener(menuHandler);
			optionsMenu.add(bgColorItem);

		}
		return menuBar;
	}

	/**
	 * Carry out one of the commands from the menu bar.
	 * 
	 * @param command the text of the menu command.
	 */
	private void doMenuCommand(String command) {
		if (command.equals("Save...")) { // save all the string info to a file
			// JOptionPane.showMessageDialog(this, "Sorry, the Save command is not
			// implemented.");
			File savedFile = fileChooser.getOutputFile(this, "Enter Filename To Save", "textcollage.txt");
			// get the new line based on the machine's OS
			String newLine = System.getProperty("line.separator");
			try {
				PrintWriter writer = new PrintWriter(savedFile);

				if (!theString.isEmpty()) {
					for (DrawTextItem drawTextItem : theString) {
						writer.write("textString:" + drawTextItem.getString() + newLine);

						String getX = String.valueOf(drawTextItem.getX());
						writer.write("textPosX:" + getX + newLine);

						String getY = String.valueOf(drawTextItem.getY());
						writer.write("textPosY:" + getY + newLine);

						if (drawTextItem.getBackground() != null) {
							String getBackground = String.valueOf(drawTextItem.getBackground().getRGB());
							writer.write("textBackground:" + getBackground + newLine);
						}

						String getBackgroundTransparency = String.valueOf(drawTextItem.getBackgroundTransparency());
						writer.write("textBackgroundTransparency:" + getBackgroundTransparency + newLine);

						String getBorder = String.valueOf(drawTextItem.getBorder());
						writer.write("textBorder:" + getBorder + newLine);

						if (drawTextItem.getFont() != null) {
							String font = drawTextItem.getFont().getFamily();
							int style = drawTextItem.getFont().getStyle();
							String fontStyle = "PLAIN";
							if (style == Font.PLAIN) {
								fontStyle = "PLAIN";
							} else if (style == Font.ITALIC) {
								fontStyle = "ITALIC";
							} else if (style == Font.BOLD) {
								fontStyle = "BOLD";
							} else if (style == Font.BOLD + Font.ITALIC) {
								fontStyle = "BOLDITALIC";
							}
							String fontSize = String.valueOf(drawTextItem.getFont().getSize());

							if (!fontStyle.isEmpty())
								font = font + "-" + fontStyle;

							if (!fontSize.isEmpty())
								font = font + "-" + fontSize;

							writer.write("textFont:" + font + newLine);
						}

						String getMagnification = String.valueOf(drawTextItem.getMagnification());
						writer.write("textMagnification:" + getMagnification + newLine);

						String getRotationAngle = String.valueOf(drawTextItem.getRotationAngle());
						writer.write("textRotationAngle:" + getRotationAngle + newLine);

						if (drawTextItem.getTextColor() != null) {
							String getTextColor = String.valueOf(drawTextItem.getTextColor().getRGB());
							writer.write("textColor:" + getTextColor + newLine);
						}

						String getTextTransparency = String.valueOf(drawTextItem.getTextTransparency());
						writer.write("textTransparency:" + getTextTransparency + newLine);
					}
				}
				if (canvas.getBackground() != null)
					writer.write("canvasBackground:" + String.valueOf(canvas.getBackground().getRGB()) + newLine);

				writer.close();
			} catch (FileNotFoundException e) {
				// e.printStackTrace();
				JOptionPane.showMessageDialog(this, "File is not found.");
			} catch (NullPointerException e) {
				// when user cancel saving the file.
				// do logging or somthing else.
				System.err.println("Saving canceled.");
			}
		} else if (command.equals("Open...")) { // read a previously saved file, and reconstruct the list of strings
			// JOptionPane.showMessageDialog(this, "Sorry, the Open command is not
			// implemented.");
			File openedFile = fileChooser.getInputFile();

			ArrayList<String> params = new ArrayList<String>();

			try {
				Scanner scanner = new Scanner(openedFile);
				while (scanner.hasNextLine()) {
					params.add(scanner.nextLine());
				}
				scanner.reset();
				scanner.close();

				// clear the component
				theString.clear();
				histories.clear();
				canvas.repaint();

				DrawTextItem drawTextItem = new DrawTextItem("");

				if (!params.isEmpty()) {
					for (String param : params) {
						String[] kv = param.split(":");
						String key = kv[0];
						String value = kv[1];

						if (key.equals("textString") && kv.length > 2) {
							String text = "";
							for (int i = 1; i < kv.length; i++) {
								text = text + kv[i];
							}
							value = text;
						}

						switch (key) {
							case "textString":
								drawTextItem = new DrawTextItem(value);
								break;
							case "canvasBackground":
								Color canvasBgColor = Color
										.decode(String.format("#%06X", (0xFFFFFF & Integer.parseInt(value))));
								canvas.setBackground(canvasBgColor);
								break;
							case "textPosX":
								drawTextItem.setX(Integer.parseInt(value.trim()));
								break;
							case "textPosY":
								drawTextItem.setY(Integer.parseInt(value.trim()));
								break;
							case "textBackground":
								Color textBackground = Color
										.decode(String.format("#%06X", (0xFFFFFF & Integer.parseInt(value.trim()))));
								drawTextItem.setBackground(textBackground);
								break;
							case "textBackgroundTransparency":
								double backgroundTransparency = Double.parseDouble(value.trim());
								drawTextItem.setBackgroundTransparency(backgroundTransparency);
								break;
							case "textBorder":
								boolean border = Boolean.parseBoolean(value.trim());
								drawTextItem.setBorder(border);
								break;
							case "textFont":
								Font font = Font.decode(value.trim());
								drawTextItem.setFont(font);
								textFont = font;
								break;
							case "textMagnification":
								double magnification = Double.parseDouble(value.trim());
								drawTextItem.setMagnification(magnification);
								break;
							case "textRotationAngle":
								double rotationAngle = Double.parseDouble(value.trim());
								drawTextItem.setRotationAngle(rotationAngle);
								break;
							case "textColor":
								Color textColor = Color
										.decode(String.format("#%06X", (0xFFFFFF & Integer.parseInt(value.trim()))));
								drawTextItem.setTextColor(textColor);
								break;
							case "textTransparency":
								double textTransparency = Double.parseDouble(value.trim());
								drawTextItem.setTextTransparency(textTransparency);
								break;
							default:
								break;
						}
						theString.add(drawTextItem);
					}
					undoMenuItem.setEnabled(!theString.isEmpty());
					canvas.repaint();
				}
				params.clear();

			} catch (FileNotFoundException e) {
				// e.printStackTrace();
				JOptionPane.showMessageDialog(this, "File is not found.");
			} catch (NullPointerException e) {
				// when user cancel opening a file.
				System.err.println("Cancel opening a file.");
			}
			canvas.repaint(); // (you'll need this to make the new list of strings take effect)
		} else if (command.equals("Clear")) { // remove all strings
			theString.clear(); // Remove the ONLY string from the canvas.
			histories.clear();
			undoMenuItem.setEnabled(false);
			redoMenuItem.setEnabled(false);
			canvas.repaint();
		} else if (command.equals("Reset")) { // remove all strings
			defaultSettings();
			canvas.repaint();
		} else if (command.equals("Undo")) { // remove the most recently added string
			histories.add(theString.get(theString.size() - 1));
			theString.remove(theString.size() - 1); // Remove the ONLY string from the canvas.

			// toggle menu item ability to redo and undo
			redoMenuItem.setEnabled(!histories.isEmpty());
			undoMenuItem.setEnabled(!theString.isEmpty());

			canvas.repaint();
		} else if (command.equals("Redo")) { // Redo
			theString.add(histories.get(histories.size() - 1));
			histories.remove(histories.size() - 1);

			// toggle menu item ability to redo and undo
			redoMenuItem.setEnabled(!histories.isEmpty());
			undoMenuItem.setEnabled(!theString.isEmpty());

			canvas.repaint();
		} else if (command.equals("Set Text Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Text Color", currentTextColor);
			if (c != null)
				currentTextColor = c;
		} else if (command.equals("Set Background Color...")) {
			Color c = JColorChooser.showDialog(this, "Select Background Color", canvas.getBackground());
			if (c != null) {
				canvas.setBackground(c);
				canvas.repaint();
			}
		} else if (command.equals("Set Font...")) {
			Font f = FontChooser.showDialog(this, "Choose Font", textFont);
			if (f != null)
				textFont = f;
		} else if (command.equals("Set Text Background...")) {
			Color c = JColorChooser.showDialog(this, "Select Text Background Color", currentBackground);
			if (c != null)
				currentBackground = c;
		} else if (command.equals("Save Image...")) { // save a PNG image of the drawing area
			File imageFile = fileChooser.getOutputFile(this, "Select Image File Name", "textimage.png");
			if (imageFile == null)
				return;
			try {
				// Because the image is not available, I will make a new BufferedImage and
				// draw the same data to the BufferedImage as is shown in the panel.
				// A BufferedImage is an image that is stored in memory, not on the screen.
				// There is a convenient method for writing a BufferedImage to a file.
				BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics g = image.getGraphics();
				g.setFont(canvas.getFont());
				canvas.paintComponent(g); // draws the canvas onto the BufferedImage, not the screen!
				boolean ok = ImageIO.write(image, "PNG", imageFile); // write to the file
				if (ok == false)
					throw new Exception("PNG format not supported (this shouldn't happen!).");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Sorry, an error occurred while trying to save the image:\n" + e);
			}
		}
	}

	/**
	 * Reset to default settings
	 */
	private void defaultSettings() {
		textFont = DEFAULT_TEXT_FONT;
		currentMagnification = DEFAULT_MAGNIFICATION;
		currentBorder = DEFAULT_TEXT_BORDER;
		currentRotationAngle = DEFAULT_TEXT_ROTATION_ANGLE;
		currentTextTransparency = DEFAULT_TEXT_TRANSPARENCY;
		currentBackground = DEFAULT_TEXT_BACKGROUND;
		currentBackgroundTransparency = DEFAULT_TEXT_BACKGROUND_TRANSPARENCY;

		theString.clear();
		histories.clear();

		undoMenuItem.setEnabled(false);
		redoMenuItem.setEnabled(false);

		input.setText(INPUT_DEFAULT);
		canvas.setBackground(CANVAS_DEFAULT_COLOR);
	}
}
