<?php

	$host		= "mysqlsvr30.world4you.com";
	$db			= "b4l_wienatdb4";
	$user		= "b4l_wienat";
	$password	= "42uyjqk";



	function executeSqQL($query) {
		$link = mysql_connect($host, $user, $password) or die("Keine Verbindung möglich: " . mysql_error());
	
		mysql_select_db($db) or die("Auswahl der Datenbank fehlgeschlagen");

		$result = mysql_query($query) or die("Anfrage fehlgeschlagen: " . mysql_error());
	
		return $result;
	}

	function getConnection() {
		global $host;		
		global $db;
		global $user;
		global $password;

		$con = new MySQLi($host, $user, $password, $db);
	
		if ($con->connect_error) {
			die('Connect Error: ' . $mysqli->connect_errno);
			throw new Exception("Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error);
		}
		
		return $con;
	}
?>