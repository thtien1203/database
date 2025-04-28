<?php
include 'config.php';
session_start();
$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) && strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false;

if ($isApiRequest) {
    header('Content-Type: application/json');
}

// Logout logic
if (isset($_GET['logout'])) {
    session_unset(); // Remove all session variables
    session_destroy(); // Destroy the session
    header("Location: index.html"); // Redirect to the login page (index.html now)
    exit();
}

if ($isApiRequest && $_SERVER["REQUEST_METHOD"] == "GET" && !isset($_GET['section']) && !isset($_GET['course_id'])) {
    if (isset($_GET['email'])) {
        $email = $_GET['email'];
        
        // Get instructor ID from email
        $instructor_sql = "SELECT instructor_id FROM instructor WHERE email = ?";
        $instructor_stmt = $conn->prepare($instructor_sql);
        $instructor_stmt->bind_param("s", $email);
        $instructor_stmt->execute();
        $instructor_result = $instructor_stmt->get_result();
        
        if ($instructor_result->num_rows > 0) {
            $instructor_row = $instructor_result->fetch_assoc();
            $instructor_id = $instructor_row['instructor_id'];
            
            // Get sections taught by this instructor
            $sections = getInstructorSection($conn, $instructor_id);
            $sections_array = array();
            
            if ($sections && $sections->num_rows > 0) {
                while ($section = $sections->fetch_assoc()) {
                    $sections_array[] = array(
                        'courseId' => $section['course_id'],
                        'courseName' => $section['course_name'],
                        'sectionId' => $section['section_id'],
                        'semester' => $section['semester'],
                        'year' => (int)$section['year']
                    );
                }
            }
            
            echo json_encode($sections_array);
            exit();
        } else {
            //echo json_encode(array('success' => false, 'message' => 'Instructor not found'));
            echo json_encode([]);
            exit();
        }
    } else {
        //echo json_encode(array('success' => false, 'message' => 'Email parameter is required'));
        echo json_encode([]);
        exit();
    }
}

// API endpoint for getting students in a section
if ($isApiRequest && $_SERVER["REQUEST_METHOD"] == "GET" && isset($_GET['course_id']) && isset($_GET['section_id']) && isset($_GET['semester']) && isset($_GET['year'])) {
    $course_id = $_GET['course_id'];
    $section_id = $_GET['section_id'];
    $semester = $_GET['semester'];
    $year = intval($_GET['year']);
    
    $students = getStudentSection($conn, $course_id, $section_id, $semester, $year);
    $students_array = array();
    
    if ($students && $students->num_rows > 0) {
        while ($student = $students->fetch_assoc()) {
            $students_array[] = array(
                'studentId' => $student['student_id'],
                'name' => $student['name'],
                'currentGrade' => $student['midtermGrade'] ?? null
            );
        }
    }
    
    echo json_encode($students_array);
    exit();
}
// Handle login
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['email']) && isset($_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    // SQL query to check email, password, and type
    $sql = "SELECT * FROM account WHERE email = ? AND type = 'instructor'";

    if ($stmt = $conn->prepare($sql)) {
        $stmt->bind_param("s", $email); // Only bind the email
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            if ($password == $row['password']) {
                $_SESSION['email'] = $email;
                $_SESSION['role'] = 'instructor';

                // Get the instructor ID based on the email
                $instructor_sql = "SELECT instructor_id FROM instructor WHERE email = ?";
                if ($instructor_stmt = $conn->prepare($instructor_sql)) {
                    $instructor_stmt->bind_param("s", $email);
                    $instructor_stmt->execute();
                    $instructor_result = $instructor_stmt->get_result();

                    if ($instructor_result->num_rows > 0) {
                        $instructor_row = $instructor_result->fetch_assoc();
                        $_SESSION['instructor_id'] = $instructor_row['instructor_id'];
                    }
                }

                if ($isApiRequest) {
                    echo json_encode(array('success' => true, 'message' => 'Login successful'));
                    exit();
                } else {
                    // Redirect to the same page to show the dashboard after login
                    header('Location: instructor_grade.php');
                    exit();
                }
            } else {
                $login_error = "Invalid email or password.";
                if ($isApiRequest) {
                    echo json_encode(array('success' => false, 'message' => $login_error));
                    exit();
                }
            }
        } else {
            $login_error = "No such instructor found.";
            if ($isApiRequest) {
                echo json_encode(array('success' => false, 'message' => $login_error));
                exit();
            }
        }
    } else {
        $login_error = "Error: " . $conn->error;
        if ($isApiRequest) {
            echo json_encode(array('success' => false, 'message' => $login_error));
            exit();
        }

    }
}

