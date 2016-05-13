#!/bin/bash
labels=('SRBCT' 'Prostate_Tumor' 'Lung_Cancer' 'Leukemia2' 'Leukemia1' 'DLBCL' 'Brain_Tumor2' 'Brain_Tumor1' '14_Tumors' '11_Tumors' '9_Tumors');

chr_size=('2308' '10509' '12600' '11225' '5327' '5469' '10367' '5920' '15009' '12533' '5726');


x=0
while [ $x -lt ${#labels[@]} ]
do
  echo "Running GA ${labels[$x]}"

  hadoop jar target/dist-ga-01.00-job.jar edu.ibu.ga.mapreduce.job.GaJob -conf src/main/resources/ga-conf.xml -Dga.mapreduce.label=${labels[$x]} -Dga.mapreduce.chromosome.length=${chr_size[$x]} > log/${labels[$x]}.log

  x=$(( $x + 1 ))
done

