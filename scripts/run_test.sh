#!/bin/bash
#param1: student id
#param2: assignment name
#param3: folder name

if [[ $1 ]] && [[ $2 ]] && [[ $3 ]]
then
	echo "Running a test"
else
	echo Invalid args 1:$1 2:$2 3:$3
	exit
fi

set -o errexit
path_to_assignment=/var/www/bigbluebutton-default/Dropbox/WoodsideAPCS2012-2013/$1/$2/$3/

# compile the assignment
echo "Compiling Assignment "$2" for user "$1
/root/woodside_apcs_2013_tests/APCS-Test-Framework/scripts/compile_java_files.sh /root/woodside_apcs_2013_tests/APCS-Test-Framework/lib/junit-4.11-SNAPSHOT-20120416-1530.jar $path_to_assignment*.java


# compile the assignment
echo "Compiling All Tests"
/root/woodside_apcs_2013_tests/APCS-Test-Framework/scripts/compile_java_files.sh /root/woodside_apcs_2013_tests/APCS-Test-Framework/lib/junit-4.11-SNAPSHOT-20120416-1530.jar:$path_to_assignment /root/woodside_apcs_2013_tests/APCS-Test-Framework/tests/*.java

#run the test
echo "Running"
/root/woodside_apcs_2013_tests/APCS-Test-Framework/scripts/run_java_files.sh /root/woodside_apcs_2013_tests/APCS-Test-Framework/tests/:/root/woodside_apcs_2013_tests/APCS-Test-Framework/lib/junit-4.11-SNAPSHOT-20120416-1530.jar:$path_to_assignment "org.junit.runner.JUnitCore Test$2"

echo "Finished running test successfully."
# compile the assignment
#echo "Compiling All Tests"
#scripts/compile_java_files.sh lib/junit-4.11-SNAPSHOT-20120416-1530.jar tests/*.java
