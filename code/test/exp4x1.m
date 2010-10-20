function [] = exp4x1(nBins, nBins2)
%%% EXP4x0 This experiment does the synthetic experiment p value
%%% comparison. 
% 
% Expects: 
% -------------    
% nBins:       number of histogram bins
% nBins2:      number of bins for exponential fitting ( if
%              unspecified  same as nBins)
% 
%% Filename: exp4x0.m
%% Description: 
%% Author: Vinay Jethava
%% Created: Mon Aug 23 11:43:48 2010 (+0530)
%% Last-Updated: Sat Sep  4 13:03:32 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 44
%% URL: 
%% Keywords: 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
    load Aug23;
    setpaths(); 
    if nargin < 1
        nBins = 10;
    end
    if nargin < 2
        nBins2 = nBins;
    end        
    legends = {}; 
    [freqC, binC] = hist(synthRes.sE, nBins); 
    [freq10, bin10] = hist(synthRes.sE, nBins2); 
    % [p] = polyfit(binC, log(freqC), 1); 
    [p] = polyfit(bin10, log(freq10), 1); 
    bar(binC', freqC'); 
    set(gca, 'FontSize', 15);
    hold on; 
    grid on; 
    legends{1} = 's(e)';
    binO = [1:10]; 
    plot(binO, exp(p(1).*binO + p(2)), 'r--', 'LineWidth', 2); 
    legends{2} = sprintf('P(s) \\propto e^{%0.2g * s}', p(1) ); 
    legend(legends, 'Interpreter', 'tex');
    xlabel('Change Score s(e)'); 
    ylabel('Number of edges');
    
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% Nested functions used - minimal data passing 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
   saveas(gcf, 'exp4x0fig4', 'fig'); 
   saveas(gcf, 'exp4x0fig4', 'png'); 
end

function [] =setpaths()
    warning off;
    addpath ../lib/KPMtools;
    addpath ../lib/KPMstats;
    addpath ../netlab3.3;
    addpath ../lib/HMM;
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% exp4x0.m ends here
