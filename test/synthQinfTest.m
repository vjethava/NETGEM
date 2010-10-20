function [result] = synthQinfTest(N, nH, nT, p, W)
%% [result] = synthQinfTest(N, nH, nT)
%
% SYNTHQINFTEST function to test the inference results based on
% the different trace of the Markov transition probability matrix 
% 
% Expects:
% ------------
% N:    number of edges  
% nH:   number of functional categories
% nT:   number of available gene expression observation points
%
warning off; 
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../code;

result = struct('trH', [], 'sH', [], 'trE', [] ,'sE', [] , ...
        'sInfH', [], 'sInfE', [], 'trInfH', [], 'trInE', []);

%% Parameters
if nargin < 5
    W = [-2 -1 0 1 2];   % weight states
end

if nargin < 4
    p = 0.01; 
end
if nargin < 3
    nT = 8;    % simulation period
end
if nargin < 2
    nH = 20;    % total number of functional classes
end
if nargin < 1 
    N = 1000;
end

nHav = 3;   % avg number of functional classes for a gene
S = [0];    % number of knocked out genes in strain

disp('Allocating G');
nS = length(S);  
nW = length(W);

G = erdosRenyi(N, p); 
G = triu(G); 
[I, J, S1]=find(G); %find(triu(G)); 
E = [I J]; 
nE = size(E, 1); 
fprintf(1, 'Allocating A nE = %d\n', nE); 
A = rand(N, nH) < (nHav)/nH; % gene-class membership matrix 
for i=1:N
    if(nnz(A(i, :)) == 0) % at least one class
        j = randint(1, 1, [1 nH]); 
        A(i, j) = 1; 
    end
end
D = A(I, :) + A(J, :);
for i=1:nE
    D(i, :) = dirichletrnd(D(i, :));  
end
D = mk_stochastic(D); % the mixture ratios  
clear A;
disp('Allocating H'); 
H = zeros(nW, nW, nH); 
trH = zeros(nH, 1); 
result.trH = trH; 
for i=1:nH 
    fprintf(2, 'initH %d of %d\n', i, nH); 
    H(:,:,i) = getQwithTracePct(min(90, max(5, rand()*100.0)), nW);
    trH(i) = trace(H(:, : , i)); 
end
result.trH = trH; 
disp('Starting to sample'); 
pw0 = (1.0/nW)*ones(nW, 1);
We = zeros(nE, nT); 
trE = zeros(nE, 1);
for e=1:nE
    fprintf(1, 'edge %d of %d\n', e, nE);
    cQ = zeros(nW, nW);
    for j=1:nH
        cQ = cQ + D(e, j)*H(:, :, j);
    end
    trE(e) = trace(cQ);
    currW= mc_sample(pw0, cQ, nT);
    We(e, :) = W(currW);  
end
result.trE = trE; 

%% Perfect inference of W gives
disp('Computing scores'); 
sE0 = zeros(nE, 1); 
sH0 = zeros(nH, 1);
nH0 = sum(D, 1)';
for n=1:nE
    sE0(n) = getScore(We(n, :)); 
end
sH0 = (D' * sE0) ./ nH0 ;
result.sE = sE0;
result.sH = sH0; 
save result result;
%% Data generation 
X = zeros(N, nT, nS); 
DF = zeros(N, nS);
qX = [-1 0 1];
nqX = length(qX);
for s=1:nS
    knocked = unidrnd(N, S(s), 1);
    DF(: , s) = graph_knockout(G, knocked, 0.5, 5);
    % keyboard; 
    for t=1:nT
        disp(sprintf('generating X(%d) for strain %d', t, s)); 
        wt = We(:, t); 
        wtFull = sparse(E(:, 1), E(:, 2), wt, N, N); 
        wtStrained = getStrainWeights(wtFull, DF(:, s)); 
        if(t == 1) % sample randomly to get some stable 
            xt = qX(unidrnd(nqX, N, 1));    
        end
        xt = getPottsNextState(xt, wtStrained, qX, 20);      
        X(:, t, s) = xt; 
    end
end

%% Introducing Noise (Initial guess)
Hguess = H; 
%%% Generate initial guesses for class evolution probabilities
for i=1:nW
    for j=1:nH
        Hguess(i, :, j) = dirichletrnd(H(i, :, j));  
    end
end
Hmy0 = Hguess;
%%% Generate initial guesses for mixture proportions
% Dguess = D; 
% for i=1:nE
%     Dguess(i, :) = dirichletrnd((D(i, :) > 0));
% end
Dguess = D; 
Qguess = zeros(nW, nW, nE); % edge evolution probabilities
for i=1:nE
    for j=1:nH
        Qguess(:,:, i) = Qguess(:, :, i) + Dguess(i, j)*Hguess(:, :, j);
    end
    Qguess(:, :, i) = mk_stochastic(Qguess(:, :, i));
end

%% Inference
notStable2 = true;
LL2old = 0.0;
nIters2 = 0;
maxIters2 = 15; 
F2 = [];
D2 = []; 
L2 = [];
HtrueVsEstFrob = zeros(nH , maxIters2 + 1) ; 
while(notStable2 && (nIters2 < maxIters2))
    nIters2 = nIters2 + 1;
    notStable2 = false; 
    for h=1:nH
        HtrueVsEstFrob(nIters2, h) = norm(H(:, :, h) - Hguess(:, :, h),'fro');
    end
    [Qest2, Aest2, wML2, LL2] = fbEdgeMM(X, E, DF, W, Hguess, Dguess);  
    % Dguess = mk_stochastic(Aedge + Aest2); 
    disp(sprintf('fbEdgeMM(): iteration: %d Log Likelihood: %g', nIters2, LL2));
    for h=1:nH
        Hguess(:, :, h) = mk_stochastic(Hmy0(:, :, h) + Qest2(:, :, h));
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
    
    p2 = nnz(wML2.*We == 1)/nnz(We);
    r2 = nnz(wML2.*We == 1)/nnz(wML2);
    fscore2 = p2*r2/(p2+r2);
    F2 = [F2 fscore2]; 

end
trInfH = zeros(nH, 1); 
trInfE = zeros(nE, 1); 
sInfH = zeros(nH, 1); 
sInfE = zeros(nE, 1); 
for i=1:nE
    sInfE(i) = getScore(wML2(i, :)); 
    trInfE(i) = trace(Qe2(:,:, i));
end
for i=1:nH
    trInfH(i) = trace(Qest2(:, :, i)); 
end
sInfH = (Dguess' * sInfE) ./ (sum(Dguess, 1)'); 

result.trInfH = trInfH; 
result.trInfE = trInfE; 
result.sInfH = sInfH;
result.sInfE = sInfE; 
result.hFrob = HtrueVsEstFrob;
end

% 
% function [score] = getScore(W)
% T = length(W);
% score = 0.0;
% for t=2:T
%     change = W(t)- W(t-1); 
%     score = score +change*change; 
% end
% score = score*1.0/T; 
% end
