
<?php
include 'config.php';

function createForm() {
    global $conn;
    $dept_query = "SELECT dept_name FROM department";
    $dept_result = mysqli_query($conn, $dept_query);

    echo "<h2>Create Student Account</h2>";
    echo "<form method='post' action='".$_SERVER['PHP_SELF']."'>";
    echo "<table>";
    echo "<tr><td>Student ID:</td><td><input type='text' name='student_id' required></td></tr>";
    echo "<tr><td>Name:</td><td><input type='text' name='name'required></td></tr>";
    echo "<tr><td>Email:</td><td><input type='email' name='email' required></td></tr>";
    echo "<tr><td>Password:</td><td><input type='password' name='password' required></td></tr>";
    echo "<tr><td>Department:</td><td><select name='dept_name' required>";

    while($row = mysqli_fetch_array($dept_result, MYSQLI_ASSOC)) {
        echo "<option value='" . $row['dept_name'] . "'>" . $row['dept_name'] . "</option>";
    }

    echo "</select></td></tr>";          // close dropdown department above
    echo "<tr><td>Student Type:</td><td>";
    echo "<select name='student_type' required>";
    echo "<option value=''>Select Type</option>";
    echo "<option value='undergraduate'>Undergraduate</option>";
    echo "<option value='master'>Master</option>";
    echo "<option value='phd'>PhD</option>";
    echo "</select></td></tr>";          // close select option for student type
    echo "<tr><td colspan='2'><input type='submit' name='submit' value='Create Account'></td></tr>";
    echo "</table>";
    echo "</form>";    
}

// check if it's an API request from Android app
$isApiRequest = isset($_SERVER['HTTP_USER_AGENT']) &&
                (strpos($_SERVER['HTTP_USER_AGENT'], 'okhttp') !== false ||
                strpos($_SERVER['CONTENT_TYPE'], 'application/json') !== false);

// if it's an API request set json 
if ($isApiRequest) {
    header('Content-Type: application/json');
}

// add process for submission
if ($_SERVER["REQUEST_METHOD"]=="POST") {
    $student_id = isset($_POST["student_id"]) ? $_POST["student_id"] : ' ';
    $name = isset($_POST["name"]) ? $_POST["name"] : ' ';
    $email = isset($_POST["email"]) ? $_POST["email"] : ' ';
    $password = isset($_POST["password"]) ? $_POST["password"] : ' ';
    $dept_name = isset($_POST["dept_name"]) ? $_POST["dept_name"] : ' ';
    $student_type = isset($_POST["student_type"]) ? $_POST["student_type"] : ' ';
    $submit = isset($_POST["submit"]) ? $_POST["submit"] : ' ';

    // validate input
    if (empty($student_id) || empty($name) || empty($email) || empty($password) || empty($dept_name) || empty($student_type)) {
        if ($isApiRequest) {
            echo json_encode(array('success' => false, 'message' => 'All fields are required'));
            exit;
        } else {
            echo "<p>All fields are required</p>";
            createForm();
            exit;
        }
    }
    // insert into account
    $account_sql = "INSERT INTO account (email, password, type) VALUES ('$email', '$password', 'student')";
    if (mysqli_query($conn, $account_sql)) {
        // next insert into student table
        $student_sql = "INSERT INTO student (student_id, name, email, dept_name) VALUES ('$student_id', '$name', '$email', '$dept_name')";
        if (mysqli_query($conn, $student_sql)) {
            // insert into the appropriate student type
            if ($student_type == 'undergraduate') {
                $type_sql = "INSERT INTO undergraduate (student_id, total_credits, class_standing) VALUES ('$student_id', 0, 'Freshman')";
            } else if ($student_type == 'master') {
                $type_sql = "INSERT INTO master (student_id, total_credits) VALUES ('$student_id', 0)";
            } else {
                $current_date = date('Y-m-d');
                $type_sql = "INSERT INTO PhD (student_id, qualifier, proposal_defence_date, dissertation_defence_date) VALUES ('$student_id', NULL, '$current_date', NULL)"; 
            }

            if (mysqli_query($conn, $type_sql)) {
                if ($isApiRequest) {
                    echo json_encode(array('success' => true, 'message' => 'Student account created successfully'));
                } else {
                    echo "<p>Student account created successfully</p>";
                }
            } else {
                if ($isApiRequest) {
                    echo json_encode(array('success' => false, 'message' => 'Error creating student record: ' . mysqli_error($conn)));
                } else {
                    echo "<p>Error creating student type record: " . mysqli_error($conn) . "</p>";
                }
            }
        } else {
            if ($isApiRequest) {
                echo json_encode(array('success' => false, 'message' => 'Error creating account: ' . mysqli_error($conn)));
            } else {
                echo "<p>Error creating student record: " . mysqli_error($conn) . "</p>";
            }
        }
    } else {
        echo "<p>Error creating account: " . mysqli_error($conn) . "</p>";
    }
} else {
    createForm();
}
mysqli_close($conn);
?>

