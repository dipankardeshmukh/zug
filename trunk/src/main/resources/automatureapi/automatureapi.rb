require 'socket'
require 'json'

class AutomatureAPI
host=""
sendport=0
receiveport = 0   

def initialize()
        @host =  '127.0.0.1'
        @sendport = Integer(ARGV[ARGV.length-1])
        @receiveport = Process.pid()    
    end
def alterContextVar(argv1,argv2)
  jsonarr = {"method" => "alter","name"=>argv1,"value"=>argv2,"port"=>"#{45000+Process.pid()}"}
  message=JSON.dump(jsonarr)
  
  sock = TCPSocket.new(@host, (45000+@sendport))
  sock.write(message)
  sock.close
  
  sock = TCPSocket.new(@host, 45000+@receiveport)
  sock.read(5000)
  sock.close
  
end

def getContextVar(argv1)
  jsonarr = {"method" => "get","name"=>argv1,"value"=>"","port"=>"#{45000+Process.pid()}"}
  message=JSON.dump(jsonarr)
  sock = TCPSocket.new(@host, (45000+@sendport))
  sock.write(message)
  sock.close
  
  sock = TCPSocket.new(@host, (45000+@receiveport))
  s = sock.recv(1024)
  sock.close
  return s
end
end
