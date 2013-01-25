The username is automature and password is automature .Once you are logged in, you need to configure the ip address of the Virtual Machine.
Hence kindly do the following:
1.Open the terminal.
2.Type ifconfig and get the ip address of the VM.
3.Type sudo vi /etc/mysql/my.cnf
4.Give the password automature.
5.Go the the line written bind-address = (ip address). Replace the ip address with the ip address of the VM. and save the file.
6.In the terminal type sudo service mysql restart
7.Open the Firefox browser and type http://localhost:31680/twiki/bin/configure . The password is automature
8.Click on General Path Settings. Go to PermittedRedirectHostUrls and type http://(ip address of the VM):31680 [ example- http://192.168.2.27:31680]
9.Click on Next at the bottom of the page and finally Save to confirm the changes.
10.In the URL type in http://localhost:31680/twiki/bin/view and log in as admin (username) and automature (password).
11.In the home tab, click on Zermatt WebHome and you are all set to use our product.

The username is admin and the password is automature .