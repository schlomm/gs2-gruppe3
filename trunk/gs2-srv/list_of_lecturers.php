<?php
	include '../../includes/config.inc.php';
	mysql_connect($dbhost, $dblogin, $dbpasswd);
	mysql_select_db($dbname_csa);
	
	$sql = "SET NAMES 'utf8'";
	mysql_query($sql);
	$sql = "SELECT * FROM (
		SELECT uidnumber, sn, givenname FROM (
		  SELECT course_lecturer FROM courses WHERE course_status > 0 UNION 
		  SELECT CONVERT(course_lecturer_co1, SIGNED INTEGER) FROM courses WHERE course_status > 0 UNION
		  SELECT CONVERT(course_lecturer_co2, SIGNED INTEGER) FROM courses WHERE course_status > 0
		) AS l1 JOIN users ON (uidnumber = course_lecturer)
		UNION
		SELECT uidnumber, sn, givenname FROM (
		  SELECT course_lecturer FROM courses WHERE course_status > 0 UNION 
		  SELECT CONVERT(course_lecturer_co1, SIGNED INTEGER) FROM courses WHERE course_status > 0 UNION
		  SELECT CONVERT(course_lecturer_co2, SIGNED INTEGER) FROM courses WHERE course_status > 0
		) AS l2 JOIN users_external ON (uidnumber = course_lecturer)
	) AS lecturers ORDER BY sn";
	
	$result = mysql_query($sql) or die(mysql_error());
	$list = array();
	while ($data = mysql_fetch_assoc($result)) {
		$data['courses'] = array();
		$sql = "SELECT * FROM courses WHERE (course_lecturer = ". $data['uidnumber'] .
			" OR course_lecturer_co1 = ". $data['uidnumber'] .
			" OR course_lecturer_co1 = ". $data['uidnumber'] .")".
			" AND course_status = 1 ORDER BY course_term, course_title";
		$subresult = mysql_query($sql) or die(mysql_error());
		while ($subdata = mysql_fetch_assoc($subresult)) {
			$data['courses'][] = array("id" => $subdata['id'], "course_term" => $subdata['course_term']);
		}
		$list[] = $data;
	}
	echo json_encode($list);
	//echo '<pre>';
	//echo indent(json_encode($list));
	//echo '</pre>';

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