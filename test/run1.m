S = struct('G', [], 'H', [] ,'B', [], 'E', [], 'W', [], 'var', [], 'cluster', [], 'isAnaerobic', []); 
c = 1;
sparsityFactor = 5.0; 
load ../data/expr.mat;
for j=0:1
    for i=1:8
        [G, H, B] = exp1_pp(i, sparsityFactor, j); 
        S(c).G = G; 
        S(c).H = H; 
        S(c).B = B; 
        A = analyze1(G, H, B); 
        S(c).cluster = i; 
        S(c).isAnaerobic = j;
        S(c).E = A.E; 
        S(c).W = A.W; 
        S(c).vW = A.vW;
        c = c + 1;  
        if(length(S(i).vW) > 0)
            if j==0
                dname = sprintf('AE%d', i);          
            else 
                dname = sprintf('AN%d', i);          
            end
            mkdir(sprintf('../results/%s',dname)); 
            mkdir(sprintf('../results/%s/edgeColor', dname)); 
            mkdir(sprintf('../results/%s/images', dname));
            edata = expr_data(:, j*8 + 1:8); 
            egenes = expr_genes;
            elegend = expr_legend(j*8 + 1:8); 
            currS = S(i);
            currG = S(i).G; 
            currScore = 0.0; % look at all edges with > one change
            cytowrite(dname, currS, currG, currScore, edata, egenes, ...
                  elegend); 
        end
    end
end
save ../results/result1.mat S;

