package org.uma.jmetal.util.experiment.component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

/**
 *  <b>Work only in problems with 2 and 3 objetives</b>
 * Need: install.packages("scatterplot3d", repos="http://R-Forge.R-project.org")
 * @author Lucas Prestes <lucas.prestes.lp@gmail.com> 
 */
public class GenerateScatterPoints implements ExperimentComponent{
    Experiment<?,?> experiment;
    List<String> tags;
    

    public GenerateScatterPoints(Experiment<?, ?> experiment) {
        this.experiment = experiment;
        tags = new ArrayList<>();
        for (ExperimentAlgorithm<? extends Solution<?>, ? extends Object> experimentAlgorithm : this.experiment.getAlgorithmList()) {
            tags.add(experimentAlgorithm.getAlgorithmTag());
        }
    }
    
    
    @Override
    public void run() throws IOException {
        String script = "library(scatterplot3d)\n";
        script += "#install.packages(\"scatterplot3d\", repos=\"http://R-Forge.R-project.org\")\n";
        script += generate2Dfunction();
        script += generate3Dfunction();
        script += "\n#############################";
        script += "\npar(mfrow=c(2,2)) #each page has 2x2 plots\n";
        for (ExperimentProblem<? extends Solution<?>> experimentProblem : experiment.getProblemList()) {
            if(experimentProblem.getProblem().getNumberOfObjectives() == 2){
                script += "\nobjectivePoints(\""+experimentProblem.getProblem().getName()+"\")";
            }else if(experimentProblem.getProblem().getNumberOfObjectives() == 3){
                script += "\nobjective3DPoints(\""+experimentProblem.getProblem().getName()+"\")";
            }
        }
        try (OutputStream os = new FileOutputStream(experiment.getExperimentBaseDirectory() + "/plot_points.R")) {
            PrintStream ps = new PrintStream(os);
            ps.print(script);
            ps.close();
        }
    }
    
    
    private String generate2Dfunction(){
        String str = 
"objectivePoints <- function(instanceName){\n" +
"	pareto <- paste(\"pareto_fronts/\",instanceName,sep=\"\")\n" +
"	instance<- paste(instanceName,\"/FUN0.tsv\",sep=\"\")\n" ;
        int i = 1;
        for (String tag : tags) {
            str+=
"	algorithm"+i+" <- read.table(paste(\"../data/"+tag+"/\",instance,sep=\"\"), header=FALSE, sep=\" \")\n" ;
            i++;
        }
        str+=
"	pf <- read.table(paste(pareto,\".pf\",sep=\"\"), header=FALSE, sep=\"\")\n\n" +
"	colors <- c(\"red\", \"blue\",\"black\",\"orange\",\"green\", \"brown\", \"deepskyblue\", \"gray60\",\"yellow\")\n\n" +
"	plot(pf[,1],pf[,2] ,col=colors[1], pch='.' , axes=FALSE, ann=FALSE)\n" ;
        i = 1;
        for (String tag : tags) {
            str+="	points(algorithm"+i+"[,1],algorithm"+i+"[,2], col=colors["+(i+1)+"], pch='.')\n";
            i++;
         }
        str+= "\n" +
"	# Make x,y axis \n" +
"	axis(1, at=c(0.0,0.25,0.5,0.75,1.0))\n" +
"	axis(2, at=c(0.0,0.25,0.5,0.75,1.0))\n\n" +
"	# Create a title bold/italic font\n" +
"	title(main=paste(\"Objective values for \", instanceName,sep=\"\"), font.main=4)\n\n" +
"	# Label the x and y axes\n" +
"	title(xlab= \"Objective 1\")\n" +
"	title(ylab= \"Objective 2\")\n" +
"	# Create a legend\n" +
"	legend(\"topright\", \n" +
"			c(\"Pareto set\"";
        for (String tag : tags) {
            str+=", \""+tag+"\"";
        }
        str+= "), \n" +
"			cex=0.5, \n" +
"			col=colors, \n" +
"			pch='.',\n" +
"			lty=1);\n" +
"}\n";
        return str;
    }
    
    
    private String generate3Dfunction(){
        String str=
"objectivePoints3D <- function(instanceName){\n" +
"	pareto <- paste(\"pareto_fronts/\",instanceName,sep=\"\")\n" +
"	instance<- paste(instanceName,\"/FUN0.tsv\",sep=\"\")\n" ;
        int i = 1;
        for (String tag : tags) {
            str+=
"	algorithm"+i+" <- read.table(paste(\"../data/"+tag+"/\",instance,sep=\"\"), header=FALSE, sep=\" \")\n" ;
            i++;
        }
        str+=    
"	pf <- read.table(paste(pareto,\".pf\",sep=\"\"), header=FALSE, sep=\"\")\n" +
"\n" +
"	colors <- c(\"red\", \"blue\",\"black\",\"orange\",\"green\", \"brown\", \"deepskyblue\", \"gray60\",\"yellow\")\n" +
"\n" +
"	s3d <- scatterplot3d(pf[,1],pf[,2],pf[,3], pch='.', angle=45, color=colors[1] ,# axes=FALSE, ann=FALSE  ,col=colors[1]\n" +
"		main=paste(\"Valores dos objetivos para \", instanceName,sep=\"\"), font.main=4, 	# Create a title bold/italic font\n" +
"		xlab= \"Objetivo 1\",	# Label the x  y z axes\n" +
"		ylab= \"Objetivo 2\",\n" +
"		zlab= \"Objetivo 3\",\n" +
"		box=FALSE);\n" ;
        i = 1;
        for (String tag : tags) {
            str+="	s3d$points3d(algorithm"+i+"[,1],algorithm"+i+"[,2],algorithm"+i+"[,3], col=colors["+(i+1)+"], pch='.')\n";
            i++;
         }
str+= "\n\n" +
"	# Create a legend\n" +
"	legend(\"topright\", \n" +
"			c(\"Pareto set\"";
                        for (String tag : tags) {
                            str+=", \""+tag+"\"";
                        }
                        str+= "), \n" +
"			cex = 0.5,\n" +
"			col=colors, \n" +
"			pch='.',\n" +
"			lty=1);\n" +
"}";
        return str;
    }
}
