function [D, A] = knockout2(G, V, damping_factor, max_depth)
%% KNOCKOUT2 does modified graph knockout based on breadth first search.
%
% Usage: D = knockout2(G, V, damping_factor, max_depth)
% 
% Expects: 
% -----------
% G - the adjacency matrix for the graph
% V - the set of vertices to be knocked out
% damping_factor - the damping going from node vs neighbours (default=1)
% max_depth - the maximum depth uptil which gene influence extends
%
% Returns:
% -----------
% D - the resulting diffusion factors. 
% 
% Uses ../lib/gaimc/bfs.m 
% 
    Dv = []; 
    addpath ../lib/gaimc;
    assert(size(G, 1) == size(G, 2)); 
    nV = size(G, 1); 
    if length(V) == 0 % nothing to be knocked out 
        D = zeros(nV, 1); 
    else
        if nargin < 4
            max_depth = 5;
        end
        if nargin < 3
            damping_factor = 0.5;
        end
        F = []; % bfs distances  
        girth = nV;  
        diameter = 0; 
        for vi=1:length(V)
            cv = V(vi); 
            [a, b, c] = bfs(G, cv);
            F = [F a];
            diameter = max(max(a), diameter);
            girth  = min(max(a), girth); 
        end
        maxF = max(F, [], 2); 
        W1 = sum((F==0), 2); 
        
        depth = min(girth, max_depth); 
        farF =  (F >= depth) + (F == -1); 
        W0 = ones(nV, 1); 
        for i=1:length(V)
            W0 = W0 .* farF(:, i); 
        end
        W0 = (sign(W0));
        I1 = V; 
        I0 = find(W0); 
        D = zeros(nV, 1); 
        max_change = true;
        G = damping_factor*G + (1-damping_factor)*eye(nV); 
        D = D + W1; 
        max_iter = 100;
        current_iter  = 1;
        %%% pre initialize the values. 
        minF = mean(F, 2); 
        minF = (1-W1).*minF;
        minF = (1-W0).*minF + 10*W0; 
        
        D = (damping_factor).^minF;
        D = (1-W1).*D + W1;
        D = (1-W0).*D;
%         while((max_change) && (current_iter < max_iter))
%             current_iter = current_iter + 1; 
%             max_change = false; 
%             prevD = D;  
%             D = (G*D)./max(sum(G, 2), 0.001);             
%             D = min( (D + W1),  1); 
%             D = D - W0.*D;
%             Dv = [Dv D];
%             if(max(abs(D - prevD) ) > 1e-5)
%                 max_change = true;
%             end
%         end
%        disp(D');
        A = struct('F', [], 'W0', [], 'Dv', []); 
        A.F = F; 
        A.Dv=  Dv; 
        A.W0 = W0; 
        
end
    