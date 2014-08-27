sudo mv /usr/lib/automature/SPARK/spark.linux /usr/lib/automature/SPARK/spark
sudo mv /usr/lib/automature/SPARK/zug.linux mv /usr/lib/automature/SPARK/zug
if [ ! -e /usr/lib/automature/SPARK/Spark.ini ]
 then
sudo mv /usr/lib/automature/SPARK/Spark.ini.Linux /usr/lib/automature/SPARK/Spark.ini.xml
fi
sudo rm /usr/lib/automature/SPARK/Spark.ini.Mac
sudo rm /usr/lib/automature/SPARK/Spark.ini.Windows
sudo chmod 755 /usr/lib/automature/SPARK/spark
case ":$PATH:" in
  *:/usr/lib/automature/SPARK:*) echo it is in the path;;
  *)sudo cp /etc/environment /etc/environment.bak
    sudo echo "PATH=$PATH:/usr/lib/automature/SPARK">/etc/environment
    ;;
esac
sudo mkdir $HOME/SPARK\ Logs
sudo chmod a+rw $HOME/SPARK\ Logs
sudo chmod a+rw /usr/lib/automature/SPARK
sudo chmod a+rw /usr/lib/automature/SPARK/log
