<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require "config.php";

$email = $_POST["email"] ?? "";
$new_password = $_POST["new_password"] ?? "";

// Validate
if (empty($email) || empty($new_password)) {
    echo json_encode(["status" => "error", "message" => "missing_fields"]);
    exit;
}

// Mã hóa mật khẩu mới (MD5 theo yêu cầu của bạn)
$hashedPassword = md5($new_password);

// 1. Kiểm tra email có tồn tại không
$check = $conn->prepare("SELECT id FROM users WHERE email=?");
$check->bind_param("s", $email);
$check->execute();
$check->store_result();

if ($check->num_rows == 0) {
    echo json_encode(["status" => "not_exist"]);
    exit;
}

// 2. Update mật khẩu mới
$update = $conn->prepare("UPDATE users SET password=? WHERE email=?");
$update->bind_param("ss", $hashedPassword, $email);

if ($update->execute()) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "failed"]);
}
?>
