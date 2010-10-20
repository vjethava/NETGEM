function [p0, mu, pExp] = getFunctionalDataStats()
%% GETFUNCTIONALDATASTATS generates the functional category stats
%% based on the gc_graph matrix used in experiment 2   
%
%
% Returns:
% ---------
% p0 = The probability vector for generating (random) number of functional
%      categories for a gene
% mu = Average number of functional categories for a gene
% p  = The exponential fit to the 
%
%
% Parameter:
% -----------
% N_G
%
addpath ../data/exp2; 
warning off; 
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../lib/HMM;
load cats.mat; 
r = sum(gc_graph, 2); % to avoid zeros in count
mu = mean(r); 
[n, bins] = hist(r, [1:15]);
n0 = n;
n = max(n, 1); % to avoid -inf in log()
[p] = polyfit(bins, log(n), 1);  
mylegend = {}; 
semilogy(bins, n, 'b-*', 'MarkerSize', 6, 'LineWidth', 3); 
mylegend{1} = 'MIPS data';
set(gca, 'FontSize', 15);
grid on; 
hold on; 
plot(bins, exp(p(1).*bins + p(2)) , 'r--', 'LineWidth', 2); 
set(gca, 'XTick', 1:15);
xlabel('number of functional categories for gene (N_F)', 'FontSize', 15);
ylabel('number of genes (N_G)', 'FontSize', 15);
mylegend{2} = sprintf('N_G = e^{(%0.2g * N_F + %0.2g)}', p(1), p(2));
legend(mylegend);
saveas(gcf, 'funcStats', 'fig');
print('-dpng', 'funcStats');
p0= mk_stochastic(n); 
end


