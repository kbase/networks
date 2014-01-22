jpath=`ls /kb/dev_container/modules/networks/lib/*.jar /kb/dev_container/modules/jars/lib/jars/*/*.jar` 
jpath=`echo $jpath | sed "s/ /:/g"` 
jpath=$jpath:/kb/dev_container/modules/networks/build/classes/:/kb/dev_container/modules/networks/src
echo $jpath
java -cp $jpath org.junit.runner.JUnitCore $1

