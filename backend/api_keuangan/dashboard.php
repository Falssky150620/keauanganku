<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");

require_once 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(["success" => false, "message" => "Method not allowed"]);
    exit();
}

$id_user = isset($_GET['id_user']) ? intval($_GET['id_user']) : 0;

if ($id_user <= 0) {
    echo json_encode([
        "success" => false,
        "message" => "ID user tidak valid"
    ]);
    exit();
}

try {
    // Hitung total pemasukan
    $stmt = $conn->prepare("SELECT COALESCE(SUM(nominal), 0) as total FROM transaksi WHERE id_user = :id_user AND jenis = 'pemasukan'");
    $stmt->execute([':id_user' => $id_user]);
    $total_pemasukan = floatval($stmt->fetch(PDO::FETCH_ASSOC)['total']);

    // Hitung total pengeluaran
    $stmt = $conn->prepare("SELECT COALESCE(SUM(nominal), 0) as total FROM transaksi WHERE id_user = :id_user AND jenis = 'pengeluaran'");
    $stmt->execute([':id_user' => $id_user]);
    $total_pengeluaran = floatval($stmt->fetch(PDO::FETCH_ASSOC)['total']);

    // Hitung saldo
    $saldo = $total_pemasukan - $total_pengeluaran;

    echo json_encode([
        "success" => true,
        "message" => "Data dashboard berhasil diambil",
        "total_pemasukan" => $total_pemasukan,
        "total_pengeluaran" => $total_pengeluaran,
        "saldo" => $saldo
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal mengambil data dashboard: " . $e->getMessage()
    ]);
}
?>
