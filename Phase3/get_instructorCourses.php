<?php
include 'config.php';
session_start();


$email = isset($_GET["email"]) ? $_GET["email"] : ' ';

if (empty($email)) {
    echo json_encode(array('success' => false, 'message' => 'email field is required'));
    exit;
}
$sql = "SELECT course_id, section_id, semester, year FROM section s
JOIN instructor i ON s.instructor_id = i.instructor_id
WHERE i.instructor_id IN ( SELECT instructor_id FROM instructor WHERE email = '$email')";

$result = mysqli_query($conn, $sql);

$users = array();

while($row = $result->fetch_assoc()) {
    $users[] = $row;
}

header('Content-Type: application/json');
echo json_encode($users);


mysqli_close($conn);
?>