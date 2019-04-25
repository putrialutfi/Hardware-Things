<?php 

    header("Content-Type: application/json; charset=UTF-8");

    require_once 'connection.php';

    $id      = $_POST['id'];
    $gambar  = $_POST['gambar'];
    $split   = explode("/", $gambar);
    $gambars = $split[5];

    $query = "DELETE FROM hardwares WHERE id='$id' ";

        if (mysqli_query($conn, $query)){
            if (unlink("picts/".$gambars)){
                success($conn);
            } else {
                failed($conn);
            }
            success($conn);
        } 
        else {
           failed($conn);
        }
            
    function success($conn){
        $result["resp_code"] = "1";
        $result["message"] = "Success!";
        echo json_encode($result);
    }
    function failed($conn){
         $response["resp_code"] = "0";
        $response["message"] = "Error! ".mysqli_error($conn);
        echo json_encode($response);
    }
?>