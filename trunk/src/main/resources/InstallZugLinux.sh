sudo mv /usr/lib/automature/ZUG/zug.linux /usr/lib/automature/ZUG/zug
sudo chmod 755 /usr/lib/automature/ZUG/zug 
case ":$PATH:" in
  *:/usr/lib/automature/ZUG:*) echo it is in the path;;
  *)sudo cp /etc/environment /etc/environment.bak
    sudo echo "PATH=$PATH:/usr/lib/automature/ZUG">/etc/environment
    ;;
esac
