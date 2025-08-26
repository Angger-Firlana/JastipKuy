-- Helper SQL (tanpa mengubah struktur tabel) untuk mengecek dan mengelola data rating

USE jastip;

-- Lihat data yang ada sekarang
-- 4) (Opsional) Normalisasi data lama: set titipan_id = NULL dulu (tidak diketahui)
-- 1) Cek rating yang match prefix order tertentu (ganti :orderId)
-- SELECT * FROM ratiing_user WHERE deskripsi LIKE CONCAT('[ORDER#', :orderId, ']%');

-- 2) Cek apakah user sudah rating jastiper untuk order tertentu (ganti :userId, :driverId, :orderId)
-- SELECT COUNT(*) FROM ratiing_user 
--   WHERE idUser = :userId AND idDriver = :driverId AND deskripsi LIKE CONCAT('[ORDER#', :orderId, ']%');

-- 3) List semua rating per jastiper
-- SELECT id, idUser, idDriver, rating_ketepatan, rating_pelayanan, deskripsi FROM ratiing_user WHERE idDriver = :driverId ORDER BY id DESC;

