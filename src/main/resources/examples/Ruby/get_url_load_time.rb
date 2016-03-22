require 'automatureapi'    

obj=AutomatureAPI.new
time1 = Time.new
system("open -a Ie #{ARGV[0]}")
time2 = Time.now 
obj.alterContextVar(ARGV[1],(time2-time1))