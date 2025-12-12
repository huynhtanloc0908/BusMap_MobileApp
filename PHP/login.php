<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require "config.php";

$email    = $_POST["email"] ?? "";
$password = md5($_POST["password"] ?? "");

// Check login
$stmt = $conn->prepare("SELECT id, full_name, email, phone, avatar FROM users WHERE email=? AND password=?");
$stmt->bind_param("ss", $email, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 1) {

    $user = $result->fetch_assoc();

    echo json_encode([
        "status" => "success",
        "user" => $user
    ]);

} else {
    echo json_encode(["status" => "failed"]);
}
?>
