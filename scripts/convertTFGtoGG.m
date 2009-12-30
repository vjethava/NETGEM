function [gene_graph, tf_names] = convertTFGtoGG(file, genes)
% Converts a bipartite gene-transcription factor graph 
% to a protein-protein interaction graph as follows:
%
% If (g1, TF_a) and (g2, TF_a) => (g1, g2)
%
fid = fopen(file, 'r');
tfg = textscan(fid, '%s%s');
s = length(genes); 
gene_graph = sparse(s, s); 
tf_names = [];
i = 1;
while(i < length(tfg{1}))
    j = i+1;
    current_tf = tfg{1}(i);
    disp(sprintf('node: %d tf: ' , i)); disp(current_tf);
    
    tf_names = [tf_names; current_tf];  
    while( (j < length(tfg{1}) ) && (strcmp(tfg{1}(j) , current_tf)) )
        j = j+1;
    end

    k = j-1; 
    disp(sprintf('k: %d', k)); 
    for l = i:(k-1)
        for m = (i+1):k
           
            gid1 = find(strcmp(tfg{2}(l), genes));
            gid2 = find(strcmp(tfg{2}(m), genes)); 
            if ((length(gid1) ~= 0) && (length(gid2) ~= 0)) % both are in the gene database
                gene_graph(min(gid1, gid2), max(gid1, gid2) ) = 1;
            %%% can be commented out to get lower triangular graph    
                gene_graph(max(gid1, gid2), min(gid1, gid2) ) = 1;
                found = 1; 
            else
                found = 0; 
            end
            disp(sprintf('\ti: %d k: %d l: %d m: %d found: %d', i, k, l, m, found));   
        end
    end
    i = k+1; 
end
