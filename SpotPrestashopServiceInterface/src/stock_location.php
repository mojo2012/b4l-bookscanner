<?php
	require_once("config.php");
	require_once("db_connection.php");
	require_once("request_util.php");
	
	function getAllCategories() {
		$con = getConnection();
		
		$sql = "
			SELECT `id_warehouse`, `name` FROM `ps_warehouse` WHERE deleted = 0";
		
		$result = $con->query($sql);
		$categories = array();
		
		if (!$result) {
			throw new Exception("Database error: " . $con->error);
		} else {	
			while($row = $result->fetch_array(MYSQL_ASSOC)) {
				$categories[] = array_map('utf8_encode', $row);
			}
		}

		$con->close();
		
		return $categories;
	}
	
	
	function main() {
		try {
			if (!authenticateRequest()) {
				throw new Exception("Autenticated failed!");
			}
		
			$categories = getAllCategories();

			sendResponseOK($categories);
		} catch (Exception $ex) {
			//showHttpError(400, );
			sendResponseError($ex->getMessage());
			return;
		}
	}


	//start processing
	main();
?>