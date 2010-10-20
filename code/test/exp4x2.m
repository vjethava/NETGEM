function [] = exp4x2(nBins, nBins2)
%%% EXP4x0 This experiment does the synthetic experiment p value
%%% comparison. 
% 
% Version: This version does the H0 vs H1 p-value histogram 
%
% Expects: 
% -------------    
% nBins:       number of histogram bins (8)
% nBins2:      number of bins for exponential fitting ( if
%              unspecified  same as nBins)
% 
%% Filename: exp4x0.m
%% Description: 
%% Author: Vinay Jethava
%% Created: Mon Aug 23 11:43:48 2010 (+0530)
%% Last-Updated: Tue Sep  7 15:16:29 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 112
%% URL: 
%% Keywords: 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
    setpaths();
    % load Aug23;   
    % nW = length(W); 
    % if nargin < 1
    %     nBins = 8;     % use 8
    % end
    % if nargin < 2
    %     nBins2 = 10; % use 10 
    % end        
    % [hboth, figboth] = inner4x2_2x0(synthRes.sE , nBins, nBins2, 'exp4x2fig1'); 
    % close(figboth) ; 

    % % Get true classification based on hypothesis
    % [trueClass] = inner4x2_1x0(synthRes, tLowPct, nW); 
    % % divide the scores into the two classes 
    % sortedScore = inner4x2_3x0(synthRes, trueClass);  
    % % make appropriate binning
    % [hE0, fE0] = inner4x2_2x0(sortedScore.sE0, 10, 10, 'exp4x2fig2'); 
    % [hE1, fE1] = inner4x2_2x0(sortedScore.sE1, 10, 10, 'exp4x2fig3'); 
    % % [hH0, fH0] = inner4x2_2x0(sortedScore.sH0, 10, 10, 'H0', 0); 
    % % [hH1, fH1] = inner4x2_2x0(sortedScore.sH1, 10, 10, 'H1'); 
    
    % save Aug30;
    % keyboard; 
    
    %% make the histogram of p-values
    load Aug30; 
    muB =  -1/0.64; 
    mu0 =  -1./hE0.pE(1);
    mu1 =  -1./hE1.pE(1);

    Px0 = @(x) ( exppdf (x , mu0) );
    Px1 = @(x) ( exppdf (x , mu1) );
    Fx0 = @(x) ( expcdf (x , mu0) );
    Fx1 = @(x) ( expcdf (x , mu1) );
    Rx = @(x) (Px0(x) ./ Px1(x) );
    % h2 = figure; 
    % y = [0:0.1:6]'; 
    % hold on; grid on; 
    % plot(y, Px1(y) , 'r--', 'LineWidth', 2); 
    % plot(y, Px0(y) , 'b-', 'LineWidth', 2);
    % legend2 = {'P_0(x)'; 'P_1(x)'};     
    % legend(legend2, 'Location', 'NorthEast');  
    % saveas(h2 , 'exp4x2fig4a', 'fig'); 
    % print(h2 , '-dpng', 'exp4x2fig4a'); 
    % close(h2); 
   
    % h3 = figure; 
    % plot(y, Rx(y) , 'g-.', 'LineWidth', 2); 
    % legend('R(x)', 'Location', 'SouthEast');
    % grid on; 
    % saveas(h3 , 'exp4x2fig4b', 'fig'); 
    % print(h3 , '-dpng', 'exp4x2fig4b'); 
    % close(h3); 
    hist( 1 - Fx1(sortedScore.sE1), 10);
    keyboard; 
end

% find the true and predicted classes (assumes tLow == tHigh)
function [ trueClass] = inner4x2_1x0(synthRes, tLowPct, nW)
   trueClassH =  synthRes.trH > (tLowPct * nW)/100.0;
   trueClassE = synthRes.trE > (tLowPct * nW)/100.0; 
   trueClass = struct('trueClassE', trueClassE, ...
                      'trueClassH', trueClassH);
   
end 

   % predClassH = synthRes.sH < criticalScore;
   % predClassE = synthRes.sE < criticalScore;
   % predClass = struct('predClassE', predClassE, 'predClassH', ...
   %                    predClassH);

% divide the data into two based on true classes
function [sortedScore] = inner4x2_3x0(synthRes, trueClass) 
    sE0 = []; sH0 = [];
    sE1 = []; sH1 = [];
    for i=1:length(synthRes.sE) 
        if trueClass.trueClassE(i) == 0
            sE0 = [sE0 ; synthRes.sE(i)];
        else
            sE1 = [sE1 ; synthRes.sE(i)];
        end
    end
    for i=1:length(synthRes.sH) 
        if trueClass.trueClassH(i) == 0
            sH0 = [sH0 ; synthRes.sH(i)];
        else
            sH1 = [sH1 ; synthRes.sH(i)];
        end
    end
    sortedScore = struct('sE0', sE0, 'sH0', sH0, ...
                         'sE1', sE1,  'sH1', sE1); 
end

% do the exponential fit for given data 
function [histData, figHandle] = inner4x2_2x0(score, nc, ne, ...
                                              figName, plotExp)
    if nargin < 3
        ne = nc 
    end
    
    if nargin < 4
        figName = ''; 
    end
    
    if nargin < 5 
        plotExp = 1;
    end
    
    [freqC, binC] = hist(score, nc); 
    [freqE, binE] = hist(score, ne); 
    [pE] = polyfit(binE, log(freqE), 1); 
    histData = struct('freqC', freqC, 'binC', binC, ...
                      'freqE', freqE, 'binE', binE, ...
                      'pE', pE) ; 
    legends = {}; 
    figHandle = figure; 
    bar(binC' , freqC');
    grid on; set(gca, 'FontSize', 15);
    legends{1} = 's(e)';  
    if plotExp == 1  
        hold on; 
        plot(binE, exp(pE(1).*binE + pE(2)), 'r--', 'LineWidth', 2); 
        legends{2} = sprintf('P(s) \\propto e^{%0.2g * s}', pE(1) ); 
    end
    legend(legends, 'Interpreter', 'tex');
    xlabel('Change Score s(e)'); 
    ylabel('Number of edges');
    if length(figName) > 0  
        print('-dpng', figName);
    end
end

% ROC plot based on exp3x2
function [figHandle] = inner4x2_4x0() 
    figHandle = []; 
end 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% exp4x0.m ends here
