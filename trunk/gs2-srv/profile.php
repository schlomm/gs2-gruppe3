<?php
	/*
	echo '<form action="" method="post">
	<input type="text" name="uid" /><br />
	<input type="text" name="matriculation_number" /><br />
	<input type="text" name="course_of_studies" /><br />
	<input type="text" name="semester" /><br />
	<input type="submit" />
	</form>';
	 */
	if (!is_numeric($_POST['uid']) and !is_numeric($_GET['uid']))
		die();
	
	include '../../includes/config.inc.php';
	mysql_connect($dbhost, $dblogin, $dbpasswd);
	mysql_select_db($dbname_csa);
	
	$sql = "SET NAMES 'utf8'";
	mysql_query($sql);
	
	if (is_numeric($_POST['uid'])) {
		$uid = mysql_real_escape_string($_POST['uid']);
		if (!is_numeric($_POST['matriculation_number']) and !is_numeric($_POST['course_of_studies']) and
			!is_numeric($_POST['semester']))
			die();
		$sql = "UPDATE users SET ";
		$items = array();
		if (is_numeric($_POST['matriculation_number']))
			$items[] = "matriculation_number = ". mysql_real_escape_string($_POST['matriculation_number']);
		if (is_numeric($_POST['course_of_studies']))
			$items[] = "course_of_studies = ". mysql_real_escape_string($_POST['course_of_studies']);
		if (is_numeric($_POST['semester']))
			$items[] = "semester = ". mysql_real_escape_string($_POST['semester']);
			$sql .= implode(', ', $items);
		$sql .= " WHERE uidnumber = ". $uid;
		mysql_query($sql);
	}
	
	if (!$uid)
		$uid = mysql_real_escape_string($_GET['uid']);
	$sql = "SELECT * FROM users WHERE uidnumber = ". $uid;
	$result = mysql_query($sql) or die(mysql_error());
	if (mysql_num_rows($result) == 0) {
		$sql = "SELECT * FROM users_external WHERE uidnumber = ". $uid;
		$result = mysql_query($sql) or die(mysql_error());
		if (mysql_num_rows($result) == 0)
			die();
	}
	$data = mysql_fetch_assoc($result);
	if ($data['pw'])
		unset($data['pw']);
	$sql = "SELECT status FROM status WHERE uidnumber = ". $data['uidnumber'];
	$subresult = mysql_query($sql) or die(mysql_error());
	$subdata = mysql_fetch_assoc($subresult);
	$data['st'] = $subdata['status'];
	
	if (is_numeric($_GET['uid'])) {
		$data['courses'] = array();		
		$course_ids = array();
		$sql = "SELECT * FROM entries WHERE entry_student = ". $data['uidnumber'] .
			" AND entry_status = 1";
		$subresult = mysql_query($sql) or die(mysql_error());
		while ($subdata = mysql_fetch_assoc($subresult)) {
			$course_ids[] = $subdata['entry_course'];
		}
		if ($course_ids) {
			$sql = "SELECT * FROM courses WHERE course_status = 1 AND 
				id IN (". implode(', ', $course_ids) .") ORDER BY course_term, course_title";
			$subresult = mysql_query($sql);
			while ($subdata = mysql_fetch_assoc($subresult)) {
				$data['courses'][] = array("id" => $subdata['id'], "course_term" => $subdata['course_term']);
			}
		}
	}
	echo json_encode($data);
	/*
	echo '<pre>';
	echo indent(json_encode($data));
	echo '</pre>';
	*/
	
	/**
	 * Indents a flat JSON string to make it more human-readable.
	 *
	 * @param string $json The original JSON string to process.
	 *
	 * @return string Indented version of the original JSON string.
	 */
	function indent($json) {
		
		$result      = '';
		$pos         = 0;
		$strLen      = strlen($json);
		$indentStr   = '  ';
		$newLine     = "\n";
		$prevChar    = '';
		$outOfQuotes = true;
		
		for ($i=0; $i<=$strLen; $i++) {
			
			// Grab the next character in the string.
			$char = substr($json, $i, 1);
			
			// Are we inside a quoted string?
			if ($char == '"' && $prevChar != '\\') {
				$outOfQuotes = !$outOfQuotes;
				
				// If this character is the end of an element, 
				// output a new line and indent the next line.
			} else if(($char == '}' || $char == ']') && $outOfQuotes) {
				$result .= $newLine;
				$pos --;
				for ($j=0; $j<$pos; $j++) {
					$result .= $indentStr;
				}
			}
			
			// Add the character to the result string.
			$result .= $char;
			
			// If the last character was the beginning of an element, 
			// output a new line and indent the next line.
			if (($char == ',' || $char == '{' || $char == '[') && $outOfQuotes) {
				$result .= $newLine;
				if ($char == '{' || $char == '[') {
					$pos ++;
				}
				
				for ($j = 0; $j < $pos; $j++) {
					$result .= $indentStr;
				}
			}
			
			$prevChar = $char;
		}
		
		return $result;
	}
?>