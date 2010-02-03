function [status, errcode] = cytowrite(dname, S, G, score, expr_data, expr_genes, expr_legend)
% CYTOWRITE converts the data and writes out the cytoscape compatible files. 
%
% Usage: [status, errcode] = cytowrite(fileName, W, E, A, Hnames, ExprData, ExprGenes)
% 
%
    addpath ../code;
    load ../data/orf.mat; 
 
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %%% SIF file generation
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    geneExprMap = containers.Map();
    graphFileName = sprintf('../results/%s/graph.sif', dname); 
    %%% write out the graph structure
    sid = fopen(graphFileName, 'w'); 
    E = S.E;
    num_genes = 0;
    last_index = 0; 
    i = 1; 
    while ( (i <size(E, 1)) && (S.vW(i) >= score))
        disp(sprintf(['cytowrite1(): Write Graph Structure edge: %d ' ...
                      'of %d'], i, size(E, 1))); 
        %%% if we want common names
        % gi = char(E(i, 1));
        % gj = char(E(i, 2));
        %%% if we want systematic names
        gi = char(G.genes(G.E(i, 1)));
        gj = char(G.genes(G.E(i, 2))); 
        fprintf(sid, '%s pp %s\n', gi, gj);
        last_index = last_index + 1; 
        for ll=1:2
            if (ll == 1)
                geneToTest = gi;
            else
                geneToTest = gj; 
            end
            %%% find the expression data for relevant genes
            if ~(geneExprMap.isKey(geneToTest))
                gttIndex = remapGeneKeys(expr_genes, geneToTest);
                geneExprMap(geneToTest) = expr_data(gttIndex, :); 
            end
        end
        i  = i + 1;
    end  
    last_index = i-1;
    fclose(sid); 
    %%% right out the expression levels for the relevant genes 
    [nfname, msg] = sprintf('../results/%s/expressions.noa', dname);
    [nid] = fopen(nfname, 'w');  
    fprintf(nid, 'ExpressionLevel');
    for j=1:length(expr_legend)
        fprintf(nid, '%s ', char(expr_legend(j))) ;
    end
    fprintf(nid, '\n'); 
    curr_genes = geneExprMap.keys()' ; 
    for j=1:length(curr_genes); 
        cg =  char(curr_genes(j)); 
        fprintf(nid, '%s =', cg); 
        vals = geneExprMap(cg);
        for l=1:length(vals)
            fprintf(nid, ' %g', vals(l)); 
        end
        fprintf(nid, '\n'); 
    end
    fclose(nid); 
    
    % keyboard; 
    %%% write out the edge interactions and node expression levels
    T = size(S.W, 2); 
    [efname, msg] = sprintf('../results/%s/interactions.eda', dname);
    [eid] = fopen(efname, 'w'); 
    fprintf(eid, 'InteractionStrength');
    for j=1:length(expr_legend)
        fprintf(eid, '%s ', char(expr_legend(j))) ;
    end 
    fprintf(eid, '\n'); 
     %    fprintf(cid, 'edge.color\n');
    for i=1:last_index
       disp(sprintf('cytowrite1(): Edge %d of %d', i, size(E, 1)));
  % if(S.var(i)>=score)
       %%% if we want common names
       % cx = char(E(i, 1)); 
       % cy = char(E(i, 2)); 
       %%% if we want systematic names
       cx = char(G.genes(G.E(i, 1))); 
       cy = char(G.genes(G.E(i, 2)));      
       fprintf(eid, '%s (pp) %s =', cx, cy);      
       for t=1:T
           cw = S.W(i, t); 
           fprintf(eid, ' %d', cw);
           % if(t < (T))
           %     fprintf(eid, ','); 
           % end
       end
       fprintf(eid, '\n'); 
 %  end
    end           
    fclose(eid); 
    %%% print out the color files. 
    for t=1:T    
         [cfname, msg] = sprintf('../results/%s/edgeColor/t%d.eda', dname, t); 
         [cid] = fopen(cfname, 'w'); 
         fprintf(cid, 'edge.color\n'); 
         disp(sprintf('cytowrite() writing color file for  t: %d', t));  
         for i=1:last_index
             gi = char(G.genes(G.E(i, 1)));
             gj = char(G.genes(G.E(i, 2))); 
             wij = S.W(i, t); 
             switch(wij)
               case -2
                 color = '255, 153, 153';
               case -1
                 color = '255, 0, 0'; 
               case 0
                 color = '255, 255, 204'; % pale yellow
               case 1
                 color = '51, 204, 255'; % light blue
               case 2
                 color = '51, 51, 255'; % dark blue
             end
             fprintf(cid, '%s (pp) %s = %s\n', gi, gj, color); 
            
         end
         fclose(cid); 
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% cytowrite2.m ends here