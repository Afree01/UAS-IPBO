package dao;

import model.Mahasiswa;
import utils.Koneksi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MahasiswaDAO {

    public boolean insert(Mahasiswa m) throws SQLException {
        String sql = "INSERT INTO mahasiswa (id, nama, jurusan, angkatan) VALUES (?, ?, ?, ?)";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNama());
            ps.setString(3, m.getJurusan());
            ps.setInt(4, m.getAngkatan());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Mahasiswa m) throws SQLException {
        String sql = "UPDATE mahasiswa SET nama=?, jurusan=?, angkatan=? WHERE id=?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNama());
            ps.setString(2, m.getJurusan());
            ps.setInt(3, m.getAngkatan());
            ps.setInt(4, m.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE id=?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Mahasiswa findById(int id) throws SQLException {
        String sql = "SELECT * FROM mahasiswa WHERE id=?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Mahasiswa(rs.getInt("id"), rs.getString("nama"),
                            rs.getString("jurusan"), rs.getInt("angkatan"));
                }
            }
        }
        return null;
    }

    public List<Mahasiswa> findAll() throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY id";
        try (Connection conn = Koneksi.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Mahasiswa(rs.getInt("id"), rs.getString("nama"),
                        rs.getString("jurusan"), rs.getInt("angkatan")));
            }
        }
        return list;
    }

    public List<Mahasiswa> findByIdRange(int idFrom, int idTo) throws SQLException {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa WHERE id BETWEEN ? AND ? ORDER BY id";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFrom);
            ps.setInt(2, idTo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Mahasiswa(rs.getInt("id"), rs.getString("nama"),
                            rs.getString("jurusan"), rs.getInt("angkatan")));
                }
            }
        }
        return list;
    }
}
