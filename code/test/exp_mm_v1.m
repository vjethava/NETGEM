function [R] = exp_mm_v1(nT, S)
%%% exp_mm.m --- 
%% 
%% Filename: exp_mm.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Thu Dec 24 17:45:07 2009 (+0530)
%% Version: 
%% Last-Updated: Mon Mar 22 13:03:40 2010 (+0100)
%%           By: Vinay Jethava
%%     Update #: 36
%% URL: http://www.github.com/vjethava
%% Keywords: 
%% Compatibility: 
%%%%%%%%%%%%%%%% Code:

%%% PreSetup 
warning off; 
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../code;

if nargin < 2
    S = [0]; 
end
%%% Initialization  
R = struct('fscore', [], 'fdiff', [], 'n', [], 'LL', []); 
N = 5; 
 G = [0 0 1 0 0;0 0 0 1 0; 0 0 0 1 0; 1 0 0 0 0;  0 1 0 0 0]; 
p = 0.2; 
% G =  erdosRenyi(N, p);
W = [0 1];
nW = length(W);  
nH = 6; % total number of classes
nHav = 2.0; % av number of classes per component
gp = 1.0;
gn = 0.20; 
% S = [0 1]; % number of genes to knock out randomly per strain 
nS = length(S); % number of iid samples

%%% Generate graph, class evolution data
%G = erdosRenyi(N, p);
%G = full(G); 

[I, J, S1]=find(G); %find(triu(G)); 
E = [I J]; 
nE = size(E, 1); 

disp(sprintf('\n******************************************************************'));
disp(sprintf('* exp_mm: synthetic data comparisons N: %d T: %d nE: %d', N, nT, nE ));
disp(sprintf('********************************************************************\n'));
 
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
disp(sprintf('\nLarge State Space HMM : Only if N < 5'));
disp(sprintf('*********************************************************')); 
   
seq = {}; 
largeW_Prior = 1/(nW^nE)*ones(nW^nE, 1); 
for i=1:nS
    seq{i} = largeX(:, i);    
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% UPDATE: Compute F-score for the real case
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% [logLik0, priorEst0, lQest0, lEest0, nIter0] = dhmm_em(seq, largeW_Prior, ...
%     largeQguess, largeE,  'adj_obs', 0, 'max_iter', 10, ...
%     'adj_prior', 0);
[logLik0, priorEst0, lQest0, lEest0, nIter0, gamma] = ...
    my_dhmm_em(seq, largeW_Prior, largeQguess, largeE,  'adj_obs', 0, ...
            'max_iter', 10, 'adj_prior', 0); 

assert(size(gamma, 1) == nW^nE); 
assert(size(gamma, 2) == nT); 
%%% computing fscore. 
[val wML0Large] = max(gamma, [], 1);
wML0 = []; 
for i=1:length(wML0Large)
    w0curr = getSmallW(wML0Large(i), W, nE);
    assert(size(w0curr, 1) == 1); 
    wML0 = [wML0 w0curr']; 
end
p0 = nnz(wML0.*We == 1)/nnz(wML0);
r0 = nnz(wML0.*We == 1)/nnz(We);
fscore0 = p0*r0/(p0+r0);
R(1).fscore = fscore0; 
%%% finished computing fscore. 
fdiff0 = norm(largeQ - lQest0, 'fro');
R(1).fdiff = fdiff0;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

R(1).n = 1;
R(1).LL = logLik0';
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Try out independent edge approach to the problem 
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
disp(sprintf('\nFactorial Assumption HMM (independent edges) '));
disp(sprintf('*********************************************************')); 
  
small_w_prior = 1/nW * ones(nW, nE); 
%%% run the edge f-b algorithm 
Qinit = Qguess;
LL1  = []; 
maxIters1 = 40; 
notStable1 = true; 
nIters1 = 0;
ll1old = 0.0; 
Fa = []; D1 = [];
while ( notStable1 && (nIters1 < maxIters1))
    notStable1 = false; 
    nIters1 = nIters1 + 1; 
    [F1, B1, Xi1, cL1, wML1] = fbEdge(X, E, DF, W', Qguess, small_w_prior);
    Qguess = 0.5 * Qinit + 0.5 * Xi1; 
    LL1 = [LL1; cL1]; 
    
    disp(sprintf('fbEdge(): iteration: %d Log Likelihood: %g', nIters1, cL1));
    for j=1:nE
        Qguess(:, :, j)  = mk_stochastic(Qguess(:,:,j));
    end
    if (abs(ll1old - cL1) > 1e-3)
        notStable1 = true;
        ll1old = cL1; 
    end
    lQ1guess = getLargeQ(Qguess); 
    fdiff1 = norm(largeQ - lQ1guess, 'fro');
    %%% UPDATE: corrected precision recall denominator (swapped)
    p1 = nnz(wML1.*We == 1)/nnz(wML1);
    r1 = nnz(wML1.*We == 1)/nnz(We);
    fscore1 = p1*r1/(p1+r1);
    D1 = [D1 fdiff1]; 
    Fa = [Fa fscore1]; 
end  
R(2).fscore = Fa;
R(2).fdiff = D1;
R(2).n = nIters1; 
R(2).LL = LL1';
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Try out the mixture model approach to the problem 
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
disp(sprintf('\nMixture model HMM'));
disp(sprintf('*********************************************************')); 
  

notStable2 = true;
LL2old = 0.0;
nIters2 = 0;
maxIters2 = 40; 
F2 = [];
D2 = []; 
L2 = [];
while(notStable2 && (nIters2 < maxIters2))
    nIters2 = nIters2 + 1;
    notStable2 = false; 
    [Qest2, Aest2, wML2, LL2] = fbEdgeMM(X, E, DF, W, Hguess, Dguess);  
    Dguess = mk_stochastic(Aedge + Aest2); 
    disp(sprintf('fbEdgeMM(): iteration: %d Log Likelihood: %g', nIters2, LL2));
    for h=1:nH
        Hguess(:, :, h) = mk_stochastic(H(:, :, h) + Qest2(:, :, h));
    end
    L2 = [L2 LL2]; 
    if(abs(LL2 - LL2old) > 1e-3)
        notStable2 = true;
        LL2old = LL2; 
    end
    Qe2 = zeros(nW, nW, nE); 
    for e=1:nE
        for h=1:nH
            Qe2(:, :, e) = Qe2(:, :, e) + Dguess(e, h) * Hguess(:, :, h); 
        end
    end
    lQ2guess  = getLargeQ(Qe2); 
    p2 = nnz(wML2.*We == 1)/nnz(We);
    r2 = nnz(wML2.*We == 1)/nnz(wML2);
    fscore2 = p2*r2/(p2+r2);
    fdiff2 = norm(largeQ - lQ2guess, 'fro');
    F2 = [F2 fscore2]; 
    D2 = [D2 fdiff2]; 
end
R(3).fdiff = D2;
R(3).fscore = F2; 
R(3).n = nIters2; 
R(3).LL = L2;
%%%%%%%%%%%%%%%% exp_mm.m ends here
