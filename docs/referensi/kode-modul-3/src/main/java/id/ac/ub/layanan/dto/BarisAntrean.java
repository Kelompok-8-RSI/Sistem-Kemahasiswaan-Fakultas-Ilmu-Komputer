package id.ac.ub.layanan.dto;

import id.ac.ub.layanan.entity.Pengajuan;

// satu baris tabel antrean: pengajuan + sisa waktu (hari) + label prioritas (hasil PC-24)
public record BarisAntrean(Pengajuan pengajuan, int sisaWaktu, String label) {

    // nama kelas CSS badge: merah / kuning / hijau
    public String warna() {
        return label.startsWith("Merah") ? "merah" : label.toLowerCase();
    }

    // teks badge pada UI-06: Terlewat, < 3 hari, 3–7 hari, > 7 hari
    public String teksPrioritas() {
        return switch (label) {
            case "Merah (Terlewat)" -> "Terlewat";
            case "Merah" -> "< 3 hari";
            case "Kuning" -> "3–7 hari";
            default -> "> 7 hari";
        };
    }

    // kolom Sisa Waktu: "8 hari" atau "Lewat 1 hari"
    public String teksSisaWaktu() {
        return sisaWaktu < 0 ? "Lewat " + (-sisaWaktu) + " hari"
                : sisaWaktu == 0 ? "Hari ini" : sisaWaktu + " hari";
    }
}
