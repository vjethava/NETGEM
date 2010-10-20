clear; 
load ../results/results_pp.mat; 
load ../results/exp2ws5c.mat;
load ../data/dataset8/cats.mat;
fractionToSee = 0.05;
totVarianceSum = sum(S2.vW);
%%% Plot the variation of results 
nBins = 6;
[freqC, binC] = hist(S2.vW, nBins); 
[p, s] = polyfit(binC, log(freqC), 1); 
est = exp(p(1)*binC + p(2))';
hold; 
hist(S2.vW, nBins); 
plot(binC, est, 'r-*', 'LineWidth', 5, 'MarkerSize', 10);
xlabel('Score', 'FontSize', 20); 
ylabel('Number of edges', 'FontSize', 20); 
title('Histogram plot of s_{change} for the edges','FontSize', 20); 
% cumSum = 0.0;
% index = 0; 
% while (cumSum < totVarianceSum*fractionToSee) 
%     index = index + 1; 
%     cumSum = cumSum + S2.vW(index); 
% end
changeScore = log(fractionToSee)/p(1); 
% cytowrite2('t3', S2, G2, changeScore);
for i=1:length(H2)