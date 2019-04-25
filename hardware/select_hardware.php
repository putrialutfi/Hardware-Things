<?php 

header("Content-Type: application/json; charset=UTF-8");

require_once 'connection.php';

$query       = "SELECT * FROM hardwares ORDER BY id DESC ";
$result      = mysqli_query($conn, $query);
$response    = array();
$server_name = $_SERVER['SERVER_ADDR'];

while ($row = mysqli_fetch_assoc($result)){

    array_push($response, 
    array(
        'id'     =>$row['id'], 
        'nama'   =>$row['nama'], 
        'harga'  =>$row['harga'],
        'gambar' =>"http://$server_name" . $row['gambar']
    ));
}
echo json_encode($response);
mysqli_close($conn);

?>