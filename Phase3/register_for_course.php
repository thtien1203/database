<?php
include 'config.php'; 

function createForm() {
    echo "<h2>Student Course Registration</h2>";
    echo "<form method='post' action='" . $_SERVER['PHP_SELF'] . "'>";
    echo "<table>";
    echo "<tr><td>Email:</td><td><input type='email' name='email' required></td></tr>";
    echo "<tr><td>Course ID:</td><td><input type='text' name='course_id' required></td></tr>";
    echo "<tr><td>Section ID:</td><td><input type='text' name='section_id' required></td></tr>";
    echo "<tr><td>Semester:</td><td><input type='text' name='semester' required></td></tr>";
    echo "<tr><td>Year:</td><td><input type='number' name='year' required></td></tr>";
    echo "<tr><td colspan='2'><input type='submit' name='register' value='Register'></td></tr>";
    echo "</table>";
    echo "</form>";
}

$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) &&
                (strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false ||
                 (isset($_SERVER['CONTENT_TYPE']) && strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false));

if ($isApiRequest) {
    header('Content-Type: application/json');
}

if ($_SERVER["REQUEST_METHOD"] === "POST" && (isset($_POST['register']) || $isApiRequest)) {
    $email      = trim($_POST['email']);
    $course_id  = trim($_POST['course_id']);
    $section_id = trim($_POST['section_id']);
    $semester   = trim($_POST['semester']);
    $year       = trim($_POST['year']);

    if (!$email || !$course_id || !$section_id || !$semester || !$year) {
        $message = "All fields are required.";
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $message]);
        } else {
            echo "<p>$message</p>";
            createForm();
        }
        exit;
    }

    $stmt = $conn->prepare("SELECT student_id FROM student WHERE email = ?");
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

    $student_id = $result->fetch_assoc()['student_id'];
    $stmt->close();

    $stmt_check_registration = $conn->prepare("SELECT * FROM take WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?");
    $stmt_check_registration->bind_param("issss", $student_id, $course_id, $section_id, $semester, $year);
    $stmt_check_registration->execute();
    $result_check_registration = $stmt_check_registration->get_result();

    if ($result_check_registration->num_rows > 0) {
        $message = "You are already registered for $course_id - $section_id.";
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $message]);
        } else {
            echo "<p>$message</p>";
            createForm();
        }
        exit;
    }

    $stmt_prereq_check = $conn->prepare("
        SELECT p.prereq_id 
        FROM prereq p
        WHERE p.course_id = ? 
        AND NOT EXISTS (
            SELECT 1
            FROM take t
            WHERE t.student_id = ? 
            AND t.course_id = p.prereq_id AND t.grade IN ('A','B','C')
        )
    ");
    $stmt_prereq_check->bind_param("si", $course_id, $student_id);
    $stmt_prereq_check->execute();
    $result_prereq = $stmt_prereq_check->get_result();

    if ($result_prereq->num_rows > 0) {
        $message = "Prerequisite not met for $course_id. Registration failed.";
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $message]);
        } else {
            echo "<p>$message</p>";
            createForm();
        }
        exit;
    }

    $stmt_check_capacity = $conn->prepare("
        SELECT c.capacity, COUNT(st.student_id) AS students_enrolled
        FROM section s
        JOIN classroom c ON c.classroom_id = s.classroom_id
        LEFT JOIN take st ON s.course_id = st.course_id 
                           AND s.section_id = st.section_id 
                           AND s.semester = st.semester 
                           AND s.year = st.year
        WHERE s.course_id = ? 
        AND s.section_id = ? 
        AND s.semester = ? 
        AND s.year = ?
        GROUP BY s.course_id, s.section_id, s.semester, s.year, c.capacity
    ");
    $stmt_check_capacity->bind_param("ssss", $course_id, $section_id, $semester, $year);
    $stmt_check_capacity->execute();
    $result_capacity = $stmt_check_capacity->get_result();

    if ($result_capacity->num_rows > 0) {
        $row = $result_capacity->fetch_assoc();
        $capacity = $row['capacity'];
        $students_enrolled = $row['students_enrolled'];

        if ($students_enrolled >= $capacity) {
            $message = "Section is too full. Cannot register for $course_id - $section_id.";
            if ($isApiRequest) {
                echo json_encode(['success' => false, 'message' => $message]);
            } else {
                echo "<p>$message</p>";
                createForm();
            }
            exit;
        } else {
            $stmt_register = $conn->prepare("INSERT INTO take (student_id, course_id, section_id, semester, year) VALUES (?, ?, ?, ?, ?)");
            $stmt_register->bind_param("isssi", $student_id, $course_id, $section_id, $semester, $year);
            $success = $stmt_register->execute();

            if ($success) {
                $message = "Successfully registered for $course_id - $section_id!";
                if ($isApiRequest) {
                    echo json_encode(['success' => true, 'message' => $message]);
                } else {
                    echo "<p>$message</p>";
                }
            } else {
                $message = "Error registering for course.";
                if ($isApiRequest) {
                    echo json_encode(['success' => false, 'message' => $message]);
                } else {
                    echo "<p>$message</p>";
                    createForm();
                }
            }
        }
    } else {
        $message = "Error checking section capacity.";
        if ($isApiRequest) {
            echo json_encode(['success' => false, 'message' => $message]);
        } else {
            echo "<p>$message</p>";
            createForm();
        }
    }
} else {
    createForm();
}

$conn->close();
?>
