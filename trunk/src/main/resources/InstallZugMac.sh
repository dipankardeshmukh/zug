#! /usr/bin/env bash
sudo mv /Applications/Automature/ZUG/zug.mac /Applications/Automature/ZUG/zug
sudo mv /Applications/Automature/ZUG/ZugINI.xml.Mac /Applications/Automature/ZUG/ZugINI.xml
sudo rm /Applications/Automature/ZUG/ZugINI.xml.Linux
sudo rm /Applications/Automature/ZUG/ZugINI.xml.Windows
sudo chmod 755 /Applications/Automature/ZUG/zug
if [[ $PATH =~ /Applications/Automature/ZUG ]]; then
	echo "Path alreasy set"
else
	sudo echo "/Applications/Automature/ZUG">/etc/paths.d/zug
fi
sudo mkdir $HOME/ZUG\ Logs
sudo mkdir /Applications/Automature/ZUG/log
sudo chmod -R a+rw $HOME/ZUG\ Logs
sudo chmod -R a+rw /Applications/Automature/ZUG/log


