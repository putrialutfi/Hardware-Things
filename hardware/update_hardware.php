<?php 

    header("Content-Type: application/json; charset=UTF-8");

    require_once 'connection.php';

    $id         = $_POST['id'];
    $nama       = $_POST['nama'];
    $harga      = $_POST['harga'];
    $gambar     = $_POST['gambar'];

    if ($gambar == null) {
        $query = "UPDATE hardwares SET nama='$nama', harga='$harga' WHERE id='$id' ";
        if (mysqli_query($conn, $query)) {
            success($conn);
        }
        else {
            failed($conn);
        }
    }
    else {
        $path = "picts/$id.jpeg";
        $paths = "/hardware/".$path;

        $query = "UPDATE hardwares SET nama='$nama', harga='$harga', gambar='$paths' WHERE id='$id' ";
        
        if (mysqli_query($conn, $query)) {
            if (file_put_contents($path, base64_decode($gambar))) {
                success($conn);
            }
            else {
                failed($conn);
            }
        }
        else {
            failed($conn);
        }
    }

    function success($conn){
        $result["resp_code"] = "1";
        $result["message"]   = "Success";
        echo json_encode($result);
        mysqli_close($conn);
    }
    function failed($conn){
        $response["resp_code"] = "0";
        $response["message"]   = "Failed : " . mysqli_error($conn);
        echo json_encode($response);
        mysqli_close($conn);
    }

?>