<?php
    $conn = mysqli_connect('localhost', 'root', '', 'db2')
        or die ('Could not connect: ' . mysql_error($conn));

    $mydb = mysqli_select_db($conn , 'db2')
        or die ('Could not select database');
?>