
package ADLCrawlerDBLayer;

# This package provides routines for writing ioc record references
# data from adl files to a MySQL database. 

use DBI qw(:sql_types);
require Exporter;
use LogUtil;
use Blowfish_PP;
use File::stat;

our @ISA = qw(Exporter);
our @EXPORT = qw( update_adl_tables set_test_mode set_home_dir set_decryption_key);
our @EXPORT_OK = qw();
our $VERSION = 1.00;
our $ADL = 1;

my $test_mode = 0;  # if 1, do no db access
my $home_dir = "."; # dir from which parent crawler is run
my $db_properties_path = "db.properties";
my $dbh;
my $db_vendor; # established on first db_connect
my $decryption_key; # optional 16 byte blowfish key
my $firstTime = 1;

# Public
sub set_test_mode {
    $test_mode = $_[0];
}

# Public
# Sets the directory where db.properties file can be found. 
sub set_home_dir {
    $home_dir = $_[0];

    $db_properties_path = $home_dir . "/db.properties";
}

# Public
# Sets the 16 char blowfish decryption key. If this is defined, this module
# will assume the db.properties file is encrypted with this key.
sub set_decryption_key {
    $decryption_key = $_[0];
}

# Public
sub update_adl_tables {
    my $adl_hits_ref = $_[0];

    LogUtil::log('debug',"ADLCrawlerDBLayer: update_adl_records entered");

    if ($test_mode) {
        return;
    }

    if (!$dbh) {
        $dbh = db_connect();
        if (!$dbh) {
            LogUtil::log('error',"ADLCrawlerDBLayer: Cannot connect to DB");
            exit;
        }
    }

    mark_adl_records_as_old() if $firstTime;
    $firstTime = 0;

    add_new_adl_records ($adl_hits_ref);

    $dbh->commit; 

    LogUtil::log('debug',"ADLCrawlerDBLayer: update_adl_records finished");
    return;
}


# Private
sub mark_adl_records_as_old {

    LogUtil::log('debug',"ADLCrawlerDBLayer: mark_adl_records_as_old entered");

    if ($test_mode) {
        return;
    }
    if (!$dbh) {
        $dbh = db_connect();
    }


    LogUtil::log('debug',"ADLCrawlerDBLayer: marking all adl records as old");

    eval {
        my $update_rec_client_sql;
        $update_rec_client_sql = 'update rec_client set current_load=0 '
            ."where rec_client_type_id=$ADL and current_load=1";

        my $update_rec_client_st;
        $update_rec_client_st = $dbh->prepare($update_rec_client_sql);
        $update_rec_client_st->execute();
    };
    if ($@) {
        $dbh->rollback;
        LogUtil::log('error',"mark_adl_records_as_old():$@");
        return;
    }
    
    LogUtil::log('debug',"ADLCrawlerDBLayer: mark_adl_records_as_old finished");
    return;
};


