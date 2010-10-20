%%% Comparison of test statistic s(e) at various simplified Q^\alpha
function [result]=testQ(T)
warning off; 
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../code;
W = [-2 -1 0 1 2];
nW = length(W); 
alpha =[0.1];
if nargin < 1
    % T = 100; 
    T = [5  10 100 1000 1000];
end
R = zeros(length(alpha), length(T)); 
pW = (1.0/nW) * ones(nW, 1); 
nsim = 200; 
h = figure;
hold on; 
grid on; 
ll = get(h, 'CurrentAxes'); 
set(ll, 'FontSize', 15, 'XTick', 0:0.1:1.0);
set(h, 'DefaultAxesColorOrder',[1 0 0;0 1 0;0 0 1], ...
     'DefaultAxesLineStyleOrder', '-*|-+|-o|-x|-s|-d|-p');
markers = {'b-*';'g-+';'r-o';'m-p';'c-s'}; 
newLgnd = {};   
result = struct('R', [], 'score', [], 'trace', [], 'alpha', [], 'weights', []);
se_count = 0; 
se_trace = []; se_alpha = []; se_weights = []; se_score = []; 

for ti = 1:length(T)
    if (T(ti) >= 50)
        N = 100;
    else
        N = nsim;
    end
    for ai = 1:length(alpha)
        for n=1:N
            fprintf(1, 'simulating: %d for alpha: %g\n', T(ti), alpha(ai));
            calpha = alpha(ai);
            Q = calpha * ones(nW, nW);
            for i=1:nW
                Q(i, i) = 1-calpha;
            end
            %%% Modification to random matrices
            Q = mk_stochastic(rand(nW, nW));
            currTrace = rand()*nW; 
            diagVals = mk_stochastic(rand(nW, 1))*currTrace; 
            for i=1:nW 
                Q(i, :) = (1-diagVals(i)).*Q(i, :); 
                Q(i, i) = diagVals(i);
            end
            Q = mk_stochastic(Q);   
             
            currState = W(mc_sample(pW, Q, T(ti)));
            currScore = getScore(currState);
            se_count = se_count + 1; 
            se_score = [se_score ; currScore];
 
            se_weights = [se_weights; currState]; 
            se_trace = [se_trace; trace(Q)]; 
            se_alpha = [se_alpha; calpha]; 
            R(ai, ti) = R(ai, ti) + currScore;
        end
        R(ai, ti) = R(ai, ti)/N;
    end
    plot(alpha', R(:, ti), markers{ti}, 'LineWidth', 3, 'MarkerSize', 10);
    newLgnd{ti} = sprintf('T = %d', T(ti)); 
   keyboard; 
end
% xlabel('\alpha \Rightarrow');
% ylabel('change score s^\alpha \Rightarrow'); 
% legend(newLgnd, 'Location', 'NorthWest'); 
% saveas(h, 'changeScore.fig');
% print('-dpng', 'changeScore'); 
result.score = se_score; 
result.trace = se_trace; 
result.weights = se_weights;
result.R = R; 
result.alpha = se_alpha; 
save testQ R se_score se_trace se_count se_weights;  
end    
