function [D, Wv] = graph_knockout(G, V_removed, gamma, T)
%
% GRAPH_KNOCKOUT remove given vertices and computes node damping factors
%                for the remaining graph.The edge damping for edge (i,j)
%                is given by T_{ij} = (1-D(i))(1-D(j))
%
% Usage: [D] = graph_knockout(G, V_removed, gamma, T)
%
% Returns:
% --------
% D - set of damping factors corresponding to each vertex
%     D=1: the node has been removed
%     D=0: no removed nodes nearby 
% 
% Expects:
% --------
% G - original graph G(i,j) = 1 iff edge between nodes i and j
% V_removed - the set of knocked out vertices.

if nargin < 4
    T = 6;
end

if nargin < 3
    gamma = 0.1;
end
K = V_removed;
N = size(G, 1);
W = zeros(N, 1); 
W_next = W; 
if(length(K) == 1)
    W(K) = 1;
else
    for i = 1:length(K)
        k = K(i); 
        W(k) = 1; 
    end
end
G = gamma*G + eye(N);
Wv = [];
% while(max(abs(W_next-W)) > 1e-2)
for iter=1:T
    W_next = W; 
   %   keyboard;
   W = (G*W_next)./max(sum(G, 2), 0.0001); % modified to take into account the
                              % averaging over edges only not N
   for i = 1:length(K)
       k = K(i); 
       W(k) = 1; 
   end
   Wv = [Wv W];
end
D = W; 
end
    
