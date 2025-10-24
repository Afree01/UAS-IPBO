package view;

import controller.MahasiswaController;
import model.Mahasiswa;
import report.ReportGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class MainForm extends JFrame {
    private MahasiswaController controller = new MahasiswaController();

    private JTextField tfId = new JTextField();
    private JTextField tfNama = new JTextField();
    private JTextField tfJurusan = new JTextField();
    private JTextField tfAngkatan = new JTextField();

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField tfIdFrom = new JTextField(5);
    private JTextField tfIdTo = new JTextField(5);

    private JPanel mainPanel;

    public MainForm() {
        setTitle("Student Master - UAS PBO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        // 🌈 Panel utama dengan gradasi
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(200, 220, 255),
                        0, getHeight(), new Color(180, 200, 240));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(mainPanel);

        initComponents();
        loadTable();
    }

    private void initComponents() {
        // 🧭 Header panel (logo + judul)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        try {
            JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/report/logo.png")));
            headerPanel.add(logo);
        } catch (Exception ignored) {}
        JLabel title = new JLabel("STUDENT MASTER SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 100));
        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 📋 Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("ID:");
        JLabel lblNama = new JLabel("Nama:");
        JLabel lblJurusan = new JLabel("Jurusan:");
        JLabel lblAngkatan = new JLabel("Angkatan:");
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJurusan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAngkatan.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        c.gridx = 0; c.gridy = 0; formPanel.add(lblId, c);
        c.gridx = 1; c.gridy = 0; formPanel.add(tfId, c);
        c.gridx = 0; c.gridy = 1; formPanel.add(lblNama, c);
        c.gridx = 1; c.gridy = 1; formPanel.add(tfNama, c);
        c.gridx = 0; c.gridy = 2; formPanel.add(lblJurusan, c);
        c.gridx = 1; c.gridy = 2; formPanel.add(tfJurusan, c);
        c.gridx = 0; c.gridy = 3; formPanel.add(lblAngkatan, c);
        c.gridx = 1; c.gridy = 3; formPanel.add(tfAngkatan, c);

        // 🎨 Tombol dengan gaya baru
        JButton btnTambah = createStyledButton("Tambah", new Color(0, 153, 255));
        JButton btnUbah = createStyledButton("Ubah", new Color(255, 180, 0));
        JButton btnHapus = createStyledButton("Hapus", new Color(255, 80, 80));
        JButton btnClear = createStyledButton("Clear", new Color(120, 120, 120));
        JButton btnCapture = createStyledButton("Capture (Screenshot)", new Color(0, 180, 90));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(btnTambah);
        btnPanel.add(btnUbah);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);
        btnPanel.add(btnCapture);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; formPanel.add(btnPanel, c);

        // 📊 Tabel
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama", "Jurusan", "Angkatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        JScrollPane sp = new JScrollPane(table);

        // 🧾 Report panel
        JPanel reportPanel = new JPanel();
        reportPanel.setOpaque(false);
        reportPanel.add(new JLabel("ID From:"));
        reportPanel.add(tfIdFrom);
        reportPanel.add(new JLabel("ID To:"));
        reportPanel.add(tfIdTo);
        JButton btnPrint = createStyledButton("Generate PDF Report (Jasper)", new Color(0, 120, 200));
        reportPanel.add(btnPrint);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, sp);
        split.setDividerLocation(350);
        split.setOpaque(false);

        add(split, BorderLayout.CENTER);
        add(reportPanel, BorderLayout.SOUTH);

        // 🧩 Listener tombol
        btnTambah.addActionListener(e -> tambahAction());
        btnUbah.addActionListener(e -> ubahAction());
        btnHapus.addActionListener(e -> hapusAction());
        btnClear.addActionListener(e -> clearForm());
        btnCapture.addActionListener(e -> {
            captureComponent(this, "captures/action_capture.png");
            JOptionPane.showMessageDialog(this, "Screenshot tersimpan di captures/action_capture.png");
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    tfId.setText(tableModel.getValueAt(r, 0).toString());
                    tfNama.setText(tableModel.getValueAt(r, 1).toString());
                    tfJurusan.setText(tableModel.getValueAt(r, 2).toString());
                    tfAngkatan.setText(tableModel.getValueAt(r, 3).toString());

                    // Ganti warna background tiap mahasiswa
                    Random rnd = new Random(Integer.parseInt(tfId.getText()));
                    mainPanel.setBackground(new Color(180 + rnd.nextInt(50), 180 + rnd.nextInt(50), 255));
                }
            }
        });

        btnPrint.addActionListener(e -> {
            try {
                int from = Integer.parseInt(tfIdFrom.getText().trim());
                int to = Integer.parseInt(tfIdTo.getText().trim());
                List<Mahasiswa> data = controller.cariRange(from, to);
                if (data.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada data pada rentang ID tersebut.");
                    return;
                }

                InputStream logoStream = getClass().getResourceAsStream("/report/logo.png");
                File outDir = new File("reports");
                outDir.mkdirs();
                String outputPath = "reports/mahasiswa_report_" + from + "_" + to + ".pdf";
                ReportGenerator.generateReport(data, from, to, logoStream, outputPath);

                captureComponent(this, "captures/report_capture.png");
                JOptionPane.showMessageDialog(this, "Report berhasil dibuat: " + outputPath);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal membuat report: " + ex.getMessage());
            }
        });
    }

    // 🌟 Utility buat tombol keren
    private JButton createStyledButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // efek hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }

    private void tambahAction() {
        try {
            Mahasiswa m = new Mahasiswa(Integer.parseInt(tfId.getText().trim()),
                    tfNama.getText().trim(), tfJurusan.getText().trim(), Integer.parseInt(tfAngkatan.getText().trim()));
            if (controller.tambah(m)) {
                loadTable();
                captureComponent(this, "captures/create.png");
                JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal tambah: " + ex.getMessage());
        }
    }

    private void ubahAction() {
        try {
            Mahasiswa m = new Mahasiswa(Integer.parseInt(tfId.getText().trim()),
                    tfNama.getText().trim(), tfJurusan.getText().trim(), Integer.parseInt(tfAngkatan.getText().trim()));
            if (controller.ubah(m)) {
                loadTable();
                captureComponent(this, "captures/update.png");
                JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            } else {
                JOptionPane.showMessageDialog(this, "Data gagal diubah atau ID tidak ditemukan.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + ex.getMessage());
        }
    }

    private void hapusAction() {
        try {
            int id = Integer.parseInt(tfId.getText().trim());
            int ok = JOptionPane.showConfirmDialog(this, "Hapus data ID " + id + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                if (controller.hapus(id)) {
                    loadTable();
                    captureComponent(this, "captures/delete.png");
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + ex.getMessage());
        }
    }

    private void loadTable() {
        try {
            List<Mahasiswa> list = controller.semua();
            tableModel.setRowCount(0);
            for (Mahasiswa m : list) {
                tableModel.addRow(new Object[]{m.getId(), m.getNama(), m.getJurusan(), m.getAngkatan()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        tfId.setText("");
        tfNama.setText("");
        tfJurusan.setText("");
        tfAngkatan.setText("");
    }

    private void captureComponent(Component c, String path) {
        try {
            File dir = new File("captures");
            dir.mkdirs();
            BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
            c.paint(img.getGraphics());
            ImageIO.write(img, "png", new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new MainForm().setVisible(true);
        });
    }
}