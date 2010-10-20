%%% [result] = getQeQh(N, nH, nT, p, W, tLow , tHigh) 
%% 
%% Filename: getQeQh.m
%% Description: 
%% Author: Vinay Jethava
%% Created: Sat Sep  4 13:17:07 2010 (+0530)
%% Last-Updated: Sat Sep  4 13:28:09 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 3
%% URL: 
%% Keywords: 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
function [result] = getQeQh(N, nH, nT, p, W, tLow , tHigh)
    setpaths(); 
result = struct('trH', [], 'sH', [], 'trE', [] ,'sE', [] , ...
                'sInfH', [], 'sInfE', [], 'trInfH', [], 'trInE', []);

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
    hypoClass = floor(2.0 * rand());
    if (hypoClass == 0)
        currTracePct = rand() * tLow;
    else
        currTracePct = tHigh + rand() * (100 - tHigh);
    end
    H(:,:,i) = getQwithTracePct(currTracePct, nW);
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
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getQeQh.m ends here
