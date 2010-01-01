function []=fbEdgeMM(X, E, D, W, H, A, Pw0)
% FBEDGEMM computes the Forward Backward iterates for the 
% specified edge, E=(i,j), given observed data, X, damping, D, and
% estimate for transition probability Q:W x W -> R+
%  
% Usage:  [F, B, Xi] = fbEdge(X,E, D, W, Q [, Pw0])f
%
% Expects:
% --------
% X - data matrix such that X(i, t, s) = gene expression level for
%      gene 'i' at time 't' for strain 's'
% E - list of edges to compute the forward backward iterates
%     for. E(e, :) = [l m] ( edge between genes l and m)
% D - the node damping for strains D(i, s) - damping for gene 'i'
%      under strain 's'
% W - the weight states W(i) = 'i'-th weight state 
% H - Estimate for the transition probability of the n^th class 
%     i.e. Q(i, j, h) = P(w_e(t+1) = W(j) | w_e(t) = W(i), Q_h).  
% A - the mixture ratios, A(e, h) = mixture ratio for edge e 
%     and class h; \sum_{h=1}^H A(e,h) = 1 for all edges 'e' 
%
% Pw0 - Probability for weights at time zero. Pw0(i, e) = P(w_e(0) = w_i)
%    
%
% Returns: 
% --------
% F - forward iterates F(i, t, e) - P(W_e(t) = i, X^{1:t} | Q)
% B - backward iterates B(i, t, e) - P(X^{t+1:T} | W_e(t) = i, Q)
% Xi - Aggregate Xi(i, j, e) - \sum_t P(W_e(t) = w_i, W_e(t+1) = w_j | X, Q) 
% 

%% Initial Checks
assert(size(E, 2) == 2); 
assert(length(W) == size(H, 1));
assert(size(H, 1) == size(H, 2));
if(length(D) == 0) % undamped version 
    D = zeros(size(X, 1), size(X, 3));
end
if(size(W, 1) == 1)
    W = W';
end
assert(size(X, 1) == size(D, 1));
assert(size(X, 3) == size(D, 2)); 
assert(size(H, 3) == size(A, 2)); 
if nargin < 7
    Pw0 = (1/length(W))*ones(length(W), size(E, 1)); 
end
nH = size(H, 3); 
nV = size(X, 1);
nE = size(E, 1);
nW = length(W);
nS = size(X, 3); 
T = size(X, 2); 
%% Initialize the variables
% F = zeros(nW, nH, T, nE);
% B = zeros(nW, nH, T, nE); 
% O = zeros(nW, T, nE); 
for e=1:nE; 
    ci = E(e, 1); 
    cj = E(e, 2); 
    xe = X([ci cj], :, :);
    de = D([ci cj], :);
    p0e = Pw0(:, e); 
    ae = A(e, :); 
    
    [fe, be, xi_e, we, lle] = fbSingleEdgeMM(xe, de, W, H, ae, p0e);  
end

function [f, b, xi, w_ml, ll]=fbSingleEdgeMM(Xe, De, W, H, A, Pw0e)
nW = length(W); 
T = size(Xe, 2); 
nH = length(H); 
nS = size(Xe, 3); 
f = zeros(nW, nH, T); 
b = zeros(nW, nH, T); 
f(:, :, 1) = Pw0e * ones(1, nH); 
b(:, :, T) = ones(nW, nH); 
obs = zeros(nW, T); % the p(w^t | x^t) matrix 
% observation matrix 
for t=1:T
    % xsum = 0.0; 
    % for s=1:nS
    %     xvertex = Xe(:, t, s).*(1-De(:, s));
    %     xsum = xsum + xvertex(1)*xvertex(2); 
    % end
    xv2 = reshape(Xe(:, t, :), nW, nS).*(1-De); 
    xsum2 = sum(xv2(1, :).*xv2(2, :)); 
    %    assert(xsum2 == xsum); 
    wsum = exp(-W*xsum2); 
    obs(:, t) = normalize(wsum);
    %    obs2 = [obs2; normalize(exp(-W*xsum2))]; 
end
keyboard;
% forward beliefs: f = zeros(nW, nH, T);  
for t=2:T
    for m=1:nW
        for h=1:nH
            ct = obs(m, t)*A(h);
            for l=1:nW % the next state  
                fterm = reshape(f(l, : ,t-1), nH, 1); 
                qterm = reshape(H(m, l, :), 1, nH); 
                f(m , h, t) =  f(m, h, t) +  fterm*qterm; 
            end
            f(m, h, t) = ct*f(m, h, t); 
        end
    end
end
% bakcward beliefs 
for t=(T-1):-1:1
    for m=1:nW
        for h=1:nH
            for l=1:nW  
                ct = obs(l, t+1);
                be = reshape(b( l, :, t+1), nH, 1);
                b(m, h, t) = b(m, h, t) + ct*(be*A')*H(m, l, h);
            end
        end
    end
end


keyboard; 

