import socket
import time
import threading  
import os                 
import platform

class AutomatureAPI(object):  
  host = ""   
  sendport = 0  
  receiveport = 0  
  
  s=None
     
  def __init__(self):
    self.host="127.0.0.1"
    if platform.system() == 'Windows':
      import psutil
      port = psutil.Process(os.getpid()).ppid
    else:
      port = os.getppid()
    self.sendport=45000+port()  
    
    self.receiveport=45000+os.getpid()   
    
                 
  def alterContextVar(self,argv1,argv2):
    message=("{\"method\":\"alter\",\"name\":\""+argv1+"\",\"value\":\""+argv2+"\",\"port\":"+str(self.receiveport)+"}")           
    self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.s.connect((self.host,self.sendport))
    self.s.sendall(bytes(message, 'UTF-8'))
    self.s.close()     
    self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.s.connect((self.host,self.receiveport))
    self.s.recv(1024).decode("utf-8")
    self.s.close()
    
  def getContextVar(self,argv1):
    
    message=("{\"method\":\"get\",\"name\":\""+argv1+"\",\"value\":\""+""+"\",\"port\":"+str(self.receiveport)+"}")    
    self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.s.connect((self.host,self.sendport))
    self.s.sendall(bytes(message, 'UTF-8'))
    self.s.close()                   
    self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    self.s.connect((self.host,self.receiveport))
    data = (self.s.recv(1024).decode("utf-8"))    
    self.s.close()    
    return data  
    
    
      
    

  