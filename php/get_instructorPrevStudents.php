<?php
include 'config.php';
session_start();

$email = isset($_GET["email"]) ? $_GET["email"] : ' ';

if (empty($email)) {
    echo json_encode(array('success' => false, 'message' => 'email feild is required'));
    exit;
}

    $sql = "SELECT
    s.section_id,
    s.course_id,
    s.semester,
    s.year,
    stu.name AS student_name,
    t.grade AS grade
    FROM section s
    JOIN instructor i ON s.instructor_id = i.instructor_id
    JOIN take t ON s.course_id = t.course_id
            AND s.section_id = t.section_id
            AND s.semester = t.semester
            AND s.year = t.year
    JOIN student stu ON t.student_id = stu.student_id
    WHERE i.instructor_id IN ( SELECT instructor_id FROM instructor WHERE email = '$email')
    AND s.year != 2025  -- Exclude all semesters in 2025
    ORDER BY s.course_id, s.section_id, s.semester, s.year, stu.name";

$result = mysqli_query($conn, $sql);

$users = array();

while($row = $result->fetch_assoc()) {
    $users[] = $row;
}

header('Content-Type: application/json');
echo json_encode($users);


mysqli_close($conn);
?>

