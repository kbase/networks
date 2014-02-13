jpath=`ls $KB_TOP/modules/networks/lib/*.jar` 
jpath=`echo $jpath | sed "s/ /:/g"` 
jpath=$jpath:$KB_TOP/modules/networks/build/classes/:$KB_TOP/modules/networks/src
echo $jpath
java -cp $jpath org.junit.runner.JUnitCore $1

