<?php 
	require_once 'connection.php';

	$nama 	= $_POST['nama'];
	$harga 	= $_POST['harga'];
	$gambar = $_POST['gambar'];

	$getid = mysqli_fetch_assoc(mysqli_query($conn, "SELECT * FROM hardwares ORDER BY id DESC LIMIT 0, 1"));
	$id = $getid['id']+1;

	if($nama != null) {
		if ($gambar == null) {
			$gambar = "/hardware/picts/chip.png";

			$query = "INSERT INTO hardwares (id, nama, harga, gambar) VALUES ('$id', '$nama', '$harga', '$gambar')";

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

			$query = "INSERT INTO hardwares (id, nama, harga, gambar) VALUES ('$id', '$nama', '$harga', '$paths')";
			
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
	}

	function success($conn){
		$result["resp_code"] = "1";
		$result["message"]	 = "Success";
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