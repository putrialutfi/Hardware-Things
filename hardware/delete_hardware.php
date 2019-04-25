<?php 

    header("Content-Type: application/json; charset=UTF-8");

    require_once 'connection.php';

    $id      = $_POST['id'];
    $gambar  = $_POST['gambar'];

    $query = "DELETE FROM hardwares WHERE id='$id' ";

        if (mysqli_query($conn, $query)){

            $split = explode("/", $gambar);
            $gambars = $split[5];

            if (unlink("picts/".$gambars)){
                $result["resp_code"] = "1";
                $result["message"] = "Success!";
                echo json_encode($result);
                mysqli_close($conn);
            } else {
                $response["resp_code"] = "0";
                $response["message"] = "Error to delete a image! ".mysqli_error($conn);
                echo json_encode($response);
                mysqli_close($conn);
            }

        } 
        else {
            $response["resp_code"] = "0";
            $response["message"] = "Error! ".mysqli_error($conn);
            echo json_encode($response);
            mysqli_close($conn);
        }

?>