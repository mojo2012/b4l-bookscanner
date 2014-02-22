<?php

	require_once("util.php");

	function getJsonPayload() {
		//return http_get_request_body();

		$payload = file_get_contents('php://input');
		
		if ($payload == null)
			throw new Exception("No payload sent");
		
		$json = json_decode($payload, TRUE);
		
		if ($json == null)
			throw new Exception("Could not parse JSON data.");
		
		return $json;
	}
	
	function getProtocol() {
		return (isset($_SERVER['SERVER_PROTOCOL']) ? $_SERVER['SERVER_PROTOCOL'] : 'HTTP/1.0');
	}
	
	function showHttpError($code, $text) {
		header(getProtocol() . ' ' . $code . ' ' . $text);	
	}
	
	function sendResponse($code, $msg, $data) {
		$arr = array('message' => $msg,'code' => $code);
		
		if ($data) {
			if (isAssocArray($data)) {
				foreach ($data as $k => $v) {
					$arr[$k] = $v;
				}
			} else {
				$arr = $data;
			}
		}

		header(getProtocol() . ' ' . $code . ' ' . $msg);
		header('Content-Type: application/json');
	
		echo json_encode($arr);
	}
	
	function sendResponseOK($data) {
		return sendResponse(200, "Success", $data);
	}
	
	function sendResponseError($msg) {
		return sendResponse(400, $msg, null);
	}
?>