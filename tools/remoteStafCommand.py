import subprocess,re,sys
from subprocess import CalledProcessError, check_output

try:
    	output = subprocess.check_output(["staf", "%s" % (sys.argv[1]) , "PROCESS", "START", "SHELL", "COMMAND", "%s" % (sys.argv[2]), "WAIT", "RETURNSTDOUT", "STDERRTOSTDOUT"])
        result = re.findall(r'Data\s*:\s*((?:[^\n]*(?:[\r\n]+(?!\s*}))?)+)', output, re.DOTALL)[0]
        print result
except CalledProcessError as e:
        print(e.returncode)
