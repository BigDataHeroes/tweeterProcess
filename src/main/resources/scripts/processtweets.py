#!/usr/bin/python

import json
import sys
import os

reload(sys)
sys.setdefaultencoding('utf-8')
input = sys.argv[1]
outputF = sys.argv[2]
inFile = input
with open(inFile) as fr:                      
  data = json.load(fr)

fw = open(outputF, "w")
sys.stdout = fw

for t in data["statuses"]:
    text = t["text"]
    print(''.join(text.splitlines()))  

os.remove(inFile)
