# ExecuteJMetal
Bash Script to help statistical tests of Multi-Objective Evolutionary Algorithms.

  1. Only for Linux Users.
 
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
    4.2 Set Algorithm and their Tag (tag is the name of data files)<br>
    4.3 Set Execution line<br>
    4.4 Set Comparative Line<br>
    
  5. Execute<br>
sh Execute.sh<br>
