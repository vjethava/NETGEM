%%% exp5x0.m --- 
%% 
%% Filename: exp5x0.m
%% Description: This file presents the experiment for low T (T=8)
%% Author: Vinay Jethava
%% Created: Thu Sep  2 10:18:49 2010 (+0530)
%% Last-Updated: Sat Sep  4 13:27:35 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 8
%% URL: 
%% Keywords: 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
function [res5x0] = exp5x0()
    setpaths(); 
    N = 1000; 
    nH = 200; 
    W = [-2 -1 0 1 2];
    T = 8; 
    tLims = [5 5;
             10 10; 
             20 20; 
             50 50];
    p = 0.01; 
    simVec = [];
    for i=1:size(tLims, 1) 
        tLow = tLims(i, 1); 
        tHigh = tLims(i, 2); 
        simR = getQeQh(N , nH, T, p, W, tLow, tHigh);
        simVec = [simVec; simR];
    end
    save exp5x0 simVec; 
    res5x0 = []; 
    % h = figure;
    % ll = get(gcf, 'CurrentAxes');
    % set(gca, 'FontSize', 15);
    % set(gcf, 'DefaultAxesColorOrder', ...
    %          [1 0 0;0 1 0;0 0 1], ...
    %          'DefaultAxesLineStyleOrder', ...
    %          '-*|-+|-o|-x|-s|-d|-p');
    % markers = {'b-*';'g-+';'r-o';'m-p';'c-s'};
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% exp5x0.m ends here

