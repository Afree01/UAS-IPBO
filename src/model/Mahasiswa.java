package model;

public class Mahasiswa {
    private int id;
    private String nama;
    private String jurusan;
    private int angkatan;

    public Mahasiswa() {}

    public Mahasiswa(int id, String nama, String jurusan, int angkatan) {
        this.id = id;
        this.nama = nama;
        this.jurusan = jurusan;
        this.angkatan = angkatan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }

    public int getAngkatan() { return angkatan; }
    public void setAngkatan(int angkatan) { this.angkatan = angkatan; }
}