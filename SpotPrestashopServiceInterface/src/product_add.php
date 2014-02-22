<?php
	require_once("config.php");
	require_once("db_connection.php");
	require_once("request_util.php");

	function addBaseProduct($categoryId, $supplierId, $ean13, $amount, $price) {
		$con = getConnection();
		
		//insert base product
		$sql = 
			"INSERT INTO `ps_product` (
				`id_category_default`
				,`ean13`
				,`quantity`
				,`price`
				,`id_supplier`
				,`id_tax_rules_group`
				,`condition`
				,`active`
				,`visibility`
				,`date_add`
				,`date_upd`
				)
			VALUES (
				?
				,?
				,?
				,?
				,?
				,0
				,'used'
				,1
				,'both'
				,now()
				,now()
			)";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$pstat->bind_param("iisid", $categoryId, $ean13, $amount, $price, $supplierId);
		$pstat->execute();
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$productId = $con->insert_id;
		
		$pstat->close();
		$con->close();
		
		return $productId;
	}
	
	function addShopProduct($productId, $categoryId, $price) {
		$con = getConnection();
	
		//insert product mapping		
		$sql = 
			"INSERT INTO `ps_product_shop` (
				`id_product`
				,`id_category_default`
				,`price`
				,`id_shop`
				,`id_tax_rules_group`
				,`unity`
				,`condition`
				,`date_add`
				,`date_upd`
				)
			VALUES (
				?
				,?
				,?
				,1
				,0
				,''
				,'used'
				,now()
				,now()
			)";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$pstat->bind_param("iid", $productId, $categoryId, $price);
		$pstat->execute();
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}
	
	function addShopProductToCategory($productId, $categoryId) {
		$con = getConnection();
	
		$sql = 
			"INSERT INTO `ps_category_product` (
				`id_category`
				,`id_product`
				,`position`
			) VALUES (
				?
				,?
				,0
			);";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$pstat->bind_param("ii",  $categoryId, $productId);
		$pstat->execute();
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}

	function addProductStockLocations($productId, $stockLocationId) {
		$con = getConnection();
	
		//insert product mapping		
		$sql = 
			"INSERT INTO `ps_warehouse_product_location` (
				`id_product`
				,`id_warehouse`
				,`location`
				,`id_product_attribute`
				)
			VALUES (
				?
				,?
				,''
				,0
				)";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$pstat->bind_param("ii", $productId, $stockLocationId);
		$pstat->execute();
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}

	function addProductStockInfos($productId, $amount) {
		$con = getConnection();
	
		//insert product mapping		
		$sql = 
			"INSERT INTO `ps_stock_available` (
				`id_product`
				,`quantity`
				,`id_product_attribute`
				,`id_shop`
				,`id_shop_group`
			) VALUES (?,?,0,1,0);";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$pstat->bind_param("ii", $productId, $amount);
		$pstat->execute();
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}

	function addDescriptions($productId, $title, $descriptionLong, $descriptionShort) {
		$con = getConnection();
	
		$sql = "INSERT INTO `ps_product_lang` (
			`id_product`
			,`name`
			,`description`
			,`description_short`
			,`id_shop`
			,`id_lang`
			,`link_rewrite`
			)
		VALUES (
			?
			,?
			,?
			,?
			,1
			,3
			,'link'
		)";
		
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		$pstat->bind_param("isss", $productId, $title, $descriptionLong, $descriptionShort);
		$pstat->execute();
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}

	function prepareTags(&$tags) {
		// & is a pointer
		foreach($tags as &$tag) {
			$tag = str_replace("_", "-", $tag);
		}
	}

	function addMissingTags($tags) {
		$con = getConnection();
	
		$sql = "
			INSERT INTO `ps_tag` (`id_lang`, `name`) 
				SELECT 3, ? FROM DUAL
			WHERE NOT EXISTS
				(SELECT `name` from `ps_tag` WHERE `name` = ?)
			;";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		foreach($tags as $v) {
			$pstat->bind_param("ss", $v, $v);
			$pstat->execute();
		}
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}

	function addTags($productId, $tags) {
		$con = getConnection();
	
		$sql = "
			INSERT INTO `ps_product_tag`(`id_product`, `id_tag`) VALUES (?,
				(SELECT	`id_tag` FROM `ps_tag` WHERE `name` = ?)
			);";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		foreach($tags as $v) {
			$pstat->bind_param("ss", $productId, $v);
			$pstat->execute();
		}
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	}

	function addImage($productId, $encodedImage) {
		global $IMG_BASEPATH;
		
		$con = getConnection();
	
		$isCover = 1;
		$position = 1;
	
		foreach($encodedImage as $encodedImg) {
			$img = createImageOfBase64EncodedString($encodedImg);

			//insert image to db
			$sql = '
				INSERT INTO `ps_image` (
					`id_product`,
					`position`,
					`cover`
				) VALUES (?,?,?)';

			$pstat = $con->prepare($sql);
		
			if (!$pstat) {
				throw new Exception("Database error: " . $con->error);
			}
		
			$pstat->bind_param("iii", $productId, $position, $isCover);
			$pstat->execute();
		
			if ($con->error) {
				throw new Exception("Database error: " . $con->error);
			}

			//get insert id and create new image file path		
			$imgId = $con->insert_id;
			$imgIdPath = prepareImagePath($imgId);
		
			$pstat->close();


			//insert product - image connection to db
			$sql = '
				INSERT INTO `ps_image_shop` (
					`id_image`,
					`id_shop`,
					`cover`
				) VALUES (?,1,?)';

			$pstat = $con->prepare($sql);
		
			if (!$pstat) {
				throw new Exception("Database error: " . $con->error);
			}
		
			$pstat->bind_param("ii", $imgId, $isCover);
			$pstat->execute();

			if ($con->error) {
				throw new Exception("Database error: " . $con->error);
			}
			
			$pstat->close();
			$con->close();
			
			
			//update sql vars
			$isCover = 0;
			$position++;


			//save original image
			saveImageFile($IMG_BASEPATH . $imgIdPath, $imgId . ".jpg", $img, true);

			//save all image sizes!
			$imgResized = resizeImageWithPadding($img, array(255, 255, 255), 600, 600);
			saveImageFile($IMG_BASEPATH . $imgIdPath, $imgId . "-thickbox_default.jpg", $imgResized, true);
			imagedestroy($imgResized);

			$imgResized = resizeImageWithPadding($img, array(255, 255, 255), 45, 45);
			saveImageFile($IMG_BASEPATH . $imgIdPath, $imgId . "-small_default.jpg", $imgResized, true);
			imagedestroy($imgResized);

			$imgResized = resizeImageWithPadding($img, array(255, 255, 255), 58, 58);
			saveImageFile($IMG_BASEPATH . $imgIdPath, $imgId . "-medium_default.jpg", $imgResized, true);
			imagedestroy($imgResized);

			$imgResized = resizeImageWithPadding($img, array(255, 255, 255), 264, 264);
			saveImageFile($IMG_BASEPATH . $imgIdPath, $imgId . "-large_default.jpg", $imgResized, true);
			imagedestroy($imgResized);

			$imgResized = resizeImageWithPadding($img, array(255, 255, 255), 124, 124);
			saveImageFile($IMG_BASEPATH . $imgIdPath, $imgId . "-home_default.jpg", $imgResized, true);
			imagedestroy($imgResized);

			imagedestroy($img);
			
			addImageDescription($imgId, null);
 		}
	}
	
	function addImageDescription($imageId, $description) {
		$con = getConnection();
	
		$sql = "
			INSERT INTO `ps_image_lang` (
				`id_image`, 
				`id_lang`,
				`legend`
			) VALUES (
				?, ?, ?
			)";
		
		$pstat = $con->prepare($sql);
		
		if (!$pstat) {
			throw new Exception("Database error: " . $con->error);
		}
		
		for ($i = 1; $i <= 6; $i++) {
			$pstat->bind_param("iis", $imageId, $i, $description);
			$pstat->execute();
		}
		
		if ($con->error) {
			throw new Exception("Database error: " . $con->error);
		}

		$pstat->close();
		$con->close();
	
	}

	function prepareImagePath($imgId) {
		$tmp = str_split($imgId . "", 1);
		$ret = "";
		
		foreach ($tmp as $c) {
 			$ret = $ret . $c . "/";
 		}
		
		return $ret;
	}

	function addProduct($data) {
		$productId = -1;
	
		try {
			$title = $data["title"];
			$amount = $data["amount"];
			$shortDescription = $data["shortDescription"];
			$longDescription = $data["longDescription"];
			$price = $data["price"];
			$tags = $data["tags"];
			$categoryId = $data["categoryId"];
			$ean13 = $data["ean13"];
			$isOnline = $data["isOnline"];
			$status = $data["status"];
			$supplierId = $data["supplierId"];
			$vatRate = $data["vatRate"];
			$stockLocationId = $data["stockLocationId"];
			$images = $data["images"];
					
			if (!is_null($title) && !is_null($amount) && !is_null($shortDescription) && 
				!is_null($longDescription) && !is_null($price) && !is_null($tags) &&
				!is_null($categoryId) && !is_null($isOnline) && !is_null($status) &&
				!is_null($supplierId) && !is_null($vatRate) && !is_null($ean13) &&
				!is_null($stockLocationId)) {
				
				//$productId = 2222;
				
				$productId = addBaseProduct($categoryId, $supplierId, $ean13, $amount, $price);
				addShopProduct($productId, $categoryId, $price);
				addShopProductToCategory($productId, $categoryId);
				addProductStockInfos($productId, $amount);
				addProductStockLocations($productId, $stockLocationId);
				addDescriptions($productId, $title, $longDescription, $shortDescription);
				
				prepareTags($tags);
				addMissingTags($tags);
				addTags($productId, $tags);
				
				if (!is_null($images))
					addImage($productId, $images);
				
			} else {
				throw new Exception("Some attribute are not set.");
			}
			
		} catch (Exception $ex) {
			throw new Exception("Could not process JSON request. Cause: " . $ex->getMessage());
		}
		
		return $productId;
	}

	function main() {
		try {
			if (!authenticateRequest()) {
				throw new Exception("Autenticated failed!");
			}
		
			$data = getJsonPayload();

			$productId = addProduct($data);
		} catch (Exception $ex) {
			//showHttpError(400, );
			sendResponseError($ex->getMessage());
			return;
		}
		
		sendResponseOK(array('productId' => $productId));
	
		//echo json_encode(, JSON_PRETTY_PRINT);
	}


	//start processing
	main();
?>