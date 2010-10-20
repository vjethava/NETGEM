function [idx, result]=hypothesisLowVsHighTraceQTest(tLowPct, tHighPct, W, T, N)
%%% [result]=hypothesisLowVsHighTraceQTest(tLowPct, tHighPct, W, T)
% 
%% Filename: hypothesisLowVsHighTraceQTest.m
%% Description: This file tests the hypothesis i.e.
%               H0: Q with trace <= tLow 
%               H1: Q with trace >= tHigh
%               using the test-statistic s(e) and draws the Q-Q
%               plots and ROC plots
%
% Expects: 
% ------------
% W:            the state space of possible weights 
% tLowPct:      pct of wt in trace value corresponding to H0
% tHighPct:     pct of wt in trace value corresponding to H1
% 
% Returns:
% ------------
% result:       structure containing...
%       scoreVec:       change scores
%       classVec:       the hypothesis H0 or H1 from which generated
%       traceVec:       the random traces generated per hypothesis
%       wtMat:          the observed weights wtMat(n, :) for n^th edge
% 
%% Author: Vinay Jethava
%% Created: Thu Aug 12 12:28:51 2010 (+0530)
%% Last-Updated: Thu Aug 12 18:11:40 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 22
%% URL: 
%% Keywords: 
%
%%% Code:
warning off; 
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../code;
if nargin < 5
    N = 1000;        % number of edges
if nargin < 4  
    T = 8;      % default number of observations
    if nargin < 3
        W = [-2 -1 0 1 2];      % default weight state space
        if nargin < 2
            tLowPct = 50; 
            tHighPct = 50; 
        end
    end
end
end
nW = length(W); % number of weights
tLow = tLowPct * nW / 100.0;
tHigh = tHighPct * nW / 100.0;

pW =  (1.0/nW) * ones(nW, 1);   % initial (equal) probability of
                                % weight states
result = struct('classVec', zeros(N, 1), 'traceVec', zeros(N, 1), ...
                'scoreVec', zeros(N, 1), 'wtMat', zeros(N, T)); 
assert((tLow >= 0) && (tHigh <= nW) && (tLow <= tHigh)); 
result.classVec = floor(2.0*rand(N, 1)); 
for n=1:N
    fprintf(2, 'Edge %d of %d\n', n , N); 
    if(result.classVec(n) == 0)          % choose the trace in H0
        currTracePct = rand() * tLowPct; 
    else                        % choose the trace in H1
        currTracePct = tHighPct + rand() * (100.0 - tHighPct); 
    end
    currTrace = currTracePct * nW /100.0; 
    Q = getQwithTracePct(currTracePct, nW); 
    currState = W(mc_sample(pW, Q, T)); 
    currScore = getScore(currState); 
    result.traceVec(n) = currTrace; 
    result.scoreVec(n) = currScore; 
    result.wtMat(n, :)  = currState; 
end
idx = floor(rand()*10000);
fprintf(2, 'Current experiment index: %d', idx); 
m = sprintf('qTrData/%d.mat', idx);
save(m, 'result', 'W', 'nW', 'T', 'N', 'tLowPct', 'tHighPct');
cinst = struct('idx', idx, 'W', W, 'nW', nW, 'tLowPct', tLowPct, 'tHighPct', tHighPct, 'T', T, 'N', N);   
if(exist('qTrLog.mat', 'file'))
    load qTrLog.mat;  
    qTrLog(idx) = cinst;
else
    qTrLog = containers.Map(idx , cinst); 
end
save qTrLog.mat qTrLog; 

