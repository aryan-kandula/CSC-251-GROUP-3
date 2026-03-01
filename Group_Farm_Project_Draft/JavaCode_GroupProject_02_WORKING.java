import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

/**
 * Farm Management System
 * Group 2
 * Modules 2 & 3 Group Project
 *
 * Manages store inventory, animal sales, veterinary services,
 * and business reporting for a small family farm.
 *
 * -------------------------------------------------------
 *  Edit History
 * -------------------------------------------------------
 *  [02/16/2026 - 4:22 PM]  Aryan Kandula  - Project created, added base class structure
 *                                            (StoreItem, Animal, ServiceRecord) and main menu
 *  [02/19/2026 - 6:47 PM]  Aryan Kandula  - Implemented Store module (add, sell, restock)
 *                                            and Animal Sales module with breeder support
 *  [02/24/2026 - 5:15 PM]  Aryan Kandula  - Added Services & Payments module, reports,
 *                                            and seed data for testing
 *  [02/27/2026 - 7:03 PM]  Aryan Kandula  - Final polish: farm-themed GUI, custom dialogs,
 *                                            FarmButton, FarmBackground, fixed font rendering
 *  [02/28/2026 - 5:41 PM]  Aryan Kandula  - Added File I/O: loadFromCSV() and saveToCSV()
 *                                            Data now reads from / writes to farm_data.csv
 * -------------------------------------------------------
 *
 * NOTE (working draft): seedData() is kept here as a fallback
 * in case farm_data.csv is missing. The polished final version
 * removes seedData entirely and relies only on the CSV file.
 */

// ─────────────────────────────────────────────
//  Data Classes
// ─────────────────────────────────────────────

// Written by: Aryan Kandula - StoreItem class to represent a product in the store.
class StoreItem {
    private String name;
    private double price;
    private int quantity;

    public StoreItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName()          { return name; }
    public double getPrice()         { return price; }
    public int    getQuantity()      { return quantity; }
    public void   setQuantity(int q) { quantity = q; }

    // Returns a CSV-formatted line for this item (commas stripped to prevent CSV corruption)
    public String toCSV() {
        return "STORE,item," + name.replace(",","") + "," + price + "," + quantity + ",,,";
    }

    @Override
    public String toString() {
        return String.format("%-28s $%-8.2f Qty: %d", name, price, quantity);
    }
}

// ─────────────────────────────────────────────

// Written by: Aryan Kandula - Animal class to represent a farm or breeder animal for sale.
class Animal {
    private String  type, breed, source;
    private double  salePrice;
    private boolean sold = false;

    public Animal(String type, String breed, double salePrice, String source) {
        this.type = type; this.breed = breed;
        this.salePrice = salePrice; this.source = source;
    }

    public String  getType()      { return type; }
    public String  getBreed()     { return breed; }
    public double  getSalePrice() { return salePrice; }
    public boolean isSold()       { return sold; }
    public String  getSource()    { return source; }
    public void    markSold()     { sold = true; }
    public void    setSold(boolean s) { sold = s; }

    // Returns a CSV-formatted line for this animal (commas stripped to prevent CSV corruption)
    public String toCSV() {
        return "ANIMAL,animal," + type.replace(",","") + "," + breed.replace(",","") + "," + salePrice + "," + source.replace(",","") + "," + sold + ",";
    }

    @Override
    public String toString() {
        return String.format("%-10s %-20s $%-8.2f %-18s %s",
            type, breed, salePrice, source, sold ? "[SOLD]" : "[Available]");
    }
}

// ─────────────────────────────────────────────

// Written by: Aryan Kandula - ServiceRecord class to track vet/grooming appointments and payments.
class ServiceRecord {
    private String customerName, animalType, serviceType, date;
    private double  fee;
    private boolean paid = false;

    public ServiceRecord(String customer, String animal, String service,
                         double fee, String date) {
        this.customerName = customer; this.animalType = animal;
        this.serviceType  = service;  this.fee = fee; this.date = date;
    }

    public String  getCustomerName() { return customerName; }
    public double  getFee()          { return fee; }
    public boolean isPaid()          { return paid; }
    public void    markPaid()        { paid = true; }
    public void    setPaid(boolean p) { paid = p; }

    // Returns a CSV-formatted line for this service record (commas stripped to prevent CSV corruption)
    public String toCSV() {
        return "SERVICE,record," + customerName.replace(",","") + "," + animalType.replace(",","") + "," +
               serviceType.replace(",","") + "," + fee + "," + date + "," + paid;
    }