// API endpoint for submitting grades
if ($isApiRequest && $_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['grades'])) {
    $course_id = $_POST['course_id'];
    $section_id = $_POST['section_id'];
    $semester = $_POST['semester'];
    $year = intval($_POST['year']);
    
    // Parse grades JSON
    $grades_json = $_POST['grades'];
    $student_grades = json_decode($grades_json, true);
    
    $success = true;
    $error_message = '';
    
    foreach ($student_grades as $student_id => $grade) {
        if (!empty($grade)) {
            // Check if the grade already exists for this student
            $check_sql = "SELECT * FROM midtermGrade 
            WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ? ";
            $check_stmt = $conn->prepare($check_sql);
            $check_stmt->bind_param('ssssi', $student_id, $course_id, $section_id, $semester, $year);
            $check_stmt->execute();
            $check_result = $check_stmt->get_result();

            if ($check_result->num_rows > 0) {
                // Update existing row
                $update_sql = "UPDATE midtermGrade SET midtermGrade = ?, submission_date = NOW()
                 WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
                $update_stmt = $conn->prepare($update_sql);
                $update_stmt->bind_param('sssssi', $grade, $student_id, $course_id, $section_id, $semester, $year);
                if (!$update_stmt->execute()) {
                    $success = false;
                    $error_message = 'Error updating grade: ' . $conn->error;
                    break;
                }
            } else {
                // Insert new grade
                $insert_sql = "INSERT INTO midtermGrade (student_id, course_id, section_id, semester, year, midtermGrade) VALUES (?, ?, ?, ?, ?, ?)";
                $insert_stmt = $conn->prepare($insert_sql);
                $insert_stmt->bind_param('ssssss', $student_id, $course_id, $section_id, $semester, $year, $grade);
                if (!$insert_stmt->execute()) {
                    $success = false;
                    $error_message = 'Error inserting grade: ' . $conn->error;
                    break;
                }
            }
            
            // Check if the grade is below C (C, C-, D+, D, D-, F)
            if (in_array($grade, ['C-', 'D+', 'D', 'D-', 'F'])) {
                $alert_message = "Your midterm grade in " . $course_id . " (" . $section_id . ") is " . $grade . ". Please consider seeking academic help to improve your performance. ";
                $alert_type = "Low Grade Alert";

                // Check if an alert already exists for this grade
                $check_alert_sql = "SELECT * FROM midtermAlerts WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
                $check_alert_stmt = $conn->prepare($check_alert_sql);
                $check_alert_stmt->bind_param('ssssi', $student_id, $course_id, $section_id, $semester, $year);
                $check_alert_stmt->execute();
                $check_alert_result = $check_alert_stmt->get_result();

                if ($check_alert_result->num_rows > 0) {
                    $update_alert_sql = "UPDATE midtermAlerts 
                    SET alert_message = ?, alert_date = NOW(), is_read = 0 
                    WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
                    $update_alert_stmt = $conn->prepare($update_alert_sql);
                    $update_alert_stmt->bind_param('sssssi', $alert_message, $student_id, $course_id, $section_id, $semester, $year);
                    if (!$update_alert_stmt->execute()) {
                        $success = false;
                        $error_message = 'Error updating alert: ' . $conn->error;
                        break;
                    }
                } else {
                    // Insert new alert
                    $insert_alert_sql = "INSERT INTO midtermAlerts (student_id, course_id, section_id, semester, year, alert_type, alert_message) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    $insert_alert_stmt = $conn->prepare($insert_alert_sql);
                    $insert_alert_stmt->bind_param("ssssiss", $student_id, $course_id, $section_id, $semester, $year, $alert_type, $alert_message);
                    if (!$insert_alert_stmt->execute()) {
                        $success = false;
                        $error_message = 'Error inserting alert: ' . $conn->error;
                        break;
                    }
                }
            }
        }
    }
    
    if ($success) {
        echo json_encode(array('success' => true, 'message' => 'Grades submitted successfully'));
    } else {
        echo json_encode(array('success' => false, 'message' => $error_message));
    }
    exit();
}

