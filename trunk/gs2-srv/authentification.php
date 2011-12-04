<?php

include('../../includes/config.inc.php');

$username = $_POST['u'];
$password = $_POST['p'];

if ($username and $password) {
	$connect_result = @ldap_connect('10.32.0.4 10.0.10.14 10.0.10.16 10.0.10.12 10.0.10.10 10.0.10.11'); // WWU LDAP Servers
	$check = @ldap_bind($connect_result, $username.'@uni-muenster.de', $password);
	
	if ($check) {
		$bind_result = @ldap_bind($connect_result, $ldapuser.'@uni-muenster.de', $ldappasswd);
		$search_result = @ldap_search($connect_result, 'ou=Projekt-Benutzer,dc=uni-muenster,dc=de', 'cn='.$username);
		$info = @ldap_get_entries($connect_result, $search_result);
		$numrows = $info["count"];
		if ($numrows == 0) {
			$error = 'STATUS_NOT_VALID';
		} else {
			for ($rownum = 0; $rownum < $numrows; $rownum++) {
		
				$un = $info[$rownum]["cn"][0];
				$ln = $info[$rownum]["sn"][0];
				$fn = $info[$rownum]["givenname"][0];
				$ml = $info[$rownum]["mail"][0];
				$id = $info[$rownum]["uidnumber"][0];
				
				mysql_connect($dbhost, $dblogin, $dbpasswd);
				mysql_select_db($dbname_csa); 
				
				$sql = "SELECT * FROM users WHERE cn = '". $un ."'";
				$req = mysql_query($sql) or die('Error SQL !<br>'.$sql.'<br>'.mysql_error());
				if (mysql_num_rows($req) == 0) {
					// create user if not exists
					$sql = "REPLACE INTO users SET cn='".$un."', sn='".$ln."', givenname='".$fn."', mail='".$ml."', uidnumber='".$id."'";
					$req = mysql_query($sql) or die('Error SQL !<br>'.$sql.'<br>'.mysql_error());
				}
				// log auth
				$sql = "INSERT INTO logs VALUES (NULL, '".$id."', '".$_SERVER['REMOTE_ADDR']."', NOW())";
				$req = mysql_query($sql) or die('Error SQL !<br>'.$sql.'<br>'.mysql_error());
				// get status if exists
				$sql = "SELECT * FROM status WHERE uidnumber = '".$id."'";
				$req = mysql_query($sql) or die('Error SQL !<br>'.$sql.'<br>'.mysql_error());
				$data = mysql_fetch_array($req);
				if (!$st) 
					$st = 5;
				else
					$st = $data['status'];
				
				$user_data = array();
				$user_data['username'] = $un;
				$user_data['first_name'] = $fn;
				$user_data['last_name'] = $ln;
				$user_data['email'] = $ml;
				$user_data['uid'] = $id;
				$user_data['st'] = $st;
			}
		}
	} else {
		$error = 'STATUS_NOT_VALID';	
	}
	ldap_close($connect_result);
	$json = array();
	$json['status'] = $error ? $error : 'STATUS_OK';
	$json['data'] = array();
	if ($user_data)
		$json['data'] = $user_data;
	echo json_encode($json);
}
/*
echo '<form action="" method="post">
<input type="text" name="u" /><br />
<input type="password" name="p" /><br />
<input type="submit" /><br />
</form>';
*/
?>