<?php
	include '../../includes/config.inc.php';
	mysql_connect($dbhost, $dblogin, $dbpasswd);
	mysql_select_db($dbname_csa);
	
	if (is_numeric($_GET['term']))
		$term = $_GET['term'];
	else
		$term = 7;
	$sort = 'course_title';
	$order = 'ASC';
	
	$sql = "SET NAMES 'utf8'";
	mysql_query($sql);
	$sql = "SELECT id, course_title, course_type, course_language, course_term, course_lecturer, ".
		"course_lecturer_co1, course_lecturer_co2, courses_of_studies FROM courses";
	if ($sort == 'lecturer') {
		$sql .= " JOIN (SELECT uidnumber, sn FROM users UNION SELECT uidnumber, sn FROM users_external) AS users_all
		ON course_lecturer = uidnumber";
	}
	if ($sort == 'type') {
		$sql .= " JOIN courses_type
		ON courses_type.id = course_type";
	}
	$sql .= " WHERE course_status = 1 AND course_term = " . $term;
	$sql .= " ORDER BY " . $sort;
	$sql .= " " . $order;
	
	$result = mysql_query($sql) or die(mysql_error());
	$list = array();
	while ($data = mysql_fetch_assoc($result)) {
		// lecturer data
		$sql = "SELECT * FROM (".
			"SELECT uidnumber, sn, givenname FROM users WHERE uidnumber = ". $data['course_lecturer'];
		
		if (is_numeric($data['course_lecturer_co1'])) 
			$sql .= " OR uidnumber = ". $data['course_lecturer_co1'];
		if (is_numeric($data['course_lecturer_co2'])) 
			$sql .= " OR uidnumber = ". $data['course_lecturer_co2'];
		$sql .= " UNION ";
		$sql .= "SELECT uidnumber, sn, givenname FROM users_external WHERE uidnumber = ". $data['course_lecturer'];
		if (is_numeric($data['course_lecturer_co1'])) 
			$sql .= " OR uidnumber = ". $data['course_lecturer_co1'];
		if (is_numeric($data['course_lecturer_co2'])) 
			$sql .= " OR uidnumber = ". $data['course_lecturer_co2'];
		$sql .= ") as all_users";
		$subresult = mysql_query($sql) or die(mysql_error());
		$lecturers = array();
		while ($subdata = mysql_fetch_assoc($subresult)) {
			$lecturer = array();
			$lecturer['uid'] = $subdata['uidnumber'];
			$lecturer['first_name'] = $subdata['givenname'];
			$lecturer['last_name'] = $subdata['sn'];
			if ($lecturer['uid'] == $data['course_lecturer']) $lecturers[0] = $lecturer;
			if ($lecturer['uid'] == $data['course_lecturer_co1']) $lecturers[1] = $lecturer;
			if ($lecturer['uid'] == $data['course_lecturer_co2']) $lecturers[2] = $lecturer;
			ksort($lecturers);
			$data['course_lecturers'] = array_values($lecturers);
		}
		
		// type data
		if ($data['course_type']) {
			$sql = "SELECT * FROM courses_type WHERE id = ". $data['course_type'];
			$subresult = mysql_query($sql) or die(mysql_error());
			$subdata = mysql_fetch_assoc($subresult);
			$data['course_type'] = array('id' => $subdata['id'], 'name' => $subdata['titel']);
		} else {
			unset($data['course_type']);
		}
		// term data
		$sql = "SELECT * FROM terms WHERE id = ". $data['course_term'];
		$subresult = mysql_query($sql) or die(mysql_error());
		$subdata = mysql_fetch_assoc($subresult);
		$data['course_term'] = array('id' => $subdata['id'], 'name' => $subdata['name']);
		
		// participants data
		$sql = "SELECT entry_student FROM entries WHERE entry_course = ". $data['id'] ." AND entry_status = 1";
		$subresult = mysql_query($sql) or die(mysql_error());
		$data['course_participants'] = mysql_num_rows($subresult);
		
		if ($data['courses_of_studies']) {
			$courses_of_studies = explode(';', $data['courses_of_studies']);
			$data['courses_of_studies'] = array();
			foreach ($courses_of_studies as $course_of_studies) {
				$obj = array();
				$sql = "SELECT id, titel FROM courses_of_studies WHERE id = ". $course_of_studies;
				$subresult = mysql_query($sql) or die(mysql_error());
				$subdata = mysql_fetch_assoc($subresult);
				$obj['id'] = $subdata['id'];
				$obj['name'] = $subdata['titel'];
				$data['courses_of_studies'][] = $obj;
			}
		} else {
			unset($data['courses_of_studies']);
		}
		unset($data['course_lecturer']);
		unset($data['course_lecturer_co1']);
		unset($data['course_lecturer_co2']);
		$list[] = $data;
	}
	echo json_encode($list);
	
	/*echo '<pre>';
	echo indent(json_encode($list));
	echo '</pre>';*/
	
	
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
		$indentStr   = '   ';
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