# Private
sub add_new_adl_records {
    my $adl_hits_ref = $_[0];
    my $prevFilename = "";

    if (!$dbh) {
        $dbh = db_connect();
    }

    LogUtil::log('debug',"Adding uri, uri_rel, and  rec_client records\n");

    my $uri_id = -1;
    my $vuri_id = -1;

    # Loop over array items, writing to URI, rec_client, and uri_rel row
    foreach $row (@$adl_hits_ref) {

        # items were added using following line
        # push (@pv_list,{$pv_name,$arg_count});
        # push (@pv_macro_list,{$pv_name});
        # push (@adl_hits,{$filename,$rel_info,\@pv_list,\@pv_macro_list});
    
        $filename = $row->[0];
        #LogUtil::log('debug',"filename=$filename\n");
        $rel_info = $row->[1];
        #LogUtil::log('debug',"rel_info=$rel_info\n");
        $pv_macro_list_ref = $row->[2];
        $pv_list_ref = $row->[3];
        eval {
            if ($filename ne $prevFilename ) {

                $uri_id = pre_insert_get_id($dbh, "uri");

                # SQL for writing out a row of the URI table
                my $insert_uri_sql = 'insert into uri ' .
                    '(uri_id, uri, uri_modified_date, modified_date, modified_by) ' .
                    'values (?, ?, ?, ' . get_current_date_time() . ', \'adlcrawler\')';
                my $insert_uri_st = $dbh->prepare($insert_uri_sql);
    
                # bind and execute to write out uri row
                if ($uri_id) { $insert_uri_st->bind_param(1, $uri_id, SQL_INTEGER); }
                $insert_uri_st->bind_param(2, $filename, SQL_VARCHAR);
                my $mtime = stat($filename)->mtime;
                if ($mtime) {
                    $insert_uri_st->bind_param(3, format_date($mtime), SQL_TIMESTAMP);
                } else {
                    $insert_uri_st->bind_param(3, get_null_timestamp(), SQL_TIMESTAMP);
                }
                $insert_uri_st->execute();

                $uri_id = post_insert_get_id($dbh, "uri");

                $prevFilename = $filename;
            }

            if (scalar(@$pv_macro_list_ref)) { 

                $vuri_id = pre_insert_get_id($dbh, "vuri");

                # SQL for writing out a row of the vuri table
                my $insert_vuri_sql = 'insert into vuri ' .
                    '(vuri_id, uri_id) ' .
                    'values (?, ?)';
                my $insert_vuri_st = $dbh->prepare($insert_vuri_sql);
    
                # bind and execute to write out vuri row
                if ($vuri_id) { $insert_vuri_st->bind_param(1, $vuri_id, SQL_INTEGER); }
                $insert_vuri_st->bind_param(2, $uri_id, SQL_INTEGER);
                $insert_vuri_st->execute();
    
                $vuri_id = post_insert_get_id($dbh, "vuri");

                my $vuri_rel_id = pre_insert_get_id($dbh, "vuri_rel");

                # SQL for writing out a row of the vuri_rel table
                my $insert_vuri_rel_sql = 'insert into vuri_rel ' .
                    '(vuri_rel_id, parent_vuri_id, child_vuri_id, rel_info) ' .
                    'values (?, null, ?, ?)';
                my $insert_vuri_rel_st = $dbh->prepare($insert_vuri_rel_sql);

                # bind and execute to write out vuri_rel row
                if ($vuri_rel_id) { $insert_vuri_rel_st->bind_param(1, $vuri_rel_id, SQL_INTEGER); }
                $insert_vuri_rel_st->bind_param(2, $vuri_id, SQL_INTEGER);
                $insert_vuri_rel_st->bind_param(3, $rel_info, SQL_VARCHAR);
                $insert_vuri_rel_st->execute();

                foreach $pv (@$pv_macro_list_ref) { 
                    $pv_name = $pv->[0];
                    #LogUtil::log('debug',"pv_name=$pv_name\n");
                    my ($rec_name,$fld_name) = split (/\./,$pv_name,2);
                    unless (defined $fld_name) {$fld_name = "VAL";}
                
                    my $rec_client_id = pre_insert_get_id($dbh, "rec_client");

                    # SQL for writing out a row of the rec_client table
                    my $insert_rec_client_sql = 'insert into rec_client ' .
                        '(rec_client_id, rec_client_type_id, rec_nm, fld_type, '.
                        'vuri_id, current_load) ';
                    $insert_rec_client_sql = $insert_rec_client_sql .
                        "values (?, $ADL, ?, ?, ?, 1)";
                    my $insert_rec_client_st = $dbh->prepare($insert_rec_client_sql);
    
                    # bind and execute to write out rec_client row
                    if ($rec_client_id) { $insert_rec_client_st->bind_param(1, $rec_client_id, SQL_INTEGER); }
                    $insert_rec_client_st->bind_param(2, $rec_name , SQL_VARCHAR);
                    $insert_rec_client_st->bind_param(3, $fld_name, SQL_VARCHAR);
                    $insert_rec_client_st->bind_param(4, $vuri_id, SQL_INTEGER);
                    $insert_rec_client_st->execute();
                }

            }

            if (scalar(@$pv_list_ref)) { 

                $vuri_id = pre_insert_get_id($dbh, "vuri");

                # SQL for writing out a row of the vuri table
                my $insert_vuri_sql = 'insert into vuri ' .
                    '(vuri_id, uri_id) ' .
                    'values (?, ?)';
                my $insert_vuri_st = $dbh->prepare($insert_vuri_sql);
    
                # bind and execute to write out vuri row
                if ($vuri_id) { $insert_vuri_st->bind_param(1, $vuri_id, SQL_INTEGER); }
                $insert_vuri_st->bind_param(2, $uri_id, SQL_INTEGER);
                $insert_vuri_st->execute();
    
                $vuri_id = post_insert_get_id($dbh, "vuri");

                $vuri_rel_id = pre_insert_get_id($dbh, "vuri_rel");

                # SQL for writing out a row of the vuri_rel table
                my $insert_vuri_rel_sql = 'insert into vuri_rel ' .
                    '(vuri_rel_id, parent_vuri_id, child_vuri_id, rel_info) ' .
                    'values (?, null, ?, null)';
                my $insert_vuri_rel_st = $dbh->prepare($insert_vuri_rel_sql);
    
                # bind and execute to write out vuri_rel row
                if ($vuri_rel_id) { $insert_vuri_rel_st->bind_param(1, $vuri_rel_id, SQL_INTEGER); }
                $insert_vuri_rel_st->bind_param(2, $vuri_id, SQL_INTEGER);
                $insert_vuri_rel_st->execute();

                foreach $pv (@$pv_list_ref) { 
                    $pv_name = $pv->[0];
                    #LogUtil::log('debug',"pv_name=$pv_name\n");
                    $arg_count = $pv->[1];
                    #LogUtil::log('debug',"arg_count=$arg_count\n");
                    my ($rec_name,$fld_name) = split (/\./,$pv_name,2);
                    unless (defined $fld_name) {$fld_name = "VAL";}
                
                    $rec_client_id = pre_insert_get_id($dbh, "rec_client");

                    # SQL for writing out a row of the rec_client table
                    my $insert_rec_client_sql = 'insert into rec_client ' .
                        '(rec_client_id, rec_client_type_id, rec_nm, fld_type, '.
                        'vuri_id, current_load) ';
                    $insert_rec_client_sql = $insert_rec_client_sql .
                        "values (?, $ADL, ?, ?, ?, 1)";
                    my $insert_rec_client_st = $dbh->prepare($insert_rec_client_sql);
    
                    # bind and execute to write out rec_client row
                    if ($rec_client_id) { $insert_rec_client_st->bind_param(1, $rec_client_id, SQL_INTEGER); }
                    $insert_rec_client_st->bind_param(2, $rec_name , SQL_VARCHAR);
                    $insert_rec_client_st->bind_param(3, $fld_name, SQL_VARCHAR);
                    $insert_rec_client_st->bind_param(4, $vuri_id, SQL_INTEGER);
                    $insert_rec_client_st->execute();
                }
            }
        };
        if ($@) {
            $dbh->rollback;
            LogUtil::log('error',"add_new_adl_records():$@");
            exit;
        }
    }
    return;
}



