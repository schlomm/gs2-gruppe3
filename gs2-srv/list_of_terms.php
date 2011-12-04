<?php
	include '../../includes/config.inc.php';
	mysql_connect($dbhost, $dblogin, $dbpasswd);
	mysql_select_db($dbname_csa);
	
	$sql = "SET NAMES 'utf8'";
	mysql_query($sql);
	$sql = "SELECT * FROM terms ORDER BY id";
	
	//echo $sql;
	//sleep(5);
	$result = mysql_query($sql) or die(mysql_error());
	$list = array();
	while ($data = mysql_fetch_assoc($result)) {
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