<?php
session_start();
include 'config.php'; // Include database connection


$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) && strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false;

if ($isApiRequest) {
    header('Content-Type: application/json');
}
// Logout logic
if (isset($_GET['logout'])) {
    session_unset(); // Remove all session variables
    session_destroy(); // Destroy the session
    header("Location: index.html"); // Redirect to the login page
    exit();
}

// Handle login
if ($_SERVER["REQUEST_METHOD"] == "POST" && !isset($_POST['mark_read'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    $sql = "SELECT * FROM account WHERE email = ? AND password = ? AND type = 'student'";
    if ($stmt = $conn->prepare($sql)) {
        $stmt->bind_param("ss", $email, $password);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $_SESSION['email'] = $email;
            $_SESSION['role'] = 'student';

            if ($isApiRequest) {
                echo json_encode(array('success' => true, 'message' => 'Login successful'));
                exit();
            } else {
                header("Location: student_alerts.php");
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
        $login_error = "Error: " . $conn->error;
        if ($isApiRequest) {
            echo json_encode(array('success' => false, 'message' => $login_error));
            exit();
        }
    }
}

// Handle marking alerts as read
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['mark_read'])) {
    $alert_ids = $_POST['alert_ids'];
    
    if (isset($_POST['alert_ids'])) {
        $alert_ids_json = json_decode($_POST['alert_ids'], true);
        $alert_ids = is_array($alert_ids_json) ? $alert_ids_json : $_POST['alert_ids'];
    } else {
        //$alert_ids = isset($_POST['alert_ids']) ? $_POST['alert_ids'] : array();
        $alert_ids = array();
    }

    // Get email from the request
    $email = isset($_POST['email']) ? $_POST['email'] : '';
    if (!empty($alert_ids) && !empty($email)) {
        // Get student ID from email
        $student_id = getStudentID($conn, $email);
        
        if ($student_id) {
            // Convert alert IDs to a comma-separated list for SQL
            $ids_str = implode(',', array_map('intval', $alert_ids));
            $update_sql = "UPDATE midtermAlerts SET is_read = 1 WHERE alert_id IN ($ids_str) AND student_id = '$student_id'";
            
            if ($conn->query($update_sql)) {
                // For API requests, return JSON
                if (isset($_SERVER['HTTP_USER_AGENT']) && strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false) {
                    echo json_encode(array('success' => true, 'message' => 'Alerts marked as read.'));
                } else {
                    $read_success = "Alerts marked as read.";
                }
            } else {
                if (isset($_SERVER['HTTP_USER_AGENT']) && strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false) {
                    echo json_encode(array('success' => false, 'message' => 'Error marking alerts as read: ' . $conn->error));
                } else {
                    $read_error = "Error marking alerts as read: " . $conn->error;
                }
            }
        } else {
            if (isset($_SERVER['HTTP_USER_AGENT']) && strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false) {
                echo json_encode(array('success' => false, 'message' => 'Student ID not found.'));
            } else {
                $read_error = "Student ID not found.";
            }
        }
    } else {
        if (isset($_SERVER['HTTP_USER_AGENT']) && strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false) {
            echo json_encode(array('success' => false, 'message' => 'No alerts selected or email not provided.'));
        } else {
            $read_error = "No alerts selected or email not provided.";
        }
    }
}

// get student id from email
function getStudentID($conn, $email) {
    $sql = "SELECT student_id FROM student WHERE email = ?";
    $sql_stmt = $conn->prepare($sql);
    $sql_stmt->bind_param("s", $email);
    $sql_stmt->execute();
    $result = $sql_stmt->get_result();

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        return $row['student_id'];
    }

    return null;
}

// get alert for a student
function getStudentAlerts($conn, $student_id) {
    $sql = "SELECT a.*, c.course_name
            FROM midtermAlerts a
            JOIN course c ON a.course_id = c.course_id
            WHERE a.student_id = ?
            ORDER BY a.alert_date DESC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $student_id);
    $stmt->execute();
    return $stmt->get_result();
}

