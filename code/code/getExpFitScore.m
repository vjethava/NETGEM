function [changeScore, freqC, binC] = getExpFitScore(vW, nBins, fractionToSee) 
%%% getExpFitScore.m --- 
%% 
%% Filename: getExpFitScore.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Wed Jan  6 23:18:21 2010 (+0530)
%% Version: 
%% Last-Updated: Wed Jan  6 23:21:41 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 8
%% URL: 
%% Keywords: 
%% Compatibility: 
%% 
if nargin < 3
    fractionToSee = 0.05;
end

if nargin < 2
    nBins = 6; 
end
totVarianceSum = sum(vW);
[freqC, binC] = hist(vW, nBins); 
[p, s] = polyfit(binC, log(freqC), 1); 
est = exp(p(1)*binC + p(2))';
changeScore = log(fractionToSee)/p(1); 
keyboard; 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getExpFitScore.m ends here
