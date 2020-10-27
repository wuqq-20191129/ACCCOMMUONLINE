LANG=Zh_CN.GB18030
export LANG
nohup ./runCommuServer.sh >commuserver.log& 
tail -f commuserver.log
