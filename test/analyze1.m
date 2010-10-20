function [A]=analyze1(G, H, B)
%% ANALYZE1 analyzes the results of exp1 (fixed mixture components) 
%
%  Usage: [A]=analyze1(G, H, B)
%
%  Expects:
% -----------
%  See exp1() for details of the input
%
    load ../data/orf.mat; 
    addpath ../code/; 
    
    A = struct('vW', [], 'W', [], 'E', []); 
%%% Measure the degree of change in W. 
    W = H.W; 
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
    % keyboard; 
    G2 = G.genes;
    A.W = G.wML(ii, :);
    disp(sprintf('\n find the common names and update')); 
    [ci, cm, cp] = remapGeneKeys(orf_systematic, G2); 
    for j=1:length(ci)
        G2(cp(j)) = orf_common(ci(j));
    end
    A.E = G2(G.E(ii, :)); 
     %    keyboard; 
end