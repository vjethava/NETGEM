function [A]=analyze1(G, H, B)
%% ANALYZE1 analyzes the results of exp1 (fixed mixture components) 
%
%  Usage: [A]=analyze1(G, H, B)
%
%  Expects:
% -----------
%  See exp1() for details of the input
%
    A = struct('varW', [], 'W', [], 'E', []); 
%%% Measure the degree of change in W. 
    W = [-1 0 1]; 
    nW = length(W); 
    nE = size(G.wML, 1);
    nT = size(G.wML, 2);
    changeInW = zeros(nE, nT-1); 
    for t=1:(nT-1)
        changeInW(:, t) = G.wML(:, t+1) - G.wML(:, t); 
    end
    varChangeInW = var(changeInW, 1, 2);  
   
    [vi,ii] = sort(varChangeInW, 'descend'); 
    A.vW = vi; 
    A.E = G.genes(G.E(ii, :));
    A.W = G.wML(ii, :); 
%    keyboard; 
end