    @Override
    public String toString() {
        return String.format("%-18s %-10s %-18s $%-7.2f %-12s %s",
            customerName, animalType, serviceType, fee, date,
            paid ? "[PAID]" : "[UNPAID]");
    }
}

// ─────────────────────────────────────────────
//  Theme - colors and fonts used throughout
// ─────────────────────────────────────────────

// Written by: Aryan Kandula - Centralized color/font constants for consistent styling.
class FarmTheme {
    static final Color BARN_RED    = new Color(139, 38,  38);
    static final Color BARN_DARK   = new Color(100, 25,  25);
    static final Color HAY_GOLD    = new Color(210, 170,  60);
    static final Color GREEN       = new Color( 74, 130,  50);
    static final Color GREEN_DARK  = new Color( 50,  90,  30);
    static final Color SKY         = new Color(135, 195, 230);
    static final Color CREAM       = new Color(255, 248, 220);
    static final Color BROWN       = new Color(160, 100,  50);
    static final Color TEXT        = new Color( 50,  30,  10);

    static final Color[] PALETTE = {
        BARN_RED, GREEN, BROWN,
        new Color(100, 80, 160),
        new Color( 40,120, 160),
        new Color(100, 25,  25),
        new Color(170,100,  30)
    };

    static final Font HEADER = new Font("Georgia",   Font.BOLD,  15);
    static final Font BUTTON = new Font("Georgia",   Font.BOLD,  13);
    static final Font BODY   = new Font("SansSerif", Font.PLAIN, 13);
    static final Font MONO   = new Font("Monospaced",Font.PLAIN, 12);
}

// ─────────────────────────────────────────────
//  Reusable gradient button with hover effect
// ─────────────────────────────────────────────

// Written by: Aryan Kandula - Custom JButton subclass with farm color gradient and hover animation.
class FarmButton extends JButton {
    private final Color base;
    private boolean hovered = false;

    public FarmButton(String text, Color base) {
        super(text);
        this.base = base;
        setFont(FarmTheme.BUTTON);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(230, 42));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color top    = hovered ? base.brighter() : base;
        Color bottom = hovered ? base : base.darker();
        g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 17, 17);
        g2.setColor(new Color(0, 0, 0, 50));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }
}

// ─────────────────────────────────────────────
//  Farm scene painted as background
// ─────────────────────────────────────────────

// Written by: Aryan Kandula - Custom JPanel that paints a farm scene (sky, barn, fence, flowers).
class FarmBackground extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, new Color(180, 220, 255),
                                      0, h * 0.55f, new Color(215, 238, 255)));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(255, 240, 100, 150));
        g2.fillOval(w - 90, 10, 60, 60);
        g2.setColor(new Color(255, 220, 50, 80));
        g2.fillOval(w - 98,  3, 76, 76);

        drawCloud(g2,  30, 18);
        drawCloud(g2, 160, 12);
        drawCloud(g2, w / 2 - 30, 25);

        g2.setPaint(new GradientPaint(0, (int)(h * 0.52f), FarmTheme.GREEN,
                                      0, h, FarmTheme.GREEN_DARK));
        g2.fillRect(0, (int)(h * 0.52f), w, h);

        drawBarn(g2, w / 2 - 55, (int)(h * 0.20f));
        drawFence(g2, w, (int)(h * 0.56f));
        drawFlowers(g2, w, h);
        g2.dispose();
    }

    private void drawCloud(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillOval(x,      y + 16, 50, 32);
        g2.fillOval(x + 16, y,      50, 46);
        g2.fillOval(x + 36, y + 12, 40, 30);
    }

    private void drawBarn(Graphics2D g2, int x, int y) {
        g2.setPaint(new GradientPaint(x, y + 30, FarmTheme.BARN_RED,
                                      x + 110, y + 30, FarmTheme.BARN_DARK));
        g2.fillRect(x, y + 35, 110, 80);

        int[] rx = {x - 10, x + 55, x + 120};
        int[] ry = {y + 38, y,       y + 38};
        g2.setColor(FarmTheme.BROWN.darker());
        g2.fillPolygon(rx, ry, 3);
        g2.setColor(new Color(210, 165, 100));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawPolygon(rx, ry, 3);

        g2.setColor(FarmTheme.BROWN);
        g2.fillRect(x + 38, y + 75, 34, 40);
        g2.setColor(FarmTheme.HAY_GOLD);
        g2.fillOval(x + 52, y + 93,  7,  7);

        g2.setColor(FarmTheme.SKY);
        g2.fillRect(x + 12, y + 50, 22, 18);
        g2.setColor(FarmTheme.BROWN.darker());
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(x + 12, y + 50, 22, 18);
        g2.drawLine(x + 23, y + 50, x + 23, y + 68);
        g2.drawLine(x + 12, y + 59, x + 34, y + 59);
    }

    private void drawFence(Graphics2D g2, int w, int y) {
        g2.setColor(new Color(240, 230, 200));
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(0, y,      w, y);
        g2.drawLine(0, y + 18, w, y + 18);
        for (int x = 10; x < w; x += 40) g2.fillRect(x, y - 10, 7, 35);
    }

    private void drawFlowers(Graphics2D g2, int w, int h) {
        Color[] petals = {Color.RED, Color.YELLOW, Color.PINK, Color.ORANGE};
        int[]   xs     = {20, 62, w-42, w-82, w/2-62, w/2+42, 14, w-26};
        int[]   ys     = {h-28, h-23, h-26, h-20, h-28, h-23, h-20, h-28};
        for (int i = 0; i < xs.length; i++) {
            g2.setColor(FarmTheme.GREEN_DARK);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(xs[i], ys[i], xs[i], ys[i] + 20);
            g2.setColor(petals[i % petals.length]);
            for (int p = 0; p < 5; p++) {
                double a = p * (2 * Math.PI / 5);
                g2.fillOval(xs[i] + (int)(7 * Math.cos(a)) - 4,
                            ys[i] + (int)(7 * Math.sin(a)) - 4, 8, 8);
            }
            g2.setColor(FarmTheme.HAY_GOLD);
            g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
        }
    }
}

