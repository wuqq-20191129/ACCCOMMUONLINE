APP_HOME=/home/acc/charge
CLASSPATH=$APP_HOME/lib
for i in "$APP_HOME"/lib/*.jar
do
 CLASSPATH="$CLASSPATH":"$i"
done
export CLASSPATH=.:$CLASSPATH
echo ${CLASSPATH}
${JAVA_HOME}/bin/java -Xms128m -Xmx1000m -Dprog=charge com.goldsign.commu.frame.server.OnlineServer
