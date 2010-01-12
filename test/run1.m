%%
% This file runs the experiment corresponding to genetic dataset 1
%
% Vinay Jethava, 2009 
% vjethava@gmail.com
%

S = struct('G', [], 'H', [] ,'B', [], 'E', [], 'W', [], 'var', [], 'cluster', [], 'isAnaerobic', []); 
c = 1;
sparsityFactor = 5.0; 
load ../data/expr.mat;
for j=0:1
    for i=1:8
        [G, H, B] = exp1(i, sparsityFactor, j); 
        S(c).G = G; 
        S(c).H = sortH(H); 
        S(c).B = B; 
        A = analyze1(G, H, B); 
        S(c).cluster = i; 
        S(c).isAnaerobic = j;
        S(c).E = A.E; 
        S(c).W = A.W; 
        S(c).vW = A.vW;
        c = c + 1;  
        save ../results/result1.mat S;
        if(length(S(i).vW) > 0)
            if j==0
                dname = sprintf('AE%d', i);          
            else 
                dname = sprintf('AN%d', i);          
            end
            mkdir(sprintf('../results/%s',dname)); 
            mkdir(sprintf('../results/%s/edgeColor', dname)); 
            mkdir(sprintf('../results/%s/images', dname));
            egenes = expr_genes;
            if j==0
                edata = expr_data(:, 1:8); 
                elegend = expr_legend(1:8);
            else
                edata = expr_data(:, 9:16); 
                elegend = expr_legend(9:16);
            end
            currS = S(j*8 + i);
            currG = S(j*8 + i).G; 
            currScore = 0.0; % look at all edges with > one change
            cytowrite(dname, currS, currG, currScore, edata, egenes, ...
                  elegend); 
            %%            keyboard; 
        end
    end
end


