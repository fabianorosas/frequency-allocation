#!/bin/sh

mkdir graficos
mkdir logs

for i in {0..9}; do
    awk '/INFO: [0-9]/ { print $2" "$3 }' ap$i.log > ap$i;
    mv ap$i.log logs;
    
    cat<<EOF > plot$i.gnu
#Gnuplot

reset
unset key
set title "Interferencia - ap$i"
set ylabel "interferencia"
set xlabel "ciclos"
set xtics 2
set yrange [0:0.1]
set xrange [0:5]
set term post eps enhanced color
set out  'ap$i.eps'
#plot 'ap$i'
plot 'ap$i' with lines

EOF

    gnuplot < plot$i.gnu;
    rm plot$i.gnu;
    epstopdf ap$i.eps;
    rm ap$i.eps
    mv ap$i.pdf graficos;
    rm ap$i
done 
