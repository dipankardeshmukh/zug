To install Davos on Ubuntu system you need to install some ruby packages and certain related gems. Follow the steps below to install all the required packages and gems:

Open the terminal and type the following commands

1. sudo apt-get update  ( recommended)
2. sudo apt-get install ruby rubygems gem libmysqlclient-dev
3. gem install sinatra data_mapper builder daemons dm-mysql-adapter
4. sudo sh /path_to_installer/DAVOS_INSTALLER-4.2

Done! Open a web browser and go to localhost:4567 to verify installation. It assumes that you MySQL installation is on the local machine and if you need to change that update the IP/Domain in /var/www/davos/davos.rb file.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

If you get the following error please follow the steps mentioned below

Invalid gemspec in [/var/lib/gems/1.8/specifications/tilt-1.3.3.gemspec]: invalid date format in specification: "2011-08-25 00:00:00.000000000Z"

Invalid gemspec in [/var/lib/gems/1.8/specifications/dm-serializer-1.2.1.gemspec]: invalid date format in specification: "2011-10-24 00:00:00.000000000Z"



automature@Meluha:/var/lib/gems/1.8/specifications$ sudo pico tilt-1.3.3.gemspec 

automature@Meluha:/var/lib/gems/1.8/specifications$ sudo pico dm-serializer-1.2.1.gemspec



step 1. cd /var/lib/gems/1.8/specifications

step 2. open the files(tilt-1.3.3.gemspec and dm-serializer-1.2.1.gemspec) in any text editor

step 3. change the s.date = %q{2011-10-13 00:00:00.000000000Z} to s.date = %q{2011-10-13}




