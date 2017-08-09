# ExecuteJMetal
Bash Script to help statistical tests of Multi-Objective Evolutionary Algorithms.
Only for Linux Users.

  1. Structure of tests<br>
    Execute.sh<br>
    QualityIndicator.sh<br>
    JMetal.jar<br>
    experiment/MyExperiments/data/<br>
 
  2. You need LaTeX - For .tex files<br>
sudo apt install texlive-latex-base<br>
sudo apt install texlive-generic-extra<br>
sudo apt-get install texlive-latex-extra<br>

  3. You need R Language - for .R files<br>
sudo apt-get update<br>
sudo apt-get install r-base<br>
sudo apt-get install r-base-dev<br>
    3.1 (optinal) Install Irace<br>
       R<br>
        R> install.packages("irace")<br>

  4. Configure Execute.sh<br>
    4.1 Set Benchmark<br>
    4.2 Set Algorithm and its Tag (tag is the name of data files)<br>
    4.3 Set Execution line<br>
    4.4 Set Comparative Line<br>
    
  5. Execute<br>
sh Execute.sh<br>


=======================================<br>
<b>In Additional Folder</b><br>
<b>Script:</b> renameAll.sh<br>
Call the renameScript.sh for rename data files<br>
It be useful if you had old versions of JMetal and update, then, <br>
all of your data files will be for example "FUN.0", and the new versions use "FUN0.tsv"<br>
If you have this problem, set in this script algorithm and instance to rename.<br>
<br>
<b>Script:</b> renameScript.sh<br>
Rename All files in ".0 .1 .2  ...  .max" to ".tsv".<br>
<br>
<br>
<b>Source:</b> How to read input parameters to execute JMetal with this scripts.



