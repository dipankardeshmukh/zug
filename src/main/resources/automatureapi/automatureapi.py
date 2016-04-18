import socket
import sys            
import json

class AutomatureAPI(object):  
  host = ""   
  port = 0  
  
  s=None
     
  def __init__(self):
  
    self.host="127.0.0.1"
   
    self.port=45000+int(sys.argv[len(sys.argv)-1]) % 10000
    
                 
  def alterContextVar(self,argv1,argv2): 
    self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.s.connect((self.host,self.port))
    message=("{\"method\":\"alter\",\"name\":\""+argv1+"\",\"value\":\""+argv2+"\"}")  
    self.s.sendall(bytes(message))
    data=self.s.recv(1024).decode("utf-8") 
    if json.loads( data)['error']=='1':                
         self.s.close()
         raise Exception('Error while altering context variable')  
    self.s.close()
    
  def getContextVar(self,argv1):
    self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.s.connect((self.host,self.port))
    message=("{\"method\":\"get\",\"name\":\""+argv1+"\",\"value\":\""+""+"\"}")  
    self.s.sendall(bytes(message))
    data = self.s.recv(1024).decode("utf-8") 
    if json.loads( data)['error']=='1':
         self.s.close()
         raise Exception('Error while getting context variable')  
    self.s.close()    
    return (json.loads(data)['value'])
    
      
    

  