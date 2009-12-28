%%% exp_mm.m --- 
%% 
%% Filename: exp_mm.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Thu Dec 24 17:45:07 2009 (+0530)
%% Version: 
%% Last-Updated: Thu Dec 24 17:48:12 2009 (+0530)
%%           By: Vinay Jethava
%%     Update #: 5
%% URL: http://www.github.com/vjethava
%% Keywords: 
%% Compatibility: 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Commentary: 
%%  
%% This file is the top-level script for comparison of the mixture
%% model with unmodified HMM. 
%% 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Change log:
%% 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%% This program is free software; you can redistribute it and/or
%% modify it under the terms of the GNU General Public License as
%% published by the Free Software Foundation; either version 3, or
%% (at your option) any later version.
%% 
%% This program is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
%% General Public License for more details.
%% 
%% You should have received a copy of the GNU General Public License
%% along with this program; see the file COPYING.  If not, write to
%% the Free Software Foundation, Inc., 51 Franklin Street, Fifth
%% Floor, Boston, MA 02110-1301, USA.
%% 
%%%%%%%%%%%%%%%% Code:

%%% PreSetup 
warning off; 
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../code;

%%% Initialization 
N = 4; 
G = [ 0 1 0 0; 0 0 1 0; 0 0 0 1; 1 0 0 0]; 
p = 0.2; 
W = [0 1];
nW = length(W);  
nH = 6; % total number of classes
nHav = 2.0; % av number of classes per component
gp = 1.0;
gn = 0.10; 
nT = 60; 
S = [0 0 0 0]; % number of genes to knock out randomly per strain 
nS = length(S); % number of iid samples

%%% Generate graph, class evolution data
% G = erdosRenyi(N, p);
% G = full(G); 

[I, J, S1]=find(G); %find(triu(G)); 
E = [I J]; 
nE = size(E, 1); 
A = rand(N, nH) < (nHav)/nH; % gene-class membership matrix 
Aedge = A(I, :) + A(J, :);
% each gene must have atleast one class.
B = gp*Aedge + rand(nE, nH)*gn;
D = [];
for i=1:size(B, 1) 
   D = [D; dirichletrnd(B(i, :))];    
end
D = floor(100*D)/100;
D = mk_stochastic(D); % the mixture ratios 
H = rand(nW, nW, nH); % the class evolution characteristics
for i=1:nH 
    H(:,:,i) = mk_stochastic(H(:,:,i));
end
Q = zeros(nW, nW, nE); % edge evolution probabilities
for i=1:nE
    for j=1:nH
        Q(:,:, i) = Q(:, :, i) + D(i, j)*H(:, :, j);
    end
    Q(:, :, i) = mk_stochastic(Q(:, :, i));
end
% direct data generation for the large graph.
largeQ = getLargeQ(Q); % Q for the original HMM
largePinit = 1/(nW^nE)*ones(nW^nE, 1);
[largeW] = mc_sample(largePinit, largeQ, nT);
We = getSmallW(largeW, W, nE); % edge hmm weights
X = zeros(N, nT, nS); % the observations
DF = zeros(N, nS); % damping factors
qX = [-1 0 1]; % possible states X can take
nqX = length(qX); 
largeX = zeros(nT, nS); 
for s=1:nS
    knocked = unidrnd(N, S(s), 1);
    DF(: , s) = graph_knockout(G, knocked, 0.5, 5);
    % keyboard; 
    for t=1:nT
        disp(sprintf('generating X(%d) for strain %d', t, s)); 
        wt = We(:, t); 
        wtFull = sparse(E(:, 1), E(:, 2), wt, N, N); 
        wtStrained = getStrainWeights(wtFull, D(:, s)); 
        if(t == 1) % sample randomly to get some stable 
            xt = qX(unidrnd(nqX, N, 1));    
        end

        xt = getPottsNextState(xt, wtStrained, qX, 3);      
        X(:, t, s) = xt; 
        largeX(t, s) = getLargeW(xt, qX); 
    end
end
%%% compute the emission probabilities
largeE = zeros(nW^nE, nqX^N);
for wl=1:(nW^nE)
    ws = getSmallW(wl, W, nE);
    wg = sparse(E(:, 1), E(:, 2), ws, N, N); 
    for xl = 1:(nqX^N)    
        xs = getSmallW(xl, qX, N); 
        hs = exp(-xs*wg*xs');
        largeE(wl, xl) = hs; 
    end
end
largeE = mk_stochastic(largeE); 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Data generation complete. Here we have generated the 
%%% following: We, E, X, DF, G, Q based on strains S,
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Hguess = H; 
%%% Generate initial guesses for class evolution probabilities
for i=1:nW
    for j=1:nH
        Hguess(i, :, j) = dirichletrnd(H(i, :, j));  
    end
end
%%% Generate initial guesses for mixture proportions
Dguess = D; 
B = gp*Aedge + rand(nE, nH)*gn;
for i=1:nE
    Dguess(i, :) = dirichletrnd(B(i, :));
end
Qguess = zeros(nW, nW, nE); % edge evolution probabilities
for i=1:nE
    for j=1:nH
        Qguess(:,:, i) = Qguess(:, :, i) + Dguess(i, j)*Hguess(:, :, j);
    end
    Qguess(:, :, i) = mk_stochastic(Qguess(:, :, i));
end
largeQguess = getLargeQ(Qguess);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Try out the basic large state space HMM
% using murphy's code - note: normalise.m in KPMTools
% modified sum(A) -> sum(sum(A))
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
seq = {}; 
largeW_Prior = 1/(nW^nE)*ones(nW^nE, 1); 
for i=1:nS
    seq{i} = largeX(:, i);    
end
[logLik0, priorEst0, lQest0, lEest0, nIter0] = dhmm_em(seq, largeW_Prior, ...
    largeQguess, largeE,  'adj_obs', 0, 'max_iter', 50, 'adj_prior', 0);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Try out independent edge approach to the problem 
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
small_w_prior = 1/nW * ones(nW, nE); 
%%% run the edge f-b algorithm 
Qinit = Qguess;
LL  = []; 
for i=1:30
    [F, B, Xi, cL] = fbEdge(X, E, DF, W', Qguess, small_w_prior);
    Qguess = 0.5 * Qinit + 0.5 * Xi; 
    LL = [LL; cL]; 
    for j=1:nE
        Qguess(:, :, j)  = mk_stochastic(Qguess(:,:,j));
    end
end
disp(LL);  

%%%%%%%%%%%%%%%% exp_mm.m ends here
