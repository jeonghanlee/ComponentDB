<link href="../common/irmis2.css" rel="stylesheet" type="text/css" />
<form action="action_net_search.php" method="POST">
<div class="searchCriteria">
  <table width="98%" align="right" cellpadding="2" cellspacing="0">
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td class="titleheading">Network Info<br />&nbsp;Search Criteria</td>
	</tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><input type="submit" class="buttonswide" name="show_network_switches" value="Show Network Switches" style="background:#ffff00"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td><input type="submit" class="buttonswide" name="show_terminal_servers" value="Show Terminal Servers" style="background:#ffff00"></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
    <!--<tr>
      <td><input type="submit" class="buttonswide" name="show_media_converters" value="Show Media Converters" style="background:#ffff00"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>-->
    <tr>
      <td><input type="submit" class="buttonswide" name="show_ioc_connections" value="Show IOC Connections" style="background:#ffff00"></td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>	
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
	  <td>&nbsp;</td>
	</tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
	<tr>
      <td>&nbsp;</td>
    </tr>
  </table>
  
  <div id="help">
  <table>
  <tr><td><a class="hyper" href="../top/wd_help.php#network" target="_blank"><b>Web Desktop Help</b></a></td></tr>
  </table>
  </div>
  
  <div id="servers">
  <?
    include_once ('db.inc');
    echo '<table><tr><td class="serverbackground"><b>Database Server:</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; '.$db_host.':'.$db_name_production_1.'</td></tr>';
	echo '<td class="serverbackground"><b>Application Server:</b> <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'.$app_host.'</td></tr>';
    echo '</table>';
  ?>
   </div>
</div>
</form>