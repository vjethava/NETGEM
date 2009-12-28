function [F, B, Xi, LL] = fbEdge(X, E, D, W, Q, Pw0)
% FBEDGE computes the Forward Backward iterates for the 
% specified edge, E=(i,j), given observed data, X, damping, D, and
% estimate for transition probability Q:W x W -> R+
%  
% Usage:  [F, B, Xi] = fbEdge(X, E, D, W, Q [, Pw0])
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
% Q - Estimate for the transition probability for the Q(i, j, e) =
%      P(w_e(t+1) = W(j) | w_e(t) = W(i)).  
% Pw0 - Probability for weights at time zero. Pw0(i, e) = P(w_e(0) = w_i)
%    
% Returns: 
% --------
% F - forward iterates F(i, t, e) - P(W_e(t) = i, X^{1:t} | Q)
% B - backward iterates B(i, t, e) - P(X^{t+1:T} | W_e(t) = i, Q)
% Xi - Aggregate Xi(i, j, e) - \sum_t P(W_e(t) = w_i, W_e(t+1) = w_j | X, Q) 
% LL - Log likelihood for the current estimate
% 
% Note: Use fbEdgeSet for fast computation over multiple edges
%
    
%%% fb_edge.m --- 
% 
% Filename: fb_edge.m
% Description: 
% Author: Vinay Jethava
% Maintainer: 
% Created: Fri Dec 11 23:14:40 2009 (+0100)
% Version: 
% Last-Updated: Sat Dec 12 09:46:32 2009 (+0100)
%           By: Vinay Jethava
%     Update #: 32
% URL: 
% Keywords: 
% Compatibility: 
% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 
%%% Commentary: 
% 
% 
% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 
%%% Change log:
% 
% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 
% This program is free software; you can redistribute it and/or
% modify it under the terms of the GNU General Public License as
% published by the Free Software Foundation; either version 3, or
% (at your option) any later version.
% 
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% General Public License for more details.
% 
% You should have received a copy of the GNU General Public License
% along with this program; see the file COPYING.  If not, write to
% the Free Software Foundation, Inc., 51 Franklin Street, Fifth
% Floor, Boston, MA 02110-1301, USA.
% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 
%%% Code:

%% Initial Checks
assert(size(E, 2) == 2); 
assert(length(W) == size(Q, 1));
assert(size(Q, 1) == size(Q, 2));
if(size(W, 1) == 1)
    W = W';
end

if(length(D) == 0) % undamped version 
    D = zeros(size(X, 1), size(X, 3));
end
assert(size(X, 1) == size(D, 1));
assert(size(X, 3) == size(D, 2)); 
assert(size(Q, 3) == size(E, 1)); 
if nargin < 6
    Pw0 = (1/length(W))*ones(length(W), size(E, 1)); 
end
nV = size(X, 1); 
nE = size(E, 1);
nW = length(W);
nS = size(X, 3); 
T = size(X, 2); 
%% memory allocation
Gamma = zeros(nE, nS); % the edge damping
O = zeros(nW, T, nE); % emission probability O(i, t, e)=P(x^{1:S}_e(t) | w_e(t) = w_i) 
F = zeros(nW, T, nE); 
B = zeros(nW, T, nE); 
Xi = zeros(nW, nW, nE);
%% pre-compute
I = E(:, 1);
J = E(:, 2);
Gamma = ((1 - D(I, :)).*(1 - D(J, :)));
for t=1:T
    Xmult = [];
    for s=1:nS
        Xmult = [Xmult  X(I, t, s).*X(J, t, s).*Gamma(:, s)];
    end     
    Xmult = sum(Xmult, 2); 
    %%% Code modification
    Wmult = exp(-W*Xmult');
    %%% 
    %%%
    Wmult = Wmult./(ones(nW, 1)*sum(Wmult, 1));
    O(:, t, :) = reshape(Wmult, [nW 1 nE]); 
end
%% initialization
F(:, 1, :) = Pw0;
B(:, T, :) = ones(nW, nE);  
%% update 
for t=2:T % forward iterate 
    for e=1:nE  
        F(:, t, e) = O(:, t, e).*(F(:, t-1, e)'*Q(:, :,  e))'; 
        F(:, t, e) =  F(:, t, e)./sum( F(:, t, e) );
    end 
end  
for t=(T-1):-1:1 % backward iterate 
    for e=1:nE 
        B(:, t, e) = Q(:, :, e)*(B(:, t+1, e).*O(:, t+1, e));
        B(:, t, e) = B(:, t, e)./sum(B(:, t, e));
    end
end

for e=1:nE
    for l=1:nW
        for m=1:nW
            for t=1:(T-1)
                R = ( F(l, t, e) * Q(l, m) * O(m, t+1, e) * B(m, t+1, e) );
                Xi(l, m, e) = Xi(l, m, e) + R;
            end
        end
    end
    %%% do not normalize here - normalized outside. 
    Xi(:, :, e) = normalise(Xi(:, :, e)); 
end
%% log likelihood computation
LL = sum(sum(sum(Xi.*log(Q))));
