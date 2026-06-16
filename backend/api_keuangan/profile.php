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
    $stmt = $conn->prepare("SELECT id_user, nama, email, created_at FROM user WHERE id_user = :id_user");
    $stmt->execute([':id_user' => $id_user]);

    if ($stmt->rowCount() === 0) {
        echo json_encode([
            "success" => false,
            "message" => "User tidak ditemukan"
        ]);
        exit();
    }

    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    echo json_encode([
        "success" => true,
        "message" => "Data profil berhasil diambil",
        "data" => $user
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Gagal mengambil profil: " . $e->getMessage()
    ]);
}
?>