// ─────────────────────────────────────────────
//  Dialog utility - all farm-themed windows
// ─────────────────────────────────────────────

// Written by: Aryan Kandula - FarmDialog replaces JOptionPane with custom themed dialogs.
class FarmDialog {

    static void message(String title, String body) {
        JDialog d = makeDialog(title, 500, 360);
        JPanel  p = makePanel(title);

        JTextArea ta = new JTextArea(body);
        ta.setFont(FarmTheme.MONO);
        ta.setEditable(false);
        ta.setBackground(new Color(255, 252, 232));
        ta.setForeground(FarmTheme.TEXT);
        ta.setBorder(new EmptyBorder(10, 14, 6, 14));
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8, 14, 4, 14),
            new LineBorder(FarmTheme.BROWN, 1, true)));
        sp.getViewport().setBackground(new Color(255, 252, 232));
        p.add(sp, BorderLayout.CENTER);

        JPanel row = btnRow();
        FarmButton ok = btn("  OK  ", FarmTheme.GREEN, 120);
        ok.addActionListener(e -> d.dispose());
        row.add(ok);
        p.add(row, BorderLayout.SOUTH);
        d.add(p);
        d.setVisible(true);
    }

    static int option(String title, String prompt, String[] options) {
        final int[] result = {-1};
        JDialog d = makeDialog(title, 480, 110 + options.length * 52);
        JPanel  p = makePanel(title);

        JLabel lbl = new JLabel(prompt, SwingConstants.CENTER);
        lbl.setFont(FarmTheme.BODY);
        lbl.setForeground(FarmTheme.TEXT);
        lbl.setBorder(new EmptyBorder(10, 0, 4, 0));
        p.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 8));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(4, 28, 14, 28));
        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            FarmButton b = new FarmButton(options[i], FarmTheme.PALETTE[i % FarmTheme.PALETTE.length]);
            b.addActionListener(e -> { result[0] = idx; d.dispose(); });
            grid.add(b);
        }
        p.add(grid, BorderLayout.CENTER);
        d.add(p);
        d.pack();
        d.setLocationRelativeTo(null);
        d.setVisible(true);
        return result[0];
    }

    static String input(String title, String prompt) {
        final String[] result = {null};
        JDialog d = makeDialog(title, 450, 230);
        JPanel  p = makePanel(title);

        JLabel lbl = new JLabel(prompt, SwingConstants.CENTER);
        lbl.setFont(FarmTheme.BODY);
        lbl.setForeground(FarmTheme.TEXT);
        lbl.setBorder(new EmptyBorder(12, 16, 4, 16));
        p.add(lbl, BorderLayout.NORTH);

        JTextField field = new JTextField(24);
        field.setFont(FarmTheme.BODY);
        field.setBackground(FarmTheme.CREAM);
        field.setForeground(FarmTheme.TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FarmTheme.BROWN, 2, true),
            new EmptyBorder(5, 8, 5, 8)));
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        mid.setOpaque(false);
        mid.add(field);
        p.add(mid, BorderLayout.CENTER);

        JPanel row = btnRow();
        FarmButton ok     = btn("Confirm", FarmTheme.GREEN,    120);
        FarmButton cancel = btn("Cancel",  FarmTheme.BARN_RED, 120);
        ok.addActionListener    (e -> { result[0] = field.getText(); d.dispose(); });
        cancel.addActionListener(e -> d.dispose());
        field.addActionListener (e -> { result[0] = field.getText(); d.dispose(); });
        row.add(ok); row.add(cancel);
        p.add(row, BorderLayout.SOUTH);
        d.add(p);
        d.setVisible(true);
        return result[0];
    }

    static boolean confirm(String title, String prompt) {
        final boolean[] result = {false};
        JDialog d = makeDialog(title, 430, 210);
        JPanel  p = makePanel(title);

        JLabel lbl = new JLabel("<html><div style='text-align:center'>"
            + prompt.replace("\n","<br>") + "</div></html>", SwingConstants.CENTER);
        lbl.setFont(FarmTheme.BODY);
        lbl.setForeground(FarmTheme.TEXT);
        lbl.setBorder(new EmptyBorder(14, 16, 8, 16));
        p.add(lbl, BorderLayout.CENTER);

        JPanel row = btnRow();
        FarmButton yes = btn("Yes, Confirm", FarmTheme.GREEN,    148);
        FarmButton no  = btn("Cancel",       FarmTheme.BARN_RED, 120);
        yes.addActionListener(e -> { result[0] = true; d.dispose(); });
        no.addActionListener (e -> d.dispose());
        row.add(yes); row.add(no);
        p.add(row, BorderLayout.SOUTH);
        d.add(p);
        d.setVisible(true);
        return result[0];
    }

    private static JDialog makeDialog(String title, int w, int h) {
        JDialog d = new JDialog();
        d.setTitle(title);
        d.setSize(w, h);
        d.setModal(true);
        d.setResizable(false);
        d.setLocationRelativeTo(null);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return d;
    }

    private static JPanel makePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, FarmTheme.CREAM,
                                              0, getHeight(), new Color(245, 232, 195)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setBorder(new CompoundBorder(
            new LineBorder(FarmTheme.BROWN, 3),
            new EmptyBorder(0, 0, 6, 0)));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, FarmTheme.BARN_RED,
                                              getWidth(), 0, FarmTheme.BARN_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(FarmTheme.HEADER);
        lbl.setForeground(Color.WHITE);
        header.add(lbl);
        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private static JPanel btnRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        row.setOpaque(false);
        return row;
    }

    private static FarmButton btn(String text, Color color, int width) {
        FarmButton b = new FarmButton(text, color);
        b.setPreferredSize(new Dimension(width, 40));
        return b;
    }
}

