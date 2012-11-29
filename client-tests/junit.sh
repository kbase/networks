jpath=`ls /kb/dev_container/modules/networks/lib/*.jar` 
jpath=`echo $jpath | sed "s/ /:/g"` 
jpath=$jpath:/kb/dev_container/modules/networks/build/classes/
echo $jpath
java -cp $jpath org.junit.runner.JUnitCore $1

