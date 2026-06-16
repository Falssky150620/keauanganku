<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once 'koneksi.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["success" => false, "message" => "Method not allowed"]);
    exit();
}

// Ambil data dari request body
$data = json_decode(file_get_contents("php://input"), true);

if (empty($data)) {
    $data = $_POST;
}

$email = isset($data['email']) ? trim($data['email']) : '';
$password = isset($data['password']) ? $data['password'] : '';

// Validasi input
if (empty($email) || empty($password)) {
    echo json_encode([
        "success" => false,
        "message" => "Email dan password wajib diisi"
    ]);
    exit();
}

try {
    // Cari user berdasarkan email
    $stmt = $conn->prepare("SELECT * FROM user WHERE email = :email");
    $stmt->execute([':email' => $email]);

    if ($stmt->rowCount() === 0) {
        echo json_encode([
            "success" => false,
            "message" => "Email tidak ditemukan"
        ]);
        exit();
    }

    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    // Verifikasi password
    if (!password_verify($password, $user['password'])) {
        echo json_encode([
            "success" => false,
            "message" => "Password salah"
        ]);
        exit();
    }

    // Login berhasil
    unset($user['password']); // Jangan kirim password ke client

    echo json_encode([
        "success" => true,
        "message" => "Login berhasil",
        "data" => $user
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Login gagal: " . $e->getMessage()
    ]);
}
?>