// ─────────────────────────────────────────────
//  Main Application
// ─────────────────────────────────────────────

public class JavaCode_GroupProject_02_WORKING {

    static final ArrayList<StoreItem>     inventory = new ArrayList<>();
    static final ArrayList<Animal>        animals   = new ArrayList<>();
    static final ArrayList<ServiceRecord> services  = new ArrayList<>();
    static double revenue = 0.0;

    // Path to the CSV data file
    static final String CSV_FILE = "farm_data.csv";

    // ── Entry point ──────────────────────────

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Try loading from CSV first; fall back to seed data if file not found
        if (!loadFromCSV()) {
            seedData();
        }

        showWelcome();
        runMainLoop();
        System.exit(0);
    }

    // ─────────────────────────────────────────
    //  FILE I/O - Written by: Aryan Kandula
    // ─────────────────────────────────────────

    // Written by: Aryan Kandula - Reads all inventory, animal, and service data from CSV file.
    static boolean loadFromCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return false;

        inventory.clear();
        animals.clear();
        services.clear();
        revenue = 0.0;

        try (Scanner sc = new Scanner(file)) {
            if (!sc.hasNextLine()) {
                FarmDialog.message("File Error", "farm_data.csv is empty.\nLoading default data instead.");
                return false;
            }
            sc.nextLine(); // skip header row
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",", -1);

                if (p[0].equals("STORE") && p.length >= 5) {
                    inventory.add(new StoreItem(p[2], Double.parseDouble(p[3]), Integer.parseInt(p[4].trim())));

                } else if (p[0].equals("ANIMAL") && p.length >= 7) {
                    Animal a = new Animal(p[2], p[3], Double.parseDouble(p[4]), p[5]);
                    a.setSold(Boolean.parseBoolean(p[6].trim()));
                    animals.add(a);

                } else if (p[0].equals("SERVICE") && p.length >= 8) {
                    ServiceRecord sr = new ServiceRecord(p[2], p[3], p[4], Double.parseDouble(p[5]), p[6]);
                    if (Boolean.parseBoolean(p[7].trim())) {
                        sr.markPaid();
                        revenue += sr.getFee(); // count previously paid services toward revenue
                    }
                    services.add(sr);
                }
            }
            return true;
        } catch (Exception e) {
            FarmDialog.message("File Error", "Could not read farm_data.csv.\nLoading default data instead.");
            return false;
        }
    }

    // Written by: Aryan Kandula - Saves all current inventory, animals, and service records to CSV.
    static void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("TYPE,CATEGORY,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,FIELD6");
            for (StoreItem  i  : inventory) pw.println(i.toCSV());
            for (Animal     a  : animals)   pw.println(a.toCSV());
            for (ServiceRecord sr : services) pw.println(sr.toCSV());
            FarmDialog.message("Data Saved", "All data saved to " + CSV_FILE + " successfully.");
        } catch (IOException e) {
            FarmDialog.message("Save Error", "Could not write to " + CSV_FILE + ".\nCheck file permissions.");
        }
    }

    // ── Welcome splash ───────────────────────

    // Written by: Aryan Kandula - Displays the farm-themed welcome splash screen on startup.
    static void showWelcome() {
        JDialog splash = new JDialog();
        splash.setUndecorated(true);
        splash.setSize(460, 310);
        splash.setLocationRelativeTo(null);
        splash.setModal(true);

        FarmBackground bg = new FarmBackground();
        bg.setLayout(new BorderLayout());

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 110));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 30, 16, 30));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = GridBagConstraints.RELATIVE;
        c.insets = new Insets(6, 0, 6, 0);

        card.add(label("~ Farm Management ~",    new Font("Georgia", Font.ITALIC, 16), new Color(255, 240, 180)), c);
        card.add(label("Farm Management System", new Font("Georgia", Font.BOLD,   21), Color.WHITE), c);
        card.add(label("Group 2",                new Font("Georgia", Font.ITALIC, 13), new Color(255, 240, 180)), c);

        FarmButton enter = new FarmButton("  Enter Farm  ", FarmTheme.BARN_RED);
        enter.setPreferredSize(new Dimension(180, 44));
        enter.addActionListener(e -> splash.dispose());
        card.add(enter, c);

        bg.add(card, BorderLayout.CENTER);
        splash.add(bg);
        splash.setVisible(true);
    }

    private static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    // ── Main menu loop ───────────────────────

    static void runMainLoop() {
        String[] menu = {"Store / Inventory", "Animal Sales",
                         "Services & Payments", "View Reports",
                         "Save Data to File",   "Exit"};
        while (true) {
            int c = FarmDialog.option("Main Menu", "Select a module:", menu);
            if      (c == 0) storeMenu();
            else if (c == 1) animalMenu();
            else if (c == 2) serviceMenu();
            else if (c == 3) reportsMenu();
            else if (c == 4) saveToCSV();
            else {
                FarmDialog.message("Goodbye",
                    "Thank you for using\nFarm Management System!\n\nHave a wonderful day!\n- Group 2");
                return;
            }
        }
    }

    // ─────────────────────────────────────────
    //  MODULE 1 - Store & Inventory
    // ─────────────────────────────────────────

    // Written by: Aryan Kandula - Store sub-menu loop.
    static void storeMenu() {
        String[] opts = {"View Inventory","Add New Item","Sell Item","Restock Item","Back to Main"};
        while (true) {
            int c = FarmDialog.option("Store & Inventory", "Choose an action:", opts);
            if      (c == 0) viewInventory();
            else if (c == 1) addItem();
            else if (c == 2) sellItem();
            else if (c == 3) restockItem();
            else return;
        }
    }

    // Written by: Aryan Kandula - Displays all current inventory items.
    static void viewInventory() {
        if (inventory.isEmpty()) { FarmDialog.message("Inventory","No items yet."); return; }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-28s %-10s %s%n","Item Name","Price","Qty"));
        sb.append("-".repeat(48)).append("\n");
        for (StoreItem i : inventory) sb.append(i).append("\n");
        FarmDialog.message("Current Inventory", sb.toString());
    }

    // Written by: Aryan Kandula - Adds a new item to inventory.
    static void addItem() {
        String name = FarmDialog.input("Add New Item","Enter item name:");
        if (name == null || name.isBlank()) return;
        String ps = FarmDialog.input("Add New Item","Enter price ($):");
        if (ps == null) return;
        String qs = FarmDialog.input("Add New Item","Enter quantity:");
        if (qs == null) return;
        try {
            inventory.add(new StoreItem(name.trim(), Double.parseDouble(ps), Integer.parseInt(qs)));
            FarmDialog.message("Success","\"" + name.trim() + "\" added to inventory.");
        } catch (NumberFormatException e) {
            FarmDialog.message("Error","Please enter valid numbers for price and quantity.");
        }
    }

    // Written by: Aryan Kandula - Processes a store item sale and updates quantity.
    static void sellItem() {
        if (inventory.isEmpty()) { FarmDialog.message("Store","No items available."); return; }
        String[] labels = inventory.stream()
            .map(i -> i.getName() + "  (Qty: " + i.getQuantity() + "  |  $" + String.format("%.2f",i.getPrice()) + ")")
            .toArray(String[]::new);
        int idx = FarmDialog.option("Sell Item","Select item to sell:", labels);
        if (idx < 0) return;
        StoreItem item = inventory.get(idx);
        if (item.getQuantity() == 0) { FarmDialog.message("Out of Stock","\"" + item.getName() + "\" is out of stock."); return; }
        String qs = FarmDialog.input("Sell Item","Qty to sell (max " + item.getQuantity() + "):");
        if (qs == null) return;
        try {
            int qty = Integer.parseInt(qs);
            if (qty <= 0 || qty > item.getQuantity()) { FarmDialog.message("Error","Invalid quantity."); return; }
            item.setQuantity(item.getQuantity() - qty);
            double sale = qty * item.getPrice();
            revenue += sale;
            FarmDialog.message("Sale Complete",
                "Sold: " + qty + "x " + item.getName() +
                "\nTotal:  $" + String.format("%.2f", sale) +
                "\nRemaining:  " + item.getQuantity() + " units");
        } catch (NumberFormatException e) {
            FarmDialog.message("Error","Please enter a valid number.");
        }
    }

    // Written by: Aryan Kandula - Restocks an existing inventory item.
    static void restockItem() {
        if (inventory.isEmpty()) { FarmDialog.message("Store","No items in inventory."); return; }
        String[] names = inventory.stream().map(StoreItem::getName).toArray(String[]::new);
        int idx = FarmDialog.option("Restock","Select item to restock:", names);
        if (idx < 0) return;
        StoreItem item = inventory.get(idx);
        String qs = FarmDialog.input("Restock","Add how many units of \"" + item.getName() + "\"?");
        if (qs == null) return;
        try {
            item.setQuantity(item.getQuantity() + Integer.parseInt(qs));
            FarmDialog.message("Restocked","New total for \"" + item.getName() + "\": " + item.getQuantity() + " units.");
        } catch (NumberFormatException e) {
            FarmDialog.message("Error","Please enter a valid number.");
        }
    }

    // ─────────────────────────────────────────
    //  MODULE 2 - Animal Sales
    // ─────────────────────────────────────────

    // Written by: Aryan Kandula - Animal sales sub-menu loop.
    static void animalMenu() {
        String[] opts = {"View Available Animals","Add Animal","Sell Animal","View All Animals","Back to Main"};
        while (true) {
            int c = FarmDialog.option("Animal Sales","Choose an action:", opts);
            if      (c == 0) viewAnimals(false);
            else if (c == 1) addAnimal();
            else if (c == 2) sellAnimal();
            else if (c == 3) viewAnimals(true);
            else return;
        }
    }

    // Written by: Aryan Kandula - Displays animals, optionally filtering to available only.
    static void viewAnimals(boolean showAll) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-20s %-10s %-18s %s%n","Type","Breed","Price","Source","Status"));
        sb.append("-".repeat(70)).append("\n");
        boolean found = false;
        for (Animal a : animals) {
            if (showAll || !a.isSold()) { sb.append(a).append("\n"); found = true; }
        }
        if (!found) sb.append("No animals found.");
        FarmDialog.message(showAll ? "All Animals" : "Available Animals", sb.toString());
    }

    // Written by: Aryan Kandula - Adds a farm or breeder animal to the list.
    static void addAnimal() {
        String[] types = {"Duck","Chicken","Hamster","Rabbit","Goat","Pig","Other"};
        int ti = FarmDialog.option("Animal Type","Select animal type:", types);
        if (ti < 0) return;
        String breed = FarmDialog.input("Add Animal","Enter breed / description:");
        if (breed == null || breed.isBlank()) return;
        String ps = FarmDialog.input("Add Animal","Enter sale price ($):");
        if (ps == null) return;
        String[] srcOpts = {"Farm (own animals)","Local Breeder (specialty resale)"};
        int si = FarmDialog.option("Animal Source","Where is this animal from?", srcOpts);
        if (si < 0) return;
        String source = "Farm";
        if (si == 1) {
            source = FarmDialog.input("Breeder Name","Enter the breeder's name:");
            if (source == null || source.isBlank()) source = "Local Breeder";
        }
        try {
            animals.add(new Animal(types[ti], breed.trim(), Double.parseDouble(ps), source));
            FarmDialog.message("Success","Animal added successfully!");
        } catch (NumberFormatException e) {
            FarmDialog.message("Error","Please enter a valid price.");
        }
    }

    // Written by: Aryan Kandula - Processes an animal sale with confirmation dialog.
    static void sellAnimal() {
        ArrayList<Animal> avail = new ArrayList<>();
        for (Animal a : animals) if (!a.isSold()) avail.add(a);
        if (avail.isEmpty()) { FarmDialog.message("Animals","No animals available for sale."); return; }
        String[] labels = avail.stream()
            .map(a -> a.getType() + " - " + a.getBreed() + "  ($" + String.format("%.2f",a.getSalePrice()) + ")")
            .toArray(String[]::new);
        int idx = FarmDialog.option("Sell Animal","Select animal to sell:", labels);
        if (idx < 0) return;
        Animal a = avail.get(idx);
        if (FarmDialog.confirm("Confirm Sale",
            "Animal: " + a.getType() + " - " + a.getBreed() +
            "\nPrice:  $" + String.format("%.2f",a.getSalePrice()) +
            "\n\nConfirm this sale?")) {
            a.markSold();
            revenue += a.getSalePrice();
            FarmDialog.message("Sold!", a.getType() + " sold for $" + String.format("%.2f",a.getSalePrice()) + ".");
        }
    }

    // ─────────────────────────────────────────
    //  MODULE 3 - Services & Payments
    // ─────────────────────────────────────────

    // Written by: Aryan Kandula - Services sub-menu loop.
    static void serviceMenu() {
        String[] opts = {"View All Records","Schedule New Service","Mark Service as Paid","View Unpaid Services","Back to Main"};
        while (true) {
            int c = FarmDialog.option("Services & Payments","Choose an action:", opts);
            if      (c == 0) viewServices(false);
            else if (c == 1) scheduleService();
            else if (c == 2) markPaid();
            else if (c == 3) viewServices(true);
            else return;
        }
    }

    // Written by: Aryan Kandula - Displays service records, optionally filtering to unpaid only.
    static void viewServices(boolean unpaidOnly) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-18s %-10s %-18s %-9s %-12s %s%n",
            "Customer","Animal","Service","Fee","Date","Status"));
        sb.append("-".repeat(80)).append("\n");
        boolean found = false;
        for (ServiceRecord sr : services) {
            if (!unpaidOnly || !sr.isPaid()) { sb.append(sr).append("\n"); found = true; }
        }
        if (!found) sb.append("No records found.");
        FarmDialog.message(unpaidOnly ? "Unpaid Services" : "All Service Records", sb.toString());
    }

    // Written by: Aryan Kandula - Schedules a new veterinary or grooming service.
    static void scheduleService() {
        String customer = FarmDialog.input("New Service","Enter customer name:");
        if (customer == null || customer.isBlank()) return;
        String animalType = FarmDialog.input("New Service","Enter animal type (e.g. Dog, Rabbit):");
        if (animalType == null || animalType.isBlank()) return;
        String[] svcTypes = {"Wellness Checkup","Vaccination","Wound Treatment",
                             "Grooming","Deworming","Microchipping","Other"};
        int si = FarmDialog.option("Service Type","Select the service:", svcTypes);
        if (si < 0) return;
        String fs = FarmDialog.input("Service Fee","Enter fee ($):");
        if (fs == null) return;
        String date = FarmDialog.input("Appointment Date","Enter date (MM/DD/YYYY):");
        if (date == null || date.isBlank()) return;
        try {
            services.add(new ServiceRecord(customer.trim(), animalType.trim(),
                svcTypes[si], Double.parseDouble(fs), date.trim()));
            FarmDialog.message("Scheduled","Service scheduled for " + customer.trim() + " on " + date.trim() + ".");
        } catch (NumberFormatException e) {
            FarmDialog.message("Error","Please enter a valid fee amount.");
        }
    }

    // Written by: Aryan Kandula - Marks a service as paid and records the revenue.
    static void markPaid() {
        ArrayList<ServiceRecord> unpaid = new ArrayList<>();
        for (ServiceRecord sr : services) if (!sr.isPaid()) unpaid.add(sr);
        if (unpaid.isEmpty()) { FarmDialog.message("Payments","All services are fully paid up."); return; }
        String[] labels = unpaid.stream()
            .map(sr -> sr.getCustomerName() + "  -  $" + String.format("%.2f",sr.getFee()))
            .toArray(String[]::new);
        int idx = FarmDialog.option("Mark as Paid","Select record to mark paid:", labels);
        if (idx < 0) return;
        ServiceRecord sr = unpaid.get(idx);
        sr.markPaid();
        revenue += sr.getFee();
        FarmDialog.message("Payment Recorded",
            "Payment of $" + String.format("%.2f",sr.getFee()) +
            " recorded for " + sr.getCustomerName() + ".");
    }

    // ─────────────────────────────────────────
    //  MODULE 4 - Business Report
    // ─────────────────────────────────────────

    // Written by: Aryan Kandula - Generates a summary business report.
    static void reportsMenu() {
        long   sold   = animals.stream().filter(Animal::isSold).count();
        long   paid   = services.stream().filter(ServiceRecord::isPaid).count();
        double unpaid = services.stream().filter(r -> !r.isPaid()).mapToDouble(ServiceRecord::getFee).sum();

        FarmDialog.message("Business Report",
            "================================================\n" +
            "           FARM BUSINESS REPORT                \n" +
            "                   Group 2                      \n" +
            "================================================\n\n" +
            "[ STORE INVENTORY ]\n" +
            "  Items tracked        : " + inventory.size() + "\n\n" +
            "[ ANIMAL SALES ]\n" +
            "  Total listed         : " + animals.size() + "\n" +
            "  Sold                 : " + sold + "\n" +
            "  Available            : " + (animals.size() - sold) + "\n\n" +
            "[ SERVICES ]\n" +
            "  Total scheduled      : " + services.size() + "\n" +
            "  Paid                 : " + paid + "\n" +
            "  Outstanding balance  : $" + String.format("%.2f", unpaid) + "\n\n" +
            "[ TOTAL REVENUE RECORDED ]\n" +
            "  $" + String.format("%.2f", revenue) + "\n\n" +
            "================================================");
    }

    // ─────────────────────────────────────────
    //  Fallback seed data (used if CSV missing)
    //  NOTE: kept in draft only; removed in final
    // ─────────────────────────────────────────

    static void seedData() {
        inventory.add(new StoreItem("Chicken Feed (50 lb)",      18.99, 20));
        inventory.add(new StoreItem("Rabbit Pellets (10 lb)",    12.49, 15));
        inventory.add(new StoreItem("Duck Starter Feed (25 lb)", 14.99, 10));
        inventory.add(new StoreItem("Animal Water Feeder",        9.99,  8));
        inventory.add(new StoreItem("Hay Bale (small)",           7.50, 25));
        inventory.add(new StoreItem("Pet Carrier (medium)",      34.99,  5));
        inventory.add(new StoreItem("Fencing Wire (50 ft)",      22.00, 12));

        animals.add(new Animal("Duck",    "Pekin",            15.00, "Farm"));
        animals.add(new Animal("Duck",    "Mallard",          18.00, "Farm"));
        animals.add(new Animal("Chicken", "Rhode Island Red", 12.00, "Farm"));
        animals.add(new Animal("Chicken", "Silkie",           20.00, "Sunrise Breeders"));
        animals.add(new Animal("Hamster", "Syrian",           10.00, "Farm"));
        animals.add(new Animal("Rabbit",  "Holland Lop",      45.00, "Valley Breeders"));
        animals.add(new Animal("Rabbit",  "Mini Rex",         40.00, "Farm"));

        services.add(new ServiceRecord("John Smith",  "Dog",     "Wellness Checkup", 45.00, "02/10/2026"));
        services.add(new ServiceRecord("Maria Lopez", "Rabbit",  "Vaccination",      30.00, "02/15/2026"));
        services.add(new ServiceRecord("Tom Harris",  "Chicken", "Wound Treatment",  25.00, "02/18/2026"));
        services.get(0).markPaid();
        revenue += 45.00;
    }
}
