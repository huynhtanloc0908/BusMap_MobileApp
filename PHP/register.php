<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require "config.php";

$full_name = $_POST["full_name"] ?? "";
$email     = $_POST["email"] ?? "";
$phone     = $_POST["phone"] ?? "";
$password  = md5($_POST["password"] ?? ""); // MD5 !!!

// Kiểm tra trùng email
$check = $conn->prepare("SELECT id FROM users WHERE email=?");
$check->bind_param("s", $email);
$check->execute();
$check->store_result();

if ($check->num_rows > 0) {
    echo json_encode(["status" => "exist"]);
    exit;
}

// Insert
$stmt = $conn->prepare("
    INSERT INTO users(full_name, email, phone, password)
    VALUES (?, ?, ?, ?)
");
$stmt->bind_param("ssss", $full_name, $email, $phone, $password);

if ($stmt->execute()) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "failed"]);
}
?>
