<?php
	if (!is_numeric($_GET['id']))
		die();
	include '../../includes/config.inc.php';
	mysql_connect($dbhost, $dblogin, $dbpasswd);
	mysql_select_db($dbname_csa);

	$sql = "SET NAMES 'utf8'";
	mysql_query($sql);
	$sql = "SELECT id, course_number, course_title, course_sws, course_ects, course_modules, course_time, course_room, course_requirements, course_short_description, course_website ". 
		"course_long_description FROM courses WHERE id = ". mysql_real_escape_string($_GET['id']);
	$result = mysql_query($sql) or die(mysql_error());
	if (mysql_num_rows($result) == 0)
		die();
	else
		$data = mysql_fetch_assoc($result);
	
	$courses_of_studies = explode(';', $data['course_modules']);
	$data['courses_of_studies'] = array();
	foreach ($courses_of_studies as $course_of_studies) {
		$exp = explode(':', $course_of_studies);
		$obj = array();
		$obj['id'] = $exp[0];
		$sql = "SELECT titel FROM courses_of_studies WHERE id = ". $obj['id'];
		$subresult = mysql_query($sql);
		$subdata = mysql_fetch_assoc($subresult);
		$obj['name'] = $subdata['titel'];
		$obj['modules'] = array();
		$list_of_modules = explode(',', $exp[1]);
		foreach ($list_of_modules as $module) {
			$obj['modules'][] =
			($course_of_studies == 3) ? // M.Sc. GeoTech
			$module + 3 :
			$module;
		}
		$data['courses_of_studies'][] = $obj;
	}
	if (trim($data['course_number']) == '')
		unset($data['course_number']);
	if (trim($data['course_ects']) == '')
		unset($data['course_ects']);
	if (trim($data['course_sws']) == '')
		unset($data['course_sws']);
	if (trim($data['course_room']) == '')
		unset($data['course_room']);
	if (trim($data['course_time']) == '')
		unset($data['course_time']);
	if (trim($data['course_website']) == '')
		unset($data['course_website']);
	
	$data['course_requirements'] = cut_string($data['course_requirements']);
	$data['course_short_description'] = cut_string($data['course_short_description']);
	$data['course_long_description'] = cut_string($data['course_long_description']);
	
	if (is_empty_string($data['course_requirements']))
		unset($data['course_requirements']);
	if (is_empty_string($data['course_short_description']))
		unset($data['course_short_description']);
	if (is_empty_string($data['course_long_description']))
		unset($data['course_long_description']);
	
	echo json_encode($data);
	
	function is_empty_string($string) {
		$string = trim($string);
		$string = str_replace('<br />', '', $string);
		$string = str_replace('<br/>', '', $string);
		$string = str_replace('<br>', '', $string);
		if (empty($string))
			return true;
		else
			return false; 
	}
	
	function cut_string($string) {
		$string = trim($string);
		$string = str_replace("\t", '', $string);
		$string = str_replace("\r", '', $string);
		$string = str_replace('<p>', '', $string);
		$string = str_replace('</p>', '<br />', $string);
		return $string;
	}
?>