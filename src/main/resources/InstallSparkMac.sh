#! /usr/bin/env bash
sudo mv /Applications/Automature/SPARK/spark.mac /Applications/Automature/SPARK/spark
sudo mv /Applications/Automature/SPARK/zug.mac /Application/Automature/SPARK/zug
if [ ! -e /Applications/Automature/SPARK/Spark.ini ]
 then
sudo mv /Applications/Automature/SPARK/Spark.ini.Mac /Applications/Automature/SPARK/Spark.ini
fi
sudo rm /Applications/Automature/SPARK/Spark.ini.Linux
sudo rm /Applications/Automature/SPARK/Spark.ini.Windows
sudo chmod 755 /Applications/Automature/SPARK/spark
if [[ $PATH =~ /Applications/Automature/SPARK ]]; then
	echo "Path alreasy set"
else
	sudo echo "/Applications/Automature/SPARK">/etc/paths.d/spark
fi
sudo mkdir $HOME/SPARK\ Logs
sudo mkdir /Applications/Automature/SPARK/log
sudo chmod -R a+rw $HOME/SPARK\ Logs
sudo chmod -R a+rw /Applications/Automature/SPARK/log


