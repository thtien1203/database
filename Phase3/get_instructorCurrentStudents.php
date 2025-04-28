<?php
include 'config.php';
session_start();


$email = isset($_GET["email"]) ? $_GET["email"] : ' ';

if (empty($email)) {
    echo json_encode(array('success' => false, 'message' => 'email field is required'));
    exit;
}

$sql = "SELECT
        s.course_id,
        s.section_id,
        stu.name AS student_name
        FROM section s
        JOIN instructor i ON s.instructor_id = i.instructor_id
        JOIN take t ON s.course_id = t.course_id
                    AND s.section_id = t.section_id
        JOIN student stu ON t.student_id = stu.student_id
        WHERE i.instructor_id IN ( SELECT instructor_id FROM instructor WHERE email = '$email')
        AND s.semester = 'Spring'
        AND s.year = 2025
        AND t.semester = 'Spring'
        AND t.year = 2025";

$result = mysqli_query($conn, $sql);

$users = array();

while($row = $result->fetch_assoc()) {
    $users[] = $row;
}

header('Content-Type: application/json');
echo json_encode($users);


mysqli_close($conn);
?>