// handle grade submission
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['submit_grades'])) {
    $course_id = $_POST['course_id'];
    $section_id = $_POST['section_id'];
    $semester = $_POST['semester'];
    $year = $_POST['year'];
    $student_grades = $_POST['grade'];

    foreach ($student_grades as $student_id => $grade) {
        if (!empty($grade)) {
            // check if the grade already exist for this student
            $check_sql = "SELECT * FROM midtermGrade 
            WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ? ";
            $check_stmt = $conn->prepare($check_sql);
            $check_stmt->bind_param('ssssi', $student_id, $course_id, $section_id, $semester, $year);
            $check_stmt->execute();
            $check_result = $check_stmt->get_result();

            if ($check_result->num_rows > 0) {
                // update existing row
                $update_sql = "UPDATE midtermGrade SET midtermGrade = ?, submission_date = NOW()
                 WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
                $update_stmt = $conn->prepare($update_sql);
                $update_stmt->bind_param('sssssi', $grade, $student_id, $course_id, $section_id, $semester, $year);
                $update_stmt->execute();
            } else {
                // insert new grade
                $insert_sql = "INSERT INTO midtermGrade (student_id, course_id, section_id, semester, year, midtermGrade) VALUES (?, ?, ?, ?, ?, ?)";
                $insert_stmt = $conn->prepare($insert_sql);
                $insert_stmt->bind_param('ssssss', $student_id, $course_id, $section_id, $semester, $year, $grade);
                $insert_stmt->execute();
            }
            // Check if the grade is below C (C, C-, D+, D, D-, F)
            if (in_array($grade, ['C-', 'D+', 'D', 'D-', 'F'])) {
                $alert_message = "Your midterm grade in " . $course_id . " (" . $section_id . ") is " . $grade . ". Please consider seeking academic help to improve your performance. ";
                $alert_type = "Low Grade Alert";

                // check if an alert already exist for this grade
                $check_alert_sql = "SELECT * FROM midtermAlerts WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
                $check_alert_stmt = $conn->prepare($check_alert_sql);
                $check_alert_stmt->bind_param('ssssi', $student_id, $course_id, $section_id, $semester, $year);
                $check_alert_stmt->execute();
                $check_alert_result = $check_alert_stmt->get_result();

                if ($check_alert_result->num_rows > 0) {
                    $update_alert_sql = "UPDATE midtermAlerts 
                    SET alert_message = ?, alert_date = NOW(), is_read = 0 
                    WHERE student_id = ? AND course_id = ? AND section_id = ? AND semester = ? AND year = ?";
                    $update_alert_stmt = $conn->prepare($update_alert_sql);
                    $update_alert_stmt->bind_param('sssssi', $alert_message, $student_id, $course_id, $section_id, $semester, $year);
                    $update_alert_stmt->execute();
                } else {
                    // insert new alert
                    $insert_alert_sql = "INSERT INTO midtermAlerts (student_id, course_id, section_id, semester, year, alert_type, alert_message) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    $insert_alert_stmt = $conn->prepare($insert_alert_sql);
                    $insert_alert_stmt->bind_param("ssssiss", $student_id, $course_id, $section_id, $semester, $year, $alert_type, $alert_message);
                    $insert_alert_stmt->execute();
                }
            }
        }
    }
    $grade_success = "Grades submitted successfully!";
}

// get section taught by instructor
function getInstructorSection($conn, $instructor_id) {
    $section_sql = "SELECT s.*, c.course_name FROM section s
    JOIN course c ON s.course_id = c.course_id
     WHERE s.instructor_id = ?
     ORDER BY s.year DESC, s.semester, s.course_id, s.section_id";

    if ($section_stmt = $conn->prepare($section_sql)) {
        $section_stmt->bind_param("i", $instructor_id);
        $section_stmt->execute();
        return $section_stmt->get_result();
    }
    return false;
}

// get students in a section
function getStudentSection($conn, $course_id, $section_id, $semester, $year) {
    $student_sql = "SELECT s.student_id, s.name, m.midtermGrade
    FROM take t
    JOIN student s ON t.student_id = s.student_id
    LEFT JOIN midtermGrade m ON m.student_id = s.student_id
    AND m.course_id = t.course_id
    AND m.section_id = t.section_id
    AND m.semester= t.semester
    AND m.year = t.year
    WHERE t.course_id = ? AND t.section_id = ? AND t.semester = ? AND t.year = ?
    ORDER by s.name";

    if ($student_stmt = $conn->prepare($student_sql)) {
        $student_stmt->bind_param('sssi', $course_id, $section_id, $semester, $year);
        $student_stmt->execute();
        return $student_stmt->get_result();
    }
    return false;
}

