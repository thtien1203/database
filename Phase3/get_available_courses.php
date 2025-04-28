<?php
include 'config.php';


$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) &&
                (strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false ||
                 (isset($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false));

if ($isApiRequest) {
    header('Content-Type: application/json');
}

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    $semester = isset($_GET['semester']) ? trim($_GET['semester']) : null;
    $year = isset($_GET['year']) ? trim($_GET['year']) : null;

    if (empty($semester) || empty($year)) {
        $error = 'Semester and Year are required';
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $error]);
        } else {
            echo "<p>$error</p>";
            createForm();
        }
        exit;
    }

    $sql = "SELECT 
                s.course_id,
                co.course_name,
                co.credits,
                s.section_id, 
                s.semester, 
                s.year, 
                i.instructor_name, 
                c.building, 
                c.room_number, 
                c.capacity, 
                t.day, 
                t.start_time, 
                t.end_time, 
                COUNT(st.student_id) AS students_enrolled
            FROM section s
            JOIN instructor i ON s.instructor_id = i.instructor_id
            JOIN classroom c ON c.classroom_id = s.classroom_id 
            JOIN time_slot t ON t.time_slot_id = s.time_slot_id
            JOIN course co ON co.course_id = s.course_id
            LEFT JOIN take st ON s.course_id = st.course_id 
                              AND s.section_id = st.section_id 
                              AND s.semester = st.semester 
                              AND s.year = st.year
            WHERE s.semester = ? AND s.year = ?
            GROUP BY s.course_id, s.section_id, s.semester, s.year, i.instructor_name, 
                     c.building, c.room_number, c.capacity, t.day, t.start_time, t.end_time
            ORDER BY s.year DESC, s.semester, s.course_id, s.section_id";

    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        $error = "Query preparation failed: " . $conn->error;
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $error]);
        } else {
            echo "<p>$error</p>";
            createForm();
        }
        exit;
    }

    $stmt->bind_param("si", $semester, $year); 
    $stmt->execute();
    $result = $stmt->get_result();

    $courses = [];
    while ($row = $result->fetch_assoc()) {
        $course = [
            'course_id' => $row['course_id'],
            'course_name' => $row['course_name'],
            'semester' => $row['semester'],
            'year' => $row['year'],
            'section_id' => $row['section_id'],
            'instructor_name' => $row['instructor_name'],
            'building' => $row['building'],
            'room_number' => $row['room_number'],
            'day' => $row['day'],
            'start_time' => $row['start_time'],
            'end_time' => $row['end_time'],
            'capacity' => $row['capacity'],
            'students_enrolled' => $row['students_enrolled'],
            'credits' => $row['credits']
        ];

        $courses[] = $course;
    }

    if ($isApiRequest) {
        echo json_encode(['success' => true, 'courses' => $courses]);
    } else {
        echo "<h3>Available Courses:</h3>";
        if (count($courses) === 0) {
            echo "<p>No courses found for $semester $year.</p>";
        } else {
            echo "<table border='1' cellpadding='5' cellspacing='0'>";
            echo "<tr><th>Course ID</th><th>Section</th><th>Instructor</th><th>Building</th><th>Room</th><th>Day</th><th>Time</th><th>Capacity</th><th>Enrolled</th><th>Credits</th></tr>";
            foreach ($courses as $course) {
                echo "<tr>";
                echo "<td>{$course['course_id']}</td>";
                echo "<td>{$course['section_id']}</td>";
                echo "<td>{$course['instructor_name']}</td>";
                echo "<td>{$course['building']}</td>";
                echo "<td>{$course['room_number']}</td>";
                echo "<td>{$course['day']}</td>";
                echo "<td>{$course['start_time']} - {$course['end_time']}</td>";
                echo "<td>{$course['capacity']}</td>";
                echo "<td>{$course['students_enrolled']}</td>";
                echo "<td>{$course['credits']}</td>";
                echo "</tr>";
            }
            echo "</table>";
        }
    }

    $stmt->close();
} else {
    echo "<h2>Get Available Courses</h2>";
    echo "<p>Use the URL with query parameters <code>?semester=Spring&year=2025</code></p>";
}

$conn->close();
?>
