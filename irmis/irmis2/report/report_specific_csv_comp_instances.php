<?php

 /*
  * Written by Dawn Clemons
  * Specialized step in creating a CSV report. Information that applies only
  * to the Component Types (Instances) PHP viewer is written in the same
  * order as it appeared in the viewer.
  */

   //The file pointer $fp was already declared in report_generic_csv.php

   $delim = ",";


   $ctEntity = $_SESSION['selectedComponentType'];
   $ctList = $ctEntity->getComponentList();

	fwrite($fp, "\r\n\r\n");
	fwrite($fp, $ctList->length()." Components Installed");
	fwrite($fp, "\r\n");
    fwrite($fp, "Component Type".$delim);
	fwrite($fp, "Name".$delim);
	fwrite($fp, "Owner".$delim);
	fwrite($fp, "IOC Parent".$delim);
	fwrite($fp, "Housing Parent Path"."\r\n\r\n");

   //Too many results, so report will not be finished
   if ($ctList == null){
      fwrite($fp, "Could not create report!\r\n");
      fwrite($fp, "Search produced too many results to display. \r\n");
      fwrite($fp, "Limit is 5000. Try using the Additional\r\n");
      fwrite($fp, "Search Terms to constrain your search.\r\n");
   }
   //No results, so report will not be finished
   else if ($ctList->length() == 0){
      fwrite($fp, "Could not create report!\r\n");
      fwrite($fp, "No Components found: please try another search.\r\n");
   }
   //Report will be finished, so write data
   else{
      $ctArray = $ctList->getArray();
      foreach ($ctArray as $ctList){
		  
		  $name = $ctEntity->getctName();
		  $comma = ",";
		  $pos = strpos($name, $comma); // identify the position of the comma in the component type name
		  if ($pos > 0) { //if there is no comma, $pos will be "0" so print out the $ctEntity->getctName() (component type name)
			  $namefix = substr_replace($name, ";", $pos, 1); //if there is a comma, replace it with a simicolon
			  fwrite($fp, $namefix. " (".$ctList->getID().")".$delim); //print out the "new" component type name
		  } else {
		  fwrite($fp, $ctEntity->getctName(). " (".$ctList->getID().")".$delim);
		  }
         fwrite($fp, $ctList->getInstanceName().$delim);
		 fwrite($fp, $ctList->getGroup().$delim);
         fwrite($fp, $ctList->getcomParentIOC().$delim);
		 
		 $locationPath=array_values($ctList->getcomLocationPath());
			if ($locationPath[0]) {
			  fwrite($fp, $locationPath[0]);
			if ($locationPath[1]) {
			  fwrite($fp, " | ".$locationPath[1]);
		    if ($locationPath[2]) {
			  fwrite($fp, " | ".$locationPath[2]);
			if ($locationPath[3]) {
			  fwrite($fp, " | ".$locationPath[3]);
			if ($locationPath[4]) {
			  fwrite($fp, " | ".$locationPath[4]);
			if ($locationPath[5]) {
			  fwrite($fp, " | ".$locationPath[5]);
			if ($locationPath[6]) {
			  fwrite($fp, " | ".$locationPath[6]);
			if ($locationPath[7]) {
			  fwrite($fp, " | ".$locationPath[7]);
			if ($locationPath[8]) {
			  fwrite($fp, " | ".$locationPath[8]);
			if ($locationPath[9]) {
			  fwrite($fp, " | ".$locationPath[9]);
			  }}}}}}}}}}
		 
         //fwrite($fp, $ctList->getcomParentRack().$delim);
         //fwrite($fp, $ctList->getcomParentRoom().$delim);
         fwrite($fp, /*($fp, $ctList->getcomParentBldg().*/"\r\n"); }
      //}
   }

?>