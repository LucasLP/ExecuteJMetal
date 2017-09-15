<html>
<h1> ExecuteJMetal </h1>
Bash Script to help statistical tests of Multi-Objective Evolutionary Algorithms.<br>
This Scripts use <a href="https://jmetal.github.io/jMetal/">JMetal Framework 5.2</a>.<br>
Only for Linux Users.<br>

  1. Use the src in additional files to programming your JMetalMain<br>
 Look at the end of this files for more informations.<br>
  
  2. You need <b>LaTeX</b> - For .tex files<br>
sudo apt install texlive-latex-base<br>
sudo apt install texlive-generic-extra<br>
sudo apt-get install texlive-latex-extra<br>

  3. You need <b>R Language</b> - for .R files<br>
<code>
sudo apt-get update<br>
sudo apt-get install r-base<br>
sudo apt-get install r-base-dev<br>
</code>
    3.1 (optinal) Install <a href="http://iridia.ulb.ac.be/irace/">Irace</a><br>
       R<br>
        R> install.packages("irace")<br>
	 3.2 (optional) Install <a href="https://plot.ly/r/getting-started/#installation">Graphic Library in R</a><br>
		R<br>
		 R>install.packages("plotly")<br>

  4. Configure Execute.sh<br>
    4.1 Set Benchmark<br>
    4.2 Set Algorithm and its Tag (tag is the name of data files)<br>
    4.3 Set Execution line<br>
    4.4 Set Comparative Line<br>
    
  5. Execute<br>
sh Execute.sh<br>

  6. Analyse the results in folder: experiment/MyExperiments/Result_$experimentName_$benchmark/

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
<b>Source:</b> How to read input parameters to execute JMetal with this scripts.<br>
"MyRunne.java" is a single runner of algorithm configured by "Configuration.java"<br>
"ExecuteExperiment.java" can execute several runs and get all data for statistical test<br>
"Configuration.java" configure all tests, algorithms and benchmark<br>
"JMetalMain.java" read first argument to configure how it will execute<br><br>
<b>Example of execution: </b><br>
<code>$ java -jar JMetal.java --statistic ZDT --algorithm NSGAII --algorithm MOEAD --tag test</code><br>
It will execute NSGAII and MOEAD algorithm in benchmark ZDT, and MOEAD will save its data in "test" folder.<br>
<br>
<code>$ java -jar JMetal.java --single-run ZDT1 --algorithm MOEAD</code><br>
It will execute MOEAD in ZDT1 instance<br>
<br>
<code>$ java -jar JMetal.jar --comparative UF --algorithm MOEAD --algorithm MOEADDRA</code><br>
It will generate latex and R files of statistical comparisons.<br>


=======================================<br>
<b>Structure of tests</b><br>
  <ul>
    <li>Execute.sh</li>
    <li>QualityIndicator.sh</li>
    <li>JMetal.jar <i>Put You'r compilation here</i></li>
    <li>Additional/ </li>
      <ul>
      <li>renameAll.sh</li>
      <li>renameScript.sh</li>
      <li>src/</li>
        <ul>
        <li>Configuration.java</li>
        <li>ExecuteExperiment.java</li>
        <li>JMetalMain.java</li>
        <li>MyRunner.java</li>
        <li>my-algorithms-jmetal4.2/</li>
				<ul>
				<li>MOEAD_DRA_UCB/</li>
				</ul>
        </ul>
      </ul>
    <li>lib/ <i>Lib of you'r project</i></li>
    <li>experiment/</li>
    <ul>
      <li>MyExperiments/</li>
      <ul>
        <li>data/</li>
      </ul>
    </ul>
 </ul>
</html>
