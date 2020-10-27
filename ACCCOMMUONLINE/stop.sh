a=`ps -ef|grep acc |grep java|grep 'prog=charge'|grep -v 'grep'|awk '{print $2}'`
kill -9  $a
