package controller;

import dao.MahasiswaDAO;
import model.Mahasiswa;

import java.sql.SQLException;
import java.util.List;

public class MahasiswaController {
    private MahasiswaDAO dao = new MahasiswaDAO();

    public boolean tambah(Mahasiswa m) throws SQLException {
        return dao.insert(m);
    }

    public boolean ubah(Mahasiswa m) throws SQLException {
        return dao.update(m);
    }

    public boolean hapus(int id) throws SQLException {
        return dao.delete(id);
    }

    public Mahasiswa cariById(int id) throws SQLException {
        return dao.findById(id);
    }

    public List<Mahasiswa> semua() throws SQLException {
        return dao.findAll();
    }

    public List<Mahasiswa> cariRange(int from, int to) throws SQLException {
        return dao.findByIdRange(from, to);
    }
}
