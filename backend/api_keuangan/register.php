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

// Jika data dikirim via form-encoded
if (empty($data)) {
    $data = $_POST;
}

$nama = isset($data['nama']) ? trim($data['nama']) : '';
$email = isset($data['email']) ? trim($data['email']) : '';
$password = isset($data['password']) ? $data['password'] : '';

// Validasi input
if (empty($nama) || empty($email) || empty($password)) {
    echo json_encode([
        "success" => false,
        "message" => "Nama, email, dan password wajib diisi"
    ]);
    exit();
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode([
        "success" => false,
        "message" => "Format email tidak valid"
    ]);
    exit();
}

if (strlen($password) < 6) {
    echo json_encode([
        "success" => false,
        "message" => "Password minimal 6 karakter"
    ]);
    exit();
}

try {
    // Cek apakah email sudah terdaftar
    $stmt = $conn->prepare("SELECT id_user FROM user WHERE email = :email");
    $stmt->execute([':email' => $email]);

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "success" => false,
            "message" => "Email sudah terdaftar"
        ]);
        exit();
    }

    // Hash password
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    // Insert user baru
    $stmt = $conn->prepare("INSERT INTO user (nama, email, password) VALUES (:nama, :email, :password)");
    $stmt->execute([
        ':nama' => $nama,
        ':email' => $email,
        ':password' => $hashed_password
    ]);

    echo json_encode([
        "success" => true,
        "message" => "Registrasi berhasil"
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Registrasi gagal: " . $e->getMessage()
    ]);
}
?>
