<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

include 'config.php';

// Function to create the login form
function createForm() {
    echo '
    <h2>Login</h2>
    <form method="POST" action="login.php">
        <label for="email">Email:</label><br>
        <input type="email" id="email" name="email" required><br><br>

        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password" required><br><br>

        <label for="role">Role:</label><br>
        <select id="role" name="role" required>
            <option value="student">Student</option>
            <option value="instructor">Instructor</option>
        </select><br><br>

        <input type="submit" name="submit" value="Login">
    </form>
    ';
}

function handleLogin($email, $password, $role) {
    global $conn;

    // Sanitize the input
    $email = mysqli_real_escape_string($conn, $email);
    $password = mysqli_real_escape_string($conn, $password);
    $role = mysqli_real_escape_string($conn, $role);

    // Check if the user exists in the database
    if ($role == 'student') {
        $sql = "SELECT * FROM account WHERE email = '$email' AND password = '$password' AND type = 'student'";
    } else if ($role == 'instructor') {
        $sql = "SELECT * FROM account WHERE email = '$email' AND password = '$password' AND type = 'instructor'";
    } else {
        echo json_encode(array('success' => false, 'message' => 'Invalid role'));
        exit;
    }

    $result = mysqli_query($conn, $sql);

    // Check if query was successful
    if (!$result) {
        echo json_encode(array('success' => false, 'message' => 'Error executing query: ' . mysqli_error($conn)));
        exit;
    }

    if (mysqli_num_rows($result) > 0) {
        // Successful login
        echo json_encode(array('success' => true, 'message' => 'Login successful'));
    } else {
        // Failed login
        echo json_encode(array('success' => false, 'message' => 'Invalid email or password'));
    }
}

// Handle POST requests (API request for login)
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Capture the incoming POST data
    $email = isset($_POST["email"]) ? $_POST["email"] : '';
    $password = isset($_POST["password"]) ? $_POST["password"] : '';
    $role = isset($_POST["role"]) ? $_POST["role"] : '';
    $submit = isset($_POST["submit"]) ? $_POST["submit"] : '';

    if (empty($email) || empty($password) || empty($role)) {
        echo json_encode(array('success' => false, 'message' => 'All fields are required'));
        exit;
    }

    handleLogin($email, $password, $role);
} else {
    // If it's not a POST request, show the form
    createForm();
}

mysqli_close($conn);
?>
