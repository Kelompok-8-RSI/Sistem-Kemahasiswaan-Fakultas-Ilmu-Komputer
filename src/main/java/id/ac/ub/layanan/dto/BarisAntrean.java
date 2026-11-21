package id.ac.ub.layanan.dto;

import id.ac.ub.layanan.entity.Pengajuan;

public record BarisAntrean(Pengajuan pengajuan, int sisaWaktu, String label) {

    public String warna() {
        return label.startsWith("Merah") ? "merah" : label.toLowerCase();
    }

    public String teksPrioritas() {
        return switch (label) {
            case "Merah (Terlewat)" -> "Terlewat";
            case "Merah" -> "< 3 hari";
            case "Kuning" -> "3–7 hari";
            default -> "> 7 hari";
        };
    }

    public String teksSisaWaktu() {
        return sisaWaktu < 0 ? "Lewat " + (-sisaWaktu) + " hari"
                : sisaWaktu == 0 ? "Hari ini" : sisaWaktu + " hari";
    }
}
