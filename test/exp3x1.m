function [result] = exp3x1(tLowPct, tHighPct, W, N, nH, T)
%%% EXP3x0 This file compare ROC plots for the estimation of
%%% functional category Q vs edge Q under the short time series
%%% data
%
%  Do not plot the functional category - do multiple for short time
%  series.    
% 
%% Filename: exp3x0.m
%% Description:
%% Author: Vinay Jethava
%% Created: Mon Aug 16 17:08:26 2010 (+0530)
%% Last-Updated: Mon Sep  6 16:53:17 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 59
%% URL:

%%
%%% Code:
    setpaths();
    % if nargin < 6
    %     T = 8;
    % end
    % if nargin < 4
    %     N = 1000;   % number of genes
    % end
    % if nargin < 5
    %     nH = 200;    % total number of functional classes
    % end
    % if nargin < 3
    %     W = [-2 -1 0 1 2];
    % end
    % p = 0.01 ; % probability of generating edges
    % [synthRes] = getQeQh(N, nH, T, p, W, tLowPct, tHighPct);
    % %% the thesholds at which to test the hypothesis
    % thresholds = [tLowPct tHighPct];
    % np1 = ceil(sqrt(size(thresholds, 1)));
    % np2 = np1 - 1;
    % if (np1*(np2) < size(thresholds, 1))
    %     np2 = np2 + 1;
    % end
    
    load Aug23; 
    
    %% draw the figure
    h = figure;
    ll = get(gcf, 'CurrentAxes');
    set(gca, 'FontSize', 15);
    set(gcf, 'DefaultAxesColorOrder',[1 0 0;0 1 0;0 0 1], ...
             'DefaultAxesLineStyleOrder', '-*|-+|-o|-x|-s|-d|-p');
    markers = {'b-*';'g-+';'r-o';'m-p';'c-s'};
    legends = {};
    nestedPlotter(synthRes.trE, synthRes.sE, 1);
    xlabel('False Positive Rate');
    ylabel('True Positive Rate');
    saveas(h, 'exp3x1fig2a.fig');
    print('-dpng', 'exp3x1fig2a');

% hold on;
    % nestedPlottaer(synthRes.trH, synthRes.sH, 3);
    % legend(legends, 'Location', 'SouthEast');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% nested fucntions   %%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    function res = nestedPlotter(t3, s3, count)
        disp('Hello Nested World');
        for t = 1:size(thresholds,1)
            stats = [];
            fpr =[];
            tpr =[];
            tLow = thresholds(t, 1);
            tHigh = thresholds(t, 2);
            assert(tLow <= tHigh);
            cutoff = [0.0:0.2:6.0]';
            for s=1:length(cutoff)
                cstat =  changeScoreHypoTest(cutoff(s), tLow, ...
                                             tHigh, '', t3, s3);
                stats = [stats; cstat];
                fpr = [fpr; cstat.fpr];
                tpr = [tpr; cstat.tpr];

            end
            % subplot(np1, np2, t);
            plot(fpr, tpr, markers{count}, 'LineWidth', 3 , 'MarkerSize', 6);
            grid on;
            xlabel('False Positive Rate');
            ylabel('True Positive Rate');
            for s=1:length(cutoff)
                m = sprintf('%2.1f', cutoff(s));
                text(fpr(s)+0.005, tpr(s)-0.02, m, ...
                'HorizontalAlignment','left', 'FontSize', 6);
            end
            if(count == 1) 
                legends{1} = sprintf('Edge (e)');
            else
                legends{2} = sprintf('Functional Category (h)'); 
            end    

            res = count;
        end
    end
    legend(legends, 'Location', 'SouthEast');
end

% function [result] = getQeQh(N, nH, nT, p, W, tLow , tHigh)
%     setpaths(); 
% result = struct('trH', [], 'sH', [], 'trE', [] ,'sE', [] , ...
%                 'sInfH', [], 'sInfE', [], 'trInfH', [], 'trInE', []);

% nHav = 3;   % avg number of functional classes for a gene
% S = [0];    % number of knocked out genes in strain

% disp('Allocating G');
% nS = length(S);
% nW = length(W);
% keyboard; 
% G = erdosRenyi(N, p);
% G = triu(G);
% [I, J, S1]=find(G); %find(triu(G));
% E = [I J];
% nE = size(E, 1);
% fprintf(1, 'Allocating A nE = %d\n', nE);
% A = rand(N, nH) < (nHav)/nH; % gene-class membership matrix
% for i=1:N
%     if(nnz(A(i, :)) == 0) % at least one class
%         j = randint(1, 1, [1 nH]);
%         A(i, j) = 1;
%     end
% end
% D = A(I, :) + A(J, :);
% for i=1:nE
%     D(i, :) = dirichletrnd(D(i, :));
% end
% D = mk_stochastic(D); % the mixture ratios
% clear A;
% disp('Allocating H');
% H = zeros(nW, nW, nH);
% trH = zeros(nH, 1);
% result.trH = trH;
% for i=1:nH
%     fprintf(2, 'initH %d of %d\n', i, nH);
%     hypoClass = floor(2.0 * rand());
%     if (hypoClass == 0)
%         currTracePct = rand() * tLow;
%     else
%         currTracePct = tHigh + rand() * (100 - tHigh);
%     end
%     H(:,:,i) = getQwithTracePct(currTracePct, nW);
%     trH(i) = trace(H(:, : , i));
% end
% result.trH = trH;
% disp('Starting to sample');
% pw0 = (1.0/nW)*ones(nW, 1);
% We = zeros(nE, nT);
% trE = zeros(nE, 1);
% for e=1:nE
%     fprintf(1, 'edge %d of %d\n', e, nE);
%     cQ = zeros(nW, nW);
%     for j=1:nH
%         cQ = cQ + D(e, j)*H(:, :, j);
%     end
%     trE(e) = trace(cQ);
%     currW= mc_sample(pw0, cQ, nT);
%     We(e, :) = W(currW);
% end
% result.trE = trE;

% %% Perfect inference of W gives
% disp('Computing scores');
% sE0 = zeros(nE, 1);
% sH0 = zeros(nH, 1);
% nH0 = sum(D, 1)';
% for n=1:nE
%     sE0(n) = getScore(We(n, :));
% end
% sH0 = (D' * sE0) ./ nH0 ;
% result.sE = sE0;
% result.sH = sH0;
% save result result;
% end



%%% exp3x0.m ends here
