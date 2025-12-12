<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require "config.php";

$id_token = $_POST['id_token'] ?? "";

// 1. Verify token with Google
$google_verify_url = "https://oauth2.googleapis.com/tokeninfo?id_token=" . $id_token;
$response = file_get_contents($google_verify_url);
$payload = json_decode($response, true);

$WEB_CLIENT_ID = "505676312200-592v2395lrgtq0qijtuvhritajnf7mdi.apps.googleusercontent.com"; // THAY BẰNG WEB CLIENT ID

if (!isset($payload['aud']) || $payload['aud'] !== $WEB_CLIENT_ID) {
    echo json_encode(["status" => "invalid_token"]);
    exit;
}

// 2. Extract user info from Google
$google_id = $payload["sub"];
$full_name = $payload["name"] ?? "";
$email     = $payload["email"] ?? "";
$avatar    = $payload["picture"] ?? "";

// 3. Check if user exists
$check = $conn->prepare("SELECT * FROM users WHERE email=?");
$check->bind_param("s", $email);
$check->execute();
$result = $check->get_result();

if ($result->num_rows > 0) {
    // User exists → login
    $user = $result->fetch_assoc();
    echo json_encode(["status" => "success", "user" => $user]);
    exit;
}

// 4. Create new Google user
$stmt = $conn->prepare("
    INSERT INTO users(full_name, email, phone, password, google_id, avatar)
    VALUES (?, ?, '', '', ?, ?)
");
$stmt->bind_param("ssss", $full_name, $email, $google_id, $avatar);
$stmt->execute();

// Get created user
$id = $stmt->insert_id;
$user = $conn->query("SELECT * FROM users WHERE id=$id")->fetch_assoc();

echo json_encode(["status" => "success", "user" => $user]);
?>
