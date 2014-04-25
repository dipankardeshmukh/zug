sudo mv /usr/lib/automature/ZUG/zug.linux /usr/lib/automature/ZUG/zug
if [ ! -e /usr/lib/automature/ZUG/ZugINI.xml ]
 then
sudo mv /usr/lib/automature/ZUG/ZugINI.xml.Linux /usr/lib/automature/ZUG/ZugINI.xml
fi
sudo rm /usr/lib/automature/ZUG/ZugINI.xml.Mac
sudo rm /usr/lib/automature/ZUG/ZugINI.xml.Windows
sudo chmod 755 /usr/lib/automature/ZUG/zug 
case ":$PATH:" in
  *:/usr/lib/automature/ZUG:*) echo it is in the path;;
  *)sudo cp /etc/environment /etc/environment.bak
    sudo echo "PATH=$PATH:/usr/lib/automature/ZUG">/etc/environment
    ;;
esac
sudo mkdir $HOME/ZUG\ Logs
sudo chmod a+rw $HOME/ZUG\ Logs
sudo chmod a+rw /usr/lib/automature/ZUG