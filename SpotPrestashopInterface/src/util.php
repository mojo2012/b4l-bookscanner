<?php

	function isAssocArray(array $array) {
		// Keys of the array
		$keys = array_keys($array);

		// If the array keys of the keys match the keys, then the array must
		// not be associative (e.g. the keys array looked like {0:0, 1:1...}).
		return array_keys($keys) !== $keys;
	}

	function saveImageFile($path, $fileName, $image, $createPath) {
		if (!file_exists($path)) {
// 			throw new Exception("Could not create image file path. PATH = " . $path);
			mkdir($path, 0775, $createPath);
		}

		if (!imagepng($image, $path . $fileName)) {
			throw new Exception("Could not save image file");
		}
	}
	
	function createImageOfBase64EncodedString($data) {
		$data = base64_decode($data);
		$data = imagecreatefromstring($data);
		return $data;
	}
	
	function resizeImageWithPadding($data, $color, $width, $height) {
		$img = imagecreatetruecolor($width, $height);
		$backgroundColor = imagecolorallocate($img, 255, $color[1], $color[2]);
		imagefill($img, 0, 0, $backgroundColor);
		
		$origWidth = imagesx($data);
		$origHeight = imagesy($data);
		
		$xRatio = $width / $origWidth;
	    $yRatio = $height / $origHeight;
		
		if (($origWidth <= $width) && ($origHeight <= $height)) {
			$newWidth = $origWidth;
			$newHeight = $origHeight;
		} elseif (($xRatio * $origHeight) < $height) {
			$newHeight = ceil($xRatio * $origHeight);
			$newWidth = $width;
		} else {
			$newWidth = ceil($yRatio * $origWidth);
			$newHeight = $height;
		}
	
		//resize image	
		$newImage = imagecreatetruecolor(round($newWidth), round($newHeight));
		imagecopyresampled($newImage, $data, 0, 0, 0, 0, $newWidth, $newHeight, $origWidth, $origHeight);
		
		//imagecopy($img, $data, (($width - $origWidth)/ 2), (($height - $origHeight) / 2), 0, 0, $origWidth, $origHeight);
		//imagecopyresized($img, $newImage, (($width - $origWidth)/ 2), (($height - $origHeight) / 2), 0, 0, $width, $height, $origWidth, $origHeight);
		imagecopy($img, $newImage, (($width - $newWidth)/ 2), (($height - $newHeight) / 2), 0, 0, $newWidth, $newHeight);

		imagedestroy($newImage);
		
		return $img;
	}
	
?>