// display section for midterm grade entry
function displaySection($conn, $instructor_id) {
    $sections = getInstructorSection($conn, $instructor_id);
    if ($sections && $sections->num_rows > 0) {
        echo "<h3>Your Course Sections:</h3>";
        echo "<ul>";
        while ($section = $sections->fetch_assoc()) {
            echo "<li>";
            echo "<strong>" . htmlspecialchars($section['course_id']) . " - " . htmlspecialchars($section['course_name']) . "</strong>";
            echo " (Section: " . htmlspecialchars($section['section_id']) . ", ";
            echo htmlspecialchars($section['semester']) . " " . htmlspecialchars($section['year']) . ")";
            echo " <a href='?section=" . $section['course_id'] . "&section_id=" . $section['section_id'] . "&semester=" . $section['semester'] . "&year=" . $section['year'] . "'>Submit Grades</a>";
            echo "</li>";
        }
        echo "</ul>";
    } else {
        echo "<p>No sections found for this instructor.</p>";
    }
}

// display grade submission form for a specific section
function displayGradeForm($conn, $course_id, $section_id, $semester, $year) {
    $students = getStudentSection($conn, $course_id, $section_id, $semester, $year);
    if ($students && $students->num_rows > 0) {
        echo "<h3>Submit Midterm Grades for " . htmlspecialchars($course_id) . " (Section: " . htmlspecialchars($section_id) . ", " . htmlspecialchars($semester) . " " . htmlspecialchars($year) . ")</h3>";
        echo "<form method='post'>";
        echo "<input type='hidden' name='course_id' value='" . htmlspecialchars($course_id) . "'>";
        echo "<input type='hidden' name='section_id' value='" . htmlspecialchars($section_id) . "'>";
        echo "<input type='hidden' name='semester' value='" . htmlspecialchars($semester) . "'>";
        echo "<input type='hidden' name='year' value='" . htmlspecialchars($year) . "'>";

        echo "<table border='1'>";
        echo "<tr><th>Student ID</th><th>Name</th><th>Current Grade</th><th><New Grade</th></tr>";
        while ($student = $students->fetch_assoc()) {
            echo "<tr>";
            echo "<td>" . htmlspecialchars($student['student_id']) . "</td>";
            echo "<td>" . htmlspecialchars($student['name']) . "</td>";
            echo "<td>" . (isset($student['midtermGrade']) ? htmlspecialchars($student['midtermGrade']) : "Not graded") . "</td>";
            echo "<td>";
            echo "<select name='grade[" . $student['student_id'] . "]'>";
            echo "<option value=''>Select Grade</option>";
            $grades = ['A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D+', 'D', 'D-', 'F'];
            foreach($grades as $grade) {
                $selected = ($student['midtermGrade'] == $grade) ? "selected" : "";
                echo "<option value='" . $grade . "' " . $selected . ">" . $grade . "</option>";
            }
            echo "</select>";
            echo "</td>";
            echo "</tr>";
        }
        echo "</table>";
        echo "<input type='submit' name='submit_grades' value='Submit Grades'>";
        echo "</form>";
        echo "<p><a href='instructor_grade.php'>Back to Sections</a></p>";
    } else {
        echo "<p>No students found in this section.</p>";
        echo "<p><a href='instructor_grade.php'>Back to Sections</a></p>";
    }
}


// Only display HTML response for browser requests
if (!$isApiRequest) {
    if (isset($_SESSION['email']) && $_SESSION['role'] == 'instructor') {
        $instructor_id = $_SESSION['instructor_id'];
        echo "<h2>Instructor Midterm Grade Submission</h2>";
        echo "<p>Welcome, " . $_SESSION['email'] . "!</p>";

        if (isset($grade_success)) {
            echo "<p><strong>" . $grade_success . "</strong></p>";
        }
        if (isset($_GET['section']) && isset($_GET['section_id']) && isset($_GET['semester']) && isset($_GET['year'])) {
            // display grade submission for selected section
            displayGradeForm($conn, $_GET['section'], $_GET['section_id'], $_GET['semester'], $_GET['year']);
        } else {
            // Display list of sections
            displaySection($conn, $instructor_id);
        }
        echo "<div>";
        echo "<p><a href='instructor_history.php'>View Course History</a> | <a href='instructor_grade.php?logout=true'>Logout</a></p>";
        echo "</div>";
    } else {
        // Display login form
        echo "<h2>Instructor Login</h2>";
            
        if (isset($login_error)) {
            echo "<p><strong>" . $login_error . "</strong></p>";
        }
        echo "<form method='post'>";
        echo "<table>";
        echo "<tr><td>Email:</td><td><input type='email' name='email' required></td></tr>";
        echo "<tr><td>Password:</td><td><input type='password' name='password' required></td></tr>";
        echo "<tr><td colspan='2'><input type='submit' value='Login'></td></tr>";
        echo "</table>";
        echo "</form>"; 
    }
}
?>