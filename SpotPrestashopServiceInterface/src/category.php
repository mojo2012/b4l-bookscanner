<?php
	require_once("config.php");
	require_once("db_connection.php");
	require_once("request_util.php");
	
	function getAllCategories() {
		$con = getConnection();
		
		$sql = "
			SELECT DISTINCT
				c.`id_category` as id, `name` as name, `is_root_category` as isRootCategory, `id_parent` as parentId, 
				`active` as isActive, `position`, `date_add` as dateAdded, `date_upd`as dateUpdated
			FROM `ps_category` c
			JOIN `ps_category_lang` l ON l.id_category = c.id_category
			WHERE l.id_lang = 3";
		
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