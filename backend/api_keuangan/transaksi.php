<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

// Handle preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

require_once 'koneksi.php';

$method = $_SERVER['REQUEST_METHOD'];

switch ($method) {
    case 'GET':
        getTransaksi($conn);
        break;
    case 'POST':
        addTransaksi($conn);
        break;
    case 'PUT':
        updateTransaksi($conn);
        break;
    case 'DELETE':
        deleteTransaksi($conn);
        break;
    default:
        http_response_code(405);
        echo json_encode(["success" => false, "message" => "Method not allowed"]);
        break;
}

// ============================================
// GET — Ambil transaksi
// ============================================
function getTransaksi($conn) {
    // Ambil transaksi spesifik berdasarkan id
    if (isset($_GET['id'])) {
        $id = intval($_GET['id']);
        try {
            $stmt = $conn->prepare("SELECT * FROM transaksi WHERE id_transaksi = :id");
            $stmt->execute([':id' => $id]);

            if ($stmt->rowCount() === 0) {
                echo json_encode([
                    "success" => false,
                    "message" => "Transaksi tidak ditemukan"
                ]);
                return;
            }

            $transaksi = $stmt->fetch(PDO::FETCH_ASSOC);
            echo json_encode([
                "success" => true,
                "message" => "Data transaksi berhasil diambil",
                "data" => $transaksi
            ]);

        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Gagal mengambil transaksi: " . $e->getMessage()
            ]);
        }
        return;
    }

    // Ambil semua transaksi berdasarkan id_user
    $id_user = isset($_GET['id_user']) ? intval($_GET['id_user']) : 0;

    if ($id_user <= 0) {
        echo json_encode([
            "success" => false,
            "message" => "ID user tidak valid"
        ]);
        return;
    }

    try {
        $stmt = $conn->prepare("SELECT * FROM transaksi WHERE id_user = :id_user ORDER BY tanggal DESC, created_at DESC");
        $stmt->execute([':id_user' => $id_user]);

        $transaksi = $stmt->fetchAll(PDO::FETCH_ASSOC);

        echo json_encode([
            "success" => true,
            "message" => "Data transaksi berhasil diambil",
            "data" => $transaksi
        ]);

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Gagal mengambil transaksi: " . $e->getMessage()
        ]);
    }
}

// ============================================
// POST — Tambah transaksi baru
// ============================================
function addTransaksi($conn) {
    $data = json_decode(file_get_contents("php://input"), true);

    if (empty($data)) {
        $data = $_POST;
    }

    $id_user = isset($data['id_user']) ? intval($data['id_user']) : 0;
    $jenis = isset($data['jenis']) ? trim($data['jenis']) : '';
    $kategori = isset($data['kategori']) ? trim($data['kategori']) : '';
    $nominal = isset($data['nominal']) ? floatval($data['nominal']) : 0;
    $tanggal = isset($data['tanggal']) ? trim($data['tanggal']) : '';
    $keterangan = isset($data['keterangan']) ? trim($data['keterangan']) : '';

    // Validasi
    if ($id_user <= 0 || empty($jenis) || empty($kategori) || $nominal <= 0 || empty($tanggal)) {
        echo json_encode([
            "success" => false,
            "message" => "Semua field wajib diisi dengan benar"
        ]);
        return;
    }

    if (!in_array($jenis, ['pemasukan', 'pengeluaran'])) {
        echo json_encode([
            "success" => false,
            "message" => "Jenis transaksi harus 'pemasukan' atau 'pengeluaran'"
        ]);
        return;
    }

    try {
        $stmt = $conn->prepare("INSERT INTO transaksi (id_user, jenis, kategori, nominal, tanggal, keterangan) VALUES (:id_user, :jenis, :kategori, :nominal, :tanggal, :keterangan)");
        $stmt->execute([
            ':id_user' => $id_user,
            ':jenis' => $jenis,
            ':kategori' => $kategori,
            ':nominal' => $nominal,
            ':tanggal' => $tanggal,
            ':keterangan' => $keterangan
        ]);

        echo json_encode([
            "success" => true,
            "message" => "Transaksi berhasil ditambahkan"
        ]);

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Gagal menambahkan transaksi: " . $e->getMessage()
        ]);
    }
}

// ============================================
// PUT — Update transaksi
// ============================================
function updateTransaksi($conn) {
    $data = json_decode(file_get_contents("php://input"), true);

    $id_transaksi = isset($data['id_transaksi']) ? intval($data['id_transaksi']) : 0;
    $jenis = isset($data['jenis']) ? trim($data['jenis']) : '';
    $kategori = isset($data['kategori']) ? trim($data['kategori']) : '';
    $nominal = isset($data['nominal']) ? floatval($data['nominal']) : 0;
    $tanggal = isset($data['tanggal']) ? trim($data['tanggal']) : '';
    $keterangan = isset($data['keterangan']) ? trim($data['keterangan']) : '';

    // Validasi
    if ($id_transaksi <= 0 || empty($jenis) || empty($kategori) || $nominal <= 0 || empty($tanggal)) {
        echo json_encode([
            "success" => false,
            "message" => "Semua field wajib diisi dengan benar"
        ]);
        return;
    }

    if (!in_array($jenis, ['pemasukan', 'pengeluaran'])) {
        echo json_encode([
            "success" => false,
            "message" => "Jenis transaksi harus 'pemasukan' atau 'pengeluaran'"
        ]);
        return;
    }

    try {
        $stmt = $conn->prepare("UPDATE transaksi SET jenis = :jenis, kategori = :kategori, nominal = :nominal, tanggal = :tanggal, keterangan = :keterangan WHERE id_transaksi = :id_transaksi");
        $stmt->execute([
            ':jenis' => $jenis,
            ':kategori' => $kategori,
            ':nominal' => $nominal,
            ':tanggal' => $tanggal,
            ':keterangan' => $keterangan,
            ':id_transaksi' => $id_transaksi
        ]);

        if ($stmt->rowCount() > 0) {
            echo json_encode([
                "success" => true,
                "message" => "Transaksi berhasil diperbarui"
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Transaksi tidak ditemukan atau tidak ada perubahan"
            ]);
        }

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Gagal memperbarui transaksi: " . $e->getMessage()
        ]);
    }
}

// ============================================
// DELETE — Hapus transaksi
// ============================================
function deleteTransaksi($conn) {
    $id = isset($_GET['id']) ? intval($_GET['id']) : 0;

    if ($id <= 0) {
        echo json_encode([
            "success" => false,
            "message" => "ID transaksi tidak valid"
        ]);
        return;
    }

    try {
        $stmt = $conn->prepare("DELETE FROM transaksi WHERE id_transaksi = :id");
        $stmt->execute([':id' => $id]);

        if ($stmt->rowCount() > 0) {
            echo json_encode([
                "success" => true,
                "message" => "Transaksi berhasil dihapus"
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Transaksi tidak ditemukan"
            ]);
        }

    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            "success" => false,
            "message" => "Gagal menghapus transaksi: " . $e->getMessage()
        ]);
    }
}
?>
