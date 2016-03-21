from automatureapi import AutomatureAPI
import sys
import webbrowser     
from time import time


s=AutomatureAPI() 

# sys.argv[1]  :  is the name of context var that holds url 
# sys.argv[2]  :  is the response time

start_time = time()
webbrowser.open_new_tab(s.getContextVar(sys.argv[1]))
end_time = time()

s.alterContextVar(sys.argv[2],str(end_time-start_time))

