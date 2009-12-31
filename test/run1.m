S = struct('G', [], 'H', [] ,'B', [], 'E', [], 'W', [], 'var', [], 'cluster', [], 'isAnaerobic', []); 
c = 1;
for j=0:1
    for i=1:8
        [G, H, B] = exp1(i, 5, j); 
        S(c).G = G; 
        S(c).H = H; 
        S(c).B = B; 
        A = analyze1(G, H, B); 
        S(c).cluster = i; 
        S(c).isAnaerobic = j;
        S(c).E = A.E; 
        S(c).W = A.W; 
        S(c).var = A.vW;
        c = c + 1;  
    end
end