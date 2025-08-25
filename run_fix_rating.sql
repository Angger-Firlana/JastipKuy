-- Jalankan script ini di MariaDB untuk memperbaiki data rating
-- Masalah: field idUser menyimpan user yang memberikan rating, bukan jastiper yang di-rating

USE jastip;

-- Lihat data yang ada sekarang
SELECT 'Data sebelum diperbaiki:' as info;
SELECT * FROM ratiing_user;

-- Perbaiki data existing:
-- Rating ID 1: seharusnya untuk jastiper ID 6
-- Rating ID 2: seharusnya untuk jastiper ID 3

-- Update rating ID 1 agar field idUser = 6 (jastiper yang di-rating)
UPDATE ratiing_user SET idUser = 6 WHERE id = 1;

-- Update rating ID 2 agar field idUser = 3 (jastiper yang di-rating)  
UPDATE ratiing_user SET idUser = 3 WHERE id = 2;

-- Verifikasi data yang sudah diperbaiki
SELECT 'Data setelah diperbaiki:' as info;
SELECT * FROM ratiing_user;

-- Catatan: 
-- Field idUser sekarang menyimpan jastiper yang di-rating
-- Untuk tracking siapa yang memberikan rating, perlu field tambahan id_penilai

