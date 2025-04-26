<?php
include 'config.php';

function createForm() {
    echo "<h2>Get Available Courses</h2>";
    echo "<form method='post' action='".$_SERVER['PHP_SELF']."'>";
    echo "<table>";
    echo "<tr><td>Semester:</td><td><input type='text' name='semester' required></td></tr>";
    echo "<tr><td>Year:</td><td><input type='text' name='year' required></td></tr>";
    echo "<tr><td><input type='hidden' name='submit' value='true'></td></tr>";
    echo "<tr><td colspan='2'><input type='submit' value='Get Available Courses'></td></tr>";
    echo "</table>";
    echo "</form>";
}

// check if it's an API request
$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) &&
                (strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false ||
                 (isset($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false));

// if it's an API request, set JSON header
if ($isApiRequest) {
    header('Content-Type: application/json');
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $semester = trim($_POST["semester"]);
    $year = trim($_POST["year"]);

    // validate input
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

    // SQL query with dynamic semester and year using prepared statement
    $sql = "SELECT 
                s.course_id, 
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
        $error = "Query preparation failed: " . $conn->error;        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $error]);
        } else {
            echo "<p>$error</p>";
            createForm();
        }
        exit;
    }

    $stmt->bind_param("si", $semester, $year);  // 's' for string, 'i' for integer
    $stmt->execute();
    $result = $stmt->get_result();

    $courses = [];
    while ($row = $result->fetch_assoc()) {
        $courses[] = $row;
    }

    if ($isApiRequest) {
        echo json_encode(['success' => true, 'courses' => $courses]);
    } else {
        echo "<h3>Available Courses:</h3>";
        if (count($courses) === 0) {
            echo "<p>No courses found for $semester $year.</p>";
        } else {
            echo "<table border='1' cellpadding='5' cellspacing='0'>";
            echo "<tr><th>Course ID</th><th>Section</th><th>Instructor</th><th>Building</th><th>Room</th><th>Day</th><th>Time</th><th>Capacity</th><th>Enrolled</th></tr>";
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
                echo "</tr>";
            }
            echo "</table>";
        }
    }

    $stmt->close();
} else {
    createForm();
}

$conn->close();
?>