# Takes seconds after epoch and converts to string with "YYYY-MM-DD HH:MM:SS"
# Private
sub format_date {
    my $secsAfter1970 = $_[0];

    if (!$secsAfter1970 || $secsAfter1970 == 0) {
        return get_null_timestamp();
    }

    my @tmArr = localtime($secsAfter1970);
    my $formatStr = '%Y-%m-%d %H:%M:%S';

    my $formatDate = POSIX::strftime($formatStr, $tmArr[0],$tmArr[1],$tmArr[2],
                                     $tmArr[3], $tmArr[4], $tmArr[5]);

    return $formatDate;
}


# Private
sub db_connect {

    #connect to database
    my $dbh;

    # Look up connection parameters in external db.properties file.
    # If a decryption key has been supplied, use blowfish algorithm
    # to decrypt the contents of the file.
    
    my $db_host;
    my $db_database;
    my $db_user;
    my $db_password;

    if (!-e $db_properties_path) {
        LogUtil::log('error',"Unable to find db.properties file");
        LogUtil::log('error',"Exiting...");
        exit;
    }

    # Read in contents of db.properties (decrypting if key defined)
    my $blowfish_object;
    if ($decryption_key) {
        my $packed_key = pack("a16",$decryption_key);
        $blowfish_object = Crypt::Blowfish_PP->new($packed_key);
        if (!$blowfish_object) {
            LogUtil::log('error',"The given blowfish key appears to be invalid");
            LogUtil::log('error',"Exiting...");
            exit;
        }
    }
    open(DBPROPS,$db_properties_path);
    my $inbuf;
    my $input_text;
    while (my $read_len = read(DBPROPS, $inbuf, 8)) {
        if ($blowfish_object) {
            my $decrypted_text = $blowfish_object->decrypt($inbuf);
            $input_text = $input_text . $decrypted_text;
        } else {
            $input_text = $input_text . $inbuf;
        }
    }
    close(DBPROPS);

    # Parse up the db.properties text
    my $props_line;
    while ($input_text =~ /^([^\n]*)\n(.*)/s) {
        $props_line = $1;
        $input_text = $2;
        if ($props_line =~ /(.*)=(.*)/) {
            $prop_name = $1;
            $prop_val = $2;
            if ($prop_name eq "db.host") {
                $db_host = $prop_val;
            } elsif ($prop_name eq "db.database") {
                $db_database = $prop_val;
            } elsif ($prop_name eq "db.user") {
                $db_user = $prop_val;
            } elsif ($prop_name eq "db.password") {
                $db_password = $prop_val;
            } elsif ($prop_name eq "db.vendor") {
                # db_vendor is package global
                $db_vendor = $prop_val;
            }
        }
    }
    if (!$db_host || !$db_database || !$db_user || !$db_password ||!$db_vendor) {
        LogUtil::log('error',"Unable to read db connection parameters from db.properties");
        LogUtil::log('error',"Exiting...");
        exit;
    }


    # Get db vendor-specific DBI connect string
    my $dsn = get_dbi_dsn($db_database, $db_host);

    eval {
        $dbh = DBI->connect($dsn,$db_user,$db_password,
                            {AutoCommit=>0, RaiseError=>1, PrintError=>0, 
                             FetchHashKeyName=>'NAME_lc'});
    };
    if ($@) {
        LogUtil::log('error',"Unable to connect to db $@");
        LogUtil::log('error',"Exiting...");
        exit;
    }
    # Do any db vendor-specific db session initialization
    initialize_session($dbh);

    return $dbh;
}

# Very odd perl autoloading ...
sub AUTOLOAD {

    # Get the core function being called
    my $call_signature;
    if ($AUTOLOAD =~ /.*::(.*)/) {
        $call_signature = $1;
    } else {
        $call_signature = $AUTOLOAD;
    }

    if ($call_signature ne 'pre_insert_get_id' &&
        $call_signature ne 'post_insert_get_id' &&
        $call_signature ne 'get_dbi_dsn' &&
        $call_signature ne 'get_null_timestamp' &&
        $call_signature ne 'get_current_date_time' &&
        $call_signature ne 'initialize_session' &&
        $call_signature ne 'get_format_date_sql') {
        warn "Attempt to call $call_signature, which is not defined!\n";
        return;
    }

    # Determine which vendor DBFunctions.pm module we need to load
    my $db_functions_module = $db_vendor . "DBFunctions";
    require "$home_dir/$db_functions_module.pm";

    my $subroutine = "$db_functions_module"."::".$call_signature;
    goto &$subroutine;
}

1;