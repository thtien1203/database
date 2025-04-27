<?php
include 'config.php';

// Check if it's an API request
$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) &&
                (strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false ||
                 (isset($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false));

// If it's an API request, set JSON header
if ($isApiRequest) {
    header('Content-Type: application/json');
}

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    // Get email, semester, and year from query parameters
    $email = isset($_GET['email']) ? trim($_GET['email']) : null;
    $semester = isset($_GET['semester']) ? trim($_GET['semester']) : null;
    $year = isset($_GET['year']) ? trim($_GET['year']) : null;

    // Validate input
    if (empty($email) || empty($semester) || empty($year)) {
        $error = 'Email, Semester, and Year are required';
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $error]);
        } else {
            echo "<p>$error</p>";
            createForm();
        }
        exit;
    }

    // Step 1: Get student_id from email
    $stmt = $conn->prepare("SELECT student_id FROM student WHERE email = ?");
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
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 0) {
        $message = "Student not found.";
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $message]);
        } else {
            echo "<p>$message</p>";
            createForm();
        }
        exit;
    }

    $row = $result->fetch_assoc();
    $studentId = $row['student_id'];
    $stmt->close();

    // Step 2: Query for academic history using student_id
    $sql = "SELECT 
                st.student_id,
                st.grade,
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
                (
                    SELECT COUNT(*) 
                    FROM take st2
                    WHERE 
                        st2.course_id = s.course_id 
                        AND st2.section_id = s.section_id 
                        AND st2.semester = s.semester 
                        AND st2.year = s.year
                ) AS students_enrolled
            FROM section s
            JOIN instructor i ON s.instructor_id = i.instructor_id
            JOIN classroom c ON c.classroom_id = s.classroom_id 
            JOIN time_slot t ON t.time_slot_id = s.time_slot_id
            JOIN course co ON co.course_id = s.course_id
            LEFT JOIN take st ON s.course_id = st.course_id 
                            AND s.section_id = st.section_id 
                            AND s.semester = st.semester 
                            AND s.year = st.year
            WHERE st.student_id = ?
            AND s.semester = ?
            AND s.year = ?
            AND st.grade IS NOT NULL
            ORDER BY s.year DESC, s.semester, s.course_id, s.section_id;";

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

    // Bind parameters properly
    $stmt->bind_param("isi", $studentId, $semester, $year);
    $stmt->execute();
    $result = $stmt->get_result();

    $gradesList = [];
    $total_credits = 0;
    $total_grade_points = 0;

    // Define grade points mapping
    $grade_points = [
        'A+' => 4.0,
        'A' => 4.0,
        'A-' => 3.7,
        'B+' => 3.3,
        'B' => 3.0,
        'B-' => 2.7,
        'C+' => 2.3,
        'C' => 2.0,
        'C-' => 1.7,
        'D+' => 1.3,
        'D' => 1.0,
        'D-' => 0.7,
        'F' => 0.0
    ];

    while ($row = $result->fetch_assoc()) {
        $grade = $row['grade'];
        $credits = $row['credits'];
        
        // Calculate grade points for each course
        $grade_point = isset($grade_points[$grade]) ? $grade_points[$grade] : 0.0;
        
        // Add credits and grade points to totals
        $total_credits += $credits;
        $total_grade_points += $grade_point * $credits;

        // Add course details to grades list
        $gradesList[] = [
            'student_id' => $row['student_id'],
            'course' => [
                'course_id' => $row['course_id'],
                'course_name' => $row['course_name'],
                'semester' => $row['semester'],
                'year' => (int)$row['year'],
                'section_id' => $row['section_id'],
                'instructor_name' => $row['instructor_name'],
                'building' => $row['building'],
                'room_number' => $row['room_number'],
                'day' => $row['day'],
                'start_time' => $row['start_time'],
                'end_time' => $row['end_time'],
                'capacity' => (int)$row['capacity'],
                'students_enrolled' => (int)$row['students_enrolled'],
                'credits' => (int)$row['credits'],
            ],
            'current_grade' => $row['grade'],
        ];
    }

    // Calculate GPA
    $gpa = $total_credits > 0 ? $total_grade_points / $total_credits : 0.0;

    // Prepare response data
    $response = [
        'success' => true,
        'grades' => $gradesList,
        'total_credits' => $total_credits,
        'gpa' => number_format($gpa, 2)
    ];

    if ($isApiRequest) {
        echo json_encode($response);
    } else {
        // Display HTML response
        echo "<h3>Academic History:</h3>";
        if (empty($gradesList)) {
            echo "<p>No courses found for student $email in $semester $year.</p>";
        } else {
            echo "<table border='1' cellpadding='5' cellspacing='0'>";
            echo "<tr><th>Student ID</th><th>Course ID</th><th>Course Name</th><th>Section</th><th>Instructor</th><th>Grade</th><th>Credits</th></tr>";
            foreach ($gradesList as $grade) {
                echo "<tr>";
                echo "<td>{$grade['student_id']}</td>";
                echo "<td>{$grade['course']['course_id']}</td>";
                echo "<td>{$grade['course']['course_name']}</td>";
                echo "<td>{$grade['course']['section_id']}</td>";
                echo "<td>{$grade['course']['instructor_name']}</td>";
                echo "<td>{$grade['current_grade']}</td>";
                echo "<td>{$grade['course']['credits']}</td>";
                echo "</tr>";
            }
            echo "</table>";
            echo "<h4>Total Credits: $total_credits</h4>";
            echo "<h4>GPA: " . number_format($gpa, 2) . "</h4>";
        }
    }

    $stmt->close();
} else {
    // Display the form for GET requests without parameters
    echo "<h2>Get Academic History</h2>";
    echo "<form method='GET'>";
    echo "<label for='email'>Email:</label><br>";
    echo "<input type='email' id='email' name='email' required><br><br>";

    echo "<label for='semester'>Semester:</label><br>";
    echo "<input type='text' id='semester' name='semester' required><br><br>";

    echo "<label for='year'>Year:</label><br>";
    echo "<input type='number' id='year' name='year' required><br><br>";

    echo "<input type='submit' value='Get Academic History'>";
    echo "</form>";
}

$conn->close();
?>
