#!/bin/sh

mkdir -p graphs
mkdir -p logs

for i in {0..9}; do
    awk '/INFO: [0-9]/ { print $2" "$3 }' ap$i.log > ap$i;
    mv ap$i.log logs;
    
    cat<<EOF > plot$i.gnu
#Gnuplot

reset
unset key
set title "Interference - ap$i"
set ylabel "interference"
set xlabel "cycles"
set xtics 2
# ranges still need some tuning
set yrange [0:0.1]
set xrange [0:5]
set term post eps enhanced color
set out  'ap$i.eps'
plot 'ap$i' with lines

EOF

    gnuplot < plot$i.gnu;
    rm plot$i.gnu;
    epstopdf ap$i.eps;
    rm ap$i.eps
    mv ap$i.pdf graficos;
    rm ap$i
done 