// get midterm grade for a student
function getMidtermGrade($conn, $student_id) {
    $sql = "SELECT m.*, c.course_name
            FROM midtermGrade m
            JOIN course c ON m.course_id = c.course_id
            WHERE m.student_id = ?
            ORDER BY m.year DESC, m.semester, m.course_id";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $student_id);
    $stmt->execute();
    return $stmt->get_result();
}

// API GET endpoint for retrieving alerts and grades
if ($isApiRequest && $_SERVER["REQUEST_METHOD"] == "GET") {
    // Get email from query parameter
    $email = isset($_GET['email']) ? $_GET['email'] : '';

    if (!empty($email)) {
        $student_id = getStudentID($conn, $email);
        if ($student_id) {
            // check if alerts or grades were requested
            if (isset($_GET['type']) && $_GET['type'] == 'grades') {
                $grades = getMidtermGrade($conn, $student_id);
                $grades_array = array();

                if ($grades && $grades->num_rows > 0) {
                    while ($grade = $grades->fetch_assoc()) {
                        $grades_array[] = array(
                            'studentId' => $grade['student_id'],
                            'courseId' => $grade['course_id'],
                            'courseName' => $grade['course_name'],
                            'sectionId' => $grade['section_id'],
                            'semester' => $grade['semester'],
                            'year' => (int) $grade['year'],
                            'midtermGrade' => $grade['midtermGrade']
                        );
                    }
                }
                echo json_encode($grades_array);
                exit();
            } else {
                // return alerts as json
                $alerts = getStudentAlerts($conn, $student_id);
                $alerts_array = array();

                if ($alerts && $alerts->num_rows > 0) {
                    while ($alert = $alerts->fetch_assoc()) {
                        $alerts_array[] = array(
                            'alertId' => (int)$alert['alert_id'],
                            'studentId' => $alert['student_id'],
                            'courseId' => $alert['course_id'],
                            'courseName' => $alert['course_name'],
                            'sectionId' => $alert['section_id'],
                            'semester' => $alert['semester'],
                            'year' => (int)$alert['year'],
                            'alertType' => $alert['alert_type'],
                            'alertMessage' => $alert['alert_message'],
                            'alertDate' => $alert['alert_date'],
                            'isRead' => (bool)$alert['is_read']
                        );
                    }
                }
                echo json_encode($alerts_array);
                exit();
            }
        } else {
            echo json_encode(array('success' => false, 'message' => 'Student ID not found'));
            exit();
        }
    } else {
        //echo json_encode(array('success' => false, 'message' => 'Email parameter is required'));
        echo json_encode([]); // Return empty array if email is missing
        exit();
    }
}

