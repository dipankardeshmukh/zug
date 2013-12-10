#! /usr/bin/env bash
sudo mv /Applications/Automature/ZUG/zug.mac /Applications/Automature/ZUG/zug
sudo chmod 755 /Applications/Automature/ZUG/zug
if [[ $PATH =~ :/Applications/Automature/ZUG: ]]; then
	echo "Path alreasy set"
else
	sudo echo "/Applications/Automature/ZUG">/etc/paths.d/zug
fi
exit
