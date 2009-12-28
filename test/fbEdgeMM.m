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
% A - the mixture ratios, A(i, h) = mixture ratio for gene i 
%     and class h; \sum_{h=1}^H A(i,h) = 1 for all i 
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
assert(length(W) == size(Q, 1));
assert(size(H, 1) == size(H, 2));
if(length(D) == 0) % undamped version 
    D = zeros(size(X, 1), size(X, 3));
end
assert(size(X, 1) == size(D, 1));
assert(size(X, 3) == size(D, 2)); 
assert(size(Q, 3) == size(E, 1)); 
if nargin < 6
    Pw0 = (1/length(W))*ones(length(W), size(E, 1)); 
end
nH = size(H, 3); 
nV = size(X, 1);
nE = size(E, 1);-
nW = length(W);
nS = size(X, 3); 
T = size(X, 2); 
%% Initialize the variables
F = zeros(nW, nH, T, nE);
B = zeros(nW, nH, T, nE); 
O = zeros(nW, T, nE); 

