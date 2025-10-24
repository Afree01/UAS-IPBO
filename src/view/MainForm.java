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
import java.io.FileInputStream;
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

    private JTextField tfIdFrom = new JTextField();
    private JTextField tfIdTo = new JTextField();

    private JPanel mainPanel;

    public MainForm() {
        setTitle("Student Master - UAS PBO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout()) {
            private BufferedImage bg;
            {
                try {
                    bg = ImageIO.read(getClass().getResource("/report/logo.png")); // small logo as background example
                } catch (Exception e) {
                    bg = null;
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bg != null) {
                    // tile the background image
                    for (int x = 0; x < getWidth(); x += bg.getWidth()) {
                        for (int y = 0; y < getHeight(); y += bg.getHeight()) {
                            g.drawImage(bg, x, y, this);
                        }
                    }
                } else {
                    // fallback background color
                    g.setColor(new Color(230, 240, 255));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        setContentPane(mainPanel);

        initComponents();
        loadTable();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; formPanel.add(new JLabel("ID:"), c);
        c.gridx = 1; c.gridy = 0; formPanel.add(tfId, c);

        c.gridx = 0; c.gridy = 1; formPanel.add(new JLabel("Nama:"), c);
        c.gridx = 1; c.gridy = 1; formPanel.add(tfNama, c);

        c.gridx = 0; c.gridy = 2; formPanel.add(new JLabel("Jurusan:"), c);
        c.gridx = 1; c.gridy = 2; formPanel.add(tfJurusan, c);

        c.gridx = 0; c.gridy = 3; formPanel.add(new JLabel("Angkatan:"), c);
        c.gridx = 1; c.gridy = 3; formPanel.add(tfAngkatan, c);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnClear = new JButton("Clear");
        JButton btnCapture = new JButton("Capture (screenshot)");

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(btnTambah); btnPanel.add(btnUbah); btnPanel.add(btnHapus); btnPanel.add(btnClear); btnPanel.add(btnCapture);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; formPanel.add(btnPanel, c);

        // table
        tableModel = new DefaultTableModel(new Object[]{"ID","Nama","Jurusan","Angkatan"}, 0) {
            @Override public boolean isCellEditable(int row, int column){ return false; }
        };
        table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);

        // report panel
        JPanel reportPanel = new JPanel();
        reportPanel.setOpaque(false);
        reportPanel.add(new JLabel("ID From:")); reportPanel.add(tfIdFrom);
        reportPanel.add(new JLabel("ID To:")); reportPanel.add(tfIdTo);
        JButton btnPrint = new JButton("Generate PDF Report (Jasper)");
        reportPanel.add(btnPrint);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, sp);
        split.setDividerLocation(320);

        add(split, BorderLayout.CENTER);
        add(reportPanel, BorderLayout.SOUTH);

        // action listeners
        btnTambah.addActionListener(e -> tambahAction());
        btnUbah.addActionListener(e -> ubahAction());
        btnHapus.addActionListener(e -> hapusAction());
        btnClear.addActionListener(e -> clearForm());
        btnCapture.addActionListener(e -> {
            captureComponent(this, "captures/action_capture.png");
            JOptionPane.showMessageDialog(this, "Screenshot saved to captures/action_capture.png");
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    tfId.setText(tableModel.getValueAt(r,0).toString());
                    tfNama.setText(tableModel.getValueAt(r,1).toString());
                    tfJurusan.setText(tableModel.getValueAt(r,2).toString());
                    tfAngkatan.setText(tableModel.getValueAt(r,3).toString());

                    // change background color randomly to ensure not the same UI for each mahasiswa
                    Random rnd = new Random(Integer.parseInt(tfId.getText()));
                    mainPanel.setBackground(new Color(150 + rnd.nextInt(100), 150 + rnd.nextInt(100), 150 + rnd.nextInt(100)));
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
                // generate report
                InputStream logoStream = getClass().getResourceAsStream("/report/logo.png");
                File outDir = new File("reports");
                outDir.mkdirs();
                String outputPath = "reports/mahasiswa_report_" + from + "_" + to + ".pdf";
                ReportGenerator.generateReport(data, from, to, logoStream, outputPath);

                // capture report button click
                captureComponent(this, "captures/report_capture.png");

                JOptionPane.showMessageDialog(this, "Report berhasil dibuat: " + outputPath);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Masukkan ID From/To yang valid (integer).");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal membuat report: " + ex.getMessage());
            }
        });
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
            ex.printStackTrace();
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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + ex.getMessage());
        }
    }

    private void hapusAction() {
        try {
            int id = Integer.parseInt(tfId.getText().trim());
            int ok = JOptionPane.showConfirmDialog(this, "Hapus data ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                if (controller.hapus(id)) {
                    loadTable();
                    captureComponent(this, "captures/delete.png");
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus. ID mungkin tidak ada.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
        }
    }

    private void clearForm() {
        tfId.setText(""); tfNama.setText(""); tfJurusan.setText(""); tfAngkatan.setText("");
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
            try {
                // set look and feel system
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) {}
            MainForm mf = new MainForm();
            mf.setVisible(true);
        });
    }
}