if (!$isApiRequest) {
    if (isset($_SESSION['email']) && $_SESSION['role'] == 'student') {
        $student_id = getStudentID($conn, $_SESSION['email']);
        if ($student_id) {
            echo "<h2>Welcome Academic Alerts</h2>";
            echo "<p>Welcome, " . $_SESSION['email'] . "!</p>";
            if (isset($read_success)) {
                echo "<p>" . $read_success . "</p>";
            } 
            if (isset($read_error)) {
                echo "<p>" . $read_error . "</p>";
            }

            // display midterm grade
            $grades = getMidtermGrade($conn, $student_id);

            if ($grades && $grades->num_rows > 0) {
                echo "<h3>Your Midterm Grade</h3>";
                echo "<table border='1'>";
                echo "<tr><th>Course</th><th>Section</th><th>Semester</th><th>Year</th><th>Grade</th></tr>";

                while ($grade = $grades->fetch_assoc()) {
                    echo "<tr>";
                    echo "<td>" . htmlspecialchars($grade['course_id']) . " - " . htmlspecialchars($grade['course_name']) . "</td>";
                    echo "<td>" . htmlspecialchars($grade['section_id']) . "</td>";
                    echo "<td>" . htmlspecialchars($grade['semester']) . "</td>";
                    echo "<td>" . htmlspecialchars($grade['year']) . "</td>";

                    if (in_array($grade['midtermGrade'], ['C', 'C-', 'D+', 'D', 'D-', 'F'])) {
                        echo "<td><strong>" . htmlspecialchars($grade['midtermGrade']) . "</strong></td>";
                    } else {
                        echo "<td>" . htmlspecialchars($grade['midtermGrade']) . "</td>";
                    }
                    echo "</tr>";
                }
                echo "</table>";
            } else {
                echo "<p>No midterm grades available.</p>";
            }

            // display alerts
            $alerts = getStudentAlerts($conn, $student_id);
            if ($alerts && $alerts->num_rows > 0) {
                echo "<h3>Your Academic Alerts</h3>";
                echo "<form method='post'>";
                echo "<table border='1'>";
                echo "<tr><th>Date</th><th>Course</th><th>Section</th><th>Semester</th><th>Year</th><th>Alert Type</th><th>Message</th><th>Status</th><th>Mark as Read</th></tr>";

                $hasUnreadAlert = false;
                while ($alert = $alerts->fetch_assoc()) {
                    echo "<tr>";
                    // add a prefix or marker for unread alerts
                    if (!$alert['is_read']) {
                        echo "<td><strong>NEW! " . htmlspecialchars(date('Y-m-d H:i', strtotime($alert['alert_date']))) . "</strong></td>";
                        echo "<td><strong>" . htmlspecialchars($alert['course_id']) . " - " . htmlspecialchars($alert['course_name']) . "</strong></td>";
                        echo "<td><strong>" . htmlspecialchars($alert['section_id']) . "</strong></td>";
                        echo "<td><strong>" . htmlspecialchars($alert['semester']) . "</strong></td>";
                        echo "<td><strong>" . htmlspecialchars($alert['year']) . "</strong></td>";
                        echo "<td><strong>" . htmlspecialchars($alert['alert_type']) . "</strong></td>";
                        echo "<td><strong>" . htmlspecialchars($alert['alert_message']) . "</strong></td>";
                        echo "<td><strong>Unread</strong></td>";
                        echo "<td><input type='checkbox' name='alert_ids[]' value='" . $alert['alert_id'] . "'></td>";
                        $hasUnreadAlert = true;
                    } else {
                        echo "<td>" . htmlspecialchars(date('Y-m-d H:i', strtotime($alert['alert_date']))) . "</td>";
                        echo "<td>" . htmlspecialchars($alert['course_id']) . " - " . htmlspecialchars($alert['course_name']) . "</td>";
                        echo "<td>" . htmlspecialchars($alert['section_id']) . "</td>";
                        echo "<td>" . htmlspecialchars($alert['semester']) . "</td>";
                        echo "<td>" . htmlspecialchars($alert['year']) . "</td>";
                        echo "<td>" . htmlspecialchars($alert['alert_type']) . "</td>";
                        echo "<td>" . htmlspecialchars($alert['alert_message']) . "</td>";
                        echo "<td>Read</td>";
                        echo "<td></td>";
                    }
                    echo "</tr>";
                }
                echo "</table>";

                if ($hasUnreadAlert) {
                    echo "<input type='submit' name='mark_read' value='Mark Selected as Read'>";
                }
                echo "</form>";
            } else {
                echo "<p>No alerts available.</p>";
            }

            echo "<p><a href='student_alerts.php?logout=true'>Logout</a></p>";
            echo "<p><a href='student_register.php'>Go to Course Registration</a></p>";
        } else {
            echo "<p>Student ID not found. Please contact support.</p>";
        }
    } else {
        // display login form
        echo "<h2>Student Login</h2>";
        if (isset($login_error)) {
            echo "<p>" . $login_error . "</p>";
        }

        echo "<form method='post'>";
        echo "<table>";
        echo "<tr><td>Email:</td><td><input type='email' name='email' required></td></tr>";
        echo "<tr><td>Password:</td><td><input type='password' name='password' required></td></tr>";
        echo "<tr><td colspan='2'><input type='submit' value='Login'></td></tr>";
        echo "</table>";
        echo "</form>";
        
        echo "<p>Don't have an account? <a href='student.php'>Register here</a></p>";
    }
}

?>