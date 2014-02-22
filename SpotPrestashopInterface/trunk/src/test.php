 <?

$host		= "mysqlsvr30.world4you.com";
	$db			= "b4l_wienatdb4";
	$user		= "b4l_wienat";
	$password	= "42uyjqk";
        
        
        
        //$mysqli_connection = new MySQLi('mysqlsvr30.world4you.com', 'b4l_wienat', '42uyjqk', 'b4l_wienatdb4');
        $mysqli_connection = new MySQLi($host, $user, $password, $db);
		if ($mysqli_connection->connect_error) {
			echo "Not connected, error: " . $mysqli_connection->connect_error;
		}
		else {
			echo "Connected.";
		}
		
		$showtablequery="SHOW TABLES FROM dbname";
        $query_result=mysqli_query($mysqli_connection, $showtablequery);
        
        $row = $query_result->fetch_array(MYSQLI_NUM);
        

			echo $row;

?>

